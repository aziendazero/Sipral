package com.phi.entities.act;

import java.util.Date;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.baseEntity.AppointmentGrouper;
import com.phi.entities.baseEntity.IntraoperatoryCard;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.PQ;
import com.phi.entities.dataTypes.PQD;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;

/**
 * Entity implementation class for Entity: VitalSign
 *
 */
@Entity
@Table(name = "vital_sign")
@Audited
public class VitalSign extends AuditedEntity implements LocatedEntity {

	private static final long serialVersionUID = -4701529074434126935L;

	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_VitalSign_Pat_Enc")
	@Index(name="IX_VitalSign_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter param) {
		this.patientEncounter = param;
	}



	/**
	 *  javadoc for AppointmentGrouper
	 */
	private AppointmentGrouper appointmentGrouper;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="appointmnet_grouper_id")
	@ForeignKey(name="FK_vitalSgn_appGroup")
	@Index(name="IX_vitalSgn_appGroup")
	public AppointmentGrouper getAppointmentGrouper(){
		return appointmentGrouper;
	}

	public void setAppointmentGrouper(AppointmentGrouper appointmentGrouper){
		this.appointmentGrouper = appointmentGrouper;
	}




	private PQD bodyTemperature; 
	private PQ rectalTemperature;	// usata in pediatria
	private PQD diastolic; 
	private PQD systolic; 
	private PQD cardiacFrequency; 
	private PQ urineStick; 
	private PQ glycemia;  
	private PQ weight;  
	private PQ weightPerc;			// usato in pediatria: percentile sulla misura di peso
	private PQ height;
	private PQ heightPerc;			// usato in pediatria: percentile sulla misura di altezza
	private PQ cc;					// usato in pediatria: circonferenza cranica
	private PQ ccPerc;				// usato in pediatria: percentile sulla misura di cc
	private PQ pain; 
	private PQ gcs;
	private PQD breathFrequency;
	private PQD o2Saturation; 
	private PQD diuresis;
	private boolean confirmed;

	private String insertMode;


	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="bodyTemperature_value")),
		@AttributeOverride(name="unit", column=@Column(name="bodyTemperature_unit")),
		@AttributeOverride(name="date", column=@Column(name="bodyTemperature_date"))
	})
	public PQD getBodyTemperature() {
		return bodyTemperature;
	}

	public void setBodyTemperature(PQD bodyTemperature) {
		this.bodyTemperature = bodyTemperature;
	}

	@Embedded
	@AttributeOverrides( {
		@AttributeOverride(name = "value", column = @Column(name = "rectalTemperature_value")),
		@AttributeOverride(name = "unit", column = @Column(name = "rectalTemperature_unit")) })
	public PQ getRectalTemperature() {
		return rectalTemperature;
	}

	public void setRectalTemperature(PQ rectalTemperature) {
		this.rectalTemperature = rectalTemperature;
	}

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="diastolic_value")),
		@AttributeOverride(name="unit", column=@Column(name="diastolic_unit")),
		@AttributeOverride(name="date", column=@Column(name="diastolic_date"))
	})
	public PQD getDiastolic() {
		return diastolic;
	}

	public void setDiastolic(PQD diastolic) {
		this.diastolic = diastolic;
	}



	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="systolic_value")),
		@AttributeOverride(name="unit", column=@Column(name="systolic_unit")),
		@AttributeOverride(name="date", column=@Column(name="systolic_date"))
	})
	public PQD getSystolic() {
		return systolic;
	}

	public void setSystolic(PQD systolic) {
		this.systolic = systolic;
	}


	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="cardiacFrequency_value")),
		@AttributeOverride(name="unit", column=@Column(name="cardiacFrequency_unit")),
		@AttributeOverride(name="date", column=@Column(name="cardiacFrequency_date"))
	})
	public PQD getCardiacFrequency() {
		return cardiacFrequency;
	}

	public void setCardiacFrequency(PQD cardiacFrequency) {
		this.cardiacFrequency = cardiacFrequency;
	}




	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="urineStick_value")),
		@AttributeOverride(name="unit", column=@Column(name="urineStick_unit"))
	})
	public PQ getUrineStick() {
		return urineStick;
	}

	public void setUrineStick(PQ urineStick) {
		this.urineStick = urineStick;
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
		@AttributeOverride(name="value", column=@Column(name="weight_value")),
		@AttributeOverride(name="unit", column=@Column(name="weight_unit"))
	})
	public PQ getWeight() {
		return weight;
	}

	public void setWeight(PQ weight) {
		this.weight = weight;
	}
	@Embedded
	@AttributeOverrides( {
		@AttributeOverride(name = "value", column = @Column(name = "weightPerc_value")),
		@AttributeOverride(name = "unit", column = @Column(name = "weightPerc_unit")) })
	public PQ getWeightPerc() {
		return weightPerc;
	}

	public void setWeightPerc(PQ weightPerc) {
		this.weightPerc = weightPerc;
	}
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="height_value")),
		@AttributeOverride(name="unit", column=@Column(name="height_unit"))
	})
	public PQ getHeight() {
		return height;
	}

	public void setHeight(PQ height) {
		this.height = height;
	}
	@Embedded
	@AttributeOverrides( {
		@AttributeOverride(name = "value", column = @Column(name = "heightPerc_value")),
		@AttributeOverride(name = "unit", column = @Column(name = "heightPerc_unit")) })
	public PQ getHeightPerc() {
		return heightPerc;
	}

	public void setHeightPerc(PQ heightPerc) {
		this.heightPerc = heightPerc;
	}
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "value", column = @Column(name = "cc_value")),
		@AttributeOverride(name = "unit", column = @Column(name = "cc_unit")) })
	public PQ getCc() {
		return cc;
	}
	public void setCc(PQ cc) {
		this.cc = cc;
	}
	@Embedded
	@AttributeOverrides( {
		@AttributeOverride(name = "value", column = @Column(name = "ccPerc_value")),
		@AttributeOverride(name = "unit", column = @Column(name = "ccPerc_unit")) })
	public PQ getCcPerc() {
		return ccPerc;
	}
	public void setCcPerc(PQ ccPerc) {
		this.ccPerc = ccPerc;
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
		@AttributeOverride(name="value", column=@Column(name="gcs_value")),
		@AttributeOverride(name="unit", column=@Column(name="gcs_unit"))
	})
	public PQ getGcs() {
		return gcs;
	}

	public void setGcs(PQ gcs) {
		this.gcs = gcs;
	}


	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="breathFrequency_value")),
		@AttributeOverride(name="unit", column=@Column(name="breathFrequency_unit")),
		@AttributeOverride(name="date", column=@Column(name="breathFrequency_date"))
	})
	public PQD getBreathFrequency() {
		return breathFrequency;
	}

	public void setBreathFrequency(PQD breathFrequency) {
		this.breathFrequency = breathFrequency;
	}


	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="o2Saturation_value")),
		@AttributeOverride(name="unit", column=@Column(name="o2Saturation_unit")),
		@AttributeOverride(name="date", column=@Column(name="o2Saturation_date"))
	})
	public PQD getO2Saturation() {
		return o2Saturation;
	}

	public void setO2Saturation(PQD o2Saturation) {
		this.o2Saturation = o2Saturation;
	}

	/**
	 *  javadoc for diuresis
	 */

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="diuresis_value")),
		@AttributeOverride(name="unit", column=@Column(name="diuresis_unit")),
		@AttributeOverride(name="date", column=@Column(name="diuresis_date"))
	})
	public PQD getDiuresis(){
		return diuresis;
	}

	public void setDiuresis(PQD diuresis){
		this.diuresis = diuresis;
	}



	@Column(name="confirmed")
	public boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	@Column(name="insert_mode")
	public String getInsertMode() {
		return insertMode;
	}
	public void setInsertMode(String insertMode) {
		this.insertMode = insertMode;
	}	


	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient")
	@ForeignKey(name="FK_VitalSign_Patient")
	@Index(name="IX_VitalSign_Patient")
	//@NotAudited
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient param) {
		this.patient = param;
	}

	private CodeValuePhi reactionEyeR;
	private CodeValuePhi reactionEyeL;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="reactionEyeR")
	@ForeignKey(name="FK_VitalSign_recEyeR")
	@Index(name="IX_VitalSign_recEyeR")	
	public CodeValuePhi getReactionEyeR() {
		return reactionEyeR;
	}
	public void setReactionEyeR(CodeValuePhi reactionEyeR) {
		this.reactionEyeR = reactionEyeR;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="reactionEyeL")
	@ForeignKey(name="FK_VitalSign_recEyeL")
	@Index(name="IX_VitalSign_recEyeL")	
	public CodeValuePhi getReactionEyeL() {
		return reactionEyeL;
	}
	public void setReactionEyeL(CodeValuePhi reactionEyeL) {
		this.reactionEyeL = reactionEyeL;
	}

	private PQ diameterEyeR;
	private PQ diameterEyeL;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="diameterEyeR_value")),
		@AttributeOverride(name="unit", column=@Column(name="diameterEyeR_unit"))
	})
	public PQ getDiameterEyeR() {
		return diameterEyeR;
	}
	public void setDiameterEyeR(PQ diameterEyeR) {
		this.diameterEyeR = diameterEyeR;
	}

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="diameterEyeL_value")),
		@AttributeOverride(name="unit", column=@Column(name="diameterEyeL_unit"))
	})
	public PQ getDiameterEyeL() {
		return diameterEyeL;
	}
	public void setDiameterEyeL(PQ diameterEyeL) {
		this.diameterEyeL = diameterEyeL;
	}

	private PQ cvp; //Central Venous Pressure also known as right atrial pressur RAP)

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="cvp_value")),
		@AttributeOverride(name="unit", column=@Column(name="cvp_unit"))
	})
	public PQ getCvp() {
		return cvp;
	}
	public void setCvp(PQ cvp) {
		this.cvp = cvp;
	}

	private Date evaluationDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="evaluationDate")	
	public Date getEvaluationDate() {
		return evaluationDate;
	}
	public void setEvaluationDate(Date evaluationDate) {
		this.evaluationDate = evaluationDate;
	}
	/**
	 *  javadoc for drainUpperRight
	 */
	private PQ drainUpperRight;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="drainUpperRight_value")),
		@AttributeOverride(name="unit", column=@Column(name="drainUpperRight_unit"))
	})
	public PQ getDrainUpperRight(){
		return drainUpperRight;
	}

	public void setDrainUpperRight(PQ drainUpperRight){
		this.drainUpperRight = drainUpperRight;
	}

	/**
	 *  javadoc for drainLowerRight
	 */
	private PQ drainLowerRight;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="drainLowerRight_value")),
		@AttributeOverride(name="unit", column=@Column(name="drainLowerRight_unit"))
	})
	public PQ getDrainLowerRight(){
		return drainLowerRight;
	}

	public void setDrainLowerRight(PQ drainLowerRight){
		this.drainLowerRight = drainLowerRight;
	}

	/**
	 *  javadoc for drainUpperLeft
	 */
	private PQ drainUpperLeft;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="drainUpperLeft_value")),
		@AttributeOverride(name="unit", column=@Column(name="drainUpperLeft_unit"))
	})
	public PQ getDrainUpperLeft(){
		return drainUpperLeft;
	}

	public void setDrainUpperLeft(PQ drainUpperLeft){
		this.drainUpperLeft = drainUpperLeft;
	}

	/**
	 *  javadoc for drainLowerLeft
	 */
	private PQ drainLowerLeft;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="drainLowerLeft_value")),
		@AttributeOverride(name="unit", column=@Column(name="drainLowerLeft_unit"))
	})
	public PQ getDrainLowerLeft(){
		return drainLowerLeft;
	}

	public void setDrainLowerLeft(PQ drainLowerLeft){
		this.drainLowerLeft = drainLowerLeft;
	}

	/**
	 *  javadoc for ilestomy
	 */
	private PQ ilestomy;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="ilestomy_value")),
		@AttributeOverride(name="unit", column=@Column(name="ilestomy_unit"))
	})
	public PQ getIlestomy(){
		return ilestomy;
	}

	public void setIlestomy(PQ ilestomy){
		this.ilestomy = ilestomy;
	}

	/**
	 *  javadoc for drainChest
	 */
	private PQ drainChest;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="drainChest_value")),
		@AttributeOverride(name="unit", column=@Column(name="drainChest_unit"))
	})
	public PQ getDrainChest(){
		return drainChest;
	}

	public void setDrainChest(PQ drainChest){
		this.drainChest = drainChest;
	}

	/**
	 *  javadoc for sng
	 */
	private PQ sng;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="sng_value")),
		@AttributeOverride(name="unit", column=@Column(name="sng_unit"))
	})
	public PQ getSng(){
		return sng;
	}

	public void setSng(PQ sng){
		this.sng = sng;
	}

	/**
	 *  javadoc for drain
	 */
	private PQ drain;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="drain_value")),
		@AttributeOverride(name="unit", column=@Column(name="drain_unit"))
	})
	public PQ getDrain(){
		return drain;
	}

	public void setDrain(PQ drain){
		this.drain = drain;
	}

	/**
	 *  hematicLoss: Perdite ematiche
	 */
	private PQD hematicLoss;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="hematicLoss_value")),
		@AttributeOverride(name="unit", column=@Column(name="hematicLoss_unit")),
		@AttributeOverride(name="date", column=@Column(name="hematicLoss_date"))
	})
	public PQD getHematicLoss(){
		return hematicLoss;
	}

	public void setHematicLoss(PQD hematicLoss){
		this.hematicLoss = hematicLoss;
	}

	/**
	 *  javadoc for perspiratio
	 */
	private PQD perspiratio;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="perspiratio_value")),
		@AttributeOverride(name="unit", column=@Column(name="perspiratio_unit")),
		@AttributeOverride(name="date", column=@Column(name="perspiratio_date"))
	})
	public PQD getPerspiratio(){
		return perspiratio;
	}

	public void setPerspiratio(PQD perspiratio){
		this.perspiratio = perspiratio;
	}


	/**
	 *  javadoc for etCO2
	 */
	private PQD etCO2;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="etCO2_value")),
		@AttributeOverride(name="unit", column=@Column(name="etCO2_unit")),
		@AttributeOverride(name="date", column=@Column(name="etCO2_date"))
	})
	public PQD getEtCO2(){
		return etCO2;
	}

	public void setEtCO2(PQD etCO2){
		this.etCO2 = etCO2;
	}

	/**
	 *  Volume tidalico
	 *  tv/fr -> tv/breathFrequency
	 */
	private PQD tv;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="tv_value")),
		@AttributeOverride(name="unit", column=@Column(name="tv_unit")),
		@AttributeOverride(name="date", column=@Column(name="tv_date"))
	})
	public PQD getTv(){
		return tv;
	}

	public void setTv(PQD tv){
		this.tv = tv;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "VitalSign_sequence")
	@SequenceGenerator(name = "VitalSign_sequence", sequenceName = "VitalSign_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	protected ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_VitalSign_sdloc")
	@Index(name="IX_VitalSign_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_VitalSign_author")
	@Index(name="IX_VitalSign_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}

	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="authorRole")
	@ForeignKey(name="FK_VitalSign_authorRole")
	@Index(name="IX_VitalSign_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}

	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cancelled_by_id")
	@ForeignKey(name="FK_VitalSign_cancelledBy")
	@Index(name="IX_VitalSigncancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}

	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cancelledByRole")
	@ForeignKey(name="FK_VitalSign_cancelledByRole")
	@Index(name="IX_VitalSign_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}

	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_VitalSign_code") //FIXME: accorciato FK_Act_code
	public CodeValuePhi getCode() {
		return code;
	}

	public void setCode(CodeValuePhi code) {
		this.code = code;
	}

	//IntraoperatoryCard fields

	/**
	 *  Pressione sistolica fatta con metodi "cruenti"
	 */
	private PQD invasiveSystolic;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="invasiveSystolic_value")),
		@AttributeOverride(name="unit", column=@Column(name="invasiveSystolic_unit")),
		@AttributeOverride(name="date", column=@Column(name="invasiveSystolic_date"))
	})
	public PQD getInvasiveSystolic(){
		return invasiveSystolic;
	}

	public void setInvasiveSystolic(PQD invasiveSystolic){
		this.invasiveSystolic = invasiveSystolic;
	}

	/**
	 *  Pressione diastolica fatta con metodi "cruenti"
	 */
	private PQD invasiveDiastolic;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="invasiveDiastolic_value")),
		@AttributeOverride(name="unit", column=@Column(name="invasiveDiastolic_unit")),
		@AttributeOverride(name="date", column=@Column(name="invasiveDiastolic_date"))
	})
	public PQD getInvasiveDiastolic(){
		return invasiveDiastolic;
	}

	public void setInvasiveDiastolic(PQD invasiveDiastolic){
		this.invasiveDiastolic = invasiveDiastolic;
	}

	/**
	 *  cvc
	 */
	private Boolean cvc;

	@Column(name="cvc")
	public Boolean getCvc(){
		return cvc;
	}

	public void setCvc(Boolean cvc){
		this.cvc = cvc;
	}

	/**
	 *  cvc data inserimento
	 */
	private Date cvcDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="cvc_date")
	public Date getCvcDate(){
		return cvcDate;
	}

	public void setCvcDate(Date cvcDate){
		this.cvcDate = cvcDate;
	}

	/**
	 *  gastricProbe
	 */
	private Boolean gastricProbe;

	@Column(name="gastric_probe")
	public Boolean getGastricProbe(){
		return gastricProbe;
	}

	public void setGastricProbe(Boolean gastricProbe){
		this.gastricProbe = gastricProbe;
	}

	/**
	 *  gastricProbe data inserimento
	 */
	private Date gastricProbeDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="gastric_probe_date")
	public Date getGastricProbeDate(){
		return gastricProbeDate;
	}

	public void setGastricProbeDate(Date gastricProbeDate){
		this.gastricProbeDate = gastricProbeDate;
	}

	/**
	 *  ega
	 */
	private Boolean ega;

	@Column(name="ega")
	public Boolean getEga(){
		return ega;
	}

	public void setEga(Boolean ega){
		this.ega = ega;
	}

	/**
	 *  ega data inserimento
	 */
	private Date egaDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ega_date")
	public Date getEgaDate(){
		return egaDate;
	}

	public void setEgaDate(Date egaDate){
		this.egaDate = egaDate;
	}

	/**
	 *  javadoc for intraoperatoryCard
	 */
	private IntraoperatoryCard intraoperatoryCard;

	@JsonBackReference

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="IntraoperatoryCard_id")
	@ForeignKey(name="FK_vtalSgn_IntraopertoryCrd")
	@Index(name="IX_vtalSgn_IntraopertoryCrd")
	public IntraoperatoryCard getIntraoperatoryCard(){
		return intraoperatoryCard;
	}

	public void setIntraoperatoryCard(IntraoperatoryCard intraoperatoryCard){
		this.intraoperatoryCard = intraoperatoryCard;
	}

	/**
	 *  javadoc for measureOrIO1
	 */
	private PQD measureOrIO1;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="measureOrIO1_value")),
		@AttributeOverride(name="unit", column=@Column(name="measureOrIO1_unit")),
		@AttributeOverride(name="date", column=@Column(name="measureOrIO1_date"))
	})
	public PQD getMeasureOrIO1(){
		return measureOrIO1;
	}

	public void setMeasureOrIO1(PQD measureOrIO1){
		this.measureOrIO1 = measureOrIO1;
	}

	/**
	 *  javadoc for measureOrIO2
	 */
	private PQD measureOrIO2;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="measureOrIO2_value")),
		@AttributeOverride(name="unit", column=@Column(name="measureOrIO2_unit")),
		@AttributeOverride(name="date", column=@Column(name="measureOrIO2_date"))
	})
	public PQD getMeasureOrIO2(){
		return measureOrIO2;
	}

	public void setMeasureOrIO2(PQD measureOrIO2){
		this.measureOrIO2 = measureOrIO2;
	}

	/**
	 *  javadoc for measureOrIO3
	 */
	private PQD measureOrIO3;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="measureOrIO3_value")),
		@AttributeOverride(name="unit", column=@Column(name="measureOrIO3_unit")),
		@AttributeOverride(name="date", column=@Column(name="measureOrIO3_date"))
	})
	public PQD getMeasureOrIO3(){
		return measureOrIO3;
	}

	public void setMeasureOrIO3(PQD measureOrIO3){
		this.measureOrIO3 = measureOrIO3;
	}

	/**
	 *  javadoc for measureOrIO4
	 */
	private PQD measureOrIO4;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="measureOrIO4_value")),
		@AttributeOverride(name="unit", column=@Column(name="measureOrIO4_unit")),
		@AttributeOverride(name="date", column=@Column(name="measureOrIO4_date"))
	})
	public PQD getMeasureOrIO4(){
		return measureOrIO4;
	}

	public void setMeasureOrIO4(PQD measureOrIO4){
		this.measureOrIO4 = measureOrIO4;
	}

	/**
	 *  javadoc for measureOrIO5
	 */
	private PQD measureOrIO5;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="measureOrIO5_value")),
		@AttributeOverride(name="unit", column=@Column(name="measureOrIO5_unit")),
		@AttributeOverride(name="date", column=@Column(name="measureOrIO5_date"))
	})
	public PQD getMeasureOrIO5(){
		return measureOrIO5;
	}

	public void setMeasureOrIO5(PQD measureOrIO5){
		this.measureOrIO5 = measureOrIO5;
	}

	/**
	 *  javadoc for measureOrIO6
	 */
	private PQD measureOrIO6;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="measureOrIO6_value")),
		@AttributeOverride(name="unit", column=@Column(name="measureOrIO6_unit")),
		@AttributeOverride(name="date", column=@Column(name="measureOrIO6_date"))
	})
	public PQD getMeasureOrIO6(){
		return measureOrIO6;
	}

	public void setMeasureOrIO6(PQD measureOrIO6){
		this.measureOrIO6 = measureOrIO6;
	}



	/**
	 *  javadoc for administration1
	 */
	private PQD administration1;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="administration1_value")),
		@AttributeOverride(name="unit", column=@Column(name="administration1_unit")),
		@AttributeOverride(name="date", column=@Column(name="administration1_date"))
	})
	public PQD getAdministration1(){
		return administration1;
	}

	public void setAdministration1(PQD administration1){
		this.administration1 = administration1;
	}

	/**
	 *  javadoc for administration2
	 */
	private PQD administration2;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="administration2_value")),
		@AttributeOverride(name="unit", column=@Column(name="administration2_unit")),
		@AttributeOverride(name="date", column=@Column(name="administration2_date"))
	})
	public PQD getAdministration2(){
		return administration2;
	}

	public void setAdministration2(PQD administration2){
		this.administration2 = administration2;
	}

	/**
	 *  javadoc for administration3
	 */
	private PQD administration3;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="administration3_value")),
		@AttributeOverride(name="unit", column=@Column(name="administration3_unit")),
		@AttributeOverride(name="date", column=@Column(name="administration3_date"))
	})
	public PQD getAdministration3(){
		return administration3;
	}

	public void setAdministration3(PQD administration3){
		this.administration3 = administration3;
	}

	/**
	 *  javadoc for administration4
	 */
	private PQD administration4;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="administration4_value")),
		@AttributeOverride(name="unit", column=@Column(name="administration4_unit")),
		@AttributeOverride(name="date", column=@Column(name="administration4_date"))
	})
	public PQD getAdministration4(){
		return administration4;
	}

	public void setAdministration4(PQD administration4){
		this.administration4 = administration4;
	}

	/**
	 *  javadoc for administration5
	 */
	private PQD administration5;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="administration5_value")),
		@AttributeOverride(name="unit", column=@Column(name="administration5_unit")),
		@AttributeOverride(name="date", column=@Column(name="administration5_date"))
	})
	public PQD getAdministration5(){
		return administration5;
	}

	public void setAdministration5(PQD administration5){
		this.administration5 = administration5;
	}

	/**
	 *  javadoc for administration6
	 */
	private PQD administration6;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="administration6_value")),
		@AttributeOverride(name="unit", column=@Column(name="administration6_unit")),
		@AttributeOverride(name="date", column=@Column(name="administration6_date"))
	})
	public PQD getAdministration6(){
		return administration6;
	}

	public void setAdministration6(PQD administration6){
		this.administration6 = administration6;
	}

	//IntraoperatoryCard fields end
}