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
 * The persistent class for the SITSOPRALLUOGHI database table.
 * 
 */
@Entity
public class Sitsopralluoghi implements Serializable {
	private static final long serialVersionUID = 1L;
	private SitsopralluoghiPK id;
	private String chkteste;
	private String note;
	private Sopralluoghidip sopralluoghidip;
	private Tabelle ruolo;
	private Utenti utenti;

	public Sitsopralluoghi() {
	}

	@EmbeddedId
	public SitsopralluoghiPK getId() {
		return this.id;
	}

	public void setId(SitsopralluoghiPK id) {
		this.id = id;
	}
	
	public String getChkteste() {
		return this.chkteste;
	}

	public void setChkteste(String chkteste) {
		this.chkteste = chkteste;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	//bi-directional many-to-one association to Sopralluoghidip
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGHI")
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


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUTENTE")
	public Utenti getUtenti() {
		return this.utenti;
	}

	public void setUtenti(Utenti utenti) {
		this.utenti = utenti;
	}

}