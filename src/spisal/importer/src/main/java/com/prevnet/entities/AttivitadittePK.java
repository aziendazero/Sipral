package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the ATTIVITADITTE database table.
 * 
 */
@Embeddable
public class AttivitadittePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idditta;
	private long idattivita;

	public AttivitadittePK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdditta() {
		return this.idditta;
	}
	public void setIdditta(long idditta) {
		this.idditta = idditta;
	}

	@Column(insertable=false, updatable=false)
	public long getIdattivita() {
		return this.idattivita;
	}
	public void setIdattivita(long idattivita) {
		this.idattivita = idattivita;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AttivitadittePK)) {
			return false;
		}
		AttivitadittePK castOther = (AttivitadittePK)other;
		return 
			(this.idditta == castOther.idditta)
			&& (this.idattivita == castOther.idattivita);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idditta ^ (this.idditta >>> 32)));
		hash = hash * prime + ((int) (this.idattivita ^ (this.idattivita >>> 32)));
		
		return hash;
	}
}