package com.prevnet.mappings;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MAPPING_PERSONEGIU")
public class MapPersoneGiuridiche extends GenericMapping{
	private static final long serialVersionUID = 1L;
	
	/*
	 * L'id della persona giuridica associata alla SEDE
	 */
	private long idPg;
	
	public long getIdPg() {
		return idPg;
	}
	public void setIdPg(long idPg) {
		this.idPg = idPg;
	}
	
	
	/**
	 * Id entità di origine
	 * @return
	 */
	@Override
	@Id
	public long getIdprevnet() {
		return idprevnet;
	}
	@Override
	public void setIdprevnet(long idprevnet) {
		this.idprevnet = idprevnet;
	}
}
