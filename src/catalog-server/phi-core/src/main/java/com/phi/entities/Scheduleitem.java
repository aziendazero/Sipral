package com.phi.entities;

// Generated Feb 9, 2009 10:44:52 AM by Hibernate Tools 3.2.2.GA

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Represent a scheduled activity with quartz.
 * 
 * Old implementation with String id, to be reimplemented/revamped...
 */

@Deprecated
//@Entity
//@Table(name = "scheduleitem")
//@Audited
public class Scheduleitem implements java.io.Serializable {

	private static final long serialVersionUID = -5292715356080786764L;

	private long internalId;
	
	private Date startDate;
	
	private Date endDate;
	
	private String actAppointmentId;
	
	private String patientId;
	
	private String scheduleType;
	
	private String scheduleSubType;
	
	private String triggerName;
	
	private String triggerGroup;
	
	private Long frequency;
	private String frequencyCron;
	private Boolean active;
	private Boolean suspended;

	public Scheduleitem(){
	}
	
	public Scheduleitem(long internalId,Date startDate, Date endDate, String actAppointmentId, String patientId,
			String triggerName, String triggerGroup, Long frequency, String frequencyCron,
			Boolean active, String scheduleType, String scheduleSubType, Boolean suspended) {
		this.internalId = internalId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.actAppointmentId = actAppointmentId;
		this.patientId = patientId;
		this.triggerName = triggerName;
		this.triggerGroup = triggerGroup;
		this.frequency = frequency;
		this.frequencyCron = frequencyCron;
		this.active = active;
		this.scheduleType = scheduleType;
		this.scheduleSubType = scheduleSubType;
		this.suspended = suspended;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "scheduleitem_sequence")
    @SequenceGenerator(name = "scheduleitem_sequence", sequenceName = "scheduleitem_sequence")
	@Column(name = "internalId")
	public long getInternalId() {
		return this.internalId;
	}

	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	@Column(name = "startDate")
	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Column(name = "endDate")
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column(name = "actAppointmentId")
	public String getActAppointmentId() {
		return this.actAppointmentId;
	}

	public void setActAppointmentId(String actAppointmentId) {
		this.actAppointmentId = actAppointmentId;
	}

	@Column(name = "triggerName")
	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}

	@Column(name = "triggerGroup")
	public String getTriggerGroup() {
		return triggerGroup;
	}

	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}

	@Column(name = "frequency")
	public Long getFrequency() {
		return this.frequency;
	}

	public void setFrequency(Long frequency) {
		this.frequency = frequency;
	}

	@Column(name = "frequencyCron")
	public String getFrequencyCron() {
		return this.frequencyCron;
	}

	public void setFrequencyCron(String frequencyCron) {
		this.frequencyCron = frequencyCron;
	}	

	@Column(name = "active")
	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Column(name = "scheduleType")
	public String getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(String scheduleType) {
		this.scheduleType = scheduleType;
	}

	@Column(name = "scheduleSubType")
	public String getScheduleSubType() {
		return scheduleSubType;
	}

	public void setScheduleSubType(String scheduleSubType) {
		this.scheduleSubType = scheduleSubType;
	}

	@Column(name = "suspended")
	public Boolean getSuspended() {
		return suspended;
	}

	public void setSuspended(Boolean suspended) {
		this.suspended = suspended;
	}

	@Column(name = "patientId")
	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	@Override
	public String toString() {
		return "ScheduleitemId: " + internalId + " patientId: " + patientId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((actAppointmentId == null) ? 0 : actAppointmentId.hashCode());
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
		result = prime * result
				+ ((frequency == null) ? 0 : frequency.hashCode());
		result = prime * result
				+ ((frequencyCron == null) ? 0 : frequencyCron.hashCode());
		result = prime * result + (int) (internalId ^ (internalId >>> 32));
		result = prime * result
				+ ((patientId == null) ? 0 : patientId.hashCode());
		result = prime * result
				+ ((scheduleSubType == null) ? 0 : scheduleSubType.hashCode());
		result = prime * result
				+ ((scheduleType == null) ? 0 : scheduleType.hashCode());
		result = prime * result
				+ ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
				+ ((suspended == null) ? 0 : suspended.hashCode());
		result = prime * result
				+ ((triggerGroup == null) ? 0 : triggerGroup.hashCode());
		result = prime * result
				+ ((triggerName == null) ? 0 : triggerName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Scheduleitem other = (Scheduleitem) obj;
		if (actAppointmentId == null) {
			if (other.actAppointmentId != null)
				return false;
		} else if (!actAppointmentId.equals(other.actAppointmentId))
			return false;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (endDate == null) {
			if (other.endDate != null)
				return false;
		} else if (!endDate.equals(other.endDate))
			return false;
		if (frequency == null) {
			if (other.frequency != null)
				return false;
		} else if (!frequency.equals(other.frequency))
			return false;
		if (frequencyCron == null) {
			if (other.frequencyCron != null)
				return false;
		} else if (!frequencyCron.equals(other.frequencyCron))
			return false;
		if (internalId != other.internalId)
			return false;
		if (patientId == null) {
			if (other.patientId != null)
				return false;
		} else if (!patientId.equals(other.patientId))
			return false;
		if (scheduleSubType == null) {
			if (other.scheduleSubType != null)
				return false;
		} else if (!scheduleSubType.equals(other.scheduleSubType))
			return false;
		if (scheduleType == null) {
			if (other.scheduleType != null)
				return false;
		} else if (!scheduleType.equals(other.scheduleType))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (suspended == null) {
			if (other.suspended != null)
				return false;
		} else if (!suspended.equals(other.suspended))
			return false;
		if (triggerGroup == null) {
			if (other.triggerGroup != null)
				return false;
		} else if (!triggerGroup.equals(other.triggerGroup))
			return false;
		if (triggerName == null) {
			if (other.triggerName != null)
				return false;
		} else if (!triggerName.equals(other.triggerName))
			return false;
		return true;
	}

}