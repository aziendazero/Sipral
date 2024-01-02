package com.phi.entities.dataTypes;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.envers.Audited;

@Embeddable
@Audited
public class PQ extends ANY implements Serializable {

	private static final long serialVersionUID = 8405816903156305086L;

	protected Double value;
	protected String unit;

	public PQ() {
	}

	public PQ(Double value, String unit) {
		this.value=value;
		this.unit=unit;
	}

	@Column(name="valueDB")
	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	@Column(name="unit")
	public String getUnit() {
		return unit;
	}
	
	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String toString() {
		if (value == null) {
			return "";
		}
		if (unit != null) {
			return value + " " + unit;
		} else {
			return value.toString();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
		PQ other = (PQ) obj;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	public PQ clonePq() {

		PQ cloned = new PQ();
		cloned.value=value;
		cloned.unit=unit;
		return cloned;
	}

}