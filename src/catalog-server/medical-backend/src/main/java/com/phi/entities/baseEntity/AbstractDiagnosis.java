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
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.act.PatientEncounter;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractDiagnosis extends AuditedEntity {

	private static final long serialVersionUID = 2835648264797571500L;
	
	/**
	 *  Author
	 */
	
	protected PatientEncounter patientEncounter;
	@Transient
	public abstract PatientEncounter getPatientEncounter();
	public abstract void setPatientEncounter(PatientEncounter patientEncounter);
	
	//-------------from auditedEntity

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_AbsDiag_sdloc")
	@Index(name = "IX_AbsDiag_sdloc")
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Diagnosis_sequence")
	@SequenceGenerator(name = "Diagnosis_sequence", sequenceName = "Diagnosis_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_AbsDiag_cancelledByRole")
	@JoinColumn(name="cancelledByRole")
	@Index(name="IX_AbsDiag_cancelledByRole")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}
	@Override
	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_AbsDiag_cancelledBy")
	@JoinColumn(name="cancelled_by_id")
	@Index(name="IX_AbsDiag_cancelledBy")
	public Employee getCancelledBy(){
		return cancelledBy;
	}
	@Override
	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_AbsDiags_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_AbsDiag_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}
	@Override
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@ForeignKey(name="FK_AbsDiag_author")
	@JoinColumn(name="author_id")
	@Index(name="IX_AbsDiag_author")
	public Employee getAuthor(){
		return author;
	}
	@Override
	public void setAuthor(Employee author){
		this.author = author;
	}


}
