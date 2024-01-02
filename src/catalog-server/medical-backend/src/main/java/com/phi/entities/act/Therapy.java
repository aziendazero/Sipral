package com.phi.entities.act;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.GenericRequest;
import com.phi.entities.baseEntity.Prescription;
import com.phi.entities.baseEntity.PrescriptionDischarge;
import com.phi.entities.role.Employee;
import com.phi.json.JsonProxyGenerator;

@Entity
@Table(name = "therapy")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=Therapy.class)
public class Therapy extends BaseEntity {

	private static final long serialVersionUID = -690919426120935735L;
	
	//methods needed for BaseEntity extension
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Therapy_sequence")
	@SequenceGenerator(name = "Therapy_sequence", sequenceName = "Therapy_sequence")
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
	@ForeignKey(name = "FK_Therapy_sdloc")
	@Index(name = "IX_Therapy_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/


	private PatientEncounter patientEncounter;

	//@JsonBackReference(value="therapy")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_encounter")
	@ForeignKey(name = "FK_Therapy_Pat_Enc")
	@Index(name="IX_Therapy_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter param) {
		this.patientEncounter = param;
	}
	
	/**
	*  javadoc for author
	*/
	private Employee author;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Therapy_author")
	@Index(name="IX_Therapy_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}

	private List<Prescription> prescription;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "therapy")
	public List<Prescription> getPrescription() {
		return prescription;
	}

	public void setPrescription(List<Prescription> param) {
		this.prescription = param;
	}

	/**
	 * java doc for PrescriptionDischarge
	 */
	private List<PrescriptionDischarge> prescriptionDischarge;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "therapy")
	public List<PrescriptionDischarge> getPrescriptionDischarge() {
		return prescriptionDischarge;
	}

	public void setPrescriptionDischarge(List<PrescriptionDischarge> param) {
		this.prescriptionDischarge = param;
	}
	
	private List<GenericRequest> supplyRequest;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "therapy", cascade = CascadeType.PERSIST)
	public List<GenericRequest> getSupplyRequest() {
		return supplyRequest;
	}

	public void setSupplyRequest(List<GenericRequest> list) {
		this.supplyRequest = list;
	}

	public void addSupplyRequest(GenericRequest supplyRequest) {
		if (this.supplyRequest == null) {
			this.supplyRequest = new ArrayList<GenericRequest>();
		}
		// add the association
		if(!this.supplyRequest.contains(supplyRequest)) {
			this.supplyRequest.add(supplyRequest);
			// make the inverse link
			supplyRequest.setTherapy(this);
		}
	}

	public void removeSupplyRequest(GenericRequest supplyRequest) {
		if (this.supplyRequest == null) {
			this.supplyRequest = new ArrayList<GenericRequest>();
			return;
		}
		//add the association
		if(this.supplyRequest.contains(supplyRequest)){
			this.supplyRequest.remove(supplyRequest);
			//make the inverse link
			supplyRequest.setTherapy(null);
		}

	}
}