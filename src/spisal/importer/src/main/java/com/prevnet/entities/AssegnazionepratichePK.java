package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class AssegnazionepratichePK implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idprocpratica;
	private long idoperatore;
	private String rdp;

	public AssegnazionepratichePK() {
	}

	@Column(insertable=false, updatable=false)
	public String getRdp() {
		return this.rdp;
	}

	public void setRdp(String rdp) {
		this.rdp = rdp;
	}
	
	@Column(insertable=false, updatable=false)
	public long getIdprocpratica() {
		return this.idprocpratica;
	}
	public void setIdprocpratica(long idprocpratica) {
		this.idprocpratica = idprocpratica;
	}

	@Column(insertable=false, updatable=false)
	public long getIdoperatore() {
		return this.idoperatore;
	}
	public void setIdoperatore(long idoperatore) {
		this.idoperatore = idoperatore;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (idoperatore ^ (idoperatore >>> 32));
		result = prime * result + (int) (idprocpratica ^ (idprocpratica >>> 32));
		result = prime * result + ((rdp == null) ? 0 : rdp.hashCode());
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
		AssegnazionepratichePK other = (AssegnazionepratichePK) obj;
		if (idoperatore != other.idoperatore)
			return false;
		if (idprocpratica != other.idprocpratica)
			return false;
		if (rdp == null) {
			if (other.rdp != null)
				return false;
		} else if (!rdp.equals(other.rdp))
			return false;
		return true;
	}


}