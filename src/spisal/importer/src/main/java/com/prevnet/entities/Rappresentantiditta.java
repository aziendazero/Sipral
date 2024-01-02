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
 * The persistent class for the RAPPRESENTANTIDITTA database table.
 * 
 */
@Entity
public class Rappresentantiditta implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idrappresentantiditta;
	private String cap;
	private Date dataal;
	private Date datadal;
	private BigDecimal idcomune;
	private BigDecimal idstoricoutente;
	private String indirizzo;
	private Date synchdate;
	private String synchflag;
	private Anagrafiche anagrafica;
	private Ditte ditta;
	private Tabelle ruolo;
	private Utenti utente;

	public Rappresentantiditta() {
	}


	@Id
	public long getIdrappresentantiditta() {
		return this.idrappresentantiditta;
	}

	public void setIdrappresentantiditta(long idrappresentantiditta) {
		this.idrappresentantiditta = idrappresentantiditta;
	}


	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataal() {
		return this.dataal;
	}

	public void setDataal(Date dataal) {
		this.dataal = dataal;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatadal() {
		return this.datadal;
	}

	public void setDatadal(Date datadal) {
		this.datadal = datadal;
	}


	public BigDecimal getIdcomune() {
		return this.idcomune;
	}

	public void setIdcomune(BigDecimal idcomune) {
		this.idcomune = idcomune;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
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


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getAnagrafica() {
		return this.anagrafica;
	}

	public void setAnagrafica(Anagrafiche anagrafica) {
		this.anagrafica = anagrafica;
	}

	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Ditte getDitta() {
		return this.ditta;
	}

	public void setDitta(Ditte ditta) {
		this.ditta = ditta;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRUOLO")
	public Tabelle getRuolo() {
		return this.ruolo;
	}

	public void setRuolo(Tabelle ruolo) {
		this.ruolo = ruolo;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUTENTE")
	public Utenti getUtente() {
		return this.utente;
	}

	public void setUtente(Utenti utente) {
		this.utente = utente;
	}

}