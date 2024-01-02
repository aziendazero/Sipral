package com.prevnet.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
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
 * The persistent class for the CRONOLOGIACARTELLESAN database table.
 * 
 */
@Entity
@NamedQuery(name="Cronologiacartellesan.findAll", query="SELECT c FROM Cronologiacartellesan c")
public class Cronologiacartellesan implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long idcronologiacartellesan;

	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"")
	private Date data;

	//bi-directional many-to-one association to Cartellesanitarie
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCARTELLASANITARIA")
	private Cartellesanitarie cartellesanitarie;

	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	private Operatori operatori;

	public Cronologiacartellesan() {
	}

	public long getIdcronologiacartellesan() {
		return this.idcronologiacartellesan;
	}

	public void setIdcronologiacartellesan(long idcronologiacartellesan) {
		this.idcronologiacartellesan = idcronologiacartellesan;
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Cartellesanitarie getCartellesanitarie() {
		return this.cartellesanitarie;
	}

	public void setCartellesanitarie(Cartellesanitarie cartellesanitarie) {
		this.cartellesanitarie = cartellesanitarie;
	}

	public Operatori getOperatori() {
		return this.operatori;
	}

	public void setOperatori(Operatori operatori) {
		this.operatori = operatori;
	}

}