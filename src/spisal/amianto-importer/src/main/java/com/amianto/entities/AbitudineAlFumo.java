package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the abitudine_al_fumo database table.
 * 
 */
@Entity
@Table(name="abitudine_al_fumo")
public class AbitudineAlFumo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer idExEsposto;
	private String cap;
	private String citta;
	private String codiceFiscale;
	private Date consegnaEsenzione;
	private Date dataFineFumo;
	private Date dataMorte;
	private String esenzioneNonRitirata;
	private String esposizioneACvm;
	private Integer etaFineFumo;
	private Integer etaInizioFumo;
	private Integer fumoAttuale;
	private Integer gestioneRadiografie;
	private Integer id;
	private String indirizzo;
	private String miglioramenti;
	private Integer nSigaretteGiorno;
	private Double numeroLotus;
	private String telefono;
	private String tesseraSanitaria;
	private String tipoFumo;
	public AbitudineAlFumo() {
	}


	@Id
	@Column(name="id_ex_esposto", unique=true, nullable=false)
	public Integer getIdExEsposto() {
		return this.idExEsposto;
	}

	public void setIdExEsposto(Integer idExEsposto) {
		this.idExEsposto = idExEsposto;
	}


	@Column(length=255)
	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}


	@Column(length=255)
	public String getCitta() {
		return this.citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}


	@Column(name="codice_fiscale", length=255)
	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="consegna_esenzione")
	public Date getConsegnaEsenzione() {
		return this.consegnaEsenzione;
	}

	public void setConsegnaEsenzione(Date consegnaEsenzione) {
		this.consegnaEsenzione = consegnaEsenzione;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_fine_fumo")
	public Date getDataFineFumo() {
		return this.dataFineFumo;
	}

	public void setDataFineFumo(Date dataFineFumo) {
		this.dataFineFumo = dataFineFumo;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_morte")
	public Date getDataMorte() {
		return this.dataMorte;
	}

	public void setDataMorte(Date dataMorte) {
		this.dataMorte = dataMorte;
	}


	@Column(name="esenzione_non_ritirata", length=255)
	public String getEsenzioneNonRitirata() {
		return this.esenzioneNonRitirata;
	}

	public void setEsenzioneNonRitirata(String esenzioneNonRitirata) {
		this.esenzioneNonRitirata = esenzioneNonRitirata;
	}


	@Column(name="esposizione_a_cvm", length=255)
	public String getEsposizioneACvm() {
		return this.esposizioneACvm;
	}

	public void setEsposizioneACvm(String esposizioneACvm) {
		this.esposizioneACvm = esposizioneACvm;
	}


	@Column(name="eta_fine_fumo")
	public Integer getEtaFineFumo() {
		return this.etaFineFumo;
	}

	public void setEtaFineFumo(Integer etaFineFumo) {
		this.etaFineFumo = etaFineFumo;
	}


	@Column(name="eta_inizio_fumo")
	public Integer getEtaInizioFumo() {
		return this.etaInizioFumo;
	}

	public void setEtaInizioFumo(Integer etaInizioFumo) {
		this.etaInizioFumo = etaInizioFumo;
	}


	@Column(name="fumo_attuale")
	public Integer getFumoAttuale() {
		return this.fumoAttuale;
	}

	public void setFumoAttuale(Integer fumoAttuale) {
		this.fumoAttuale = fumoAttuale;
	}


	@Column(name="gestione_radiografie")
	public Integer getGestioneRadiografie() {
		return this.gestioneRadiografie;
	}

	public void setGestioneRadiografie(Integer gestioneRadiografie) {
		this.gestioneRadiografie = gestioneRadiografie;
	}


	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(length=255)
	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}


	@Column(length=255)
	public String getMiglioramenti() {
		return this.miglioramenti;
	}

	public void setMiglioramenti(String miglioramenti) {
		this.miglioramenti = miglioramenti;
	}


	@Column(name="n_sigarette_giorno")
	public Integer getNSigaretteGiorno() {
		return this.nSigaretteGiorno;
	}

	public void setNSigaretteGiorno(Integer nSigaretteGiorno) {
		this.nSigaretteGiorno = nSigaretteGiorno;
	}


	@Column(name="numero_lotus")
	public Double getNumeroLotus() {
		return this.numeroLotus;
	}

	public void setNumeroLotus(Double numeroLotus) {
		this.numeroLotus = numeroLotus;
	}


	@Column(length=255)
	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	@Column(name="tessera_sanitaria", length=255)
	public String getTesseraSanitaria() {
		return this.tesseraSanitaria;
	}

	public void setTesseraSanitaria(String tesseraSanitaria) {
		this.tesseraSanitaria = tesseraSanitaria;
	}


	@Column(name="tipo_fumo", length=255)
	public String getTipoFumo() {
		return this.tipoFumo;
	}

	public void setTipoFumo(String tipoFumo) {
		this.tipoFumo = tipoFumo;
	}

}