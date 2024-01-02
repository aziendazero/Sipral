package com.sorves.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tabdiagnosispirometrica database table.
 * 
 */
@Entity
@Table(name="tabdiagnosispirometrica")
public class Tabdiagnosispirometrica implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private int id;
	private String snomed;

	public Tabdiagnosispirometrica() {
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Id
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSnomed() {
		return this.snomed;
	}

	public void setSnomed(String snomed) {
		this.snomed = snomed;
	}

}