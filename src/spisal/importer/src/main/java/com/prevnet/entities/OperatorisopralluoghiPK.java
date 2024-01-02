package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class OperatorisopralluoghiPK implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idoperatore;
	private long idsopralluogo;

	public OperatorisopralluoghiPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdoperatore() {
		return this.idoperatore;
	}

	public void setIdoperatore(long idoperatore) {
		this.idoperatore = idoperatore;
	}

	@Column(insertable=false, updatable=false)
	public long getIdsopralluogo() {
		return this.idsopralluogo;
	}

	public void setIdsopralluogo(long sopralluoghidip) {
		this.idsopralluogo = sopralluoghidip;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof OperatorisopralluoghiPK)) {
			return false;
		}
		OperatorisopralluoghiPK castOther = (OperatorisopralluoghiPK)other;
		return 
			(this.idsopralluogo == castOther.idsopralluogo)
			&& (this.idoperatore == castOther.idoperatore);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idsopralluogo ^ (this.idsopralluogo >>> 32)));
		hash = hash * prime + ((int) (this.idoperatore ^ (this.idoperatore >>> 32)));
		
		return hash;
	}	
}