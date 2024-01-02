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
 * The persistent class for the ATTI database table.
 * 
 */
@Entity
@NamedQuery(name="Atti.findAll", query="SELECT a FROM Atti a")
public class Atti implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idatti;
	private String daaltropasso;
	private Date data;
	private BigDecimal idesposto;
	private BigDecimal idintegrazionedocin;
	private BigDecimal idintegrazionedocout;
	private BigDecimal idparerigen;
	private BigDecimal idparerigenerici;
	private BigDecimal idparerinip;
	private BigDecimal idprotocollo;
	private BigDecimal idsanzione;
	private String inpartenza;
	private String note;
	private String nprotocollo;
	private BigDecimal quantita;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private Pratiche pratica;
	private ProvvedimentiPrevnet provvedimento;
	private Richiestaregistrazione richiestaregistrazione;
	private Tabelle tabelle1;
	private Tabelle tabelle2;
	private Tabelle tabelle3;
	private Tabelle tabelle4;
	private List<Documentiscan> documentiscans;

	public Atti() {
	}


	@Id
	public long getIdatti() {
		return this.idatti;
	}

	public void setIdatti(long idatti) {
		this.idatti = idatti;
	}


	public String getDaaltropasso() {
		return this.daaltropasso;
	}

	public void setDaaltropasso(String daaltropasso) {
		this.daaltropasso = daaltropasso;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"")
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	public BigDecimal getIdesposto() {
		return this.idesposto;
	}

	public void setIdesposto(BigDecimal idesposto) {
		this.idesposto = idesposto;
	}


	public BigDecimal getIdintegrazionedocin() {
		return this.idintegrazionedocin;
	}

	public void setIdintegrazionedocin(BigDecimal idintegrazionedocin) {
		this.idintegrazionedocin = idintegrazionedocin;
	}


	public BigDecimal getIdintegrazionedocout() {
		return this.idintegrazionedocout;
	}

	public void setIdintegrazionedocout(BigDecimal idintegrazionedocout) {
		this.idintegrazionedocout = idintegrazionedocout;
	}


	public BigDecimal getIdparerigen() {
		return this.idparerigen;
	}

	public void setIdparerigen(BigDecimal idparerigen) {
		this.idparerigen = idparerigen;
	}


	public BigDecimal getIdparerigenerici() {
		return this.idparerigenerici;
	}

	public void setIdparerigenerici(BigDecimal idparerigenerici) {
		this.idparerigenerici = idparerigenerici;
	}


	public BigDecimal getIdparerinip() {
		return this.idparerinip;
	}

	public void setIdparerinip(BigDecimal idparerinip) {
		this.idparerinip = idparerinip;
	}


	public BigDecimal getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(BigDecimal idprotocollo) {
		this.idprotocollo = idprotocollo;
	}


	public BigDecimal getIdsanzione() {
		return this.idsanzione;
	}

	public void setIdsanzione(BigDecimal idsanzione) {
		this.idsanzione = idsanzione;
	}


	public String getInpartenza() {
		return this.inpartenza;
	}

	public void setInpartenza(String inpartenza) {
		this.inpartenza = inpartenza;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNprotocollo() {
		return this.nprotocollo;
	}

	public void setNprotocollo(String nprotocollo) {
		this.nprotocollo = nprotocollo;
	}


	public BigDecimal getQuantita() {
		return this.quantita;
	}

	public void setQuantita(BigDecimal quantita) {
		this.quantita = quantita;
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


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	//bi-directional many-to-one association to Provvedimenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVVEDIMENTO")
	public ProvvedimentiPrevnet getProvvedimento() {
		return this.provvedimento;
	}

	public void setProvvedimento(ProvvedimentiPrevnet provvedimento) {
		this.provvedimento = provvedimento;
	}


	//bi-directional many-to-one association to Richiestaregistrazione
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDREGRICHIESTA")
	public Richiestaregistrazione getRichiestaregistrazione() {
		return this.richiestaregistrazione;
	}

	public void setRichiestaregistrazione(Richiestaregistrazione richiestaregistrazione) {
		this.richiestaregistrazione = richiestaregistrazione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPO")
	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDESTINAZIONE")
	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVENIENZA")
	public Tabelle getTabelle3() {
		return this.tabelle3;
	}

	public void setTabelle3(Tabelle tabelle3) {
		this.tabelle3 = tabelle3;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASL")
	public Tabelle getTabelle4() {
		return this.tabelle4;
	}

	public void setTabelle4(Tabelle tabelle4) {
		this.tabelle4 = tabelle4;
	}


	//bi-directional many-to-one association to Documentiscan
	@OneToMany(mappedBy="atti")
	public List<Documentiscan> getDocumentiscans() {
		return this.documentiscans;
	}

	public void setDocumentiscans(List<Documentiscan> documentiscans) {
		this.documentiscans = documentiscans;
	}

	public Documentiscan addDocumentiscan(Documentiscan documentiscan) {
		getDocumentiscans().add(documentiscan);
		documentiscan.setAtti(this);

		return documentiscan;
	}

	public Documentiscan removeDocumentiscan(Documentiscan documentiscan) {
		getDocumentiscans().remove(documentiscan);
		documentiscan.setAtti(null);

		return documentiscan;
	}

}