package com.phi.entities.baseEntity;

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
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.act.PatientEncounter;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;
@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public abstract class MedicalHistory extends AuditedEntity {

	private static final long serialVersionUID = 1471796419L;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MedicalHistory_sequence")
	@SequenceGenerator(name = "MedicalHistory_sequence", sequenceName = "MedicalHistory_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	private ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_MedicalHistory_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	/**
	 *  javadoc for patient
	 */
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_MedHis_pat")
	@Index(name="IX_MedHis_pat")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}
	/**
	 *  javadoc for patientEncounter
	 */
	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patientEncounter_id")
	@ForeignKey(name="FK_MedHis_ptntEnc")
	@Index(name="IX_MedHis_ptntEnc")
	public PatientEncounter getPatientEncounter(){
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter patientEncounter){
		this.patientEncounter = patientEncounter;
	}

	/**
	 *  javadoc for appointmentGrouper
	 */
	private AppointmentGrouper appointmentGrouper;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="appointmentGrouper_id")
	@ForeignKey(name="FK_MedHist_appGroup")
	@Index(name="IX_MedHist_appGroup")
	public AppointmentGrouper getAppointmentGrouper(){
		return appointmentGrouper;
	}

	public void setAppointmentGrouper(AppointmentGrouper appointmentGrouper){
		this.appointmentGrouper = appointmentGrouper;
	}

	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_MedHist_code")
	@Index(name="IX_MedHist_code") 
	public CodeValuePhi getCode() {
		return code;
	}

	public void setCode(CodeValuePhi code) {
		this.code = code;
	}

	//-------------from auditedEntity
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_MedHist_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_MedHist_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_MedHist_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_MedHist_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_MedHist_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_MedHist_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_MedHist_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_MedHist_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}

    private boolean confirmed=false;
	
	@Column(name = "confirmed")
	public boolean getConfirmed(){
		return confirmed;
	}
	
	public void setConfirmed(boolean confirmed){
		this.confirmed = confirmed;
	}
}