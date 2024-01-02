package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.phi.entities.FavoriteProfile;
import com.phi.entities.act.SubstanceAdministration;
import com.phi.entities.act.Therapy;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.PQ;
import com.phi.json.JsonProxyGenerator;

@Entity
@Table(name = "prescription")
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=Prescription.class)
public class Prescription extends PrescriptionGeneric {

	private static final long serialVersionUID = -4413522559681531336L;

	/**
	 *  javadoc for period
	 */
	private IVL<Date> period;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="period_time_low")),
		@AttributeOverride(name="high", column=@Column(name="period_time_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="period_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="period_highClosed"))
	})
	public IVL<Date> getPeriod() {
		return period;
	}

	public void setPeriod(IVL<Date> period) {
		this.period = period;
	}
	
	/**
	 *  javadoc for validityPeriod
	 */
	private IVL<Date> validityPeriod;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="validity_period_time_low")),
		@AttributeOverride(name="high", column=@Column(name="validity_period_time_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="vperiod_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="validity_period_highClosed"))
	})
	public IVL<Date> getValidityPeriod() {
		return validityPeriod;
	}

	public void setValidityPeriod(IVL<Date> validityPeriod) {
		this.validityPeriod = validityPeriod;
	}

	/**
	 *  javadoc for substanceAdministration
	 */
	private List<SubstanceAdministration> substanceAdministration;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE, mappedBy = "prescription")	
	public List<SubstanceAdministration> getSubstanceAdministration() {
	    return substanceAdministration;
	}

	public void setSubstanceAdministration(List<SubstanceAdministration> param) {
	    this.substanceAdministration = param;
	}
	
	public void addSubstanceAdministration(SubstanceAdministration substanceAdministration) {
		if (this.substanceAdministration == null) {
			this.substanceAdministration = new ArrayList<SubstanceAdministration>();
		}
		// add the association
		if(!this.substanceAdministration.contains(substanceAdministration)) {
			this.substanceAdministration.add(substanceAdministration);
			// make the inverse link
			substanceAdministration.setPrescription(this);
		}
	}

	public void removeSubstanceAdministration(SubstanceAdministration substanceAdministration) {
		if (this.substanceAdministration == null) {
			this.substanceAdministration = new ArrayList<SubstanceAdministration>();
			return;
		}
		//remove the association
		if(this.substanceAdministration.contains(substanceAdministration)){
			this.substanceAdministration.remove(substanceAdministration);
			//remove the inverse link
			substanceAdministration.setPrescription(null);
		}
	}
	
	private FavoriteProfile profile;
	
	//@JsonBackReference(value="profile_prescription")
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="favoriteProfile_id")
	@Audited(targetAuditMode=RelationTargetAuditMode.NOT_AUDITED)
	@ForeignKey(name="FK_FavProf_Pres")
	@Index(name="IX_Pres_FavProf")
	public FavoriteProfile getProfile() 
	{
		return profile;
	}

	public void setProfile(FavoriteProfile profile) 
	{
			this.profile = profile;
	}
	

	//Override of PrescriptionGeneric:
	
	/**
	 *  javadoc for therapy
	 */
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.PERSIST)
	@JoinColumn(name="therapy")
	@ForeignKey(name="FK_Prescription_therapy")
	@Index(name="IX_Prescription_therapy")
	public Therapy getTherapy() {
		return therapy;
	}

	/**
	*  javadoc for prescriptionMedicine
	*/
	@JsonManagedReference(value="prescription_prescriptionMedicine")
	@JsonDeserialize(contentAs=PrescriptionMedicine.class)
	@OneToMany(fetch=FetchType.EAGER, mappedBy="prescription", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, targetEntity=PrescriptionMedicine.class)
	@Fetch(FetchMode.SELECT)
	public List<PrescriptionMedicineGeneric> getPrescriptionMedicine(){
		return prescriptionMedicine;
	}

//	public void setPrescriptionMedicine(List<PrescriptionMedicineGeneric> list){
//		prescriptionMedicine = list;
//	}
//
	public void addPrescriptionMedicine(PrescriptionMedicine prescriptionMedicine) {
		if (this.prescriptionMedicine == null) {
			this.prescriptionMedicine = new ArrayList<PrescriptionMedicineGeneric>();
		}
		// add the association
		if(!this.prescriptionMedicine.contains(prescriptionMedicine)) {
			this.prescriptionMedicine.add(prescriptionMedicine);
			// make the inverse link
			prescriptionMedicine.setPrescription(this);
		}
	}

	public void removePrescriptionMedicine(PrescriptionMedicine prescriptionMedicine) {
		if (this.prescriptionMedicine == null) {
			this.prescriptionMedicine = new ArrayList<PrescriptionMedicineGeneric>();
			return;
		}
		//add the association
		if(this.prescriptionMedicine.contains(prescriptionMedicine)){
			this.prescriptionMedicine.remove(prescriptionMedicine);
			//make the inverse link
			prescriptionMedicine.setPrescription(null);
		}

	}

	/**
	 *  javadoc for rootPrescription
	 */
//	private Prescription rootPrescription;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=Prescription.class)
	@JoinColumn(name="root_prescription_id")
	@ForeignKey(name="FK_Prscrpt_rtPrscrptn")
	@Index(name="IX_Prscrpt_rtPrscrptn")
	@JsonDeserialize(as=Prescription.class)
	public PrescriptionGeneric getRootPrescription(){
		return rootPrescription;
	}

//	public void setRootPrescription(Prescription rootPrescription){
//		this.rootPrescription = rootPrescription;
//	}
	
	
	private PQ systolicPressure;
	private PQ diastolicPressure;
	private PQ temperature;
	private PQ glycemia;
	private PQ diuresis;
	private PQ pain;
	private PQ heartRate;
	private PQ spo2;
	
	private CodeValue systolicPressureThreshold;
	private CodeValue diastolicPressureThreshold;
	private CodeValue temperatureThreshold;
	private CodeValue glycemiaThreshold;
	private CodeValue diuresisThreshold;
	private CodeValue painThreshold;
	private CodeValue heartRateThreshold;
	private CodeValue spo2Threshold;
	
	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="systolic_pressure_value")),
	       @AttributeOverride(name="unit", column=@Column(name="systolic_pressure_unit"))
	})
	public PQ getSystolicPressure() {
		return systolicPressure;
	}

	public void setSystolicPressure(PQ systolicPressure) {
		this.systolicPressure = systolicPressure;
	}

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="diastolic_pressure_value")),
	       @AttributeOverride(name="unit", column=@Column(name="diastolic_pressure_unit"))
	})
	public PQ getDiastolicPressure() {
		return diastolicPressure;
	}

	public void setDiastolicPressure(PQ diastolicPressure) {
		this.diastolicPressure = diastolicPressure;
	}

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="temperature_value")),
	       @AttributeOverride(name="unit", column=@Column(name="temperature_unit"))
	})
	public PQ getTemperature() {
		return temperature;
	}

	public void setTemperature(PQ temperature) {
		this.temperature = temperature;
	}

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="glycemia_value")),
	       @AttributeOverride(name="unit", column=@Column(name="glycemia_unit"))
	})
	public PQ getGlycemia() {
		return glycemia;
	}

	public void setGlycemia(PQ glycemia) {
		this.glycemia = glycemia;
	}

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="diuresis_value")),
	       @AttributeOverride(name="unit", column=@Column(name="diuresis_unit"))
	})
	public PQ getDiuresis() {
		return diuresis;
	}

	public void setDiuresis(PQ diuresis) {
		this.diuresis = diuresis;
	}

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="pain_value")),
	       @AttributeOverride(name="unit", column=@Column(name="pain_unit"))
	})
	public PQ getPain() {
		return pain;
	}

	public void setPain(PQ pain) {
		this.pain = pain;
	}

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="heartRate_value")),
	       @AttributeOverride(name="unit", column=@Column(name="heartRate_unit"))
	})
	public PQ getHeartRate() {
		return heartRate;
	}

	public void setHeartRate(PQ heartRate) {
		this.heartRate = heartRate;
	}

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="spo2_value")),
	       @AttributeOverride(name="unit", column=@Column(name="spo2_unit"))
	})
	public PQ getSpo2() {
		return spo2;
	}

	public void setSpo2(PQ spo2) {
		this.spo2 = spo2;
	}
	
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="systolicPressure_Threshold")
	@ForeignKey(name="FK_Prescription_syst_Levl")
	@Index(name="IX_Prescription_syst_Levl")
	public CodeValue getSystolicPressureThreshold() {
		return systolicPressureThreshold;
	}

	public void setSystolicPressureThreshold(
			CodeValue systolicPressureThreshold) {
		this.systolicPressureThreshold = systolicPressureThreshold;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="diastolicPressure_Threshold")
	@ForeignKey(name="FK_Prescription_diast_Lvl")
	@Index(name="IX_Prescription_diast_Lvl")
	public CodeValue getDiastolicPressureThreshold() {
		return diastolicPressureThreshold;
	}

	public void setDiastolicPressureThreshold(
			CodeValue diastolicPressureThreshold) {
		this.diastolicPressureThreshold = diastolicPressureThreshold;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="temperature_Threshold")
	@ForeignKey(name="FK_Prescription_temp_lvl")
	@Index(name="IX_Prescription_temp_lvl")
	public CodeValue getTemperatureThreshold() {
		return temperatureThreshold;
	}

	public void setTemperatureThreshold(CodeValue temperatureThreshold) {
		this.temperatureThreshold = temperatureThreshold;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="glycemia_Threshold")
	@ForeignKey(name="FK_Prescription_glyc_lvl")
	@Index(name="IX_Prescription_glyc_lvl")
	public CodeValue getGlycemiaThreshold() {
		return glycemiaThreshold;
	}

	public void setGlycemiaThreshold(CodeValue glycemiaThreshold) {
		this.glycemiaThreshold = glycemiaThreshold;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="diuresis_Threshold")
	@ForeignKey(name="FK_Prescription_diur_lvl")
	@Index(name="IX_Prescription_diur_lvl")
	public CodeValue getDiuresisThreshold() {
		return diuresisThreshold;
	}

	public void setDiuresisThreshold(CodeValue diuresisThreshold) {
		this.diuresisThreshold = diuresisThreshold;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="pain_Threshold")
	@ForeignKey(name="FK_Prescription_pain_lvl")
	@Index(name="IX_Prescription_pain_lvl")
	public CodeValue getPainThreshold() {
		return painThreshold;
	}

	public void setPainThreshold(CodeValue painThreshold) {
		this.painThreshold = painThreshold;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="heart_RateThreshold")
	@ForeignKey(name="FK_Prescription_heart_lvl")
	@Index(name="IX_Prescription_heart_lvl")
	public CodeValue getHeartRateThreshold() {
		return heartRateThreshold;
	}

	public void setHeartRateThreshold(CodeValue heartRateThreshold) {
		this.heartRateThreshold = heartRateThreshold;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="spo2_Threshold")
	@ForeignKey(name="FK_Prescription_spo2_lvl")
	@Index(name="IX_Prescription_spo2_lvl")
	public CodeValue getSpo2Threshold() {
		return spo2Threshold;
	}

	public void setSpo2Threshold(CodeValue spo2Threshold) {
		this.spo2Threshold = spo2Threshold;
	}
	
}