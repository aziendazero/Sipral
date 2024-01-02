package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.json.CustomJsonEntity;
import com.phi.json.PrescriptionMedicineGenericDeserializer;

/**
 * Many to many table between PrescriptionDischarge and Medicine
 * @author alex.zupan
 */

@Entity
@Table(name = "prscrpt_dsch_medicine")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
@CustomJsonEntity(deserializer = PrescriptionMedicineGenericDeserializer.class)
public class PrescriptionDischargeMedicine extends PrescriptionMedicineGeneric {

	private static final long serialVersionUID = 693885816L;
	
	protected static final Logger log = Logger.getLogger(PrescriptionDischargeMedicine.class);

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PresDischargeMed_sequence")
	@SequenceGenerator(name = "PresDischargeMed_sequence", sequenceName = "PresDischargeMed_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	@JsonBackReference(value="prescriptionDischarge_prescriptionMedicineDischarge")
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=PrescriptionDischarge.class)
	@JoinColumn(name="prescription_id")
	@ForeignKey(name="FK_PresDisch_Medicine")
	@Index(name="IX_PresDisch_Medicine")
	@JsonDeserialize(as=PrescriptionDischarge.class)
	public PrescriptionGeneric getPrescription(){
		return prescription;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="medicine_id")
	@ForeignKey(name="FK_Medicine_PresDisch")
	@Index(name="IX_Medicine_PresDisch")
	public Medicine getMedicine(){
		return medicine;
	}

	
	private CodeValuePhi aifaNote;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="aifaNote")
	@ForeignKey(name="FK_PresDischargeMed_aifaNote")
	@Index(name="IX_PresDischargeMed_aifaNote")
	public CodeValuePhi getAifaNote(){
		return aifaNote;
	}

	public void setAifaNote(CodeValuePhi aifaNote){
		this.aifaNote = aifaNote;
	}
	
	
	private boolean showMedicineName = false;

	@Column(name="show_medicine_name")
	public boolean getShowMedicineName(){
		return showMedicineName;
	}

	public void setShowMedicineName(boolean showMedicineName){
		this.showMedicineName = showMedicineName;
	}
	
	
	private boolean chronicPatient = false;

	@Column(name="chronic_patient")
	public boolean getChronicPatient(){
		return chronicPatient;
	}

	public void setChronicPatient(boolean chronicPatient){
		this.chronicPatient = chronicPatient;
	}
	
	
	private boolean irreplaceable = false;

	@Column(name="irreplaceable")
	public boolean getIrreplaceable(){
		return irreplaceable;
	}

	public void setIrreplaceable(boolean irreplaceable){
		this.irreplaceable = irreplaceable;
	}

	
	private boolean therapeuticPlan = false;

	@Column(name="therapeutic_plan")
	public boolean getTherapeuticPlan(){
		return therapeuticPlan;
	}

	public void setTherapeuticPlan(boolean therapeuticPlan){
		this.therapeuticPlan = therapeuticPlan;
	}

}