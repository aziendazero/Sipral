package com.sorves.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ANAGRAFICA database table.
 * 
 */
@Entity
@Table(name="anagrafica")
public class Anagrafica implements Serializable {
	private static final long serialVersionUID = 1L;
	private BigDecimal accettazionesorveglianza;	//0-1
	private String cap;
	private String civico;
	private String codicefiscale;
	private String cognome;
	private Comuni comunenascita;
	private Comuni comuneresidenza;
	private Date datadecesso;
	private Date datanascita;
	private String email;
	private String fax;
	private String frazione;
	private long id;
	private String indirizzo;
	private BigDecimal informato;					//0-1
	private BigDecimal inviolettera;				//0-1
	private String nome;
	private String rnlib;
	private String sesso;
	private Tabstatolavorativo statolavorativo;
	private BigDecimal statovita;					//0-1
	private String telefono;
	private String tesserasanitaria;
	private String tipovia;
	private List<Anamnesifamiliare> anamnesifamiliare;
	private List<Anamnesifisiologica> anamnesifisiologica;
	private List<Anamnesiremota> anamnesiremota;
	private List<Anamnesilavorativa> anamnesilavorativa;
	private List<Visita> visita;
	private List<Ecografia> ecografia;
	private List<Esamealtro> esamealtro;
	private List<Esameistologico> esameistologico;
	private List<Esamilaboratorio> esamilaboratorio;
	private List<Radiografia> radiografia;
	private List<Spirometria> spirometria;
	public Anagrafica() {
	}


	public BigDecimal getAccettazionesorveglianza() {
		return this.accettazionesorveglianza;
	}

	public void setAccettazionesorveglianza(BigDecimal accettazionesorveglianza) {
		this.accettazionesorveglianza = accettazionesorveglianza;
	}


	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}


	public String getCivico() {
		return this.civico;
	}

	public void setCivico(String civico) {
		this.civico = civico;
	}


	public String getCodicefiscale() {
		return this.codicefiscale;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}


	public String getCognome() {
		return this.cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="comunenascita")
	public Comuni getComunenascita() {
		return this.comunenascita;
	}

	public void setComunenascita(Comuni comunenascita) {
		this.comunenascita = comunenascita;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="comuneresidenza")
	public Comuni getComuneresidenza() {
		return this.comuneresidenza;
	}

	public void setComuneresidenza(Comuni comuneresidenza) {
		this.comuneresidenza = comuneresidenza;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatadecesso() {
		return this.datadecesso;
	}

	public void setDatadecesso(Date datadecesso) {
		this.datadecesso = datadecesso;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanascita() {
		return this.datanascita;
	}

	public void setDatanascita(Date datanascita) {
		this.datanascita = datanascita;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getFax() {
		return this.fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}


	public String getFrazione() {
		return this.frazione;
	}

	public void setFrazione(String frazione) {
		this.frazione = frazione;
	}

	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}


	public BigDecimal getInformato() {
		return this.informato;
	}

	public void setInformato(BigDecimal informato) {
		this.informato = informato;
	}


	public BigDecimal getInviolettera() {
		return this.inviolettera;
	}

	public void setInviolettera(BigDecimal inviolettera) {
		this.inviolettera = inviolettera;
	}


	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getRnlib() {
		return this.rnlib;
	}

	public void setRnlib(String rnlib) {
		this.rnlib = rnlib;
	}


	public String getSesso() {
		return this.sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="statolavorativo")
	public Tabstatolavorativo getStatolavorativo() {
		return this.statolavorativo;
	}

	public void setStatolavorativo(Tabstatolavorativo statolavorativo) {
		this.statolavorativo = statolavorativo;
	}


	public BigDecimal getStatovita() {
		return this.statovita;
	}

	public void setStatovita(BigDecimal statovita) {
		this.statovita = statovita;
	}


	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	public String getTesserasanitaria() {
		return this.tesserasanitaria;
	}

	public void setTesserasanitaria(String tesserasanitaria) {
		this.tesserasanitaria = tesserasanitaria;
	}


	public String getTipovia() {
		return this.tipovia;
	}

	public void setTipovia(String tipovia) {
		this.tipovia = tipovia;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Anamnesifamiliare> getAnamnesifamiliare() {
	    return anamnesifamiliare;
	}


	public void setAnamnesifamiliare(List<Anamnesifamiliare> param) {
	    this.anamnesifamiliare = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Anamnesifisiologica> getAnamnesifisiologica() {
	    return anamnesifisiologica;
	}


	public void setAnamnesifisiologica(List<Anamnesifisiologica> param) {
	    this.anamnesifisiologica = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Anamnesiremota> getAnamnesiremota() {
	    return anamnesiremota;
	}


	public void setAnamnesiremota(List<Anamnesiremota> param) {
	    this.anamnesiremota = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Anamnesilavorativa> getAnamnesilavorativa() {
	    return anamnesilavorativa;
	}


	public void setAnamnesilavorativa(List<Anamnesilavorativa> param) {
	    this.anamnesilavorativa = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("dataprel ASC")
	public List<Visita> getVisita() {
	    return visita;
	}


	public void setVisita(List<Visita> param) {
	    this.visita = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Ecografia> getEcografia() {
	    return ecografia;
	}


	public void setEcografia(List<Ecografia> param) {
	    this.ecografia = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Esamealtro> getEsamealtro() {
	    return esamealtro;
	}


	public void setEsamealtro(List<Esamealtro> param) {
	    this.esamealtro = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Esameistologico> getEsameistologico() {
	    return esameistologico;
	}


	public void setEsameistologico(List<Esameistologico> param) {
	    this.esameistologico = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Esamilaboratorio> getEsamilaboratorio() {
	    return esamilaboratorio;
	}


	public void setEsamilaboratorio(List<Esamilaboratorio> param) {
	    this.esamilaboratorio = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Radiografia> getRadiografia() {
	    return radiografia;
	}


	public void setRadiografia(List<Radiografia> param) {
	    this.radiografia = param;
	}


	@OneToMany(mappedBy = "anagrafica")
	@OrderBy("id ASC")
	public List<Spirometria> getSpirometria() {
	    return spirometria;
	}


	public void setSpirometria(List<Spirometria> param) {
	    this.spirometria = param;
	}
}