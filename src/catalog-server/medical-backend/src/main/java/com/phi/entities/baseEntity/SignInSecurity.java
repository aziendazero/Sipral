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
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "sign_in_security")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SignInSecurity extends AuditedEntity {

	private static final long serialVersionUID = 451504500L;

	private Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	*  javadoc for proceduretext
	*/
	private String proceduretext;

	@Column(name="proceduretext")
	public String getProceduretext(){
		return proceduretext;
	}

	public void setProceduretext(String proceduretext){
		this.proceduretext = proceduretext;
	}

	/**
	*  javadoc for q71
	*/
	private CodeValuePhi q71;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q71")
	@ForeignKey(name="FK_SignInSecurity_q71")
	@Index(name="IX_SignInSecurity_q71")
	public CodeValuePhi getQ71(){
		return q71;
	}

	public void setQ71(CodeValuePhi q71){
		this.q71 = q71;
	}

	/**
	*  javadoc for q61
	*/
	private CodeValuePhi q61;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q61")
	@ForeignKey(name="FK_SignInSecurity_q61")
	@Index(name="IX_SignInSecurity_q61")
	public CodeValuePhi getQ61(){
		return q61;
	}

	public void setQ61(CodeValuePhi q61){
		this.q61 = q61;
	}

	/**
	*  javadoc for q51text
	*/
	private String q51text;

	@Column(name="q51text")
	public String getQ51text(){
		return q51text;
	}

	public void setQ51text(String q51text){
		this.q51text = q51text;
	}

	/**
	*  javadoc for q51
	*/
	private CodeValuePhi q51;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q51")
	@ForeignKey(name="FK_SignInSecurity_q51")
	@Index(name="IX_SignInSecurity_q51")
	public CodeValuePhi getQ51(){
		return q51;
	}

	public void setQ51(CodeValuePhi q51){
		this.q51 = q51;
	}

	/**
	*  javadoc for q41
	*/
	private CodeValuePhi q41;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q41")
	@ForeignKey(name="FK_SignInSecurity_q41")
	@Index(name="IX_SignInSecurity_q41")
	public CodeValuePhi getQ41(){
		return q41;
	}

	public void setQ41(CodeValuePhi q41){
		this.q41 = q41;
	}

	/**
	*  javadoc for q31text
	*/
	private String q31text;

	@Column(name="q31text")
	public String getQ31text(){
		return q31text;
	}

	public void setQ31text(String q31text){
		this.q31text = q31text;
	}

	/**
	*  javadoc for q31
	*/
	private CodeValuePhi q31;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q31")
	@ForeignKey(name="FK_SignInSecurity_q31")
	@Index(name="IX_SignInSecurity_q31")
	public CodeValuePhi getQ31(){
		return q31;
	}

	public void setQ31(CodeValuePhi q31){
		this.q31 = q31;
	}

	/**
	*  javadoc for q21
	*/
	private CodeValuePhi q21;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q21")
	@ForeignKey(name="FK_SignInSecurity_q21")
	@Index(name="IX_SignInSecurity_q21")
	public CodeValuePhi getQ21(){
		return q21;
	}

	public void setQ21(CodeValuePhi q21){
		this.q21 = q21;
	}


	/**
	*  javadoc for q14text
	*/
	private String q14text;

	@Column(name="q14text")
	public String getQ14text(){
		return q14text;
	}

	public void setQ14text(String q14text){
		this.q14text = q14text;
	}

	/**
	*  javadoc for q14
	*/
	private CodeValuePhi q14;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q14")
	@ForeignKey(name="FK_SignInSecurity_q14")
	@Index(name="IX_SignInSecurity_q14")
	public CodeValuePhi getQ14(){
		return q14;
	}

	public void setQ14(CodeValuePhi q14){
		this.q14 = q14;
	}

	/**
	*  javadoc for q13text
	*/
	private String q13text;

	@Column(name="q13text")
	public String getQ13text(){
		return q13text;
	}

	public void setQ13text(String q13text){
		this.q13text = q13text;
	}

	/**
	*  javadoc for q13
	*/
	private CodeValuePhi q13;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q13")
	@ForeignKey(name="FK_SignInSecurity_q13")
	@Index(name="IX_SignInSecurity_q13")
	public CodeValuePhi getQ13(){
		return q13;
	}

	public void setQ13(CodeValuePhi q13){
		this.q13 = q13;
	}

	/**
	*  javadoc for q12text
	*/
	private String q12text;

	@Column(name="q12text")
	public String getQ12text(){
		return q12text;
	}

	public void setQ12text(String q12text){
		this.q12text = q12text;
	}

	/**
	*  javadoc for q12
	*/
	private CodeValuePhi q12;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q12")
	@ForeignKey(name="FK_SignInSecurity_q12")
	@Index(name="IX_SignInSecurity_q12")
	public CodeValuePhi getQ12(){
		return q12;
	}

	public void setQ12(CodeValuePhi q12){
		this.q12 = q12;
	}

	/**
	*  javadoc for q11text
	*/
	private String q11text;

	@Column(name="q11text")
	public String getQ11text(){
		return q11text;
	}

	public void setQ11text(String q11text){
		this.q11text = q11text;
	}

	/**
	*  javadoc for q11
	*/
	private CodeValuePhi q11;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q11")
	@ForeignKey(name="FK_SignInSecurity_q11")
	@Index(name="IX_SignInSecurity_q11")
	public CodeValuePhi getQ11(){
		return q11;
	}

	public void setQ11(CodeValuePhi q11){
		this.q11 = q11;
	}


//
//	/**
//	*  javadoc for surgicalSecurityCheck
//	*/
//	private SurgicalSecurityCheck surgicalSecurityCheck;
//
//	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="surgical_security_check_id")
//	@ForeignKey(name="FK_SgnnScrty_srgclScrtyChck")
//	@Index(name="IX_SgnnScrty_srgclScrtyChck")
//	public SurgicalSecurityCheck getSurgicalSecurityCheck(){
//		return surgicalSecurityCheck;
//	}
//
//	public void setSurgicalSecurityCheck(SurgicalSecurityCheck surgicalSecurityCheck){
//		this.surgicalSecurityCheck = surgicalSecurityCheck;
//	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SignInSecurity_sequence")
	@SequenceGenerator(name = "SignInSecurity_sequence", sequenceName = "SignInSecurity_sequence")
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
	@ForeignKey(name="FK_SignInSecurity_cncelldByRol")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_SignInSecurity_cncelldByRol")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SignInSecurity_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_SignInSecurity_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
	this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@ForeignKey(name="FK_SignInSecurity_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_SignInSecurity_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SignInSecurity_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_SignInSecurity_author")
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
	@ForeignKey(name="FK_SignInSecurity_lastAth")
	@Index(name="IX_SignInSecurity_lastAth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

}
