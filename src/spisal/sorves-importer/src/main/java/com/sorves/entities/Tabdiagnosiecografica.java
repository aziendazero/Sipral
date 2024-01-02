package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABDIAGNOSIECOGRAFICA database table.
 * 
 */
@Entity
@Table(name="tabdiagnosiecografica")
public class Tabdiagnosiecografica implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;

	private long id;

	private String snomed;

	private List<Ecografia> ecografia;

	public Tabdiagnosiecografica() {
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

	@ManyToMany(mappedBy = "tabdiagnosiecografica")
	public List<Ecografia> getEcografia() {
	    return ecografia;
	}

	public void setEcografia(List<Ecografia> param) {
	    this.ecografia = param;
	}

}