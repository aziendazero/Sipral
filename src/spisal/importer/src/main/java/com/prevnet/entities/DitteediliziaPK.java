package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the DITTEEDILIZIA database table.
 * 
 */
@Embeddable
public class DitteediliziaPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idsopralluogo;
	private long idditta;

	public DitteediliziaPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdsopralluogo() {
		return this.idsopralluogo;
	}
	public void setIdsopralluogo(long idsopralluogo) {
		this.idsopralluogo = idsopralluogo;
	}

	@Column(insertable=false, updatable=false)
	public long getIdditta() {
		return this.idditta;
	}
	public void setIdditta(long idditta) {
		this.idditta = idditta;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof DitteediliziaPK)) {
			return false;
		}
		DitteediliziaPK castOther = (DitteediliziaPK)other;
		return 
			(this.idsopralluogo == castOther.idsopralluogo)
			&& (this.idditta == castOther.idditta);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idsopralluogo ^ (this.idsopralluogo >>> 32)));
		hash = hash * prime + ((int) (this.idditta ^ (this.idditta >>> 32)));
		
		return hash;
	}
}