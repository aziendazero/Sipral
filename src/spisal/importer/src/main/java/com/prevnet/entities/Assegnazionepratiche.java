package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ASSEGNAZIONEPRATICHE database table.
 * 
 */
@Entity
public class Assegnazionepratiche implements Serializable {
	private static final long serialVersionUID = 1L;
	private AssegnazionepratichePK id;
	private Date dataass;
	private Date datafineass;
	private BigDecimal idscadenza;
	private BigDecimal idufficio;
	private BigDecimal ordine;
	private String rdp;
	private String solalettura;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private String tuttiopufficio;
	private Operatori operatori;
	private Pratiche pratiche;
	private Tabelle livello;

	public Assegnazionepratiche() {
	}
	
	@EmbeddedId
	public AssegnazionepratichePK getId() {
		return this.id;
	}

	public void setId(AssegnazionepratichePK id) {
		this.id = id;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataass() {
		return this.dataass;
	}

	public void setDataass(Date dataass) {
		this.dataass = dataass;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafineass() {
		return this.datafineass;
	}

	public void setDatafineass(Date datafineass) {
		this.datafineass = datafineass;
	}


	public BigDecimal getIdscadenza() {
		return this.idscadenza;
	}

	public void setIdscadenza(BigDecimal idscadenza) {
		this.idscadenza = idscadenza;
	}


	public BigDecimal getIdufficio() {
		return this.idufficio;
	}

	public void setIdufficio(BigDecimal idufficio) {
		this.idufficio = idufficio;
	}


	public BigDecimal getOrdine() {
		return this.ordine;
	}

	public void setOrdine(BigDecimal ordine) {
		this.ordine = ordine;
	}

	@Column(insertable=false, updatable=false)
	public String getRdp() {
		return this.rdp;
	}

	public void setRdp(String rdp) {
		this.rdp = rdp;
	}


	public String getSolalettura() {
		return this.solalettura;
	}

	public void setSolalettura(String solalettura) {
		this.solalettura = solalettura;
	}


	@Temporal(TemporalType.DATE)
	public Date getSynchdate() {
		return this.synchdate;
	}

	public void setSynchdate(Date synchdate) {
		this.synchdate = synchdate;
	}


	public String getSynchflag() {
		return this.synchflag;
	}

	public void setSynchflag(String synchflag) {
		this.synchflag = synchflag;
	}


	public String getSynchid() {
		return this.synchid;
	}

	public void setSynchid(String synchid) {
		this.synchid = synchid;
	}


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	public String getTuttiopufficio() {
		return this.tuttiopufficio;
	}

	public void setTuttiopufficio(String tuttiopufficio) {
		this.tuttiopufficio = tuttiopufficio;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatori() {
		return this.operatori;
	}

	public void setOperatori(Operatori operatori) {
		this.operatori = operatori;
	}


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratiche() {
		return this.pratiche;
	}

	public void setPratiche(Pratiche procpratiche) {
		this.pratiche = procpratiche;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLIVELLO")
	public Tabelle getLivello() {
		return this.livello;
	}

	public void setLivello(Tabelle livello) {
		this.livello = livello;
	}

}