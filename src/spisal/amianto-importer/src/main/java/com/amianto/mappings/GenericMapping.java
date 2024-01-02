package com.amianto.mappings;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Generico Mapping
 * @author 510087
 *
 */
@MappedSuperclass
public abstract class GenericMapping implements Serializable {

	private static final long serialVersionUID = 1L;
	protected int idsorves;
	private long idphi;
	private Date copyDate;
	private String copiedBy;
	private String ulss;

	@Transient
	public abstract int getIdsorves();
	
	public abstract void setIdsorves(int idprevnet);
	
	/**
	 * Id entità di destinazione (copia)
	 * @return
	 */
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
}
