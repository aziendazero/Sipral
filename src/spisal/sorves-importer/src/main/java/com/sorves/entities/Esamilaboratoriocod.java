package com.sorves.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;



/**
 * The persistent class for the ESAMILABORATORIOCOD database table.
 * 
 */
@Entity
@Table(name="esamilaboratoriocod")
public class Esamilaboratoriocod implements Serializable {
	private static final long serialVersionUID = 1L;

	private String esito;
	private long id;
	private Double risultato;
	private Esamilaboratorio esamilaboratorio;
	private Tabesami tabesami;

	public Esamilaboratoriocod() {
	}

	public String getEsito() {
		return this.esito;
	}

	public void setEsito(String esito) {
		this.esito = esito;
	}

	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Double getRisultato() {
		return this.risultato;
	}

	public void setRisultato(Double risultato) {
		this.risultato = risultato;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idesame")
	public Esamilaboratorio getEsamilaboratorio() {
	    return esamilaboratorio;
	}

	public void setEsamilaboratorio(Esamilaboratorio param) {
	    this.esamilaboratorio = param;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "iddiagnosi")
	public Tabesami getTabesami() {
	    return tabesami;
	}

	public void setTabesami(Tabesami param) {
	    this.tabesami = param;
	}

}