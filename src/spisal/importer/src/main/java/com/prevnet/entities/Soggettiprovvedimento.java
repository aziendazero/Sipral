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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the SOGGETTIPROVVEDIMENTO database table.
 * 
 */
@Entity
@NamedQuery(name="Soggettiprovvedimento.findAll", query="SELECT s FROM Soggettiprovvedimento s")
public class Soggettiprovvedimento implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idprovvedimento;
	private Date datarilasciodoc;
	private Integer enterilasciantedoc;
	private Comuni comuneDoc;
	private Anagrafiche soggetto;
	private BigDecimal idstoricoutentesoggetti;
	private String numerodoc;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private Integer tipodoc;
	private ProvvedimentiPrevnet provvedimenti;
	private Tabelle ruolo;
	private Tabelle provinciaDoc;
	private Tabelle statoEsteroDoc;

	public Soggettiprovvedimento() {
	}


	@Id
	public long getIdprovvedimento() {
		return this.idprovvedimento;
	}

	public void setIdprovvedimento(long idprovvedimento) {
		this.idprovvedimento = idprovvedimento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarilasciodoc() {
		return this.datarilasciodoc;
	}

	public void setDatarilasciodoc(Date datarilasciodoc) {
		this.datarilasciodoc = datarilasciodoc;
	}


	public Integer getEnterilasciantedoc() {
		return this.enterilasciantedoc;
	}

	public void setEnterilasciantedoc(Integer enterilasciantedoc) {
		this.enterilasciantedoc = enterilasciantedoc;
	}
	
	
	public BigDecimal getIdstoricoutentesoggetti() {
		return this.idstoricoutentesoggetti;
	}

	public void setIdstoricoutentesoggetti(BigDecimal idstoricoutentesoggetti) {
		this.idstoricoutentesoggetti = idstoricoutentesoggetti;
	}


	public String getNumerodoc() {
		return this.numerodoc;
	}

	public void setNumerodoc(String numerodoc) {
		this.numerodoc = numerodoc;
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


	public Integer getTipodoc() {
		return this.tipodoc;
	}

	public void setTipodoc(Integer tipodoc) {
		this.tipodoc = tipodoc;
	}
	
	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEDOC")
	public Comuni getComuneDoc() {
		return this.comuneDoc;
	}

	public void setComuneDoc(Comuni comuneDoc) {
		this.comuneDoc = comuneDoc;
	}
	
	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOGGETTI")
	public Anagrafiche getSoggetto() {
		return this.soggetto;
	}

	public void setSoggetto(Anagrafiche soggetto) {
		this.soggetto = soggetto;
	}


	//bi-directional one-to-one association to Provvedimenti
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDPROVVEDIMENTO")
	public ProvvedimentiPrevnet getProvvedimenti() {
		return this.provvedimenti;
	}

	public void setProvvedimenti(ProvvedimentiPrevnet provvedimenti) {
		this.provvedimenti = provvedimenti;
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


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVINCIADOC")
	public Tabelle getProvinciaDoc() {
		return this.provinciaDoc;
	}

	public void setProvinciaDoc(Tabelle provinciaDoc) {
		this.provinciaDoc = provinciaDoc;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTATOESTERODOC")
	public Tabelle getStatoEsteroDoc() {
		return this.statoEsteroDoc;
	}

	public void setStatoEsteroDoc(Tabelle statoEsteroDoc) {
		this.statoEsteroDoc = statoEsteroDoc;
	}

}