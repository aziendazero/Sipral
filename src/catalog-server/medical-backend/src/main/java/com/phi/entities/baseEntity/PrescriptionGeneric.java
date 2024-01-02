package com.phi.entities.baseEntity;

import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.act.Therapy;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.PQ;
import com.phi.entities.role.Employee;

/**
 * Generic prescription, sub classed by: Prescription and PrescriptionDischarge
 * @author alex.zupan
 */

@Entity
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PrescriptionGeneric extends AuditedEntity {

	private static final long serialVersionUID = -5408554964376825924L;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Prescription_sequence")
	@SequenceGenerator(name = "Prescription_sequence", sequenceName = "Prescription_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_Prescription_sdloc")
	@Index(name="IX_CliProc_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	
	private CodeValue routeCode;
	
	private String note;
//	private IVL<Date> period;
//	private IVL<Date> validityPeriod;
	private boolean needsBased = false;
	private boolean extemporaneous = false;
	private boolean urgent = false;
	private boolean continuous = false;
//	private List<Dosage> dosage;
	
	//Quantity is copied in dosage for medicine
	private PQ quantity;
	
	//As needed administration
	private PQ maximumQuantity24h;
	private String hoursGap;
	private String minsGap;
	private String other;
	
//	private PQ systolicPressure;
//	private PQ diastolicPressure;
//	private PQ temperature;
//	private PQ glycemia;
//	private PQ diuresis;
//	private PQ pain;
//	private PQ heartRate;
//	private PQ spo2;
//	
//	private CodeValue systolicPressureThreshold;
//	private CodeValue diastolicPressureThreshold;
//	private CodeValue temperatureThreshold;
//	private CodeValue glycemiaThreshold;
//	private CodeValue diuresisThreshold;
//	private CodeValue painThreshold;
//	private CodeValue heartRateThreshold;
//	private CodeValue spo2Threshold;
	
	
	//planned administration
	private String scheduling;
	
	
	//Infusion
	private CodeValue infusionTypeCode;
	private PQ infusionSpeed;
	private PQ infusionDuration;
	
	
	@Column(name="needsBased")
	public Boolean getNeedsBased() {
		return needsBased;
	}

	public void setNeedsBased(Boolean needsBased) {
		this.needsBased = needsBased;
	}

	private boolean glySecondaryProtocol = false;
	
	@Column(name="gly_sec_prot")
	public Boolean getGlySecondaryProtocol() {
		return glySecondaryProtocol;
	}

	public void setGlySecondaryProtocol(Boolean glySecondaryProtocol) {
		this.glySecondaryProtocol = glySecondaryProtocol;
	}
	
	private CodeValue painTypeCode;
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="pain_Type_Code")
	@ForeignKey(name="FK_Prescription_pai_type")
	@Index(name="IX_Prescription_pai_type")
	public CodeValue getPainTypeCode() {
		return painTypeCode;
	}

	public void setPainTypeCode(CodeValue painTypeCode) {
		this.painTypeCode = painTypeCode;
	}
	
	@Column(name="extemporaneous")
	public Boolean getExtemporaneous() {
		return extemporaneous;
	}

	public void setExtemporaneous(Boolean extemporaneous) {
		this.extemporaneous = extemporaneous;
	}
	
	@Column(name="continuous")
	public Boolean getContinuous() {
		return continuous;
	}

	public void setContinuous(Boolean continuous) {
		this.continuous = continuous;
	}	
	
	
	@Column(name="urgent")
	public Boolean getUrgent() {
		return urgent;
	}

	public void setUrgent(Boolean urgent) {
		this.urgent = urgent;
	}	
	
	private String deleteNote;
	private Boolean modified;
//	private List<SubstanceAdministration> substanceAdministration;

	protected Therapy therapy;
	
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="therapy")
//	@ForeignKey(name="FK_Prescription_therapy")
//	@Index(name="IX_Prescription_therapy")
//	public Therapy getTherapy() {
//		return therapy;
//	}
	
	@Transient //To be implemented in subclasses
	public abstract Therapy getTherapy();

	public void setTherapy(Therapy therapy) {
		this.therapy = therapy;
	}
	
	@Column(name="delete_note")
	public String getDeleteNote() {
		return deleteNote;
	}

	public void setDeleteNote(String deleteNote) {
		this.deleteNote = deleteNote;
	}
	
	@Column(name="modified")
	public Boolean getModified() {
		return modified;
	}

	public void setModified(Boolean modified) {
		this.modified = modified;
	}

//	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "prescription")
//	public List<SubstanceAdministration> getSubstanceAdministration() {
//	    return substanceAdministration;
//	}
//
//	public void setSubstanceAdministration(List<SubstanceAdministration> param) {
//	    this.substanceAdministration = param;
//	}

//	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "prescription")
//	public List<Dosage> getDosage() {
//	    return dosage;
//	}
//
//	public void setDosage(List<Dosage> param) {
//	    this.dosage = param;
//	}
//
//	public void addDosage(Dosage dosage) {
//		if (this.dosage == null) {
//			this.dosage = new ArrayList<Dosage>();
//		}
//		// add the association
//		if(!this.dosage.contains(dosage)) {
//			this.dosage.add(dosage);
//			// make the inverse link
//			dosage.setPrescription(this);
//		}
//	}
	
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="route_code")
	@ForeignKey(name="FK_Prescription_RouteCode")
	@Index(name="IX_Prescription_RouteCode")
	public CodeValue getRouteCode() {
		return routeCode;
	}

	public void setRouteCode(CodeValue routeCode) {
		this.routeCode = routeCode;
	}

	@Column(name="note")
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

//	@Embedded
//	@AttributeOverrides({
//		@AttributeOverride(name="low", column=@Column(name="period_time_low")),
//		@AttributeOverride(name="high", column=@Column(name="period_time_high")),
//		@AttributeOverride(name="lowClosed", column=@Column(name="period_lowClosed")),
//		@AttributeOverride(name="highClosed", column=@Column(name="period_highClosed"))
//	})
//
//	public IVL<Date> getPeriod() {
//		return period;
//	}
//
//	public void setPeriod(IVL<Date> period) {
//		this.period = period;
//	}
	
//	@Embedded
//	@AttributeOverrides({
//		@AttributeOverride(name="low", column=@Column(name="validity_period_time_low")),
//		@AttributeOverride(name="high", column=@Column(name="validity_period_time_high")),
//		@AttributeOverride(name="lowClosed", column=@Column(name="vperiod_lowClosed")),
//		@AttributeOverride(name="highClosed", column=@Column(name="validity_period_highClosed"))
//	})
//
//	public IVL<Date> getValidityPeriod() {
//		return validityPeriod;
//	}
//
//	public void setValidityPeriod(IVL<Date> validityPeriod) {
//		this.validityPeriod = validityPeriod;
//	}
	
	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="maxQuant24h_value")),
	       @AttributeOverride(name="unit", column=@Column(name="maxQuant24h_unit"))
	})
	public PQ getMaximumQuantity24h() {
		return maximumQuantity24h;
	}

	public void setMaximumQuantity24h(PQ maximumQuantity24h) {
		this.maximumQuantity24h = maximumQuantity24h;
	}

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="quantity_value")),
	       @AttributeOverride(name="unit", column=@Column(name="quantity_unit"))
	})
	public PQ getQuantity() {
		return quantity;
	}

	public void setQuantity(PQ quantity) {
		this.quantity = quantity;
	}
	
	
	@Column(name="hoursGap")
	public String getHoursGap() {
		return hoursGap;
	}

	public void setHoursGap(String hoursGap) {
		this.hoursGap = hoursGap;
	}

	@Column(name="minsGap")
	public String getMinsGap() {
		return minsGap;
	}

	public void setMinsGap(String minsGap) {
		this.minsGap = minsGap;
	}

	@Column(name="other",length=4000)
	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}
	
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="value", column=@Column(name="systolic_pressure_value")),
//	       @AttributeOverride(name="unit", column=@Column(name="systolic_pressure_unit"))
//	})
//	public PQ getSystolicPressure() {
//		return systolicPressure;
//	}
//
//	public void setSystolicPressure(PQ systolicPressure) {
//		this.systolicPressure = systolicPressure;
//	}
//
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="value", column=@Column(name="diastolic_pressure_value")),
//	       @AttributeOverride(name="unit", column=@Column(name="diastolic_pressure_unit"))
//	})
//	public PQ getDiastolicPressure() {
//		return diastolicPressure;
//	}
//
//	public void setDiastolicPressure(PQ diastolicPressure) {
//		this.diastolicPressure = diastolicPressure;
//	}
//
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="value", column=@Column(name="temperature_value")),
//	       @AttributeOverride(name="unit", column=@Column(name="temperature_unit"))
//	})
//	public PQ getTemperature() {
//		return temperature;
//	}
//
//	public void setTemperature(PQ temperature) {
//		this.temperature = temperature;
//	}
//
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="value", column=@Column(name="glycemia_value")),
//	       @AttributeOverride(name="unit", column=@Column(name="glycemia_unit"))
//	})
//	public PQ getGlycemia() {
//		return glycemia;
//	}
//
//	public void setGlycemia(PQ glycemia) {
//		this.glycemia = glycemia;
//	}
//
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="value", column=@Column(name="diuresis_value")),
//	       @AttributeOverride(name="unit", column=@Column(name="diuresis_unit"))
//	})
//	public PQ getDiuresis() {
//		return diuresis;
//	}
//
//	public void setDiuresis(PQ diuresis) {
//		this.diuresis = diuresis;
//	}
//
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="value", column=@Column(name="pain_value")),
//	       @AttributeOverride(name="unit", column=@Column(name="pain_unit"))
//	})
//	public PQ getPain() {
//		return pain;
//	}
//
//	public void setPain(PQ pain) {
//		this.pain = pain;
//	}
//
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="value", column=@Column(name="heartRate_value")),
//	       @AttributeOverride(name="unit", column=@Column(name="heartRate_unit"))
//	})
//	public PQ getHeartRate() {
//		return heartRate;
//	}
//
//	public void setHeartRate(PQ heartRate) {
//		this.heartRate = heartRate;
//	}
//
//	@Embedded
//	@AttributeOverrides({
//	       @AttributeOverride(name="value", column=@Column(name="spo2_value")),
//	       @AttributeOverride(name="unit", column=@Column(name="spo2_unit"))
//	})
//	public PQ getSpo2() {
//		return spo2;
//	}
//
//	public void setSpo2(PQ spo2) {
//		this.spo2 = spo2;
//	}

	@Column(name="scheduling")
	public String getScheduling() {
		return scheduling;
	}

	public void setScheduling(String scheduling) {
		this.scheduling = scheduling;
	}

	
	
	
	
	
	
	
	
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//	@JoinColumn(name="systolicPressure_Threshold")
//	@ForeignKey(name="FK_Prescription_syst_Levl")
//	@Index(name="IX_Prescription_syst_Levl")
//	public CodeValue getSystolicPressureThreshold() {
//		return systolicPressureThreshold;
//	}
//
//	public void setSystolicPressureThreshold(
//			CodeValue systolicPressureThreshold) {
//		this.systolicPressureThreshold = systolicPressureThreshold;
//	}
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//	@JoinColumn(name="diastolicPressure_Threshold")
//	@ForeignKey(name="FK_Prescription_diast_Lvl")
//	@Index(name="IX_Prescription_diast_Lvl")
//	public CodeValue getDiastolicPressureThreshold() {
//		return diastolicPressureThreshold;
//	}
//
//	public void setDiastolicPressureThreshold(
//			CodeValue diastolicPressureThreshold) {
//		this.diastolicPressureThreshold = diastolicPressureThreshold;
//	}
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//	@JoinColumn(name="temperature_Threshold")
//	@ForeignKey(name="FK_Prescription_temp_lvl")
//	@Index(name="IX_Prescription_temp_lvl")
//	public CodeValue getTemperatureThreshold() {
//		return temperatureThreshold;
//	}
//
//	public void setTemperatureThreshold(CodeValue temperatureThreshold) {
//		this.temperatureThreshold = temperatureThreshold;
//	}
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//	@JoinColumn(name="glycemia_Threshold")
//	@ForeignKey(name="FK_Prescription_glyc_lvl")
//	@Index(name="IX_Prescription_glyc_lvl")
//	public CodeValue getGlycemiaThreshold() {
//		return glycemiaThreshold;
//	}
//
//	public void setGlycemiaThreshold(CodeValue glycemiaThreshold) {
//		this.glycemiaThreshold = glycemiaThreshold;
//	}
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//	@JoinColumn(name="diuresis_Threshold")
//	@ForeignKey(name="FK_Prescription_diur_lvl")
//	@Index(name="IX_Prescription_diur_lvl")
//	public CodeValue getDiuresisThreshold() {
//		return diuresisThreshold;
//	}
//
//	public void setDiuresisThreshold(CodeValue diuresisThreshold) {
//		this.diuresisThreshold = diuresisThreshold;
//	}
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//	@JoinColumn(name="pain_Threshold")
//	@ForeignKey(name="FK_Prescription_pain_lvl")
//	@Index(name="IX_Prescription_pain_lvl")
//	public CodeValue getPainThreshold() {
//		return painThreshold;
//	}
//
//	public void setPainThreshold(CodeValue painThreshold) {
//		this.painThreshold = painThreshold;
//	}
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//	@JoinColumn(name="heart_RateThreshold")
//	@ForeignKey(name="FK_Prescription_heart_lvl")
//	@Index(name="IX_Prescription_heart_lvl")
//	public CodeValue getHeartRateThreshold() {
//		return heartRateThreshold;
//	}
//
//	public void setHeartRateThreshold(CodeValue heartRateThreshold) {
//		this.heartRateThreshold = heartRateThreshold;
//	}
//
//	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
//	@JoinColumn(name="spo2_Threshold")
//	@ForeignKey(name="FK_Prescription_spo2_lvl")
//	@Index(name="IX_Prescription_spo2_lvl")
//	public CodeValue getSpo2Threshold() {
//		return spo2Threshold;
//	}
//
//	public void setSpo2Threshold(CodeValue spo2Threshold) {
//		this.spo2Threshold = spo2Threshold;
//	}
	

	
	
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="infusion_Type_Code")
	@ForeignKey(name="FK_Prescription_inf_type")
	@Index(name="IX_Prescription_inf_type")
	public CodeValue getInfusionTypeCode() {
		return infusionTypeCode;
	}

	public void setInfusionTypeCode(CodeValue infusionTypeCode) {
		this.infusionTypeCode = infusionTypeCode;
	}

	/**
	*  javadoc for prescriptionMedicine
	*/
	protected List<PrescriptionMedicineGeneric> prescriptionMedicine;

//	@OneToMany(fetch=FetchType.LAZY, mappedBy="prescription", cascade=CascadeType.PERSIST)
//	public List<PrescriptionMedicine> getPrescriptionMedicine(){
//		return prescriptionMedicine;
//	}
	
	@Transient //To be implemented in subclasses
	public abstract List<PrescriptionMedicineGeneric> getPrescriptionMedicine();


	public void setPrescriptionMedicine(List<PrescriptionMedicineGeneric> list){
		prescriptionMedicine = list;
	}

//	public void addPrescriptionMedicine(PrescriptionMedicineGeneric prescriptionMedicine) {
//		if (this.prescriptionMedicine == null) {
//			this.prescriptionMedicine = new ArrayList<PrescriptionMedicineGeneric>();
//		}
//		// add the association
//		if(!this.prescriptionMedicine.contains(prescriptionMedicine)) {
//			this.prescriptionMedicine.add(prescriptionMedicine);
//			// make the inverse link
//			prescriptionMedicine.setPrescription(this);
//		}
//	}
//
//	public void removePrescriptionMedicine(PrescriptionMedicine prescriptionMedicine) {
//		if (this.prescriptionMedicine == null) {
//			this.prescriptionMedicine = new ArrayList<PrescriptionMedicineGeneric>();
//			return;
//		}
//		//add the association
//		if(this.prescriptionMedicine.contains(prescriptionMedicine)){
//			this.prescriptionMedicine.remove(prescriptionMedicine);
//			//make the inverse link
//			prescriptionMedicine.setPrescription(null);
//		}
//
//	}
	
	
	
	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="infusion_speed_value")),
	       @AttributeOverride(name="unit", column=@Column(name="infusion_speed_unit"))
	})
	public PQ getInfusionSpeed() {
		return infusionSpeed;
	}

	public void setInfusionSpeed(PQ infusionSpeed) {
		this.infusionSpeed = infusionSpeed;
	}
	
	
	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="value", column=@Column(name="infusion_duration_value")),
	       @AttributeOverride(name="unit", column=@Column(name="infusion_duration_unit"))
	})
	public PQ getInfusionDuration() {
		return infusionDuration;
	}

	public void setInfusionDuration(PQ infusionDuration) {
		this.infusionDuration = infusionDuration;
	}
	
	//-------------from auditedEntity
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_Prscrpt_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_Prscrpt_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_Prscrpt_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_Prscrpt_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_Prscrpt_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_Prscrpt_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_Prscrpt_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_Prscrpt_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}
	
	//From act:
	
	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="status_code")
	@ForeignKey(name="FK_Prescription_sc")
	@Index(name="IX_Prescription_sc")
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}
	
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Prescription_c")
	@Index(name="IX_Prescription_c")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}

	/**
	 *  javadoc for rootPrescription
	 */
	 protected PrescriptionGeneric rootPrescription;

//	 @ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	 @JoinColumn(name="root_prescription_id")
//	 @ForeignKey(name="FK_Prscrpt_rtPrscrptn")
//	 @Index(name="IX_Prscrpt_rtPrscrptn")
//	 public Prescription getRootPrescription(){
//	  return rootPrescription;
//	 }
	 
	 @Transient //To be implemented in subclasses
	 public abstract PrescriptionGeneric getRootPrescription();

	 public void setRootPrescription(PrescriptionGeneric rootPrescription){
		 this.rootPrescription = rootPrescription;
	 }
}
