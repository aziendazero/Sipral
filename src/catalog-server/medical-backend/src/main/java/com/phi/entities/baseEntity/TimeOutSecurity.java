
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
@Table(name = "time_out_security")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class TimeOutSecurity extends AuditedEntity {

	private static final long serialVersionUID = 451391413L;

	private Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	*  javadoc for q71
	*/
	private CodeValuePhi q71;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q71")
	@ForeignKey(name="FK_TimeOutSecurity_q71")
	@Index(name="IX_TimeOutSecurity_q71")
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
	@ForeignKey(name="FK_TimeOutSecurity_q61")
	@Index(name="IX_TimeOutSecurity_q61")
	public CodeValuePhi getQ61(){
		return q61;
	}

	public void setQ61(CodeValuePhi q61){
		this.q61 = q61;
	}

	/**
	*  javadoc for q51
	*/
	private CodeValuePhi q51;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q51")
	@ForeignKey(name="FK_TimeOutSecurity_q51")
	@Index(name="IX_TimeOutSecurity_q51")
	public CodeValuePhi getQ51(){
		return q51;
	}

	public void setQ51(CodeValuePhi q51){
		this.q51 = q51;
	}

	/**
	*  javadoc for q34
	*/
	private String q34;

	@Column(name="q34",length=2500)
	public String getQ34(){
		return q34;
	}

	public void setQ34(String q34){
		this.q34 = q34;
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
	*  javadoc for q21
	*/
	private CodeValuePhi q21;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q21")
	@ForeignKey(name="FK_TimeOutSecurity_q21")
	@Index(name="IX_TimeOutSecurity_q21")
	public CodeValuePhi getQ21(){
		return q21;
	}

	public void setQ21(CodeValuePhi q21){
		this.q21 = q21;
	}

	/**
	*  javadoc for q11
	*/
	private CodeValuePhi q11;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="q11")
	@ForeignKey(name="FK_TimeOutSecurity_q11")
	@Index(name="IX_TimeOutSecurity_q11")
	public CodeValuePhi getQ11(){
		return q11;
	}

	public void setQ11(CodeValuePhi q11){
		this.q11 = q11;
	}


//	/**
//	*  javadoc for surgicalSecurityCheck
//	*/
//	private SurgicalSecurityCheck surgicalSecurityCheck;
//
//	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="surgical_security_check_id")
//	@ForeignKey(name="FK_TmtScrty_srgcalScrtyChck")
//	@Index(name="IX_TmtScrty_srgcalScrtyChck")
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TimeOutSecurity_sequence")
	@SequenceGenerator(name = "TimeOutSecurity_sequence", sequenceName = "TimeOutSecurity_sequence")
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
	@ForeignKey(name="FK_TimeOutSecurity_cnclldByRol")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_TimeOutSecurity_cnclldByRol")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_TimeOutSecurity_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_TimeOutSecurity_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
	this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@ForeignKey(name="FK_TimeOutSecurity_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_TimeOutSecurity_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_TimeOutSecurity_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_TimeOutSecurity_author")
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
	@ForeignKey(name="FK_TimeOutSecurity_lastAth")
	@Index(name="IX_TimeOutSecurity_lastAth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}

}
