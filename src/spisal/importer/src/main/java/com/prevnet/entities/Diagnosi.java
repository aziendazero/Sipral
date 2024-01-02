package com.prevnet.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the DIAGNOSI database table.
 * 
 */
@Entity
@NamedQuery(name="Diagnosi.findAll", query="SELECT d FROM Diagnosi d")
public class Diagnosi implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long iddiagnosi;

	@Temporal(TemporalType.DATE)
	private Date datafollowup;

	private String note;

	//bi-directional many-to-one association to Cartellesanitarie
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCARTELLASANITARIA")
	private Cartellesanitarie cartellesanitarie;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITOFINALE")
	private Tabelle tabelle1;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRESACARICO")
	private Tabelle tabelle2;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCEDURAINTERMEDIA")
	private Tabelle tabelle3;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDEZIOLOGIA")
	private Tabelle tabelle4;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCATEGORIADIAGNOSTICA")
	private Tabelle tabelle5;

	public Diagnosi() {
	}

	public long getIddiagnosi() {
		return this.iddiagnosi;
	}

	public void setIddiagnosi(long iddiagnosi) {
		this.iddiagnosi = iddiagnosi;
	}

	public Date getDatafollowup() {
		return this.datafollowup;
	}

	public void setDatafollowup(Date datafollowup) {
		this.datafollowup = datafollowup;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Cartellesanitarie getCartellesanitarie() {
		return this.cartellesanitarie;
	}

	public void setCartellesanitarie(Cartellesanitarie cartellesanitarie) {
		this.cartellesanitarie = cartellesanitarie;
	}

	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}

	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}

	public Tabelle getTabelle3() {
		return this.tabelle3;
	}

	public void setTabelle3(Tabelle tabelle3) {
		this.tabelle3 = tabelle3;
	}

	public Tabelle getTabelle4() {
		return this.tabelle4;
	}

	public void setTabelle4(Tabelle tabelle4) {
		this.tabelle4 = tabelle4;
	}

	public Tabelle getTabelle5() {
		return this.tabelle5;
	}

	public void setTabelle5(Tabelle tabelle5) {
		this.tabelle5 = tabelle5;
	}

}