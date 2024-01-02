package com.phi.entities.act;

import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Lob;
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
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.ED;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;

@Entity
@Table(name = "annotation")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Annotation extends AuditedEntity {

	private static final long serialVersionUID = 7785725248937648507L;
	
	//methods needed for BaseEntity extension

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Annotation_sequence")
	@SequenceGenerator(name = "Annotation_sequence", sequenceName = "Annotation_sequence")
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
	@ForeignKey(name = "FK_Annotation_sdloc")
	@Index(name = "IX_Annotation_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	


		
	private PatientEncounter patientEncounter;


	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Annotation_author")
	@Index(name="IX_Annotation_author")
	public Employee getAuthor() {
	    return author;
	}

	public void setAuthor(Employee param) {
	    this.author = param;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="authorRole")
	@ForeignKey(name="FK_Annotation_authorRole")
	@Index(name="IX_Annotation_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}

	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_Annotation_Pat_Enc")
	@Index(name="IX_Annotation_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
	    return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter param) {
	    this.patientEncounter = param;
	}
	
	/**
	 *  javadoc for patient
	 *  Added patient for backward compatibility for VCO
	 */
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_Annotation_patient")
	@Index(name="IX_Annotation_patient")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}

	/**
	*  javadoc for cancelled_by
	*/

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cancelled_by_id")
	@ForeignKey(name="FK_Annot_cancelled_by")
	@Index(name="IX_Annot_cancelled_by")
	public Employee getCancelledBy(){
		return cancelledBy;
	}

	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cancelledByRole")
	@ForeignKey(name="FK_nnttn_cnclld_by_rl")
	@Index(name="IX_nnttn_cnclld_by_rl")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}

	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}

	/**
	*  javadoc for confirmed  (Temp or Def)
	*  if true, the annotation is definitely confirmed, instead with false is only a temporary annotation
	*/

	private boolean confirmed;
	
	@Column(name="confirmed")
	public boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	
	private ServiceDeliveryLocation assignedSDL;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="assignedSDL")
	@ForeignKey(name="FK_Annot_AssignedSDL")
	@Index(name="IX_Annot_AssignedSDL")
	public ServiceDeliveryLocation getAssignedSDL() {
	    return assignedSDL;
	}

	public void setAssignedSDL(ServiceDeliveryLocation param) {
	    this.assignedSDL = param;
	}
	
	
	private Date observationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="observation_date")
	public Date getObservationDate() {
		return observationDate;
	}

	public void setObservationDate(Date observationDate) {
		this.observationDate = observationDate;
	}
	
	private Boolean isDeleted;
	
	@Column(name="is_deleted")
	public Boolean getIsDeleted(){
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted){
		this.isDeleted = isDeleted;
	}
	
	private String note;
	
	@Column(name="note")
	@Lob
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	//From Act
	
	private CodeValue levelCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="level_code")
	@ForeignKey(name="FK_Annot_lvlcod")
	@Index(name="IX_Annot_lvlcod")
	public CodeValue getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(CodeValue levelCode) {
		this.levelCode = levelCode;
	}
	
	private ED title;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="title_mediaType"))
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
	

	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Annot_Code")
	@Index(name="IX_Annot_Code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}

	
}