package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DLGS758GIORNIPROROGA database table.
 * 
 */
@Embeddable
public class Dlgs758giorniprorogaPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idartprovv;
	private long ordine;

	public Dlgs758giorniprorogaPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdartprovv() {
		return this.idartprovv;
	}
	public void setIdartprovv(long idartprovv) {
		this.idartprovv = idartprovv;
	}

	public long getOrdine() {
		return this.ordine;
	}
	public void setOrdine(long ordine) {
		this.ordine = ordine;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Dlgs758giorniprorogaPK)) {
			return false;
		}
		Dlgs758giorniprorogaPK castOther = (Dlgs758giorniprorogaPK)other;
		return 
			(this.idartprovv == castOther.idartprovv)
			&& (this.ordine == castOther.ordine);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idartprovv ^ (this.idartprovv >>> 32)));
		hash = hash * prime + ((int) (this.ordine ^ (this.ordine >>> 32)));
		
		return hash;
	}
}