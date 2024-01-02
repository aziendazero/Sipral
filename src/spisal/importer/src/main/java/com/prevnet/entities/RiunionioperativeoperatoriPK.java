package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the RIUNIONIOPERATIVEOPERATORI database table.
 * 
 */
@Embeddable
public class RiunionioperativeoperatoriPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idriunione;
	private long idoperatore;

	public RiunionioperativeoperatoriPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdriunione() {
		return this.idriunione;
	}
	public void setIdriunione(long idriunione) {
		this.idriunione = idriunione;
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
		if (!(other instanceof RiunionioperativeoperatoriPK)) {
			return false;
		}
		RiunionioperativeoperatoriPK castOther = (RiunionioperativeoperatoriPK)other;
		return 
			(this.idriunione == castOther.idriunione)
			&& (this.idoperatore == castOther.idoperatore);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idriunione ^ (this.idriunione >>> 32)));
		hash = hash * prime + ((int) (this.idoperatore ^ (this.idoperatore >>> 32)));
		
		return hash;
	}
}