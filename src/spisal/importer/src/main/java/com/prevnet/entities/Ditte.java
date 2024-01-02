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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the DITTE database table.
 * 
 */
@Entity
public class Ditte implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idditte;
	private String aaepDatainizioval;
	private BigDecimal aaepIdazienda;
	private BigDecimal aaepIdsede;
	private BigDecimal addetti;
	private String altrocodicevet;
	private String ambitoionizzante;
	private BigDecimal anticipo194;
	private String approvidricocomunale;
	private String approvidricoimptrattamento;
	private String approvidricoprivato;
	private String autocontrollo;
	private String autocontrollopresente;
	private String autocontrolloritiratopervalut;
	private String autocontrollovalutato;
	private String capcom;
	private String capintestazionefat;
	private String capspedizionefat;
	private String capspedizioneref;
	private String caratteredia;
	private String cciaa;
	private String cervednrea;
	private String cervedprogul;
	private String chkRegCe;
	private String chkabilitanecroscopiche;
	private String chkattestato;
	private String chkcessazioneattivri;
	private String chkcomunicazri;
	private String chkdia;
	private String chkdittapubblica;
	private String chkimpcondaria;
	private String chkimpgastossici;
	private String chkimpradiolog;
	private String chknonallineabileimpresa;
	private String chkregccommercio;
	private String chkrichiestanostari;
	private String chkrichnecroscopiche;
	private String chkrspppresente;
	private String chksorvsanitaria;
	private String chksslpresente;
	private String chkunitlocterr;
	private String classificazioneps;
	private BigDecimal codcontabilitastorico;
	private String codfiscpartiva;
	private String codicecamerale;
	private String codicefiscale;
	private String codiceoliamm;
	private String comunicazioneditta;
	private Date dataaggimpresa;
	private Date dataaggiornamentosirsap;
	private Date dataaggmedico;
	private Date dataautorizzazione;
	private Date dataautorizzazionesmaltimento;
	private Date datacciaa;
	private Date datacessazioneattivri;
	private Date datacomunicazione;
	private Date datacomunicazri;
	private Date datadia;
	private Date datahaccp;
	private Date datarichiestanostari;
	private Date dataultimoaggiornamento;
	private Date datavariazione;
	private String denominazioneditta;
	private String denominazioneesercizio;
	private String descrizioneattivita;
	private Date diadataiscrizioneri;
	private String dianumerori;
	private BigDecimal dipendenti;
	private String email;
	private String fax;
	private Date fineattivita;
	private String fontesirsap;
	private BigDecimal idcircoscrizione;
	private BigDecimal idcompartoesercizi;
	private BigDecimal idsirsap;
	private BigDecimal idsito;
	private BigDecimal idstoricoutenteresphaccp;
	private BigDecimal idstorutenterespautocontrollo;
	private BigDecimal idtipoattivita;
	private String impresaCdtipostruttura;
	private String impresaCodasl;
	private String impresaDstipostruttura;
	private String impresaGetsede;
	private BigDecimal impresaIdanag;
	private String impresaIdsoggetto;
	private String impresaSottotipo;
	private String impresaTipo;
	private String indirizzocom;
	private String indirizzointestazionefat;
	private String indirizzospedizionefat;
	private String indirizzospedizioneref;
	private Date inizioattivita;
	private String latitudine;
	private String longitudine;
	private String manualehaccppresente;
	private String manualehaccpritiratopervalut;
	private String manualehaccpvalutato;
	private BigDecimal nlavcoinv;
	private String note;
	private String noteapprovidrico;
	private String noterspp;
	private String numautorizzazione;
	private String numerodia;
	private String oggettosociale;
	private String pec;
	private String pianorintracciabilita;
	private String pivanonnota;
	private String privato;
	private String ragionesociale;
	private String schedacorrente;
	private String sitoweb;
	private String smaltimento;
	private BigDecimal superficie;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private String telefono;
	private Date timestampinsmod;
	private String umsuperficie;
	private String visfiscnonfatturabili;
	private List<Attivitaditte> attivitadittes;
	private Anagrafiche proprietarioRurale;
	private Anagrafiche respAutomezzi;
	private Anagrafiche detentoreRurale;
	private Anagrafiche riferimento;
	private Anagrafiche enteRiferimentoImpresa;
	private Anagrafiche anagrafica;
	private Anagrafiche respAutoControllo;
	private AttivitaPrevnet attivitaPrevnetEsercizi;
	private AttivitaPrevnet attivitaPrevnet;
	private Comuni comuneIntestazioneFat;
	private Comuni comuneSpedizioneFat;
	private Comuni comuneCom;
	private Comuni comuneSpedizioneRef;
	private Ditte schedaEnteRiferimentoImpresa;
	private Localita localita;
	private Localita localitaCom;
	private Medici medicoCompetente;
	private Tabelle naturaGiuridica;
	private Tabelle comparto;
	private Tabelle stato;
	private Tabelle aslCompetente;
	private Tabelle esitoUltimaVal;
	private Tabelle insalubrita;
	private Tabelle cittaEstera;
	private Tabelle nazionalita;
	private Tabelle acquedotto;
	private Tabelle tipoStruttura;
	private Tabelle distrettoAmbito;
	private Tabelle qualificaRapprLeg;
	private Tabelle provCCIAA;
	private Tabelle tipoSmaltimento;
	private Utenti rls;
	private Utenti rapprLegale;
	private Utenti titolare;
	private Utenti responsabileHACCP;
	private List<Rappresentantiditta> rappresentanti;

	public Ditte() {
	}


	@Id
	public long getIdditte() {
		return this.idditte;
	}

	public void setIdditte(long idditte) {
		this.idditte = idditte;
	}


	@Column(name="AAEP_DATAINIZIOVAL")
	public String getAaepDatainizioval() {
		return this.aaepDatainizioval;
	}

	public void setAaepDatainizioval(String aaepDatainizioval) {
		this.aaepDatainizioval = aaepDatainizioval;
	}


	@Column(name="AAEP_IDAZIENDA")
	public BigDecimal getAaepIdazienda() {
		return this.aaepIdazienda;
	}

	public void setAaepIdazienda(BigDecimal aaepIdazienda) {
		this.aaepIdazienda = aaepIdazienda;
	}


	@Column(name="AAEP_IDSEDE")
	public BigDecimal getAaepIdsede() {
		return this.aaepIdsede;
	}

	public void setAaepIdsede(BigDecimal aaepIdsede) {
		this.aaepIdsede = aaepIdsede;
	}


	public BigDecimal getAddetti() {
		return this.addetti;
	}

	public void setAddetti(BigDecimal addetti) {
		this.addetti = addetti;
	}


	public String getAltrocodicevet() {
		return this.altrocodicevet;
	}

	public void setAltrocodicevet(String altrocodicevet) {
		this.altrocodicevet = altrocodicevet;
	}


	public String getAmbitoionizzante() {
		return this.ambitoionizzante;
	}

	public void setAmbitoionizzante(String ambitoionizzante) {
		this.ambitoionizzante = ambitoionizzante;
	}


	public BigDecimal getAnticipo194() {
		return this.anticipo194;
	}

	public void setAnticipo194(BigDecimal anticipo194) {
		this.anticipo194 = anticipo194;
	}


	public String getApprovidricocomunale() {
		return this.approvidricocomunale;
	}

	public void setApprovidricocomunale(String approvidricocomunale) {
		this.approvidricocomunale = approvidricocomunale;
	}


	public String getApprovidricoimptrattamento() {
		return this.approvidricoimptrattamento;
	}

	public void setApprovidricoimptrattamento(String approvidricoimptrattamento) {
		this.approvidricoimptrattamento = approvidricoimptrattamento;
	}


	public String getApprovidricoprivato() {
		return this.approvidricoprivato;
	}

	public void setApprovidricoprivato(String approvidricoprivato) {
		this.approvidricoprivato = approvidricoprivato;
	}


	public String getAutocontrollo() {
		return this.autocontrollo;
	}

	public void setAutocontrollo(String autocontrollo) {
		this.autocontrollo = autocontrollo;
	}


	public String getAutocontrollopresente() {
		return this.autocontrollopresente;
	}

	public void setAutocontrollopresente(String autocontrollopresente) {
		this.autocontrollopresente = autocontrollopresente;
	}


	public String getAutocontrolloritiratopervalut() {
		return this.autocontrolloritiratopervalut;
	}

	public void setAutocontrolloritiratopervalut(String autocontrolloritiratopervalut) {
		this.autocontrolloritiratopervalut = autocontrolloritiratopervalut;
	}


	public String getAutocontrollovalutato() {
		return this.autocontrollovalutato;
	}

	public void setAutocontrollovalutato(String autocontrollovalutato) {
		this.autocontrollovalutato = autocontrollovalutato;
	}


	public String getCapcom() {
		return this.capcom;
	}

	public void setCapcom(String capcom) {
		this.capcom = capcom;
	}


	public String getCapintestazionefat() {
		return this.capintestazionefat;
	}

	public void setCapintestazionefat(String capintestazionefat) {
		this.capintestazionefat = capintestazionefat;
	}


	public String getCapspedizionefat() {
		return this.capspedizionefat;
	}

	public void setCapspedizionefat(String capspedizionefat) {
		this.capspedizionefat = capspedizionefat;
	}


	public String getCapspedizioneref() {
		return this.capspedizioneref;
	}

	public void setCapspedizioneref(String capspedizioneref) {
		this.capspedizioneref = capspedizioneref;
	}


	public String getCaratteredia() {
		return this.caratteredia;
	}

	public void setCaratteredia(String caratteredia) {
		this.caratteredia = caratteredia;
	}


	public String getCciaa() {
		return this.cciaa;
	}

	public void setCciaa(String cciaa) {
		this.cciaa = cciaa;
	}


	public String getCervednrea() {
		return this.cervednrea;
	}

	public void setCervednrea(String cervednrea) {
		this.cervednrea = cervednrea;
	}


	public String getCervedprogul() {
		return this.cervedprogul;
	}

	public void setCervedprogul(String cervedprogul) {
		this.cervedprogul = cervedprogul;
	}


	@Column(name="CHK_REG_CE")
	public String getChkRegCe() {
		return this.chkRegCe;
	}

	public void setChkRegCe(String chkRegCe) {
		this.chkRegCe = chkRegCe;
	}


	public String getChkabilitanecroscopiche() {
		return this.chkabilitanecroscopiche;
	}

	public void setChkabilitanecroscopiche(String chkabilitanecroscopiche) {
		this.chkabilitanecroscopiche = chkabilitanecroscopiche;
	}


	public String getChkattestato() {
		return this.chkattestato;
	}

	public void setChkattestato(String chkattestato) {
		this.chkattestato = chkattestato;
	}


	public String getChkcessazioneattivri() {
		return this.chkcessazioneattivri;
	}

	public void setChkcessazioneattivri(String chkcessazioneattivri) {
		this.chkcessazioneattivri = chkcessazioneattivri;
	}


	public String getChkcomunicazri() {
		return this.chkcomunicazri;
	}

	public void setChkcomunicazri(String chkcomunicazri) {
		this.chkcomunicazri = chkcomunicazri;
	}


	public String getChkdia() {
		return this.chkdia;
	}

	public void setChkdia(String chkdia) {
		this.chkdia = chkdia;
	}


	public String getChkdittapubblica() {
		return this.chkdittapubblica;
	}

	public void setChkdittapubblica(String chkdittapubblica) {
		this.chkdittapubblica = chkdittapubblica;
	}


	public String getChkimpcondaria() {
		return this.chkimpcondaria;
	}

	public void setChkimpcondaria(String chkimpcondaria) {
		this.chkimpcondaria = chkimpcondaria;
	}


	public String getChkimpgastossici() {
		return this.chkimpgastossici;
	}

	public void setChkimpgastossici(String chkimpgastossici) {
		this.chkimpgastossici = chkimpgastossici;
	}


	public String getChkimpradiolog() {
		return this.chkimpradiolog;
	}

	public void setChkimpradiolog(String chkimpradiolog) {
		this.chkimpradiolog = chkimpradiolog;
	}


	public String getChknonallineabileimpresa() {
		return this.chknonallineabileimpresa;
	}

	public void setChknonallineabileimpresa(String chknonallineabileimpresa) {
		this.chknonallineabileimpresa = chknonallineabileimpresa;
	}


	public String getChkregccommercio() {
		return this.chkregccommercio;
	}

	public void setChkregccommercio(String chkregccommercio) {
		this.chkregccommercio = chkregccommercio;
	}


	public String getChkrichiestanostari() {
		return this.chkrichiestanostari;
	}

	public void setChkrichiestanostari(String chkrichiestanostari) {
		this.chkrichiestanostari = chkrichiestanostari;
	}


	public String getChkrichnecroscopiche() {
		return this.chkrichnecroscopiche;
	}

	public void setChkrichnecroscopiche(String chkrichnecroscopiche) {
		this.chkrichnecroscopiche = chkrichnecroscopiche;
	}


	public String getChkrspppresente() {
		return this.chkrspppresente;
	}

	public void setChkrspppresente(String chkrspppresente) {
		this.chkrspppresente = chkrspppresente;
	}


	public String getChksorvsanitaria() {
		return this.chksorvsanitaria;
	}

	public void setChksorvsanitaria(String chksorvsanitaria) {
		this.chksorvsanitaria = chksorvsanitaria;
	}


	public String getChksslpresente() {
		return this.chksslpresente;
	}

	public void setChksslpresente(String chksslpresente) {
		this.chksslpresente = chksslpresente;
	}


	public String getChkunitlocterr() {
		return this.chkunitlocterr;
	}

	public void setChkunitlocterr(String chkunitlocterr) {
		this.chkunitlocterr = chkunitlocterr;
	}


	public String getClassificazioneps() {
		return this.classificazioneps;
	}

	public void setClassificazioneps(String classificazioneps) {
		this.classificazioneps = classificazioneps;
	}


	public BigDecimal getCodcontabilitastorico() {
		return this.codcontabilitastorico;
	}

	public void setCodcontabilitastorico(BigDecimal codcontabilitastorico) {
		this.codcontabilitastorico = codcontabilitastorico;
	}


	public String getCodfiscpartiva() {
		return this.codfiscpartiva;
	}

	public void setCodfiscpartiva(String codfiscpartiva) {
		this.codfiscpartiva = codfiscpartiva;
	}


	public String getCodicecamerale() {
		return this.codicecamerale;
	}

	public void setCodicecamerale(String codicecamerale) {
		this.codicecamerale = codicecamerale;
	}


	public String getCodicefiscale() {
		return this.codicefiscale;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}


	public String getCodiceoliamm() {
		return this.codiceoliamm;
	}

	public void setCodiceoliamm(String codiceoliamm) {
		this.codiceoliamm = codiceoliamm;
	}


	public String getComunicazioneditta() {
		return this.comunicazioneditta;
	}

	public void setComunicazioneditta(String comunicazioneditta) {
		this.comunicazioneditta = comunicazioneditta;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaggimpresa() {
		return this.dataaggimpresa;
	}

	public void setDataaggimpresa(Date dataaggimpresa) {
		this.dataaggimpresa = dataaggimpresa;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaggiornamentosirsap() {
		return this.dataaggiornamentosirsap;
	}

	public void setDataaggiornamentosirsap(Date dataaggiornamentosirsap) {
		this.dataaggiornamentosirsap = dataaggiornamentosirsap;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaggmedico() {
		return this.dataaggmedico;
	}

	public void setDataaggmedico(Date dataaggmedico) {
		this.dataaggmedico = dataaggmedico;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataautorizzazione() {
		return this.dataautorizzazione;
	}

	public void setDataautorizzazione(Date dataautorizzazione) {
		this.dataautorizzazione = dataautorizzazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataautorizzazionesmaltimento() {
		return this.dataautorizzazionesmaltimento;
	}

	public void setDataautorizzazionesmaltimento(Date dataautorizzazionesmaltimento) {
		this.dataautorizzazionesmaltimento = dataautorizzazionesmaltimento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacciaa() {
		return this.datacciaa;
	}

	public void setDatacciaa(Date datacciaa) {
		this.datacciaa = datacciaa;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacessazioneattivri() {
		return this.datacessazioneattivri;
	}

	public void setDatacessazioneattivri(Date datacessazioneattivri) {
		this.datacessazioneattivri = datacessazioneattivri;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicazione() {
		return this.datacomunicazione;
	}

	public void setDatacomunicazione(Date datacomunicazione) {
		this.datacomunicazione = datacomunicazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicazri() {
		return this.datacomunicazri;
	}

	public void setDatacomunicazri(Date datacomunicazri) {
		this.datacomunicazri = datacomunicazri;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatadia() {
		return this.datadia;
	}

	public void setDatadia(Date datadia) {
		this.datadia = datadia;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatahaccp() {
		return this.datahaccp;
	}

	public void setDatahaccp(Date datahaccp) {
		this.datahaccp = datahaccp;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarichiestanostari() {
		return this.datarichiestanostari;
	}

	public void setDatarichiestanostari(Date datarichiestanostari) {
		this.datarichiestanostari = datarichiestanostari;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultimoaggiornamento() {
		return this.dataultimoaggiornamento;
	}

	public void setDataultimoaggiornamento(Date dataultimoaggiornamento) {
		this.dataultimoaggiornamento = dataultimoaggiornamento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavariazione() {
		return this.datavariazione;
	}

	public void setDatavariazione(Date datavariazione) {
		this.datavariazione = datavariazione;
	}


	public String getDenominazioneditta() {
		return this.denominazioneditta;
	}

	public void setDenominazioneditta(String denominazioneditta) {
		this.denominazioneditta = denominazioneditta;
	}


	public String getDenominazioneesercizio() {
		return this.denominazioneesercizio;
	}

	public void setDenominazioneesercizio(String denominazioneesercizio) {
		this.denominazioneesercizio = denominazioneesercizio;
	}


	public String getDescrizioneattivita() {
		return this.descrizioneattivita;
	}

	public void setDescrizioneattivita(String descrizioneattivita) {
		this.descrizioneattivita = descrizioneattivita;
	}


	@Temporal(TemporalType.DATE)
	public Date getDiadataiscrizioneri() {
		return this.diadataiscrizioneri;
	}

	public void setDiadataiscrizioneri(Date diadataiscrizioneri) {
		this.diadataiscrizioneri = diadataiscrizioneri;
	}


	public String getDianumerori() {
		return this.dianumerori;
	}

	public void setDianumerori(String dianumerori) {
		this.dianumerori = dianumerori;
	}


	public BigDecimal getDipendenti() {
		return this.dipendenti;
	}

	public void setDipendenti(BigDecimal dipendenti) {
		this.dipendenti = dipendenti;
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


	@Temporal(TemporalType.DATE)
	public Date getFineattivita() {
		return this.fineattivita;
	}

	public void setFineattivita(Date fineattivita) {
		this.fineattivita = fineattivita;
	}


	public String getFontesirsap() {
		return this.fontesirsap;
	}

	public void setFontesirsap(String fontesirsap) {
		this.fontesirsap = fontesirsap;
	}


	public BigDecimal getIdcircoscrizione() {
		return this.idcircoscrizione;
	}

	public void setIdcircoscrizione(BigDecimal idcircoscrizione) {
		this.idcircoscrizione = idcircoscrizione;
	}


	public BigDecimal getIdcompartoesercizi() {
		return this.idcompartoesercizi;
	}

	public void setIdcompartoesercizi(BigDecimal idcompartoesercizi) {
		this.idcompartoesercizi = idcompartoesercizi;
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


	public BigDecimal getIdstoricoutenteresphaccp() {
		return this.idstoricoutenteresphaccp;
	}

	public void setIdstoricoutenteresphaccp(BigDecimal idstoricoutenteresphaccp) {
		this.idstoricoutenteresphaccp = idstoricoutenteresphaccp;
	}


	public BigDecimal getIdstorutenterespautocontrollo() {
		return this.idstorutenterespautocontrollo;
	}

	public void setIdstorutenterespautocontrollo(BigDecimal idstorutenterespautocontrollo) {
		this.idstorutenterespautocontrollo = idstorutenterespautocontrollo;
	}


	public BigDecimal getIdtipoattivita() {
		return this.idtipoattivita;
	}

	public void setIdtipoattivita(BigDecimal idtipoattivita) {
		this.idtipoattivita = idtipoattivita;
	}


	@Column(name="IMPRESA_CDTIPOSTRUTTURA")
	public String getImpresaCdtipostruttura() {
		return this.impresaCdtipostruttura;
	}

	public void setImpresaCdtipostruttura(String impresaCdtipostruttura) {
		this.impresaCdtipostruttura = impresaCdtipostruttura;
	}


	@Column(name="IMPRESA_CODASL")
	public String getImpresaCodasl() {
		return this.impresaCodasl;
	}

	public void setImpresaCodasl(String impresaCodasl) {
		this.impresaCodasl = impresaCodasl;
	}


	@Column(name="IMPRESA_DSTIPOSTRUTTURA")
	public String getImpresaDstipostruttura() {
		return this.impresaDstipostruttura;
	}

	public void setImpresaDstipostruttura(String impresaDstipostruttura) {
		this.impresaDstipostruttura = impresaDstipostruttura;
	}


	@Column(name="IMPRESA_GETSEDE")
	public String getImpresaGetsede() {
		return this.impresaGetsede;
	}

	public void setImpresaGetsede(String impresaGetsede) {
		this.impresaGetsede = impresaGetsede;
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


	@Column(name="IMPRESA_SOTTOTIPO")
	public String getImpresaSottotipo() {
		return this.impresaSottotipo;
	}

	public void setImpresaSottotipo(String impresaSottotipo) {
		this.impresaSottotipo = impresaSottotipo;
	}


	@Column(name="IMPRESA_TIPO")
	public String getImpresaTipo() {
		return this.impresaTipo;
	}

	public void setImpresaTipo(String impresaTipo) {
		this.impresaTipo = impresaTipo;
	}


	public String getIndirizzocom() {
		return this.indirizzocom;
	}

	public void setIndirizzocom(String indirizzocom) {
		this.indirizzocom = indirizzocom;
	}


	public String getIndirizzointestazionefat() {
		return this.indirizzointestazionefat;
	}

	public void setIndirizzointestazionefat(String indirizzointestazionefat) {
		this.indirizzointestazionefat = indirizzointestazionefat;
	}


	public String getIndirizzospedizionefat() {
		return this.indirizzospedizionefat;
	}

	public void setIndirizzospedizionefat(String indirizzospedizionefat) {
		this.indirizzospedizionefat = indirizzospedizionefat;
	}


	public String getIndirizzospedizioneref() {
		return this.indirizzospedizioneref;
	}

	public void setIndirizzospedizioneref(String indirizzospedizioneref) {
		this.indirizzospedizioneref = indirizzospedizioneref;
	}


	@Temporal(TemporalType.DATE)
	public Date getInizioattivita() {
		return this.inizioattivita;
	}

	public void setInizioattivita(Date inizioattivita) {
		this.inizioattivita = inizioattivita;
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


	public String getManualehaccppresente() {
		return this.manualehaccppresente;
	}

	public void setManualehaccppresente(String manualehaccppresente) {
		this.manualehaccppresente = manualehaccppresente;
	}


	public String getManualehaccpritiratopervalut() {
		return this.manualehaccpritiratopervalut;
	}

	public void setManualehaccpritiratopervalut(String manualehaccpritiratopervalut) {
		this.manualehaccpritiratopervalut = manualehaccpritiratopervalut;
	}


	public String getManualehaccpvalutato() {
		return this.manualehaccpvalutato;
	}

	public void setManualehaccpvalutato(String manualehaccpvalutato) {
		this.manualehaccpvalutato = manualehaccpvalutato;
	}


	public BigDecimal getNlavcoinv() {
		return this.nlavcoinv;
	}

	public void setNlavcoinv(BigDecimal nlavcoinv) {
		this.nlavcoinv = nlavcoinv;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNoteapprovidrico() {
		return this.noteapprovidrico;
	}

	public void setNoteapprovidrico(String noteapprovidrico) {
		this.noteapprovidrico = noteapprovidrico;
	}


	public String getNoterspp() {
		return this.noterspp;
	}

	public void setNoterspp(String noterspp) {
		this.noterspp = noterspp;
	}


	public String getNumautorizzazione() {
		return this.numautorizzazione;
	}

	public void setNumautorizzazione(String numautorizzazione) {
		this.numautorizzazione = numautorizzazione;
	}


	public String getNumerodia() {
		return this.numerodia;
	}

	public void setNumerodia(String numerodia) {
		this.numerodia = numerodia;
	}


	public String getOggettosociale() {
		return this.oggettosociale;
	}

	public void setOggettosociale(String oggettosociale) {
		this.oggettosociale = oggettosociale;
	}


	public String getPec() {
		return this.pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}


	public String getPianorintracciabilita() {
		return this.pianorintracciabilita;
	}

	public void setPianorintracciabilita(String pianorintracciabilita) {
		this.pianorintracciabilita = pianorintracciabilita;
	}


	public String getPivanonnota() {
		return this.pivanonnota;
	}

	public void setPivanonnota(String pivanonnota) {
		this.pivanonnota = pivanonnota;
	}


	public String getPrivato() {
		return this.privato;
	}

	public void setPrivato(String privato) {
		this.privato = privato;
	}


	public String getRagionesociale() {
		return this.ragionesociale;
	}

	public void setRagionesociale(String ragionesociale) {
		this.ragionesociale = ragionesociale;
	}


	public String getSchedacorrente() {
		return this.schedacorrente;
	}

	public void setSchedacorrente(String schedacorrente) {
		this.schedacorrente = schedacorrente;
	}


	public String getSitoweb() {
		return this.sitoweb;
	}

	public void setSitoweb(String sitoweb) {
		this.sitoweb = sitoweb;
	}


	public String getSmaltimento() {
		return this.smaltimento;
	}

	public void setSmaltimento(String smaltimento) {
		this.smaltimento = smaltimento;
	}


	public BigDecimal getSuperficie() {
		return this.superficie;
	}

	public void setSuperficie(BigDecimal superficie) {
		this.superficie = superficie;
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


	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	public String getUmsuperficie() {
		return this.umsuperficie;
	}

	public void setUmsuperficie(String umsuperficie) {
		this.umsuperficie = umsuperficie;
	}


	public String getVisfiscnonfatturabili() {
		return this.visfiscnonfatturabili;
	}

	public void setVisfiscnonfatturabili(String visfiscnonfatturabili) {
		this.visfiscnonfatturabili = visfiscnonfatturabili;
	}


	//bi-directional many-to-one association to Attivitaditte
	@OneToMany(mappedBy="ditta")
	public List<Attivitaditte> getAttivitadittes() {
		return this.attivitadittes;
	}

	public void setAttivitadittes(List<Attivitaditte> attivitadittes) {
		this.attivitadittes = attivitadittes;
	}

	public Attivitaditte addAttivitaditte(Attivitaditte attivitaditte) {
		getAttivitadittes().add(attivitaditte);
		attivitaditte.setDitta(this);

		return attivitaditte;
	}

	public Attivitaditte removeAttivitaditte(Attivitaditte attivitaditte) {
		getAttivitadittes().remove(attivitaditte);
		attivitaditte.setDitta(null);

		return attivitaditte;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROPRIETARIORURALE")
	public Anagrafiche getProprietarioRurale() {
		return this.proprietarioRurale;
	}

	public void setProprietarioRurale(Anagrafiche proprietarioRurale) {
		this.proprietarioRurale = proprietarioRurale;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTITOLAREDITTA")
	public Utenti getTitolare() {
		return this.titolare;
	}

	public void setTitolare(Utenti titolare) {
		this.titolare = titolare;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRESPONSABILEAUTOMEZZI")
	public Anagrafiche getRespAutomezzi() {
		return this.respAutomezzi;
	}

	public void setRespAutomezzi(Anagrafiche respAutomezzi) {
		this.respAutomezzi = respAutomezzi;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDETENTORERURALE")
	public Anagrafiche getDetentoreRurale() {
		return this.detentoreRurale;
	}

	public void setDetentoreRurale(Anagrafiche detentoreRurale) {
		this.detentoreRurale = detentoreRurale;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="RIFERIMENTO")
	public Anagrafiche getRiferimento() {
		return this.riferimento;
	}

	public void setRiferimento(Anagrafiche riferimento) {
		this.riferimento = riferimento;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDENTERIFERIMENTOIMPRESA")
	public Anagrafiche getEnteRiferimentoImpresa() {
		return this.enteRiferimentoImpresa;
	}

	public void setEnteRiferimentoImpresa(Anagrafiche enteRiferimentoImpresa) {
		this.enteRiferimentoImpresa = enteRiferimentoImpresa;
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
	@JoinColumn(name="IDRESPAUTOCONTROLLO")
	public Anagrafiche getRespAutoControllo() {
		return this.respAutoControllo;
	}

	public void setRespAutoControllo(Anagrafiche respAutoControllo) {
		this.respAutoControllo = respAutoControllo;
	}


	//bi-directional many-to-one association to Attivita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITAESERCIZI")
	public AttivitaPrevnet getAttivitaPrevnetEsercizi() {
		return this.attivitaPrevnetEsercizi;
	}

	public void setAttivitaPrevnetEsercizi(AttivitaPrevnet attivitaPrevnetEsercizi) {
		this.attivitaPrevnetEsercizi = attivitaPrevnetEsercizi;
	}


	//bi-directional many-to-one association to Attivita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITA")
	public AttivitaPrevnet getAttivitaPrevnet() {
		return this.attivitaPrevnet;
	}

	public void setAttivitaPrevnet(AttivitaPrevnet attivitaPrevnet) {
		this.attivitaPrevnet = attivitaPrevnet;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEINTESTAZIONEFAT")
	public Comuni getComuneIntestazioneFat() {
		return this.comuneIntestazioneFat;
	}

	public void setComuneIntestazioneFat(Comuni comuneIntestazioneFat) {
		this.comuneIntestazioneFat = comuneIntestazioneFat;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNESPEDIZIONEFAT")
	public Comuni getComuneSpedizioneFat() {
		return this.comuneSpedizioneFat;
	}

	public void setComuneSpedizioneFat(Comuni comuneSpedizioneFat) {
		this.comuneSpedizioneFat = comuneSpedizioneFat;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNECOM")
	public Comuni getComuneCom() {
		return this.comuneCom;
	}

	public void setComuneCom(Comuni comuneCom) {
		this.comuneCom = comuneCom;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNESPEDIZIONEREF")
	public Comuni getComuneSpedizioneRef() {
		return this.comuneSpedizioneRef;
	}

	public void setComuneSpedizioneRef(Comuni comuneSpedizioneRef) {
		this.comuneSpedizioneRef = comuneSpedizioneRef;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDAENTERIFERIMENTOIMPRESA")
	public Ditte getSchedaEnteRiferimentoImpresa() {
		return this.schedaEnteRiferimentoImpresa;
	}

	public void setSchedaEnteRiferimentoImpresa(Ditte schedaEnteRiferimentoImpresa) {
		this.schedaEnteRiferimentoImpresa = schedaEnteRiferimentoImpresa;
	}


	//bi-directional many-to-one association to Localita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLOCALITA")
	public Localita getLocalita() {
		return this.localita;
	}

	public void setLocalita(Localita localita) {
		this.localita = localita;
	}


	//bi-directional many-to-one association to Localita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLOCALITACOM")
	public Localita getLocalitaCom() {
		return this.localitaCom;
	}

	public void setLocalitaCom(Localita localitaCom) {
		this.localitaCom = localitaCom;
	}


	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICOCOMPETENTE")
	public Medici getMedicoCompetente() {
		return this.medicoCompetente;
	}

	public void setMedicoCompetente(Medici medicoCompetente) {
		this.medicoCompetente = medicoCompetente;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDNATURAGIURIDICA")
	public Tabelle getNaturaGiuridica() {
		return this.naturaGiuridica;
	}

	public void setNaturaGiuridica(Tabelle naturaGiuridica) {
		this.naturaGiuridica = naturaGiuridica;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMPARTO")
	public Tabelle getComparto() {
		return this.comparto;
	}

	public void setComparto(Tabelle comparto) {
		this.comparto = comparto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTATO")
	public Tabelle getStato() {
		return this.stato;
	}

	public void setStato(Tabelle stato) {
		this.stato = stato;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASLCOMPETENTE")
	public Tabelle getAslCompetente() {
		return this.aslCompetente;
	}

	public void setAslCompetente(Tabelle aslCompetente) {
		this.aslCompetente = aslCompetente;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITOULTIMAVAL")
	public Tabelle getEsitoUltimaVal() {
		return this.esitoUltimaVal;
	}

	public void setEsitoUltimaVal(Tabelle esitoUltimaVal) {
		this.esitoUltimaVal = esitoUltimaVal;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINSALUBRITA")
	public Tabelle getInsalubrita() {
		return this.insalubrita;
	}

	public void setInsalubrita(Tabelle insalubrita) {
		this.insalubrita = insalubrita;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCITTAESTERA")
	public Tabelle getCittaEstera() {
		return this.cittaEstera;
	}

	public void setCittaEstera(Tabelle cittaEstera) {
		this.cittaEstera = cittaEstera;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDNAZIONALITA")
	public Tabelle getNazionalita() {
		return this.nazionalita;
	}

	public void setNazionalita(Tabelle nazionalita) {
		this.nazionalita = nazionalita;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDACQUEDOTTOAPPRIDRICO")
	public Tabelle getAcquedotto() {
		return this.acquedotto;
	}

	public void setAcquedotto(Tabelle acquedotto) {
		this.acquedotto = acquedotto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOSTRUTTURA")
	public Tabelle getTipoStruttura() {
		return this.tipoStruttura;
	}

	public void setTipoStruttura(Tabelle tipoStruttura) {
		this.tipoStruttura = tipoStruttura;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDISTRETTOAMBITO")
	public Tabelle getDistrettoAmbito() {
		return this.distrettoAmbito;
	}

	public void setDistrettoAmbito(Tabelle distrettoAmbito) {
		this.distrettoAmbito = distrettoAmbito;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDQUALIFICARAPPRLEG")
	public Tabelle getQualificaRapprLeg() {
		return this.qualificaRapprLeg;
	}

	public void setQualificaRapprLeg(Tabelle qualificaRapprLeg) {
		this.qualificaRapprLeg = qualificaRapprLeg;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="DIAIDPROVINCIACCIAA")
	public Tabelle getProvCCIAA() {
		return this.provCCIAA;
	}

	public void setProvCCIAA(Tabelle provCCIAA) {
		this.provCCIAA = provCCIAA;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOSMALTIMENTO")
	public Tabelle getTipoSmaltimento() {
		return this.tipoSmaltimento;
	}

	public void setTipoSmaltimento(Tabelle tipoSmaltimento) {
		this.tipoSmaltimento = tipoSmaltimento;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRLS")
	public Utenti getRls() {
		return this.rls;
	}

	public void setRls(Utenti rls) {
		this.rls = rls;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRAPPRLEGALE")
	public Utenti getRapprLegale() {
		return this.rapprLegale;
	}

	public void setRapprLegale(Utenti rapprLegale) {
		this.rapprLegale = rapprLegale;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRESPONSABILEHACCP")
	public Utenti getResponsabileHACCP() {
		return this.responsabileHACCP;
	}

	public void setResponsabileHACCP(Utenti responsabileHACCP) {
		this.responsabileHACCP = responsabileHACCP;
	}


	//bi-directional many-to-one association to Rappresentantiditta
	@OneToMany(mappedBy="ditta")
	public List<Rappresentantiditta> getRappresentanti() {
		return this.rappresentanti;
	}

	public void setRappresentanti(List<Rappresentantiditta> rappresentantidittas) {
		this.rappresentanti = rappresentantidittas;
	}

	public Rappresentantiditta addRappresentantiditta(Rappresentantiditta rappresentantiditta) {
		getRappresentanti().add(rappresentantiditta);
		rappresentantiditta.setDitta(this);

		return rappresentantiditta;
	}

	public Rappresentantiditta removeRappresentantiditta(Rappresentantiditta rappresentantiditta) {
		getRappresentanti().remove(rappresentantiditta);
		rappresentantiditta.setDitta(null);

		return rappresentantiditta;
	}

}