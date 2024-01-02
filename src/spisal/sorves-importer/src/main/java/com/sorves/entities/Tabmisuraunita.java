package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABMISURAUNITA database table.
 * 
 */
@Entity
@Table(name="tabmisuraunita")
public class Tabmisuraunita implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private long id;

	private List<Esposizione> esposizione;

	public Tabmisuraunita() {
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

	@OneToMany(mappedBy = "tabmisuraunita")
	public List<Esposizione> getEsposizione() {
	    return esposizione;
	}

	public void setEsposizione(List<Esposizione> param) {
	    this.esposizione = param;
	}

}