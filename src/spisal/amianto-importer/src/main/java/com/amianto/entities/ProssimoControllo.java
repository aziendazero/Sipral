package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the prossimo_controllo database table.
 * 
 */
@Entity
@Table(name="prossimo_controllo")
@NamedQuery(name="ProssimoControllo.findAll", query="SELECT p FROM ProssimoControllo p")
public class ProssimoControllo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String agente_lavorazione_esposizione;
	private Integer asbestosi;
	private byte autonomo;
	private Integer bilaterali;
	private String causaInizialeDiMorte;
	private String causaIntermediaDiMorte;
	private String causaTerminaleDiMorte;
	private String codice;
	private String codiceFiscale;
	private String codiceMalattia;
	private String commento;
	private String comune;
	private String data;
	private Date dataProssimoControllo;
	private String datoreDiLavoro;
	private byte dipendente;
	private Integer id;
	private String lista;
	private String malattia;
	private String mansione;
	private Integer mesotelioma;
	private Integer n1CodiceIcd;
	private String n1StatiMorbosiRilevanti;
	private Integer n2CodiceIcd;
	private String n2StatiMorbosiRilevanti;
	private Integer n3CodiceIcd;
	private Integer n4CodiceIcd;
	private Integer n5CodiceIcd;
	private Integer nNoduli;
	private Integer nodulo;
	private Double numeroLotus;
	private Integer placche;
	private String provincia;
	private String settoreLavorativo;
	private Integer tumorePolmonare;

	public ProssimoControllo() {
	}


	@Column(name="`agente-lavorazione-esposizione`", length=255)
	public String getAgente_lavorazione_esposizione() {
		return this.agente_lavorazione_esposizione;
	}

	public void setAgente_lavorazione_esposizione(String agente_lavorazione_esposizione) {
		this.agente_lavorazione_esposizione = agente_lavorazione_esposizione;
	}


	public Integer getAsbestosi() {
		return this.asbestosi;
	}

	public void setAsbestosi(Integer asbestosi) {
		this.asbestosi = asbestosi;
	}


	public byte getAutonomo() {
		return this.autonomo;
	}

	public void setAutonomo(byte autonomo) {
		this.autonomo = autonomo;
	}


	public Integer getBilaterali() {
		return this.bilaterali;
	}

	public void setBilaterali(Integer bilaterali) {
		this.bilaterali = bilaterali;
	}


	@Column(name="causa_iniziale_di_morte", length=255)
	public String getCausaInizialeDiMorte() {
		return this.causaInizialeDiMorte;
	}

	public void setCausaInizialeDiMorte(String causaInizialeDiMorte) {
		this.causaInizialeDiMorte = causaInizialeDiMorte;
	}


	@Column(name="causa_intermedia_di_morte", length=255)
	public String getCausaIntermediaDiMorte() {
		return this.causaIntermediaDiMorte;
	}

	public void setCausaIntermediaDiMorte(String causaIntermediaDiMorte) {
		this.causaIntermediaDiMorte = causaIntermediaDiMorte;
	}


	@Column(name="causa_terminale_di_morte", length=255)
	public String getCausaTerminaleDiMorte() {
		return this.causaTerminaleDiMorte;
	}

	public void setCausaTerminaleDiMorte(String causaTerminaleDiMorte) {
		this.causaTerminaleDiMorte = causaTerminaleDiMorte;
	}


	@Lob
	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}


	@Column(name="codice_fiscale", length=50)
	public String getCodiceFiscale() {
		return this.codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}


	@Column(name="codice_malattia", length=255)
	public String getCodiceMalattia() {
		return this.codiceMalattia;
	}

	public void setCodiceMalattia(String codiceMalattia) {
		this.codiceMalattia = codiceMalattia;
	}


	@Lob
	public String getCommento() {
		return this.commento;
	}

	public void setCommento(String commento) {
		this.commento = commento;
	}


	@Column(length=255)
	public String getComune() {
		return this.comune;
	}

	public void setComune(String comune) {
		this.comune = comune;
	}


	@Column(length=50)
	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_prossimo_controllo")
	public Date getDataProssimoControllo() {
		return this.dataProssimoControllo;
	}

	public void setDataProssimoControllo(Date dataProssimoControllo) {
		this.dataProssimoControllo = dataProssimoControllo;
	}


	@Column(name="datore_di_lavoro", length=255)
	public String getDatoreDiLavoro() {
		return this.datoreDiLavoro;
	}

	public void setDatoreDiLavoro(String datoreDiLavoro) {
		this.datoreDiLavoro = datoreDiLavoro;
	}


	public byte getDipendente() {
		return this.dipendente;
	}

	public void setDipendente(byte dipendente) {
		this.dipendente = dipendente;
	}

	@Id
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(length=255)
	public String getLista() {
		return this.lista;
	}

	public void setLista(String lista) {
		this.lista = lista;
	}


	@Column(length=255)
	public String getMalattia() {
		return this.malattia;
	}

	public void setMalattia(String malattia) {
		this.malattia = malattia;
	}


	@Column(length=255)
	public String getMansione() {
		return this.mansione;
	}

	public void setMansione(String mansione) {
		this.mansione = mansione;
	}


	public Integer getMesotelioma() {
		return this.mesotelioma;
	}

	public void setMesotelioma(Integer mesotelioma) {
		this.mesotelioma = mesotelioma;
	}


	@Column(name="n_1_codice_icd")
	public Integer getN1CodiceIcd() {
		return this.n1CodiceIcd;
	}

	public void setN1CodiceIcd(Integer n1CodiceIcd) {
		this.n1CodiceIcd = n1CodiceIcd;
	}


	@Column(name="n_1_stati_morbosi_rilevanti", length=255)
	public String getN1StatiMorbosiRilevanti() {
		return this.n1StatiMorbosiRilevanti;
	}

	public void setN1StatiMorbosiRilevanti(String n1StatiMorbosiRilevanti) {
		this.n1StatiMorbosiRilevanti = n1StatiMorbosiRilevanti;
	}


	@Column(name="n_2_codice_icd")
	public Integer getN2CodiceIcd() {
		return this.n2CodiceIcd;
	}

	public void setN2CodiceIcd(Integer n2CodiceIcd) {
		this.n2CodiceIcd = n2CodiceIcd;
	}


	@Column(name="n_2_stati_morbosi_rilevanti", length=255)
	public String getN2StatiMorbosiRilevanti() {
		return this.n2StatiMorbosiRilevanti;
	}

	public void setN2StatiMorbosiRilevanti(String n2StatiMorbosiRilevanti) {
		this.n2StatiMorbosiRilevanti = n2StatiMorbosiRilevanti;
	}


	@Column(name="n_3_codice_icd")
	public Integer getN3CodiceIcd() {
		return this.n3CodiceIcd;
	}

	public void setN3CodiceIcd(Integer n3CodiceIcd) {
		this.n3CodiceIcd = n3CodiceIcd;
	}


	@Column(name="n_4_codice_icd")
	public Integer getN4CodiceIcd() {
		return this.n4CodiceIcd;
	}

	public void setN4CodiceIcd(Integer n4CodiceIcd) {
		this.n4CodiceIcd = n4CodiceIcd;
	}


	@Column(name="n_5_codice_icd")
	public Integer getN5CodiceIcd() {
		return this.n5CodiceIcd;
	}

	public void setN5CodiceIcd(Integer n5CodiceIcd) {
		this.n5CodiceIcd = n5CodiceIcd;
	}


	@Column(name="n_noduli")
	public Integer getNNoduli() {
		return this.nNoduli;
	}

	public void setNNoduli(Integer nNoduli) {
		this.nNoduli = nNoduli;
	}


	public Integer getNodulo() {
		return this.nodulo;
	}

	public void setNodulo(Integer nodulo) {
		this.nodulo = nodulo;
	}


	@Column(name="numero_lotus")
	public Double getNumeroLotus() {
		return this.numeroLotus;
	}

	public void setNumeroLotus(Double numeroLotus) {
		this.numeroLotus = numeroLotus;
	}


	public Integer getPlacche() {
		return this.placche;
	}

	public void setPlacche(Integer placche) {
		this.placche = placche;
	}


	@Column(length=255)
	public String getProvincia() {
		return this.provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}


	@Column(name="settore_lavorativo", length=255)
	public String getSettoreLavorativo() {
		return this.settoreLavorativo;
	}

	public void setSettoreLavorativo(String settoreLavorativo) {
		this.settoreLavorativo = settoreLavorativo;
	}


	@Column(name="tumore_polmonare")
	public Integer getTumorePolmonare() {
		return this.tumorePolmonare;
	}

	public void setTumorePolmonare(Integer tumorePolmonare) {
		this.tumorePolmonare = tumorePolmonare;
	}

}