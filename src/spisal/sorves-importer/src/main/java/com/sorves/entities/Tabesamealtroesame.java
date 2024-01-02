package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABESAMEALTROESAME database table.
 * 
 */
@Entity
@Table(name="tabesamealtroesame")
public class Tabesamealtroesame implements Serializable {
	private static final long serialVersionUID = 1L;
	private String descrizione;
	private long id;
	private List<Esamealtro> esamealtro;

	public Tabesamealtroesame() {
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
	public List<Esamealtro> getEsamealtro() {
	    return esamealtro;
	}


	public void setEsamealtro(List<Esamealtro> param) {
	    this.esamealtro = param;
	}

}