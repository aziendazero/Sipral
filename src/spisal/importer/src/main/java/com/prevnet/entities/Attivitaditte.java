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
 * The persistent class for the ATTIVITADITTE database table.
 * 
 */
@Entity
public class Attivitaditte implements Serializable {
	private static final long serialVersionUID = 1L;
	private AttivitadittePK id;
	private BigDecimal contatore;
	private Date datafine;
	private Date datainizio;
	private AttivitaPrevnet attivitaPrevnet;
	private Ditte ditta;

	public Attivitaditte() {
	}


	@EmbeddedId
	public AttivitadittePK getId() {
		return this.id;
	}

	public void setId(AttivitadittePK id) {
		this.id = id;
	}


	public BigDecimal getContatore() {
		return this.contatore;
	}

	public void setContatore(BigDecimal contatore) {
		this.contatore = contatore;
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


	//bi-directional many-to-one association to Attivita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITA")
	public AttivitaPrevnet getAttivitaPrevnet() {
		return this.attivitaPrevnet;
	}

	public void setAttivitaPrevnet(AttivitaPrevnet attivitaPrevnet) {
		this.attivitaPrevnet = attivitaPrevnet;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Ditte getDitta() {
		return this.ditta;
	}

	public void setDitta(Ditte ditte) {
		this.ditta = ditte;
	}

}