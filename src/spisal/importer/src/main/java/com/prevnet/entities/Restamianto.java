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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the RESTAMIANTO database table.
 * 
 */
@Entity
@NamedQuery(name="Restamianto.findAll", query="SELECT r FROM Restamianto r")
public class Restamianto implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idrestamianto;
	private Date dataesito;
	private Pratiche pratica;
	private BigDecimal idprocedura;
	private String note;
	private BigDecimal numprelievi;
	private Tabelle esito;

	public Restamianto() {
	}


	@Id
	public long getIdrestamianto() {
		return this.idrestamianto;
	}

	public void setIdrestamianto(long idrestamianto) {
		this.idrestamianto = idrestamianto;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataesito() {
		return this.dataesito;
	}

	public void setDataesito(Date dataesito) {
		this.dataesito = dataesito;
	}


	//bi-directional many-to-one association to Pratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}



	public BigDecimal getIdprocedura() {
		return this.idprocedura;
	}

	public void setIdprocedura(BigDecimal idprocedura) {
		this.idprocedura = idprocedura;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getNumprelievi() {
		return this.numprelievi;
	}

	public void setNumprelievi(BigDecimal numprelievi) {
		this.numprelievi = numprelievi;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITO")
	public Tabelle getEsito() {
		return this.esito;
	}

	public void setEsito(Tabelle esito) {
		this.esito = esito;
	}

}