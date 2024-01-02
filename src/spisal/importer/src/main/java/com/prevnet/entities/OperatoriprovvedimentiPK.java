package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


/**
 * The primary key class for the OPERATORIPROVVEDIMENTI database table.
 * 
 */
@Embeddable
public class OperatoriprovvedimentiPK implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idoperatore;
	private long idprovvedimento;


	public OperatoriprovvedimentiPK() {
	}

	@Column(insertable=false, updatable=false)
	public long getIdoperatore() {
		return idoperatore;
	}

	public void setIdoperatore(long idoperatore) {
		this.idoperatore = idoperatore;
	}

	@Column(insertable=false, updatable=false)
	public long getIdprovvedimento() {
		return idprovvedimento;
	}

	public void setIdprovvedimento(long idprovvedimento) {
		this.idprovvedimento = idprovvedimento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (idoperatore ^ (idoperatore >>> 32));
		result = prime * result + (int) (idprovvedimento ^ (idprovvedimento >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperatoriprovvedimentiPK other = (OperatoriprovvedimentiPK) obj;
		if (idoperatore != other.idoperatore)
			return false;
		if (idprovvedimento != other.idprovvedimento)
			return false;
		return true;
	}
	
	

}