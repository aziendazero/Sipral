package com.prevnet.mappings;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MAPPING_OPERATORI")
public class MapOperatori extends GenericMapping {
	private static final long serialVersionUID = 1L;

	private boolean operatoreEsterno;
	private long migoperatore;
	
	public boolean isOperatoreEsterno() {
		return operatoreEsterno;
	}
	public void setOperatoreEsterno(boolean operatoreEsterno) {
		this.operatoreEsterno = operatoreEsterno;
	}
	
	@Column(name="mig_operatore", precision=22)
	public long getMigoperatore() {
		return migoperatore;
	}
	public void setMigoperatore(long migoperatore) {
		this.migoperatore = migoperatore;
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
