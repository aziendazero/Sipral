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
 * The persistent class for the ANAMNESIPATOLOGICHE database table.
 * 
 */
@Entity
@NamedQuery(name="Anamnesipatologiche.findAll", query="SELECT a FROM Anamnesipatologiche a")
public class Anamnesipatologiche implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long idanamnesipatologiche;

	@Temporal(TemporalType.DATE)
	private Date datacompilazione;

	@Temporal(TemporalType.DATE)
	private Date dataevento;

	private String notizia;

	//bi-directional many-to-one association to Cartellesanitarie
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCARTELLASANITARIA")
	private Cartellesanitarie cartellesanitarie;

	public Anamnesipatologiche() {
	}

	public long getIdanamnesipatologiche() {
		return this.idanamnesipatologiche;
	}

	public void setIdanamnesipatologiche(long idanamnesipatologiche) {
		this.idanamnesipatologiche = idanamnesipatologiche;
	}

	public Date getDatacompilazione() {
		return this.datacompilazione;
	}

	public void setDatacompilazione(Date datacompilazione) {
		this.datacompilazione = datacompilazione;
	}

	public Date getDataevento() {
		return this.dataevento;
	}

	public void setDataevento(Date dataevento) {
		this.dataevento = dataevento;
	}

	public String getNotizia() {
		return this.notizia;
	}

	public void setNotizia(String notizia) {
		this.notizia = notizia;
	}

	public Cartellesanitarie getCartellesanitarie() {
		return this.cartellesanitarie;
	}

	public void setCartellesanitarie(Cartellesanitarie cartellesanitarie) {
		this.cartellesanitarie = cartellesanitarie;
	}

}