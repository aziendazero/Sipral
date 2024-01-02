package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the STORICOIMPIEGHI database table.
 * 
 */
@Entity
@NamedQuery(name="Storicoimpieghi.findAll", query="SELECT s FROM Storicoimpieghi s")
public class Storicoimpieghi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long idstoricoimpieghi;

	private BigDecimal anno;

	@Temporal(TemporalType.DATE)
	private Date datacomunicazione;

	@Temporal(TemporalType.DATE)
	private Date datafine;

	@Temporal(TemporalType.DATE)
	private Date datainizio;

	@Temporal(TemporalType.DATE)
	private Date dataproxvisita;

	@Temporal(TemporalType.DATE)
	private Date dataregistrazione;

	@Temporal(TemporalType.DATE)
	private Date datarilpatadetto;

	@Temporal(TemporalType.DATE)
	private Date datarilpatcoord;

	@Temporal(TemporalType.DATE)
	private Date datascadpatadetto;

	@Temporal(TemporalType.DATE)
	private Date datascadpatcoord;

	@Temporal(TemporalType.DATE)
	private Date datavisita;

	private String descrizionedittalavoratore;

	private String fattoririschiotab;

	private String flagcertif;

	private String flagregistrocancerogeni;

	private BigDecimal idcomunicazionecancerogeni;

	private BigDecimal idditta;

	private BigDecimal mesivalidita;

	private BigDecimal mesivalpatadetto;

	private BigDecimal mesivalpatcoord;

	private String tipoperiodo;

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAZIENDA")
	private Anagrafiche anagrafiche;

	//bi-directional many-to-one association to Cartellesanitarie
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCARTELLASANITARIA")
	private Cartellesanitarie cartellesanitarie;

	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICOCOMPETENTE")
	private Medici medici;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMANSIONE")
	private Tabelle tabelle1;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDREPARTO")
	private Tabelle tabelle2;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTATOIDONEITA")
	private Tabelle tabelle3;

	public Storicoimpieghi() {
	}

	public long getIdstoricoimpieghi() {
		return this.idstoricoimpieghi;
	}

	public void setIdstoricoimpieghi(long idstoricoimpieghi) {
		this.idstoricoimpieghi = idstoricoimpieghi;
	}

	public BigDecimal getAnno() {
		return this.anno;
	}

	public void setAnno(BigDecimal anno) {
		this.anno = anno;
	}

	public Date getDatacomunicazione() {
		return this.datacomunicazione;
	}

	public void setDatacomunicazione(Date datacomunicazione) {
		this.datacomunicazione = datacomunicazione;
	}

	public Date getDatafine() {
		return this.datafine;
	}

	public void setDatafine(Date datafine) {
		this.datafine = datafine;
	}

	public Date getDatainizio() {
		return this.datainizio;
	}

	public void setDatainizio(Date datainizio) {
		this.datainizio = datainizio;
	}

	public Date getDataproxvisita() {
		return this.dataproxvisita;
	}

	public void setDataproxvisita(Date dataproxvisita) {
		this.dataproxvisita = dataproxvisita;
	}

	public Date getDataregistrazione() {
		return this.dataregistrazione;
	}

	public void setDataregistrazione(Date dataregistrazione) {
		this.dataregistrazione = dataregistrazione;
	}

	public Date getDatarilpatadetto() {
		return this.datarilpatadetto;
	}

	public void setDatarilpatadetto(Date datarilpatadetto) {
		this.datarilpatadetto = datarilpatadetto;
	}

	public Date getDatarilpatcoord() {
		return this.datarilpatcoord;
	}

	public void setDatarilpatcoord(Date datarilpatcoord) {
		this.datarilpatcoord = datarilpatcoord;
	}

	public Date getDatascadpatadetto() {
		return this.datascadpatadetto;
	}

	public void setDatascadpatadetto(Date datascadpatadetto) {
		this.datascadpatadetto = datascadpatadetto;
	}

	public Date getDatascadpatcoord() {
		return this.datascadpatcoord;
	}

	public void setDatascadpatcoord(Date datascadpatcoord) {
		this.datascadpatcoord = datascadpatcoord;
	}

	public Date getDatavisita() {
		return this.datavisita;
	}

	public void setDatavisita(Date datavisita) {
		this.datavisita = datavisita;
	}

	public String getDescrizionedittalavoratore() {
		return this.descrizionedittalavoratore;
	}

	public void setDescrizionedittalavoratore(String descrizionedittalavoratore) {
		this.descrizionedittalavoratore = descrizionedittalavoratore;
	}

	public String getFattoririschiotab() {
		return this.fattoririschiotab;
	}

	public void setFattoririschiotab(String fattoririschiotab) {
		this.fattoririschiotab = fattoririschiotab;
	}

	public String getFlagcertif() {
		return this.flagcertif;
	}

	public void setFlagcertif(String flagcertif) {
		this.flagcertif = flagcertif;
	}

	public String getFlagregistrocancerogeni() {
		return this.flagregistrocancerogeni;
	}

	public void setFlagregistrocancerogeni(String flagregistrocancerogeni) {
		this.flagregistrocancerogeni = flagregistrocancerogeni;
	}

	public BigDecimal getIdcomunicazionecancerogeni() {
		return this.idcomunicazionecancerogeni;
	}

	public void setIdcomunicazionecancerogeni(BigDecimal idcomunicazionecancerogeni) {
		this.idcomunicazionecancerogeni = idcomunicazionecancerogeni;
	}

	public BigDecimal getIdditta() {
		return this.idditta;
	}

	public void setIdditta(BigDecimal idditta) {
		this.idditta = idditta;
	}

	public BigDecimal getMesivalidita() {
		return this.mesivalidita;
	}

	public void setMesivalidita(BigDecimal mesivalidita) {
		this.mesivalidita = mesivalidita;
	}

	public BigDecimal getMesivalpatadetto() {
		return this.mesivalpatadetto;
	}

	public void setMesivalpatadetto(BigDecimal mesivalpatadetto) {
		this.mesivalpatadetto = mesivalpatadetto;
	}

	public BigDecimal getMesivalpatcoord() {
		return this.mesivalpatcoord;
	}

	public void setMesivalpatcoord(BigDecimal mesivalpatcoord) {
		this.mesivalpatcoord = mesivalpatcoord;
	}

	public String getTipoperiodo() {
		return this.tipoperiodo;
	}

	public void setTipoperiodo(String tipoperiodo) {
		this.tipoperiodo = tipoperiodo;
	}

	public Anagrafiche getAnagrafiche() {
		return this.anagrafiche;
	}

	public void setAnagrafiche(Anagrafiche anagrafiche) {
		this.anagrafiche = anagrafiche;
	}

	public Cartellesanitarie getCartellesanitarie() {
		return this.cartellesanitarie;
	}

	public void setCartellesanitarie(Cartellesanitarie cartellesanitarie) {
		this.cartellesanitarie = cartellesanitarie;
	}

	public Medici getMedici() {
		return this.medici;
	}

	public void setMedici(Medici medici) {
		this.medici = medici;
	}

	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}

	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}

	public Tabelle getTabelle3() {
		return this.tabelle3;
	}

	public void setTabelle3(Tabelle tabelle3) {
		this.tabelle3 = tabelle3;
	}

}