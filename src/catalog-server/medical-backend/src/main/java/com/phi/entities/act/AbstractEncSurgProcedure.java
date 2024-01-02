package com.phi.entities.act;

import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

@Entity
@Audited
public abstract class AbstractEncSurgProcedure extends AbstractProcedure {

	private static final long serialVersionUID = 892480545L;

	private PatientEncounter patientEncounter;
	
	@Transient
	abstract public PatientEncounter getPatientEncounter();

	abstract public void setPatientEncounter(PatientEncounter param);


}