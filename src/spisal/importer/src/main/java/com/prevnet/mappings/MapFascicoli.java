package com.prevnet.mappings;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MAPPING_FASCICOLI")
public class MapFascicoli extends GenericMapping{
	private static final long serialVersionUID = 1L;
	
	private Boolean tabella;
	
	public Boolean getTabella() {
		return tabella;
	}
	public void setTabella(Boolean tabella) {
		this.tabella = tabella;
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
