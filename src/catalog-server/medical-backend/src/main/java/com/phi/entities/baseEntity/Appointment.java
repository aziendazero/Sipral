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
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.act.Procedure;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.ED;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.tracedEntity.TracedEntity;

@Entity
@Table(name = "appointment")
@Audited
@org.hibernate.annotations.Table(appliesTo = "appointment", indexes = { @Index(name="IX_APPOINTMENT", columnNames = { "defaultDate", "serviceDeliveryLocation", "insert_completed" } ) })
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="internalId")
public class Appointment extends BaseEntity implements LocatedEntity, TracedEntity {

	private static final long serialVersionUID = 586502787L;
	
	/**
	*  javadoc for visitType
	*/
	private CodeValuePhi visitType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="visitType")
	@ForeignKey(name="FK_appointment_visitType")
	@Index(name="IX_appointment_visitType")
	public CodeValuePhi getVisitType(){
		return visitType;
	}

	public void setVisitType(CodeValuePhi visitType){
		this.visitType = visitType;
	}
	
	/**
	*  Code to identify type of reason/person cause of the appointment.  
	*  E.g.: specialist doctor / SSN convention request / patient decision / .. 
	*/
	private CodeValuePhi sourceCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sourceCode")
	@ForeignKey(name="FK_appointmenta_sourceCode")
	@Index(name="IX_appointment_sourceCode")
	public CodeValuePhi getSourceCode(){
		return sourceCode;
	}

	public void setSourceCode(CodeValuePhi sourceCode){
		this.sourceCode = sourceCode;
	}


	/**
	*  javadoc for groupers
	*/
	private List<AppointmentGrouper> groupers;
	
	@JsonManagedReference(value="groupers")
	@OneToMany(fetch=FetchType.LAZY, mappedBy="rootAppointment", cascade=CascadeType.PERSIST)
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<AppointmentGrouper> getGroupers() {
		return groupers;
	}

	public void setGroupers(List<AppointmentGrouper>list){
		groupers = list;
	}

	public void addAppointmentGrouper(AppointmentGrouper group) {
		if (this.groupers == null) {
			this.groupers = new ArrayList<AppointmentGrouper>();
		}
		// add the association
		if(!this.groupers.contains(group)) {
			this.groupers.add(group);
			// make the inverse link
			group.setRootAppointment(this);
		}
	}

	public void removeAppointmentGrouper(AppointmentGrouper group) {
		if (this.groupers == null) {
			this.groupers = new ArrayList<AppointmentGrouper>();
			return;
		}
		//add the association
		if(this.groupers.contains(group)){
			this.groupers.remove(group);
			//make the inverse link
			group.setRootAppointment(null);
		}
	}

	//ACT FIELDS
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "appointment_sequence")
	@SequenceGenerator(name = "appointment_sequence", sequenceName = "appointment_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}

	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	protected ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_appointment_sdloc")
	@Index(name="IX_appointment_sdloc")
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
	@ForeignKey(name="FK_appointment_sc")
	@Index(name="IX_appointment_sc")
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}

	private ED text;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="text_mediaType"))
	//FIXME: @ForeignKey(name="FK_Act_text_mediaType")
	//@Index(name="IX_Act_text_mediaType")
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
	//END ACT FIELDS

	/**
	*  javadoc for color
	*/
	private Integer color;

	@Column(name="color")
	public Integer getColor(){
		return color;
	}

	public void setColor(Integer color){
		this.color = color;
	}
	
	/**
	 *  javadoc for duration
	 */
//	private ED duration;
//
//	@Embedded
//	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="duration_mediaType"))
//	@AttributeOverrides({
//		@AttributeOverride(name="string", column=@Column(name="duration_string", length=2500)),
//		@AttributeOverride(name="bytes", column=@Column(name="duration_bytes"))
//	})
//	public ED getDuration(){
//		return duration;
//	}
//
//	public void setDuration(ED duration){
//		this.duration = duration;
//	}
	
	private Integer duration;

	@Column(name="duration")
	public Integer getDuration(){
		return duration;
	}

	public void setDuration(Integer duration){
		this.duration = duration;
	}
	
	/**
	 *  javadoc for insertCompleted
	 */
	private Boolean insertCompleted;

	@Column(name="insert_completed")
	public Boolean getInsertCompleted(){
		return insertCompleted;
	}

	public void setInsertCompleted(Boolean insertCompleted){
		this.insertCompleted = insertCompleted;
	}

	/**
	 *  javadoc for isIndirect
	 */
	private Boolean isIndirect;

	@Column(name="is_indirect")
	public Boolean getIsIndirect(){
		return isIndirect;
	}

	public void setIsIndirect(Boolean isIndirect){
		this.isIndirect = isIndirect;
	}

	/**
	 *  javadoc for date
	 */
	private Date defaultDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="defaultDate")
	public Date getDefaultDate(){
		return defaultDate;
	}

	public void setDefaultDate(Date defaultDate){
		this.defaultDate = defaultDate;
	}
	
	/**
	 *  date for set confirmation date by patient or date when effectly patient is arrived (Equal to encounter effective time)
	 */
	private Date confirmDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="confirmDate")
	public Date getConfirmDate(){
		return confirmDate;
	}

	public void setConfirmDate(Date confirmDate){
		this.confirmDate = confirmDate;
	}

	/**
	 *  javadoc for cancReasonDetail
	 */
	private String cancReasonDetail;

	@Column(name="canc_reason_detail",length=2500)
	public String getCancReasonDetail(){
		return cancReasonDetail;
	}

	public void setCancReasonDetail(String cancReasonDetail){
		this.cancReasonDetail = cancReasonDetail;
	}

	/**
	 *  javadoc for cancReasonCode
	 */
	private CodeValue cancReasonCode;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="cancReasonCode")
	@ForeignKey(name="FK_Appointment_cancReasonCode")
	@Index(name="IX_Appointment_cancReasonCode")
	public CodeValue getCancReasonCode(){
		return cancReasonCode;
	}

	public void setCancReasonCode(CodeValue cancReasonCode){
		this.cancReasonCode = cancReasonCode;
	}


	/**
	 *  javadoc for patientEncounter
	 */
	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_App_Pat_Enc")
	@Index(name="IX_App_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter param) {
		this.patientEncounter = param;
	}


	/**
	 *  javadoc for author
	 */
	private Employee author;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Appointment_author")
	@Index(name="IX_Appointment_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}

	/**
	 *  javadoc for patient
	 */

	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_Appointment_patient")
	@Index(name="IX_Appointment_patient")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}

	/**
	 *  javadoc for procedure
	 */

	private Procedure procedure;

	@ManyToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="procedure_id")
	@ForeignKey(name="FK_Appointment_procedure")
	@Index(name="IX_Appointment_procedure")
	public Procedure getProcedure(){
		return procedure;
	}

	public void setProcedure(Procedure procedure){
		this.procedure = procedure;
	}





	/**
	 *  javadoc for firstAppointment
	 */
	private Boolean firstAppointment;

	@Column(name="first_appointment")
	public Boolean getFirstAppointment(){
		return firstAppointment;
	}

	public void setFirstAppointment(Boolean firstAppointment){
		this.firstAppointment = firstAppointment;
	}

	private AppointmentGrouper appointmentGrouper;

	//@JsonManagedReference(value="grouper_appointment")
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="appointmentGrouper_id")
	@ForeignKey(name="FK_Appointment_appGrouper")
	@Index(name="IX_Appointment_appGrouper")
	public AppointmentGrouper getAppointmentGrouper() {
		return appointmentGrouper;
	}

	public void setAppointmentGrouper(AppointmentGrouper appointmentGrouper) {
		this.appointmentGrouper = appointmentGrouper;
	}


	private Boolean indirectProcedure;



	@Column(name="indirect_procedure")
	public Boolean getIndirectProcedure() {
		return indirectProcedure;
	}

	public void setIndirectProcedure(Boolean indirectProcedure) {
		this.indirectProcedure = indirectProcedure;
	}

	/**
	 *  javadoc for clinicalProcedures
	 *  PHI_KLINIK: performedProdedure is used to map list of PLANNED procedures.
	 *  the procedures effectly peformed during the appointment are attached to the encounter 
	 */
	private List<Procedure> performedProcedure;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="Appointment_id")
	@ForeignKey(name="FK_prfmdPrcedure_Appntment")
	@Index(name="IX_prfmdlPrcedure_Appntment")	
	public List<Procedure> getPerformedProcedure() {
		return performedProcedure;
	}

	public void setPerformedProcedure(List<Procedure> list){
		performedProcedure = list;
	}

	public void addPerformedProcedure(Procedure performedProcedure) {
		if (this.performedProcedure == null) {
			this.performedProcedure = new ArrayList<Procedure>();
		}
		// add the association
		if(!this.performedProcedure.contains(performedProcedure)) {
			this.performedProcedure.add(performedProcedure);
		}
	}
	
	public void removePerformedProcedure(Procedure performedProcedure) {
		//remove the association
		if(this.performedProcedure != null && this.performedProcedure.contains(performedProcedure)){
			this.performedProcedure.remove(performedProcedure);
		}
	}
	
	/**
	 *  javadoc for procedureRequest
	 *  see http://stackoverflow.com/questions/4334970/hibernate-cannot-simultaneously-fetch-multiple-bags why
	 *  there is LazyCollection and not FETCH_TYPE.EAGER
	 */
	private List<ProcedureRequest> procedureRequest;
	@JsonManagedReference
	@OneToMany(mappedBy="appointment", cascade={CascadeType.PERSIST,CascadeType.MERGE})
	@LazyCollection(LazyCollectionOption.FALSE)
	public List<ProcedureRequest> getProcedureRequest() {
		return procedureRequest;
	}

	public void setProcedureRequest(List<ProcedureRequest>list){
		procedureRequest = list;
	}

	public void addProcedureRequest(ProcedureRequest procedureRequest) {
		if (this.procedureRequest == null) {
			this.procedureRequest = new ArrayList<ProcedureRequest>();
		}
		// add the association
		if(!this.procedureRequest.contains(procedureRequest)) {
			this.procedureRequest.add(procedureRequest);
			// make the inverse link
			procedureRequest.setAppointment(this);
		}
	}

	public void removeProcedureRequest(ProcedureRequest procedureRequest) {
		if (this.procedureRequest == null) {
			this.procedureRequest = new ArrayList<ProcedureRequest>();
			return;
		}
		//remove the association
		if(this.procedureRequest.contains(procedureRequest)){
			this.procedureRequest.remove(procedureRequest);
			//make the inverse link
			procedureRequest.setAppointment(null);
		}
	}

	/**
	 *  externalId used by integrations
	 *  store here id of an external system
	 */
	private String externalId;

	@Column(name="external_id")//, unique=true)
	@Index(name="IX_Appointment_ExtId")
	public String getExternalId(){
		return externalId;
	}

	public void setExternalId(String externalId){
		this.externalId = externalId;
	}
	
	
	/**
	 *  externalIdRoot used by integrations
	 *  store here the root code of an external system
	 */
	private String externalIdRoot;

	@Column(name="external_id_root")
	@Index(name="IX_App_ExtId_R")
	public String getExternalIdRoot(){
		return externalIdRoot;
	}

	public void setExternalIdRoot(String externalIdRoot){
		this.externalIdRoot = externalIdRoot;
	}
	
	/**
	 *  date for set activation time (assignment of encounter)
	 */
	private Date activationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="activationDate")
	public Date getActivationDate(){
		return activationDate;
	}

	public void setActivationDate(Date activationDate){
		this.activationDate = activationDate;
	}
	
//	/**
//	*  javadoc for session
//	*/
//	private Session session;
//
//	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="session_id")
//	@ForeignKey(name="FK_Appointment_session")
//	@Index(name="IX_Appointment_session")
//	public Session getSession(){
//		return session;
//	}
//
//	public void setSession(Session session){
//		this.session = session;
//	}
	
	/**
	 *  Used to map general informations in a rehab program
	 */
	private String stepMarker;

	@Column(name="step_marker")
	public String getStepMarker(){
		return stepMarker;
	}

	public void setStepMarker(String stepMarker){
		this.stepMarker = stepMarker;
	}
	
	/**
	 *  COMPLETED OR REPORTED DATE
	 */
	private Date completionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="completionDate")
	public Date getCompletionDate(){
		return completionDate;
	}

	public void setCompletionDate(Date completionDate){
		this.completionDate = completionDate;
	}
	
	/**
	 *  Used to map a reference to an external system for the appointment
	 */
	private String externalReference;

	@Column(name="external_reference")
	public String getExternalReference(){
		return externalReference;
	}

	public void setExternalReference(String externalReference){
		this.externalReference = externalReference;
	}

	/* *********** TRACEDENTITY IMPLEMENTATION *********** */

	private String createdByLocation;

	@Column(name = "created_by_location")
	public String getCreatedByLocation() {
		return createdByLocation;
	}

	public void setCreatedByLocation(String createdByLocation) {
		this.createdByLocation = createdByLocation;
	}

	private String modifiedBy;

	@Column(name = "modified_by")
	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	private Date modificationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modification_date")
	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	private String modifiedByLocation;

	@Column(name = "modified_by_location")
	public String getModifiedByLocation() {
		return modifiedByLocation;
	}

	public void setModifiedByLocation(String modifiedByLocation) {
		this.modifiedByLocation = modifiedByLocation;
	}

}
