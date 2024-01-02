package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the COMMITTENTICANTIERE database table.
 * 
 */
@Embeddable
public class CommittenticantierePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idcantiere;
	private long idcommittenti;

	public CommittenticantierePK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdcantiere() {
		return this.idcantiere;
	}
	public void setIdcantiere(long idcantiere) {
		this.idcantiere = idcantiere;
	}

	@Column(insertable=false, updatable=false)
	public long getIdcommittenti() {
		return this.idcommittenti;
	}
	public void setIdcommittenti(long idcommittenti) {
		this.idcommittenti = idcommittenti;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CommittenticantierePK)) {
			return false;
		}
		CommittenticantierePK castOther = (CommittenticantierePK)other;
		return 
			(this.idcantiere == castOther.idcantiere)
			&& (this.idcommittenti == castOther.idcommittenti);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idcantiere ^ (this.idcantiere >>> 32)));
		hash = hash * prime + ((int) (this.idcommittenti ^ (this.idcommittenti >>> 32)));
		
		return hash;
	}
}