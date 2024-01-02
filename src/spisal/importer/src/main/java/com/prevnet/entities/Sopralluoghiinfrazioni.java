package com.prevnet.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the SOPRALLUOGHIINFRAZIONI database table.
 * 
 */
@Entity
public class Sopralluoghiinfrazioni implements Serializable {
	private static final long serialVersionUID = 1L;
	private SopralluoghiinfrazioniPK id;
	private String numinfr;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private Sopralluoghidip sopralluoghidip;
	private Tabelle tabelle;

	public Sopralluoghiinfrazioni() {
	}

	@EmbeddedId
	public SopralluoghiinfrazioniPK getId() {
		return this.id;
	}

	public void setId(SopralluoghiinfrazioniPK id) {
		this.id = id;
	}

	public String getNuminfr() {
		return this.numinfr;
	}

	public void setNuminfr(String numinfr) {
		this.numinfr = numinfr;
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
	@JoinColumn(name="IDINFRAZIONE")
	public Tabelle getTabelle() {
		return this.tabelle;
	}

	public void setTabelle(Tabelle tabelle) {
		this.tabelle = tabelle;
	}

}