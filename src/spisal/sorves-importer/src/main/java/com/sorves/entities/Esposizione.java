package com.sorves.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ESPOSIZIONE database table.
 * 
 */
@Entity
@Table(name="esposizione")
public class Esposizione implements Serializable {
	private static final long serialVersionUID = 1L;

	private BigDecimal annofin;
	private BigDecimal annoini;
	private long id;
	private BigDecimal livello;
	private Anamnesilavorativa anamnesilavorativa;
	private Esposizionecod esposizionecod;
	private Tabmisuraunita tabmisuraunita;
	private Tabmisuravaluta tabmisuravaluta;

	public Esposizione() {
	}

	public BigDecimal getAnnofin() {
		return this.annofin;
	}

	public void setAnnofin(BigDecimal annofin) {
		this.annofin = annofin;
	}

	public BigDecimal getAnnoini() {
		return this.annoini;
	}

	public void setAnnoini(BigDecimal annoini) {
		this.annoini = annoini;
	}

	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getLivello() {
		return this.livello;
	}

	public void setLivello(BigDecimal livello) {
		this.livello = livello;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idanamnesi")
	public Anamnesilavorativa getAnamnesilavorativa() {
	    return anamnesilavorativa;
	}

	public void setAnamnesilavorativa(Anamnesilavorativa param) {
	    this.anamnesilavorativa = param;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idesposizione", referencedColumnName = "id")
	public Esposizionecod getEsposizionecod() {
	    return esposizionecod;
	}

	public void setEsposizionecod(Esposizionecod param) {
	    this.esposizionecod = param;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idmisuraunita", referencedColumnName = "id")
	public Tabmisuraunita getTabmisuraunita() {
	    return tabmisuraunita;
	}

	public void setTabmisuraunita(Tabmisuraunita param) {
	    this.tabmisuraunita = param;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idmisuravaluta", referencedColumnName = "id")
	public Tabmisuravaluta getTabmisuravaluta() {
	    return tabmisuravaluta;
	}

	public void setTabmisuravaluta(Tabmisuravaluta param) {
	    this.tabmisuravaluta = param;
	}

}