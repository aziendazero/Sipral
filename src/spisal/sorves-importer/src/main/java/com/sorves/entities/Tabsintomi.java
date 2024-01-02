package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABSINTOMI database table.
 * 
 */
@Entity
@Table(name="tabsintomi")
public class Tabsintomi implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private long id;
	private String snomed;

	private List<Anamnesifamiliare> anamnesifamiliare;

	private List<Anamnesifisiologica> anamnesifisiologica;

	private List<Anamnesiremota> anamnesiremota;

	public Tabsintomi() {
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

	@ManyToMany(mappedBy = "tabsintomi")
	public List<Anamnesifamiliare> getAnamnesifamiliare() {
	    return anamnesifamiliare;
	}

	public void setAnamnesifamiliare(List<Anamnesifamiliare> param) {
	    this.anamnesifamiliare = param;
	}

	@ManyToMany(mappedBy = "tabsintomi")
	public List<Anamnesifisiologica> getAnamnesifisiologica() {
	    return anamnesifisiologica;
	}

	public void setAnamnesifisiologica(List<Anamnesifisiologica> param) {
	    this.anamnesifisiologica = param;
	}

	@ManyToMany(mappedBy = "tabsintomi")
	public List<Anamnesiremota> getAnamnesiremota() {
	    return anamnesiremota;
	}

	public void setAnamnesiremota(List<Anamnesiremota> param) {
	    this.anamnesiremota = param;
	}

}