package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
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
 * The persistent class for the DETTAGLIVISITE database table.
 * 
 */
@Entity
@NamedQuery(name="Dettaglivisite.findAll", query="SELECT d FROM Dettaglivisite d")
public class Dettaglivisite implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long iddettaglivisite;

	private String chkacquisito;

	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"")
	private Date data;

	private String esameintegrativo;

	private BigDecimal idammasilo;

	private BigDecimal idammcomunita;

	private BigDecimal idmalattiaprof;

	private BigDecimal idpatente;

	private BigDecimal idprestazionesan;

	private BigDecimal idscreening;

	private BigDecimal idvisitaapprendista;

	private BigDecimal idvisitaconcertificato;

	private BigDecimal idvisitamedsport;

	private String note;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDGIUDIZIO")
	private Tabelle tabelle;

	//bi-directional many-to-one association to Visitespecialistiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDVISITASPECIALISTICA")
	private Visitespecialistiche visitespecialistiche;
	
	
//	//bi-directional many-to-one association to Esamiobiettivi
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="IDPRESTAZIONESAN", referencedColumnName="IDPRESTAZIONISAN")
//	private PrestazioniSan idprestazionesan;

	public Dettaglivisite() {
	}

	public long getIddettaglivisite() {
		return this.iddettaglivisite;
	}

	public void setIddettaglivisite(long iddettaglivisite) {
		this.iddettaglivisite = iddettaglivisite;
	}

	public String getChkacquisito() {
		return this.chkacquisito;
	}

	public void setChkacquisito(String chkacquisito) {
		this.chkacquisito = chkacquisito;
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getEsameintegrativo() {
		return this.esameintegrativo;
	}

	public void setEsameintegrativo(String esameintegrativo) {
		this.esameintegrativo = esameintegrativo;
	}

	public BigDecimal getIdammasilo() {
		return this.idammasilo;
	}

	public void setIdammasilo(BigDecimal idammasilo) {
		this.idammasilo = idammasilo;
	}

	public BigDecimal getIdammcomunita() {
		return this.idammcomunita;
	}

	public void setIdammcomunita(BigDecimal idammcomunita) {
		this.idammcomunita = idammcomunita;
	}

	public BigDecimal getIdmalattiaprof() {
		return this.idmalattiaprof;
	}

	public void setIdmalattiaprof(BigDecimal idmalattiaprof) {
		this.idmalattiaprof = idmalattiaprof;
	}

	public BigDecimal getIdpatente() {
		return this.idpatente;
	}

	public void setIdpatente(BigDecimal idpatente) {
		this.idpatente = idpatente;
	}

	public BigDecimal getIdprestazionesan() {
		return this.idprestazionesan;
	}

	public void setIdprestazionesan(BigDecimal idprestazionesan) {
		this.idprestazionesan = idprestazionesan;
	}

	public BigDecimal getIdscreening() {
		return this.idscreening;
	}

	public void setIdscreening(BigDecimal idscreening) {
		this.idscreening = idscreening;
	}

	public BigDecimal getIdvisitaapprendista() {
		return this.idvisitaapprendista;
	}

	public void setIdvisitaapprendista(BigDecimal idvisitaapprendista) {
		this.idvisitaapprendista = idvisitaapprendista;
	}

	public BigDecimal getIdvisitaconcertificato() {
		return this.idvisitaconcertificato;
	}

	public void setIdvisitaconcertificato(BigDecimal idvisitaconcertificato) {
		this.idvisitaconcertificato = idvisitaconcertificato;
	}

	public BigDecimal getIdvisitamedsport() {
		return this.idvisitamedsport;
	}

	public void setIdvisitamedsport(BigDecimal idvisitamedsport) {
		this.idvisitamedsport = idvisitamedsport;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Tabelle getTabelle() {
		return this.tabelle;
	}

	public void setTabelle(Tabelle tabelle) {
		this.tabelle = tabelle;
	}

	public Visitespecialistiche getVisitespecialistiche() {
		return this.visitespecialistiche;
	}

	public void setVisitespecialistiche(Visitespecialistiche visitespecialistiche) {
		this.visitespecialistiche = visitespecialistiche;
	}

}