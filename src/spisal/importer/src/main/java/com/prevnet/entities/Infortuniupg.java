package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the INFORTUNIUPG database table.
 * 
 */
@Entity
public class Infortuniupg implements Serializable {
	private static final long serialVersionUID = 1L;
	private InfortuniupgPK id;
	private BigDecimal ordine;
	private InfortuniPrevnet infortuniPrevnet;
	private Operatori operatori;

	public Infortuniupg() {
	}


	@EmbeddedId
	public InfortuniupgPK getId() {
		return this.id;
	}

	public void setId(InfortuniupgPK id) {
		this.id = id;
	}


	public BigDecimal getOrdine() {
		return this.ordine;
	}

	public void setOrdine(BigDecimal ordine) {
		this.ordine = ordine;
	}


	//bi-directional many-to-one association to Infortuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINFORTUNIO")
	public InfortuniPrevnet getInfortuniPrevnet() {
		return this.infortuniPrevnet;
	}

	public void setInfortuniPrevnet(InfortuniPrevnet infortuniPrevnet) {
		this.infortuniPrevnet = infortuniPrevnet;
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