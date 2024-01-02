package com.phi.entities.act;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;

/**
 * Entity implementation class for Entity: AssessmentScale
 *
 */
@Entity
@Table(name = "assessment_scale")
@Audited
public class AssessmentScale extends AuditedEntity {

	private static final long serialVersionUID = 3503534484689563815L;

	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_AssmentScale_Pat_Enc")
	@Index(name="IX_AssmentScale_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
		return patientEncounter;
	}
	public void setPatientEncounter(PatientEncounter param) {
		this.patientEncounter = param;
	}

	/**
	 *  javadoc for author
	 */
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_AssmentScale_author")
	@Index(name="IX_AssmentScale_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}

	/**
	 *  javadoc for authorRole
	 */
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="authorRole")
	@ForeignKey(name="FK_ssscl_authorRole")
	@Index(name="IX_ssscl_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cancelled_by_id")
	@ForeignKey(name="FK_ssscl_cancelled_by")
	@Index(name="IX_ssscl_cancelled_by")
	public Employee getCancelledBy(){
		return cancelledBy;
	}

	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cancelled_by_role")
	@ForeignKey(name="FK_ssscl_cnclld_by_rl")
	@Index(name="IX_ssscl_cnclld_by_rl")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}

	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}

	/**
	 * javadoc for code
	 */
	private CodeValue code;

	@ManyToOne(fetch = FetchType.LAZY,targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "code")
	@ForeignKey(name = "FK_Assessment_Scale_code")
	@Index(name="IX_Assessment_Scale_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private CodeValue moodCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="mood_code")
	@ForeignKey(name="FK_AssScal_mc")
	@Index(name="IX_AssScal_mc") //FIXME: shorted FK_AssessmentScale_moodCode
	public CodeValue getMoodCode() {
		return moodCode;
	}

	public void setMoodCode(CodeValue moodCode) {
		this.moodCode = moodCode;
	}
	
	//MANTIS http://support.insielmercato.it/mantis/view.php?id=25886
	/**
	* if the Scale is filled for a rehabilitation Path, this code explains the type of setting in which the path is
	* in that moment. 
	*/
	private CodeValuePhi rehabPathSettingCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="rehabPathSettingCode")
	@ForeignKey(name="FK_AssScale_settingCode")
	@Index(name="IX_AssScale_settingCode")
	public CodeValuePhi getRehabPathSettingCode(){
		return rehabPathSettingCode;
	}

	public void setRehabPathSettingCode(CodeValuePhi rehabPathSettingCode){
		this.rehabPathSettingCode = rehabPathSettingCode;
	}
	
	/**
	 * This boolean reveal the Scale as important/critical
	 */
	private Boolean meaningful;

	@Column(name="meaningful")
	public Boolean getMeaningful() {
		return meaningful;
	}
	public void setMeaningful(Boolean meaningful) {
		this.meaningful = meaningful;
	}
	
	/**
	 * This note explains the reason the Scale is important/critical
	 */
	private String meaningNote;
	
	@Column(name="meaning_note", length = 2500)
	public String getMeaningNote() {
		return meaningNote;
	}

	public void setMeaningNote(String meaningNote) {
		this.meaningNote = meaningNote;
	}
	
	private Integer score;
	private Double scoreDouble;
	private String note;


	private Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	@Column(name="note", length = 2500)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name="score")
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	
	@Column(name="score_double")
	public Double getScoreDouble() {
		return scoreDouble;
	}
	public void setScoreDouble(Double scoreDouble) {
		this.scoreDouble = scoreDouble;
	}
	//methods needed for BaseEntity extension

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_AssScale_sdloc")
	@Index(name = "IX_AssScale_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AssessmentScale_sequence")
	@SequenceGenerator(name = "AssessmentScale_sequence", sequenceName = "AssessmentScale_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
}