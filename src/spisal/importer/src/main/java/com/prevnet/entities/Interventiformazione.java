package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the INTERVENTIFORMAZIONE database table.
 * 
 */
@Entity
@NamedQuery(name="Interventiformazione.findAll", query="SELECT i FROM Interventiformazione i")
public class Interventiformazione implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idintervento;
	private String codificascheda;
	private String descrfuoriprogetto;
	private Anagrafiche soggetto;
	private Ditte ditta;
	private BigDecimal idstoricoutente;
	private String noteint;
	private BigDecimal tipoanag;
	private String verificaapprendimento;
	private String verificagradimento;
	private String verificaimpatto;
	private List<Interventiattivitasvolta> interventiattivitasvoltas;
	private Tabelle progetto;
	private Tabelle areatematica;
	private Tabelle macroprodotto;
	private Pratiche pratica;
	
	public Interventiformazione() {
	}


	@Id
	public long getIdintervento() {
		return this.idintervento;
	}

	public void setIdintervento(long idintervento) {
		this.idintervento = idintervento;
	}


	public String getCodificascheda() {
		return this.codificascheda;
	}

	public void setCodificascheda(String codificascheda) {
		this.codificascheda = codificascheda;
	}


	public String getDescrfuoriprogetto() {
		return this.descrfuoriprogetto;
	}

	public void setDescrfuoriprogetto(String descrfuoriprogetto) {
		this.descrfuoriprogetto = descrfuoriprogetto;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getSoggetto() {
		return this.soggetto;
	}

	public void setSoggetto(Anagrafiche soggetto) {
		this.soggetto = soggetto;
	}

	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getDitta() {
		return this.ditta;
	}

	public void setDitta(Ditte ditta) {
		this.ditta = ditta;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	@Lob
	public String getNoteint() {
		return this.noteint;
	}

	public void setNoteint(String noteint) {
		this.noteint = noteint;
	}


	public BigDecimal getTipoanag() {
		return this.tipoanag;
	}

	public void setTipoanag(BigDecimal tipoanag) {
		this.tipoanag = tipoanag;
	}


	public String getVerificaapprendimento() {
		return this.verificaapprendimento;
	}

	public void setVerificaapprendimento(String verificaapprendimento) {
		this.verificaapprendimento = verificaapprendimento;
	}


	public String getVerificagradimento() {
		return this.verificagradimento;
	}

	public void setVerificagradimento(String verificagradimento) {
		this.verificagradimento = verificagradimento;
	}


	public String getVerificaimpatto() {
		return this.verificaimpatto;
	}

	public void setVerificaimpatto(String verificaimpatto) {
		this.verificaimpatto = verificaimpatto;
	}


	//bi-directional many-to-one association to Interventiattivitasvolta
	@OneToMany(mappedBy="interventiformazione")
	public List<Interventiattivitasvolta> getInterventiattivitasvoltas() {
		return this.interventiattivitasvoltas;
	}

	public void setInterventiattivitasvoltas(List<Interventiattivitasvolta> interventiattivitasvoltas) {
		this.interventiattivitasvoltas = interventiattivitasvoltas;
	}

	public Interventiattivitasvolta addInterventiattivitasvolta(Interventiattivitasvolta interventiattivitasvolta) {
		getInterventiattivitasvoltas().add(interventiattivitasvolta);
		interventiattivitasvolta.setInterventiformazione(this);

		return interventiattivitasvolta;
	}

	public Interventiattivitasvolta removeInterventiattivitasvolta(Interventiattivitasvolta interventiattivitasvolta) {
		getInterventiattivitasvoltas().remove(interventiattivitasvolta);
		interventiattivitasvolta.setInterventiformazione(null);

		return interventiattivitasvolta;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROGETTO")
	public Tabelle getProgetto() {
		return this.progetto;
	}

	public void setProgetto(Tabelle progetto) {
		this.progetto = progetto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAREATEMATICA")
	public Tabelle getAreatematica() {
		return this.areatematica;
	}

	public void setAreatematica(Tabelle areatematica) {
		this.areatematica = areatematica;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMACROPRODOTTO")
	public Tabelle getMacroprodotto() {
		return this.macroprodotto;
	}

	public void setMacroprodotto(Tabelle macroprodotto) {
		this.macroprodotto = macroprodotto;
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
}