package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the MALATTIEPROUPG database table.
 * 
 */
@Entity
@NamedQuery(name="Malattieproupg.findAll", query="SELECT m FROM Malattieproupg m")
public class Malattieproupg implements Serializable {
	private static final long serialVersionUID = 1L;
	private MalattieproupgPK id;
	private BigDecimal ordine;
	private Inchiestemalatpro inchiestemalatpro;
	private Malattieprofessionali malattia;
	private Operatori operatore;
	
	public Malattieproupg() {
	}


	@EmbeddedId
	public MalattieproupgPK getId() {
		return this.id;
	}

	public void setId(MalattieproupgPK id) {
		this.id = id;
	}
	
	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMALATTIA",insertable=false, updatable=false)
	public Malattieprofessionali getMalattia() {
		return this.malattia;
	}

	public void setMalattia(Malattieprofessionali malattia) {
		this.malattia = malattia;
	}
	
	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE",insertable=false, updatable=false)
	public Operatori getOperatore() {
		return this.operatore;
	}

	public void setOperatore(Operatori operatori1) {
		this.operatore = operatori1;
	}


	public BigDecimal getOrdine() {
		return this.ordine;
	}

	public void setOrdine(BigDecimal ordine) {
		this.ordine = ordine;
	}

}