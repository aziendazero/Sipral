package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the RIUNIONIOPERATIVE database table.
 * 
 */
@Entity
public class Riunionioperative implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idriunionioperative;
	private Date data;
	private String descrizione;
	private BigDecimal durata;
	private Pratiche pratica;
	private Tabelle tipologia;
	private Tabelle argomento;
	private List<Riunionioperativeoperatori> riunionioperativeoperatoris;

	public Riunionioperative() {
	}


	@Id
	public long getIdriunionioperative() {
		return this.idriunionioperative;
	}

	public void setIdriunionioperative(long idriunionioperative) {
		this.idriunionioperative = idriunionioperative;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"")
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public BigDecimal getDurata() {
		return this.durata;
	}

	public void setDurata(BigDecimal durata) {
		this.durata = durata;
	}

	//bi-directional many-to-one association to Pratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOLOGIA")
	public Tabelle getTipologia() {
		return this.tipologia;
	}

	public void setTipologia(Tabelle tipologia) {
		this.tipologia = tipologia;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDARGOMENTO")
	public Tabelle getArgomento() {
		return this.argomento;
	}

	public void setArgomento(Tabelle argomento) {
		this.argomento = argomento;
	}


	//bi-directional many-to-one association to Riunionioperativeoperatori
	@OneToMany(mappedBy="riunionioperative")
	public List<Riunionioperativeoperatori> getRiunionioperativeoperatoris() {
		return this.riunionioperativeoperatoris;
	}

	public void setRiunionioperativeoperatoris(List<Riunionioperativeoperatori> riunionioperativeoperatoris) {
		this.riunionioperativeoperatoris = riunionioperativeoperatoris;
	}

	public Riunionioperativeoperatori addRiunionioperativeoperatori(Riunionioperativeoperatori riunionioperativeoperatori) {
		getRiunionioperativeoperatoris().add(riunionioperativeoperatori);
		riunionioperativeoperatori.setRiunionioperative(this);

		return riunionioperativeoperatori;
	}

	public Riunionioperativeoperatori removeRiunionioperativeoperatori(Riunionioperativeoperatori riunionioperativeoperatori) {
		getRiunionioperativeoperatoris().remove(riunionioperativeoperatori);
		riunionioperativeoperatori.setRiunionioperative(null);

		return riunionioperativeoperatori;
	}

}