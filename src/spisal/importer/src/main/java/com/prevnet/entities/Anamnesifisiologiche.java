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
 * The persistent class for the ANAMNESIFISIOLOGICHE database table.
 * 
 */
@Entity
@NamedQuery(name="Anamnesifisiologiche.findAll", query="SELECT a FROM Anamnesifisiologiche a")
public class Anamnesifisiologiche implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long idanamnesifisiologiche;

	@Temporal(TemporalType.DATE)
	private Date datacompilazione;

	private String notizia;

	//bi-directional many-to-one association to Cartellesanitarie
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCARTELLASANITARIA")
	private Cartellesanitarie cartellesanitarie;

	public Anamnesifisiologiche() {
	}

	public long getIdanamnesifisiologiche() {
		return this.idanamnesifisiologiche;
	}

	public void setIdanamnesifisiologiche(long idanamnesifisiologiche) {
		this.idanamnesifisiologiche = idanamnesifisiologiche;
	}

	public Date getDatacompilazione() {
		return this.datacompilazione;
	}

	public void setDatacompilazione(Date datacompilazione) {
		this.datacompilazione = datacompilazione;
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