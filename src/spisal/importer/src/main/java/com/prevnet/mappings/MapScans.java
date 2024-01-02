package com.prevnet.mappings;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MAPPING_SCANS")
public class MapScans extends GenericMapping{
	private static final long serialVersionUID = 1L;

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
