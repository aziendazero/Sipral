package com.prevnet.entities;

import java.io.Serializable;
import java.util.Date;

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
 * The persistent class for the COMMITTENTICANTIERE database table.
 * 
 */
@Entity
public class Committenticantiere implements Serializable {
	private static final long serialVersionUID = 1L;
	private CommittenticantierePK id;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private Anagrafiche anagrafiche;
	private Cantieri cantieri;

	public Committenticantiere() {
	}


	@EmbeddedId
	public CommittenticantierePK getId() {
		return this.id;
	}

	public void setId(CommittenticantierePK id) {
		this.id = id;
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


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMMITTENTI")
	public Anagrafiche getAnagrafiche() {
		return this.anagrafiche;
	}

	public void setAnagrafiche(Anagrafiche anagrafiche) {
		this.anagrafiche = anagrafiche;
	}


	//bi-directional many-to-one association to Cantieri
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCANTIERE")
	public Cantieri getCantieri() {
		return this.cantieri;
	}

	public void setCantieri(Cantieri cantieri) {
		this.cantieri = cantieri;
	}

}