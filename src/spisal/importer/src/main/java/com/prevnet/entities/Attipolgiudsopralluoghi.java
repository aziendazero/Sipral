package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the ATTIPOLGIUDSOPRALLUOGHI database table.
 * 
 */
@Entity
public class Attipolgiudsopralluoghi implements Serializable {
	private static final long serialVersionUID = 1L;
	private AttipolgiudsopralluoghiPK id;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private BigDecimal totaleattigiud;
	private Sopralluoghidip sopralluoghidip;
	private Tabelle attoPolGiud;

	public Attipolgiudsopralluoghi() {
	}
	
	@EmbeddedId
	public AttipolgiudsopralluoghiPK getId() {
		return this.id;
	}

	public void setId(AttipolgiudsopralluoghiPK id) {
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


	public BigDecimal getTotaleattigiud() {
		return this.totaleattigiud;
	}

	public void setTotaleattigiud(BigDecimal totaleattigiud) {
		this.totaleattigiud = totaleattigiud;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGO")
	public Sopralluoghidip getSopralluoghidip() {
		return this.sopralluoghidip;
	}

	public void setSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		this.sopralluoghidip = sopralluoghidip;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTOPOLGIUD")
	public Tabelle getAttoPolGiud() {
		return this.attoPolGiud;
	}

	public void setAttoPolGiud(Tabelle attoPolGiud) {
		this.attoPolGiud = attoPolGiud;
	}

}