package com.phi.entities.act;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;

/**
 * Entity implementation class for Entity: ObjectiveExam
 * 
 * FIXME: estendere baseEntity al posto di ACT
 *
 */
@Entity
@Table(name = "objective_exam")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited


public class ObjectiveExam extends AuditedEntity {

	private static final long serialVersionUID = 7024890337818547813L;
	
	//BaseEntity
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ObjctExam_sequence")
	@SequenceGenerator(name = "ObjctExam_sequence", sequenceName = "ObjctExam_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
		
	/**
	 *  javadoc for patient
	 */
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_ObjExam_pat")
	@Index(name="IX_ObjExam_pat")
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
	private Employee author;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_ObjctExam_Pat_Enc")
	@Index(name="IX_ObjctExam_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
	    return patientEncounter;
	}
	public void setPatientEncounter(PatientEncounter param) {
	    this.patientEncounter = param;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="author")
	@ForeignKey(name="FK_ObjectExam_Author")
	@Index(name="IX_ObjectExam_Author")
	public Employee getAuthor() {
	    return author;
	}
	public void setAuthor(Employee param) {
	    this.author = param;
	}
	
	private boolean confirmed=false;
	
	@Column(name = "confirmed")
	public boolean getConfirmed(){
		return confirmed;
	}
	
	public void setConfirmed(boolean confirmed){
		this.confirmed = confirmed;
	}
   
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_ObjectExam_code")
	@Index(name="IX_ObjectExam_code")
	public CodeValuePhi getCode() {
		return code;
	}

	public void setCode(CodeValuePhi code) {
		this.code = code;
	}
	
	//from auditedEntity
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_ObjExam_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_ObjExam_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_ObjExam_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_ObjExam_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_ObjExam_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_MedHist_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	
}
