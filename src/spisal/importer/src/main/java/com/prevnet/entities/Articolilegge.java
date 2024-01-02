package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ARTICOLILEGGE database table.
 * 
 */
@Entity
@NamedQuery(name="Articolilegge.findAll", query="SELECT a FROM Articolilegge a")
public class Articolilegge implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idarticoli;
	private String arresto;
	private String comma;
	private String controlloRuolo;
	private String corpo;
	private Date dataultaggiornamento;
	private Date datavalidazione;
	private BigDecimal importomax;
	private BigDecimal importomin;
	private String invigore;
	private Date invigoredal;
	private Date invigorefinoal;
	private String lettera;
	private String noteprescr;
	private String noteviolazione;
	private String numero;
	private BigDecimal riferimento;
	private String sanzionatoda;
	private String sanzione;
	private String titolo;
	private String vidimato;
	private Leggi leggi;
	private List<Sanzioniarticoli> sanzioniarticolis;
	private Operatori operatori;
	private Tabelle tabelle1;
	private Tabelle tabelle2;
	private Tabelle contravventore;
	private List<Articoliprovvedimenti> articoliprovvedimentis;

	public Articolilegge() {
	}


	@Id
	public long getIdarticoli() {
		return this.idarticoli;
	}

	public void setIdarticoli(long idarticoli) {
		this.idarticoli = idarticoli;
	}


	public String getArresto() {
		return this.arresto;
	}

	public void setArresto(String arresto) {
		this.arresto = arresto;
	}


	public String getComma() {
		return this.comma;
	}

	public void setComma(String comma) {
		this.comma = comma;
	}


	@Column(name="CONTROLLO_RUOLO")
	public String getControlloRuolo() {
		return this.controlloRuolo;
	}

	public void setControlloRuolo(String controlloRuolo) {
		this.controlloRuolo = controlloRuolo;
	}


	public String getCorpo() {
		return this.corpo;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultaggiornamento() {
		return this.dataultaggiornamento;
	}

	public void setDataultaggiornamento(Date dataultaggiornamento) {
		this.dataultaggiornamento = dataultaggiornamento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavalidazione() {
		return this.datavalidazione;
	}

	public void setDatavalidazione(Date datavalidazione) {
		this.datavalidazione = datavalidazione;
	}


	public BigDecimal getImportomax() {
		return this.importomax;
	}

	public void setImportomax(BigDecimal importomax) {
		this.importomax = importomax;
	}


	public BigDecimal getImportomin() {
		return this.importomin;
	}

	public void setImportomin(BigDecimal importomin) {
		this.importomin = importomin;
	}


	public String getInvigore() {
		return this.invigore;
	}

	public void setInvigore(String invigore) {
		this.invigore = invigore;
	}


	@Temporal(TemporalType.DATE)
	public Date getInvigoredal() {
		return this.invigoredal;
	}

	public void setInvigoredal(Date invigoredal) {
		this.invigoredal = invigoredal;
	}


	@Temporal(TemporalType.DATE)
	public Date getInvigorefinoal() {
		return this.invigorefinoal;
	}

	public void setInvigorefinoal(Date invigorefinoal) {
		this.invigorefinoal = invigorefinoal;
	}


	public String getLettera() {
		return this.lettera;
	}

	public void setLettera(String lettera) {
		this.lettera = lettera;
	}


	public String getNoteprescr() {
		return this.noteprescr;
	}

	public void setNoteprescr(String noteprescr) {
		this.noteprescr = noteprescr;
	}


	public String getNoteviolazione() {
		return this.noteviolazione;
	}

	public void setNoteviolazione(String noteviolazione) {
		this.noteviolazione = noteviolazione;
	}


	public String getNumero() {
		return this.numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}


	public BigDecimal getRiferimento() {
		return this.riferimento;
	}

	public void setRiferimento(BigDecimal riferimento) {
		this.riferimento = riferimento;
	}


	public String getSanzionatoda() {
		return this.sanzionatoda;
	}

	public void setSanzionatoda(String sanzionatoda) {
		this.sanzionatoda = sanzionatoda;
	}


	public String getSanzione() {
		return this.sanzione;
	}

	public void setSanzione(String sanzione) {
		this.sanzione = sanzione;
	}


	public String getTitolo() {
		return this.titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}


	public String getVidimato() {
		return this.vidimato;
	}

	public void setVidimato(String vidimato) {
		this.vidimato = vidimato;
	}


	//bi-directional many-to-one association to Leggi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLEGGE")
	public Leggi getLeggi() {
		return this.leggi;
	}

	public void setLeggi(Leggi leggi) {
		this.leggi = leggi;
	}


	//bi-directional many-to-one association to Sanzioniarticoli
	@OneToMany(mappedBy="articolilegge")
	public List<Sanzioniarticoli> getSanzioniarticolis() {
		return this.sanzioniarticolis;
	}

	public void setSanzioniarticolis(List<Sanzioniarticoli> sanzioniarticolis) {
		this.sanzioniarticolis = sanzioniarticolis;
	}

	public Sanzioniarticoli addSanzioniarticoli(Sanzioniarticoli sanzioniarticoli) {
		getSanzioniarticolis().add(sanzioniarticoli);
		sanzioniarticoli.setArticolilegge(this);

		return sanzioniarticoli;
	}

	public Sanzioniarticoli removeSanzioniarticoli(Sanzioniarticoli sanzioniarticoli) {
		getSanzioniarticolis().remove(sanzioniarticoli);
		sanzioniarticoli.setArticolilegge(null);

		return sanzioniarticoli;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREVALIDANTE")
	public Operatori getOperatori() {
		return this.operatori;
	}

	public void setOperatori(Operatori operatori) {
		this.operatori = operatori;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCLASSIFICAZIONEARTICOLO")
	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDARTICOLOIMPRESA")
	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCONTRAVVENTORE")
	public Tabelle getContravventore() {
		return this.contravventore;
	}

	public void setContravventore(Tabelle contravventore) {
		this.contravventore = contravventore;
	}


	//bi-directional many-to-one association to Articoliprovvedimenti
	@OneToMany(mappedBy="articolilegge")
	public List<Articoliprovvedimenti> getArticoliprovvedimentis() {
		return this.articoliprovvedimentis;
	}

	public void setArticoliprovvedimentis(List<Articoliprovvedimenti> articoliprovvedimentis) {
		this.articoliprovvedimentis = articoliprovvedimentis;
	}

	public Articoliprovvedimenti addArticoliprovvedimenti(Articoliprovvedimenti articoliprovvedimenti) {
		getArticoliprovvedimentis().add(articoliprovvedimenti);
		articoliprovvedimenti.setArticolilegge(this);

		return articoliprovvedimenti;
	}

	public Articoliprovvedimenti removeArticoliprovvedimenti(Articoliprovvedimenti articoliprovvedimenti) {
		getArticoliprovvedimentis().remove(articoliprovvedimenti);
		articoliprovvedimenti.setArticolilegge(null);

		return articoliprovvedimenti;
	}

}