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
 * The persistent class for the PERSONAPRESENTESOP database table.
 * 
 */
@Entity
public class Personapresentesop implements Serializable {
	private static final long serialVersionUID = 1L;
	private PersonapresentesopPK id;
	private Utenti persona;
	private Sopralluoghidip sopralluoghidip;
	private Tabelle ruolo;

	public Personapresentesop() {
	}
	
	@EmbeddedId
	public PersonapresentesopPK getId() {
		return this.id;
	}

	public void setId(PersonapresentesopPK id) {
		this.id = id;
	}
	
	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPERSONAPRESENTESOP")
	public Utenti getPersona() {
		return this.persona;
	}

	public void setPersona(Utenti utenti) {
		this.persona = utenti;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGO")
	public Sopralluoghidip getSopralluoghidip() {
		return this.sopralluoghidip;
	}

	public void setSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		this.sopralluoghidip = sopralluoghidip;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRUOLO")
	public Tabelle getRuolo() {
		return this.ruolo;
	}

	public void setRuolo(Tabelle ruolo) {
		this.ruolo = ruolo;
	}

}