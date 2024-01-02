package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the PARERIGENERICIALTRIOP database table.
 * 
 */
@Embeddable
public class ParerigenericialtriopPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idpareregenerico;
	private long idoperatore;

	public ParerigenericialtriopPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdpareregenerico() {
		return this.idpareregenerico;
	}
	public void setIdpareregenerico(long idpareregenerico) {
		this.idpareregenerico = idpareregenerico;
	}

	@Column(insertable=false, updatable=false)
	public long getIdoperatore() {
		return this.idoperatore;
	}
	public void setIdoperatore(long idoperatore) {
		this.idoperatore = idoperatore;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ParerigenericialtriopPK)) {
			return false;
		}
		ParerigenericialtriopPK castOther = (ParerigenericialtriopPK)other;
		return 
			(this.idpareregenerico == castOther.idpareregenerico)
			&& (this.idoperatore == castOther.idoperatore);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idpareregenerico ^ (this.idpareregenerico >>> 32)));
		hash = hash * prime + ((int) (this.idoperatore ^ (this.idoperatore >>> 32)));
		
		return hash;
	}
}