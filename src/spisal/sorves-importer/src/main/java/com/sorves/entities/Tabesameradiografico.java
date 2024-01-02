package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABESAMERADIOGRAFICO database table.
 * 
 */
@Entity
@Table(name="tabesameradiografico")
public class Tabesameradiografico implements Serializable {
	private static final long serialVersionUID = 1L;
	private String descrizione;
	private long id;
	private List<Radiografia> radiografia;

	public Tabesameradiografico() {
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
	public List<Radiografia> getRadiografia() {
	    return radiografia;
	}


	public void setRadiografia(List<Radiografia> param) {
	    this.radiografia = param;
	}

}