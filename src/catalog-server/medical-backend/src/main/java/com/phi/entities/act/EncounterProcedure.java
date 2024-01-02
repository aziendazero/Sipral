package com.phi.entities.act;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;


@Entity
@Table(name = "encounter_procedure")
@Audited
public class EncounterProcedure extends AbstractEncSurgProcedure implements LocatedEntity {

	private static final long serialVersionUID = 1090730345219948902L;
	
	protected ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_EncounterProcedure_sdloc")
	@Index(name="IX_EncounterProcedure_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}


	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
	private PatientEncounter patientEncounter;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_EncProc_Pat_Enc")
	@Index(name="IX_EncProc_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
	    return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter param) {
	    this.patientEncounter = param;
	}
}