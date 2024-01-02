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
 * The persistent class for the SANZIONIARTICOLI database table.
 * 
 */
@Entity
@NamedQuery(name="Sanzioniarticoli.findAll", query="SELECT s FROM Sanzioniarticoli s")
public class Sanzioniarticoli implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idsanzioniarticoli;
	private String correlazioneinfmalprof;
	private String exportimpresa;
	private BigDecimal importo;
	private String notearticolo;
	private String noteviolazione;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private Articolilegge articolilegge;
	private Sanzioni sanzioni;
	private Articoliprovvedimenti articoliprovvedimenti;
	private Tabelle classificazione;

	public Sanzioniarticoli() {
	}


	@Id
	public long getIdsanzioniarticoli() {
		return this.idsanzioniarticoli;
	}

	public void setIdsanzioniarticoli(long idsanzioniarticoli) {
		this.idsanzioniarticoli = idsanzioniarticoli;
	}


	public String getCorrelazioneinfmalprof() {
		return this.correlazioneinfmalprof;
	}

	public void setCorrelazioneinfmalprof(String correlazioneinfmalprof) {
		this.correlazioneinfmalprof = correlazioneinfmalprof;
	}


	public String getExportimpresa() {
		return this.exportimpresa;
	}

	public void setExportimpresa(String exportimpresa) {
		this.exportimpresa = exportimpresa;
	}


	public BigDecimal getImporto() {
		return this.importo;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}


	public String getNotearticolo() {
		return this.notearticolo;
	}

	public void setNotearticolo(String notearticolo) {
		this.notearticolo = notearticolo;
	}


	public String getNoteviolazione() {
		return this.noteviolazione;
	}

	public void setNoteviolazione(String noteviolazione) {
		this.noteviolazione = noteviolazione;
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


	//bi-directional many-to-one association to Articolilegge
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDARTICOLI")
	public Articolilegge getArticolilegge() {
		return this.articolilegge;
	}

	public void setArticolilegge(Articolilegge articolilegge) {
		this.articolilegge = articolilegge;
	}


	//bi-directional many-to-one association to Sanzioni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSANZIONI")
	public Sanzioni getSanzioni() {
		return this.sanzioni;
	}

	public void setSanzioni(Sanzioni sanzioni) {
		this.sanzioni = sanzioni;
	}


	//bi-directional many-to-one association to Articoliprovvedimenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDARTICOLOPROVVEDIMENTO")
	public Articoliprovvedimenti getArticoliprovvedimenti() {
		return this.articoliprovvedimenti;
	}

	public void setArticoliprovvedimenti(Articoliprovvedimenti articoliprovvedimenti) {
		this.articoliprovvedimenti = articoliprovvedimenti;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCLASSIFICAZIONE")
	public Tabelle getClassificazione() {
		return this.classificazione;
	}

	public void setClassificazione(Tabelle classificazione) {
		this.classificazione = classificazione;
	}

}