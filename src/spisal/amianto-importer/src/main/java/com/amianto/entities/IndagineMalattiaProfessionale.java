package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the indagine_malattia_professionale database table.
 * 
 */
@Entity
@Table(name="indagine_malattia_professionale")
public class IndagineMalattiaProfessionale implements Serializable {
	private static final long serialVersionUID = 1L;
	private String anni;
	private Integer annoDiagnosi;
	private Integer codiceMansione;
	private Date dataInvioRapporto;
	private String dataSegnalazione;
	private String esposizioneARischio;
	private String icd;
	private Integer id;
	private String livelloEsposizione;
	private String mansioneAttribuita;
	private String medicoCompetente;
	private String mp;
	private String n1DataIndagineAmbientale;
	private String n1IndagineAmbientale;
	private String n1Risultati;
	private String n2DataIndagineAmbientale;
	private String n2IndagineAmbientale;
	private String n2Risultati;
	private String n3DataIndagineAmbientale;
	private String n3IndagineAmbientale;
	private String n3Risultati;
	private byte nuovaDiagnosiDiSorveglianzaSanitaria;
	private String ospedale;
	private String protocollo;
	private String rischioValutato;
	private String sommarieInformazioni;
	private String strutturaCheSegnala;
	private String strutturaDiagnostica;
	private String valutazioneDeiRischi626;
	private Anagrafica anagrafica;
	public IndagineMalattiaProfessionale() {
	}


	@Column(length=250)
	public String getAnni() {
		return this.anni;
	}

	public void setAnni(String anni) {
		this.anni = anni;
	}


	@Column(name="anno_diagnosi")
	public Integer getAnnoDiagnosi() {
		return this.annoDiagnosi;
	}

	public void setAnnoDiagnosi(Integer annoDiagnosi) {
		this.annoDiagnosi = annoDiagnosi;
	}


	@Column(name="codice_mansione")
	public Integer getCodiceMansione() {
		return this.codiceMansione;
	}

	public void setCodiceMansione(Integer codiceMansione) {
		this.codiceMansione = codiceMansione;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_invio_rapporto")
	public Date getDataInvioRapporto() {
		return this.dataInvioRapporto;
	}

	public void setDataInvioRapporto(Date dataInvioRapporto) {
		this.dataInvioRapporto = dataInvioRapporto;
	}


	@Column(name="data_segnalazione", length=50)
	public String getDataSegnalazione() {
		return this.dataSegnalazione;
	}

	public void setDataSegnalazione(String dataSegnalazione) {
		this.dataSegnalazione = dataSegnalazione;
	}


	@Column(name="esposizione_a_rischio", length=50)
	public String getEsposizioneARischio() {
		return this.esposizioneARischio;
	}

	public void setEsposizioneARischio(String esposizioneARischio) {
		this.esposizioneARischio = esposizioneARischio;
	}


	@Column(length=50)
	public String getIcd() {
		return this.icd;
	}

	public void setIcd(String icd) {
		this.icd = icd;
	}

	@Id
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name="livello_esposizione", length=250)
	public String getLivelloEsposizione() {
		return this.livelloEsposizione;
	}

	public void setLivelloEsposizione(String livelloEsposizione) {
		this.livelloEsposizione = livelloEsposizione;
	}


	@Column(name="mansione_attribuita", length=50)
	public String getMansioneAttribuita() {
		return this.mansioneAttribuita;
	}

	public void setMansioneAttribuita(String mansioneAttribuita) {
		this.mansioneAttribuita = mansioneAttribuita;
	}


	@Column(name="medico_competente", length=50)
	public String getMedicoCompetente() {
		return this.medicoCompetente;
	}

	public void setMedicoCompetente(String medicoCompetente) {
		this.medicoCompetente = medicoCompetente;
	}


	@Column(length=50)
	public String getMp() {
		return this.mp;
	}

	public void setMp(String mp) {
		this.mp = mp;
	}


	@Column(name="n_1_data_indagine_ambientale", length=50)
	public String getN1DataIndagineAmbientale() {
		return this.n1DataIndagineAmbientale;
	}

	public void setN1DataIndagineAmbientale(String n1DataIndagineAmbientale) {
		this.n1DataIndagineAmbientale = n1DataIndagineAmbientale;
	}


	@Column(name="n_1_indagine_ambientale", length=250)
	public String getN1IndagineAmbientale() {
		return this.n1IndagineAmbientale;
	}

	public void setN1IndagineAmbientale(String n1IndagineAmbientale) {
		this.n1IndagineAmbientale = n1IndagineAmbientale;
	}


	@Column(name="n_1_risultati", length=50)
	public String getN1Risultati() {
		return this.n1Risultati;
	}

	public void setN1Risultati(String n1Risultati) {
		this.n1Risultati = n1Risultati;
	}


	@Column(name="n_2_data_indagine_ambientale", length=50)
	public String getN2DataIndagineAmbientale() {
		return this.n2DataIndagineAmbientale;
	}

	public void setN2DataIndagineAmbientale(String n2DataIndagineAmbientale) {
		this.n2DataIndagineAmbientale = n2DataIndagineAmbientale;
	}


	@Column(name="n_2_indagine_ambientale", length=250)
	public String getN2IndagineAmbientale() {
		return this.n2IndagineAmbientale;
	}

	public void setN2IndagineAmbientale(String n2IndagineAmbientale) {
		this.n2IndagineAmbientale = n2IndagineAmbientale;
	}


	@Column(name="n_2_risultati", length=50)
	public String getN2Risultati() {
		return this.n2Risultati;
	}

	public void setN2Risultati(String n2Risultati) {
		this.n2Risultati = n2Risultati;
	}


	@Column(name="n_3_data_indagine_ambientale", length=50)
	public String getN3DataIndagineAmbientale() {
		return this.n3DataIndagineAmbientale;
	}

	public void setN3DataIndagineAmbientale(String n3DataIndagineAmbientale) {
		this.n3DataIndagineAmbientale = n3DataIndagineAmbientale;
	}


	@Column(name="n_3_indagine_ambientale", length=250)
	public String getN3IndagineAmbientale() {
		return this.n3IndagineAmbientale;
	}

	public void setN3IndagineAmbientale(String n3IndagineAmbientale) {
		this.n3IndagineAmbientale = n3IndagineAmbientale;
	}


	@Column(name="n_3_risultati", length=50)
	public String getN3Risultati() {
		return this.n3Risultati;
	}

	public void setN3Risultati(String n3Risultati) {
		this.n3Risultati = n3Risultati;
	}


	@Column(name="nuova_diagnosi_di_sorveglianza_sanitaria")
	public byte getNuovaDiagnosiDiSorveglianzaSanitaria() {
		return this.nuovaDiagnosiDiSorveglianzaSanitaria;
	}

	public void setNuovaDiagnosiDiSorveglianzaSanitaria(byte nuovaDiagnosiDiSorveglianzaSanitaria) {
		this.nuovaDiagnosiDiSorveglianzaSanitaria = nuovaDiagnosiDiSorveglianzaSanitaria;
	}


	@Column(length=50)
	public String getOspedale() {
		return this.ospedale;
	}

	public void setOspedale(String ospedale) {
		this.ospedale = ospedale;
	}


	@Column(length=50)
	public String getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}


	@Column(name="rischio_valutato", length=50)
	public String getRischioValutato() {
		return this.rischioValutato;
	}

	public void setRischioValutato(String rischioValutato) {
		this.rischioValutato = rischioValutato;
	}


	@Column(name="sommarie_informazioni", length=50)
	public String getSommarieInformazioni() {
		return this.sommarieInformazioni;
	}

	public void setSommarieInformazioni(String sommarieInformazioni) {
		this.sommarieInformazioni = sommarieInformazioni;
	}


	@Column(name="struttura_che_segnala", length=50)
	public String getStrutturaCheSegnala() {
		return this.strutturaCheSegnala;
	}

	public void setStrutturaCheSegnala(String strutturaCheSegnala) {
		this.strutturaCheSegnala = strutturaCheSegnala;
	}


	@Column(name="struttura_diagnostica", length=50)
	public String getStrutturaDiagnostica() {
		return this.strutturaDiagnostica;
	}

	public void setStrutturaDiagnostica(String strutturaDiagnostica) {
		this.strutturaDiagnostica = strutturaDiagnostica;
	}


	@Column(name="valutazione_dei_rischi_626", length=250)
	public String getValutazioneDeiRischi626() {
		return this.valutazioneDeiRischi626;
	}

	public void setValutazioneDeiRischi626(String valutazioneDeiRischi626) {
		this.valutazioneDeiRischi626 = valutazioneDeiRischi626;
	}


	@ManyToOne
	@JoinColumn(name = "id", referencedColumnName = "id", insertable = false, updatable = false)
	public Anagrafica getAnagrafica() {
	    return anagrafica;
	}


	public void setAnagrafica(Anagrafica param) {
	    this.anagrafica = param;
	}

}