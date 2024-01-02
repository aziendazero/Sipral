package com.phi.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Id of SequenceValue entity
 *   name - name of the sequence
 *   date - can be no or for a daily seq is the date in format: DD/MM/YYYY or for a monthly seq is the date in format: MM/YYYY  or for a yearly seq is the date in format: YYYY
 *   owner - can be all or id of an owner
 * 
 * @author Zupan
 */

@Embeddable
public class SequenceValueId implements Serializable {
	
	private static final long serialVersionUID = 6878111892651322986L;
	
	private String name;
	private String date;
	private String owner;
	
	public SequenceValueId() { }
	
	public SequenceValueId(String name, String date, String owner) { 
		this.name = name;
		this.date = date;
		this.owner = owner;
	}
	
    @Column(name = "NAME_DB", nullable = false)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
    @Column(name = "DATE_DB", nullable = false)
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
    @Column(name = "OWNER", nullable = false)
	public String getOwner() {
		return owner;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		SequenceValueId other = (SequenceValueId) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}
	
}