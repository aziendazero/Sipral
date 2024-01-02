package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class VisitaGenerica implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer asbestosi;
	private Integer bilaterali;
	private String biopsia;
	private Double codice;
	private String conclusioni;
	private Integer cvpc;
	private String dataAggiornamentoPrimo;
	private String dataAggiornamentoSecondo;
	private String dataAggiornamentoTerzo;
	private String dataAltraVisita;
	private String dataConclusioniELetteraCurante;
	private Date dataInvioLetteraAdesione2004;
	private String dataPrelievo;
	private String dataRicovero;
	private Date dataVisita;
	private Date dataSpirometria;
	private String dataVisitaChirurgica;
	private String dataVisitaPneumologica;
	private String datiAnamnesticiSalienti;
	private String diagnosiDimissione;
	private String diametro;
	private Integer dispnea;
	private String esameObiettivo;
	private Integer escreato;
	private Integer fattoriAtmosferici;
	private String followUp;
	private Integer followUpSi_no;
	private String folow_upMotivo;
	private Integer fumoAttuale;
	private Integer id;
	private String lesione;
	private String malattiaProfessionale;
	private Integer mesotelioma;
	private Integer nNoduli;
	private Integer nodulo;
	private String numero;
	private String pcInail;
	private String pet;
	private Integer placche;
	private String prelievoEmatico;
	private Integer rantoliniBasilari;
	private String referto;
	private String refertoBiopsia;
	private String refertoPet;
	private String repartoRicovero;
	private Date indagineRadiologica;
	private String spirometria;
	private Integer tosse;
	private Integer tumorePolmonare;
	private Integer vems_cvpc;
	private Integer vemspc;
	private String visita;
	private String visitaChirurgica;
	private String visitaPneumologica;
	protected String evoluzione;
	protected String visitaMedica;

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


	@Column(name="data_altra_visita", length=50)
	public String getDataAltraVisita() {
		return this.dataAltraVisita;
	}

	public void setDataAltraVisita(String dataAltraVisita) {
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


	@Column(name="data_ricovero", length=50)
	public String getDataRicovero() {
		return this.dataRicovero;
	}

	public void setDataRicovero(String dataRicovero) {
		this.dataRicovero = dataRicovero;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_prima_visita")
	public Date getDataVisita() {
		return this.dataVisita;
	}

	public void setDataVisita(Date dataSecondaVisita) {
		this.dataVisita = dataSecondaVisita;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_spirometria")
	public Date getDataSpirometria() {
		return this.dataSpirometria;
	}

	public void setDataSpirometria(Date dataSpirometria) {
		this.dataSpirometria = dataSpirometria;
	}


	@Column(name="data_visita_chirurgica", length=50)
	public String getDataVisitaChirurgica() {
		return this.dataVisitaChirurgica;
	}

	public void setDataVisitaChirurgica(String dataVisitaChirurgica) {
		this.dataVisitaChirurgica = dataVisitaChirurgica;
	}


	@Column(name="data_visita_pneumologica", length=50)
	public String getDataVisitaPneumologica() {
		return this.dataVisitaPneumologica;
	}

	public void setDataVisitaPneumologica(String dataVisitaPneumologica) {
		this.dataVisitaPneumologica = dataVisitaPneumologica;
	}


	@Column(name="dati_anamnestici_salienti", length=250)
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


	@Column(name="diametro", length=50)
	public String getDiametro() {
		return this.diametro;
	}

	public void setDiametro(String diametro2) {
		this.diametro = diametro2;
	}


	public Integer getDispnea() {
		return this.dispnea;
	}

	public void setDispnea(Integer dispnea) {
		this.dispnea = dispnea;
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

	@Column(name="fattori_atmosferici")
	public Integer getFattoriAtmosferici() {
		return this.fattoriAtmosferici;
	}

	public void setFattoriAtmosferici(Integer fattoriAtmosferici) {
		this.fattoriAtmosferici = fattoriAtmosferici;
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


	@Column(name="`follow-up_motivo`", length=50)
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


	@Column(name="lesione", length=50)
	public String getLesione() {
		return this.lesione;
	}

	public void setLesione(String lesione2) {
		this.lesione = lesione2;
	}


	@Column(name="malattia_professionale", length=255)
	public String getMalattiaProfessionale() {
		return this.malattiaProfessionale;
	}

	public void setMalattiaProfessionale(String malattiaProfessionale) {
		this.malattiaProfessionale = malattiaProfessionale;
	}


	public Integer getMesotelioma() {
		return this.mesotelioma;
	}

	public void setMesotelioma(Integer mesotelioma) {
		this.mesotelioma = mesotelioma;
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


	@Column(name="numero", length=50)
	public String getNumero() {
		return this.numero;
	}

	public void setNumero(String numero2) {
		this.numero = numero2;
	}


	@Column(name="pc_inail", length=255)
	public String getPcInail() {
		return this.pcInail;
	}

	public void setPcInail(String pcInail) {
		this.pcInail = pcInail;
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


	@Column(name="rantolini_basilari")
	public Integer getRantoliniBasilari() {
		return this.rantoliniBasilari;
	}

	public void setRantoliniBasilari(Integer rantoliniBasilari) {
		this.rantoliniBasilari = rantoliniBasilari;
	}


	@Lob
	@Column(name="referto")
	public String getReferto() {
		return this.referto;
	}

	public void setReferto(String referto2) {
		this.referto = referto2;
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


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="prima_indagine_radiologica")
	public Date getIndagineRadiologica() {
		return this.indagineRadiologica;
	}

	public void setIndagineRadiologica(Date indagineRadiologica) {
		this.indagineRadiologica = indagineRadiologica;
	}

	@Column(length=50)
	public String getSpirometria() {
		return this.spirometria;
	}

	public void setSpirometria(String spirometria) {
		this.spirometria = spirometria;
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
	
	@Transient
	public abstract String getEvoluzione();

	@Transient
	public abstract void setEvoluzione(String evoluzione2);
	
	@Transient
	public abstract String getVisitaMedica();

	@Transient
	public abstract void setVisitaMedica(String secondaVisita);
}