package com.prevnet.mappings;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Questa non estende GenericMapping perchè idprevnet può ripetersi 
 * (sarebbe la coppia idprevnet-idditta ad essere pk, ma va bene impostare come pk anche idphi)
 * @author 510087
 *
 */
@Entity
@Table(name="MAPPING_ATTIVITA")
public class MapAttivita implements Serializable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Id entità di origine
	 * @return
	 */
	@Column(precision=22)
	public long getIdprevnet() {
		return idprevnet;
	}
	public void setIdprevnet(long idprevnet) {
		this.idprevnet = idprevnet;
	}
	
	/**
	 * Id entità di destinazione (copia)
	 * @return
	 */
	@Id
	@Column(precision=22)
	public long getIdphi() {
		return idphi;
	}
	public void setIdphi(long idphi) {
		this.idphi = idphi;
	}
	
	/**
	 * Data di copia
	 * @return
	 */
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCopyDate() {
		return copyDate;
	}
	public void setCopyDate(Date copyDate) {
		this.copyDate = copyDate;
	}
	
	/**
	 * Autore copia
	 * @return
	 */
	@Column(length=255)
	public String getCopiedBy() {
		return copiedBy;
	}
	public void setCopiedBy(String copiedBy) {
		this.copiedBy = copiedBy;
	}
	
	public String getUlss() {
		return ulss;
	}
	public void setUlss(String ulss) {
		this.ulss = ulss;
	}	
	
	private long idprevnet;
	private long idphi;
	private Date copyDate;
	private String copiedBy;
	private String ulss;
	private long idDitta;

	public long getIdDitta() {
		return idDitta;
	}

	public void setIdDitta(long idDitta) {
		this.idDitta = idDitta;
	}
}
