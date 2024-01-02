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

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.act.PatientEncounter;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public abstract class PhysicalExam extends BaseEntity {

	private static final long serialVersionUID = 1473074113L;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PhysicalExam_sequence")
	@SequenceGenerator(name = "PhysicalExam_sequence", sequenceName = "PhysicalExam_sequence")
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
	@ForeignKey(name = "FK_PhysicalExam_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	/**
	*  javadoc for patient
	*/
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_PhyscalExam_patient")
	@Index(name="IX_PhyscalExam_patient")
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

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patientEncounter_id")
	@ForeignKey(name="FK_PhyExm_ptentEnc")
	@Index(name="IX_PhyExm_ptentEnc")
	public PatientEncounter getEncounter(){
		return patientEncounter;
	}

	public void setEncounter(PatientEncounter patientEncounter){
		this.patientEncounter = patientEncounter;
	}
	
	/**
	*  javadoc for author
	*/
	private Employee author;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_PhyExm_auth")
	@Index(name="IX_PhyExm_auth")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}
}