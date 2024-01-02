package com.prevnet.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the OPERATORIPROVVEDIMENTI database table.
 * 
 */
@Entity
@NamedQuery(name="Operatoriprovvedimenti.findAll", query="SELECT o FROM Operatoriprovvedimenti o")
public class Operatoriprovvedimenti implements Serializable {
	private static final long serialVersionUID = 1L;
	private OperatoriprovvedimentiPK id;
	private Operatori operatore;
	private ProvvedimentiPrevnet provvedimenti;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;


	public Operatoriprovvedimenti() {
	}
	
	@EmbeddedId
	public OperatoriprovvedimentiPK getId() {
		return this.id;
	}

	public void setId(OperatoriprovvedimentiPK id) {
		this.id = id;
	}

	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatore() {
		return this.operatore;
	}

	public void setOperatore(Operatori idoperatore) {
		this.operatore = idoperatore;
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


	//bi-directional many-to-one association to Provvedimenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVVEDIMENTO")
	public ProvvedimentiPrevnet getProvvedimenti() {
		return this.provvedimenti;
	}

	public void setProvvedimenti(ProvvedimentiPrevnet provvedimenti) {
		this.provvedimenti = provvedimenti;
	}

}