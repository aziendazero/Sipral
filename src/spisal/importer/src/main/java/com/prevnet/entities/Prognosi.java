package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the PROGNOSI database table.
 * 
 */
@Entity
public class Prognosi implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idprognosi;
	private Date dataprognosi;
	private BigDecimal giorni;
	private InfortuniPrevnet infortuniPrevnet;

	public Prognosi() {
	}


	@Id
	public long getIdprognosi() {
		return this.idprognosi;
	}

	public void setIdprognosi(long idprognosi) {
		this.idprognosi = idprognosi;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataprognosi() {
		return this.dataprognosi;
	}

	public void setDataprognosi(Date dataprognosi) {
		this.dataprognosi = dataprognosi;
	}


	public BigDecimal getGiorni() {
		return this.giorni;
	}

	public void setGiorni(BigDecimal giorni) {
		this.giorni = giorni;
	}


	//bi-directional many-to-one association to Infortuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINFORTUNIO")
	public InfortuniPrevnet getInfortuniPrevnet() {
		return this.infortuniPrevnet;
	}

	public void setInfortuniPrevnet(InfortuniPrevnet infortuniPrevnet) {
		this.infortuniPrevnet = infortuniPrevnet;
	}

}