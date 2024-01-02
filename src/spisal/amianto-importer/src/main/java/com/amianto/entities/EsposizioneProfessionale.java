package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the esposizione_professionale database table.
 * 
 */
@Entity
@Table(name="esposizione_professionale")
@NamedQuery(name="EsposizioneProfessionale.findAll", query="SELECT e FROM EsposizioneProfessionale e")
public class EsposizioneProfessionale implements Serializable {
	private static final long serialVersionUID = 1L;
	private String annoFine1;
	private String annoFine2;
	private String annoFine3;
	private String annoFine4;
	private String annoInizio1;
	private String annoInizio2;
	private String annoInizio3;
	private String annoInizio4;
	private String attivita1;
	private String attivita2;
	private String attivita3;
	private String attivita4;
	private String frequenza1;
	private String frequenza2;
	private String frequenza3;
	private String frequenza4;
	private Integer id;
	private String intensita1;
	private String intensita2;
	private String intensita3;
	private String intensita4;
	private String mansione1;
	private String mansione2;
	private String mansione3;
	private String mansione4;
	private Integer n1Confidenza;
	private Integer n1FrequenzaDiEsposizione;
	private Integer n1IntensitaDiEsposizione;
	private Integer n1classeDiOperazioneDisturboMeccanico;
	private Integer n1condizioniDelMateriale;
	private Integer n1confinamentoAmbientale;
	private Integer n1confinamentoDelMateriale;
	private Integer n1confinamentoDellareaDiLavoroEDepurazioneAria;
	private Integer n1dispositiviIndividualiDiProtezione;
	private Integer n1distanzaDallaSorgente;
	private Integer n1estensioneAreaDiLavoro;
	private Integer n1friabilita;
	private Integer n1materiale;
	private Integer n1percezioneDiPolverositaDelLuogoDiLavoro;
	private Integer n1puliziaLuogoDiLavoro;
	private Integer n1strumentiUsatiRilascioDiPolvere;
	private Integer n1tipoFibre;
	private Integer n1usoDiAriaCompressa;
	private Integer n1velocitaRilascioPolvere;
	private Integer n1vicinanzaDellaSorgente;
	private Integer n2Confidenza;
	private Integer n2FrequenzaDiEsposizione;
	private Integer n2IntensitaDiEsposizione;
	private Integer n2classeDiOperazioneDisturboMeccanico;
	private Integer n2condizioniDelMateriale;
	private Integer n2confinamentoAmbientale;
	private Integer n2confinamentoDelMateriale;
	private Integer n2confinamentoDellareaDiLavoroEDepurazioneAria;
	private Integer n2dispositiviIndividualiDiProtezione;
	private Integer n2distanzaDallaSorgente;
	private Integer n2estensioneAreaDiLavoro;
	private Integer n2friabilita;
	private Integer n2materiale;
	private Integer n2percezioneDiPolverositaDelLuogoDiLavoro;
	private Integer n2puliziaLuogoDiLavoro;
	private Integer n2strumentiUsatiRilascioDiPolvere;
	private Integer n2tipoFibre;
	private Integer n2usoDiAriaCompressa;
	private Integer n2velocitaRilascioPolvere;
	private Integer n2vicinanzaDellaSorgente;
	private Integer n3Confidenza;
	private Integer n3FrequenzaDiEsposizione;
	private Integer n3IntensitaDiEsposizione;
	private Integer n3classeDiOperazioneDisturboMeccanico;
	private Integer n3condizioniDelMateriale;
	private Integer n3confinamentoAmbientale;
	private Integer n3confinamentoDelMateriale;
	private Integer n3confinamentoDellareaDiLavoroEDepurazioneAria;
	private Integer n3dispositiviIndividualiDiProtezione;
	private Integer n3distanzaDallaSorgente;
	private Integer n3estensioneAreaDiLavoro;
	private Integer n3friabilita;
	private Integer n3materiale;
	private Integer n3percezioneDiPolverositaDelLuogoDiLavoro;
	private Integer n3puliziaLuogoDiLavoro;
	private Integer n3strumentiUsatiRilascioDiPolvere;
	private Integer n3tipoFibre;
	private Integer n3usoDiAriaCompressa;
	private Integer n3velocitaRilascioPolvere;
	private Integer n3vicinanzaDellaSorgente;
	private Integer n4Confidenza;
	private Integer n4FrequenzaDiEsposizione;
	private Integer n4IntensitaDiEsposizione;
	private Integer n4classeDiOperazioneDisturboMeccanico;
	private Integer n4condizioniDelMateriale;
	private Integer n4confinamentoAmbientale;
	private Integer n4confinamentoDelMateriale;
	private Integer n4confinamentoDellareaDiLavoroEDepurazioneAria;
	private Integer n4dispositiviIndividualiDiProtezione;
	private Integer n4distanzaDallaSorgente;
	private Integer n4estensioneAreaDiLavoro;
	private Integer n4friabilita;
	private Integer n4materiale;
	private Integer n4percezioneDiPolverositaDelLuogoDiLavoro;
	private Integer n4puliziaLuogoDiLavoro;
	private Integer n4strumentiUsatiRilascioDiPolvere;
	private Integer n4tipoFibre;
	private Integer n4usoDiAriaCompressa;
	private Integer n4velocitaRilascioPolvere;
	private Integer n4vicinanzaDellaSorgente;
	private String primaEsposizione;
	private String quartaEsposizione;
	private String secondaEsposizione;
	private String terzaEsposizione;
	private Date ultimoAnnoDiEsposizione;
	public EsposizioneProfessionale() {
	}


	@Column(name="anno_fine_1", length=50)
	public String getAnnoFine1() {
		return this.annoFine1;
	}

	public void setAnnoFine1(String annoFine1) {
		this.annoFine1 = annoFine1;
	}


	@Column(name="anno_fine_2", length=50)
	public String getAnnoFine2() {
		return this.annoFine2;
	}

	public void setAnnoFine2(String annoFine2) {
		this.annoFine2 = annoFine2;
	}


	@Column(name="anno_fine_3", length=50)
	public String getAnnoFine3() {
		return this.annoFine3;
	}

	public void setAnnoFine3(String annoFine3) {
		this.annoFine3 = annoFine3;
	}


	@Column(name="anno_fine_4", length=50)
	public String getAnnoFine4() {
		return this.annoFine4;
	}

	public void setAnnoFine4(String annoFine4) {
		this.annoFine4 = annoFine4;
	}


	@Column(name="anno_inizio_1", length=50)
	public String getAnnoInizio1() {
		return this.annoInizio1;
	}

	public void setAnnoInizio1(String annoInizio1) {
		this.annoInizio1 = annoInizio1;
	}


	@Column(name="anno_inizio_2", length=50)
	public String getAnnoInizio2() {
		return this.annoInizio2;
	}

	public void setAnnoInizio2(String annoInizio2) {
		this.annoInizio2 = annoInizio2;
	}


	@Column(name="anno_inizio_3", length=50)
	public String getAnnoInizio3() {
		return this.annoInizio3;
	}

	public void setAnnoInizio3(String annoInizio3) {
		this.annoInizio3 = annoInizio3;
	}


	@Column(name="anno_inizio_4", length=50)
	public String getAnnoInizio4() {
		return this.annoInizio4;
	}

	public void setAnnoInizio4(String annoInizio4) {
		this.annoInizio4 = annoInizio4;
	}


	@Column(name="attivita_1", length=250)
	public String getAttivita1() {
		return this.attivita1;
	}

	public void setAttivita1(String attivita1) {
		this.attivita1 = attivita1;
	}


	@Column(name="attivita_2", length=250)
	public String getAttivita2() {
		return this.attivita2;
	}

	public void setAttivita2(String attivita2) {
		this.attivita2 = attivita2;
	}


	@Column(name="attivita_3", length=250)
	public String getAttivita3() {
		return this.attivita3;
	}

	public void setAttivita3(String attivita3) {
		this.attivita3 = attivita3;
	}


	@Column(name="attivita_4", length=250)
	public String getAttivita4() {
		return this.attivita4;
	}

	public void setAttivita4(String attivita4) {
		this.attivita4 = attivita4;
	}


	@Column(name="frequenza_1", length=50)
	public String getFrequenza1() {
		return this.frequenza1;
	}

	public void setFrequenza1(String frequenza1) {
		this.frequenza1 = frequenza1;
	}


	@Column(name="frequenza_2", length=50)
	public String getFrequenza2() {
		return this.frequenza2;
	}

	public void setFrequenza2(String frequenza2) {
		this.frequenza2 = frequenza2;
	}


	@Column(name="frequenza_3", length=50)
	public String getFrequenza3() {
		return this.frequenza3;
	}

	public void setFrequenza3(String frequenza3) {
		this.frequenza3 = frequenza3;
	}


	@Column(name="frequenza_4", length=50)
	public String getFrequenza4() {
		return this.frequenza4;
	}

	public void setFrequenza4(String frequenza4) {
		this.frequenza4 = frequenza4;
	}

	@Id
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name="Intensita_1", length=50)
	public String getIntensita1() {
		return this.intensita1;
	}

	public void setIntensita1(String Intensita1) {
		this.intensita1 = Intensita1;
	}


	@Column(name="Intensita_2", length=50)
	public String getIntensita2() {
		return this.intensita2;
	}

	public void setIntensita2(String Intensita2) {
		this.intensita2 = Intensita2;
	}


	@Column(name="Intensita_3", length=50)
	public String getIntensita3() {
		return this.intensita3;
	}

	public void setIntensita3(String Intensita3) {
		this.intensita3 = Intensita3;
	}


	@Column(name="Intensita_4", length=50)
	public String getIntensita4() {
		return this.intensita4;
	}

	public void setIntensita4(String Intensita4) {
		this.intensita4 = Intensita4;
	}


	@Column(name="mansione_1", length=50)
	public String getMansione1() {
		return this.mansione1;
	}

	public void setMansione1(String mansione1) {
		this.mansione1 = mansione1;
	}


	@Column(name="mansione_2", length=50)
	public String getMansione2() {
		return this.mansione2;
	}

	public void setMansione2(String mansione2) {
		this.mansione2 = mansione2;
	}


	@Column(name="mansione_3", length=50)
	public String getMansione3() {
		return this.mansione3;
	}

	public void setMansione3(String mansione3) {
		this.mansione3 = mansione3;
	}


	@Column(name="mansione_4", length=50)
	public String getMansione4() {
		return this.mansione4;
	}

	public void setMansione4(String mansione4) {
		this.mansione4 = mansione4;
	}


	@Column(name="n_1_confidenza")
	public Integer getN1Confidenza() {
		return this.n1Confidenza;
	}

	public void setN1Confidenza(Integer n1Confidenza) {
		this.n1Confidenza = n1Confidenza;
	}


	@Column(name="n_1_frequenza_di_esposizione")
	public Integer getN1FrequenzaDiEsposizione() {
		return this.n1FrequenzaDiEsposizione;
	}

	public void setN1FrequenzaDiEsposizione(Integer n1FrequenzaDiEsposizione) {
		this.n1FrequenzaDiEsposizione = n1FrequenzaDiEsposizione;
	}


	@Column(name="n_1_Intensita_di_esposizione")
	public Integer getN1IntensitaDiEsposizione() {
		return this.n1IntensitaDiEsposizione;
	}

	public void setN1IntensitaDiEsposizione(Integer n1IntensitaDiEsposizione) {
		this.n1IntensitaDiEsposizione = n1IntensitaDiEsposizione;
	}


	@Column(name="n_1classe_di_operazione_disturbo_meccanico")
	public Integer getN1classeDiOperazioneDisturboMeccanico() {
		return this.n1classeDiOperazioneDisturboMeccanico;
	}

	public void setN1classeDiOperazioneDisturboMeccanico(Integer n1classeDiOperazioneDisturboMeccanico) {
		this.n1classeDiOperazioneDisturboMeccanico = n1classeDiOperazioneDisturboMeccanico;
	}


	@Column(name="n_1condizioni_del_materiale")
	public Integer getN1condizioniDelMateriale() {
		return this.n1condizioniDelMateriale;
	}

	public void setN1condizioniDelMateriale(Integer n1condizioniDelMateriale) {
		this.n1condizioniDelMateriale = n1condizioniDelMateriale;
	}


	@Column(name="n_1confinamento_ambientale")
	public Integer getN1confinamentoAmbientale() {
		return this.n1confinamentoAmbientale;
	}

	public void setN1confinamentoAmbientale(Integer n1confinamentoAmbientale) {
		this.n1confinamentoAmbientale = n1confinamentoAmbientale;
	}


	@Column(name="n_1confinamento_del_materiale")
	public Integer getN1confinamentoDelMateriale() {
		return this.n1confinamentoDelMateriale;
	}

	public void setN1confinamentoDelMateriale(Integer n1confinamentoDelMateriale) {
		this.n1confinamentoDelMateriale = n1confinamentoDelMateriale;
	}


	@Column(name="n_1confinamento_dellarea_di_lavoro_e_depurazione_aria")
	public Integer getN1confinamentoDellareaDiLavoroEDepurazioneAria() {
		return this.n1confinamentoDellareaDiLavoroEDepurazioneAria;
	}

	public void setN1confinamentoDellareaDiLavoroEDepurazioneAria(Integer n1confinamentoDellareaDiLavoroEDepurazioneAria) {
		this.n1confinamentoDellareaDiLavoroEDepurazioneAria = n1confinamentoDellareaDiLavoroEDepurazioneAria;
	}


	@Column(name="n_1dispositivi_individuali_di_protezione")
	public Integer getN1dispositiviIndividualiDiProtezione() {
		return this.n1dispositiviIndividualiDiProtezione;
	}

	public void setN1dispositiviIndividualiDiProtezione(Integer n1dispositiviIndividualiDiProtezione) {
		this.n1dispositiviIndividualiDiProtezione = n1dispositiviIndividualiDiProtezione;
	}


	@Column(name="n_1distanza_dalla_sorgente")
	public Integer getN1distanzaDallaSorgente() {
		return this.n1distanzaDallaSorgente;
	}

	public void setN1distanzaDallaSorgente(Integer n1distanzaDallaSorgente) {
		this.n1distanzaDallaSorgente = n1distanzaDallaSorgente;
	}


	@Column(name="n_1estensione_area_di_lavoro")
	public Integer getN1estensioneAreaDiLavoro() {
		return this.n1estensioneAreaDiLavoro;
	}

	public void setN1estensioneAreaDiLavoro(Integer n1estensioneAreaDiLavoro) {
		this.n1estensioneAreaDiLavoro = n1estensioneAreaDiLavoro;
	}


	@Column(name="n_1friabilita")
	public Integer getN1friabilita() {
		return this.n1friabilita;
	}

	public void setN1friabilita(Integer n1friabilita) {
		this.n1friabilita = n1friabilita;
	}


	@Column(name="n_1materiale")
	public Integer getN1materiale() {
		return this.n1materiale;
	}

	public void setN1materiale(Integer n1materiale) {
		this.n1materiale = n1materiale;
	}


	@Column(name="n_1percezione_di_polverosita_del_luogo_di_lavoro")
	public Integer getN1percezioneDiPolverositaDelLuogoDiLavoro() {
		return this.n1percezioneDiPolverositaDelLuogoDiLavoro;
	}

	public void setN1percezioneDiPolverositaDelLuogoDiLavoro(Integer n1percezioneDiPolverositaDelLuogoDiLavoro) {
		this.n1percezioneDiPolverositaDelLuogoDiLavoro = n1percezioneDiPolverositaDelLuogoDiLavoro;
	}


	@Column(name="n_1pulizia_luogo_di_lavoro")
	public Integer getN1puliziaLuogoDiLavoro() {
		return this.n1puliziaLuogoDiLavoro;
	}

	public void setN1puliziaLuogoDiLavoro(Integer n1puliziaLuogoDiLavoro) {
		this.n1puliziaLuogoDiLavoro = n1puliziaLuogoDiLavoro;
	}


	@Column(name="n_1strumenti_usati_rilascio_di_polvere")
	public Integer getN1strumentiUsatiRilascioDiPolvere() {
		return this.n1strumentiUsatiRilascioDiPolvere;
	}

	public void setN1strumentiUsatiRilascioDiPolvere(Integer n1strumentiUsatiRilascioDiPolvere) {
		this.n1strumentiUsatiRilascioDiPolvere = n1strumentiUsatiRilascioDiPolvere;
	}


	@Column(name="n_1tipo_fibre")
	public Integer getN1tipoFibre() {
		return this.n1tipoFibre;
	}

	public void setN1tipoFibre(Integer n1tipoFibre) {
		this.n1tipoFibre = n1tipoFibre;
	}


	@Column(name="n_1uso_di_aria_compressa")
	public Integer getN1usoDiAriaCompressa() {
		return this.n1usoDiAriaCompressa;
	}

	public void setN1usoDiAriaCompressa(Integer n1usoDiAriaCompressa) {
		this.n1usoDiAriaCompressa = n1usoDiAriaCompressa;
	}


	@Column(name="n_1velocita_rilascio_polvere")
	public Integer getN1velocitaRilascioPolvere() {
		return this.n1velocitaRilascioPolvere;
	}

	public void setN1velocitaRilascioPolvere(Integer n1velocitaRilascioPolvere) {
		this.n1velocitaRilascioPolvere = n1velocitaRilascioPolvere;
	}


	@Column(name="n_1vicinanza_della_sorgente")
	public Integer getN1vicinanzaDellaSorgente() {
		return this.n1vicinanzaDellaSorgente;
	}

	public void setN1vicinanzaDellaSorgente(Integer n1vicinanzaDellaSorgente) {
		this.n1vicinanzaDellaSorgente = n1vicinanzaDellaSorgente;
	}


	@Column(name="n_2_confidenza")
	public Integer getN2Confidenza() {
		return this.n2Confidenza;
	}

	public void setN2Confidenza(Integer n2Confidenza) {
		this.n2Confidenza = n2Confidenza;
	}


	@Column(name="n_2_frequenza_di_esposizione")
	public Integer getN2FrequenzaDiEsposizione() {
		return this.n2FrequenzaDiEsposizione;
	}

	public void setN2FrequenzaDiEsposizione(Integer n2FrequenzaDiEsposizione) {
		this.n2FrequenzaDiEsposizione = n2FrequenzaDiEsposizione;
	}


	@Column(name="n_2_Intensita_di_esposizione")
	public Integer getN2IntensitaDiEsposizione() {
		return this.n2IntensitaDiEsposizione;
	}

	public void setN2IntensitaDiEsposizione(Integer n2IntensitaDiEsposizione) {
		this.n2IntensitaDiEsposizione = n2IntensitaDiEsposizione;
	}


	@Column(name="n_2classe_di_operazione_disturbo_meccanico")
	public Integer getN2classeDiOperazioneDisturboMeccanico() {
		return this.n2classeDiOperazioneDisturboMeccanico;
	}

	public void setN2classeDiOperazioneDisturboMeccanico(Integer n2classeDiOperazioneDisturboMeccanico) {
		this.n2classeDiOperazioneDisturboMeccanico = n2classeDiOperazioneDisturboMeccanico;
	}


	@Column(name="n_2condizioni_del_materiale")
	public Integer getN2condizioniDelMateriale() {
		return this.n2condizioniDelMateriale;
	}

	public void setN2condizioniDelMateriale(Integer n2condizioniDelMateriale) {
		this.n2condizioniDelMateriale = n2condizioniDelMateriale;
	}


	@Column(name="n_2confinamento_ambientale")
	public Integer getN2confinamentoAmbientale() {
		return this.n2confinamentoAmbientale;
	}

	public void setN2confinamentoAmbientale(Integer n2confinamentoAmbientale) {
		this.n2confinamentoAmbientale = n2confinamentoAmbientale;
	}


	@Column(name="n_2confinamento_del_materiale")
	public Integer getN2confinamentoDelMateriale() {
		return this.n2confinamentoDelMateriale;
	}

	public void setN2confinamentoDelMateriale(Integer n2confinamentoDelMateriale) {
		this.n2confinamentoDelMateriale = n2confinamentoDelMateriale;
	}


	@Column(name="n_2confinamento_dellarea_di_lavoro_e_depurazione_aria")
	public Integer getN2confinamentoDellareaDiLavoroEDepurazioneAria() {
		return this.n2confinamentoDellareaDiLavoroEDepurazioneAria;
	}

	public void setN2confinamentoDellareaDiLavoroEDepurazioneAria(Integer n2confinamentoDellareaDiLavoroEDepurazioneAria) {
		this.n2confinamentoDellareaDiLavoroEDepurazioneAria = n2confinamentoDellareaDiLavoroEDepurazioneAria;
	}


	@Column(name="n_2dispositivi_individuali_di_protezione")
	public Integer getN2dispositiviIndividualiDiProtezione() {
		return this.n2dispositiviIndividualiDiProtezione;
	}

	public void setN2dispositiviIndividualiDiProtezione(Integer n2dispositiviIndividualiDiProtezione) {
		this.n2dispositiviIndividualiDiProtezione = n2dispositiviIndividualiDiProtezione;
	}


	@Column(name="n_2distanza_dalla_sorgente")
	public Integer getN2distanzaDallaSorgente() {
		return this.n2distanzaDallaSorgente;
	}

	public void setN2distanzaDallaSorgente(Integer n2distanzaDallaSorgente) {
		this.n2distanzaDallaSorgente = n2distanzaDallaSorgente;
	}


	@Column(name="n_2estensione_area_di_lavoro")
	public Integer getN2estensioneAreaDiLavoro() {
		return this.n2estensioneAreaDiLavoro;
	}

	public void setN2estensioneAreaDiLavoro(Integer n2estensioneAreaDiLavoro) {
		this.n2estensioneAreaDiLavoro = n2estensioneAreaDiLavoro;
	}


	@Column(name="n_2friabilita")
	public Integer getN2friabilita() {
		return this.n2friabilita;
	}

	public void setN2friabilita(Integer n2friabilita) {
		this.n2friabilita = n2friabilita;
	}


	@Column(name="n_2materiale")
	public Integer getN2materiale() {
		return this.n2materiale;
	}

	public void setN2materiale(Integer n2materiale) {
		this.n2materiale = n2materiale;
	}


	@Column(name="n_2percezione_di_polverosita_del_luogo_di_lavoro")
	public Integer getN2percezioneDiPolverositaDelLuogoDiLavoro() {
		return this.n2percezioneDiPolverositaDelLuogoDiLavoro;
	}

	public void setN2percezioneDiPolverositaDelLuogoDiLavoro(Integer n2percezioneDiPolverositaDelLuogoDiLavoro) {
		this.n2percezioneDiPolverositaDelLuogoDiLavoro = n2percezioneDiPolverositaDelLuogoDiLavoro;
	}


	@Column(name="n_2pulizia_luogo_di_lavoro")
	public Integer getN2puliziaLuogoDiLavoro() {
		return this.n2puliziaLuogoDiLavoro;
	}

	public void setN2puliziaLuogoDiLavoro(Integer n2puliziaLuogoDiLavoro) {
		this.n2puliziaLuogoDiLavoro = n2puliziaLuogoDiLavoro;
	}


	@Column(name="n_2strumenti_usati_rilascio_di_polvere")
	public Integer getN2strumentiUsatiRilascioDiPolvere() {
		return this.n2strumentiUsatiRilascioDiPolvere;
	}

	public void setN2strumentiUsatiRilascioDiPolvere(Integer n2strumentiUsatiRilascioDiPolvere) {
		this.n2strumentiUsatiRilascioDiPolvere = n2strumentiUsatiRilascioDiPolvere;
	}


	@Column(name="n_2tipo_fibre")
	public Integer getN2tipoFibre() {
		return this.n2tipoFibre;
	}

	public void setN2tipoFibre(Integer n2tipoFibre) {
		this.n2tipoFibre = n2tipoFibre;
	}


	@Column(name="n_2uso_di_aria_compressa")
	public Integer getN2usoDiAriaCompressa() {
		return this.n2usoDiAriaCompressa;
	}

	public void setN2usoDiAriaCompressa(Integer n2usoDiAriaCompressa) {
		this.n2usoDiAriaCompressa = n2usoDiAriaCompressa;
	}


	@Column(name="n_2velocita_rilascio_polvere")
	public Integer getN2velocitaRilascioPolvere() {
		return this.n2velocitaRilascioPolvere;
	}

	public void setN2velocitaRilascioPolvere(Integer n2velocitaRilascioPolvere) {
		this.n2velocitaRilascioPolvere = n2velocitaRilascioPolvere;
	}


	@Column(name="n_2vicinanza_della_sorgente")
	public Integer getN2vicinanzaDellaSorgente() {
		return this.n2vicinanzaDellaSorgente;
	}

	public void setN2vicinanzaDellaSorgente(Integer n2vicinanzaDellaSorgente) {
		this.n2vicinanzaDellaSorgente = n2vicinanzaDellaSorgente;
	}


	@Column(name="n_3_confidenza")
	public Integer getN3Confidenza() {
		return this.n3Confidenza;
	}

	public void setN3Confidenza(Integer n3Confidenza) {
		this.n3Confidenza = n3Confidenza;
	}


	@Column(name="n_3_frequenza_di_esposizione")
	public Integer getN3FrequenzaDiEsposizione() {
		return this.n3FrequenzaDiEsposizione;
	}

	public void setN3FrequenzaDiEsposizione(Integer n3FrequenzaDiEsposizione) {
		this.n3FrequenzaDiEsposizione = n3FrequenzaDiEsposizione;
	}


	@Column(name="n_3_Intensita_di_esposizione")
	public Integer getN3IntensitaDiEsposizione() {
		return this.n3IntensitaDiEsposizione;
	}

	public void setN3IntensitaDiEsposizione(Integer n3IntensitaDiEsposizione) {
		this.n3IntensitaDiEsposizione = n3IntensitaDiEsposizione;
	}


	@Column(name="n_3classe_di_operazione_disturbo_meccanico")
	public Integer getN3classeDiOperazioneDisturboMeccanico() {
		return this.n3classeDiOperazioneDisturboMeccanico;
	}

	public void setN3classeDiOperazioneDisturboMeccanico(Integer n3classeDiOperazioneDisturboMeccanico) {
		this.n3classeDiOperazioneDisturboMeccanico = n3classeDiOperazioneDisturboMeccanico;
	}


	@Column(name="n_3condizioni_del_materiale")
	public Integer getN3condizioniDelMateriale() {
		return this.n3condizioniDelMateriale;
	}

	public void setN3condizioniDelMateriale(Integer n3condizioniDelMateriale) {
		this.n3condizioniDelMateriale = n3condizioniDelMateriale;
	}


	@Column(name="n_3confinamento_ambientale")
	public Integer getN3confinamentoAmbientale() {
		return this.n3confinamentoAmbientale;
	}

	public void setN3confinamentoAmbientale(Integer n3confinamentoAmbientale) {
		this.n3confinamentoAmbientale = n3confinamentoAmbientale;
	}


	@Column(name="n_3confinamento_del_materiale")
	public Integer getN3confinamentoDelMateriale() {
		return this.n3confinamentoDelMateriale;
	}

	public void setN3confinamentoDelMateriale(Integer n3confinamentoDelMateriale) {
		this.n3confinamentoDelMateriale = n3confinamentoDelMateriale;
	}


	@Column(name="n_3confinamento_dellarea_di_lavoro_e_depurazione_aria")
	public Integer getN3confinamentoDellareaDiLavoroEDepurazioneAria() {
		return this.n3confinamentoDellareaDiLavoroEDepurazioneAria;
	}

	public void setN3confinamentoDellareaDiLavoroEDepurazioneAria(Integer n3confinamentoDellareaDiLavoroEDepurazioneAria) {
		this.n3confinamentoDellareaDiLavoroEDepurazioneAria = n3confinamentoDellareaDiLavoroEDepurazioneAria;
	}


	@Column(name="n_3dispositivi_individuali_di_protezione")
	public Integer getN3dispositiviIndividualiDiProtezione() {
		return this.n3dispositiviIndividualiDiProtezione;
	}

	public void setN3dispositiviIndividualiDiProtezione(Integer n3dispositiviIndividualiDiProtezione) {
		this.n3dispositiviIndividualiDiProtezione = n3dispositiviIndividualiDiProtezione;
	}


	@Column(name="n_3distanza_dalla_sorgente")
	public Integer getN3distanzaDallaSorgente() {
		return this.n3distanzaDallaSorgente;
	}

	public void setN3distanzaDallaSorgente(Integer n3distanzaDallaSorgente) {
		this.n3distanzaDallaSorgente = n3distanzaDallaSorgente;
	}


	@Column(name="n_3estensione_area_di_lavoro")
	public Integer getN3estensioneAreaDiLavoro() {
		return this.n3estensioneAreaDiLavoro;
	}

	public void setN3estensioneAreaDiLavoro(Integer n3estensioneAreaDiLavoro) {
		this.n3estensioneAreaDiLavoro = n3estensioneAreaDiLavoro;
	}


	@Column(name="n_3friabilita")
	public Integer getN3friabilita() {
		return this.n3friabilita;
	}

	public void setN3friabilita(Integer n3friabilita) {
		this.n3friabilita = n3friabilita;
	}


	@Column(name="n_3materiale")
	public Integer getN3materiale() {
		return this.n3materiale;
	}

	public void setN3materiale(Integer n3materiale) {
		this.n3materiale = n3materiale;
	}


	@Column(name="n_3percezione_di_polverosita_del_luogo_di_lavoro")
	public Integer getN3percezioneDiPolverositaDelLuogoDiLavoro() {
		return this.n3percezioneDiPolverositaDelLuogoDiLavoro;
	}

	public void setN3percezioneDiPolverositaDelLuogoDiLavoro(Integer n3percezioneDiPolverositaDelLuogoDiLavoro) {
		this.n3percezioneDiPolverositaDelLuogoDiLavoro = n3percezioneDiPolverositaDelLuogoDiLavoro;
	}


	@Column(name="n_3pulizia_luogo_di_lavoro")
	public Integer getN3puliziaLuogoDiLavoro() {
		return this.n3puliziaLuogoDiLavoro;
	}

	public void setN3puliziaLuogoDiLavoro(Integer n3puliziaLuogoDiLavoro) {
		this.n3puliziaLuogoDiLavoro = n3puliziaLuogoDiLavoro;
	}


	@Column(name="n_3strumenti_usati_rilascio_di_polvere")
	public Integer getN3strumentiUsatiRilascioDiPolvere() {
		return this.n3strumentiUsatiRilascioDiPolvere;
	}

	public void setN3strumentiUsatiRilascioDiPolvere(Integer n3strumentiUsatiRilascioDiPolvere) {
		this.n3strumentiUsatiRilascioDiPolvere = n3strumentiUsatiRilascioDiPolvere;
	}


	@Column(name="n_3tipo_fibre")
	public Integer getN3tipoFibre() {
		return this.n3tipoFibre;
	}

	public void setN3tipoFibre(Integer n3tipoFibre) {
		this.n3tipoFibre = n3tipoFibre;
	}


	@Column(name="n_3uso_di_aria_compressa")
	public Integer getN3usoDiAriaCompressa() {
		return this.n3usoDiAriaCompressa;
	}

	public void setN3usoDiAriaCompressa(Integer n3usoDiAriaCompressa) {
		this.n3usoDiAriaCompressa = n3usoDiAriaCompressa;
	}


	@Column(name="n_3velocita_rilascio_polvere")
	public Integer getN3velocitaRilascioPolvere() {
		return this.n3velocitaRilascioPolvere;
	}

	public void setN3velocitaRilascioPolvere(Integer n3velocitaRilascioPolvere) {
		this.n3velocitaRilascioPolvere = n3velocitaRilascioPolvere;
	}


	@Column(name="n_3vicinanza_della_sorgente")
	public Integer getN3vicinanzaDellaSorgente() {
		return this.n3vicinanzaDellaSorgente;
	}

	public void setN3vicinanzaDellaSorgente(Integer n3vicinanzaDellaSorgente) {
		this.n3vicinanzaDellaSorgente = n3vicinanzaDellaSorgente;
	}


	@Column(name="n_4_confidenza")
	public Integer getN4Confidenza() {
		return this.n4Confidenza;
	}

	public void setN4Confidenza(Integer n4Confidenza) {
		this.n4Confidenza = n4Confidenza;
	}


	@Column(name="n_4_frequenza_di_esposizione")
	public Integer getN4FrequenzaDiEsposizione() {
		return this.n4FrequenzaDiEsposizione;
	}

	public void setN4FrequenzaDiEsposizione(Integer n4FrequenzaDiEsposizione) {
		this.n4FrequenzaDiEsposizione = n4FrequenzaDiEsposizione;
	}


	@Column(name="n_4_Intensita_di_esposizione")
	public Integer getN4IntensitaDiEsposizione() {
		return this.n4IntensitaDiEsposizione;
	}

	public void setN4IntensitaDiEsposizione(Integer n4IntensitaDiEsposizione) {
		this.n4IntensitaDiEsposizione = n4IntensitaDiEsposizione;
	}


	@Column(name="n_4classe_di_operazione_disturbo_meccanico")
	public Integer getN4classeDiOperazioneDisturboMeccanico() {
		return this.n4classeDiOperazioneDisturboMeccanico;
	}

	public void setN4classeDiOperazioneDisturboMeccanico(Integer n4classeDiOperazioneDisturboMeccanico) {
		this.n4classeDiOperazioneDisturboMeccanico = n4classeDiOperazioneDisturboMeccanico;
	}


	@Column(name="n_4condizioni_del_materiale")
	public Integer getN4condizioniDelMateriale() {
		return this.n4condizioniDelMateriale;
	}

	public void setN4condizioniDelMateriale(Integer n4condizioniDelMateriale) {
		this.n4condizioniDelMateriale = n4condizioniDelMateriale;
	}


	@Column(name="n_4confinamento_ambientale")
	public Integer getN4confinamentoAmbientale() {
		return this.n4confinamentoAmbientale;
	}

	public void setN4confinamentoAmbientale(Integer n4confinamentoAmbientale) {
		this.n4confinamentoAmbientale = n4confinamentoAmbientale;
	}


	@Column(name="n_4confinamento_del_materiale")
	public Integer getN4confinamentoDelMateriale() {
		return this.n4confinamentoDelMateriale;
	}

	public void setN4confinamentoDelMateriale(Integer n4confinamentoDelMateriale) {
		this.n4confinamentoDelMateriale = n4confinamentoDelMateriale;
	}


	@Column(name="n_4confinamento_dellarea_di_lavoro_e_depurazione_aria")
	public Integer getN4confinamentoDellareaDiLavoroEDepurazioneAria() {
		return this.n4confinamentoDellareaDiLavoroEDepurazioneAria;
	}

	public void setN4confinamentoDellareaDiLavoroEDepurazioneAria(Integer n4confinamentoDellareaDiLavoroEDepurazioneAria) {
		this.n4confinamentoDellareaDiLavoroEDepurazioneAria = n4confinamentoDellareaDiLavoroEDepurazioneAria;
	}


	@Column(name="n_4dispositivi_individuali_di_protezione")
	public Integer getN4dispositiviIndividualiDiProtezione() {
		return this.n4dispositiviIndividualiDiProtezione;
	}

	public void setN4dispositiviIndividualiDiProtezione(Integer n4dispositiviIndividualiDiProtezione) {
		this.n4dispositiviIndividualiDiProtezione = n4dispositiviIndividualiDiProtezione;
	}


	@Column(name="n_4distanza_dalla_sorgente")
	public Integer getN4distanzaDallaSorgente() {
		return this.n4distanzaDallaSorgente;
	}

	public void setN4distanzaDallaSorgente(Integer n4distanzaDallaSorgente) {
		this.n4distanzaDallaSorgente = n4distanzaDallaSorgente;
	}


	@Column(name="n_4estensione_area_di_lavoro")
	public Integer getN4estensioneAreaDiLavoro() {
		return this.n4estensioneAreaDiLavoro;
	}

	public void setN4estensioneAreaDiLavoro(Integer n4estensioneAreaDiLavoro) {
		this.n4estensioneAreaDiLavoro = n4estensioneAreaDiLavoro;
	}


	@Column(name="n_4friabilita")
	public Integer getN4friabilita() {
		return this.n4friabilita;
	}

	public void setN4friabilita(Integer n4friabilita) {
		this.n4friabilita = n4friabilita;
	}


	@Column(name="n_4materiale")
	public Integer getN4materiale() {
		return this.n4materiale;
	}

	public void setN4materiale(Integer n4materiale) {
		this.n4materiale = n4materiale;
	}


	@Column(name="n_4percezione_di_polverosita_del_luogo_di_lavoro")
	public Integer getN4percezioneDiPolverositaDelLuogoDiLavoro() {
		return this.n4percezioneDiPolverositaDelLuogoDiLavoro;
	}

	public void setN4percezioneDiPolverositaDelLuogoDiLavoro(Integer n4percezioneDiPolverositaDelLuogoDiLavoro) {
		this.n4percezioneDiPolverositaDelLuogoDiLavoro = n4percezioneDiPolverositaDelLuogoDiLavoro;
	}


	@Column(name="n_4pulizia_luogo_di_lavoro")
	public Integer getN4puliziaLuogoDiLavoro() {
		return this.n4puliziaLuogoDiLavoro;
	}

	public void setN4puliziaLuogoDiLavoro(Integer n4puliziaLuogoDiLavoro) {
		this.n4puliziaLuogoDiLavoro = n4puliziaLuogoDiLavoro;
	}


	@Column(name="n_4strumenti_usati_rilascio_di_polvere")
	public Integer getN4strumentiUsatiRilascioDiPolvere() {
		return this.n4strumentiUsatiRilascioDiPolvere;
	}

	public void setN4strumentiUsatiRilascioDiPolvere(Integer n4strumentiUsatiRilascioDiPolvere) {
		this.n4strumentiUsatiRilascioDiPolvere = n4strumentiUsatiRilascioDiPolvere;
	}


	@Column(name="n_4tipo_fibre")
	public Integer getN4tipoFibre() {
		return this.n4tipoFibre;
	}

	public void setN4tipoFibre(Integer n4tipoFibre) {
		this.n4tipoFibre = n4tipoFibre;
	}


	@Column(name="n_4uso_di_aria_compressa")
	public Integer getN4usoDiAriaCompressa() {
		return this.n4usoDiAriaCompressa;
	}

	public void setN4usoDiAriaCompressa(Integer n4usoDiAriaCompressa) {
		this.n4usoDiAriaCompressa = n4usoDiAriaCompressa;
	}


	@Column(name="n_4velocita_rilascio_polvere")
	public Integer getN4velocitaRilascioPolvere() {
		return this.n4velocitaRilascioPolvere;
	}

	public void setN4velocitaRilascioPolvere(Integer n4velocitaRilascioPolvere) {
		this.n4velocitaRilascioPolvere = n4velocitaRilascioPolvere;
	}


	@Column(name="n_4vicinanza_della_sorgente")
	public Integer getN4vicinanzaDellaSorgente() {
		return this.n4vicinanzaDellaSorgente;
	}

	public void setN4vicinanzaDellaSorgente(Integer n4vicinanzaDellaSorgente) {
		this.n4vicinanzaDellaSorgente = n4vicinanzaDellaSorgente;
	}


	@Column(name="prima_esposizione", length=255)
	public String getPrimaEsposizione() {
		return this.primaEsposizione;
	}

	public void setPrimaEsposizione(String primaEsposizione) {
		this.primaEsposizione = primaEsposizione;
	}


	@Column(name="quarta_esposizione", length=255)
	public String getQuartaEsposizione() {
		return this.quartaEsposizione;
	}

	public void setQuartaEsposizione(String quartaEsposizione) {
		this.quartaEsposizione = quartaEsposizione;
	}


	@Column(name="seconda_esposizione", length=255)
	public String getSecondaEsposizione() {
		return this.secondaEsposizione;
	}

	public void setSecondaEsposizione(String secondaEsposizione) {
		this.secondaEsposizione = secondaEsposizione;
	}


	@Column(name="terza_esposizione", length=255)
	public String getTerzaEsposizione() {
		return this.terzaEsposizione;
	}

	public void setTerzaEsposizione(String terzaEsposizione) {
		this.terzaEsposizione = terzaEsposizione;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ultimo_anno_di_esposizione")
	public Date getUltimoAnnoDiEsposizione() {
		return this.ultimoAnnoDiEsposizione;
	}

	public void setUltimoAnnoDiEsposizione(Date ultimoAnnoDiEsposizione) {
		this.ultimoAnnoDiEsposizione = ultimoAnnoDiEsposizione;
	}

}