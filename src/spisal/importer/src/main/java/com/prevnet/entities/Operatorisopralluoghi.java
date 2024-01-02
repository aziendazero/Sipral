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
 * The persistent class for the OPERATORISOPRALLUOGHI database table.
 * 
 */
@Entity
public class Operatorisopralluoghi implements Serializable {
	private static final long serialVersionUID = 1L;
	private OperatorisopralluoghiPK id;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private Operatori operatori;
	private Sopralluoghidip sopralluoghidip;

	public Operatorisopralluoghi() {
	}
	
	@EmbeddedId
	public OperatorisopralluoghiPK getId() {
		return this.id;
	}

	public void setId(OperatorisopralluoghiPK id) {
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

}