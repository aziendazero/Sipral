package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the IMPRESECANTIERE database table.
 * 
 */
@Embeddable
public class ImpresecantierePK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idcantiere;
	private long idimprese;

	public ImpresecantierePK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdcantiere() {
		return this.idcantiere;
	}
	public void setIdcantiere(long idcantiere) {
		this.idcantiere = idcantiere;
	}

	@Column(insertable=false, updatable=false)
	public long getIdimprese() {
		return this.idimprese;
	}
	public void setIdimprese(long idimprese) {
		this.idimprese = idimprese;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ImpresecantierePK)) {
			return false;
		}
		ImpresecantierePK castOther = (ImpresecantierePK)other;
		return 
			(this.idcantiere == castOther.idcantiere)
			&& (this.idimprese == castOther.idimprese);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idcantiere ^ (this.idcantiere >>> 32)));
		hash = hash * prime + ((int) (this.idimprese ^ (this.idimprese >>> 32)));
		
		return hash;
	}
}