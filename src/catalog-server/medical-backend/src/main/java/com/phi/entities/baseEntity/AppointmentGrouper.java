package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.phi.entities.act.VitalSign;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.ED;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;

@javax.persistence.Entity
@Table(name = "appointment_grouper")
@org.hibernate.annotations.Table(appliesTo = "appointment_grouper", indexes = { @Index(name="IX_APPOINTMENT_GROUPER", columnNames = { "availability_time", "serviceDeliveryLocation", "status_code" } ) })
@Audited
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="internalId")
/**
 *  When patient is admitted, his nominative gets in a waitling list, from that 
 *  moment an {@link AppointmentGrouper} is created.
 *  New appointments are added to the AppointmentGrouper already created until they are executed.
 * @author francesco.rossi
 *
 */
public class AppointmentGrouper extends BaseEntity implements LocatedEntity {

	private static final long serialVersionUID = 586478367L;
	
	/**
	*  javadoc for school
	*/
	private String school;

	@Column(name="school")
	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}
	/**
	*  javadoc for socialServ
	*/
	private CodeValuePhi socialServ;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="socialServ")
	@ForeignKey(name="socialServ")
	@Index(name="IX_AppGrop_socialServ")
	public CodeValuePhi getSocialServ(){
		return socialServ;
	}

	public void setSocialServ(CodeValuePhi socialServ){
		this.socialServ = socialServ;
	}
	
	/**
	*  javadoc for relyOn
	*/
	private CodeValuePhi relyOn;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="relyOn")
	@ForeignKey(name="FK_AppGroup_relyOn")
	@Index(name="IX_AppGrop_relyOn")
	public CodeValuePhi getRelyOn(){
		return relyOn;
	}

	public void setRelyOn(CodeValuePhi relyOn){
		this.relyOn = relyOn;
	}


	/**
	 *  javadoc for statusFam
	 */
	private CodeValuePhi statusFam;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="statusFam")
	@ForeignKey(name="FK_AppGroup_statusFam")
	@Index(name="IX_AppGrop_statusFam")
	public CodeValuePhi getStatusFam(){
		return statusFam;
	}

	public void setStatusFam(CodeValuePhi statusFam){
		this.statusFam = statusFam;
	}

	/**
	 *  javadoc for procedureRequests
	 */
	private List<ProcedureRequest> procedureRequests;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="appointmentGrouper", cascade=CascadeType.PERSIST)
	public List<ProcedureRequest> getProcedureRequests() {
		return procedureRequests;
	}

	public void setProcedureRequests(List<ProcedureRequest>list){
		procedureRequests = list;
	}

	public void addProcedureRequests(ProcedureRequest procedureRequests) {
		if (this.procedureRequests == null) {
			this.procedureRequests = new ArrayList<ProcedureRequest>();
		}
		// add the association
		if(!this.procedureRequests.contains(procedureRequests)) {
			this.procedureRequests.add(procedureRequests);
			// make the inverse link
			procedureRequests.setAppointmentGrouper(this);
		}
	}

	public void removeProcedureRequests(ProcedureRequest procedureRequests) {
		if (this.procedureRequests == null) {
			this.procedureRequests = new ArrayList<ProcedureRequest>();
			return;
		}
		//add the association
		if(this.procedureRequests.contains(procedureRequests)){
			this.procedureRequests.remove(procedureRequests);
			//make the inverse link
			procedureRequests.setAppointmentGrouper(null);
		}
	}

	/**
	 *  javadoc for rootAppointment
	 */
	private Appointment rootAppointment;

	@JsonBackReference(value="groupers")
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="root_appointment_id")
	@ForeignKey(name="FK_ppntmentGruper_rtppntmnt")
	@Index(name="IX_ppntmentGruper_rtppntmnt")
	public Appointment getRootAppointment(){
		return rootAppointment;
	}

	public void setRootAppointment(Appointment rootAppointment){
		this.rootAppointment = rootAppointment;
	}

	/**
	 *  javadoc for secondaryDiagnosis
	 */
	private CodeValue secondaryDiagnosis;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="secondaryDiagnosis")
	@ForeignKey(name="FK_AppGroup_scndDiagnosis")
	@Index(name="IX_AppGrop_scndDiagnosis")
	public CodeValue getSecondaryDiagnosis(){
		return secondaryDiagnosis;
	}

	public void setSecondaryDiagnosis(CodeValue secondaryDiagnosis){
		this.secondaryDiagnosis = secondaryDiagnosis;
	}
	//	/**
	//	*  javadoc for internalActivity
	//	*/
	//	private List<InternalActivity> internalActivity;
	//
	//	@OneToMany(fetch=FetchType.LAZY, mappedBy="appointmentGrouper", cascade=CascadeType.PERSIST)
	//	public List<InternalActivity> getInternalActivity(){
	//		return internalActivity;
	//	}
	//
	//	public void setInternalActivity(List<InternalActivity> list){
	//		internalActivity = list;
	//	}
	//
	//	public void addInternalActivity(InternalActivity internalActivity) {
	//		if (this.internalActivity == null) {
	//			this.internalActivity = new ArrayList<InternalActivity>();
	//		}
	//		// add the association
	//		if(!this.internalActivity.contains(internalActivity)) {
	//			this.internalActivity.add(internalActivity);
	//			// make the inverse link
	//			internalActivity.setAppointmentGrouper(this);
	//		}
	//	}
	//
	//	public void removeInternalActivity(InternalActivity internalActivity) {
	//		if (this.internalActivity == null) {
	//			this.internalActivity = new ArrayList<InternalActivity>();
	//			return;
	//		}
	//		//add the association
	//		if(this.internalActivity.contains(internalActivity)){
	//			this.internalActivity.remove(internalActivity);
	//			//make the inverse link
	//			internalActivity.setAppointmentGrouper(null);
	//		}
	//
	//	}


	/**
	 *  javadoc for MedicalHistory
	 */
	private List<MedicalHistory> medicalHistory;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="appointmentGrouper", cascade=CascadeType.PERSIST)
	public List<MedicalHistory> getMedicalHistory(){
		return medicalHistory;
	}

	public void setMedicalHistory(List<MedicalHistory> list){
		medicalHistory = list;
	}

	public void addMedicalHistory(MedicalHistory medicalHistory) {
		if (this.medicalHistory == null) {
			this.medicalHistory = new ArrayList<MedicalHistory>();
		}
		// add the association
		if(!this.medicalHistory.contains(medicalHistory)) {
			this.medicalHistory.add(medicalHistory);
			// make the inverse link
			medicalHistory.setAppointmentGrouper(this);
		}
	}

	public void removeMedicalHistory(MedicalHistory medicalHistory) {
		if (this.medicalHistory == null) {
			this.medicalHistory = new ArrayList<MedicalHistory>();
			return;
		}
		//add the association
		if(this.medicalHistory.contains(medicalHistory)){
			this.medicalHistory.remove(medicalHistory);
			//make the inverse link
			medicalHistory.setAppointmentGrouper(null);
		}

	}


	//ACT FIELDS

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "appointmentGrouper_sequence")
	@SequenceGenerator(name = "appointmentGrouper_sequence", sequenceName = "appointmentGrouper_sequence")
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
	@ForeignKey(name = "FK_appointmentGrouper_sdloc")
	@Index(name="IX_appointmentGrouper_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_appointmentGrouper_sc")
	@Index(name="IX_appointmentGrouper_sc")
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}

	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_appointmentGrouper_c")
	@Index(name="IX_appointmentGrouper_c")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}

	/**
	 *  javadoc for therapyType
	 */
	private CodeValue therapyType;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="therapyType")
	@ForeignKey(name="FK_AppGroup_therapyType")
	@Index(name="IX_AppGroup_therapyType")
	public CodeValue getTherapyType(){
		return therapyType;
	}

	public void setTherapyType(CodeValue therapyType){
		this.therapyType = therapyType;
	}

	private Date availabilityTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="availability_time")
	public Date getAvailabilityTime() {
		return availabilityTime;
	}

	public void setAvailabilityTime(Date availabilityTime) {
		this.availabilityTime = availabilityTime;
	}

	private ED title;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="title_mediaType"))
	//FIXME: @ForeignKey(name="FK_appointmentGrouper_title_mediaType")
	//	@Index(name="IX_appointmentGrouper_title_mediaType")
	@AttributeOverrides({
		@AttributeOverride(name="string", column=@Column(name="title_string", length=2500)),
		@AttributeOverride(name="bytes", column=@Column(name="title_bytes"))
	})
	public ED getTitle() {
		return title;
	}

	public void setTitle(ED title) {
		this.title = title;
	}

	private CodeValue levelCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="level_code")
	@ForeignKey(name="FK_appointmentGrouper_lec")
	@Index(name="IX_appointmentGrouper_lec") 
	public CodeValue getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(CodeValue levelCode) {
		this.levelCode = levelCode;
	}


	private String derivationExpr;

	@Column(name="derivation_expr")
	public String getDerivationExpr() {
		return derivationExpr;
	}

	public void setDerivationExpr(String derivationExpr) {
		this.derivationExpr = derivationExpr;
	}

	private CodeValue uncertaintyCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="uncertainty_code")
	@ForeignKey(name="FK_appointmentGrouper_uc")
	@Index(name="IX_appointmentGrouper_uc")
	public CodeValue getUncertaintyCode() {
		return uncertaintyCode;
	}

	public void setUncertaintyCode(CodeValue uncertaintyCode) {
		this.uncertaintyCode = uncertaintyCode;
	}

	private ED text;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="text_mediaType"))
	//FIXME: @ForeignKey(name="FK_Act_text_mediaType")
	//	@Index(name="IX_Act_text_mediaType")
	@AttributeOverrides({
		@AttributeOverride(name="string", column=@Column(name="text_string", length=4000)),
		@AttributeOverride(name="bytes", column=@Column(name="text_bytes"))
	})
	public ED getText() {
		return text;
	}

	public void setText(ED text) {
		this.text = text;
	}

	private CodeValue languageCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="language_code")
	@ForeignKey(name="FK_appointmentGrouper_lac")
	@Index(name="IX_appointmentGrouper_lac") //FIXME: accorciato FK_Act_languageCode
	public CodeValue getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(CodeValue languageCode) {
		this.languageCode = languageCode;
	}
	//ACT FIELDS END

	/**
	 *  javadoc for vitalSign
	 */
	private List<VitalSign> vitalSign;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="appointmentGrouper", cascade=CascadeType.PERSIST)
	public List<VitalSign> getVitalSign(){
		return vitalSign;
	}

	public void setVitalSign(List<VitalSign> list){
		vitalSign = list;
	}

	public void addVitalSign(VitalSign vitalSign) {
		if (this.vitalSign == null) {
			this.vitalSign = new ArrayList<VitalSign>();
		}
		// add the association
		if(!this.vitalSign.contains(vitalSign)) {
			this.vitalSign.add(vitalSign);
			// make the inverse link
			vitalSign.setAppointmentGrouper(this);
		}
	}

	public void removeVitalSign(VitalSign vitalSign) {
		if (this.vitalSign == null) {
			this.vitalSign = new ArrayList<VitalSign>();
			return;
		}
		//add the association
		if(this.vitalSign.contains(vitalSign)){
			this.vitalSign.remove(vitalSign);
			//make the inverse link
			vitalSign.setAppointmentGrouper(null);
		}

	}


	/**
	 *  javadoc for otherSecondLanguage
	 */
	private String otherSecondLanguage;

	@Column(name="other_second_language")
	public String getOtherSecondLanguage(){
		return otherSecondLanguage;
	}

	public void setOtherSecondLanguage(String otherSecondLanguage){
		this.otherSecondLanguage = otherSecondLanguage;
	}

	/**
	 *  javadoc for otherFirstLanguage
	 */
	private String otherFirstLanguage;

	@Column(name="other_first_language")
	public String getOtherFirstLanguage(){
		return otherFirstLanguage;
	}

	public void setOtherFirstLanguage(String otherFirstLanguage){
		this.otherFirstLanguage = otherFirstLanguage;
	}

	/**
	 *  javadoc for secondLanguage
	 */
	private CodeValue secondLanguage;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="secondLanguage")
	@ForeignKey(name="FK_AppGroup_secLang")
	@Index(name="IX_AppGroup_secLang")
	public CodeValue getSecondLanguage(){
		return secondLanguage;
	}

	public void setSecondLanguage(CodeValue secondLanguage){
		this.secondLanguage = secondLanguage;
	}

	/**
	 *  javadoc for firstLanguage
	 */
	private CodeValue spokenLanguage;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="spokenLanguage")
	@ForeignKey(name="FK_AppGroup_spokLang")
	@Index(name="IX_AppGroup_spokLang")
	public CodeValue getSpokenLanguage(){
		return spokenLanguage;
	}

	public void setSpokenLanguage(CodeValue spokenLanguage){
		this.spokenLanguage = spokenLanguage;
	}

	/**
	 *  javadoc for ageType
	 */
	private CodeValue ageType;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="ageType")
	@ForeignKey(name="FK_AppGroup_age")
	@Index(name="IX_AppGroup_age")
	public CodeValue getAgeType(){
		return ageType;
	}

	public void setAgeType(CodeValue ageType){
		this.ageType = ageType;
	}

	/**
	 *  javadoc for location
	 */
	private CodeValue location;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="location")
	@ForeignKey(name="FK_AppGroup_loc")
	@Index(name="IX_AppGroup_loc")
	public CodeValue getLocation(){
		return location;
	}

	public void setLocation(CodeValue location){
		this.location = location;
	}

	/**
	 *  javadoc for sentBy
	 */
	private CodeValue sentBy;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="sentBy")
	@ForeignKey(name="FK_AppGroup_sentBy")
	@Index(name="IX_AppGroup_sentBy")
	public CodeValue getSentBy(){
		return sentBy;
	}

	public void setSentBy(CodeValue sentBy){
		this.sentBy = sentBy;
	}

	/**
	 *  javadoc for priority
	 */
	private CodeValue priority;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="priority")
	@ForeignKey(name="FK_AppGroup_priority")
	@Index(name="IX_AppGroup_priority")
	public CodeValue getPriority(){
		return priority;
	}

	public void setPriority(CodeValue priority){
		this.priority = priority;
	}

	/**
	 *  javadoc for diagnosis
	 */
	private CodeValue diagnosis;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="diagnosis")
	@ForeignKey(name="FK_AppGroup_diag")
	@Index(name="IX_AppGroup_diag")
	public CodeValue getDiagnosis(){
		return diagnosis;
	}

	public void setDiagnosis(CodeValue diagnosis){
		this.diagnosis = diagnosis;
	}

	/**
	 *  javadoc for diagnosisIcd9
	 */ 
	private CodeValue diagnosisIcd9;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueIcd9.class)
	@JoinColumn(name="diagnosisIcd9")
	@ForeignKey(name="FK_AppGrp_diagIcd9")
	@Index(name="IX_AppGrp_diagIcd9")
	public CodeValue getDiagnosisIcd9(){
		return diagnosisIcd9;
	}

	public void setDiagnosisIcd9(CodeValue diagnosisIcd9){
		this.diagnosisIcd9 = diagnosisIcd9;
	}

	/**
	 *  javadoc for phoneNumber
	 */
	private String phoneNumber;

	@Column(name="phone_number")
	public String getPhoneNumber(){
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	/**
	 *  javadoc for cancelNote
	 */
	private String cancelNote;

	@Column(name="cancel_note",length=2500)
	public String getCancelNote(){
		return cancelNote;
	}

	public void setCancelNote(String cancelNote){
		this.cancelNote = cancelNote;
	}

	/**
	 *  javadoc for cancDate
	 */
	private Date cancDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="canc_date")
	public Date getCancDate(){
		return cancDate;
	}

	public void setCancDate(Date cancDate){
		this.cancDate = cancDate;
	}
	
	/**
	 *  javadoc for completionDate
	 */
	private Date completionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="compl_date")
	public Date getCompletionDate(){
		return completionDate;
	}

	public void setCompletionDate(Date completionDate){
		this.completionDate = completionDate;
	}

	/**
	 *  javadoc for patientType
	 */
	private CodeValue patientType;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="patientType")
	@ForeignKey(name="FK_AppGroup_patType")
	@Index(name="IX_AppGroup_patType")
	public CodeValue getPatientType(){
		return patientType;
	}

	public void setPatientType(CodeValue patientType){
		this.patientType = patientType;
	}


	/**
	 *  javadoc for availableFrom
	 */
	private Date availableFrom;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="available_from")
	public Date getAvailableFrom(){
		return availableFrom;
	}

	public void setAvailableFrom(Date availableFrom){
		this.availableFrom = availableFrom;
	}


	/**
	 *  javadoc for author
	 */
	private Employee author;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_AppGroup_author")
	@Index(name="IX_AppGroup_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}


	/**
	 *  javadoc for cancelled_by
	 */
	private Employee cancelledBy;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cancelled_by_id")
	@ForeignKey(name="FK_AppGroup_cancelled_by")
	@Index(name="IX_AppGroup_cancelled_by")
	public Employee getCancelledBy(){
		return cancelledBy;
	}

	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}


	/**
	 *  javadoc for appointment
	 */
	private List<Appointment> appointment;

	//@JsonBackReference(value="grouper_appointment")
	@OneToMany(fetch=FetchType.LAZY, mappedBy="appointmentGrouper", cascade=CascadeType.PERSIST)

	public List<Appointment> getAppointment() {
		return appointment;
	}

	public void setAppointment(List<Appointment> appointment) {
		this.appointment = appointment;
	}

	public void addAppointment(Appointment appointment) {
		if (this.appointment == null) {
			this.appointment = new ArrayList<Appointment>();
		}
		// add the association
		if(!this.appointment.contains(appointment)) {
			this.appointment.add(appointment);
			// make the inverse link
			appointment.setAppointmentGrouper(this);
		}
	}


	/**
	 *  javadoc for patient
	 */
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_AppGroup_pat")
	@Index(name="IX_AppGroup_pat")
	//@NotAudited
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}


	/**
	 *  javadoc for genericReport
	 */
	private List<Report> report;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="appointmentGrouper", cascade=CascadeType.PERSIST)
	public List<Report> getReport(){
		return report;
	}

	public void setReport(List<Report> list){
		report = list;
	}

	public void addReport(Report report) {
		if (this.report == null) {
			this.report = new ArrayList<Report>();
		}
		// add the association
		if(!this.report.contains(report)) {
			this.report.add(report);
			// make the inverse link
			report.setAppointmentGrouper(this);
		}
	}

	public void removeGenericReport(Report report) {
		if (this.report == null) {
			this.report = new ArrayList<Report>();
			return;
		}
		//add the association
		if(this.report.contains(report)){
			this.report.remove(report);
			//make the inverse link
			report.setAppointmentGrouper(null);
		}

	}
	
	/**
	 *  javadoc for genericReport
	 */
	private boolean hasCallAnnotation = false;
		
	@Column(name="has_call_annotation")
	public boolean getHasCallAnnotation() {
		return hasCallAnnotation;
	}

	public void setHasCallAnnotation(boolean hasCallAnnotation) {
		this.hasCallAnnotation = hasCallAnnotation;
	}

	/**
	*  javadoc for availableSDLoc
	*/
	private List<ServiceDeliveryLocation> availableSDLoc;

	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinTable(name="AppGrp_SDL", 
		joinColumns = { @JoinColumn(name="AppointmentGrouper_id") }, 
		inverseJoinColumns = { @JoinColumn(name="ServiceDeliveryLocation_id") })
	@ForeignKey(name="FK_SDL_AppGrp", inverseName="FK_AppGrp_sdl")
	
	public List<ServiceDeliveryLocation> getAvailableSDLoc() {
		return availableSDLoc;
	}

	public void setAvailableSDLoc(List<ServiceDeliveryLocation>list){
		availableSDLoc = list;
	}

	public void addAvailableSDLoc(ServiceDeliveryLocation availableSDLoc) {
		if (this.availableSDLoc == null) {
			this.availableSDLoc = new ArrayList<ServiceDeliveryLocation>();
		}
		// add the association
		if(!this.availableSDLoc.contains(availableSDLoc)) {
			this.availableSDLoc.add(availableSDLoc);
			// make the inverse link
//			if (availableSDLoc.getAppointmentGrouper() == null || !availableSDLoc.getAppointmentGrouper().contains(this))
//				availableSDLoc.addAppointmentGrouper(this);
		}
	}

	public void removeAvailableSDLoc(ServiceDeliveryLocation availableSDLoc) {
		if (this.availableSDLoc == null) {
			this.availableSDLoc = new ArrayList<ServiceDeliveryLocation>();
			return;
		}
		//add the association
		if(this.availableSDLoc.contains(availableSDLoc)){
			this.availableSDLoc.remove(availableSDLoc);
			//make the inverse link
//			if (availableSDLoc.getAppointmentGrouper() != null && availableSDLoc.getAppointmentGrouper().contains(this))
//			availableSDLoc.removeAppointmentGrouper(this);
		}
	}
	
	/**
	 * javadoc for unit
	 */
	private CodeValuePhi unit;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="unit")
	@ForeignKey(name="FK_AppGroup_unt")
	@Index(name="IX_AppGroup_unt")
	public CodeValuePhi getUnit(){
		return unit;
	}

	public void setUnit(CodeValuePhi unit){
		this.unit = unit;
	}

	/**
	 * javadoc for disabilityCode
	 */
	private CodeValuePhi disabilityCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="disability_code")
	@ForeignKey(name="FK_AppGroup_disCd")
	@Index(name="IX_AppGroup_disCd")
	public CodeValuePhi getDisabilityCode(){
		return disabilityCode;
	}

	public void setDisabilityCode(CodeValuePhi disabilityCode){
		this.disabilityCode = disabilityCode;
	}

	/**
	 * javadoc for disabilityPriority
	 */
	private Boolean disabilityPriority;

	@Column(name="disability_priority")
	public Boolean getDisabilityPriority() {
		return disabilityPriority;
	}

	public void setDisabilityPriority(Boolean priority) {
		this.disabilityPriority = priority;
	}

}
