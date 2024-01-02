package com.prevnet.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
 * The persistent class for the LOCALITA database table.
 * 
 */
@Entity
public class Localita implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idlocalita;
	private String cap;
	private String codice;
	private String descrlocalita;
	private String riferimentobdn;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private List<Anagrafiche> anagrafiches;
	private Tabelle statoEstero;
	private Comuni comuni;

	public Localita() {
	}


	@Id
	public long getIdlocalita() {
		return this.idlocalita;
	}

	public void setIdlocalita(long idlocalita) {
		this.idlocalita = idlocalita;
	}


	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}


	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}


	public String getDescrlocalita() {
		return this.descrlocalita;
	}

	public void setDescrlocalita(String descrlocalita) {
		this.descrlocalita = descrlocalita;
	}


	public String getRiferimentobdn() {
		return this.riferimentobdn;
	}

	public void setRiferimentobdn(String riferimentobdn) {
		this.riferimentobdn = riferimentobdn;
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


	//bi-directional many-to-one association to Anagrafiche
	@OneToMany(mappedBy="localita")
	public List<Anagrafiche> getAnagrafiches() {
		return this.anagrafiches;
	}

	public void setAnagrafiches(List<Anagrafiche> anagrafiches) {
		this.anagrafiches = anagrafiches;
	}

	public Anagrafiche addAnagrafich(Anagrafiche anagrafich) {
		getAnagrafiches().add(anagrafich);
		anagrafich.setLocalita(this);

		return anagrafich;
	}

	public Anagrafiche removeAnagrafich(Anagrafiche anagrafich) {
		getAnagrafiches().remove(anagrafich);
		anagrafich.setLocalita(null);

		return anagrafich;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTATOESTERO")
	public Tabelle getStatoEstero() {
		return this.statoEstero;
	}

	public void setStatoEstero(Tabelle statoEstero) {
		this.statoEstero = statoEstero;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNE")
	public Comuni getComuni() {
		return this.comuni;
	}

	public void setComuni(Comuni comuni) {
		this.comuni = comuni;
	}

}