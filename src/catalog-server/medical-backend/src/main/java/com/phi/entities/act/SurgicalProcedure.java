package com.phi.entities.act;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

@javax.persistence.Entity
@Table(name = "surgical_procedure")
public class SurgicalProcedure extends AbstractEncSurgProcedure {

	private static final long serialVersionUID = -7892633179573300676L;

	private LisaSurgicalIntervention surgicalIntervention;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="surg_interv")
	@ForeignKey(name="FK_SurgInterv")
	@Index(name="IX_SurgInterv")
	public LisaSurgicalIntervention getSurgicalIntervention() {
		return surgicalIntervention;
	}

	public void setSurgicalIntervention(LisaSurgicalIntervention surgicalIntervention) {
		this.surgicalIntervention = surgicalIntervention;
	}
	
	private PatientEncounter patientEncounter;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_SurgProc_PatEnc")
	@Index(name="IX_SurgProc_PatEnc")
	public PatientEncounter getPatientEncounter() {
	    return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter param) {
	    this.patientEncounter = param;
	}
	
	private Long externalId;

	@Column(name="external_id")
	public Long getExternalId() {
		return externalId;
	}

	public void setExternalId(Long externalId) {
		this.externalId = externalId;
	}
}