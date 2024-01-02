package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the DLGS758GIORNIPROROGA database table.
 * 
 */
@Entity
@NamedQuery(name="Dlgs758giorniproroga.findAll", query="SELECT d FROM Dlgs758giorniproroga d")
public class Dlgs758giorniproroga implements Serializable {
	private static final long serialVersionUID = 1L;
	private Dlgs758giorniprorogaPK id;
	private Date datacomprorogapm;
	private Date dataproroga;
	private BigDecimal giorni;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private Dlgs758iter dlgs758iter;

	public Dlgs758giorniproroga() {
	}


	@EmbeddedId
	public Dlgs758giorniprorogaPK getId() {
		return this.id;
	}

	public void setId(Dlgs758giorniprorogaPK id) {
		this.id = id;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomprorogapm() {
		return this.datacomprorogapm;
	}

	public void setDatacomprorogapm(Date datacomprorogapm) {
		this.datacomprorogapm = datacomprorogapm;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataproroga() {
		return this.dataproroga;
	}

	public void setDataproroga(Date dataproroga) {
		this.dataproroga = dataproroga;
	}


	public BigDecimal getGiorni() {
		return this.giorni;
	}

	public void setGiorni(BigDecimal giorni) {
		this.giorni = giorni;
	}


	@Temporal(TemporalType.DATE)
	public Date getSynchdate() {
		return this.synchdate;
	}

	public void setSynchdate(Date synchdate) {
		this.synchdate = synchdate;
	}


	public String getSynchflag() {
		return this.synchflag;
	}

	public void setSynchflag(String synchflag) {
		this.synchflag = synchflag;
	}


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	//bi-directional many-to-one association to Dlgs758iter
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDARTPROVV")
	public Dlgs758iter getDlgs758iter() {
		return this.dlgs758iter;
	}

	public void setDlgs758iter(Dlgs758iter dlgs758iter) {
		this.dlgs758iter = dlgs758iter;
	}

}