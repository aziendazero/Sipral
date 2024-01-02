package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the anamnesi_prima_visita database table.
 * 
 */
@Entity
@Table(name="anamnesi_prima_visita")
public class AnamnesiPrimaVisita implements Serializable {
	private static final long serialVersionUID = 1L;
	private String altezza;
	private byte antiinfluenzale;
	private Integer asbestosi;
	private Integer bilaterali;
	private String biopsia;
	private Double codice;
	private String conclusioni;
	private Integer cvpc;
	private Date data;
	private String dataAggiornamentoPrimo;
	private String dataAggiornamentoSecondo;
	private String dataAggiornamentoTerzo;
	private Date dataAltraVisita;
	private String dataConclusioniELetteraCurante;
	private Date dataInvioLetteraAdesione2004;
	private String dataPrelievo;
	private Date dataPrimaVisita;
	private String dataRicovero;
	private Date dataSpirometria;
	private Date dataVisitaChirurgica;
	private Date dataVisitaPneumologica;
	private String datiAnamnesticiSalienti;
	private String diagnosiDimissione;
	private String diametro1;
	private Integer dispnea;
	private String dlco;
	private String esameObiettivo;
	private Integer escreato;
	private String esposizioneAccidentaleARxORadionuclidi;
	private Integer etaFineFumo;
	private Integer etaInizioFumo;
	private Integer fattoriAtmosferici;
	private String febbre;
	private String followUp;
	private Integer followUpSi_no;
	private String folow_upMotivo;
	private Integer fumoAttuale;
	private Integer id;
	private String lesione1;
	private String malattiaProfessionale;
	private String malattieInfettiveUltimi5Anni;
	private String malattieProfessionali;
	private Integer mesotelioma;
	private String motivoRadioterapia;
	private byte n1AnnoFa;
	private byte n2AnniFa;
	private byte n3AnniFa;
	private byte n4AnniFa;
	private byte n5AnniFa;
	private Integer nNoduli;
	private Integer nRadioterapia;
	private Integer nRx;
	private Integer nSigaretteGiorno;
	private Integer nodulo;
	private String numero1;
	private String pcInail;
	private String periodoRadioterapia;
	private String peso;
	private String pet;
	private Integer placche;
	private String prelievoEmatico;
	private Date primaIndagineRadiologica;
	private Integer radioterapia;
	private Integer rantoliniBasilari;
	private String referto1;
	private String refertoBiopsia;
	private String refertoPet;
	private String repartoRicovero;
	private Integer rxLavoroScuola;
	private String sedeRadioterapia;
	private String spirometria;
	private Integer tbc;
	private Integer tosse;
	private Integer tumorePolmonare;
	private String vaccinazioneTbc;
	private Integer vems_cvpc;
	private Integer vemspc;
	private String visita;
	private String visitaChirurgica;
	private String visitaPneumologica;
	public AnamnesiPrimaVisita() {
	}


	@Column(length=50)
	public String getAltezza() {
		return this.altezza;
	}

	public void setAltezza(String altezza) {
		this.altezza = altezza;
	}


	public byte getAntiinfluenzale() {
		return this.antiinfluenzale;
	}

	public void setAntiinfluenzale(byte antiinfluenzale) {
		this.antiinfluenzale = antiinfluenzale;
	}


	public Integer getAsbestosi() {
		return this.asbestosi;
	}

	public void setAsbestosi(Integer asbestosi) {
		this.asbestosi = asbestosi;
	}


	public Integer getBilaterali() {
		return this.bilaterali;
	}

	public void setBilaterali(Integer bilaterali) {
		this.bilaterali = bilaterali;
	}


	@Column(length=255)
	public String getBiopsia() {
		return this.biopsia;
	}

	public void setBiopsia(String biopsia) {
		this.biopsia = biopsia;
	}


	public Double getCodice() {
		return this.codice;
	}

	public void setCodice(Double codice) {
		this.codice = codice;
	}


	@Column(length=255)
	public String getConclusioni() {
		return this.conclusioni;
	}

	public void setConclusioni(String conclusioni) {
		this.conclusioni = conclusioni;
	}


	public Integer getCvpc() {
		return this.cvpc;
	}

	public void setCvpc(Integer cvpc) {
		this.cvpc = cvpc;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	@Column(name="data_aggiornamento_primo", length=50)
	public String getDataAggiornamentoPrimo() {
		return this.dataAggiornamentoPrimo;
	}

	public void setDataAggiornamentoPrimo(String dataAggiornamentoPrimo) {
		this.dataAggiornamentoPrimo = dataAggiornamentoPrimo;
	}


	@Column(name="data_aggiornamento_secondo", length=50)
	public String getDataAggiornamentoSecondo() {
		return this.dataAggiornamentoSecondo;
	}

	public void setDataAggiornamentoSecondo(String dataAggiornamentoSecondo) {
		this.dataAggiornamentoSecondo = dataAggiornamentoSecondo;
	}


	@Column(name="data_aggiornamento_terzo", length=50)
	public String getDataAggiornamentoTerzo() {
		return this.dataAggiornamentoTerzo;
	}

	public void setDataAggiornamentoTerzo(String dataAggiornamentoTerzo) {
		this.dataAggiornamentoTerzo = dataAggiornamentoTerzo;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_altra_visita")
	public Date getDataAltraVisita() {
		return this.dataAltraVisita;
	}

	public void setDataAltraVisita(Date dataAltraVisita) {
		this.dataAltraVisita = dataAltraVisita;
	}


	@Column(name="data_conclusioni_e_lettera_curante", length=50)
	public String getDataConclusioniELetteraCurante() {
		return this.dataConclusioniELetteraCurante;
	}

	public void setDataConclusioniELetteraCurante(String dataConclusioniELetteraCurante) {
		this.dataConclusioniELetteraCurante = dataConclusioniELetteraCurante;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_invio_lettera_adesione_2004")
	public Date getDataInvioLetteraAdesione2004() {
		return this.dataInvioLetteraAdesione2004;
	}

	public void setDataInvioLetteraAdesione2004(Date dataInvioLetteraAdesione2004) {
		this.dataInvioLetteraAdesione2004 = dataInvioLetteraAdesione2004;
	}


	@Column(name="data_prelievo", length=50)
	public String getDataPrelievo() {
		return this.dataPrelievo;
	}

	public void setDataPrelievo(String dataPrelievo) {
		this.dataPrelievo = dataPrelievo;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_prima_visita")
	public Date getDataPrimaVisita() {
		return this.dataPrimaVisita;
	}

	public void setDataPrimaVisita(Date dataPrimaVisita) {
		this.dataPrimaVisita = dataPrimaVisita;
	}


	@Column(name="data_ricovero", length=50)
	public String getDataRicovero() {
		return this.dataRicovero;
	}

	public void setDataRicovero(String dataRicovero) {
		this.dataRicovero = dataRicovero;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_spirometria")
	public Date getDataSpirometria() {
		return this.dataSpirometria;
	}

	public void setDataSpirometria(Date dataSpirometria) {
		this.dataSpirometria = dataSpirometria;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_visita_chirurgica")
	public Date getDataVisitaChirurgica() {
		return this.dataVisitaChirurgica;
	}

	public void setDataVisitaChirurgica(Date dataVisitaChirurgica) {
		this.dataVisitaChirurgica = dataVisitaChirurgica;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_visita_pneumologica")
	public Date getDataVisitaPneumologica() {
		return this.dataVisitaPneumologica;
	}

	public void setDataVisitaPneumologica(Date dataVisitaPneumologica) {
		this.dataVisitaPneumologica = dataVisitaPneumologica;
	}


	@Lob
	@Column(name="dati_anamnestici_salienti")
	public String getDatiAnamnesticiSalienti() {
		return this.datiAnamnesticiSalienti;
	}

	public void setDatiAnamnesticiSalienti(String datiAnamnesticiSalienti) {
		this.datiAnamnesticiSalienti = datiAnamnesticiSalienti;
	}


	@Column(name="diagnosi_dimissione", length=250)
	public String getDiagnosiDimissione() {
		return this.diagnosiDimissione;
	}

	public void setDiagnosiDimissione(String diagnosiDimissione) {
		this.diagnosiDimissione = diagnosiDimissione;
	}


	@Column(name="diametro_1", length=50)
	public String getDiametro1() {
		return this.diametro1;
	}

	public void setDiametro1(String diametro1) {
		this.diametro1 = diametro1;
	}


	public Integer getDispnea() {
		return this.dispnea;
	}

	public void setDispnea(Integer dispnea) {
		this.dispnea = dispnea;
	}


	@Column(length=250)
	public String getDlco() {
		return this.dlco;
	}

	public void setDlco(String dlco) {
		this.dlco = dlco;
	}


	@Column(name="esame_obiettivo", length=250)
	public String getEsameObiettivo() {
		return this.esameObiettivo;
	}

	public void setEsameObiettivo(String esameObiettivo) {
		this.esameObiettivo = esameObiettivo;
	}


	public Integer getEscreato() {
		return this.escreato;
	}

	public void setEscreato(Integer escreato) {
		this.escreato = escreato;
	}


	@Column(name="esposizione_accidentale_a_rx_o_radionuclidi", length=50)
	public String getEsposizioneAccidentaleARxORadionuclidi() {
		return this.esposizioneAccidentaleARxORadionuclidi;
	}

	public void setEsposizioneAccidentaleARxORadionuclidi(String esposizioneAccidentaleARxORadionuclidi) {
		this.esposizioneAccidentaleARxORadionuclidi = esposizioneAccidentaleARxORadionuclidi;
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


	@Column(name="fattori_atmosferici")
	public Integer getFattoriAtmosferici() {
		return this.fattoriAtmosferici;
	}

	public void setFattoriAtmosferici(Integer fattoriAtmosferici) {
		this.fattoriAtmosferici = fattoriAtmosferici;
	}


	@Column(length=50)
	public String getFebbre() {
		return this.febbre;
	}

	public void setFebbre(String febbre) {
		this.febbre = febbre;
	}


	@Column(name="follow_up", length=50)
	public String getFollowUp() {
		return this.followUp;
	}

	public void setFollowUp(String followUp) {
		this.followUp = followUp;
	}


	@Column(name="`follow_up_si-no`")
	public Integer getFollowUpSi_no() {
		return this.followUpSi_no;
	}

	public void setFollowUpSi_no(Integer followUpSi_no) {
		this.followUpSi_no = followUpSi_no;
	}


	@Column(name="`folow-up_motivo`", length=50)
	public String getFolow_upMotivo() {
		return this.folow_upMotivo;
	}

	public void setFolow_upMotivo(String folow_upMotivo) {
		this.folow_upMotivo = folow_upMotivo;
	}


	@Column(name="fumo_attuale")
	public Integer getFumoAttuale() {
		return this.fumoAttuale;
	}

	public void setFumoAttuale(Integer fumoAttuale) {
		this.fumoAttuale = fumoAttuale;
	}

	@Id
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name="lesione_1", length=50)
	public String getLesione1() {
		return this.lesione1;
	}

	public void setLesione1(String lesione1) {
		this.lesione1 = lesione1;
	}


	@Column(name="malattia_professionale", length=255)
	public String getMalattiaProfessionale() {
		return this.malattiaProfessionale;
	}

	public void setMalattiaProfessionale(String malattiaProfessionale) {
		this.malattiaProfessionale = malattiaProfessionale;
	}


	@Column(name="malattie_infettive_ultimi_5_anni", length=250)
	public String getMalattieInfettiveUltimi5Anni() {
		return this.malattieInfettiveUltimi5Anni;
	}

	public void setMalattieInfettiveUltimi5Anni(String malattieInfettiveUltimi5Anni) {
		this.malattieInfettiveUltimi5Anni = malattieInfettiveUltimi5Anni;
	}


	@Column(name="malattie_professionali", length=50)
	public String getMalattieProfessionali() {
		return this.malattieProfessionali;
	}

	public void setMalattieProfessionali(String malattieProfessionali) {
		this.malattieProfessionali = malattieProfessionali;
	}


	public Integer getMesotelioma() {
		return this.mesotelioma;
	}

	public void setMesotelioma(Integer mesotelioma) {
		this.mesotelioma = mesotelioma;
	}


	@Column(name="motivo_radioterapia", length=50)
	public String getMotivoRadioterapia() {
		return this.motivoRadioterapia;
	}

	public void setMotivoRadioterapia(String motivoRadioterapia) {
		this.motivoRadioterapia = motivoRadioterapia;
	}


	@Column(name="n_1_anno_fa")
	public byte getN1AnnoFa() {
		return this.n1AnnoFa;
	}

	public void setN1AnnoFa(byte n1AnnoFa) {
		this.n1AnnoFa = n1AnnoFa;
	}


	@Column(name="n_2_anni_fa")
	public byte getN2AnniFa() {
		return this.n2AnniFa;
	}

	public void setN2AnniFa(byte n2AnniFa) {
		this.n2AnniFa = n2AnniFa;
	}


	@Column(name="n_3_anni_fa")
	public byte getN3AnniFa() {
		return this.n3AnniFa;
	}

	public void setN3AnniFa(byte n3AnniFa) {
		this.n3AnniFa = n3AnniFa;
	}


	@Column(name="n_4_anni_fa")
	public byte getN4AnniFa() {
		return this.n4AnniFa;
	}

	public void setN4AnniFa(byte n4AnniFa) {
		this.n4AnniFa = n4AnniFa;
	}


	@Column(name="n_5_anni_fa")
	public byte getN5AnniFa() {
		return this.n5AnniFa;
	}

	public void setN5AnniFa(byte n5AnniFa) {
		this.n5AnniFa = n5AnniFa;
	}


	@Column(name="n_noduli")
	public Integer getNNoduli() {
		return this.nNoduli;
	}

	public void setNNoduli(Integer nNoduli) {
		this.nNoduli = nNoduli;
	}


	@Column(name="n_radioterapia")
	public Integer getNRadioterapia() {
		return this.nRadioterapia;
	}

	public void setNRadioterapia(Integer nRadioterapia) {
		this.nRadioterapia = nRadioterapia;
	}


	@Column(name="n_rx")
	public Integer getNRx() {
		return this.nRx;
	}

	public void setNRx(Integer nRx) {
		this.nRx = nRx;
	}


	@Column(name="n_sigarette_giorno")
	public Integer getNSigaretteGiorno() {
		return this.nSigaretteGiorno;
	}

	public void setNSigaretteGiorno(Integer nSigaretteGiorno) {
		this.nSigaretteGiorno = nSigaretteGiorno;
	}


	public Integer getNodulo() {
		return this.nodulo;
	}

	public void setNodulo(Integer nodulo) {
		this.nodulo = nodulo;
	}


	@Column(name="numero_1", length=50)
	public String getNumero1() {
		return this.numero1;
	}

	public void setNumero1(String numero1) {
		this.numero1 = numero1;
	}


	@Column(name="pc_inail", length=255)
	public String getPcInail() {
		return this.pcInail;
	}

	public void setPcInail(String pcInail) {
		this.pcInail = pcInail;
	}


	@Column(name="periodo_radioterapia", length=50)
	public String getPeriodoRadioterapia() {
		return this.periodoRadioterapia;
	}

	public void setPeriodoRadioterapia(String periodoRadioterapia) {
		this.periodoRadioterapia = periodoRadioterapia;
	}


	@Column(length=50)
	public String getPeso() {
		return this.peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
	}


	@Column(length=255)
	public String getPet() {
		return this.pet;
	}

	public void setPet(String pet) {
		this.pet = pet;
	}


	public Integer getPlacche() {
		return this.placche;
	}

	public void setPlacche(Integer placche) {
		this.placche = placche;
	}


	@Column(name="prelievo_ematico", length=50)
	public String getPrelievoEmatico() {
		return this.prelievoEmatico;
	}

	public void setPrelievoEmatico(String prelievoEmatico) {
		this.prelievoEmatico = prelievoEmatico;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="prima_indagine_radiologica")
	public Date getPrimaIndagineRadiologica() {
		return this.primaIndagineRadiologica;
	}

	public void setPrimaIndagineRadiologica(Date primaIndagineRadiologica) {
		this.primaIndagineRadiologica = primaIndagineRadiologica;
	}


	public Integer getRadioterapia() {
		return this.radioterapia;
	}

	public void setRadioterapia(Integer radioterapia) {
		this.radioterapia = radioterapia;
	}


	@Column(name="rantolini_basilari")
	public Integer getRantoliniBasilari() {
		return this.rantoliniBasilari;
	}

	public void setRantoliniBasilari(Integer rantoliniBasilari) {
		this.rantoliniBasilari = rantoliniBasilari;
	}


	@Lob
	@Column(name="referto_1")
	public String getReferto1() {
		return this.referto1;
	}

	public void setReferto1(String referto1) {
		this.referto1 = referto1;
	}


	@Column(name="referto_biopsia", length=50)
	public String getRefertoBiopsia() {
		return this.refertoBiopsia;
	}

	public void setRefertoBiopsia(String refertoBiopsia) {
		this.refertoBiopsia = refertoBiopsia;
	}


	@Column(name="referto_pet", length=50)
	public String getRefertoPet() {
		return this.refertoPet;
	}

	public void setRefertoPet(String refertoPet) {
		this.refertoPet = refertoPet;
	}


	@Column(name="reparto_ricovero", length=50)
	public String getRepartoRicovero() {
		return this.repartoRicovero;
	}

	public void setRepartoRicovero(String repartoRicovero) {
		this.repartoRicovero = repartoRicovero;
	}


	@Column(name="rx_lavoro_scuola")
	public Integer getRxLavoroScuola() {
		return this.rxLavoroScuola;
	}

	public void setRxLavoroScuola(Integer rxLavoroScuola) {
		this.rxLavoroScuola = rxLavoroScuola;
	}


	@Column(name="sede_radioterapia", length=50)
	public String getSedeRadioterapia() {
		return this.sedeRadioterapia;
	}

	public void setSedeRadioterapia(String sedeRadioterapia) {
		this.sedeRadioterapia = sedeRadioterapia;
	}


	@Column(length=50)
	public String getSpirometria() {
		return this.spirometria;
	}

	public void setSpirometria(String spirometria) {
		this.spirometria = spirometria;
	}


	public Integer getTbc() {
		return this.tbc;
	}

	public void setTbc(Integer tbc) {
		this.tbc = tbc;
	}


	public Integer getTosse() {
		return this.tosse;
	}

	public void setTosse(Integer tosse) {
		this.tosse = tosse;
	}


	@Column(name="tumore_polmonare")
	public Integer getTumorePolmonare() {
		return this.tumorePolmonare;
	}

	public void setTumorePolmonare(Integer tumorePolmonare) {
		this.tumorePolmonare = tumorePolmonare;
	}


	@Column(name="vaccinazione_tbc", length=50)
	public String getVaccinazioneTbc() {
		return this.vaccinazioneTbc;
	}

	public void setVaccinazioneTbc(String vaccinazioneTbc) {
		this.vaccinazioneTbc = vaccinazioneTbc;
	}


	@Column(name="`vems-cvpc`")
	public Integer getVems_cvpc() {
		return this.vems_cvpc;
	}

	public void setVems_cvpc(Integer vems_cvpc) {
		this.vems_cvpc = vems_cvpc;
	}


	public Integer getVemspc() {
		return this.vemspc;
	}

	public void setVemspc(Integer vemspc) {
		this.vemspc = vemspc;
	}


	@Column(length=250)
	public String getVisita() {
		return this.visita;
	}

	public void setVisita(String visita) {
		this.visita = visita;
	}


	@Column(name="visita_chirurgica", length=250)
	public String getVisitaChirurgica() {
		return this.visitaChirurgica;
	}

	public void setVisitaChirurgica(String visitaChirurgica) {
		this.visitaChirurgica = visitaChirurgica;
	}


	@Column(name="visita_pneumologica", length=250)
	public String getVisitaPneumologica() {
		return this.visitaPneumologica;
	}

	public void setVisitaPneumologica(String visitaPneumologica) {
		this.visitaPneumologica = visitaPneumologica;
	}

}