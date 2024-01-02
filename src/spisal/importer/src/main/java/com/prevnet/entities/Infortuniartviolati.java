package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the INFORTUNIARTVIOLATI database table.
 * 
 */
@Entity
public class Infortuniartviolati implements Serializable {
	private static final long serialVersionUID = 1L;
	private InfortuniartviolatiPK id;
	private Infortuniindagati infortuniindagati;
	private Tabelle articoloLegge;
	
	public Infortuniartviolati() {
	}


	@EmbeddedId
	public InfortuniartviolatiPK getId() {
		return this.id;
	}

	public void setId(InfortuniartviolatiPK id) {
		this.id = id;
	}


	//bi-directional many-to-one association to Infortuniindagati
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINFORTUNIOINDAGATO")
	public Infortuniindagati getInfortuniindagati() {
		return this.infortuniindagati;
	}

	public void setInfortuniindagati(Infortuniindagati infortuniindagati) {
		this.infortuniindagati = infortuniindagati;
	}
	
	
	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDARTICOLOLEGGE")
	public Tabelle getArticoloLegge() {
		return articoloLegge;
	}


	public void setArticoloLegge(Tabelle articoloLegge) {
		this.articoloLegge = articoloLegge;
	}
}