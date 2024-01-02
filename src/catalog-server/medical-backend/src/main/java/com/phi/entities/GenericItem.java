package com.phi.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

/**
 * Used for storing generic information in db
 */

@Entity
@Table(name = "genericitem")
@Audited
public class GenericItem  implements Serializable{
	
	private static final long serialVersionUID = -1239866740516905393L;
	
	private String internalId;
	private String datavalue;
	
	@Id
	@Column(name = "internalId")
	public String getInternalId() {
		return internalId;
	}

	public void setInternalId(String internalId) {
		this.internalId = internalId;
	}

	@Column(name = "datavalue", length = 45)
	public String getDatavalue() {
		return datavalue;
	}

	public void setDatavalue(String datavalue) {
		this.datavalue = datavalue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((datavalue == null) ? 0 : datavalue.hashCode());
		result = prime * result
				+ ((internalId == null) ? 0 : internalId.hashCode());
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
		GenericItem other = (GenericItem) obj;
		if (datavalue == null) {
			if (other.datavalue != null)
				return false;
		} else if (!datavalue.equals(other.datavalue))
			return false;
		if (internalId == null) {
			if (other.internalId != null)
				return false;
		} else if (!internalId.equals(other.internalId))
			return false;
		return true;
	}

}