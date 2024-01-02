package com.sorves.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the TABPRESTAZIONI database table.
 * 
 */
@Entity
@Table(name="tabprestazioni")
public class Tabprestazioni implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private long id;

	public Tabprestazioni() {
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