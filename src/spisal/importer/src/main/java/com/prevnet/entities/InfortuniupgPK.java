package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the INFORTUNIUPG database table.
 * 
 */
@Embeddable
public class InfortuniupgPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idinfortunio;
	private long idoperatore;

	public InfortuniupgPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdinfortunio() {
		return this.idinfortunio;
	}
	public void setIdinfortunio(long idinfortunio) {
		this.idinfortunio = idinfortunio;
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
		if (!(other instanceof InfortuniupgPK)) {
			return false;
		}
		InfortuniupgPK castOther = (InfortuniupgPK)other;
		return 
			(this.idinfortunio == castOther.idinfortunio)
			&& (this.idoperatore == castOther.idoperatore);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idinfortunio ^ (this.idinfortunio >>> 32)));
		hash = hash * prime + ((int) (this.idoperatore ^ (this.idoperatore >>> 32)));
		
		return hash;
	}
}