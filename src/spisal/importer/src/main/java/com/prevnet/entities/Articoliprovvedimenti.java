package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ARTICOLIPROVVEDIMENTI database table.
 * 
 */
@Entity
@NamedQuery(name="Articoliprovvedimenti.findAll", query="SELECT a FROM Articoliprovvedimenti a")
public class Articoliprovvedimenti implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idarticoliprovvedimenti;
	private String correlazioneinfmalprof;
	private Date datacominottautgiud301;
	private Date datacominottcomune301;
	private Date datacominottcontravv301;
	private Date datacomottcomune301;
	private Date datanotificaammpag301;
	private Date dataottemperanza;
	private Date datascadenzaart;
	private Date datascadenzapag301;
	private String exportimpresa;
	private String note;
	private String noteviolazione;
	private BigDecimal numgginiziali;
	private BigDecimal ordineart;
	private String ottemperanza;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private Articolilegge articolilegge;
	private Comuni comuneComunicazioneOtt301;
	private Comuni comuneComunicazioneInott301;
	private ProvvedimentiPrevnet provvedimenti;
	private Scadenze scadenze;
	private Tabelle classificazione;
	private Dlgs758iter dlgs758iter;
	private List<Sanzioniarticoli> sanzioniarticolis;

	public Articoliprovvedimenti() {
	}


	@Id
	public long getIdarticoliprovvedimenti() {
		return this.idarticoliprovvedimenti;
	}

	public void setIdarticoliprovvedimenti(long idarticoliprovvedimenti) {
		this.idarticoliprovvedimenti = idarticoliprovvedimenti;
	}


	public String getCorrelazioneinfmalprof() {
		return this.correlazioneinfmalprof;
	}

	public void setCorrelazioneinfmalprof(String correlazioneinfmalprof) {
		this.correlazioneinfmalprof = correlazioneinfmalprof;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacominottautgiud301() {
		return this.datacominottautgiud301;
	}

	public void setDatacominottautgiud301(Date datacominottautgiud301) {
		this.datacominottautgiud301 = datacominottautgiud301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacominottcomune301() {
		return this.datacominottcomune301;
	}

	public void setDatacominottcomune301(Date datacominottcomune301) {
		this.datacominottcomune301 = datacominottcomune301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacominottcontravv301() {
		return this.datacominottcontravv301;
	}

	public void setDatacominottcontravv301(Date datacominottcontravv301) {
		this.datacominottcontravv301 = datacominottcontravv301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomottcomune301() {
		return this.datacomottcomune301;
	}

	public void setDatacomottcomune301(Date datacomottcomune301) {
		this.datacomottcomune301 = datacomottcomune301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanotificaammpag301() {
		return this.datanotificaammpag301;
	}

	public void setDatanotificaammpag301(Date datanotificaammpag301) {
		this.datanotificaammpag301 = datanotificaammpag301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataottemperanza() {
		return this.dataottemperanza;
	}

	public void setDataottemperanza(Date dataottemperanza) {
		this.dataottemperanza = dataottemperanza;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatascadenzaart() {
		return this.datascadenzaart;
	}

	public void setDatascadenzaart(Date datascadenzaart) {
		this.datascadenzaart = datascadenzaart;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatascadenzapag301() {
		return this.datascadenzapag301;
	}

	public void setDatascadenzapag301(Date datascadenzapag301) {
		this.datascadenzapag301 = datascadenzapag301;
	}


	public String getExportimpresa() {
		return this.exportimpresa;
	}

	public void setExportimpresa(String exportimpresa) {
		this.exportimpresa = exportimpresa;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	@Lob
	public String getNoteviolazione() {
		return this.noteviolazione;
	}

	public void setNoteviolazione(String noteviolazione) {
		this.noteviolazione = noteviolazione;
	}


	public BigDecimal getNumgginiziali() {
		return this.numgginiziali;
	}

	public void setNumgginiziali(BigDecimal numgginiziali) {
		this.numgginiziali = numgginiziali;
	}


	public BigDecimal getOrdineart() {
		return this.ordineart;
	}

	public void setOrdineart(BigDecimal ordineart) {
		this.ordineart = ordineart;
	}


	public String getOttemperanza() {
		return this.ottemperanza;
	}

	public void setOttemperanza(String ottemperanza) {
		this.ottemperanza = ottemperanza;
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


	//bi-directional many-to-one association to Articolilegge
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDARTICOLO")
	public Articolilegge getArticolilegge() {
		return this.articolilegge;
	}

	public void setArticolilegge(Articolilegge articolilegge) {
		this.articolilegge = articolilegge;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNECOMUNICOTT301")
	public Comuni getComuneComunicazioneOtt301() {
		return this.comuneComunicazioneOtt301;
	}

	public void setComuneComunicazioneOtt301(Comuni comuni1) {
		this.comuneComunicazioneOtt301 = comuni1;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNECOMUNICINOTT301")
	public Comuni getComuneComunicazioneInott301() {
		return this.comuneComunicazioneInott301;
	}

	public void setComuneComunicazioneInott301(Comuni comuni2) {
		this.comuneComunicazioneInott301 = comuni2;
	}


	//bi-directional many-to-one association to Provvedimenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVVEDIMENTO")
	public ProvvedimentiPrevnet getProvvedimenti() {
		return this.provvedimenti;
	}

	public void setProvvedimenti(ProvvedimentiPrevnet provvedimenti) {
		this.provvedimenti = provvedimenti;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZA")
	public Scadenze getScadenze() {
		return this.scadenze;
	}

	public void setScadenze(Scadenze scadenze) {
		this.scadenze = scadenze;
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


	//bi-directional one-to-one association to Dlgs758iter
	@OneToOne(mappedBy="articoliprovvedimenti", fetch=FetchType.LAZY)
	public Dlgs758iter getDlgs758iter() {
		return this.dlgs758iter;
	}

	public void setDlgs758iter(Dlgs758iter dlgs758iter) {
		this.dlgs758iter = dlgs758iter;
	}


	//bi-directional many-to-one association to Sanzioniarticoli
	@OneToMany(mappedBy="articoliprovvedimenti")
	public List<Sanzioniarticoli> getSanzioniarticolis() {
		return this.sanzioniarticolis;
	}

	public void setSanzioniarticolis(List<Sanzioniarticoli> sanzioniarticolis) {
		this.sanzioniarticolis = sanzioniarticolis;
	}

	public Sanzioniarticoli addSanzioniarticoli(Sanzioniarticoli sanzioniarticoli) {
		getSanzioniarticolis().add(sanzioniarticoli);
		sanzioniarticoli.setArticoliprovvedimenti(this);

		return sanzioniarticoli;
	}

	public Sanzioniarticoli removeSanzioniarticoli(Sanzioniarticoli sanzioniarticoli) {
		getSanzioniarticolis().remove(sanzioniarticoli);
		sanzioniarticoli.setArticoliprovvedimenti(null);

		return sanzioniarticoli;
	}

}