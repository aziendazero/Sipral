package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the FARMACI database table.
 * 
 */
@Entity
@NamedQuery(name="Farmaci.findAll", query="SELECT f FROM Farmaci f")
public class Farmaci implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long idfarmaci;

	private String classe;

	private String dose;

	private String periodo;

	private String tipo;

	//bi-directional many-to-one association to Cartellesanitarie
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCARTELLASANITARIA")
	private Cartellesanitarie cartellesanitarie;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPO")
	private Tabelle tabelle;

	public Farmaci() {
	}

	public long getIdfarmaci() {
		return this.idfarmaci;
	}

	public void setIdfarmaci(long idfarmaci) {
		this.idfarmaci = idfarmaci;
	}

	public String getClasse() {
		return this.classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public String getDose() {
		return this.dose;
	}

	public void setDose(String dose) {
		this.dose = dose;
	}

	public String getPeriodo() {
		return this.periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Cartellesanitarie getCartellesanitarie() {
		return this.cartellesanitarie;
	}

	public void setCartellesanitarie(Cartellesanitarie cartellesanitarie) {
		this.cartellesanitarie = cartellesanitarie;
	}

	public Tabelle getTabelle() {
		return this.tabelle;
	}

	public void setTabelle(Tabelle tabelle) {
		this.tabelle = tabelle;
	}

}