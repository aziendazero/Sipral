package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.act.Diagnosis;
import com.phi.entities.act.PatientEncounter;
import com.phi.entities.act.Procedure;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;
@javax.persistence.Entity
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Report extends AuditedEntity implements LocatedEntity {

	private static final long serialVersionUID = 254457400L;

	/**
	*  javadoc for disOption
	*/
	private CodeValuePhi disOption;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="disOption")
	@ForeignKey(name="FK_report_disOption")
	@Index(name="IX_report_disOption")
	public CodeValuePhi getDisOption(){
		return disOption;
	}

	public void setDisOption(CodeValuePhi disOption){
		this.disOption = disOption;
	}
	/**
	*  javadoc for isPrivate
	*/
	private Boolean isPrivate;

	@Column(name="is_private")
	public Boolean getIsPrivate(){
		return isPrivate;
	}

	public void setIsPrivate(Boolean isPrivate){
		this.isPrivate = isPrivate;
	}
	
	/**
	 *  javadoc for appointmentGrouper
	 */
	private AppointmentGrouper appointmentGrouper;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="appointmentGrouper_id")
	@ForeignKey(name="FK_GnrcRport_appontmntGrupr")
	@Index(name="IX_GnrcRport_appontmntGrupr")
	public AppointmentGrouper getAppointmentGrouper(){
		return appointmentGrouper;
	}

	public void setAppointmentGrouper(AppointmentGrouper appointmentGrouper){
		this.appointmentGrouper = appointmentGrouper;
	}


	/**
	 *  javadoc for appointment
	 */
	private Appointment appointment;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="appointment_id")
	@ForeignKey(name="FK_Report_appointment")
	@Index(name="IX_Report_appointment")
	public Appointment getAppointment(){
		return appointment;
	}

	public void setAppointment(Appointment appointment){
		this.appointment = appointment;
	}


	/**
	 *  javadoc for patient
	 */
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_Report_patient")
	@Index(name="IX_Report_patient")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}

	@Override
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE, generator = "report_sequence")
	@TableGenerator(name="report_sequence", pkColumnValue="REPORT_ID", allocationSize = 1)
	//@GeneratedValue(strategy = GenerationType.AUTO, generator = "report_sequence")
	//@SequenceGenerator(name = "report_sequence", sequenceName = "report_sequence")
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
	@ForeignKey(name = "FK_Rep_sdloc")
	@Index(name="IX_Rep_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}


	/**
	 *  javadoc for patientEncounter
	 */

	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_encounter_id")
	@ForeignKey(name="FK_Rep_patientEncounter")
	@Index(name="IX_Rep_patientEncounter")
	public PatientEncounter getPatientEncounter(){
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter patientEncounter){
		this.patientEncounter = patientEncounter;
	}
	/**
	 *  javadoc for author
	 */

	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_Rep_code")
	@Index(name="IX_Rep_code") 
	public CodeValuePhi getCode() {
		return code;
	}

	public void setCode(CodeValuePhi code) {
		this.code = code;
	}

	private ServiceDeliveryLocation district;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="district")
	@ForeignKey(name="FK_DschDt_dstrct")
	@Index(name="IX_DschDt_dstrct")
	public ServiceDeliveryLocation getDistrict() {
		return district;
	}
	public void setDistrict(ServiceDeliveryLocation district) {
		this.district = district;
	}

	protected Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	private String stateMode;

	@Column(name="state_mode")
	public String getStateMode(){
		return stateMode;
	}

	public void setStateMode(String stateMode){
		this.stateMode = stateMode;
	}

//	private EmployeeRole respNote;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="respNote")
//	@ForeignKey(name="FK_note_Resp")
//	@Index(name="IX_note_Resp")
//	public EmployeeRole getRespNote() {
//		return respNote;
//	}
//
//	public void setRespNote(EmployeeRole respNote) {
//		this.respNote = respNote;
//	}

	private Date respDate;

	@Column(name="resp_date")
	public Date getRespDate(){
		return respDate;
	}

	public void setRespDate(Date respDate){
		this.respDate = respDate;
	}

//	private Boolean unblocked;
//
//
//	@Column(name="unblocked")
//	public Boolean getUnblocked() {
//		return unblocked;
//	}
//	public void setUnblocked(Boolean unblocked) {
//		this.unblocked = unblocked;
//	}
//
//	/**
//	 *  javadoc for cancellationDate
//	 */
//	private Date unblockDate;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="unblock_date")
//	public Date getUnblockDate(){
//		return unblockDate;
//	}
//
//	public void setUnblockDate(Date unblockDate){
//		this.unblockDate = unblockDate;
//	}
//
//	private EmployeeRole unblockAuthor;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="unblockAuthor")
//	@ForeignKey(name="FK_NurseRep_UnblockAth")
//	@Index(name="IX_NurseRep_UnblockAth")
//	public EmployeeRole getUnblockAuthor() {
//		return unblockAuthor;
//	}
//	public void setUnblockAuthor(EmployeeRole unblockAuthor) {
//		this.unblockAuthor = unblockAuthor;
//	}
//
//	private String unblockNote;
//	@Column(name="unblockNote") 
//	public String getUnblockNote() {
//		return unblockNote;
//	}
//	public void setUnblockNote(String unblockNote) {
//		this.unblockNote = unblockNote;
//	}

	private boolean isUpdate=false;

	@Column(name = "isUpdate")
	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}
	
	/**
	*  javadoc for procedure
	*/
	private List<Procedure> procedure;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="report", cascade=CascadeType.PERSIST)
	public List<Procedure> getProcedure(){
		return procedure;
	}

	public void setProcedure(List<Procedure> list){
		procedure = list;
	}

	public void addProcedure(Procedure procedure) {
		if (this.procedure == null) {
			this.procedure = new ArrayList<Procedure>();
		}
		// add the association
		if(!this.procedure.contains(procedure)) {
			this.procedure.add(procedure);
			// make the inverse link
			procedure.setReport(this);
		}
	}

	public void removeProcedure(Procedure procedure) {
		if (this.procedure == null) {
			this.procedure = new ArrayList<Procedure>();
			return;
		}
		//add the association
		if(this.procedure.contains(procedure)){
			this.procedure.remove(procedure);
			//make the inverse link
			procedure.setReport(null);
		}

	}
	

	/**
	*  javadoc for diagnosis
	*/
	private List<Diagnosis> diagnosis;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="report")
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
			diagnosis.setReport(this);
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
			diagnosis.setReport(null);
		}

	}
	
	//-------------from auditedEntity
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_Report_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_Report_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_Report_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_Report_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_Report_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_Report_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_Report_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_Report_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}
	
	private Employee lastAuthor;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="last_author_id")
	@ForeignKey(name="FK_Report_lastAth")
	@Index(name="IX_Report_lastAth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}
	
	private Boolean privateData = false;
	
	@Column(name = "priv_data")
	public Boolean getPrivateData() {
		return privateData;
	}

	public void setPrivateData(Boolean privateData) {
		this.privateData = privateData;
	}

	private Boolean fseObscuration = false;

	@Column(name = "fse_oscure")
	public Boolean getFseObscuration() {
		return fseObscuration;
	}

	public void setFseObscuration(Boolean fseObscuration) {
		this.fseObscuration = fseObscuration;
	}

	private Boolean availableToPatient = false;

	@Column(name = "avail_to_pat")
	public Boolean getAvailableToPatient() {
		return availableToPatient;
	}

	public void setAvailableToPatient(Boolean availableToPatient) {
		this.availableToPatient = availableToPatient;
	}
}
