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
@Table(name = "sign_out_security")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SignOutSecurity extends AuditedEntity {

	private static final long serialVersionUID = 451306239L;

	private Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	
	/**
	*  javadoc for q21text
	*/
	private String q21text;

	@Column(name="q21text")
	public String getQ21text(){
		return q21text;
	}

	public void setQ21text(String q21text){
		this.q21text = q21text;
	}

	/**
	*  javadoc for q61text
	*/
	private String q61text;

	@Column(name="q61text")
	public String getQ61text(){
		return q61text;
	}

	public void setQ61text(String q61text){
		this.q61text = q61text;
	}

	/**
	*  javadoc for q61
	*/
	private CodeValuePhi q61;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q61")
	@ForeignKey(name="FK_SignOutSecurity_q61")
	@Index(name="IX_SignOutSecurity_q61")
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
	@ForeignKey(name="FK_SignOutSecurity_q51")
	@Index(name="IX_SignOutSecurity_q51")
	public CodeValuePhi getQ51(){
		return q51;
	}

	public void setQ51(CodeValuePhi q51){
		this.q51 = q51;
	}

	/**
	*  javadoc for q41text
	*/
	private String q41text;

	@Column(name="q41text")
	public String getQ41text(){
		return q41text;
	}

	public void setQ41text(String q41text){
		this.q41text = q41text;
	}

	/**
	*  javadoc for q41
	*/
	private CodeValuePhi q41;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q41")
	@ForeignKey(name="FK_SignOutSecurity_q41")
	@Index(name="IX_SignOutSecurity_q41")
	public CodeValuePhi getQ41(){
		return q41;
	}

	public void setQ41(CodeValuePhi q41){
		this.q41 = q41;
	}

	/**
	*  javadoc for q31
	*/
	private CodeValuePhi q31;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q31")
	@ForeignKey(name="FK_SignOutSecurity_q31")
	@Index(name="IX_SignOutSecurity_q31")
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
	@ForeignKey(name="FK_SignOutSecurity_q21")
	@Index(name="IX_SignOutSecurity_q21")
	public CodeValuePhi getQ21(){
		return q21;
	}

	public void setQ21(CodeValuePhi q21){
		this.q21 = q21;
	}

	/**
	*  javadoc for q11
	*/
	private String q11;

	@Column(name="q11", length=2500)
	public String getQ11(){
		return q11;
	}

	public void setQ11(String q11){
		this.q11 = q11;
	}

//
//	/**
//	*  javadoc for 
//	*/
//	private SurgicalSecurityCheck surgicalSecurityCheck;
//
//	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="surgical_security_check_id")
//	@ForeignKey(name="FK_SgntScrty_srgclScrtyChck")
//	@Index(name="IX_SgntScrty_srgclScrtyChck")
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SignOutSecurity_sequence")
	@SequenceGenerator(name = "SignOutSecurity_sequence", sequenceName = "SignOutSecurity_sequence")
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
	@ForeignKey(name="FK_SignOutSecurity_cnclldByRol")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_SignOutSecurity_cnclldByRol")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SignOutSecurity_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_SignOutSecurity_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
	this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@ForeignKey(name="FK_SignOutSecurity_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_SignOutSecurity_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_SignOutSecurity_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_SignOutSecurity_author")
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
	@ForeignKey(name="FK_SignOutSecurity_lastAth")
	@Index(name="IX_SignOutSecurity_lastAth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

}
