package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the PROCPRATICHE database table.
 * 
 */
@Entity
@Table(name="PROCPRATICHE")
public class Pratiche implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idprocpratiche;
	private BigDecimal anno;
	private String completa;
	private Date data;
	private Date datacompleta;
	private Date datainizioprocedimento;
	private BigDecimal idanagrafica;
	private Ditte ditta;
	private BigDecimal idprocedura;
	private BigDecimal idprovenienza;
	private BigDecimal idufficio;
	private BigDecimal idverificaimp;
	private String nascondi;
	private String note;
	private BigDecimal numero;
	private String numerocell;
	private String nuova;
	private String riferimento;
	private BigDecimal riservatezzapratica;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private BigDecimal tiposoggetto;
	private FascicoliPrevnet fascicolo;
	private List<Altridatipratica> altridatipraticas;
	private List<Assegnazionepratiche> assegnazionepratiches;
	private List<Eventi> eventis;
	private List<Malattieprofessionali> malattieprofessionalis;
	private List<Interventiformazione> interventiformaziones;
	private Tabelle posizioneAttuale;
	private Tabelle tipoPratica;
	private Tabelle ambito;
	private Tabelle statoPratica;
	private Tabelle progetto;
	private Tabelle asl;
	private List<Sopralluoghidip> sopralluoghidips;
	private List<Riunionioperative> riunionioperatives;
	private List<InfortuniPrevnet> infortuniPrevnets;
	private List<Cantieri> cantieris;
	private List<Infortuniindagati> infortuniindagatis;
	private List<Parerigenerici> parerigenericis;
	private List<Parerigen> parerigens;
	private List<Altridatinip> altridatinips;
	private List<Richiestaregistrazione> richiestaregistraziones;
	private List<Ricorsigiudizimedici> ricorsigiudizimedicis;
	private List<Scadenze> scadenzes;
	private List<Sanzioni> sanzionis;
	private List<ProvvedimentiPrevnet> provvedimentis;
	private Operatori operPosAttuale;
	private Operatori operCompleta;
	private Operatori veroCreatore;
	private List<Lavoratricimadre> lavoratricimadres;
	private List<Tblblob> tblblobs;
	private List<Documentiscan> documentiscans;
	private List<Visitespecialistiche> visitespecialistiches;
	private List<Misureambientali> misureambientalis;

	private List<Autorizzazionideroga> autorizzazioniderogas;
	private List<Atti> attis;

	public Pratiche() {
	}


	@Id
	@Column(unique=true, nullable=false, precision=22)
	public long getIdprocpratiche() {
		return this.idprocpratiche;
	}

	public void setIdprocpratiche(long idprocpratiche) {
		this.idprocpratiche = idprocpratiche;
	}


	@Column(precision=16, scale=6)
	public BigDecimal getAnno() {
		return this.anno;
	}

	public void setAnno(BigDecimal anno) {
		this.anno = anno;
	}


	@Column(nullable=false, length=1)
	public String getCompleta() {
		return this.completa;
	}

	public void setCompleta(String completa) {
		this.completa = completa;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"")
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacompleta() {
		return this.datacompleta;
	}

	public void setDatacompleta(Date datacompleta) {
		this.datacompleta = datacompleta;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizioprocedimento() {
		return this.datainizioprocedimento;
	}

	public void setDatainizioprocedimento(Date datainizioprocedimento) {
		this.datainizioprocedimento = datainizioprocedimento;
	}


	@Column(precision=22)
	public BigDecimal getIdanagrafica() {
		return this.idanagrafica;
	}

	public void setIdanagrafica(BigDecimal idanagrafica) {
		this.idanagrafica = idanagrafica;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Ditte getDitta() {
		return this.ditta;
	}

	public void setDitta(Ditte ditta) {
		this.ditta = ditta;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDFASCICOLO")
	public FascicoliPrevnet getFascicolo() {
		return this.fascicolo;
	}

	public void setFascicolo(FascicoliPrevnet fascicolo) {
		this.fascicolo = fascicolo;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERPOSATTUALE")
	public Operatori getOperPosAttuale() {
		return this.operPosAttuale;
	}

	public void setOperPosAttuale(Operatori operatori1) {
		this.operPosAttuale = operatori1;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERCOMPLETA")
	public Operatori getOperCompleta() {
		return this.operCompleta;
	}

	public void setOperCompleta(Operatori operatori2) {
		this.operCompleta = operatori2;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDVEROCREATORE")
	public Operatori getVeroCreatore() {
		return this.veroCreatore;
	}

	public void setVeroCreatore(Operatori operatori3) {
		this.veroCreatore = operatori3;
	}

	@Column(precision=22)
	public BigDecimal getIdprocedura() {
		return this.idprocedura;
	}

	public void setIdprocedura(BigDecimal idprocedura) {
		this.idprocedura = idprocedura;
	}


	@Column(precision=22)
	public BigDecimal getIdprovenienza() {
		return this.idprovenienza;
	}

	public void setIdprovenienza(BigDecimal idprovenienza) {
		this.idprovenienza = idprovenienza;
	}


	@Column(precision=22)
	public BigDecimal getIdufficio() {
		return this.idufficio;
	}

	public void setIdufficio(BigDecimal idufficio) {
		this.idufficio = idufficio;
	}


	@Column(precision=22)
	public BigDecimal getIdverificaimp() {
		return this.idverificaimp;
	}

	public void setIdverificaimp(BigDecimal idverificaimp) {
		this.idverificaimp = idverificaimp;
	}


	@Column(nullable=false, length=1)
	public String getNascondi() {
		return this.nascondi;
	}

	public void setNascondi(String nascondi) {
		this.nascondi = nascondi;
	}


	@Column(length=4000)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	@Column(precision=22)
	public BigDecimal getNumero() {
		return this.numero;
	}

	public void setNumero(BigDecimal numero) {
		this.numero = numero;
	}


	@Column(length=20)
	public String getNumerocell() {
		return this.numerocell;
	}

	public void setNumerocell(String numerocell) {
		this.numerocell = numerocell;
	}


	@Column(nullable=false, length=1)
	public String getNuova() {
		return this.nuova;
	}

	public void setNuova(String nuova) {
		this.nuova = nuova;
	}


	@Column(length=1000)
	public String getRiferimento() {
		return this.riferimento;
	}

	public void setRiferimento(String riferimento) {
		this.riferimento = riferimento;
	}


	@Column(nullable=false, precision=22)
	public BigDecimal getRiservatezzapratica() {
		return this.riservatezzapratica;
	}

	public void setRiservatezzapratica(BigDecimal riservatezzapratica) {
		this.riservatezzapratica = riservatezzapratica;
	}


	@Temporal(TemporalType.DATE)
	public Date getSynchdate() {
		return this.synchdate;
	}

	public void setSynchdate(Date synchdate) {
		this.synchdate = synchdate;
	}


	@Column(nullable=false, length=1)
	public String getSynchflag() {
		return this.synchflag;
	}

	public void setSynchflag(String synchflag) {
		this.synchflag = synchflag;
	}


	@Column(length=16)
	public String getSynchid() {
		return this.synchid;
	}

	public void setSynchid(String synchid) {
		this.synchid = synchid;
	}


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	@Column(precision=16, scale=6)
	public BigDecimal getTiposoggetto() {
		return this.tiposoggetto;
	}

	public void setTiposoggetto(BigDecimal tiposoggetto) {
		this.tiposoggetto = tiposoggetto;
	}


	//bi-directional many-to-one association to Altridatipratica
	@OneToMany(mappedBy="pratica")
	public List<Altridatipratica> getAltridatipraticas() {
		return this.altridatipraticas;
	}

	public void setAltridatipraticas(List<Altridatipratica> altridatipraticas) {
		this.altridatipraticas = altridatipraticas;
	}

	public Altridatipratica addAltridatipratica(Altridatipratica altridatipratica) {
		getAltridatipraticas().add(altridatipratica);
		altridatipratica.setPratica(this);

		return altridatipratica;
	}

	public Altridatipratica removeAltridatipratica(Altridatipratica altridatipratica) {
		getAltridatipraticas().remove(altridatipratica);
		altridatipratica.setPratica(null);

		return altridatipratica;
	}

	//bi-directional many-to-one association to Eventipratica
	@OneToMany(mappedBy="pratiche")
	public List<Assegnazionepratiche> getAssegnazionepratiches() {
		return this.assegnazionepratiches;
	}

	public void setAssegnazionepratiches(List<Assegnazionepratiche> assegnazionepratiche) {
		this.assegnazionepratiches = assegnazionepratiche;
	}

	public Assegnazionepratiche addEventipratica(Assegnazionepratiche assegnazionepratiche) {
		getAssegnazionepratiches().add(assegnazionepratiche);
		assegnazionepratiche.setPratiche(this);

		return assegnazionepratiche;
	}

	public Assegnazionepratiche removeEventipratica(Assegnazionepratiche assegnazionepratiche) {
		getAssegnazionepratiches().remove(assegnazionepratiche);
		assegnazionepratiche.setPratiche(null);

		return assegnazionepratiche;
	}

	//bi-directional many-to-one association to Eventipratica
	@OneToMany(mappedBy="pratiche")
	public List<Eventi> getEventis() {
		return this.eventis;
	}

	public void setEventis(List<Eventi> eventis) {
		this.eventis = eventis;
	}

	public Eventi addEventi(Eventi eventi) {
		getEventis().add(eventi);
		eventi.setPratiche(this);

		return eventi;
	}

	public Eventi removeEventi(Eventi eventi) {
		getEventis().remove(eventi);
		eventi.setPratiche(null);

		return eventi;
	}


	//bi-directional many-to-one association to Malattieprofessionali
	@OneToMany(mappedBy="pratiche")
	public List<Malattieprofessionali> getMalattieprofessionalis() {
		return this.malattieprofessionalis;
	}

	public void setMalattieprofessionalis(List<Malattieprofessionali> malattieprofessionalis) {
		this.malattieprofessionalis = malattieprofessionalis;
	}

	public Malattieprofessionali addMalattieprofessionali(Malattieprofessionali malattieprofessionali) {
		getMalattieprofessionalis().add(malattieprofessionali);
		malattieprofessionali.setPratiche(this);

		return malattieprofessionali;
	}

	public Malattieprofessionali removeMalattieprofessionali(Malattieprofessionali malattieprofessionali) {
		getMalattieprofessionalis().remove(malattieprofessionali);
		malattieprofessionali.setPratiche(null);

		return malattieprofessionali;
	}

	//bi-directional many-to-one association to InterventiFormaziones
	@OneToMany(mappedBy="pratica")
	public List<Interventiformazione> getInterventiformaziones() {
		return this.interventiformaziones;
	}

	public void setInterventiformaziones(List<Interventiformazione> interventiformaziones) {
		this.interventiformaziones = interventiformaziones;
	}

	public Interventiformazione addInterventiformazione(Interventiformazione interventiformazione) {
		getInterventiformaziones().add(interventiformazione);
		interventiformazione.setPratica(this);

		return interventiformazione;
	}

	public Interventiformazione removeInterventiformazione(Interventiformazione interventiformazione) {
		getInterventiformaziones().remove(interventiformazione);
		interventiformazione.setPratica(null);

		return interventiformazione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPOSIZIONEATTUALE")
	public Tabelle getPosizioneAttuale() {
		return this.posizioneAttuale;
	}

	public void setPosizioneAttuale(Tabelle tabelle1) {
		this.posizioneAttuale = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOPRATICA")
	public Tabelle getTipoPratica() {
		return this.tipoPratica;
	}

	public void setTipoPratica(Tabelle tabelle2) {
		this.tipoPratica = tabelle2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAMBITO")
	public Tabelle getAmbito() {
		return this.ambito;
	}

	public void setAmbito(Tabelle tabelle3) {
		this.ambito = tabelle3;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTATOPRATICA")
	public Tabelle getStatoPratica() {
		return this.statoPratica;
	}

	public void setStatoPratica(Tabelle tabelle4) {
		this.statoPratica = tabelle4;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROGETTO")
	public Tabelle getProgetto() {
		return this.progetto;
	}

	public void setProgetto(Tabelle tabelle5) {
		this.progetto = tabelle5;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASL")
	public Tabelle getAsl() {
		return this.asl;
	}

	public void setAsl(Tabelle tabelle6) {
		this.asl = tabelle6;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@OneToMany(mappedBy="pratiche")
	public List<Sopralluoghidip> getSopralluoghidips() {
		return this.sopralluoghidips;
	}

	public void setSopralluoghidips(List<Sopralluoghidip> sopralluoghidips) {
		this.sopralluoghidips = sopralluoghidips;
	}

	public Sopralluoghidip addSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		getSopralluoghidips().add(sopralluoghidip);
		sopralluoghidip.setPratiche(this);

		return sopralluoghidip;
	}

	public Sopralluoghidip removeSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		getSopralluoghidips().remove(sopralluoghidip);
		sopralluoghidip.setPratiche(null);

		return sopralluoghidip;
	}

	//bi-directional many-to-one association to Sopralluoghidip
	@OneToMany(mappedBy="pratica")
	public List<Riunionioperative> getRiunionioperatives() {
		return this.riunionioperatives;
	}

	public void setRiunionioperatives(List<Riunionioperative> riunionioperatives) {
		this.riunionioperatives = riunionioperatives;
	}

	public Riunionioperative addRiunionioperative(Riunionioperative sopralluoghidip) {
		getRiunionioperatives().add(sopralluoghidip);
		sopralluoghidip.setPratica(this);

		return sopralluoghidip;
	}

	public Riunionioperative removeRiunionioperative(Riunionioperative sopralluoghidip) {
		getRiunionioperatives().remove(sopralluoghidip);
		sopralluoghidip.setPratica(null);

		return sopralluoghidip;
	}

	//bi-directional many-to-one association to Infortuni
	@OneToMany(mappedBy="pratiche")
	public List<Cantieri> getCantieris() {
		return this.cantieris;
	}

	public void setCantieris(List<Cantieri> cantieri) {
		this.cantieris = cantieri;
	}

	public Cantieri addCantieri(Cantieri cantiere) {
		getCantieris().add(cantiere);
		cantiere.setPratiche(this);

		return cantiere;
	}

	public Cantieri removeCantieri(Cantieri cantiere) {
		getCantieris().remove(cantiere);
		cantiere.setPratiche(null);

		return cantiere;
	}

	//bi-directional many-to-one association to Infortuni
	@OneToMany(mappedBy="pratiche")
	public List<InfortuniPrevnet> getInfortuniPrevnets() {
		return this.infortuniPrevnets;
	}

	public void setInfortuniPrevnets(List<InfortuniPrevnet> infortuniPrevnets) {
		this.infortuniPrevnets = infortuniPrevnets;
	}

	public InfortuniPrevnet addInfortuniPrevnet(InfortuniPrevnet infortuniPrevnet) {
		getInfortuniPrevnets().add(infortuniPrevnet);
		infortuniPrevnet.setPratiche(this);

		return infortuniPrevnet;
	}

	public InfortuniPrevnet removeInfortuniPrevnet(InfortuniPrevnet infortuniPrevnet) {
		getInfortuniPrevnets().remove(infortuniPrevnet);
		infortuniPrevnet.setPratiche(null);

		return infortuniPrevnet;
	}


	//bi-directional many-to-one association to Infortuniindagati
	@OneToMany(mappedBy="pratiche")
	public List<Infortuniindagati> getInfortuniindagatis() {
		return this.infortuniindagatis;
	}

	public void setInfortuniindagatis(List<Infortuniindagati> infortuniindagatis) {
		this.infortuniindagatis = infortuniindagatis;
	}

	public Infortuniindagati addInfortuniindagati(Infortuniindagati infortuniindagati) {
		getInfortuniindagatis().add(infortuniindagati);
		infortuniindagati.setPratiche(this);

		return infortuniindagati;
	}

	public Infortuniindagati removeInfortuniindagati(Infortuniindagati infortuniindagati) {
		getInfortuniindagatis().remove(infortuniindagati);
		infortuniindagati.setPratiche(null);

		return infortuniindagati;
	}


	private List<Pianidilavoroamianto> pianidilavoroamianto;

	//bi-directional many-to-one association to Pianidilavoroamianto
	@OneToMany(mappedBy="pratica")
	public List<Pianidilavoroamianto> getPianidilavoroamiantos() {
		return this.pianidilavoroamianto;
	}

	public void setPianidilavoroamiantos(List<Pianidilavoroamianto> pianidilavoroamianto) {
		this.pianidilavoroamianto = pianidilavoroamianto;
	}

	public Pianidilavoroamianto addPianidilavoroamianto(Pianidilavoroamianto pianidilavoroamianto) {
		getPianidilavoroamiantos().add(pianidilavoroamianto);
		pianidilavoroamianto.setPratica(this);

		return pianidilavoroamianto;
	}

	public Pianidilavoroamianto removePianidilavoroamianto(Pianidilavoroamianto pianidilavoroamianto) {
		getPianidilavoroamiantos().remove(pianidilavoroamianto);
		pianidilavoroamianto.setPratica(null);

		return pianidilavoroamianto;
	}


	private List<Restamianto> restamiantos;

	//bi-directional many-to-one association to Restamianto
	@OneToMany(mappedBy="pratica")
	public List<Restamianto> getRestamiantos() {
		return this.restamiantos;
	}

	public void setRestamiantos(List<Restamianto> restamiantos) {
		this.restamiantos = restamiantos;
	}

	public Restamianto addRestamianto(Restamianto restamianto) {
		getRestamiantos().add(restamianto);
		restamianto.setPratica(this);

		return restamianto;
	}

	public Restamianto removeRestamianto(Restamianto restamianto) {
		getRestamiantos().remove(restamianto);
		restamianto.setPratica(null);

		return restamianto;
	}


	//bi-directional many-to-one association to Parerigen
	@OneToMany(mappedBy="pratica")
	public List<Parerigen> getParerigens() {
		return this.parerigens;
	}

	public void setParerigens(List<Parerigen> parerigens) {
		this.parerigens = parerigens;
	}

	public Parerigen addParerigen(Parerigen parerigen) {
		getParerigens().add(parerigen);
		parerigen.setPratica(this);

		return parerigen;
	}

	public Parerigen removeParerigen(Parerigen parerigen) {
		getParerigens().remove(parerigen);
		parerigen.setPratica(null);

		return parerigen;
	}

	//bi-directional many-to-one association to Parerigenerici
	@OneToMany(mappedBy="pratica")
	public List<Parerigenerici> getParerigenericis() {
		return this.parerigenericis;
	}

	public void setParerigenericis(List<Parerigenerici> parerigenericis) {
		this.parerigenericis = parerigenericis;
	}

	public Parerigenerici addParerigenerici(Parerigenerici parerigenerici) {
		getParerigenericis().add(parerigenerici);
		parerigenerici.setPratica(this);

		return parerigenerici;
	}

	public Parerigenerici removeParerigenerici(Parerigenerici parerigenerici) {
		getParerigenericis().remove(parerigenerici);
		parerigenerici.setPratica(null);

		return parerigenerici;
	}

	//bi-directional many-to-one association to Altridatinip
	@OneToMany(mappedBy="pratica")
	public List<Altridatinip> getAltridatinips() {
		return this.altridatinips;
	}

	public void setAltridatinips(List<Altridatinip> altridatinips) {
		this.altridatinips = altridatinips;
	}

	public Altridatinip addAltridatinip(Altridatinip altridatinip) {
		getAltridatinips().add(altridatinip);
		altridatinip.setPratica(this);

		return altridatinip;
	}

	public Altridatinip removeAltridatinip(Altridatinip altridatinip) {
		getAltridatinips().remove(altridatinip);
		altridatinip.setPratica(null);

		return altridatinip;
	}

	//bi-directional many-to-one association to Richiestaregistrazione
	@OneToMany(mappedBy="pratica")
	public List<Ricorsigiudizimedici> getRicorsigiudizimedicis() {
		return this.ricorsigiudizimedicis;
	}

	public void setRicorsigiudizimedicis(List<Ricorsigiudizimedici> ricorsigiudizimedicis) {
		this.ricorsigiudizimedicis = ricorsigiudizimedicis;
	}
	
	//bi-directional many-to-one association to Richiestaregistrazione
	@OneToMany(mappedBy="pratica")
	public List<Richiestaregistrazione> getRichiestaregistraziones() {
		return this.richiestaregistraziones;
	}

	public void setRichiestaregistraziones(List<Richiestaregistrazione> richiestaregistraziones) {
		this.richiestaregistraziones = richiestaregistraziones;
	}

	public Richiestaregistrazione addRichiestaregistrazione(Richiestaregistrazione richiestaregistrazione) {
		getRichiestaregistraziones().add(richiestaregistrazione);
		richiestaregistrazione.setPratica(this);

		return richiestaregistrazione;
	}

	public Richiestaregistrazione removeRichiestaregistrazione(Richiestaregistrazione richiestaregistrazione) {
		getRichiestaregistraziones().remove(richiestaregistrazione);
		richiestaregistrazione.setPratica(null);

		return richiestaregistrazione;
	}


	//bi-directional many-to-one association to Scadenze
	@OneToMany(mappedBy="pratica")
	public List<Scadenze> getScadenzes() {
		return this.scadenzes;
	}

	public void setScadenzes(List<Scadenze> scadenzes) {
		this.scadenzes = scadenzes;
	}

	public Scadenze addScadenze(Scadenze scadenze) {
		getScadenzes().add(scadenze);
		scadenze.setPratica(this);

		return scadenze;
	}

	public Scadenze removeScadenze(Scadenze scadenze) {
		getScadenzes().remove(scadenze);
		scadenze.setPratica(null);

		return scadenze;
	}

	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="pratica")
	public List<Sanzioni> getSanzionis() {
		return this.sanzionis;
	}

	public void setSanzionis(List<Sanzioni> sanzionis) {
		this.sanzionis = sanzionis;
	}

	public Sanzioni addSanzioni(Sanzioni sanzioni) {
		getSanzionis().add(sanzioni);
		sanzioni.setPratica(this);

		return sanzioni;
	}

	public Sanzioni removeSanzioni(Sanzioni sanzioni) {
		getSanzionis().remove(sanzioni);
		sanzioni.setPratica(null);

		return sanzioni;
	}

	//bi-directional many-to-one association to Provvedimenti
	@OneToMany(mappedBy="pratica")
	public List<ProvvedimentiPrevnet> getProvvedimentis() {
		return this.provvedimentis;
	}

	public void setProvvedimentis(List<ProvvedimentiPrevnet> provvedimentis) {
		this.provvedimentis = provvedimentis;
	}

	public ProvvedimentiPrevnet addProvvedimenti(ProvvedimentiPrevnet provvedimenti) {
		getProvvedimentis().add(provvedimenti);
		provvedimenti.setPratica(this);

		return provvedimenti;
	}

	public ProvvedimentiPrevnet removeProvvedimenti(ProvvedimentiPrevnet provvedimenti) {
		getProvvedimentis().remove(provvedimenti);
		provvedimenti.setPratica(null);

		return provvedimenti;
	}

	//bi-directional many-to-one association to Lavoratricimadre
	@OneToMany(mappedBy="pratica")
	public List<Lavoratricimadre> getLavoratricimadres() {
		return this.lavoratricimadres;
	}

	public void setLavoratricimadres(List<Lavoratricimadre> lavoratricimadres) {
		this.lavoratricimadres = lavoratricimadres;
	}

	public Lavoratricimadre addLavoratricimadre(Lavoratricimadre lavoratricimadre) {
		getLavoratricimadres().add(lavoratricimadre);
		lavoratricimadre.setPratica(this);

		return lavoratricimadre;
	}

	public Lavoratricimadre removeLavoratricimadre(Lavoratricimadre lavoratricimadre) {
		getLavoratricimadres().remove(lavoratricimadre);
		lavoratricimadre.setPratica(null);

		return lavoratricimadre;
	}

	//bi-directional many-to-one association to Tblblob
	@OneToMany(mappedBy="pratica")
	public List<Tblblob> getTblblobs() {
		return this.tblblobs;
	}

	public void setTblblobs(List<Tblblob> tblblobs) {
		this.tblblobs = tblblobs;
	}

	public Tblblob addTblblob(Tblblob tblblob) {
		getTblblobs().add(tblblob);
		tblblob.setPratica(this);

		return tblblob;
	}

	public Tblblob removeTblblob(Tblblob tblblob) {
		getTblblobs().remove(tblblob);
		tblblob.setPratica(null);

		return tblblob;
	}

	//bi-directional many-to-one association to Documentiscan
	@OneToMany(mappedBy="pratica")
	public List<Documentiscan> getDocumentiscans() {
		return this.documentiscans;
	}

	public void setDocumentiscans(List<Documentiscan> documentiscans) {
		this.documentiscans = documentiscans;
	}

	public Documentiscan addDocumentiscan(Documentiscan documentiscan) {
		getDocumentiscans().add(documentiscan);
		documentiscan.setPratica(this);

		return documentiscan;
	}

	public Documentiscan removeDocumentiscan(Documentiscan documentiscan) {
		getDocumentiscans().remove(documentiscan);
		documentiscan.setPratica(null);

		return documentiscan;
	}

	//bi-directional many-to-one association to Visitespecialistiche
	@OneToMany(mappedBy="pratica")
	public List<Visitespecialistiche> getVisitespecialistiches() {
		return this.visitespecialistiches;
	}

	public void setVisitespecialistiches(List<Visitespecialistiche> visitespecialistiches) {
		this.visitespecialistiches = visitespecialistiches;
	}

	public Visitespecialistiche addVisitespecialistiche(Visitespecialistiche visitespecialistiche) {
		getVisitespecialistiches().add(visitespecialistiche);
		visitespecialistiche.setPratica(this);

		return visitespecialistiche;
	}

	public Visitespecialistiche removeVisitespecialistiche(Visitespecialistiche visitespecialistiche) {
		getVisitespecialistiches().remove(visitespecialistiche);
		visitespecialistiche.setPratica(null);

		return visitespecialistiche;
	}
	
	//bi-directional many-to-one association to Misureambientali
	@OneToMany(mappedBy="pratica")
	public List<Misureambientali> getMisureambientalis() {
		return this.misureambientalis;
	}

	public void setMisureambientalis(List<Misureambientali> misureambientalis) {
		this.misureambientalis = misureambientalis;
	}

	public Misureambientali addMisureambientali(Misureambientali misureambientali) {
		getMisureambientalis().add(misureambientali);
		misureambientali.setPratica(this);

		return misureambientali;
	}

	public Misureambientali removeMisureambientali(Misureambientali misureambientali) {
		getMisureambientalis().remove(misureambientali);
		misureambientali.setPratica(null);

		return misureambientali;
	}


	//bi-directional many-to-one association to Autorizzazionideroga
	@OneToMany(mappedBy="pratiche")
	public List<Autorizzazionideroga> getAutorizzazioniderogas() {
		return this.autorizzazioniderogas;
	}

	public void setAutorizzazioniderogas(List<Autorizzazionideroga> autorizzazioniderogas) {
		this.autorizzazioniderogas = autorizzazioniderogas;
	}

	public Autorizzazionideroga addAutorizzazionideroga(Autorizzazionideroga autorizzazionideroga) {
		getAutorizzazioniderogas().add(autorizzazionideroga);
		autorizzazionideroga.setPratiche(this);

		return autorizzazionideroga;
	}

	public Autorizzazionideroga removeAutorizzazionideroga(Autorizzazionideroga autorizzazionideroga) {
		getAutorizzazioniderogas().remove(autorizzazionideroga);
		autorizzazionideroga.setPratiche(null);

		return autorizzazionideroga;
	}

	//bi-directional many-to-one association to Atti
	@OneToMany(mappedBy="pratica")
	public List<Atti> getAttis() {
		return this.attis;
	}

	public void setAttis(List<Atti> attis) {
		this.attis = attis;
	}

	public Atti addAtti(Atti atti) {
		getAttis().add(atti);
		atti.setPratica(this);

		return atti;
	}

	public Atti removeAtti(Atti atti) {
		getAttis().remove(atti);
		atti.setPratica(null);

		return atti;
	}

}