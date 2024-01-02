package com.phi.entities.act;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AssociationOverride;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.phi.entities.baseEntity.Appointment;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.MedicalHistory;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueExemption;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.ED;
import com.phi.entities.dataTypes.II;
import com.phi.entities.dataTypes.II4PatientEncounter;
import com.phi.entities.dataTypes.IIEmbeddable;
import com.phi.entities.dataTypes.PQ;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.tracedEntity.TracedEntity;
import com.phi.json.JsonProxyGenerator;

@Entity
@Table(name = "patient_encounter", uniqueConstraints = {@UniqueConstraint(columnNames = {"visit_number_root", "visit_number_extension"}), @UniqueConstraint(columnNames = {"preadmit_number_root", "preadmit_number_extension"})})
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=PatientEncounter.class)
public class PatientEncounter extends BaseEntity implements LocatedEntity, TracedEntity {

	private static final long serialVersionUID = -351972720240523760L;
	
	//methods needed for BaseEntity extension
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PatientEncounter_sequence")
	@SequenceGenerator(name = "PatientEncounter_sequence", sequenceName = "PatientEncounter_sequence")
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
	@ForeignKey(name = "FK_PatientEncounter_sdloc")
	@Index(name = "IX_PatientEncounter_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
	/**
	*  javadoc for nortonScore
	*/
	private Integer nortonScore;

	@Column(name="norton_score")
	public Integer getNortonScore(){
		return nortonScore;
	}

	public void setNortonScore(Integer nortonScore){
		this.nortonScore = nortonScore;
	}
	
	/**
	*  javadoc for bradenScore
	*/
	private Integer bradenScore;

	@Column(name="braden_score")
	public Integer getBradenScore(){
		return bradenScore;
	}

	public void setBradenScore(Integer bradenScore){
		this.bradenScore = bradenScore;
	}
	
	/**
	*  javadoc for brassScore
	*/
	private Integer brassScore;

	@Column(name="brass_score")
	public Integer getBrassScore(){
		return brassScore;
	}

	public void setBrassScore(Integer brassScore){
		this.brassScore = brassScore;
	}
	
	/**
	*  javadoc for pain
	*/
	private Double pain;

	@Column(name="pain")
	public Double getPain(){
		return pain;
	}

	public void setPain(Double pain){
		this.pain = pain;
	}
	
	/**
	 * Set = true in ActivityPrescriber (FlamingoClient) if a specific nanda diagnosis is created (true) or deleted (false)
	 */
	private boolean fallRisk = false;

	@Column(name="fall_risk")
	public boolean getFallRisk(){
		return fallRisk;
	}

	public void setFallRisk(boolean fallRisk){
		this.fallRisk = fallRisk;
	}

	private IIEmbeddable visitNumber;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "root", 		column = @Column(name = "visit_number_root")),
		@AttributeOverride(name = "extension", 	column = @Column(name = "visit_number_extension")) })
	public IIEmbeddable getVisitNumber() {
		return visitNumber;
	}

	public void setVisitNumber(IIEmbeddable visitNumber) {
		this.visitNumber = visitNumber;
	}	
	
	/**
	 * external id for the EmergencyEncounter
	 */
	private IIEmbeddable emergencyEncounterId;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "root", 		column = @Column(name = "emergency_enc_id_root")),
		@AttributeOverride(name = "extension", 	column = @Column(name = "emergency_enc_id_extension")) })
	public IIEmbeddable getEmergencyEncounterId() {
		return emergencyEncounterId;
	}

	public void setEmergencyEncounterId(IIEmbeddable emergencyEncounterId) {
		this.emergencyEncounterId = emergencyEncounterId;
	}

	private IIEmbeddable preadmitNumber;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "root", 		column = @Column(name = "preadmit_number_root")),
		@AttributeOverride(name = "extension", 	column = @Column(name = "preadmit_number_extension")) })
	public IIEmbeddable getPreadmitNumber() {
		return preadmitNumber;
	}

	public void setPreadmitNumber(IIEmbeddable preadmitNumber) {
		this.preadmitNumber = preadmitNumber;
	}	

	private CodeValue admissionReferralSourceCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="admission_referral_source_code")
	@ForeignKey(name="FK_PatEnc_admRSC")
	@Index(name="IX_PatEnc_admRSC")
	public CodeValue getAdmissionReferralSourceCode() {
		return admissionReferralSourceCode;
	}

	public void setAdmissionReferralSourceCode(CodeValue admissionReferralSourceCode) {
		this.admissionReferralSourceCode = admissionReferralSourceCode;
	}

	private PQ lengthOfStayQuantity;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="value", column=@Column(name="lengthOfStayQuantity_value")),
		@AttributeOverride(name="unit", column=@Column(name="lengthOfStayQuantity_unit"))
	})
	public PQ getLengthOfStayQuantity() {
		return lengthOfStayQuantity;
	}

	public void setLengthOfStayQuantity(PQ lengthOfStayQuantity) {
		this.lengthOfStayQuantity = lengthOfStayQuantity;
	}

	private CodeValue dischargeDispositionCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="discharge_disposition_code")
	@ForeignKey(name="FK_PatEnc_dischargeD")
	@Index(name="IX_PatEnc_dischargeD")
	public CodeValue getDischargeDispositionCode() {
		return dischargeDispositionCode;
	}

	public void setDischargeDispositionCode(CodeValue dischargeDispositionCode) {
		this.dischargeDispositionCode = dischargeDispositionCode;
	}

	private Boolean preAdmitTestInd;

	@Column(name="pre_admit_test_ind")
	public Boolean getPreAdmitTestInd() {
		return preAdmitTestInd;
	}

	public void setPreAdmitTestInd(Boolean preAdmitTestInd) {
		this.preAdmitTestInd = preAdmitTestInd;
	}

	private Set<CodeValue> specialCourtesiesCode;

	@ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinTable(name="patenc_courtesies_code",
	joinColumns = { @JoinColumn(name="patientencounter_id", nullable=false, updatable=false) }, 
	inverseJoinColumns = { @JoinColumn(name="special_courtesies_code", nullable=false, updatable=false) })
	@ForeignKey(name="FK_specialCourtesiesCode_pe", inverseName="FK_specialCourtesiesCode_cv") //FIXME: accorciato FK_specialCourtesiesCode_patientEncounter
	public Set<CodeValue> getSpecialCourtesiesCode() {
		return specialCourtesiesCode;
	}

	public void setSpecialCourtesiesCode(Set<CodeValue> specialCourtesiesCode) {
		this.specialCourtesiesCode = specialCourtesiesCode;
	}

	private Set<CodeValue> specialArrangementCode;

	@ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinTable(name="patenc_arrangement_code", 
	joinColumns = { @JoinColumn(name="patientencounter_id", nullable=false, updatable=false) }, 
	inverseJoinColumns = { @JoinColumn(name="special_arrangement_code", nullable=false, updatable=false) })
	@ForeignKey(name="FK_specialArrangementCode_pe", inverseName="FK_specialArrangementCode_cv") //FIXME: accorciato FK_specialArrangementCode_patientEncounter
	public Set<CodeValue> getSpecialArrangementCode() {
		return specialArrangementCode;
	}

	public void setSpecialArrangementCode(Set<CodeValue> specialArrangementCode) {
		this.specialArrangementCode = specialArrangementCode;
	}

	private CodeValue acuityLevelCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="acuity_level_code")
	@ForeignKey(name="FK_PatEnc_acuLevel")
	@Index(name="IX_PatEnc_acuLevel")
	public CodeValue getAcuityLevelCode() {
		return acuityLevelCode;
	}

	public void setAcuityLevelCode(CodeValue acuityLevelCode) {
		this.acuityLevelCode = acuityLevelCode;
	}

	// SHORTCUTS
	private Patient patient;

	//@JsonBackReference(value="patientEncounter")
	@ManyToOne(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_PatEnc_Patient")
	@Index(name="IX_PatEnc_Patient")
	//@NotAudited
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}



	private ServiceDeliveryLocation assignedSDL;

	private ServiceDeliveryLocation scheduledSDL;

	private ServiceDeliveryLocation temporarySDL;

//	private List<Annotation> annotation;



	private List<EncounterProcedure> procedure;
	private List<VitalSign> vitalSign;
//	private List<Observation> observation; 
	private List<MedicalHistory> anamnesis;
	private List<ObjectiveExam> objectiveExam;
	private List<AssessmentScale> assessmentScale;
	private List<Nanda> nanda;


	/**
	*  javadoc for appointment
	*/
	private List<Appointment> appointment;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="patientEncounter", cascade=CascadeType.PERSIST)
	public List<Appointment> getAppointment(){
		return appointment;
	}

	public void setAppointment(List<Appointment> list){
		appointment = list;
	}

	public void addAppointment(Appointment appointment) {
		if (this.appointment == null) {
			this.appointment = new ArrayList<Appointment>();
		}
		// add the association
		if(!this.appointment.contains(appointment)) {
			this.appointment.add(appointment);
		}
		// make the inverse link
		appointment.setPatientEncounter(this);
	}

	public void removeAppointment(Appointment appointment) {
		if (this.appointment == null) {
			this.appointment = new ArrayList<Appointment>();
			return;
		}
		//add the association
		if(this.appointment.contains(appointment)){
			this.appointment.remove(appointment);
		}
		//make the inverse link
		appointment.setPatientEncounter(null);
	}
	
	// SHORTCUTS END

	/**
	*  javadoc for procedure
	*  PHI KLINIK: procedure executed during encounter
	*/
	private List<Procedure> dischargeProcedure;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="patientEncounter", cascade=CascadeType.PERSIST)
	public List<Procedure> getDischargeProcedure() {
		return dischargeProcedure;
	}

	public void setDischargeProcedure(List<Procedure>list){
		dischargeProcedure = list;
	}

	public void addDischargeProcedure(Procedure procedure) {
		if (this.dischargeProcedure == null) {
			this.dischargeProcedure = new ArrayList<Procedure>();
		}
		// add the association
		if(!this.dischargeProcedure.contains(procedure)) {
			this.dischargeProcedure.add(procedure);
			// make the inverse link
			procedure.setPatientEncounter(this);
		}
	}

	public void removeDischargeProcedure(Procedure procedure) {
		if (this.dischargeProcedure == null) {
			this.dischargeProcedure = new ArrayList<Procedure>();
			return;
		}
		//add the association
		if(this.dischargeProcedure.contains(procedure)){
			this.dischargeProcedure.remove(procedure);
			//make the inverse link
			procedure.setPatientEncounter(null);
		}
	}




	private List<Therapy> therapy;

	//@JsonManagedReference(value="therapy")
	//@JsonBackReference
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
	public List<Therapy> getTherapy() {
		return therapy;
	}

	public void setTherapy(List<Therapy> param) {
		this.therapy = param;
	}
	
	 public void addTherapy(Therapy therapy) {
		 if (this.therapy == null) {
			 this.therapy = new ArrayList<Therapy>();
		 }
		 // add the association
		 if(!this.therapy.contains(therapy)) {
			 this.therapy.add(therapy);
			 // make the inverse link
			 therapy.setPatientEncounter(this);
		 }
	 }

	 public void removeTherapy(Therapy therapy) {
		 if (this.therapy == null) {
			 this.therapy = new ArrayList<Therapy>();
			 return;
		 }
		 //add the association
		 if(this.therapy.contains(therapy)){
			 this.therapy.remove(therapy);
			 //make the inverse link
			 therapy.setPatientEncounter(null);
		 }
	 }

	 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="assignedSDL")
	@ForeignKey(name="FK_Pat_Enc_AssignedSDL")
	@Index(name="IX_Pat_Enc_AssignedSDL")
	public ServiceDeliveryLocation getAssignedSDL() {
		return assignedSDL;
	}

	public void setAssignedSDL(ServiceDeliveryLocation param) {
		this.assignedSDL = param;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="scheduledSDL")
	@ForeignKey(name="FK_Pat_Enc_SchedSDL")
	@Index(name="IX_Pat_Enc_SchedSDL")
	public ServiceDeliveryLocation getScheduledSDL() {
		return scheduledSDL;
	}

	public void setScheduledSDL(ServiceDeliveryLocation param) {
		this.scheduledSDL = param;
	}

	private ServiceDeliveryLocation bed;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bed")
	@ForeignKey(name="FK_Pat_Enc_Bed")
	@Index(name="IX_Pat_Enc_Bed")
	public ServiceDeliveryLocation getBed() {
	    return bed;
	}

	public void setBed(ServiceDeliveryLocation bed) {
	    this.bed = bed;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="temporarySDL")
	@ForeignKey(name="FK_Pat_Enc_TempSDL")
	@Index(name="IX_Pat_Enc_TempSDL")
	public ServiceDeliveryLocation getTemporarySDL() {
		return temporarySDL;
	}

	public void setTemporarySDL(ServiceDeliveryLocation param) {
		this.temporarySDL = param;
	}

	/**
	 *  javadoc for admitter
	 */
	private Employee admitter;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="admitter_id")
	@ForeignKey(name="FK_Pat_Enc_admitter")
	@Index(name="IX_Pat_Enc_admitter")
	public Employee getAdmitter(){
		return admitter;
	}

	public void setAdmitter(Employee admitter){
		this.admitter = admitter;
	}

//	/**
//	 *  javadoc for admitter
//	 */
//	private EmployeeRole admitterRole;
//
//	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="admitterRole_id")
//	@ForeignKey(name="FK_Pat_Enc_admRole")
//	@Index(name="IX_Pat_Enc_admRole")
//	public EmployeeRole getAdmitterRole(){
//		return admitterRole;
//	}
//
//	public void setAdmitterRole(EmployeeRole admitterRole){
//		this.admitterRole = admitterRole;
//	}

	/**
	 *  javadoc for referrer
	 */
	private Employee referrer;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="referrer_id")
	@ForeignKey(name="FK_Pat_Enc_referrer")
	@Index(name="IX_Pat_Enc_referrer")
	public Employee getReferrer(){
		return referrer;
	}

	public void setReferrer(Employee referrer){
		this.referrer = referrer;
	}

	/**
	 *  javadoc for discharger
	 */
	private Employee discharger;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="discharger_id")
	@ForeignKey(name="FK_Pat_Enc_discharger")
	@Index(name="IX_Pat_Enc_discharger")
	public Employee getDischarger(){
		return discharger;
	}

	public void setDischarger(Employee discharger){
		this.discharger = discharger;
	}

	/**
	 *  javadoc for attender
	 */
	private Employee attender;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="attender_id")
	@ForeignKey(name="FK_Pat_Enc_attender")
	@Index(name="IX_Pat_Enc_attender")
	public Employee getAttender(){
		return attender;
	}

	public void setAttender(Employee attender){
		this.attender = attender;
	}

//	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
//	public List<Annotation> getAnnotation() {
//		return annotation;
//	}
//
//	public void setAnnotation(List<Annotation> param) {
//		this.annotation = param;
//	}

//	private List<Equipe> equipe;
//	//FROM PHI CI
//	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
//	public List<Equipe> getEquipe() {
//		return equipe;
//	}
//
//	public void setEquipe(List<Equipe> param) {
//		this.equipe = param;
//	}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
	public List<EncounterProcedure> getProcedure() {
		return procedure;
	}

	public void setProcedure(List<EncounterProcedure> param) {
		this.procedure = param;
	}
	public void addProcedure(EncounterProcedure param) {
		if (this.procedure == null) {
			this.procedure = new ArrayList<EncounterProcedure>();
		}
		// add the association
		if(!this.procedure.contains(param)) {
			this.procedure.add(param);
			// make the inverse link
			param.setPatientEncounter(this);
		}
	}	


	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
	public List<VitalSign> getVitalSign() {
		return vitalSign;
	}

	public void setVitalSign(List<VitalSign> param) {
		this.vitalSign = param;
	}

	public void addVitalSign(VitalSign vitalSign) {
		if (this.vitalSign == null) {
			this.vitalSign = new ArrayList<VitalSign>();
		}
		// add the association
		if(!this.vitalSign.contains(vitalSign)) {
			this.vitalSign.add(vitalSign);
			// make the inverse link
			vitalSign.setPatientEncounter(this);
		}
	}


//	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
//	public List<Observation> getObservation() {
//		return observation;
//	}
//
//	public void setObservation(List<Observation> observation) {
//		this.observation = observation;
//	}
//	public void addObservation(Observation observation) {
//		if (this.observation == null) {
//			this.observation = new ArrayList<Observation>();
//		}
//		// add the association
//		if(!this.observation.contains(observation)) {
//			this.observation.add(observation);
//			// make the inverse link
//			observation.setPatientEncounter(this);
//		}
//	}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
	public List<MedicalHistory> getAnamnesis() {
		return anamnesis;
	}

	public void setAnamnesis(List<MedicalHistory> param) {
		this.anamnesis = param;
	}


	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
	public List<ObjectiveExam> getObjectiveExam() {
		return objectiveExam;
	}

	public void setObjectiveExam(List<ObjectiveExam> param) {
		this.objectiveExam = param;
	}

	
	/**
	*  javadoc for diagnosis
	*/
	private List<Diagnosis> diagnosis;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="patientEncounter", cascade=CascadeType.PERSIST)
	public List<Diagnosis> getDiagnosis(){
		return diagnosis;
	}

	public void setDiagnosis(List<Diagnosis> list){
		diagnosis = list;
	}

	public void addDiagnosis(Diagnosis diagnosis) {
		if (this.diagnosis == null) {
			this.diagnosis = new ArrayList<Diagnosis>();
		}
		// add the association
		if(!this.diagnosis.contains(diagnosis)) {
			this.diagnosis.add(diagnosis);
			// make the inverse link
			diagnosis.setPatientEncounter(this);
		}
	}

	public void removeDiagnosis(Diagnosis diagnosis) {
		if (this.diagnosis == null) {
			this.diagnosis = new ArrayList<Diagnosis>();
			return;
		}
		//add the association
		if(this.diagnosis.contains(diagnosis)){
			this.diagnosis.remove(diagnosis);
			//make the inverse link
			diagnosis.setPatientEncounter(null);
		}

	}
	
	


	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
	public List<AssessmentScale> getAssessmentScale() {
		return assessmentScale;
	}

	public void setAssessmentScale(List<AssessmentScale> param) {
		this.assessmentScale = param;
	}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, mappedBy = "patientEncounter")
	public List<Nanda> getNanda() {
		return nanda;
	}

	public void setNanda(List<Nanda> param) {
		this.nanda = param;
	}
	
	private Date dismissionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="dismission_date")
	public Date getDismissionDate() {
		return dismissionDate;
	}

	public void setDismissionDate(Date dismissionDate) {
		this.dismissionDate = dismissionDate;
	}

	private Date convocationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="convocation_date")
	public Date getConvocationDate() {
		return convocationDate;
	}

	public void setConvocationDate(Date convocationDate) {
		this.convocationDate = convocationDate;
	}

	private Date assignment;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="assignment_date")
	public Date getAssignment() {
		return assignment;
	}

	public void setAssignment(Date assignment) {
		this.assignment = assignment;
	}

//	private Discharge discharge;
//	//FORM PHI CI
//	@OneToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="discharge")
//	@ForeignKey(name="FK_Pat_Enc_discharge")
//	@Index(name="IX_Pat_Enc_discharge")
//	public Discharge getDischarge() {
//		return discharge;
//	}
//
//	public void setDischarge(Discharge discharge) {
//		this.discharge = discharge;
//	}

    //StatementId used for nosographic number for PHI_KLINIK
	private String statementId;
	private CodeValue colorCode;
	private CodeValue mainProblemCode; 
	private CodeValue traumaTypeCode;
	private CodeValue arrivalModalityCode;
	private CodeValue deambulationCode;
	private CodeValue isConfidentialCode;

	//tree free input text, where operator insert some code coming from papers received during encounter
	//they refers to operation center where the patient comes, the emergency mission code provided by 
	//people which are taking hte patient, and an access code received by them.
	private String operationCenterCode;
	private String missionCode;
	private String accessCode;

	//is a coded created by a combination of color code and a sequence number.
	//e.g. red_1, green_1, yellow_4. 
	//the sequence number must be reset to 0 every day at midnight.
	private String callCode;

	//during the encounter close, is the institution code where the patient is moved to.
	//it must be free text because it could be an institute not present in the list of serviceDeliveryLocation.
	private String transferInstituteCode;

	//used for administrative generic notes
	private String details;

	private String clinicalSuspect;


	@Column(name="statement_id")
	public String getStatementId() {
		return statementId;
	}

	public void setStatementId(String statementId) {
		this.statementId = statementId;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="color_code")
	@ForeignKey(name="FK_EmeEnc_colorc_cv")
	@Index(name="IX_EmeEnc_colorc_cv")
	public CodeValue getColorCode() {
		return colorCode;
	}

	public void setColorCode(CodeValue colorCode) {
		this.colorCode = colorCode;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="main_problem_code")
	@ForeignKey(name="FK_EmeEnc_main_prob_cv")
	@Index(name="IX_EmeEnc_main_prob_cv")
	public CodeValue getMainProblemCode() {
		return mainProblemCode;
	}

	public void setMainProblemCode(CodeValue mainProlem) {
		this.mainProblemCode = mainProlem;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="trauma_type_code")
	@ForeignKey(name="FK_EmeEnc_traumaType")
	@Index(name="IX_EmeEnc_traumaType")
	public CodeValue getTraumaTypeCode() {
		return traumaTypeCode;
	}

	public void setTraumaTypeCode(CodeValue traumaTypeCode) {
		this.traumaTypeCode = traumaTypeCode;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="arrival_modality_code")
	@ForeignKey(name="FK_EmeEnc_arrMod_cv")
	@Index(name="IX_EmeEnc_arrMod_cv")
	public CodeValue getArrivalModalityCode() {
		return arrivalModalityCode;
	}

	public void setArrivalModalityCode(CodeValue arrivalModalityCode) {
		this.arrivalModalityCode = arrivalModalityCode;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="deambulation_code")
	@ForeignKey(name="FK_EmeEnc_deamb_cv")
	@Index(name="IX_EmeEnc_deamb_cv")
	public CodeValue getDeambulationCode() {
		return deambulationCode;
	}

	public void setDeambulationCode(
			CodeValue deambulationCode) {
		this.deambulationCode = deambulationCode;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="is_confidential_code")
	@ForeignKey(name="FK_EmeEnc_is_conf_cv")
	@Index(name="IX_EmeEnc_is_conf_cv")
	public CodeValue getIsConfidentialCode() {
		return isConfidentialCode;
	}

	public void setIsConfidentialCode(CodeValue isConfidentialCode) {
		this.isConfidentialCode = isConfidentialCode;
	}

	@Column(name="operation_center_code")
	public String getOperationCenterCode() {
		return operationCenterCode;
	}

	public void setOperationCenterCode(String operationCenterCode) {
		this.operationCenterCode = operationCenterCode;
	}

	@Column(name="mission_code")
	public String getMissionCode() {
		return missionCode;
	}

	public void setMissionCode(String missionCode) {
		this.missionCode = missionCode;
	}

	@Column(name="access_code")
	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	@Column(name="call_code")
	public String getCallCode() {
		return callCode;
	}

	public void setCallCode(String callCode) {
		this.callCode = callCode;
	}

	@Column(name="transfer_institute_code")
	public String getTransferInstituteCode() {
		return transferInstituteCode;
	}

	public void setTransferInstituteCode(String transferInstituteCode) {
		this.transferInstituteCode = transferInstituteCode;
	}

	@Column(name="details")
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	@Column(name="clinical_suspect")
	public String getClinicalSuspect() {
		return clinicalSuspect;
	}

	public void setClinicalSuspect(String clinicalSuspect) {
		this.clinicalSuspect = clinicalSuspect;
	}

	/**
	 *  G2 EPIS is used to save the EPIS0 of LISA IM DB.
	 *  EPIS0 invece è la PK degli episodi del clinico ( gar_episodi.epis0) e questa a sua volta puo' essere legata ad un episodio SIO
	 */
	private String g2Epis;

	@Column(name="g2_epis", unique=true)
	public String getG2Epis(){
		return g2Epis;
	}

	public void setG2Epis(String g2Epis){
		this.g2Epis = g2Epis;
	}

	/**
	 *  G2 EPSD is used to save the EPSD0 of LISA IM DB
	 *  EPSD0 è la PK degli episodio SIO ( de_episodi_sio.epsd0) .
	 */
	private String g2Epsd;

	@Column(name="g2_epsd", unique=true)
	public String getG2Epsd(){
		return g2Epsd;
	}

	public void setG2Epsd(String g2Epsd){
		this.g2Epsd = g2Epsd;
	}

	/**
	 *  G2 RICO is used to save the RICO0 of LISA IM DB
	 *  RICO0 è la PK di adtricovero.
	 */
	private String g2Rico;

	@Column(name="g2_rico", unique=true)
	public String getG2Rico(){
		return g2Rico;
	}

	public void setG2Rico(String g2Rico){
		this.g2Rico = g2Rico;
	}

	/**
	 *  G2 ANNOSDO LISA IM DB
	 */
	private String g2Aasdo;

	@Column(name="g2_aasdo", unique=true)
	public String getG2Aasdo(){
		return g2Aasdo;
	}

	public void setG2Aasdo(String g2Aasdo){
		this.g2Aasdo = g2Aasdo;
	}
	/**
	 *  G2 ANNOSDO LISA IM DB
	 */
	private String g2CodSdo;

	@Column(name="g2_codsdo", unique=true)
	public String getG2CodSdo(){
		return g2CodSdo;
	}

	public void setG2CodSdo(String g2CodSdo){
		this.g2CodSdo = g2CodSdo;
	}

	/**
	 *  javadoc patient Class HL7 PV1.2
	 */	
	private CodeValue patientClass;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="patient_class")
	@ForeignKey(name="FK_EmeEnc_pat_class")
	@Index(name="IX_EmeEnc_pat_class")	
	public CodeValue getPatientClass() {
		return patientClass;
	}

	public void setPatientClass(CodeValue patientClass) {
		this.patientClass = patientClass;
	}

	private String surgeryDrainName;

	@Column(name="surgery_drain_name")
	public String getSurgeryDrainName() {
		return surgeryDrainName;
	}

	public void setSurgeryDrainName(String surgeryDrainName) {
		this.surgeryDrainName = surgeryDrainName;
	}
	
	private String barcode;
	
	@Column(name="barcode")
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	/**
	 *  tpCode stores info on the clinical pathway linked to the PatientEncounter
	 *  
	 */
	private CodeValue tpCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="tp_code")
	@ForeignKey(name="FK_PatEnc_tp")
	@Index(name="IX_PatEnc_tp")
	public CodeValue getTpCode() {
		return tpCode;
	}

	public void setTpCode(CodeValue tpCode) {
		this.tpCode = tpCode;
	}

	
	private String roomString;
	
	@Column(name="room_string")
	public String getRoomString() {
		return roomString;
	}

	public void setRoomString(String roomString) {
		this.roomString = roomString;
	}
	
	private String bedString;
	
	@Column(name="bed_string")
	public String getBedString() {
		return bedString;
	}

	public void setBedString(String bedString) {
		this.bedString = bedString;
	}
	
	//From Act
	
	private Date availabilityTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="availability_time")
	@Index(name="IX_Pat_Enc_avail_time")
	public Date getAvailabilityTime() {
		return availabilityTime;
	}

	public void setAvailabilityTime(Date availabilityTime) {
		this.availabilityTime = availabilityTime;
	}
	
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Pat_Enc_Code")
	@Index(name="IX_Pat_Enc_Code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private Set<II> id;

	@OneToMany(targetEntity=II4PatientEncounter.class, fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="pat_enc_id")
	@ForeignKey(name="FK_Pat_Enc_id")
	@Index(name="IX_Pat_Enc_id")
	@AuditJoinTable(name="z_pat_enc_id_jt")
	public Set<II> getId() {
		return id;
	}

	public void setId(Set<II> id) {
		this.id = id;
	}

	public II getId(String root) {
		if (getId() != null) {
			for (II ii : getId()) {
				if (ii.getRoot().toString().equals(root))
					return ii;
			}
		}
		return null;
	}

	public void addId(II id) {
		if (id == null)
			return;
		
		if (this.id == null) {
			this.id = new HashSet<II>();
		}
		
		this.id.add(id);
	}
	
	/**
	 * Main status
	*/
	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="status_code")
	@ForeignKey(name="FK_Pat_Enc_statusCode")
	@Index(name="IX_Pat_Enc_statusCode")
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}
	
//	PS STATUS
//	/**
//	*  Sub status: 
//	*  In attesa = false
//	*  In carico = true
//	*/
//	private Boolean inCharge;
//
//	@Column(name="in_charge")
//	public Boolean getInCharge(){
//		return inCharge;
//	}
//
//	public void setInCharge(Boolean inCharge){
//		this.inCharge = inCharge;
//	}
//	
//	/**
//	*  Sub status: 
//	*  Non In Osservazione = false
//	*  In Osservazione = true
//	*/
//	private Boolean underObservation;
//
//	@Column(name="under_observation")
//	public Boolean getUnderObservation(){
//		return underObservation;
//	}
//
//	public void setUnderObservation(Boolean underObservation){
//		this.underObservation = underObservation;
//	}
//	
//	/**
//	*  Sub status: 
//	*  No Accertamento/Consulenza = false
//	*  Accertamento/Consulenza in corso = true
//	*/
//	private Boolean consultancy;
//
//	@Column(name="consultancy")
//	public Boolean getConsultancy(){
//		return consultancy;
//	}
//
//	public void setConsultancy(Boolean consultancy){
//		this.consultancy = consultancy;
//	}
//	
//	/**
//	*  Sub status: 
//	*  Presente = false
//	*  In dimissione temporanea = true
//	*/
//	private Boolean temporaryDischarge;
//
//	@Column(name="temporary_discharge")
//	public Boolean getTemporaryDischarge(){
//		return temporaryDischarge;
//	}
//
//	public void setTemporaryDischarge(Boolean temporaryDischarge){
//		this.temporaryDischarge = temporaryDischarge;
//	}
//	
//	/**
//	*  Type of the encounter
//	*/
//	private CodeValuePhi typeCode;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="typeCode")
//	@ForeignKey(name="FK_PatientEncounter_typeCode")
//	@Index(name="IX_PatientEncounter_typeCode")
//	public CodeValuePhi getTypeCode(){
//		return typeCode;
//	}
//
//	public void setTypeCode(CodeValuePhi typeCode){
//		this.typeCode = typeCode;
//	}
//	
//	
	private ED text;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="text_mediaType"))
	@AttributeOverrides({
	       @AttributeOverride(name="string", column=@Column(name="text_string", length=2500)),
	       @AttributeOverride(name="bytes", column=@Column(name="text_bytes"))
	})
	public ED getText() {
		return text;
	}

	public void setText(ED text) {
		this.text = text;
	}
	
	/**
	*  regime di erogazione
	*/
	private CodeValuePhi paymentAgreementCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="paymentAgreementCode")
	@ForeignKey(name="FK_PatEnc_paymentAgreementCode")
	@Index(name="IX_PatEnc_paymentAgreementCode")
	public CodeValuePhi getPaymentAgreementCode(){
		return paymentAgreementCode;
	}

	public void setPaymentAgreementCode(CodeValuePhi paymentAgreementCode){
		this.paymentAgreementCode = paymentAgreementCode;
	}
	
	/**
	*  numero impegnativa riccetta rossa italiana
	*/
	private String numeroImpegnativa;

	@Column(name="numero_impegnativa")
	public String getNumeroImpegnativa(){
		return numeroImpegnativa;
	}

	public void setNumeroImpegnativa(String numeroImpegnativa){
		this.numeroImpegnativa = numeroImpegnativa;
	}
	
	/**
	*  priorita della ricetta
	*/
	private CodeValuePhi priorityCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="priorityCode")
	@ForeignKey(name="FK_PatEnc_priorityCode")
	@Index(name="IX_PatEnc_priorityCode")
	public CodeValuePhi getPriorityCode(){
		return priorityCode;
	}

	public void setPriorityCode(CodeValuePhi priorityCode){
		this.priorityCode = priorityCode;
	}
	
	/**
	*  suggerita si/no della ricetta
	*/
	private Boolean suggested;

	@Column(name="suggested")
	public Boolean getSuggested(){
		return suggested;
	}

	public void setSuggested(Boolean suggested){
		this.suggested = suggested;
	}
	
	/**
	*  codice esenzione riportato in ricetta
	*/
	private CodeValueExemption exemptionCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="exemptionCode")
	@ForeignKey(name="FK_PatEnc_exemptionCode")
	@Index(name="IX_PatEnc_exemptionCode")
	public CodeValueExemption getExemptionCode(){
		return exemptionCode;
	}

	public void setExemptionCode(CodeValueExemption exemptionCode){
		this.exemptionCode = exemptionCode;
	}
	
	/**
	*  sospetto diagnostico in ricetta
	*/
	private CodeValueIcd9 suspectedDiagnosis;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="suspectedDiagnosis")
	@ForeignKey(name="FK_PatEnc_suspectedDiagnosis")
	@Index(name="IX_PatEnc_suspectedDiagnosis")
	public CodeValueIcd9 getSuspectedDiagnosis(){
		return suspectedDiagnosis;
	}

	public void setSuspectedDiagnosis(CodeValueIcd9 suspectedDiagnosis){
		this.suspectedDiagnosis = suspectedDiagnosis;
	}
	
	/**
	*  motivo del ricovero
	*/
	private CodeValuePhi reasonCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="reasonCode")
	@ForeignKey(name="FK_PatEnc_reasonCode")
	@Index(name="IX_PatEnc_reasonCode")
	public CodeValuePhi getReasonCode(){
		return reasonCode;
	}

	public void setReasonCode(CodeValuePhi reasonCode){
		this.reasonCode = reasonCode;
	}
	
	/**
	*  specialità del ricovero
	*/
	private CodeValuePhi specialityCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="specialityCode")
	@ForeignKey(name="FK_PatEnc_specialityCode")
	@Index(name="IX_PatEnc_specialityCode")
	public CodeValuePhi getSpecialityCode(){
		return specialityCode;
	}

	public void setSpecialityCode(CodeValuePhi specialityCode){
		this.specialityCode = specialityCode;
	}
	
	/**
	*  codice PAC
	*/
	private CodeValueIcd9 PACCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PACCode")
	@ForeignKey(name="FK_PatEnc_PACCode")
	@Index(name="IX_PatEnc_PACCode")
	public CodeValueIcd9 getPACCode(){
		return PACCode;
	}

	public void setPACCode(CodeValueIcd9 PACCode){
		this.PACCode = PACCode;
	}
	
	/**
	*  codice di provenienza
	*/
	private CodeValuePhi originCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="originCode")
	@ForeignKey(name="FK_PatEnc_originCode")
	@Index(name="IX_PatEnc_originCode")
	public CodeValuePhi getOriginCode(){
		return originCode;
	}

	public void setOriginCode(CodeValuePhi originCode){
		this.originCode = originCode;
	}
	
	/**
	*  classe ASA
	*/
	private CodeValuePhi classeASA;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="classeASA")
	@ForeignKey(name="FK_PatEnc_classeASA")
	@Index(name="IX_PatEnc_classeASA")
	public CodeValuePhi getClasseASA(){
		return classeASA;
	}

	public void setClasseASA(CodeValuePhi classeASA){
		this.classeASA = classeASA;
	}
	
	/**
	*  causa esterna
	*/
	private CodeValuePhi externalCause;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="externalCause")
	@ForeignKey(name="FK_PatEnc_externalCause")
	@Index(name="IX_PatEnc_externalCause")
	public CodeValuePhi getExternalCause(){
		return externalCause;
	}

	public void setExternalCause(CodeValuePhi externalCause){
		this.externalCause = externalCause;
	}
	
	/**
	 *  Redundant attribute from DischargeData.java in order to simplify ADT Query
	 */
	private Date dischargeDate;
	
	@Column(name="discharge_date")
	public Date getDischargeDate(){
		return dischargeDate;
	}

	public void setDischargeDate(Date dischargeDate){
		this.dischargeDate = dischargeDate;
	}
	
	/**
	 *  Redundant attribute from DischargeData.java in order to simplify ADT Query
	 */
	private Boolean optionTPN;
	
	@Column(name="optionTPN")
	public Boolean getOptionTPN() {
		return optionTPN;
	}
	public void setOptionTPN(Boolean optionTPN) {
		this.optionTPN = optionTPN;
	}
	
	/**
	 *  Redundant attribute from DischargeData.java in order to simplify ADT Query
	 */
	private String complexity;
	
	@Column(name="complexity")
	public String getComplexity() {
		return complexity;
	}

	public void setComplexity(String complexity) {
		this.complexity = complexity;
	}
	
	/**
	 *  Redundant attribute from ClinicalDiary.java in order to simplify ADT Query
	 */
	private Date lastClinicalDiary;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_clinical_diary")
	public Date getLastClinicalDiary() {
		return lastClinicalDiary;
	}

	public void setLastClinicalDiary(Date lastClinicalDiary) {
		this.lastClinicalDiary = lastClinicalDiary;
	}
	
	/**
	 *  Redundant attribute from Checking.java in order to simplify ADT Query
	 */
	private Date lastChecking;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_checking")
	public Date getLastChecking() {
		return lastChecking;
	}

	public void setLastChecking(Date lastChecking) {
		this.lastChecking = lastChecking;
	}
	//FIXME RINOMINA onereDegenza e responsabileInvio 
	/**
	*  onere degenza
	*/
	private CodeValuePhi onereDegenza;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="onereDegenza")
	@ForeignKey(name="FK_PatEnc_onereDegenza")
	@Index(name="IX_PatEnc_onereDegenza")
	public CodeValuePhi getOnereDegenza(){
		return onereDegenza;
	}

	public void setOnereDegenza(CodeValuePhi onereDegenza){
		this.onereDegenza = onereDegenza;
	}
	
	/**
	*  responsabile invio
	*/
	private CodeValuePhi responsabileInvio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="responsabileInvio")
	@ForeignKey(name="FK_PatEnc_responsabileInvio")
	@Index(name="IX_PatEnc_responsabileInvio")
	public CodeValuePhi getResponsabileInvio(){
		return responsabileInvio;
	}

	public void setResponsabileInvio(CodeValuePhi responsabileInvio){
		this.responsabileInvio = responsabileInvio;
	}
	
	private List<CodeValueIcd9> plannedProcedures;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "planned_procedures", joinColumns = { @JoinColumn(name = "pat_enc_id") }, inverseJoinColumns = { @JoinColumn(name = "planned_proc_code") })
	@ForeignKey(name = "FK_planned_proc", inverseName = "FK_planned_proc_back")
	public List<CodeValueIcd9> getPlannedProcedures() {
		return plannedProcedures;
	}
	public void setPlannedProcedures(List<CodeValueIcd9> plannedProcedures) {
		this.plannedProcedures = plannedProcedures;
	}
		
	/**
	*  Intervento
	*/
	private CodeValueIcd9 intervention;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="intervention")
	@ForeignKey(name="FK_PatEnc_intervention")
	@Index(name="IX_PatEnc_intervention")
	public CodeValueIcd9 getIntervention(){
		return intervention;
	}

	public void setIntervention(CodeValueIcd9 intervention){
		this.intervention = intervention;
	}
	
	
	// methods needed for TracedEntity implementation
	/**
	 * javadoc for createdByLocation
	 */
	private String createdByLocation;

	@Column(name = "created_by_location")
	public String getCreatedByLocation() {
		return createdByLocation;
	}

	public void setCreatedByLocation(String createdByLocation) {
		this.createdByLocation = createdByLocation;
	}

	/**
	 * javadoc for modifiedBy
	 */
	private String modifiedBy;

	@Column(name = "modified_by")
	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * javadoc for modificationDate
	 */
	private Date modificationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modification_date")
	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * javadoc for modifiedByLocation
	 */
	private String modifiedByLocation;

	@Column(name = "modified_by_location")
	public String getModifiedByLocation() {
		return modifiedByLocation;
	}

	public void setModifiedByLocation(String modifiedByLocation) {
		this.modifiedByLocation = modifiedByLocation;
	}
	
	
	/**
	 * Status of linked NurseReport
	*/
	private CodeValuePhi nurseReportCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="nurse_report_code")
	@ForeignKey(name="FK_Pat_Enc_nurseRepCode")
	@Index(name="IX_Pat_Enc_nurseRepCode")
	public CodeValuePhi getNurseReportCode() {
		return nurseReportCode;
	}

	public void setNurseReportCode(CodeValuePhi nurseReportCode) {
		this.nurseReportCode =nurseReportCode;
	}
}
