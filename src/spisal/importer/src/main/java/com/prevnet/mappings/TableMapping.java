package com.prevnet.mappings;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Tabella di mapping per code value
 * @author 510087
 *
 */
@Entity
@Table(name="TABLE_MAPPING")
public class TableMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	private long internalId;
	private String codeSystem;
	private String domain;
	private String cvClass;
	private String codiceTabella;
	private String ulss;
	
	public TableMapping(){
		
	}
	
	public TableMapping(String codeSystem, String domain, String cvClass, String codiceTabella, String ulss) {
		super();
		this.codeSystem = codeSystem;
		this.domain = domain;
		this.cvClass = cvClass;
		this.codiceTabella = codiceTabella;
		this.ulss = ulss;
	}
	
	public String getCodeSystem() {
		return codeSystem;
	}
	public void setCodeSystem(String codeSystem) {
		this.codeSystem = codeSystem;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getCvClass() {
		return cvClass;
	}
	public void setCvClass(String cvClass) {
		this.cvClass = cvClass;
	}
	public String getCodiceTabella() {
		return codiceTabella;
	}
	public void setCodiceTabella(String codiceTabella) {
		this.codiceTabella = codiceTabella;
	}
	public String getUlss() {
		return ulss;
	}
	public void setUlss(String ulss) {
		this.ulss = ulss;
	}	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tabmap_sequence")
	@SequenceGenerator(name = "tabmap_sequence", sequenceName = "tabmap_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
}
