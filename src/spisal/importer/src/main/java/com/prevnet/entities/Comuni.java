package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the COMUNI database table.
 * 
 */
@Entity
public class Comuni implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idcomuni;
	private String capdelcomune;
	private String codicecategoria;
	private String codicecomune;
	private String codicefiscale;
	private String codiceistat;
	private String contocorrente;
	private String descrizione;
	private Date finevalidita;
	private Date iniziovalidita;
	private String istat;
	private BigDecimal km;
	private String portaleimp;
	private String prefissotel;
	private String riferimentobdn;
	private String stradario;
	private String strutturaamministrativa;
	private Tabelle zona;
	private Tabelle provincia;
	private Tabelle distretto;

	public Comuni() {
	}


	@Id
	public long getIdcomuni() {
		return this.idcomuni;
	}

	public void setIdcomuni(long idcomuni) {
		this.idcomuni = idcomuni;
	}


	public String getCapdelcomune() {
		return this.capdelcomune;
	}

	public void setCapdelcomune(String capdelcomune) {
		this.capdelcomune = capdelcomune;
	}


	public String getCodicecategoria() {
		return this.codicecategoria;
	}

	public void setCodicecategoria(String codicecategoria) {
		this.codicecategoria = codicecategoria;
	}


	public String getCodicecomune() {
		return this.codicecomune;
	}

	public void setCodicecomune(String codicecomune) {
		this.codicecomune = codicecomune;
	}


	public String getCodicefiscale() {
		return this.codicefiscale;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}


	public String getCodiceistat() {
		return this.codiceistat;
	}

	public void setCodiceistat(String codiceistat) {
		this.codiceistat = codiceistat;
	}


	public String getContocorrente() {
		return this.contocorrente;
	}

	public void setContocorrente(String contocorrente) {
		this.contocorrente = contocorrente;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	@Temporal(TemporalType.DATE)
	public Date getFinevalidita() {
		return this.finevalidita;
	}

	public void setFinevalidita(Date finevalidita) {
		this.finevalidita = finevalidita;
	}


	@Temporal(TemporalType.DATE)
	public Date getIniziovalidita() {
		return this.iniziovalidita;
	}

	public void setIniziovalidita(Date iniziovalidita) {
		this.iniziovalidita = iniziovalidita;
	}


	public String getIstat() {
		return this.istat;
	}

	public void setIstat(String istat) {
		this.istat = istat;
	}


	public BigDecimal getKm() {
		return this.km;
	}

	public void setKm(BigDecimal km) {
		this.km = km;
	}


	public String getPortaleimp() {
		return this.portaleimp;
	}

	public void setPortaleimp(String portaleimp) {
		this.portaleimp = portaleimp;
	}


	public String getPrefissotel() {
		return this.prefissotel;
	}

	public void setPrefissotel(String prefissotel) {
		this.prefissotel = prefissotel;
	}


	public String getRiferimentobdn() {
		return this.riferimentobdn;
	}

	public void setRiferimentobdn(String riferimentobdn) {
		this.riferimentobdn = riferimentobdn;
	}


	public String getStradario() {
		return this.stradario;
	}

	public void setStradario(String stradario) {
		this.stradario = stradario;
	}


	public String getStrutturaamministrativa() {
		return this.strutturaamministrativa;
	}

	public void setStrutturaamministrativa(String strutturaamministrativa) {
		this.strutturaamministrativa = strutturaamministrativa;
	}

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDZONA")
	public Tabelle getZona() {
		return this.zona;
	}

	public void setZona(Tabelle zona) {
		this.zona = zona;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVINCIA")
	public Tabelle getProvincia() {
		return this.provincia;
	}

	public void setProvincia(Tabelle provincia) {
		this.provincia = provincia;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDISTRETTO")
	public Tabelle getDistretto() {
		return this.distretto;
	}

	public void setDistretto(Tabelle distretto) {
		this.distretto = distretto;
	}

}