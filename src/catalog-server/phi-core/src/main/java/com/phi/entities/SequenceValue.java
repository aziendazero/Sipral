package com.phi.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity bean that contains sequence values. Used by SequenceManager to generate sequence numbers.
 * 
 * Fields:
 *   id.name - name of the sequence
 *   id.date - can be notDependant or for a daily seq is the date in format: DD/MM/YYYY or for a monthly seq is the date in format: MM/YYYY  or for a yearly seq is the date in format: YYYY
 *   id.owner - can be ALL OWNERS or id of an owner
 *   value - last value of the sequence
 * 
 * @author Zupan
 */

@Entity
@Table(name = "sequence_value")
public class SequenceValue implements Serializable {

	private static final long serialVersionUID = -7546128443173828137L;
	
	private SequenceValueId id;
	private Long value;
	
	public SequenceValue() { }
	
    @EmbeddedId
	public SequenceValueId getId() {
		return id;
	}

	public void setId(SequenceValueId id) {
		this.id = id;
	}
	
	@Column(name = "VALUE_DB", nullable = false)
	public Long getValue() {
		return value;
	}
	
	public void setValue(Long value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		SequenceValue other = (SequenceValue) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}