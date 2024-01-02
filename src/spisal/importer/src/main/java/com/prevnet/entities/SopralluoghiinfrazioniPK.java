package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class SopralluoghiinfrazioniPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private long idsopralluogo;
	private long idinfrazione;

	public SopralluoghiinfrazioniPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdsopralluogo() {
		return this.idsopralluogo;
	}

	public void setIdsopralluogo(long sopralluoghidip) {
		this.idsopralluogo = sopralluoghidip;
	}

	@Column(insertable=false, updatable=false)
	public long getIdinfrazione() {
		return this.idinfrazione;
	}

	public void setIdinfrazione(long tabelle) {
		this.idinfrazione = tabelle;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SopralluoghiinfrazioniPK)) {
			return false;
		}
		SopralluoghiinfrazioniPK castOther = (SopralluoghiinfrazioniPK)other;
		return 
			(this.idsopralluogo == castOther.idsopralluogo)
			&& (this.idinfrazione == castOther.idinfrazione);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idsopralluogo ^ (this.idsopralluogo >>> 32)));
		hash = hash * prime + ((int) (this.idinfrazione ^ (this.idinfrazione >>> 32)));
		
		return hash;
	}	

}