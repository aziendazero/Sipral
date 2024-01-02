package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABCANCEROGENI database table.
 * 
 */
@Entity
@Table(name="tabcancerogeni")
public class Tabcancerogeni implements Serializable {
	private static final long serialVersionUID = 1L;

	private String cas;
	private String descrizione;
	private long id;

	private List<Esposizionecod> esposizionecod;

	public Tabcancerogeni() {
	}

	public String getCas() {
		return this.cas;
	}

	public void setCas(String cas) {
		this.cas = cas;
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

	@OneToMany(mappedBy = "tabcancerogeni")
	public List<Esposizionecod> getEsposizionecod() {
	    return esposizionecod;
	}

	public void setEsposizionecod(List<Esposizionecod> param) {
	    this.esposizionecod = param;
	}

}