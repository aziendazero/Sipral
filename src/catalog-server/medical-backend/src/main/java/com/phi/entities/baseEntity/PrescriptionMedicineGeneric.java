package com.phi.entities.baseEntity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Contexts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phi.entities.act.Dosage;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.dataTypes.PQ;

/**
 * Generic many to many table between PrescriptionGeneric and Medicine, 
 * sub classed by: {@link PrescriptionMedicine} and {@link PrescriptionDischargeMedicine}
 * 
 * contains: 
 *  - List<Dosage>: serialized to dosage column
 *  - templateDosageKey: column to join with TemplateDosage table
 * 
 * @author alex.zupan
 */

@MappedSuperclass
public abstract class PrescriptionMedicineGeneric extends BaseEntity {
	
	private static final long serialVersionUID = -2380968266588464235L;
	
	protected static final Logger log = Logger.getLogger(PrescriptionMedicineGeneric.class);

	
	/**
	*  Prescription
	*/
	protected PrescriptionGeneric prescription;

	@Transient //To be implemented in subclasses
	public abstract PrescriptionGeneric getPrescription();
	
	public void setPrescription(PrescriptionGeneric prescription){
		this.prescription = prescription;
	}

	
	/**
	*  Medicine
	*/
	protected Medicine medicine;

	@Transient //To be implemented in subclasses
	public abstract Medicine getMedicine();
	
	public void setMedicine(Medicine medicine){
		this.medicine = medicine;
	}

	
	/**
	 * Dosages: list of dosages, mapped as two columns: 
	 * dosageSerialized serialized representation of the list of dosages
	 * templateDosageKey key used to join with template dosage
	 */

	/**
	 * Serialize dosage and templateDosageKey
	 */
	@PrePersist
	public void onPrePersist() {

			DateFormat df = new SimpleDateFormat("HH:mm");

			dosageSerialized = "";
			templateDosageKey = "";
			
			if (getPrescription() != null && getPrescription().getTherapy() != null) {//FIXME: profile prescription doesn't have therapy > search sdl through profile -> section -> tab 
				templateDosageKey += getPrescription().getTherapy().getPatientEncounter().getAssignedSDL().getInternalId();
				//templateDosageKey += ((PatientEncounter)Contexts.getConversationContext().get("PatientEncounter")).getAssignedSDL().getInternalId();
			} else if (getPrescription() != null && getPrescription().getTherapy() == null){
				templateDosageKey += ((Prescription)getPrescription()).getProfile().getSection().getTab().getServiceDeliveryLocation().getInternalId();
			}
			
			templateDosageKey +=";T=";
			
			String dayTimeStr = null;
			
			if (dosage!=null && !dosage.isEmpty()) {
				for (Dosage d : dosage) {
					if (d.getDayInterval() == null &&  (d.getQuantity() == null || (d.getQuantity().getValue() == null && d.getQuantity().getUnit() == null))){
						continue;
					}
					if (d.getDaytime() == null && d.getDayInterval() == null) { //asNeeded prescription
						dosageSerialized += fmt(d.getQuantity().getValue()) + "[" + d.getQuantity().getUnit() + "]|";
						templateDosageKey = "|";
					} else {
						dayTimeStr = df.format(d.getDaytime());
						dosageSerialized += d.getDayInterval() + "@" + dayTimeStr;
						if (d.getQuantity() != null && d.getQuantity().getValue() != null) {
							dosageSerialized += "=" + fmt(d.getQuantity().getValue()) + "[" + d.getQuantity().getUnit() + "]|";
						} else {
							dosageSerialized += "|";
						}
						templateDosageKey += d.getDayInterval() + "@" + dayTimeStr + "|";
					}
				}
				if (!dosageSerialized.isEmpty()) {
					dosageSerialized = dosageSerialized.substring(0, dosageSerialized.length() - 1);
					templateDosageKey = templateDosageKey.substring(0, templateDosageKey.length() - 1);
				} else {
					dosageSerialized = null;
					templateDosageKey = null;
				}
			}
		}
	
	protected String fmt(double d)
	{
	    if(d == (int) d)
	        return String.format("%d",(int)d);
	    else
	        return String.format("%s",d);
	}
	
		
	/**
	 * Deserialize dosage
	 */
	@PostLoad
	public void onPostLoad() {
		String dosageRegEx = "(?:(\\d+)@([^=]+))?(?:=)?(?:(\\d+(\\.\\d+)?)\\[([^\\]]+)\\])?";
		Pattern dosagePattern = Pattern.compile(dosageRegEx, Pattern.CASE_INSENSITIVE);
		
		DateFormat df = new SimpleDateFormat("HH:mm");
		
		dosage = new ArrayList<Dosage>();
		
		if (dosageSerialized != null) {
			String[] dosages = dosageSerialized.split("\\|");
			
			Matcher matcher = null;
			Dosage dos = null;
	
			for (String d : dosages) {
				if (!d.equals("")) {
				matcher = dosagePattern.matcher(d);
					try {
						if (matcher.matches()) {
							
							dos = new Dosage();
							
							if (matcher.group(1)!=null) {
								dos.setDayInterval(Integer.parseInt(matcher.group(1)));
							}
							if (matcher.group(2)!=null) {
								dos.setDaytime(df.parse(matcher.group(2)));
							}
							
							if (matcher.group(3) != null && matcher.group(5) != null) {
								PQ quantity = new PQ();
								quantity.setValue(Double.parseDouble(matcher.group(3)));
								quantity.setUnit(matcher.group(5));
								dos.setQuantity(quantity);
							}
		
							dosage.add(dos);
							
							//Add fake id to distinguish instances
							dos.setInternalId(dosage.indexOf(dos)); 
						} else
							throw new IllegalArgumentException("Unable to deserialize dosage: " + d);
					} catch (Exception e) {
						log.error("Error deserializing dosage", e);
					}
				}
			}
		}

	}
	
	private List<Dosage> dosage;
	
	@Transient
	public List<Dosage> getDosage() {
	    return dosage;
	}

	public void setDosage(List<Dosage> param) {
	    this.dosage = param;
	}
	
	private String dosageSerialized;

	@JsonIgnore
	@Column(name="dosage", length=4000)
	public String getDosageSerialized() {
		return dosageSerialized;
	}
	public void setDosageSerialized(String dosageSerialized) {
		this.dosageSerialized = dosageSerialized;
	}
	
	private String templateDosageKey;

	@JsonIgnore
	@Column(name="template_dosage_key")
	public String getTemplateDosageKey() {
		return templateDosageKey;
	}
	public void setTemplateDosageKey(String templateDosageKey) {
		this.templateDosageKey = templateDosageKey;
	}

}