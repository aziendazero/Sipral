package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class AttipolgiudsopralluoghiPK implements Serializable {
	private static final long serialVersionUID = 1L;

	private long idsopralluogo;
	private long idattopolgiud;

	public AttipolgiudsopralluoghiPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdsopralluogo() {
		return this.idsopralluogo;
	}

	public void setIdsopralluogo(long idsopralluogo) {
		this.idsopralluogo = idsopralluogo;
	}

	@Column(insertable=false, updatable=false)
	public long getIdattopolgiud() {
		return this.idattopolgiud;
	}

	public void setIdattopolgiud(long idattopolgiud	) {
		this.idattopolgiud = idattopolgiud;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AttipolgiudsopralluoghiPK)) {
			return false;
		}
		AttipolgiudsopralluoghiPK castOther = (AttipolgiudsopralluoghiPK)other;
		return 
			(this.idsopralluogo == castOther.idsopralluogo)
			&& (this.idattopolgiud == castOther.idattopolgiud);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idsopralluogo ^ (this.idsopralluogo >>> 32)));
		hash = hash * prime + ((int) (this.idattopolgiud ^ (this.idattopolgiud >>> 32)));
		
		return hash;
	}	

}