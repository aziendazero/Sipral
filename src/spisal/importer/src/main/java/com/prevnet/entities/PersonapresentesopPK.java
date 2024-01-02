package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class PersonapresentesopPK implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idpersonapresentesop;
	private long idsopralluogo;

	public PersonapresentesopPK() {
	}


	public long getIdpersonapresentesop() {
		return this.idpersonapresentesop;
	}

	public void setIdpersonapresentesop(long idpersonapresentesop) {
		this.idpersonapresentesop = idpersonapresentesop;
	}


	@Column(insertable=false, updatable=false)
	public long getIdsopralluogo() {
		return this.idsopralluogo;
	}

	public void setIdsopralluogo(long idsopralluogo) {
		this.idsopralluogo = idsopralluogo;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PersonapresentesopPK)) {
			return false;
		}
		PersonapresentesopPK castOther = (PersonapresentesopPK)other;
		return 
			(this.idsopralluogo == castOther.idsopralluogo)
			&& (this.idpersonapresentesop == castOther.idpersonapresentesop);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idsopralluogo ^ (this.idsopralluogo >>> 32)));
		hash = hash * prime + ((int) (this.idpersonapresentesop ^ (this.idpersonapresentesop >>> 32)));
		
		return hash;
	}	
}