package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the TBLBLOB database table.
 * 
 */
@Entity
@NamedQuery(name="Tblblob.findAll", query="SELECT t FROM Tblblob t")
public class Tblblob implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idtblblob;
	private Date data;
	private Date dataultmod;
	private String descrizionefile;
	private String fontedoc;
	private BigDecimal iddati;
	private BigDecimal idprotocollo;
	private byte[] immaginefile;
	private String nomefile;
	private Operatori operatori;
	private Pratiche pratica;
	private Tabellastampe tabellastampe;

	public Tblblob() {
	}


	@Id
	public long getIdtblblob() {
		return this.idtblblob;
	}

	public void setIdtblblob(long idtblblob) {
		this.idtblblob = idtblblob;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"")
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultmod() {
		return this.dataultmod;
	}

	public void setDataultmod(Date dataultmod) {
		this.dataultmod = dataultmod;
	}


	public String getDescrizionefile() {
		return this.descrizionefile;
	}

	public void setDescrizionefile(String descrizionefile) {
		this.descrizionefile = descrizionefile;
	}


	public String getFontedoc() {
		return this.fontedoc;
	}

	public void setFontedoc(String fontedoc) {
		this.fontedoc = fontedoc;
	}


	public BigDecimal getIddati() {
		return this.iddati;
	}

	public void setIddati(BigDecimal iddati) {
		this.iddati = iddati;
	}


	public BigDecimal getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(BigDecimal idprotocollo) {
		this.idprotocollo = idprotocollo;
	}


	@Lob
	public byte[] getImmaginefile() {
		return this.immaginefile;
	}

	public void setImmaginefile(byte[] immaginefile) {
		this.immaginefile = immaginefile;
	}


	public String getNomefile() {
		return this.nomefile;
	}

	public void setNomefile(String nomefile) {
		this.nomefile = nomefile;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatori() {
		return this.operatori;
	}

	public void setOperatori(Operatori operatori) {
		this.operatori = operatori;
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


	//bi-directional many-to-one association to Tabellastampe
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTAMPA")
	public Tabellastampe getTabellastampe() {
		return this.tabellastampe;
	}

	public void setTabellastampe(Tabellastampe tabellastampe) {
		this.tabellastampe = tabellastampe;
	}

}