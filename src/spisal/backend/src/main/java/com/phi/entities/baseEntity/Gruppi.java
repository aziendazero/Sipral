package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AssociationOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.envers.Audited;

import com.phi.entities.actions.GruppiAction;
import com.phi.entities.actions.ProvvedimentiAction;
import com.phi.entities.dataTypes.CodeValuePhi;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import javax.persistence.JoinColumn;
import com.phi.entities.dataTypes.AD;
import javax.persistence.Embedded;
import javax.persistence.AttributeOverrides;
import javax.persistence.AttributeOverride;

@javax.persistence.Entity
@Table(name = "gruppi")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Gruppi extends BaseEntity {

	private static final long serialVersionUID = 627274223L;

	/**
	*  javadoc for dataArchiviazioneSede
	*/
	private Date dataArchiviazioneSede;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_archiv_sede")
	public Date getDataArchiviazioneSede(){
		return dataArchiviazioneSede;
	}

	public void setDataArchiviazioneSede(Date dataArchiviazioneSede){
		this.dataArchiviazioneSede = dataArchiviazioneSede;
	}

	/**
	*  javadoc for pagNonEffettuatoNP
	*/
	private Boolean pagNonEffettuatoNP;

	@Column(name="pag_non_effettuato_np")
	public Boolean getPagNonEffettuatoNP(){
		return pagNonEffettuatoNP;
	}

	public void setPagNonEffettuatoNP(Boolean pagNonEffettuatoNP){
		this.pagNonEffettuatoNP = pagNonEffettuatoNP;
	}


	/**
	*  javadoc for scadOttModiDiversiPM
	*/
	private Date scadOttModiDiversiPM;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scad_ott_modi_diversi_pm")
	public Date getScadOttModiDiversiPM(){
		return scadOttModiDiversiPM;
	}

	public void setScadOttModiDiversiPM(Date scadOttModiDiversiPM){
		this.scadOttModiDiversiPM = scadOttModiDiversiPM;
	}

	/**
	*  javadoc for scadOttModiDiversi
	*/
	private Date scadOttModiDiversi;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scad_ott_modi_diversi")
	public Date getScadOttModiDiversi(){
		return scadOttModiDiversi;
	}

	public void setScadOttModiDiversi(Date scadOttModiDiversi){
		this.scadOttModiDiversi = scadOttModiDiversi;
	}

	/**
	*  javadoc for scadenzaComPM
	*/
	private Date scadenzaComPM;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scadenza_com_pm")
	public Date getScadenzaComPM(){
		return scadenzaComPM;
	}

	public void setScadenzaComPM(Date scadenzaComPM){
		this.scadenzaComPM = scadenzaComPM;
	}

	/**
	*  javadoc for dataScadenzaPagamento
	*/
	private Date dataScadenzaPagamento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_scadenza_pagamento")
	public Date getDataScadenzaPagamento(){
		return dataScadenzaPagamento;
	}

	public void setDataScadenzaPagamento(Date dataScadenzaPagamento){
		this.dataScadenzaPagamento = dataScadenzaPagamento;
	}

	/**
	*  javadoc for scadComInottemperanzaPM
	*/
	private Date scadComInottemperanzaPM;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scad_com_inottemperanza_pm")
	public Date getScadComInottemperanzaPM(){
		return scadComInottemperanzaPM;
	}

	public void setScadComInottemperanzaPM(Date scadComInottemperanzaPM){
		this.scadComInottemperanzaPM = scadComInottemperanzaPM;
	}

	/**
	*  javadoc for scadComInottemperanza
	*/
	private Date scadComInottemperanza;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scad_com_inottemperanza")
	public Date getScadComInottemperanza(){
		return scadComInottemperanza;
	}

	public void setScadComInottemperanza(Date scadComInottemperanza){
		this.scadComInottemperanza = scadComInottemperanza;
	}

	/**
	*  javadoc for dataScadenzaComPMNP
	*/
	private Date dataScadenzaComPMNP;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_scadenza_com_pmnp")
	public Date getDataScadenzaComPMNP(){
		return dataScadenzaComPMNP;
	}

	public void setDataScadenzaComPMNP(Date dataScadenzaComPMNP){
		this.dataScadenzaComPMNP = dataScadenzaComPMNP;
	}

	/**
	*  javadoc for dataScadenzaPagamentoNP
	*/
	private Date dataScadenzaPagamentoNP;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_scadenza_pagamento_np")
	public Date getDataScadenzaPagamentoNP(){
		return dataScadenzaPagamentoNP;
	}

	public void setDataScadenzaPagamentoNP(Date dataScadenzaPagamentoNP){
		this.dataScadenzaPagamentoNP = dataScadenzaPagamentoNP;
	}

	/**
	*  javadoc for scadenzaDellaVerifica
	*/
	private Date scadenzaDellaVerifica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scadenza_della_verifica")
	public Date getScadenzaDellaVerifica(){
		return scadenzaDellaVerifica;
	}

	public void setScadenzaDellaVerifica(Date scadenzaDellaVerifica){
		this.scadenzaDellaVerifica = scadenzaDellaVerifica;
	}

	/**
	*  javadoc for importoInottemperanza
	*/
	private Double importoInottemperanza;

	@Column(name="importo_inottemperanza")
	public Double getImportoInottemperanza(){
		return importoInottemperanza;
	}

	public void setImportoInottemperanza(Double importoInottemperanza){
		this.importoInottemperanza = importoInottemperanza;
	}

	/**
	*  javadoc for comune
	*/
	private AD comune;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="comune_code"))
	@AttributeOverrides({
	@AttributeOverride(name="adl", column=@Column(name="comune_adl")),
	@AttributeOverride(name="bnr", column=@Column(name="comune_bnr")),
	@AttributeOverride(name="cen", column=@Column(name="comune_cen")),
	@AttributeOverride(name="cnt", column=@Column(name="comune_cnt")),
	@AttributeOverride(name="cpa", column=@Column(name="comune_cpa")),
	@AttributeOverride(name="cty", column=@Column(name="comune_cty")),
	@AttributeOverride(name="sta", column=@Column(name="comune_sta")),
	@AttributeOverride(name="stb", column=@Column(name="comune_stb")),
	@AttributeOverride(name="str", column=@Column(name="comune_str")),
	@AttributeOverride(name="zip", column=@Column(name="comune_zip"))
	})
	public AD getComune(){
		return comune;
	}

	public void setComune(AD comune){
		this.comune = comune;
	}

	/**
	*  javadoc for dataTrasmissione
	*/
	private Date dataTrasmissione;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_trasmissione")
	public Date getDataTrasmissione(){
		return dataTrasmissione;
	}

	public void setDataTrasmissione(Date dataTrasmissione){
		this.dataTrasmissione = dataTrasmissione;
	}

	/**
	*  javadoc for pagamentoNonEffettuato
	*/
	private Boolean pagamentoNonEffettuato;

	@Column(name="pagamento_non_effettuato")
	public Boolean getPagamentoNonEffettuato(){
		return pagamentoNonEffettuato;
	}

	public void setPagamentoNonEffettuato(Boolean pagamentoNonEffettuato){
		this.pagamentoNonEffettuato = pagamentoNonEffettuato;
	}

	/**
	*  javadoc for comunicazioneComune
	*/
	private Date comunicazioneComune;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="comunicazione_comune")
	public Date getComunicazioneComune(){
		return comunicazioneComune;
	}

	public void setComunicazioneComune(Date comunicazioneComune){
		this.comunicazioneComune = comunicazioneComune;
	}

	/**
	*  javadoc for scadComunicazioneComune
	*/
	private Date scadComunicazioneComune;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="scad_comunicazione_comune")
	public Date getScadComunicazioneComune(){
		return scadComunicazioneComune;
	}

	public void setScadComunicazioneComune(Date scadComunicazioneComune){
		this.scadComunicazioneComune = scadComunicazioneComune;
	}

	/**
	*  javadoc for dataEsitoRicorso
	*/
	private Date dataEsitoRicorso;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_esito_ricorso")
	public Date getDataEsitoRicorso(){
		return dataEsitoRicorso;
	}

	public void setDataEsitoRicorso(Date dataEsitoRicorso){
		this.dataEsitoRicorso = dataEsitoRicorso;
	}

	/**
	*  javadoc for dataRicorso
	*/
	private Date dataRicorso;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_ricorso")
	public Date getDataRicorso(){
		return dataRicorso;
	}

	public void setDataRicorso(Date dataRicorso){
		this.dataRicorso = dataRicorso;
	}

	/**
	*  javadoc for ricorsoAccolto
	*/
	private CodeValuePhi ricorsoAccolto;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ricorsoAccolto")
	@ForeignKey(name="FK_Gruppi_ricorsoAccolto")
	//@Index(name="IX_Gruppi_ricorsoAccolto")
	public CodeValuePhi getRicorsoAccolto(){
		return ricorsoAccolto;
	}

	public void setRicorsoAccolto(CodeValuePhi ricorsoAccolto){
		this.ricorsoAccolto = ricorsoAccolto;
	}

	/**
	*  javadoc for ricorso
	*/
	private Boolean ricorso;

	@Column(name="ricorso")
	public Boolean getRicorso(){
		return ricorso;
	}

	public void setRicorso(Boolean ricorso){
		this.ricorso = ricorso;
	}
	
	/**
	*  javadoc for comunicazioneInottemperanza
	*/
	private Date comunicazionePM;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="comunicazione_pm")
	public Date getComunicazionePM(){
		return comunicazionePM;
	}

	public void setComunicazionePM(Date comunicazionePM){
		this.comunicazionePM = comunicazionePM;
	}
	
	/**
	*  javadoc for comunicazioneInottemperanza
	*/
	private Date comunicazionePMNP;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="comunicazione_pmnp")
	public Date getComunicazionePMNP(){
		return comunicazionePMNP;
	}

	public void setComunicazionePMNP(Date comunicazionePMNP){
		this.comunicazionePMNP = comunicazionePMNP;
	}
	
	/**
	*  javadoc for comunicazioneInottemperanza
	*/
	private Date comunicazioneInottemperanzaPM;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="comunicazione_inpm")
	public Date getComunicazioneInottemperanzaPM(){
		return comunicazioneInottemperanzaPM;
	}

	public void setComunicazioneInottemperanzaPM(Date comunicazioneInottemperanzaPM){
		this.comunicazioneInottemperanzaPM = comunicazioneInottemperanzaPM;
	}
	
	/**
	*  javadoc for comunicazioneInottemperanza
	*/
	private Date comunicazioneInottemperanza;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="comunicazione_inott")
	public Date getComunicazioneInottemperanza(){
		return comunicazioneInottemperanza;
	}

	public void setComunicazioneInottemperanza(Date comunicazioneInottemperanza){
		this.comunicazioneInottemperanza = comunicazioneInottemperanza;
	}
	
	/**
	*  javadoc for comunicazioneOttModiDiversi
	*/
	private Date ottemperanzaModiDiversi;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ottemperanza_diversi")
	public Date getOttemperanzaModiDiversi(){
		return ottemperanzaModiDiversi;
	}

	public void setOttemperanzaModiDiversi(Date ottemperanzaModiDiversi){
		this.ottemperanzaModiDiversi = ottemperanzaModiDiversi;
	}
	
	/**
	*  javadoc for comunicazioneOttModiDiversi
	*/
	private Date ottemperanzaModiDiversiPM;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ottemperanza_diversi_pm")
	public Date getOttemperanzaModiDiversiPM(){
		return ottemperanzaModiDiversiPM;
	}

	public void setOttemperanzaModiDiversiPM(Date ottemperanzaModiDiversiPM){
		this.ottemperanzaModiDiversiPM = ottemperanzaModiDiversiPM;
	}
	
	/**
	*  javadoc for dettagli
	*/
	private String dettagli;

	@Column(name="dettagli", length=4000)
	public String getDettagli(){
		return dettagli;
	}

	public void setDettagli(String dettagli){
		this.dettagli = dettagli;
	}
	
	/**
	*  javadoc for notificaPagamento
	*/
	private Date notificaPagamento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="notifica_pagamento")
	public Date getNotificaPagamento(){
		return notificaPagamento;
	}

	public void setNotificaPagamento(Date notificaPagamento){
		this.notificaPagamento = notificaPagamento;
	}
	
	/**
	*  javadoc for notificaPagamento
	*/
	private Date notificaPagamentoNP;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="notifica_pagamento_np")
	public Date getNotificaPagamentoNP(){
		return notificaPagamentoNP;
	}

	public void setNotificaPagamentoNP(Date notificaPagamentoNP){
		this.notificaPagamentoNP = notificaPagamentoNP;
	}

	/**
	*  javadoc for ammissionePagamento
	*/
	private Date ammissionePagamento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ammissione_pagamento")
	public Date getAmmissionePagamento(){
		return ammissionePagamento;
	}

	public void setAmmissionePagamento(Date ammissionePagamento){
		this.ammissionePagamento = ammissionePagamento;
	}
	
	/**
	*  javadoc for ammissionePagamento
	*/
	private Date ammissionePagamentoNP;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ammissione_pagamento_np")
	public Date getAmmissionePagamentoNP(){
		return ammissionePagamentoNP;
	}

	public void setAmmissionePagamentoNP(Date ammissionePagamentoNP){
		this.ammissionePagamentoNP = ammissionePagamentoNP;
	}
	
	/**
	*  javadoc for dataPagamento
	*/
	private Date dataPagamento;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_pagamento")
	public Date getDataPagamento(){
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento){
		this.dataPagamento = dataPagamento;
	}
	
	
	/**
	*  javadoc for dataPagamento
	*/
	private Date dataPagamentoNP;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_pagamento_np")
	public Date getDataPagamentoNP(){
		return dataPagamentoNP;
	}

	public void setDataPagamentoNP(Date dataPagamentoNP){
		this.dataPagamentoNP = dataPagamentoNP;
	}


	/**
	*  javadoc for speseNotifica
	*/
	private Double importoVersato;

	@Column(name="importo_versato")
	public Double getImportoVersato(){
		return importoVersato;
	}

	public void setImportoVersato(Double importoVersato){
		this.importoVersato = importoVersato;
	}
	
	/**
	*  javadoc for speseNotifica
	*/
	private Double importoVersatoNP;

	@Column(name="importo_versato_np")
	public Double getImportoVersatoNP(){
		return importoVersatoNP;
	}

	public void setImportoVersatoNP(Double importoVersatoNP){
		this.importoVersatoNP = importoVersatoNP;
	}
	
	/**
	*  javadoc for speseNotifica
	*/
	private Double speseNotifica;

	@Column(name="spese_notifica")
	public Double getSpeseNotifica(){
		return speseNotifica;
	}

	public void setSpeseNotifica(Double speseNotifica){
		this.speseNotifica = speseNotifica;
	}
	
	/**
	*  javadoc for speseNotifica
	*/
	private Double speseNotificaNP;

	@Column(name="spese_notifica_np")
	public Double getSpeseNotificaNP(){
		return speseNotificaNP;
	}

	public void setSpeseNotificaNP(Double speseNotificaNP){
		this.speseNotificaNP = speseNotificaNP;
	}
	
	/**
	*  javadoc for dataDellaVerifica
	*/
	private Date dataDellaVerifica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_della_verifica")
	public Date getDataDellaVerifica(){
		return dataDellaVerifica;
	}

	public void setDataDellaVerifica(Date dataDellaVerifica){
		this.dataDellaVerifica = dataDellaVerifica;
	}

	/**
	*  javadoc for secondaProroga
	*/
	private Integer secondaProroga;

	@Column(name="seconda_proroga")
	public Integer getSecondaProroga(){
		return secondaProroga;
	}

	public void setSecondaProroga(Integer secondaProroga){
		this.secondaProroga = secondaProroga;
	}

	/**
	*  javadoc for primaProroga
	*/
	private Integer primaProroga;

	@Column(name="prima_proroga")
	public Integer getPrimaProroga(){
		return primaProroga;
	}

	public void setPrimaProroga(Integer primaProroga){
		this.primaProroga = primaProroga;
	}

	/**
	*  javadoc for giorniIniziali
	*/
	private Integer giorniIniziali;

	@Column(name="giorni_iniziali")
	public Integer getGiorniIniziali(){
		return giorniIniziali;
	}

	public void setGiorniIniziali(Integer giorniIniziali){
		this.giorniIniziali = giorniIniziali;
	}

	/**
	*  javadoc for dataNotifica
	*/
	private Date dataNotifica;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_notifica")
	public Date getDataNotifica(){
		return dataNotifica;
	}

	public void setDataNotifica(Date dataNotifica){
		this.dataNotifica = dataNotifica;
	}

	/**
	*  javadoc for name
	*/
	private String name;

	@Column(name="name")
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}


	/**
	*  javadoc for articoli
	*/
	private List<Articoli> articoli;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="gruppo", cascade=CascadeType.PERSIST)
	public List<Articoli> getArticoli(){
		return articoli;
	}

	public void setArticoli(List<Articoli> list){
		articoli = list;
	}

	public void addArticoli(Articoli articoli) {
		if (this.articoli == null) {
			this.articoli = new ArrayList<Articoli>();
		}
		// add the association
		if(!this.articoli.contains(articoli)) {
			this.articoli.add(articoli);
			// make the inverse link
			articoli.setGruppo(this);
		}
	}

	public void removeArticoli(Articoli articoli) {
		if (this.articoli == null) {
			this.articoli = new ArrayList<Articoli>();
			return;
		}
		//add the association
		if(this.articoli.contains(articoli)){
			this.articoli.remove(articoli);
			//make the inverse link
			articoli.setGruppo(null);
		}

	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Gruppi_sequence")
	@SequenceGenerator(name = "Gruppi_sequence", sequenceName = "Gruppi_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	@Transient
	public Date getScadenzaPerOttemperareWrapped(){
		Date res = null;
		GruppiAction ga = GruppiAction.instance();
		res = ga.scadenzaPerOttemperare(giorniIniziali, primaProroga, secondaProroga, ProvvedimentiAction.instance().getEntity().getDataNotifica());
		return res;
	}

}
