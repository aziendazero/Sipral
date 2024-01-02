package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 * The persistent class for the TABDIAGNOSIALTROESAME database table.
 * 
 */
@Entity
@Table(name="tabdiagnosialtroesame")
public class Tabdiagnosialtroesame implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;

	private long id;

	private String snomed;

	private List<Esamealtro> esamealtro;

	public Tabdiagnosialtroesame() {
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

	@ManyToMany(mappedBy = "tabdiagnosialtroesame")
	public List<Esamealtro> getEsamealtro() {
	    return esamealtro;
	}

	public void setEsamealtro(List<Esamealtro> param) {
	    this.esamealtro = param;
	}

}