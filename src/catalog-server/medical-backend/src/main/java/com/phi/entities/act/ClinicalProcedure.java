package com.phi.entities.act;

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

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;

/**
 * Superclass of Procedure, SubstanceAdministration and LEPActivity
 * @author alex.zupan
 */

@javax.persistence.Entity
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ClinicalProcedure extends AuditedEntity {

	private static final long serialVersionUID = 6792434794986962929L;


	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ClinicalProcedure_sequence")
	@SequenceGenerator(name = "ClinicalProcedure_sequence", sequenceName = "ClinicalProcedure_sequence")
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
	@ForeignKey(name = "FK_ClinicalProcedure_sdloc")
	@Index(name="IX_CliProc_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/

	private CodeValue statusCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="status_code")
	@ForeignKey(name="FK_ClinProc_sc")
	@Index(name="IX_ClinProc_sc") 
	public CodeValue getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(CodeValue statusCode) {
		this.statusCode = statusCode;
	}

	//-------------from auditedEntity
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_ClinProc_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_ClinProc_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_ClinProc_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_ClinProc_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_ClinProc_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_ClinProc_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_ClinProc_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_ClinProc_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}

}
