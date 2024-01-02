package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABOBIETTIVITA database table.
 * 
 */
@Entity
@Table(name="tabobiettivita")
public class Tabobiettivita implements Serializable {
	private static final long serialVersionUID = 1L;
	private String descrizione;
	private long id;
	private String snomed;
	private List<Visita> visita;

	public Tabobiettivita() {
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


	public String getSnomed() {
		return this.snomed;
	}

	public void setSnomed(String snomed) {
		this.snomed = snomed;
	}


	@ManyToMany(mappedBy = "tabobiettivita")
	public List<Visita> getVisita() {
	    return visita;
	}


	public void setVisita(List<Visita> param) {
	    this.visita = param;
	}

}