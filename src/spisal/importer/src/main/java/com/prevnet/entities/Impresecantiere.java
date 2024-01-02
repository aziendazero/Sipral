package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the IMPRESECANTIERE database table.
 * 
 */
@Entity
public class Impresecantiere implements Serializable {
	private static final long serialVersionUID = 1L;
	private ImpresecantierePK id;
	private String attivitasvolta;
	private String chkpospresente;
	private BigDecimal contatore;
	private Date datainserimento;
	private Date dataultvalutpos;
	private String eseclavori;
	private BigDecimal idconsorzioati;
	private String impresaprincipale;
	private String lavautonomo;
	private String subappalto;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private String tiponolo;
	private Anagrafiche impresa;
	private Ditte dittaSubappalto;
	private Cantieri cantieri;
	private Tabelle esitoUltimaVal;

	public Impresecantiere() {
	}


	@EmbeddedId
	public ImpresecantierePK getId() {
		return this.id;
	}

	public void setId(ImpresecantierePK id) {
		this.id = id;
	}


	public String getAttivitasvolta() {
		return this.attivitasvolta;
	}

	public void setAttivitasvolta(String attivitasvolta) {
		this.attivitasvolta = attivitasvolta;
	}


	public String getChkpospresente() {
		return this.chkpospresente;
	}

	public void setChkpospresente(String chkpospresente) {
		this.chkpospresente = chkpospresente;
	}


	public BigDecimal getContatore() {
		return this.contatore;
	}

	public void setContatore(BigDecimal contatore) {
		this.contatore = contatore;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainserimento() {
		return this.datainserimento;
	}

	public void setDatainserimento(Date datainserimento) {
		this.datainserimento = datainserimento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultvalutpos() {
		return this.dataultvalutpos;
	}

	public void setDataultvalutpos(Date dataultvalutpos) {
		this.dataultvalutpos = dataultvalutpos;
	}


	public String getEseclavori() {
		return this.eseclavori;
	}

	public void setEseclavori(String eseclavori) {
		this.eseclavori = eseclavori;
	}


	public BigDecimal getIdconsorzioati() {
		return this.idconsorzioati;
	}

	public void setIdconsorzioati(BigDecimal idconsorzioati) {
		this.idconsorzioati = idconsorzioati;
	}


	public String getImpresaprincipale() {
		return this.impresaprincipale;
	}

	public void setImpresaprincipale(String impresaprincipale) {
		this.impresaprincipale = impresaprincipale;
	}


	public String getLavautonomo() {
		return this.lavautonomo;
	}

	public void setLavautonomo(String lavautonomo) {
		this.lavautonomo = lavautonomo;
	}


	public String getSubappalto() {
		return this.subappalto;
	}

	public void setSubappalto(String subappalto) {
		this.subappalto = subappalto;
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


	public String getTiponolo() {
		return this.tiponolo;
	}

	public void setTiponolo(String tiponolo) {
		this.tiponolo = tiponolo;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDIMPRESE")
	public Anagrafiche getImpresa() {
		return this.impresa;
	}

	public void setImpresa(Anagrafiche impresa) {
		this.impresa = impresa;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTASUBAPPALTO")
	public Ditte getDittaSubappalto() {
		return this.dittaSubappalto;
	}

	public void setDittaSubappalto(Ditte dittaSubappalto) {
		this.dittaSubappalto = dittaSubappalto;
	}


	//bi-directional many-to-one association to Cantieri
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCANTIERE")
	public Cantieri getCantieri() {
		return this.cantieri;
	}

	public void setCantieri(Cantieri cantieri) {
		this.cantieri = cantieri;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITOULTIMAVALPOS")
	public Tabelle getEsitoUltimaVal() {
		return this.esitoUltimaVal;
	}

	public void setEsitoUltimaVal(Tabelle esitoUltimaVal) {
		this.esitoUltimaVal = esitoUltimaVal;
	}

}