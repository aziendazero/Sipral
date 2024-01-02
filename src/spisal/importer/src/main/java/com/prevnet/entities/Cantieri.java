package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the CANTIERI database table.
 * 
 */
@Entity
public class Cantieri implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idcantieri;
	private BigDecimal ammontlavori;
	private String chkGru;
	private String chkImpianti;
	private String chkacquacorrente;
	private String chkcameraps;
	private String chkcassettaps;
	private String chkdocinv;
	private String chkmedicazioneps;
	private String chknavale;
	private String chknonallineabileimpresa;
	private String chkpimus;
	private String chkpsicurezzapresente;
	private String comunicazione44997;
	private Date dataaggiornamentosirsap;
	private Date datachiusura;
	private Date datadocsicur;
	private Date datafine;
	private Date datainizio;
	private Date datanotifica;
	private Date dataoraaggiornamento;
	private Date dataultaggnomine;
	private Date dataultvalpimus;
	private Date datavariazione;
	private BigDecimal duratalavori;
	private BigDecimal entita;
	private String fontesirsap;
	private BigDecimal idncwcantiere;
	private BigDecimal idncwcantierevariazione;
	private BigDecimal idncwcoordesecuzione;
	private BigDecimal idncwcoordprogettazione;
	private BigDecimal idncwdittacommittente;
	private BigDecimal idncwoperatore;
	private BigDecimal idncwrapprlegale;
	private BigDecimal idncwresplavori;
	private BigDecimal idncwufficio;
	private BigDecimal idncwutentecommittente;
	private Pratiche pratiche;
	private BigDecimal idprotocollo;
	private BigDecimal idsirsap;
	private BigDecimal idsito;
	private BigDecimal idstoricoutentecommitt;
	private BigDecimal idstoricoutentecoordesecuz;
	private BigDecimal idstoricoutentecoordprog;
	private BigDecimal idstoricoutenterapplegale;
	private BigDecimal idstoricoutenteresplavori;
	private String impresaCodasl;
	private BigDecimal impresaIdanag;
	private String impresaIdsoggetto;
	private String impresaTipo;
	private String latitudine;
	private String longitudine;
	private String naturaopera;
	private BigDecimal ndocce;
	private BigDecimal nlatrine;
	private BigDecimal nlavandini;
	private BigDecimal nlocali;
	private BigDecimal nmensa;
	private String nominacoordinatori;
	private String note;
	private BigDecimal nspogliatoi;
	private BigDecimal numimprese;
	private BigDecimal numlavoratori;
	private BigDecimal numlavoratoriauto;
	private String numnotifica;
	private String riskcaduta;
	private String riskseppellimento;
	private String risksprofondamento;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private Anagrafiche anagrafica;
	private Anagrafiche coordinatoreEsec;
	private Anagrafiche rappLegale;
	private Anagrafiche anagCommittente;
	private Anagrafiche anagImpresaCapoCom;
	private Anagrafiche anagEsecLavori;
	private Ditte dittaCoordEsec;
	private Ditte dittaCapoCom;
	private Ditte dittaEsecLavori;
	private Ditte dittaCommittente;
	private Operatori opAggiornamento;
	private Tabelle faseLavoro;
	private Tabelle opera;
	private Tabelle valpimus;
	private Tabelle naturaOpera;
	private Tabelle categoriaOpera;
	private Tabelle classeRischio;
	private Utenti coordinatoreProgetto;
	private Utenti coordinatoreEsecuzione;
	private Utenti responsabileLavori;
	private List<Committenticantiere> committenti;
	private List<Impresecantiere> ditteCantiere;

	public Cantieri() {
	}


	@Id
	public long getIdcantieri() {
		return this.idcantieri;
	}

	public void setIdcantieri(long idcantieri) {
		this.idcantieri = idcantieri;
	}


	public BigDecimal getAmmontlavori() {
		return this.ammontlavori;
	}

	public void setAmmontlavori(BigDecimal ammontlavori) {
		this.ammontlavori = ammontlavori;
	}

	@Column(name="CHK_GRU")
	public String getChkGru() {
		return this.chkGru;
	}

	public void setChkGru(String chkGru) {
		this.chkGru = chkGru;
	}


	@Column(name="CHK_IMPIANTI")
	public String getChkImpianti() {
		return this.chkImpianti;
	}

	public void setChkImpianti(String chkImpianti) {
		this.chkImpianti = chkImpianti;
	}


	public String getChkacquacorrente() {
		return this.chkacquacorrente;
	}

	public void setChkacquacorrente(String chkacquacorrente) {
		this.chkacquacorrente = chkacquacorrente;
	}


	public String getChkcameraps() {
		return this.chkcameraps;
	}

	public void setChkcameraps(String chkcameraps) {
		this.chkcameraps = chkcameraps;
	}


	public String getChkcassettaps() {
		return this.chkcassettaps;
	}

	public void setChkcassettaps(String chkcassettaps) {
		this.chkcassettaps = chkcassettaps;
	}


	public String getChkdocinv() {
		return this.chkdocinv;
	}

	public void setChkdocinv(String chkdocinv) {
		this.chkdocinv = chkdocinv;
	}


	public String getChkmedicazioneps() {
		return this.chkmedicazioneps;
	}

	public void setChkmedicazioneps(String chkmedicazioneps) {
		this.chkmedicazioneps = chkmedicazioneps;
	}


	public String getChknavale() {
		return this.chknavale;
	}

	public void setChknavale(String chknavale) {
		this.chknavale = chknavale;
	}


	public String getChknonallineabileimpresa() {
		return this.chknonallineabileimpresa;
	}

	public void setChknonallineabileimpresa(String chknonallineabileimpresa) {
		this.chknonallineabileimpresa = chknonallineabileimpresa;
	}


	public String getChkpimus() {
		return this.chkpimus;
	}

	public void setChkpimus(String chkpimus) {
		this.chkpimus = chkpimus;
	}


	public String getChkpsicurezzapresente() {
		return this.chkpsicurezzapresente;
	}

	public void setChkpsicurezzapresente(String chkpsicurezzapresente) {
		this.chkpsicurezzapresente = chkpsicurezzapresente;
	}


	public String getComunicazione44997() {
		return this.comunicazione44997;
	}

	public void setComunicazione44997(String comunicazione44997) {
		this.comunicazione44997 = comunicazione44997;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaggiornamentosirsap() {
		return this.dataaggiornamentosirsap;
	}

	public void setDataaggiornamentosirsap(Date dataaggiornamentosirsap) {
		this.dataaggiornamentosirsap = dataaggiornamentosirsap;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatachiusura() {
		return this.datachiusura;
	}

	public void setDatachiusura(Date datachiusura) {
		this.datachiusura = datachiusura;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatadocsicur() {
		return this.datadocsicur;
	}

	public void setDatadocsicur(Date datadocsicur) {
		this.datadocsicur = datadocsicur;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafine() {
		return this.datafine;
	}

	public void setDatafine(Date datafine) {
		this.datafine = datafine;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizio() {
		return this.datainizio;
	}

	public void setDatainizio(Date datainizio) {
		this.datainizio = datainizio;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanotifica() {
		return this.datanotifica;
	}

	public void setDatanotifica(Date datanotifica) {
		this.datanotifica = datanotifica;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataoraaggiornamento() {
		return this.dataoraaggiornamento;
	}

	public void setDataoraaggiornamento(Date dataoraaggiornamento) {
		this.dataoraaggiornamento = dataoraaggiornamento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultaggnomine() {
		return this.dataultaggnomine;
	}

	public void setDataultaggnomine(Date dataultaggnomine) {
		this.dataultaggnomine = dataultaggnomine;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultvalpimus() {
		return this.dataultvalpimus;
	}

	public void setDataultvalpimus(Date dataultvalpimus) {
		this.dataultvalpimus = dataultvalpimus;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavariazione() {
		return this.datavariazione;
	}

	public void setDatavariazione(Date datavariazione) {
		this.datavariazione = datavariazione;
	}


	public BigDecimal getDuratalavori() {
		return this.duratalavori;
	}

	public void setDuratalavori(BigDecimal duratalavori) {
		this.duratalavori = duratalavori;
	}


	public BigDecimal getEntita() {
		return this.entita;
	}

	public void setEntita(BigDecimal entita) {
		this.entita = entita;
	}


	public String getFontesirsap() {
		return this.fontesirsap;
	}

	public void setFontesirsap(String fontesirsap) {
		this.fontesirsap = fontesirsap;
	}


	public BigDecimal getIdncwcantiere() {
		return this.idncwcantiere;
	}

	public void setIdncwcantiere(BigDecimal idncwcantiere) {
		this.idncwcantiere = idncwcantiere;
	}


	public BigDecimal getIdncwcantierevariazione() {
		return this.idncwcantierevariazione;
	}

	public void setIdncwcantierevariazione(BigDecimal idncwcantierevariazione) {
		this.idncwcantierevariazione = idncwcantierevariazione;
	}


	public BigDecimal getIdncwcoordesecuzione() {
		return this.idncwcoordesecuzione;
	}

	public void setIdncwcoordesecuzione(BigDecimal idncwcoordesecuzione) {
		this.idncwcoordesecuzione = idncwcoordesecuzione;
	}


	public BigDecimal getIdncwcoordprogettazione() {
		return this.idncwcoordprogettazione;
	}

	public void setIdncwcoordprogettazione(BigDecimal idncwcoordprogettazione) {
		this.idncwcoordprogettazione = idncwcoordprogettazione;
	}


	public BigDecimal getIdncwdittacommittente() {
		return this.idncwdittacommittente;
	}

	public void setIdncwdittacommittente(BigDecimal idncwdittacommittente) {
		this.idncwdittacommittente = idncwdittacommittente;
	}


	public BigDecimal getIdncwoperatore() {
		return this.idncwoperatore;
	}

	public void setIdncwoperatore(BigDecimal idncwoperatore) {
		this.idncwoperatore = idncwoperatore;
	}


	public BigDecimal getIdncwrapprlegale() {
		return this.idncwrapprlegale;
	}

	public void setIdncwrapprlegale(BigDecimal idncwrapprlegale) {
		this.idncwrapprlegale = idncwrapprlegale;
	}


	public BigDecimal getIdncwresplavori() {
		return this.idncwresplavori;
	}

	public void setIdncwresplavori(BigDecimal idncwresplavori) {
		this.idncwresplavori = idncwresplavori;
	}


	public BigDecimal getIdncwufficio() {
		return this.idncwufficio;
	}

	public void setIdncwufficio(BigDecimal idncwufficio) {
		this.idncwufficio = idncwufficio;
	}


	public BigDecimal getIdncwutentecommittente() {
		return this.idncwutentecommittente;
	}

	public void setIdncwutentecommittente(BigDecimal idncwutentecommittente) {
		this.idncwutentecommittente = idncwutentecommittente;
	}


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratiche() {
		return this.pratiche;
	}

	public void setPratiche(Pratiche pratiche) {
		this.pratiche = pratiche;
	}


	public BigDecimal getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(BigDecimal idprotocollo) {
		this.idprotocollo = idprotocollo;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}


	public BigDecimal getIdsito() {
		return this.idsito;
	}

	public void setIdsito(BigDecimal idsito) {
		this.idsito = idsito;
	}


	public BigDecimal getIdstoricoutentecommitt() {
		return this.idstoricoutentecommitt;
	}

	public void setIdstoricoutentecommitt(BigDecimal idstoricoutentecommitt) {
		this.idstoricoutentecommitt = idstoricoutentecommitt;
	}


	public BigDecimal getIdstoricoutentecoordesecuz() {
		return this.idstoricoutentecoordesecuz;
	}

	public void setIdstoricoutentecoordesecuz(BigDecimal idstoricoutentecoordesecuz) {
		this.idstoricoutentecoordesecuz = idstoricoutentecoordesecuz;
	}


	public BigDecimal getIdstoricoutentecoordprog() {
		return this.idstoricoutentecoordprog;
	}

	public void setIdstoricoutentecoordprog(BigDecimal idstoricoutentecoordprog) {
		this.idstoricoutentecoordprog = idstoricoutentecoordprog;
	}


	public BigDecimal getIdstoricoutenterapplegale() {
		return this.idstoricoutenterapplegale;
	}

	public void setIdstoricoutenterapplegale(BigDecimal idstoricoutenterapplegale) {
		this.idstoricoutenterapplegale = idstoricoutenterapplegale;
	}


	public BigDecimal getIdstoricoutenteresplavori() {
		return this.idstoricoutenteresplavori;
	}

	public void setIdstoricoutenteresplavori(BigDecimal idstoricoutenteresplavori) {
		this.idstoricoutenteresplavori = idstoricoutenteresplavori;
	}


	@Column(name="IMPRESA_CODASL")
	public String getImpresaCodasl() {
		return this.impresaCodasl;
	}

	public void setImpresaCodasl(String impresaCodasl) {
		this.impresaCodasl = impresaCodasl;
	}


	@Column(name="IMPRESA_IDANAG")
	public BigDecimal getImpresaIdanag() {
		return this.impresaIdanag;
	}

	public void setImpresaIdanag(BigDecimal impresaIdanag) {
		this.impresaIdanag = impresaIdanag;
	}


	@Column(name="IMPRESA_IDSOGGETTO")
	public String getImpresaIdsoggetto() {
		return this.impresaIdsoggetto;
	}

	public void setImpresaIdsoggetto(String impresaIdsoggetto) {
		this.impresaIdsoggetto = impresaIdsoggetto;
	}


	@Column(name="IMPRESA_TIPO")
	public String getImpresaTipo() {
		return this.impresaTipo;
	}

	public void setImpresaTipo(String impresaTipo) {
		this.impresaTipo = impresaTipo;
	}


	public String getLatitudine() {
		return this.latitudine;
	}

	public void setLatitudine(String latitudine) {
		this.latitudine = latitudine;
	}


	public String getLongitudine() {
		return this.longitudine;
	}

	public void setLongitudine(String longitudine) {
		this.longitudine = longitudine;
	}


	public String getNaturaopera() {
		return this.naturaopera;
	}

	public void setNaturaopera(String naturaopera) {
		this.naturaopera = naturaopera;
	}


	public BigDecimal getNdocce() {
		return this.ndocce;
	}

	public void setNdocce(BigDecimal ndocce) {
		this.ndocce = ndocce;
	}


	public BigDecimal getNlatrine() {
		return this.nlatrine;
	}

	public void setNlatrine(BigDecimal nlatrine) {
		this.nlatrine = nlatrine;
	}


	public BigDecimal getNlavandini() {
		return this.nlavandini;
	}

	public void setNlavandini(BigDecimal nlavandini) {
		this.nlavandini = nlavandini;
	}


	public BigDecimal getNlocali() {
		return this.nlocali;
	}

	public void setNlocali(BigDecimal nlocali) {
		this.nlocali = nlocali;
	}


	public BigDecimal getNmensa() {
		return this.nmensa;
	}

	public void setNmensa(BigDecimal nmensa) {
		this.nmensa = nmensa;
	}


	public String getNominacoordinatori() {
		return this.nominacoordinatori;
	}

	public void setNominacoordinatori(String nominacoordinatori) {
		this.nominacoordinatori = nominacoordinatori;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getNspogliatoi() {
		return this.nspogliatoi;
	}

	public void setNspogliatoi(BigDecimal nspogliatoi) {
		this.nspogliatoi = nspogliatoi;
	}


	public BigDecimal getNumimprese() {
		return this.numimprese;
	}

	public void setNumimprese(BigDecimal numimprese) {
		this.numimprese = numimprese;
	}


	public BigDecimal getNumlavoratori() {
		return this.numlavoratori;
	}

	public void setNumlavoratori(BigDecimal numlavoratori) {
		this.numlavoratori = numlavoratori;
	}


	public BigDecimal getNumlavoratoriauto() {
		return this.numlavoratoriauto;
	}

	public void setNumlavoratoriauto(BigDecimal numlavoratoriauto) {
		this.numlavoratoriauto = numlavoratoriauto;
	}


	public String getNumnotifica() {
		return this.numnotifica;
	}

	public void setNumnotifica(String numnotifica) {
		this.numnotifica = numnotifica;
	}


	public String getRiskcaduta() {
		return this.riskcaduta;
	}

	public void setRiskcaduta(String riskcaduta) {
		this.riskcaduta = riskcaduta;
	}


	public String getRiskseppellimento() {
		return this.riskseppellimento;
	}

	public void setRiskseppellimento(String riskseppellimento) {
		this.riskseppellimento = riskseppellimento;
	}


	public String getRisksprofondamento() {
		return this.risksprofondamento;
	}

	public void setRisksprofondamento(String risksprofondamento) {
		this.risksprofondamento = risksprofondamento;
	}


	@Temporal(TemporalType.DATE)
	public Date getSynchdate() {
		return this.synchdate;
	}

	public void setSynchdate(Date synchdate) {
		this.synchdate = synchdate;
	}


	public String getSynchflag() {
		return this.synchflag;
	}

	public void setSynchflag(String synchflag) {
		this.synchflag = synchflag;
	}


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


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getAnagrafica() {
		return this.anagrafica;
	}

	public void setAnagrafica(Anagrafiche anagrafica) {
		this.anagrafica = anagrafica;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTACOORDESEC")
	public Anagrafiche getCoordinatoreEsec() {
		return this.coordinatoreEsec;
	}

	public void setCoordinatoreEsec(Anagrafiche coordinatoreEsec) {
		this.coordinatoreEsec = coordinatoreEsec;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRAPPLEGALE")
	public Anagrafiche getRappLegale() {
		return this.rappLegale;
	}

	public void setRappLegale(Anagrafiche rappLegale) {
		this.rappLegale = rappLegale;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICACOMMITT")
	public Anagrafiche getAnagCommittente() {
		return this.anagCommittente;
	}

	public void setAnagCommittente(Anagrafiche anagCommittente) {
		this.anagCommittente = anagCommittente;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDIMPRESACAPOCOM")
	public Anagrafiche getAnagImpresaCapoCom() {
		return this.anagImpresaCapoCom;
	}

	public void setAnagImpresaCapoCom(Anagrafiche anagImpresaCapoCom) {
		this.anagImpresaCapoCom = anagImpresaCapoCom;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGESECLAVORI")
	public Anagrafiche getAnagEsecLavori() {
		return this.anagEsecLavori;
	}

	public void setAnagEsecLavori(Anagrafiche anagEsecLavori) {
		this.anagEsecLavori = anagEsecLavori;
	}

	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTACOORDESEC")
	public Ditte getDittaCoordEsec() {
		return this.dittaCoordEsec;
	}

	public void setDittaCoordEsec(Ditte dittaCoordEsec) {
		this.dittaCoordEsec = dittaCoordEsec;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDAIMPRESACAPOCOM")
	public Ditte getDittaCapoCom() {
		return this.dittaCapoCom;
	}

	public void setDittaCapoCom(Ditte dittaCapoCom) {
		this.dittaCapoCom = dittaCapoCom;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTAESECLAVORI")
	public Ditte getDittaEsecLavori() {
		return this.dittaEsecLavori;
	}

	public void setDittaEsecLavori(Ditte dittaEsecLavori) {
		this.dittaEsecLavori = dittaEsecLavori;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTACOMMITT")
	public Ditte getDittaCommittente() {
		return this.dittaCommittente;
	}

	public void setDittaCommittente(Ditte dittaCommittente) {
		this.dittaCommittente = dittaCommittente;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREAGGIORNAMENTO")
	public Operatori getOpAggiornamento() {
		return this.opAggiornamento;
	}

	public void setOpAggiornamento(Operatori opAggiornamento) {
		this.opAggiornamento = opAggiornamento;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDFASELAVORO")
	public Tabelle getFaseLavoro() {
		return this.faseLavoro;
	}

	public void setFaseLavoro(Tabelle faseLavoro) {
		this.faseLavoro = faseLavoro;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERA")
	public Tabelle getOpera() {
		return this.opera;
	}

	public void setOpera(Tabelle opera) {
		this.opera = opera;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDVALPIMUS")
	public Tabelle getValpimus() {
		return this.valpimus;
	}

	public void setValpimus(Tabelle valpimus) {
		this.valpimus = valpimus;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDNATURAOPERA")
	public Tabelle getNaturaOpera() {
		return this.naturaOpera;
	}

	public void setNaturaOpera(Tabelle naturaOpera) {
		this.naturaOpera = naturaOpera;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCATEGORIAOPERA")
	public Tabelle getCategoriaOpera() {
		return this.categoriaOpera;
	}

	public void setCategoriaOpera(Tabelle categoriaOpera) {
		this.categoriaOpera = categoriaOpera;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCLASSERISCHIO")
	public Tabelle getClasseRischio() {
		return this.classeRischio;
	}

	public void setClasseRischio(Tabelle classeRischio) {
		this.classeRischio = classeRischio;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOORDINATOREPROG")
	public Utenti getCoordinatoreProgetto() {
		return this.coordinatoreProgetto;
	}

	public void setCoordinatoreProgetto(Utenti coordinatoreProgetto) {
		this.coordinatoreProgetto = coordinatoreProgetto;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOORDINATOREESECUZ")
	public Utenti getCoordinatoreEsecuzione() {
		return this.coordinatoreEsecuzione;
	}

	public void setCoordinatoreEsecuzione(Utenti coordinatoreEsecuzione) {
		this.coordinatoreEsecuzione = coordinatoreEsecuzione;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRESPONSABILELAVORI")
	public Utenti getResponsabileLavori() {
		return this.responsabileLavori;
	}

	public void setResponsabileLavori(Utenti responsabileLavori) {
		this.responsabileLavori = responsabileLavori;
	}


	//bi-directional many-to-one association to Committenticantiere
	@OneToMany(mappedBy="cantieri")
	public List<Committenticantiere> getCommittenti() {
		return this.committenti;
	}

	public void setCommittenti(List<Committenticantiere> committenticantieres) {
		this.committenti = committenticantieres;
	}

	public Committenticantiere addCommittenticantiere(Committenticantiere committenticantiere) {
		getCommittenti().add(committenticantiere);
		committenticantiere.setCantieri(this);

		return committenticantiere;
	}

	public Committenticantiere removeCommittenticantiere(Committenticantiere committenticantiere) {
		getCommittenti().remove(committenticantiere);
		committenticantiere.setCantieri(null);

		return committenticantiere;
	}


	//bi-directional many-to-one association to Impresecantiere
	@OneToMany(mappedBy="cantieri")
	public List<Impresecantiere> getDitteCantiere() {
		return this.ditteCantiere;
	}

	public void setDitteCantiere(List<Impresecantiere> impresecantieres) {
		this.ditteCantiere = impresecantieres;
	}

	public Impresecantiere addImpresecantiere(Impresecantiere impresecantiere) {
		getDitteCantiere().add(impresecantiere);
		impresecantiere.setCantieri(this);

		return impresecantiere;
	}

	public Impresecantiere removeImpresecantiere(Impresecantiere impresecantiere) {
		getDitteCantiere().remove(impresecantiere);
		impresecantiere.setCantieri(null);

		return impresecantiere;
	}

}