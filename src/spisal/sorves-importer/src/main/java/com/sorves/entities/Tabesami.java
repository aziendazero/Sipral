package com.sorves.entities;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the tabesami database table.
 * 
 */
@Entity
@Table(name="tabesami")
public class Tabesami implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;

	private int id;
	private String unita;
	private String vndesc;
	private Double vnmax;
	private Double vnmin;

	public Tabesami() {
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

	public String getUnita() {
		return this.unita;
	}

	public void setUnita(String unita) {
		this.unita = unita;
	}

	public String getVndesc() {
		return this.vndesc;
	}

	public void setVndesc(String vndesc) {
		this.vndesc = vndesc;
	}

	public Double getVnmax() {
		return this.vnmax;
	}

	public void setVnmax(Double vnmax) {
		this.vnmax = vnmax;
	}

	public Double getVnmin() {
		return this.vnmin;
	}

	public void setVnmin(Double vnmin) {
		this.vnmin = vnmin;
	}

}