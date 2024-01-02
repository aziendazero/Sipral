package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the INFORTUNITESTIMONI database table.
 * 
 */
@Entity
public class Infortunitestimoni implements Serializable {
	private static final long serialVersionUID = 1L;
	private InfortunitestimoniPK id;
	private BigDecimal ordine;
	private String testimonianza;
	private InfortuniPrevnet infortuniPrevnet;
	private Utenti utenti;

	public Infortunitestimoni() {
	}


	@EmbeddedId
	public InfortunitestimoniPK getId() {
		return this.id;
	}

	public void setId(InfortunitestimoniPK id) {
		this.id = id;
	}


	public BigDecimal getOrdine() {
		return this.ordine;
	}

	public void setOrdine(BigDecimal ordine) {
		this.ordine = ordine;
	}


	@Lob
	public String getTestimonianza() {
		return this.testimonianza;
	}

	public void setTestimonianza(String testimonianza) {
		this.testimonianza = testimonianza;
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


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Utenti getUtenti() {
		return this.utenti;
	}

	public void setUtenti(Utenti utenti) {
		this.utenti = utenti;
	}

}