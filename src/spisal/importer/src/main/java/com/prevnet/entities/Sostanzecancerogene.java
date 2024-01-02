package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the SOSTANZECANCEROGENE database table.
 * 
 */
@Entity
@NamedQuery(name="Sostanzecancerogene.findAll", query="SELECT s FROM Sostanzecancerogene s")
public class Sostanzecancerogene implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idsostanzecancerogene;
	private String chkkgnonnoti;
	private Date datacomunicazione;
	private Date datafine;
	private Date datainizio;
	private Date dataorains;
	private Date dataoramod;
	private BigDecimal tonnellate;
	private Comunicazionicancerogeni comunicazionicancerogeni1;
	private Comunicazionicancerogeni comunicazionicancerogeni2;
	private Fattoririschio fattoririschio;
	private Operatori operatori1;
	private Operatori operatori2;

	public Sostanzecancerogene() {
	}


	@Id
	public long getIdsostanzecancerogene() {
		return this.idsostanzecancerogene;
	}

	public void setIdsostanzecancerogene(long idsostanzecancerogene) {
		this.idsostanzecancerogene = idsostanzecancerogene;
	}


	public String getChkkgnonnoti() {
		return this.chkkgnonnoti;
	}

	public void setChkkgnonnoti(String chkkgnonnoti) {
		this.chkkgnonnoti = chkkgnonnoti;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicazione() {
		return this.datacomunicazione;
	}

	public void setDatacomunicazione(Date datacomunicazione) {
		this.datacomunicazione = datacomunicazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafine() {
		return this.datafine;
	}

	public void setDatafine(Date datafine) {
		this.datafine = datafine;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizio() {
		return this.datainizio;
	}

	public void setDatainizio(Date datainizio) {
		this.datainizio = datainizio;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataorains() {
		return this.dataorains;
	}

	public void setDataorains(Date dataorains) {
		this.dataorains = dataorains;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataoramod() {
		return this.dataoramod;
	}

	public void setDataoramod(Date dataoramod) {
		this.dataoramod = dataoramod;
	}


	public BigDecimal getTonnellate() {
		return this.tonnellate;
	}

	public void setTonnellate(BigDecimal tonnellate) {
		this.tonnellate = tonnellate;
	}


	//bi-directional many-to-one association to Comunicazionicancerogeni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNICAZIONECESSAZIONE")
	public Comunicazionicancerogeni getComunicazionicancerogeni1() {
		return this.comunicazionicancerogeni1;
	}

	public void setComunicazionicancerogeni1(Comunicazionicancerogeni comunicazionicancerogeni1) {
		this.comunicazionicancerogeni1 = comunicazionicancerogeni1;
	}


	//bi-directional many-to-one association to Comunicazionicancerogeni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNICAZIONECANCEROGENI")
	public Comunicazionicancerogeni getComunicazionicancerogeni2() {
		return this.comunicazionicancerogeni2;
	}

	public void setComunicazionicancerogeni2(Comunicazionicancerogeni comunicazionicancerogeni2) {
		this.comunicazionicancerogeni2 = comunicazionicancerogeni2;
	}


	//bi-directional many-to-one association to Fattoririschio
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDNCAS")
	public Fattoririschio getFattoririschio() {
		return this.fattoririschio;
	}

	public void setFattoririschio(Fattoririschio fattoririschio) {
		this.fattoririschio = fattoririschio;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREINS")
	public Operatori getOperatori1() {
		return this.operatori1;
	}

	public void setOperatori1(Operatori operatori1) {
		this.operatori1 = operatori1;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREMOD")
	public Operatori getOperatori2() {
		return this.operatori2;
	}

	public void setOperatori2(Operatori operatori2) {
		this.operatori2 = operatori2;
	}

}