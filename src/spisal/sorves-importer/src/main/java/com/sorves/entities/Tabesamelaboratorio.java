package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABESAMELABORATORIO database table.
 * 
 */
@Entity
@Table(name="tabesamelaboratorio")
public class Tabesamelaboratorio implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private long id;

	private List<Esamilaboratorio> esamilaboratorio;

	public Tabesamelaboratorio() {
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

	@OneToMany(mappedBy = "tipo")
	public List<Esamilaboratorio> getEsamilaboratorio() {
	    return esamilaboratorio;
	}

	public void setEsamilaboratorio(List<Esamilaboratorio> param) {
	    this.esamilaboratorio = param;
	}

}