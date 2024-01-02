package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the UBICAZIONIAMIANTO database table.
 * 
 */
@Entity
@NamedQuery(name="Ubicazioniamianto.findAll", query="SELECT u FROM Ubicazioniamianto u")
public class Ubicazioniamianto implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idubicazioniamianto;
	private Date dataattivita;
	private String descrizione;
	private BigDecimal kg;
	private BigDecimal mq;
	private BigDecimal numlavoratori;
	private BigDecimal nummattonelle;
	private BigDecimal quantita;
	private String ubicazione;
	private BigDecimal unitam;
	private Pianidilavoroamianto pianidilavoroamianto;
	private Comuni comune;
	private Tabelle tipoIntervento;

	public Ubicazioniamianto() {
	}


	@Id
	public long getIdubicazioniamianto() {
		return this.idubicazioniamianto;
	}

	public void setIdubicazioniamianto(long idubicazioniamianto) {
		this.idubicazioniamianto = idubicazioniamianto;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataattivita() {
		return this.dataattivita;
	}

	public void setDataattivita(Date dataattivita) {
		this.dataattivita = dataattivita;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public BigDecimal getKg() {
		return this.kg;
	}

	public void setKg(BigDecimal kg) {
		this.kg = kg;
	}


	public BigDecimal getMq() {
		return this.mq;
	}

	public void setMq(BigDecimal mq) {
		this.mq = mq;
	}


	public BigDecimal getNumlavoratori() {
		return this.numlavoratori;
	}

	public void setNumlavoratori(BigDecimal numlavoratori) {
		this.numlavoratori = numlavoratori;
	}


	public BigDecimal getNummattonelle() {
		return this.nummattonelle;
	}

	public void setNummattonelle(BigDecimal nummattonelle) {
		this.nummattonelle = nummattonelle;
	}


	public BigDecimal getQuantita() {
		return this.quantita;
	}

	public void setQuantita(BigDecimal quantita) {
		this.quantita = quantita;
	}


	public String getUbicazione() {
		return this.ubicazione;
	}

	public void setUbicazione(String ubicazione) {
		this.ubicazione = ubicazione;
	}


	public BigDecimal getUnitam() {
		return this.unitam;
	}

	public void setUnitam(BigDecimal unitam) {
		this.unitam = unitam;
	}


	//bi-directional many-to-one association to Pianidilavoroamianto
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPIANO")
	public Pianidilavoroamianto getPianidilavoroamianto() {
		return this.pianidilavoroamianto;
	}

	public void setPianidilavoroamianto(Pianidilavoroamianto pianidilavoroamianto) {
		this.pianidilavoroamianto = pianidilavoroamianto;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEUBIC")
	public Comuni getComune() {
		return this.comune;
	}

	public void setComune(Comuni comune) {
		this.comune = comune;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOINTERVENTO")
	public Tabelle getTipoIntervento() {
		return this.tipoIntervento;
	}

	public void setTipoIntervento(Tabelle tipoIntervento) {
		this.tipoIntervento = tipoIntervento;
	}

}