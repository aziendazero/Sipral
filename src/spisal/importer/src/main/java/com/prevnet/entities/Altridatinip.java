package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ALTRIDATINIP database table.
 * 
 */
@Entity
@NamedQuery(name="Altridatinip.findAll", query="SELECT a FROM Altridatinip a")
public class Altridatinip implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idaltridatinip;
	private BigDecimal esito;
	private Pratiche pratica;
	private BigDecimal idstoricoutente;
	private String tipoanagrafica;
	private Anagrafiche gestore;
	private Ditte schedaDitta;

	public Altridatinip() {
	}


	@Id
	public long getIdaltridatinip() {
		return this.idaltridatinip;
	}

	public void setIdaltridatinip(long idaltridatinip) {
		this.idaltridatinip = idaltridatinip;
	}


	public BigDecimal getEsito() {
		return this.esito;
	}

	public void setEsito(BigDecimal esito) {
		this.esito = esito;
	}

	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public String getTipoanagrafica() {
		return this.tipoanagrafica;
	}

	public void setTipoanagrafica(String tipoanagrafica) {
		this.tipoanagrafica = tipoanagrafica;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDGESTORE")
	public Anagrafiche getGestore() {
		return this.gestore;
	}

	public void setGestore(Anagrafiche gestore) {
		this.gestore = gestore;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getSchedaDitta() {
		return this.schedaDitta;
	}

	public void setSchedaDitta(Ditte schedaDitta) {
		this.schedaDitta = schedaDitta;
	}

}