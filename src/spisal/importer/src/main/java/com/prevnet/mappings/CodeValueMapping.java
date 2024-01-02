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
@Table(name="CV_MAPPING")
public class CodeValueMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	private long internalId;
	private String utcode;
	private String oid;
	private String cvClass;
	private String codiceTabella;
	private String ulss;
	
	public CodeValueMapping(){
		
	}
	
	public CodeValueMapping(String utcode, String oid, String cvClass, String codiceTabella, String ulss) {
		super();
		this.utcode = utcode;
		this.oid = oid;
		this.cvClass = cvClass;
		this.codiceTabella = codiceTabella;
		this.ulss = ulss;
	}
	
	public String getUtcode() {
		return utcode;
	}
	public void setUtcode(String utcode) {
		this.utcode = utcode;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "cvmap_sequence")
	@SequenceGenerator(name = "cvmap_sequence", sequenceName = "cvmap_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
}
