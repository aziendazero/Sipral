package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the DITTEEDILIZIA database table.
 * 
 */
@Entity
public class Ditteedilizia implements Serializable {
	private static final long serialVersionUID = 1L;
	private DitteediliziaPK id;
	private String chkpospresente;
	private Date dataultvalutpos;
	private BigDecimal nlav;
	private BigDecimal nstranieri;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private Anagrafiche ditta;
	private AttivitaPrevnet attivita;
	private Ditte ditte;
	private Sopralluoghidip sopralluoghidip;
	private Tabelle esitoControllo;
	private Tabelle esitoUltimaValPos;

	public Ditteedilizia() {
	}


	@EmbeddedId
	public DitteediliziaPK getId() {
		return this.id;
	}

	public void setId(DitteediliziaPK id) {
		this.id = id;
	}


	public String getChkpospresente() {
		return this.chkpospresente;
	}

	public void setChkpospresente(String chkpospresente) {
		this.chkpospresente = chkpospresente;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultvalutpos() {
		return this.dataultvalutpos;
	}

	public void setDataultvalutpos(Date dataultvalutpos) {
		this.dataultvalutpos = dataultvalutpos;
	}


	public BigDecimal getNlav() {
		return this.nlav;
	}

	public void setNlav(BigDecimal nlav) {
		this.nlav = nlav;
	}


	public BigDecimal getNstranieri() {
		return this.nstranieri;
	}

	public void setNstranieri(BigDecimal nstranieri) {
		this.nstranieri = nstranieri;
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


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Anagrafiche getDitta() {
		return this.ditta;
	}

	public void setDitta(Anagrafiche ditta) {
		this.ditta = ditta;
	}


	//bi-directional many-to-one association to AttivitaPrevnet
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITACONTROLLATA")
	public AttivitaPrevnet getAttivitaPrevnet() {
		return this.attivita;
	}

	public void setAttivitaPrevnet(AttivitaPrevnet attivita) {
		this.attivita = attivita;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getDitte() {
		return this.ditte;
	}

	public void setDitte(Ditte ditte) {
		this.ditte = ditte;
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
	@JoinColumn(name="IDESITOCONTROLLO")
	public Tabelle getEsitoControllo() {
		return this.esitoControllo;
	}

	public void setEsitoControllo(Tabelle esitoControllo) {
		this.esitoControllo = esitoControllo;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITOULTIMAVALPOS")
	public Tabelle getEsitoUltimaValPos() {
		return this.esitoUltimaValPos;
	}

	public void setEsitoUltimaValPos(Tabelle esitoUltimaValPos) {
		this.esitoUltimaValPos = esitoUltimaValPos;
	}

}