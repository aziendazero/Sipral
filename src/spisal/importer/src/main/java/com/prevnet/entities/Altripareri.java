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
 * The persistent class for the ALTRIPARERI database table.
 * 
 */
@Entity
@NamedQuery(name="Altripareri.findAll", query="SELECT a FROM Altripareri a")
public class Altripareri implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idaltripareri;
	private Date data;
	private BigDecimal idstoricoutenteemessoda;
	private String note;
	private BigDecimal tipoemessoda;
	private Anagrafiche emessoDa;
	private Ditte ditte;
	private Parerigen parerigen;
	private Tabelle parere;

	public Altripareri() {
	}


	@Id
	public long getIdaltripareri() {
		return this.idaltripareri;
	}

	public void setIdaltripareri(long idaltripareri) {
		this.idaltripareri = idaltripareri;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"")
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	public BigDecimal getIdstoricoutenteemessoda() {
		return this.idstoricoutenteemessoda;
	}

	public void setIdstoricoutenteemessoda(BigDecimal idstoricoutenteemessoda) {
		this.idstoricoutenteemessoda = idstoricoutenteemessoda;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getTipoemessoda() {
		return this.tipoemessoda;
	}

	public void setTipoemessoda(BigDecimal tipoemessoda) {
		this.tipoemessoda = tipoemessoda;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDEMESSODA")
	public Anagrafiche getEmessoDa() {
		return this.emessoDa;
	}

	public void setEmessoDa(Anagrafiche emessoDa) {
		this.emessoDa = emessoDa;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTAEMESSODA")
	public Ditte getDitte() {
		return this.ditte;
	}

	public void setDitte(Ditte ditte) {
		this.ditte = ditte;
	}


	//bi-directional many-to-one association to Parerigen
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPAREREGEN")
	public Parerigen getParerigen() {
		return this.parerigen;
	}

	public void setParerigen(Parerigen parerigen) {
		this.parerigen = parerigen;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPARERE")
	public Tabelle getParere() {
		return this.parere;
	}

	public void setParere(Tabelle parere) {
		this.parere = parere;
	}

}