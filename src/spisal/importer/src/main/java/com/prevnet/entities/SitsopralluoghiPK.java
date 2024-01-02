package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SitsopralluoghiPK implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idsopralluoghi;
	private long idutente;

	public SitsopralluoghiPK() {
	}


	@Column(insertable=false, updatable=false)
	public long getIdsopralluoghi() {
		return this.idsopralluoghi;
	}

	public void setIdsopralluoghi(long sopralluoghidip) {
		this.idsopralluoghi = sopralluoghidip;
	}
	
	@Column(insertable=false, updatable=false)
	public long getIdutente() {
		return this.idutente;
	}

	public void setIdutente(long utenti) {
		this.idutente = utenti;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SitsopralluoghiPK)) {
			return false;
		}
		SitsopralluoghiPK castOther = (SitsopralluoghiPK)other;
		return 
			(this.idsopralluoghi == castOther.idsopralluoghi)
			&& (this.idutente == castOther.idutente);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idsopralluoghi ^ (this.idsopralluoghi >>> 32)));
		hash = hash * prime + ((int) (this.idutente ^ (this.idutente >>> 32)));
		
		return hash;
	}	

}