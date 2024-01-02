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
 * Questa non estende GenericMapping perchè idprevnet può essere vuota (caso di inserimento di titolare)
 * @author 510087
 *
 */
@Entity
@Table(name="MAPPING_RAPPRESENTANTI")
public class MapRappresentanti implements Serializable{
	private static final long serialVersionUID = 1L;

	private long pgId;
	private long utenteId;
	private long dipId;
	private long idprevnet;
	private long idphi;
	private Date copyDate;
	private String copiedBy;
	private String ulss;

	
	public long getPgId() {
		return pgId;
	}
	public void setPgId(long pgId) {
		this.pgId = pgId;
	}
	public long getUtenteId() {
		return utenteId;
	}
	public void setUtenteId(long utenteId) {
		this.utenteId = utenteId;
	}
	public long getDipId() {
		return dipId;
	}
	public void setDipId(long dipId) {
		this.dipId = dipId;
	}	

	/**
	 * Id entità di destinazione (copia)
	 * @return
	 */
	@Id
	public long getIdphi() {
		return this.idphi;
	}
	public void setIdphi(long idphi) {
		this.idphi = idphi;
	}
	
	/**
	 * Id entità di origine
	 * @return
	 */
	public long getIdprevnet() {
		return idprevnet;
	}
	public void setIdprevnet(long idprevnet) {
		this.idprevnet = idprevnet;
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
}
