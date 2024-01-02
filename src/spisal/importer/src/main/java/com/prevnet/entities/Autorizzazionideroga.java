package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * The persistent class for the AUTORIZZAZIONIDEROGA database table.
 * 
 */
@Entity
@NamedQuery(name="Autorizzazionideroga.findAll", query="SELECT a FROM Autorizzazionideroga a")
public class Autorizzazionideroga implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idautorizzazionideroga;
	private String chkritirata;
	private Date dataautorizzazione;
	private Date datafine;
	private Date datainizio;
	private Date dataritiro;
	private String definitivo;
	private BigDecimal idanagrafica;
	private BigDecimal idschedacantiere;
	private BigDecimal idschedaditta;
	private String note;
	private String numero;
	private BigDecimal tipoanagrafica;
	private Pratiche pratiche;

	private Tabelle tabelle1;
	private Tabelle tabelle2;

	public Autorizzazionideroga() {
	}


	@Id
	public long getIdautorizzazionideroga() {
		return this.idautorizzazionideroga;
	}

	public void setIdautorizzazionideroga(long idautorizzazionideroga) {
		this.idautorizzazionideroga = idautorizzazionideroga;
	}


	public String getChkritirata() {
		return this.chkritirata;
	}

	public void setChkritirata(String chkritirata) {
		this.chkritirata = chkritirata;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataautorizzazione() {
		return this.dataautorizzazione;
	}

	public void setDataautorizzazione(Date dataautorizzazione) {
		this.dataautorizzazione = dataautorizzazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafine() {
		return this.datafine;
	}

	public void setDatafine(Date datafine) {
		this.datafine = datafine;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizio() {
		return this.datainizio;
	}

	public void setDatainizio(Date datainizio) {
		this.datainizio = datainizio;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataritiro() {
		return this.dataritiro;
	}

	public void setDataritiro(Date dataritiro) {
		this.dataritiro = dataritiro;
	}


	public String getDefinitivo() {
		return this.definitivo;
	}

	public void setDefinitivo(String definitivo) {
		this.definitivo = definitivo;
	}


	public BigDecimal getIdanagrafica() {
		return this.idanagrafica;
	}

	public void setIdanagrafica(BigDecimal idanagrafica) {
		this.idanagrafica = idanagrafica;
	}



	public BigDecimal getIdschedacantiere() {
		return this.idschedacantiere;
	}

	public void setIdschedacantiere(BigDecimal idschedacantiere) {
		this.idschedacantiere = idschedacantiere;
	}


	public BigDecimal getIdschedaditta() {
		return this.idschedaditta;
	}

	public void setIdschedaditta(BigDecimal idschedaditta) {
		this.idschedaditta = idschedaditta;
	}



	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNumero() {
		return this.numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}


	public BigDecimal getTipoanagrafica() {
		return this.tipoanagrafica;
	}

	public void setTipoanagrafica(BigDecimal tipoanagrafica) {
		this.tipoanagrafica = tipoanagrafica;
	}


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDPRATICA")
	public Pratiche getPratiche() {
		return this.pratiche;
	}

	public void setPratiche(Pratiche pratiche) {
		this.pratiche = pratiche;
	}

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDTIPOAUTORIZZAZIONI")
	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDESITO")
	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}

}