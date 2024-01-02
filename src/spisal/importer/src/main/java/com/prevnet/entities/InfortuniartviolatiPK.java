package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the INFORTUNIARTVIOLATI database table.
 * 
 */
@Embeddable
public class InfortuniartviolatiPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idinfortunioindagato;
	private long idarticololegge;

	public InfortuniartviolatiPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdinfortunioindagato() {
		return this.idinfortunioindagato;
	}
	public void setIdinfortunioindagato(long idinfortunioindagato) {
		this.idinfortunioindagato = idinfortunioindagato;
	}

	@Column(insertable=false, updatable=false)
	public long getIdarticololegge() {
		return this.idarticololegge;
	}
	public void setIdarticololegge(long idarticololegge) {
		this.idarticololegge = idarticololegge;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof InfortuniartviolatiPK)) {
			return false;
		}
		InfortuniartviolatiPK castOther = (InfortuniartviolatiPK)other;
		return 
			(this.idinfortunioindagato == castOther.idinfortunioindagato)
			&& (this.idarticololegge == castOther.idarticololegge);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idinfortunioindagato ^ (this.idinfortunioindagato >>> 32)));
		hash = hash * prime + ((int) (this.idarticololegge ^ (this.idarticololegge >>> 32)));
		
		return hash;
	}
}