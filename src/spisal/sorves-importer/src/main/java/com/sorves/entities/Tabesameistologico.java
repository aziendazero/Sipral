package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABESAMEISTOLOGICO database table.
 * 
 */
@Entity
@Table(name="tabesameistologico")
public class Tabesameistologico implements Serializable {
	private static final long serialVersionUID = 1L;
	private String descrizione;
	private long id;
	private List<Esameistologico> esameistologico;

	public Tabesameistologico() {
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
	public List<Esameistologico> getEsameistologico() {
	    return esameistologico;
	}


	public void setEsameistologico(List<Esameistologico> param) {
	    this.esameistologico = param;
	}

}