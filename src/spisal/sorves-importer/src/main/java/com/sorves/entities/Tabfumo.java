package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABFUMO database table.
 * 
 */
@Entity
@Table(name="tabfumo")
public class Tabfumo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private long id;

	private List<Visita> visita;

	public Tabfumo() {
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "tabfumo")
	public List<Visita> getVisita() {
	    return visita;
	}

	public void setVisita(List<Visita> param) {
	    this.visita = param;
	}

}