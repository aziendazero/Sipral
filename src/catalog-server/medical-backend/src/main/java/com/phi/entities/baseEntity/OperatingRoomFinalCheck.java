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
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "operating_room_final_check")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class OperatingRoomFinalCheck extends AuditedEntity {

	private static final long serialVersionUID = 1629529144L;
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "operating_room_final_check_seq")
	@SequenceGenerator(name = "operating_room_final_check_seq", sequenceName = "operating_room_final_check_seq")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	protected Boolean confirmed;

	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}
	
	private Boolean q01;

	@Column(name="q1")
	public Boolean getQ01() {
		return q01;
	}

	public void setQ01(Boolean q01) {
		this.q01 = q01;
	}
	
	private String q02;

	@Column(name="q2")
	public String getQ02() {
		return q02;
	}

	public void setQ02(String q02) {
		this.q02 = q02;
	}
	
	private Boolean q03;
	
	@Column(name="q3")
	public Boolean getQ03() {
		return q03;
	}

	public void setQ03(Boolean q03) {
		this.q03 = q03;
	}
	
	private Boolean q04;

	@Column(name="q4")
	public Boolean getQ04() {
		return q04;
	}
	
	public void setQ04(Boolean q04) {
		this.q04 = q04;
	}
	
	private Boolean q05;

	@Column(name="q5")
	public Boolean getQ05() {
		return q05;
	}
	
	public void setQ05(Boolean q05) {
		this.q05 = q05;
	}
	
	private Boolean q06;

	@Column(name="q6")
	public Boolean getQ06() {
		return q06;
	}
	
	public void setQ06(Boolean q06) {
		this.q06 = q06;
	}
	
	private Boolean q07;

	@Column(name="q7")
	public Boolean getQ07() {
		return q07;
	}
	
	public void setQ07(Boolean q07) {
		this.q07 = q07;
	}
	
	private Boolean q08;

	@Column(name="q8")
	public Boolean getQ08() {
		return q08;
	}
	
	public void setQ08(Boolean q08) {
		this.q08 = q08;
	}
	
	private Boolean q09;

	@Column(name="q9")
	public Boolean getQ09() {
		return q09;
	}
	
	public void setQ09(Boolean q09) {
		this.q09 = q09;
	}
	
	private Boolean q10;

	@Column(name="q10")
	public Boolean getQ10() {
		return q10;
	}
	
	public void setQ10(Boolean q10) {
		this.q10 = q10;
	}
	
	private Boolean q11;

	@Column(name="q11")
	public Boolean getQ11() {
		return q11;
	}
	
	public void setQ11(Boolean q11) {
		this.q11 = q11;
	}
	
	private Boolean q12;

	@Column(name="q12")
	public Boolean getQ12() {
		return q12;
	}
	
	public void setQ12(Boolean q12) {
		this.q12 = q12;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != HibernateProxyHelper.getClassWithoutInitializingProxy(obj))
			return false;
		
		if (obj instanceof HibernateProxy) {
			obj = ((HibernateProxy)obj).getHibernateLazyInitializer().getImplementation();
		}

		OperatingRoomFinalCheck other = (OperatingRoomFinalCheck) obj;
		if (q01 == null) {
			if (other.q01 != null)
				return false;
		} else if (!q01.equals(other.q01))
			return false;
		if (q02 == null) {
			if (other.q02 != null)
				return false;
		} else if (!q02.equals(other.q02))
			return false;
		if (q03 == null) {
			if (other.q03 != null)
				return false;
		} else if (!q03.equals(other.q03))
			return false;
		if (q04 == null) {
			if (other.q04 != null)
				return false;
		} else if (!q04.equals(other.q04))
			return false;
		if (q05 == null) {
			if (other.q05 != null)
				return false;
		} else if (!q05.equals(other.q05))
			return false;
		if (q06 == null) {
			if (other.q06 != null)
				return false;
		} else if (!q06.equals(other.q06))
			return false;
		if (q07 == null) {
			if (other.q07 != null)
				return false;
		} else if (!q07.equals(other.q07))
			return false;
		if (q08 == null) {
			if (other.q08 != null)
				return false;
		} else if (!q08.equals(other.q08))
			return false;
		if (q09 == null) {
			if (other.q09 != null)
				return false;
		} else if (!q09.equals(other.q09))
			return false;
		if (q10 == null) {
			if (other.q10 != null)
				return false;
		} else if (!q10.equals(other.q10))
			return false;
		if (q11 == null) {
			if (other.q11 != null)
				return false;
		} else if (!q11.equals(other.q11))
			return false;
		if (q12 == null) {
			if (other.q12 != null)
				return false;
		} else if (!q12.equals(other.q12))
			return false;
		return true;
	}
	
	//methods needed for AuditedEntity extension
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@JoinColumn(name="cancelledByRole")
	@ForeignKey(name="FK_orfc_canc_by_role")
	@Index(name="IX_orfc_canc_by_role")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cancelled_by_id")
	@ForeignKey(name="FK_orfc_canc_By")
	@Index(name="IX_orfc_canc_By")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueRole.class)
	@JoinColumn(name="authorRole")
	@ForeignKey(name="FK_orfc_auth_Role")
	@Index(name="IX_orfc_auth_Role")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_orfc_author")
	@Index(name="IX_orfc_author")
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
	@ForeignKey(name="FK_orfc_last_Auth")
	@Index(name="IX_orfc_last_Auth")
	public Employee getLastAuthor() {
		return lastAuthor;
	}
	
	public void setLastAuthor(Employee lastAuthor) {
		this.lastAuthor = lastAuthor;
	}
	
}