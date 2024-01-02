package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the FASCICOLI database table.
 * 
 */
@Entity
@Table(name="FASCICOLI")
public class FascicoliPrevnet implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idfascicoli;
	private BigDecimal anno;
	private String descrizione;
	private BigDecimal idasl;
	private BigDecimal idcodifica;
	private String note;
	private BigDecimal numero;
	private String stato;

	public FascicoliPrevnet() {
	}


	@Id
	@Column(unique=true, nullable=false, precision=22)
	public long getIdfascicoli() {
		return this.idfascicoli;
	}

	public void setIdfascicoli(long idfascicoli) {
		this.idfascicoli = idfascicoli;
	}


	@Column(nullable=false, precision=22)
	public BigDecimal getAnno() {
		return this.anno;
	}

	public void setAnno(BigDecimal anno) {
		this.anno = anno;
	}


	@Column(length=255)
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	@Column(precision=22)
	public BigDecimal getIdasl() {
		return this.idasl;
	}

	public void setIdasl(BigDecimal idasl) {
		this.idasl = idasl;
	}


	@Column(precision=22)
	public BigDecimal getIdcodifica() {
		return this.idcodifica;
	}

	public void setIdcodifica(BigDecimal idcodifica) {
		this.idcodifica = idcodifica;
	}


	@Column(length=4000)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	@Column(nullable=false, precision=22)
	public BigDecimal getNumero() {
		return this.numero;
	}

	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}


	@Column(nullable=false, length=1)
	public String getStato() {
		return this.stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

}