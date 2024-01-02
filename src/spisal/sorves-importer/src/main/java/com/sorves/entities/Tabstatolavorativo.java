package com.sorves.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the TABSTATOLAVORATIVO database table.
 * 
 */
@Entity
@Table(name="tabstatolavorativo")
public class Tabstatolavorativo implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private long id;

	public Tabstatolavorativo() {
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

}