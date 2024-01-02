package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the INFORTUNITESTIMONI database table.
 * 
 */
@Embeddable
public class InfortunitestimoniPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;
	private long idinfortunio;
	private long idanagrafica;

	public InfortunitestimoniPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdinfortunio() {
		return this.idinfortunio;
	}
	public void setIdinfortunio(long idinfortunio) {
		this.idinfortunio = idinfortunio;
	}

	@Column(insertable=false, updatable=false)
	public long getIdanagrafica() {
		return this.idanagrafica;
	}
	public void setIdanagrafica(long idanagrafica) {
		this.idanagrafica = idanagrafica;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof InfortunitestimoniPK)) {
			return false;
		}
		InfortunitestimoniPK castOther = (InfortunitestimoniPK)other;
		return 
			(this.idinfortunio == castOther.idinfortunio)
			&& (this.idanagrafica == castOther.idanagrafica);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.idinfortunio ^ (this.idinfortunio >>> 32)));
		hash = hash * prime + ((int) (this.idanagrafica ^ (this.idanagrafica >>> 32)));
		
		return hash;
	}
}