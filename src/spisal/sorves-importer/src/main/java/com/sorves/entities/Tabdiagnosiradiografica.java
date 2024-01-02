package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABDIAGNOSIRADIOGRAFICA database table.
 * 
 */
@Entity
@Table(name="tabdiagnosiradiografica")
public class Tabdiagnosiradiografica implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;

	private long id;

	private String snomed;

	private List<Radiografia> radiografia;

	public Tabdiagnosiradiografica() {
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

	@ManyToMany(mappedBy = "tabdiagnosiradiografica")
	public List<Radiografia> getRadiografia() {
	    return radiografia;
	}

	public void setRadiografia(List<Radiografia> param) {
	    this.radiografia = param;
	}

}