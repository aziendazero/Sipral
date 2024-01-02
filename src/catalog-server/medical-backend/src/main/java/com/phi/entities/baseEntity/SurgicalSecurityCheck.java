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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;


@javax.persistence.Entity
@Table(name = "surgical_security_check")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SurgicalSecurityCheck extends AuditedEntity {

	private static final long serialVersionUID = 450980677L;

	private Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	*  javadoc for lisaSurgInt
	*/
//	private LisaSurgicalIntervention lisaSurgInt;
//
//	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="lisa_surg_int_id")
//	@ForeignKey(name="FK_SurgSecChk_lisaSurgInt")
//	@Index(name="IX_SurgSecChk_lisaSurgInt")
//	@NotAudited
//	public LisaSurgicalIntervention getLisaSurgInt(){
//		return lisaSurgInt;
//	}
//
//	public void setLisaSurgInt(LisaSurgicalIntervention lisaSurgInt){
//		this.lisaSurgInt = lisaSurgInt;
//	}
	
	
	/**
	*  javadoc for authora
	*/
	private Employee authora;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="authora_id")
	@ForeignKey(name="FK_SrgcalSecrtyCheck_athora")
	@Index(name="IX_SrgcalSecrtyCheck_athora")
	public Employee getAuthora(){
		return authora;
	}

	public void setAuthora(Employee authora){
		this.authora = authora;
	}


	/**
	*  javadoc for authorb
	*/
	private Employee authorb;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="authorb_id")
	@ForeignKey(name="FK_SrgcalSecrtyCheck_athorb")
	@Index(name="IX_SrgcalSecrtyCheck_athorb")
	public Employee getAuthorb(){
		return authorb;
	}

	public void setAuthorb(Employee authorb){
		this.authorb = authorb;
	}


	/**
	*  javadoc for authorc
	*/
	private Employee authorc;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="authorc_id")
	@ForeignKey(name="FK_SrgcalSecrtyCheck_athorc")
	@Index(name="IX_SrgcalSecrtyCheck_athorc")
	public Employee getAuthorc(){
		return authorc;
	}

	public void setAuthorc(Employee authorc){
		this.authorc = authorc;
	}



	/**
	*  javadoc for author3
	*/
	private Boolean author3;

	@Column(name="author3")
	public Boolean getAuthor3(){
		return author3;
	}

	public void setAuthor3(Boolean author3){
		this.author3 = author3;
	}

	/**
	*  javadoc for author2
	*/
	private Boolean author2;

	@Column(name="author2")
	public Boolean getAuthor2(){
		return author2;
	}

	public void setAuthor2(Boolean author2){
		this.author2 = author2;
	}

	/**
	*  javadoc for author1
	*/
	private Boolean author1;

	@Column(name="author1")
	public Boolean getAuthor1(){
		return author1;
	}

	public void setAuthor1(Boolean author1){
		this.author1 = author1;
	}




	/**
	*  javadoc for signInSecurity
	*/
	private SignInSecurity signInSecurity;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="sign_in_security_id")
	@ForeignKey(name="FK_SrgclScrtyChck_sgnnScrty")
	@Index(name="IX_SrgclScrtyChck_sgnnScrty")
	public SignInSecurity getSignInSecurity(){
		return signInSecurity;
	}

	public void setSignInSecurity(SignInSecurity signInSecurity){
		this.signInSecurity = signInSecurity;
	}



	/**
	*  javadoc for signOutSecurity
	*/
	private SignOutSecurity signOutSecurity;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="sign_out_security_id")
	@ForeignKey(name="FK_SrgclScrtyChck_sgntScrty")
	@Index(name="IX_SrgclScrtyChck_sgntScrty")
	public SignOutSecurity getSignOutSecurity(){
		return signOutSecurity;
	}

	public void setSignOutSecurity(SignOutSecurity signOutSecurity){
		this.signOutSecurity = signOutSecurity;
	}



	/**
	*  javadoc for timeOutSecurity
	*/
	private TimeOutSecurity timeOutSecurity;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="time_out_security_id")
	@ForeignKey(name="FK_SrgclScrtyChck_timtScrty")
	@Index(name="IX_SrgclScrtyChck_timtScrty")
	public TimeOutSecurity getTimeOutSecurity(){
		return timeOutSecurity;
	}

	public void setTimeOutSecurity(TimeOutSecurity timeOutSecurity){
		this.timeOutSecurity = timeOutSecurity;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SurgicalSecurityCheck_sequence")
	@SequenceGenerator(name = "SurgicalSecurityCheck_sequence", sequenceName = "SurgicalSecurityCheck_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	
	
	//methods needed for AuditedEntity extension
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@ForeignKey(name="FK_SurgclScurtyChck_cnclldByRl")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_SurgclScurtyChck_cnclldByRl")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SurgiclScurityChck_cnclldBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_SurgiclScurityChck_cnclldBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
	this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@ForeignKey(name="FK_SurgiclScurityChck_uthorRol")
	@JoinColumn(name="authorRole")
	@Index(name="IX_SurgiclScurityChck_uthorRol")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SurgicalSecurityCheck_uthor")
	@JoinColumn(name="author_id")
	@Index(name="IX_SurgicalSecurityCheck_uthor")
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
	@ForeignKey(name="FK_SurgiclSecurityCheck_lstAth")
	@Index(name="IX_SurgiclSecurityCheck_lstAth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

}
