package com.phi.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity bean for sequence configuration. Used by SequenceManager to generate sequence numbers.
 * 
 * Fields: 
 *   name - name of the sequence
 *   type - type can be simple, sequence, complex
 *   description - description of the sequence
 *   timeBase - can be no, daily, monthly, yearly
 *   timeBasePattern - date pattern for timeBased sequences
 *   ownerDependant - boolean, if true the sequence value query will search for a sequence that depends on current owners of the logged user
 * 
 * @author Zupan
 */

@Entity
@Table(name = "sequence_configuration")
public class SequenceConfiguration implements Serializable {

	private static final long serialVersionUID = -4286517620660598018L;
	
	private String name;
	private String type;
	private String description;
	private String timeBase;
	private String timeBasePattern;
	private Boolean ownerDependant;
	private Boolean callProcedure;
	
	public SequenceConfiguration() { }
	
    @Id
    @Column(name = "NAME_DB", nullable = false)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "TYPE_DB", nullable = false)
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "DESCRIPTION_DB", nullable = false)
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "TIME_BASE", nullable = false)
	public String getTimeBase() {
		return timeBase;
	}
	
	public void setTimeBase(String timeBase) {
		this.timeBase = timeBase;
	}
	
	@Column(name = "TIME_BASE_PATTERN")
	public String getTimeBasePattern() {
		return timeBasePattern;
	}
	
	public void setTimeBasePattern(String timeBasePattern) {
		this.timeBasePattern = timeBasePattern;
	}
	
	@Column(name = "OWNED_DEPENDANT", nullable = false)
	public Boolean getOwnerDependant() {
		return ownerDependant;
	}
	
	public void setOwnerDependant(Boolean ownerDependant) {
		this.ownerDependant = ownerDependant;
	}
	
	
	@Column(name = "CALL_PROCEDURE")
	public Boolean getCallProcedure() {
		return callProcedure;
	}

	public void setCallProcedure(Boolean callProcedure) {
		this.callProcedure = callProcedure;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ownerDependant == null) ? 0 : ownerDependant.hashCode());
		result = prime * result + ((timeBase == null) ? 0 : timeBase.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((callProcedure == null) ? 0 : callProcedure.hashCode());
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
		SequenceConfiguration other = (SequenceConfiguration) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ownerDependant == null) {
			if (other.ownerDependant != null)
				return false;
		} else if (!ownerDependant.equals(other.ownerDependant))
			return false;
		if (timeBase == null) {
			if (other.timeBase != null)
				return false;
		} else if (!timeBase.equals(other.timeBase))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (callProcedure == null) {
			if (other.callProcedure != null)
				return false;
		} else if (!callProcedure.equals(other.callProcedure))
			return false;
		return true;
	}
	
}