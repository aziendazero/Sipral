package com.prevnet.entities;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the PARERIGENERICIALTRIOP database table.
 * 
 */
@Entity
@NamedQuery(name="Parerigenericialtriop.findAll", query="SELECT p FROM Parerigenericialtriop p")
public class Parerigenericialtriop implements Serializable {
	private static final long serialVersionUID = 1L;
	private ParerigenericialtriopPK id;
	private Parerigenerici parerigenerici;
	private Operatori operatore;

	public Parerigenericialtriop() {
	}


	@EmbeddedId
	public ParerigenericialtriopPK getId() {
		return this.id;
	}

	public void setId(ParerigenericialtriopPK id) {
		this.id = id;
	}
	
	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE",insertable=false, updatable=false)
	public Operatori getOperatore() {
		return this.operatore;
	}

	public void setOperatore(Operatori operatore) {
		this.operatore = operatore;
	}


	//bi-directional many-to-one association to Parerigenerici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPAREREGENERICO",insertable=false, updatable=false)
	public Parerigenerici getParerigenerici() {
		return this.parerigenerici;
	}

	public void setParerigenerici(Parerigenerici parerigenerici) {
		this.parerigenerici = parerigenerici;
	}

}