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
 * The persistent class for the MISUREAMBIENTALI database table.
 * 
 */
@Entity
public class Misureambientali implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idmisureamb;
	private String note;
	private BigDecimal numerorilev;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private Pratiche pratica;
	private Sopralluoghidip sopralluoghidip;
	private Tabelle rilevazioneAmb;
	private Tabelle esitoRilevAmb;

	public Misureambientali() {
	}


	@Id
	public long getIdmisureamb() {
		return this.idmisureamb;
	}

	public void setIdmisureamb(long idmisureamb) {
		this.idmisureamb = idmisureamb;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getNumerorilev() {
		return this.numerorilev;
	}

	public void setNumerorilev(BigDecimal numerorilev) {
		this.numerorilev = numerorilev;
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


	public String getSynchid() {
		return this.synchid;
	}

	public void setSynchid(String synchid) {
		this.synchid = synchid;
	}


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	//bi-directional many-to-one association to Pratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGO")
	public Sopralluoghidip getSopralluoghidip() {
		return this.sopralluoghidip;
	}

	public void setSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		this.sopralluoghidip = sopralluoghidip;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRILEVAZIONIAMB")
	public Tabelle getRilevazioneAmb() {
		return this.rilevazioneAmb;
	}

	public void setRilevazioneAmb(Tabelle rilevazioneAmb) {
		this.rilevazioneAmb = rilevazioneAmb;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITORILEVAMB")
	public Tabelle getEsitoRilevAmb() {
		return this.esitoRilevAmb;
	}

	public void setEsitoRilevAmb(Tabelle esitoRilevAmb) {
		this.esitoRilevAmb = esitoRilevAmb;
	}

}