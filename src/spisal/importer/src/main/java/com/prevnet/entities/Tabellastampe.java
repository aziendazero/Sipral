package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the TABELLASTAMPE database table.
 * 
 */
@Entity
@NamedQuery(name="Tabellastampe.findAll", query="SELECT t FROM Tabellastampe t")
public class Tabellastampe implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idtabellastampe;
	private String cancellabile;
	private String descrizione;
	private BigDecimal gruppo;
	private String integrazionedoc;
	private String nomefile;
	private String note;
	private BigDecimal tipo;
	private String tipoweb;
	private Tabelle tabelle;
	private List<Tblblob> tblblobs;

	public Tabellastampe() {
	}


	@Id
	public long getIdtabellastampe() {
		return this.idtabellastampe;
	}

	public void setIdtabellastampe(long idtabellastampe) {
		this.idtabellastampe = idtabellastampe;
	}


	public String getCancellabile() {
		return this.cancellabile;
	}

	public void setCancellabile(String cancellabile) {
		this.cancellabile = cancellabile;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public BigDecimal getGruppo() {
		return this.gruppo;
	}

	public void setGruppo(BigDecimal gruppo) {
		this.gruppo = gruppo;
	}


	public String getIntegrazionedoc() {
		return this.integrazionedoc;
	}

	public void setIntegrazionedoc(String integrazionedoc) {
		this.integrazionedoc = integrazionedoc;
	}


	public String getNomefile() {
		return this.nomefile;
	}

	public void setNomefile(String nomefile) {
		this.nomefile = nomefile;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getTipo() {
		return this.tipo;
	}

	public void setTipo(BigDecimal tipo) {
		this.tipo = tipo;
	}


	public String getTipoweb() {
		return this.tipoweb;
	}

	public void setTipoweb(String tipoweb) {
		this.tipoweb = tipoweb;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDGRUPPOSTAMPA")
	public Tabelle getTabelle() {
		return this.tabelle;
	}

	public void setTabelle(Tabelle tabelle) {
		this.tabelle = tabelle;
	}


	//bi-directional many-to-one association to Tblblob
	@OneToMany(mappedBy="tabellastampe")
	public List<Tblblob> getTblblobs() {
		return this.tblblobs;
	}

	public void setTblblobs(List<Tblblob> tblblobs) {
		this.tblblobs = tblblobs;
	}

	public Tblblob addTblblob(Tblblob tblblob) {
		getTblblobs().add(tblblob);
		tblblob.setTabellastampe(this);

		return tblblob;
	}

	public Tblblob removeTblblob(Tblblob tblblob) {
		getTblblobs().remove(tblblob);
		tblblob.setTabellastampe(null);

		return tblblob;
	}

}