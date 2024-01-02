package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the MALATTIEPROFESSIONALI database table.
 * 
 */
@Entity
public class Malattieprofessionali implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idmalattieprofessionali;
	private String altrifattori;
	private BigDecimal annoinviomalprof;
	private String chktrasferitoasl;
	private String chkulteriorinotmal;
	private String correlazione;
	private Date dataanalisi;
	private Date dataarchiviazione;
	private Date datacertificato;
	private Date datacomunicazione;
	private Date datadecesso;
	private Date datafineindagine;
	private Date datainizioindagine;
	private Date datainoltro;
	private Date dataorigdoctrasmissione;
	private Date datariconoscimentoinail;
	private String diagnosi;
	private String esitoindagine;
	private BigDecimal idprocura;
	private BigDecimal idsirsap;
	private BigDecimal idstoricoutentelavoratore;
	private String inchiestaindagine;
	private String inviatoprocura;
	private BigDecimal lepd;
	private BigDecimal lepw;
	private String luogodecesso;
	private String note;
	private Date oradecesso;
	private String procedibilita;
	private String riconoscimentoinail;
	private Anagrafiche ente;
	private Anagrafiche dittaprincRespons;
	private Anagrafiche anagraficaDitta;
	private AttivitaPrevnet attivita;
	private Comuni comuneComunicazione;
	private Comuni comuneDecesso;
	private Ditte dittaPrincRespons;
	private Ditte schedaDitta;
	private Fattoririschio fattoririschio;
	private Medici medici;
	private Operatori operatore;
	private Operatori operatoreAnalisi;
	private Pratiche pratiche;
	private Tabelle malattiaContratta;
	private Tabelle sedeMalattia;
	private Tabelle tipoInchiesta;
	private Tabelle nessoGlobale;
	private Tabelle qualInfo;
	private Tabelle qualDiagnosi;
	private Tabelle codiceIcd;
	private Tabelle gravita;
	private Tabelle asl;
	private Tabelle condProf;
	private Tabelle notifica;
	private Tabelle organo;
	private Tabelle malattiaInail;
	private Tabelle motivoArchiviazione;
	private Tabelle motivoProcedibile;
	private Tabelle professione;
	private Tabelle mansione;
	private Utenti lavoratore;
	private Esamiobiettivi esamiobiettivi;
	private List<Inchiestemalatpro> inchiestemalatpros;
	private List<Malattieproupg> malattieproupgs;
	
	public Malattieprofessionali() {
	}


	@Id
	public long getIdmalattieprofessionali() {
		return this.idmalattieprofessionali;
	}

	public void setIdmalattieprofessionali(long idmalattieprofessionali) {
		this.idmalattieprofessionali = idmalattieprofessionali;
	}


	public String getAltrifattori() {
		return this.altrifattori;
	}

	public void setAltrifattori(String altrifattori) {
		this.altrifattori = altrifattori;
	}


	public BigDecimal getAnnoinviomalprof() {
		return this.annoinviomalprof;
	}

	public void setAnnoinviomalprof(BigDecimal annoinviomalprof) {
		this.annoinviomalprof = annoinviomalprof;
	}


	public String getChktrasferitoasl() {
		return this.chktrasferitoasl;
	}

	public void setChktrasferitoasl(String chktrasferitoasl) {
		this.chktrasferitoasl = chktrasferitoasl;
	}


	public String getChkulteriorinotmal() {
		return this.chkulteriorinotmal;
	}

	public void setChkulteriorinotmal(String chkulteriorinotmal) {
		this.chkulteriorinotmal = chkulteriorinotmal;
	}


	public String getCorrelazione() {
		return this.correlazione;
	}

	public void setCorrelazione(String correlazione) {
		this.correlazione = correlazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataanalisi() {
		return this.dataanalisi;
	}

	public void setDataanalisi(Date dataanalisi) {
		this.dataanalisi = dataanalisi;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataarchiviazione() {
		return this.dataarchiviazione;
	}

	public void setDataarchiviazione(Date dataarchiviazione) {
		this.dataarchiviazione = dataarchiviazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacertificato() {
		return this.datacertificato;
	}

	public void setDatacertificato(Date datacertificato) {
		this.datacertificato = datacertificato;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicazione() {
		return this.datacomunicazione;
	}

	public void setDatacomunicazione(Date datacomunicazione) {
		this.datacomunicazione = datacomunicazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatadecesso() {
		return this.datadecesso;
	}

	public void setDatadecesso(Date datadecesso) {
		this.datadecesso = datadecesso;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafineindagine() {
		return this.datafineindagine;
	}

	public void setDatafineindagine(Date datafineindagine) {
		this.datafineindagine = datafineindagine;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizioindagine() {
		return this.datainizioindagine;
	}

	public void setDatainizioindagine(Date datainizioindagine) {
		this.datainizioindagine = datainizioindagine;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainoltro() {
		return this.datainoltro;
	}

	public void setDatainoltro(Date datainoltro) {
		this.datainoltro = datainoltro;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataorigdoctrasmissione() {
		return this.dataorigdoctrasmissione;
	}

	public void setDataorigdoctrasmissione(Date dataorigdoctrasmissione) {
		this.dataorigdoctrasmissione = dataorigdoctrasmissione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatariconoscimentoinail() {
		return this.datariconoscimentoinail;
	}

	public void setDatariconoscimentoinail(Date datariconoscimentoinail) {
		this.datariconoscimentoinail = datariconoscimentoinail;
	}


	public String getDiagnosi() {
		return this.diagnosi;
	}

	public void setDiagnosi(String diagnosi) {
		this.diagnosi = diagnosi;
	}


	public String getEsitoindagine() {
		return this.esitoindagine;
	}

	public void setEsitoindagine(String esitoindagine) {
		this.esitoindagine = esitoindagine;
	}

	public BigDecimal getIdprocura() {
		return this.idprocura;
	}

	public void setIdprocura(BigDecimal idprocura) {
		this.idprocura = idprocura;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}


	public BigDecimal getIdstoricoutentelavoratore() {
		return this.idstoricoutentelavoratore;
	}

	public void setIdstoricoutentelavoratore(BigDecimal idstoricoutentelavoratore) {
		this.idstoricoutentelavoratore = idstoricoutentelavoratore;
	}


	public String getInchiestaindagine() {
		return this.inchiestaindagine;
	}

	public void setInchiestaindagine(String inchiestaindagine) {
		this.inchiestaindagine = inchiestaindagine;
	}


	public String getInviatoprocura() {
		return this.inviatoprocura;
	}

	public void setInviatoprocura(String inviatoprocura) {
		this.inviatoprocura = inviatoprocura;
	}


	public BigDecimal getLepd() {
		return this.lepd;
	}

	public void setLepd(BigDecimal lepd) {
		this.lepd = lepd;
	}


	public BigDecimal getLepw() {
		return this.lepw;
	}

	public void setLepw(BigDecimal lepw) {
		this.lepw = lepw;
	}


	public String getLuogodecesso() {
		return this.luogodecesso;
	}

	public void setLuogodecesso(String luogodecesso) {
		this.luogodecesso = luogodecesso;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	@Temporal(TemporalType.DATE)
	public Date getOradecesso() {
		return this.oradecesso;
	}

	public void setOradecesso(Date oradecesso) {
		this.oradecesso = oradecesso;
	}


	public String getProcedibilita() {
		return this.procedibilita;
	}

	public void setProcedibilita(String procedibilita) {
		this.procedibilita = procedibilita;
	}


	public String getRiconoscimentoinail() {
		return this.riconoscimentoinail;
	}

	public void setRiconoscimentoinail(String riconoscimentoinail) {
		this.riconoscimentoinail = riconoscimentoinail;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDENTE")
	public Anagrafiche getEnte() {
		return this.ente;
	}

	public void setEnte(Anagrafiche anagrafiche1) {
		this.ente = anagrafiche1;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANDITTAPRINCRESPONS")
	public Anagrafiche getDittaprincRespons() {
		return this.dittaprincRespons;
	}

	public void setDittaprincRespons(Anagrafiche anagrafiche2) {
		this.dittaprincRespons = anagrafiche2;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICADITTA")
	public Anagrafiche getAnagraficaDitta() {
		return this.anagraficaDitta;
	}

	public void setAnagraficaDitta(Anagrafiche anagrafiche3) {
		this.anagraficaDitta = anagrafiche3;
	}


	//bi-directional many-to-one association to AttivitaPrevnet
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITA")
	public AttivitaPrevnet getAttivitaPrevnet() {
		return this.attivita;
	}

	public void setAttivitaPrevnet(AttivitaPrevnet attivita) {
		this.attivita = attivita;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNECOMUNICAZIONE")
	public Comuni getComuneComunicazione() {
		return this.comuneComunicazione;
	}

	public void setComuneComunicazione(Comuni comuni1) {
		this.comuneComunicazione = comuni1;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEDECESSO")
	public Comuni getComuneDecesso() {
		return this.comuneDecesso;
	}

	public void setComuneDecesso(Comuni comuni2) {
		this.comuneDecesso = comuni2;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTAPRINCRESPONS")
	public Ditte getDittaPrincRespons() {
		return this.dittaPrincRespons;
	}

	public void setDittaPrincRespons(Ditte ditte1) {
		this.dittaPrincRespons = ditte1;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getSchedaDitta() {
		return this.schedaDitta;
	}

	public void setSchedaDitta(Ditte ditte2) {
		this.schedaDitta = ditte2;
	}


	//bi-directional many-to-one association to Fattoririschio
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDFATTORERISCHIO")
	public Fattoririschio getFattoririschio() {
		return this.fattoririschio;
	}

	public void setFattoririschio(Fattoririschio fattoririschio) {
		this.fattoririschio = fattoririschio;
	}


	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICO")
	public Medici getMedici() {
		return this.medici;
	}

	public void setMedici(Medici medici) {
		this.medici = medici;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatore() {
		return this.operatore;
	}

	public void setOperatore(Operatori operatori1) {
		this.operatore = operatori1;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREANALISI")
	public Operatori getOperatoreAnalisi() {
		return this.operatoreAnalisi;
	}

	public void setOperatoreAnalisi(Operatori operatori2) {
		this.operatoreAnalisi = operatori2;
	}


	//bi-directional many-to-one association to Pratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratiche() {
		return this.pratiche;
	}

	public void setPratiche(Pratiche procpratiche) {
		this.pratiche = procpratiche;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMALATTIACONTRATTA")
	public Tabelle getMalattiaContratta() {
		return this.malattiaContratta;
	}

	public void setMalattiaContratta(Tabelle tabelle1) {
		this.malattiaContratta = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSEDEMALATTIA")
	public Tabelle getSedeMalattia() {
		return this.sedeMalattia;
	}

	public void setSedeMalattia(Tabelle tabelle2) {
		this.sedeMalattia = tabelle2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOINCHIESTA")
	public Tabelle getTipoInchiesta() {
		return this.tipoInchiesta;
	}

	public void setTipoInchiesta(Tabelle tabelle3) {
		this.tipoInchiesta = tabelle3;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDNESSOGLOBALE")
	public Tabelle getNessoGlobale() {
		return this.nessoGlobale;
	}

	public void setNessoGlobale(Tabelle tabelle4) {
		this.nessoGlobale = tabelle4;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDQUALINFO")
	public Tabelle getQualInfo() {
		return this.qualInfo;
	}

	public void setQualInfo(Tabelle tabelle5) {
		this.qualInfo = tabelle5;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDQUALDIAGNOSI")
	public Tabelle getQualDiagnosi() {
		return this.qualDiagnosi;
	}

	public void setQualDiagnosi(Tabelle tabelle6) {
		this.qualDiagnosi = tabelle6;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCODICEICDX")
	public Tabelle getCodiceIcd() {
		return this.codiceIcd;
	}

	public void setCodiceIcd(Tabelle tabelle7) {
		this.codiceIcd = tabelle7;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDGRAVITAMALATTIA")
	public Tabelle getGravita() {
		return this.gravita;
	}

	public void setGravita(Tabelle tabelle8) {
		this.gravita = tabelle8;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASL")
	public Tabelle getAsl() {
		return this.asl;
	}

	public void setAsl(Tabelle tabelle9) {
		this.asl = tabelle9;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCONDPROF")
	public Tabelle getCondProf() {
		return this.condProf;
	}

	public void setCondProf(Tabelle tabelle10) {
		this.condProf = tabelle10;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDNOTIFICA")
	public Tabelle getNotifica() {
		return this.notifica;
	}

	public void setNotifica(Tabelle tabelle11) {
		this.notifica = tabelle11;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDORGANO")
	public Tabelle getOrgano() {
		return this.organo;
	}

	public void setOrgano(Tabelle organo) {
		this.organo = organo;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMALATTIAINAIL")
	public Tabelle getMalattiaInail() {
		return this.malattiaInail;
	}

	public void setMalattiaInail(Tabelle tabelle13) {
		this.malattiaInail = tabelle13;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOARCHIVIAZIONE")
	public Tabelle getMotivoArchiviazione() {
		return this.motivoArchiviazione;
	}

	public void setMotivoArchiviazione(Tabelle tabelle14) {
		this.motivoArchiviazione = tabelle14;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOPROCEDIBILE")
	public Tabelle getMotivoProcedibile() {
		return this.motivoProcedibile;
	}

	public void setMotivoProcedibile(Tabelle tabelle15) {
		this.motivoProcedibile = tabelle15;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROFESSIONE")
	public Tabelle getProfessione() {
		return this.professione;
	}

	public void setProfessione(Tabelle tabelle16) {
		this.professione = tabelle16;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMANSIONE")
	public Tabelle getMansione() {
		return this.mansione;
	}

	public void setMansione(Tabelle mansione) {
		this.mansione = mansione;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLAVORATORE")
	public Utenti getLavoratore() {
		return this.lavoratore;
	}

	public void setLavoratore(Utenti utenti) {
		this.lavoratore = utenti;
	}
	
	//bi-directional many-to-one association to Esamiobiettivi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESAMEOBIETTIVO")
	public Esamiobiettivi getEsamiobiettivi() {
		return this.esamiobiettivi;
	}

	public void setEsamiobiettivi(Esamiobiettivi esamiobiettivi) {
		this.esamiobiettivi = esamiobiettivi;
	}
	
	//bi-directional many-to-one association to Inchiestemalatpro
	@OneToMany(mappedBy="malattia")
	public List<Inchiestemalatpro> getInchiestemalatpros() {
		return this.inchiestemalatpros;
	}

	public void setInchiestemalatpros(List<Inchiestemalatpro> inchiestemalatpros) {
		this.inchiestemalatpros = inchiestemalatpros;
	}

	public Inchiestemalatpro addInchiestemalatpro(Inchiestemalatpro inchiestemalatpro) {
		getInchiestemalatpros().add(inchiestemalatpro);
		inchiestemalatpro.setMalattia(this);

		return inchiestemalatpro;
	}

	public Inchiestemalatpro removeInchiestemalatpro(Inchiestemalatpro inchiestemalatpro) {
		getInchiestemalatpros().remove(inchiestemalatpro);
		inchiestemalatpro.setMalattia(null);

		return inchiestemalatpro;
	}
	
	//bi-directional many-to-one association to Malattieproupg
	@OneToMany(mappedBy="malattia")
	public List<Malattieproupg> getMalattieproupgs() {
		return this.malattieproupgs;
	}

	public void setMalattieproupgs(List<Malattieproupg> malattieproupgs) {
		this.malattieproupgs = malattieproupgs;
	}

	public Malattieproupg addMalattieproupg(Malattieproupg malattieproupg) {
		getMalattieproupgs().add(malattieproupg);
		malattieproupg.setMalattia(this);

		return malattieproupg;
	}

	public Malattieproupg removeMalattieproupg(Malattieproupg malattieproupg) {
		getMalattieproupgs().remove(malattieproupg);
		malattieproupg.setMalattia(null);

		return malattieproupg;
	}

}