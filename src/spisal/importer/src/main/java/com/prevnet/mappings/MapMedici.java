package com.prevnet.mappings;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MAPPING_MEDICI")
public class MapMedici {
	private static final long serialVersionUID = 1L;
	private long idprevnet;
	private String mpi;
	private long idphi;
	private Date copyDate;
	private String copiedBy;
	private String ulss;
	
	public String getMpi() {
		return mpi;
	}
	public void setMpi(String mpi) {
		this.mpi = mpi;
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
}
