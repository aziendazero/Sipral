package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the RIUNIONIOPERATIVEOPERATORI database table.
 * 
 */
@Entity
public class Riunionioperativeoperatori implements Serializable {
	private static final long serialVersionUID = 1L;
	private RiunionioperativeoperatoriPK id;
	private Riunionioperative riunionioperative;
	private Operatori operatori;
	
	public Riunionioperativeoperatori() {
	}


	@EmbeddedId
	public RiunionioperativeoperatoriPK getId() {
		return this.id;
	}

	public void setId(RiunionioperativeoperatoriPK id) {
		this.id = id;
	}


	//bi-directional many-to-one association to Riunionioperative
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRIUNIONE")
	public Riunionioperative getRiunionioperative() {
		return this.riunionioperative;
	}

	public void setRiunionioperative(Riunionioperative riunionioperative) {
		this.riunionioperative = riunionioperative;
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

}