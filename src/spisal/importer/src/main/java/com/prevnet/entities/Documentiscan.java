package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the DOCUMENTISCAN database table.
 * 
 */
@Entity
@NamedQuery(name="Documentiscan.findAll", query="SELECT d FROM Documentiscan d")
public class Documentiscan implements Serializable {
	private static final long serialVersionUID = 1L;
	private long iddocumentiscan;
	private Date datafile;
	private Date dataultmod;
	private String descrizionefile;
	private byte[] filescan;
	private String fontedoc;
	private BigDecimal idanagrafichecapi;
	private BigDecimal idanimali;
	private BigDecimal idarticolo;
	private BigDecimal idimpianto;
	private BigDecimal idimprichiestaverifica;
	private BigDecimal idimpsoggettoabilitato;
	private BigDecimal idpassoprocedura;
	private BigDecimal idprocedura;
	private BigDecimal idprotocollo;
	private BigDecimal idseduta;
	private BigDecimal idsessionerichiestapwi;
	private BigDecimal idterritorio;
	private String incancellabile;
	private String nomefile;
	private String nuovoallegato;
	private String pagekey;
	private String parolechiave;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private BigDecimal tipofile;
	private String visibilepwi;
	private Anagrafiche anagrafiche;
	private Atti atti;
	private Ditte ditte;
	private Pratiche pratica;
	private Tabelle tabelle;

	public Documentiscan() {
	}


	@Id
	public long getIddocumentiscan() {
		return this.iddocumentiscan;
	}

	public void setIddocumentiscan(long iddocumentiscan) {
		this.iddocumentiscan = iddocumentiscan;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="\"DATAFILE\"")
	public Date getDatafile() {
		return this.datafile;
	}

	public void setDatafile(Date datafile) {
		this.datafile = datafile;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultmod() {
		return this.dataultmod;
	}

	public void setDataultmod(Date dataultmod) {
		this.dataultmod = dataultmod;
	}


	public String getDescrizionefile() {
		return this.descrizionefile;
	}

	public void setDescrizionefile(String descrizionefile) {
		this.descrizionefile = descrizionefile;
	}


	@Lob
	public byte[] getFilescan() {
		return this.filescan;
	}

	public void setFilescan(byte[] filescan) {
		this.filescan = filescan;
	}


	public String getFontedoc() {
		return this.fontedoc;
	}

	public void setFontedoc(String fontedoc) {
		this.fontedoc = fontedoc;
	}


	public BigDecimal getIdanagrafichecapi() {
		return this.idanagrafichecapi;
	}

	public void setIdanagrafichecapi(BigDecimal idanagrafichecapi) {
		this.idanagrafichecapi = idanagrafichecapi;
	}


	public BigDecimal getIdanimali() {
		return this.idanimali;
	}

	public void setIdanimali(BigDecimal idanimali) {
		this.idanimali = idanimali;
	}


	public BigDecimal getIdarticolo() {
		return this.idarticolo;
	}

	public void setIdarticolo(BigDecimal idarticolo) {
		this.idarticolo = idarticolo;
	}


	public BigDecimal getIdimpianto() {
		return this.idimpianto;
	}

	public void setIdimpianto(BigDecimal idimpianto) {
		this.idimpianto = idimpianto;
	}


	public BigDecimal getIdimprichiestaverifica() {
		return this.idimprichiestaverifica;
	}

	public void setIdimprichiestaverifica(BigDecimal idimprichiestaverifica) {
		this.idimprichiestaverifica = idimprichiestaverifica;
	}


	public BigDecimal getIdimpsoggettoabilitato() {
		return this.idimpsoggettoabilitato;
	}

	public void setIdimpsoggettoabilitato(BigDecimal idimpsoggettoabilitato) {
		this.idimpsoggettoabilitato = idimpsoggettoabilitato;
	}


	public BigDecimal getIdpassoprocedura() {
		return this.idpassoprocedura;
	}

	public void setIdpassoprocedura(BigDecimal idpassoprocedura) {
		this.idpassoprocedura = idpassoprocedura;
	}


	public BigDecimal getIdprocedura() {
		return this.idprocedura;
	}

	public void setIdprocedura(BigDecimal idprocedura) {
		this.idprocedura = idprocedura;
	}


	public BigDecimal getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(BigDecimal idprotocollo) {
		this.idprotocollo = idprotocollo;
	}


	public BigDecimal getIdseduta() {
		return this.idseduta;
	}

	public void setIdseduta(BigDecimal idseduta) {
		this.idseduta = idseduta;
	}


	public BigDecimal getIdsessionerichiestapwi() {
		return this.idsessionerichiestapwi;
	}

	public void setIdsessionerichiestapwi(BigDecimal idsessionerichiestapwi) {
		this.idsessionerichiestapwi = idsessionerichiestapwi;
	}


	public BigDecimal getIdterritorio() {
		return this.idterritorio;
	}

	public void setIdterritorio(BigDecimal idterritorio) {
		this.idterritorio = idterritorio;
	}


	public String getIncancellabile() {
		return this.incancellabile;
	}

	public void setIncancellabile(String incancellabile) {
		this.incancellabile = incancellabile;
	}


	public String getNomefile() {
		return this.nomefile;
	}

	public void setNomefile(String nomefile) {
		this.nomefile = nomefile;
	}


	public String getNuovoallegato() {
		return this.nuovoallegato;
	}

	public void setNuovoallegato(String nuovoallegato) {
		this.nuovoallegato = nuovoallegato;
	}


	public String getPagekey() {
		return this.pagekey;
	}

	public void setPagekey(String pagekey) {
		this.pagekey = pagekey;
	}


	public String getParolechiave() {
		return this.parolechiave;
	}

	public void setParolechiave(String parolechiave) {
		this.parolechiave = parolechiave;
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


	public BigDecimal getTipofile() {
		return this.tipofile;
	}

	public void setTipofile(BigDecimal tipofile) {
		this.tipofile = tipofile;
	}


	public String getVisibilepwi() {
		return this.visibilepwi;
	}

	public void setVisibilepwi(String visibilepwi) {
		this.visibilepwi = visibilepwi;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getAnagrafiche() {
		return this.anagrafiche;
	}

	public void setAnagrafiche(Anagrafiche anagrafiche) {
		this.anagrafiche = anagrafiche;
	}


	//bi-directional many-to-one association to Atti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIINOUT")
	public Atti getAtti() {
		return this.atti;
	}

	public void setAtti(Atti atti) {
		this.atti = atti;
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


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDBANCADATI")
	public Tabelle getTabelle() {
		return this.tabelle;
	}

	public void setTabelle(Tabelle tabelle) {
		this.tabelle = tabelle;
	}

}