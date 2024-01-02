package com.prevnet.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the INFORTUNIINDAGATI database table.
 * 
 */
@Entity
public class Infortuniindagati implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idinfortuniindagati;
	private List<Infortuniartviolati> infortuniartviolatis;
	private Pratiche pratiche;
	private Utenti utenti;

	public Infortuniindagati() {
	}


	@Id
	public long getIdinfortuniindagati() {
		return this.idinfortuniindagati;
	}

	public void setIdinfortuniindagati(long idinfortuniindagati) {
		this.idinfortuniindagati = idinfortuniindagati;
	}


	//bi-directional many-to-one association to Infortuniartviolati
	@OneToMany(mappedBy="infortuniindagati")
	public List<Infortuniartviolati> getInfortuniartviolatis() {
		return this.infortuniartviolatis;
	}

	public void setInfortuniartviolatis(List<Infortuniartviolati> infortuniartviolatis) {
		this.infortuniartviolatis = infortuniartviolatis;
	}

	public Infortuniartviolati addInfortuniartviolati(Infortuniartviolati infortuniartviolati) {
		getInfortuniartviolatis().add(infortuniartviolati);
		infortuniartviolati.setInfortuniindagati(this);

		return infortuniartviolati;
	}

	public Infortuniartviolati removeInfortuniartviolati(Infortuniartviolati infortuniartviolati) {
		getInfortuniartviolatis().remove(infortuniartviolati);
		infortuniartviolati.setInfortuniindagati(null);

		return infortuniartviolati;
	}


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratiche() {
		return this.pratiche;
	}

	public void setPratiche(Pratiche pratiche) {
		this.pratiche = pratiche;
	}

	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICAUTENTE")
	public Utenti getUtenti() {
		return this.utenti;
	}

	public void setUtenti(Utenti utenti) {
		this.utenti = utenti;
	}

}