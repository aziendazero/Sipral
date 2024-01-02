package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the LEGGI database table.
 * 
 */
@Entity
@NamedQuery(name="Leggi.findAll", query="SELECT l FROM Leggi l")
public class Leggi implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idleggi;
	private BigDecimal anno;
	private String approssimazione;
	private String codice;
	private String codiceregionale;
	private Date datafinevalidita;
	private Date datainiziovalidita;
	private String descrizione;
	private String invigore;
	private BigDecimal maxnumarticoli;
	private String note;
	private BigDecimal numero;
	private List<Articolilegge> articolilegges;

	public Leggi() {
	}


	@Id
	public long getIdleggi() {
		return this.idleggi;
	}

	public void setIdleggi(long idleggi) {
		this.idleggi = idleggi;
	}


	public BigDecimal getAnno() {
		return this.anno;
	}

	public void setAnno(BigDecimal anno) {
		this.anno = anno;
	}


	public String getApprossimazione() {
		return this.approssimazione;
	}

	public void setApprossimazione(String approssimazione) {
		this.approssimazione = approssimazione;
	}


	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}


	public String getCodiceregionale() {
		return this.codiceregionale;
	}

	public void setCodiceregionale(String codiceregionale) {
		this.codiceregionale = codiceregionale;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafinevalidita() {
		return this.datafinevalidita;
	}

	public void setDatafinevalidita(Date datafinevalidita) {
		this.datafinevalidita = datafinevalidita;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainiziovalidita() {
		return this.datainiziovalidita;
	}

	public void setDatainiziovalidita(Date datainiziovalidita) {
		this.datainiziovalidita = datainiziovalidita;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public String getInvigore() {
		return this.invigore;
	}

	public void setInvigore(String invigore) {
		this.invigore = invigore;
	}


	public BigDecimal getMaxnumarticoli() {
		return this.maxnumarticoli;
	}

	public void setMaxnumarticoli(BigDecimal maxnumarticoli) {
		this.maxnumarticoli = maxnumarticoli;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getNumero() {
		return this.numero;
	}

	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}


	//bi-directional many-to-one association to Articolilegge
	@OneToMany(mappedBy="leggi")
	public List<Articolilegge> getArticolilegges() {
		return this.articolilegges;
	}

	public void setArticolilegges(List<Articolilegge> articolilegges) {
		this.articolilegges = articolilegges;
	}

	public Articolilegge addArticolilegge(Articolilegge articolilegge) {
		getArticolilegges().add(articolilegge);
		articolilegge.setLeggi(this);

		return articolilegge;
	}

	public Articolilegge removeArticolilegge(Articolilegge articolilegge) {
		getArticolilegges().remove(articolilegge);
		articolilegge.setLeggi(null);

		return articolilegge;
	}

}