package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the SANZIONI database table.
 * 
 */
@Entity
@NamedQuery(name="Sanzioni.findAll", query="SELECT s FROM Sanzioni s")
public class Sanzioni implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idsanzioni;
	private BigDecimal annosanz;
	private BigDecimal chiavesid;
	private String chkrichiestaaudizione;
	private String codicepagamentosanz;
	private Date dataarchiviazione;
	private Date dataaudizione;
	private Date datacommissione;
	private Date datacomunicazione;
	private Date datacomunicestiest;
	private Date datacontestazione;
	private Date datacontrodeduzioni;
	private Date datadefrichrate;
	private Date dataemissioneording;
	private Date dataesitoistdif;
	private Date dataesitoruolo;
	private Date dataiscrizruolo;
	private Date datanotifica;
	private Date datanotifordingiunzione;
	private Date dataoblazione;
	private Date dataoblazording;
	private Date dataopposizordingiunzione;
	private Date datapagamimportoridotto;
	private Date datapagamimprespingimento;
	private Date datapresentazionerapp;
	private Date datarichiestaaud;
	private Date datarichiscrruolo;
	private Date datarichordingiunzione;
	private Date datarichrate;
	private Date dataricorsotribunale;
	private Date datarilasciodocidentita;
	private Date datariscossionecoattiva;
	private Date datascadprimarata;
	private Date datascrittodif;
	private Date dataspedizioneverbale;
	private Date dataverbale;
	private String dichtrasgr;
	private String esitocommissione;
	private String esitodefrichrate;
	private String esitoistdif;
	private String esitoricorsotribunale;
	private String esitoruolo;
	private String exportimpresa;
	private String flagnotificatamano;
	private String flagscrittodifensivo;
	private BigDecimal ggpagamrespingimento;
	private BigDecimal ggpagamridotto;
	private String giorniscadingiunz;
	private String giorniscadoblaz;
	private BigDecimal idcampionamentisopr;
	private BigDecimal idpassoprocedura;
	private BigDecimal idprelievoacque;
	private BigDecimal idprelievoalimenti;
	private BigDecimal idprotocollo;
	private BigDecimal idsirsap;
	private BigDecimal idstoricoutente;
	private BigDecimal idstoricoutentetrasgressore;
	private BigDecimal idtipoanagrafica;
	private BigDecimal importoording;
	private BigDecimal importorespingimento;
	private BigDecimal importoridotto;
	private String importoridottopagato;
	private BigDecimal importototale;
	private BigDecimal importoversato;
	private String impresainfmalprof;
	private String luogosanzione;
	private String metodopagamento;
	private String motivoarchiviazione;
	private String note;
	private String noteinfrazioni;
	private BigDecimal numcup;
	private String numerodocidentita;
	private BigDecimal numerorate;
	private BigDecimal numerosanz;
	private String numeroverbale;
	private String numording;
	private String numprotocollo;
	private Date oracontestazione;
	private String pagata;
	private BigDecimal percentualerate;
	private String provenienza;
	private String riferimentiruolo;
	private String rilasciantedocidentita;
	private BigDecimal speseaggiuntive;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	private BigDecimal tipodocumentoidentita;
	private Sopralluoghidip sopralluoghidip;
	private List<Sanzioniarticoli> sanzioniarticolis;
	private List<Dlgs758iter> dlgs758iters;
	private List<ProvvedimentiPrevnet> provvedimentis;
	private Anagrafiche anagrafiche1;
	private Anagrafiche anagrafiche2;
	private Anagrafiche anagrafiche3;
	private Anagrafiche anagrafiche4;
	private Comuni comuni;
	private Ditte ditte1;
	private Ditte ditte2;
	private Operatori operatori;
	private Pratiche pratica;
	private ProvvedimentiPrevnet provvedimenti;
	private Scadenze scadenze1;
	private Scadenze scadenzaOblazione;
	private Scadenze scadenze3;
	private Scadenze scadenze4;
	private Scadenze scadenze5;
	private Scadenze scadenze6;
	private Tabelle tabelle1;
	private Tabelle tabelle2;
	private Tabelle tabelle3;
	private Tabelle tabelle4;
	private Tabelle tabelle5;
	private Tabelle tabelle6;
	private Tabelle tabelle7;
	private Tabelle tabelle8;
	private Utenti utenti1;
	private Utenti obbligatoInSolido;

	public Sanzioni() {
	}


	@Id
	public long getIdsanzioni() {
		return this.idsanzioni;
	}

	public void setIdsanzioni(long idsanzioni) {
		this.idsanzioni = idsanzioni;
	}


	public BigDecimal getAnnosanz() {
		return this.annosanz;
	}

	public void setAnnosanz(BigDecimal annosanz) {
		this.annosanz = annosanz;
	}


	public BigDecimal getChiavesid() {
		return this.chiavesid;
	}

	public void setChiavesid(BigDecimal chiavesid) {
		this.chiavesid = chiavesid;
	}


	public String getChkrichiestaaudizione() {
		return this.chkrichiestaaudizione;
	}

	public void setChkrichiestaaudizione(String chkrichiestaaudizione) {
		this.chkrichiestaaudizione = chkrichiestaaudizione;
	}


	public String getCodicepagamentosanz() {
		return this.codicepagamentosanz;
	}

	public void setCodicepagamentosanz(String codicepagamentosanz) {
		this.codicepagamentosanz = codicepagamentosanz;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataarchiviazione() {
		return this.dataarchiviazione;
	}

	public void setDataarchiviazione(Date dataarchiviazione) {
		this.dataarchiviazione = dataarchiviazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaudizione() {
		return this.dataaudizione;
	}

	public void setDataaudizione(Date dataaudizione) {
		this.dataaudizione = dataaudizione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacommissione() {
		return this.datacommissione;
	}

	public void setDatacommissione(Date datacommissione) {
		this.datacommissione = datacommissione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicazione() {
		return this.datacomunicazione;
	}

	public void setDatacomunicazione(Date datacomunicazione) {
		this.datacomunicazione = datacomunicazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicestiest() {
		return this.datacomunicestiest;
	}

	public void setDatacomunicestiest(Date datacomunicestiest) {
		this.datacomunicestiest = datacomunicestiest;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacontestazione() {
		return this.datacontestazione;
	}

	public void setDatacontestazione(Date datacontestazione) {
		this.datacontestazione = datacontestazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacontrodeduzioni() {
		return this.datacontrodeduzioni;
	}

	public void setDatacontrodeduzioni(Date datacontrodeduzioni) {
		this.datacontrodeduzioni = datacontrodeduzioni;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatadefrichrate() {
		return this.datadefrichrate;
	}

	public void setDatadefrichrate(Date datadefrichrate) {
		this.datadefrichrate = datadefrichrate;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataemissioneording() {
		return this.dataemissioneording;
	}

	public void setDataemissioneording(Date dataemissioneording) {
		this.dataemissioneording = dataemissioneording;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataesitoistdif() {
		return this.dataesitoistdif;
	}

	public void setDataesitoistdif(Date dataesitoistdif) {
		this.dataesitoistdif = dataesitoistdif;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataesitoruolo() {
		return this.dataesitoruolo;
	}

	public void setDataesitoruolo(Date dataesitoruolo) {
		this.dataesitoruolo = dataesitoruolo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataiscrizruolo() {
		return this.dataiscrizruolo;
	}

	public void setDataiscrizruolo(Date dataiscrizruolo) {
		this.dataiscrizruolo = dataiscrizruolo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanotifica() {
		return this.datanotifica;
	}

	public void setDatanotifica(Date datanotifica) {
		this.datanotifica = datanotifica;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanotifordingiunzione() {
		return this.datanotifordingiunzione;
	}

	public void setDatanotifordingiunzione(Date datanotifordingiunzione) {
		this.datanotifordingiunzione = datanotifordingiunzione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataoblazione() {
		return this.dataoblazione;
	}

	public void setDataoblazione(Date dataoblazione) {
		this.dataoblazione = dataoblazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataoblazording() {
		return this.dataoblazording;
	}

	public void setDataoblazording(Date dataoblazording) {
		this.dataoblazording = dataoblazording;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataopposizordingiunzione() {
		return this.dataopposizordingiunzione;
	}

	public void setDataopposizordingiunzione(Date dataopposizordingiunzione) {
		this.dataopposizordingiunzione = dataopposizordingiunzione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatapagamimportoridotto() {
		return this.datapagamimportoridotto;
	}

	public void setDatapagamimportoridotto(Date datapagamimportoridotto) {
		this.datapagamimportoridotto = datapagamimportoridotto;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatapagamimprespingimento() {
		return this.datapagamimprespingimento;
	}

	public void setDatapagamimprespingimento(Date datapagamimprespingimento) {
		this.datapagamimprespingimento = datapagamimprespingimento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatapresentazionerapp() {
		return this.datapresentazionerapp;
	}

	public void setDatapresentazionerapp(Date datapresentazionerapp) {
		this.datapresentazionerapp = datapresentazionerapp;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarichiestaaud() {
		return this.datarichiestaaud;
	}

	public void setDatarichiestaaud(Date datarichiestaaud) {
		this.datarichiestaaud = datarichiestaaud;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarichiscrruolo() {
		return this.datarichiscrruolo;
	}

	public void setDatarichiscrruolo(Date datarichiscrruolo) {
		this.datarichiscrruolo = datarichiscrruolo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarichordingiunzione() {
		return this.datarichordingiunzione;
	}

	public void setDatarichordingiunzione(Date datarichordingiunzione) {
		this.datarichordingiunzione = datarichordingiunzione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarichrate() {
		return this.datarichrate;
	}

	public void setDatarichrate(Date datarichrate) {
		this.datarichrate = datarichrate;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataricorsotribunale() {
		return this.dataricorsotribunale;
	}

	public void setDataricorsotribunale(Date dataricorsotribunale) {
		this.dataricorsotribunale = dataricorsotribunale;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarilasciodocidentita() {
		return this.datarilasciodocidentita;
	}

	public void setDatarilasciodocidentita(Date datarilasciodocidentita) {
		this.datarilasciodocidentita = datarilasciodocidentita;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatariscossionecoattiva() {
		return this.datariscossionecoattiva;
	}

	public void setDatariscossionecoattiva(Date datariscossionecoattiva) {
		this.datariscossionecoattiva = datariscossionecoattiva;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatascadprimarata() {
		return this.datascadprimarata;
	}

	public void setDatascadprimarata(Date datascadprimarata) {
		this.datascadprimarata = datascadprimarata;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatascrittodif() {
		return this.datascrittodif;
	}

	public void setDatascrittodif(Date datascrittodif) {
		this.datascrittodif = datascrittodif;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataspedizioneverbale() {
		return this.dataspedizioneverbale;
	}

	public void setDataspedizioneverbale(Date dataspedizioneverbale) {
		this.dataspedizioneverbale = dataspedizioneverbale;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataverbale() {
		return this.dataverbale;
	}

	public void setDataverbale(Date dataverbale) {
		this.dataverbale = dataverbale;
	}


	public String getDichtrasgr() {
		return this.dichtrasgr;
	}

	public void setDichtrasgr(String dichtrasgr) {
		this.dichtrasgr = dichtrasgr;
	}


	public String getEsitocommissione() {
		return this.esitocommissione;
	}

	public void setEsitocommissione(String esitocommissione) {
		this.esitocommissione = esitocommissione;
	}


	public String getEsitodefrichrate() {
		return this.esitodefrichrate;
	}

	public void setEsitodefrichrate(String esitodefrichrate) {
		this.esitodefrichrate = esitodefrichrate;
	}


	public String getEsitoistdif() {
		return this.esitoistdif;
	}

	public void setEsitoistdif(String esitoistdif) {
		this.esitoistdif = esitoistdif;
	}


	public String getEsitoricorsotribunale() {
		return this.esitoricorsotribunale;
	}

	public void setEsitoricorsotribunale(String esitoricorsotribunale) {
		this.esitoricorsotribunale = esitoricorsotribunale;
	}


	public String getEsitoruolo() {
		return this.esitoruolo;
	}

	public void setEsitoruolo(String esitoruolo) {
		this.esitoruolo = esitoruolo;
	}


//	public String getExportimpresa() {
//		return this.exportimpresa;
//	}
//
//	public void setExportimpresa(String exportimpresa) {
//		this.exportimpresa = exportimpresa;
//	}


	public String getFlagnotificatamano() {
		return this.flagnotificatamano;
	}

	public void setFlagnotificatamano(String flagnotificatamano) {
		this.flagnotificatamano = flagnotificatamano;
	}


	public String getFlagscrittodifensivo() {
		return this.flagscrittodifensivo;
	}

	public void setFlagscrittodifensivo(String flagscrittodifensivo) {
		this.flagscrittodifensivo = flagscrittodifensivo;
	}


	public BigDecimal getGgpagamrespingimento() {
		return this.ggpagamrespingimento;
	}

	public void setGgpagamrespingimento(BigDecimal ggpagamrespingimento) {
		this.ggpagamrespingimento = ggpagamrespingimento;
	}


	public BigDecimal getGgpagamridotto() {
		return this.ggpagamridotto;
	}

	public void setGgpagamridotto(BigDecimal ggpagamridotto) {
		this.ggpagamridotto = ggpagamridotto;
	}


	public String getGiorniscadingiunz() {
		return this.giorniscadingiunz;
	}

	public void setGiorniscadingiunz(String giorniscadingiunz) {
		this.giorniscadingiunz = giorniscadingiunz;
	}


	public String getGiorniscadoblaz() {
		return this.giorniscadoblaz;
	}

	public void setGiorniscadoblaz(String giorniscadoblaz) {
		this.giorniscadoblaz = giorniscadoblaz;
	}


	public BigDecimal getIdcampionamentisopr() {
		return this.idcampionamentisopr;
	}

	public void setIdcampionamentisopr(BigDecimal idcampionamentisopr) {
		this.idcampionamentisopr = idcampionamentisopr;
	}


	public BigDecimal getIdpassoprocedura() {
		return this.idpassoprocedura;
	}

	public void setIdpassoprocedura(BigDecimal idpassoprocedura) {
		this.idpassoprocedura = idpassoprocedura;
	}


	public BigDecimal getIdprelievoacque() {
		return this.idprelievoacque;
	}

	public void setIdprelievoacque(BigDecimal idprelievoacque) {
		this.idprelievoacque = idprelievoacque;
	}


	public BigDecimal getIdprelievoalimenti() {
		return this.idprelievoalimenti;
	}

	public void setIdprelievoalimenti(BigDecimal idprelievoalimenti) {
		this.idprelievoalimenti = idprelievoalimenti;
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


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public BigDecimal getIdstoricoutentetrasgressore() {
		return this.idstoricoutentetrasgressore;
	}

	public void setIdstoricoutentetrasgressore(BigDecimal idstoricoutentetrasgressore) {
		this.idstoricoutentetrasgressore = idstoricoutentetrasgressore;
	}


	public BigDecimal getIdtipoanagrafica() {
		return this.idtipoanagrafica;
	}

	public void setIdtipoanagrafica(BigDecimal idtipoanagrafica) {
		this.idtipoanagrafica = idtipoanagrafica;
	}


	public BigDecimal getImportoording() {
		return this.importoording;
	}

	public void setImportoording(BigDecimal importoording) {
		this.importoording = importoording;
	}


	public BigDecimal getImportorespingimento() {
		return this.importorespingimento;
	}

	public void setImportorespingimento(BigDecimal importorespingimento) {
		this.importorespingimento = importorespingimento;
	}


	public BigDecimal getImportoridotto() {
		return this.importoridotto;
	}

	public void setImportoridotto(BigDecimal importoridotto) {
		this.importoridotto = importoridotto;
	}


	public String getImportoridottopagato() {
		return this.importoridottopagato;
	}

	public void setImportoridottopagato(String importoridottopagato) {
		this.importoridottopagato = importoridottopagato;
	}


	public BigDecimal getImportototale() {
		return this.importototale;
	}

	public void setImportototale(BigDecimal importototale) {
		this.importototale = importototale;
	}


	public BigDecimal getImportoversato() {
		return this.importoversato;
	}

	public void setImportoversato(BigDecimal importoversato) {
		this.importoversato = importoversato;
	}


	public String getImpresainfmalprof() {
		return this.impresainfmalprof;
	}

	public void setImpresainfmalprof(String impresainfmalprof) {
		this.impresainfmalprof = impresainfmalprof;
	}


	public String getLuogosanzione() {
		return this.luogosanzione;
	}

	public void setLuogosanzione(String luogosanzione) {
		this.luogosanzione = luogosanzione;
	}


	public String getMetodopagamento() {
		return this.metodopagamento;
	}

	public void setMetodopagamento(String metodopagamento) {
		this.metodopagamento = metodopagamento;
	}


	public String getMotivoarchiviazione() {
		return this.motivoarchiviazione;
	}

	public void setMotivoarchiviazione(String motivoarchiviazione) {
		this.motivoarchiviazione = motivoarchiviazione;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNoteinfrazioni() {
		return this.noteinfrazioni;
	}

	public void setNoteinfrazioni(String noteinfrazioni) {
		this.noteinfrazioni = noteinfrazioni;
	}


	public BigDecimal getNumcup() {
		return this.numcup;
	}

	public void setNumcup(BigDecimal numcup) {
		this.numcup = numcup;
	}


	public String getNumerodocidentita() {
		return this.numerodocidentita;
	}

	public void setNumerodocidentita(String numerodocidentita) {
		this.numerodocidentita = numerodocidentita;
	}


	public BigDecimal getNumerorate() {
		return this.numerorate;
	}

	public void setNumerorate(BigDecimal numerorate) {
		this.numerorate = numerorate;
	}


	public BigDecimal getNumerosanz() {
		return this.numerosanz;
	}

	public void setNumerosanz(BigDecimal numerosanz) {
		this.numerosanz = numerosanz;
	}


	public String getNumeroverbale() {
		return this.numeroverbale;
	}

	public void setNumeroverbale(String numeroverbale) {
		this.numeroverbale = numeroverbale;
	}


	public String getNumording() {
		return this.numording;
	}

	public void setNumording(String numording) {
		this.numording = numording;
	}


	public String getNumprotocollo() {
		return this.numprotocollo;
	}

	public void setNumprotocollo(String numprotocollo) {
		this.numprotocollo = numprotocollo;
	}


	@Temporal(TemporalType.DATE)
	public Date getOracontestazione() {
		return this.oracontestazione;
	}

	public void setOracontestazione(Date oracontestazione) {
		this.oracontestazione = oracontestazione;
	}


	public String getPagata() {
		return this.pagata;
	}

	public void setPagata(String pagata) {
		this.pagata = pagata;
	}


	public BigDecimal getPercentualerate() {
		return this.percentualerate;
	}

	public void setPercentualerate(BigDecimal percentualerate) {
		this.percentualerate = percentualerate;
	}


	public String getProvenienza() {
		return this.provenienza;
	}

	public void setProvenienza(String provenienza) {
		this.provenienza = provenienza;
	}


	public String getRiferimentiruolo() {
		return this.riferimentiruolo;
	}

	public void setRiferimentiruolo(String riferimentiruolo) {
		this.riferimentiruolo = riferimentiruolo;
	}


	public String getRilasciantedocidentita() {
		return this.rilasciantedocidentita;
	}

	public void setRilasciantedocidentita(String rilasciantedocidentita) {
		this.rilasciantedocidentita = rilasciantedocidentita;
	}


	public BigDecimal getSpeseaggiuntive() {
		return this.speseaggiuntive;
	}

	public void setSpeseaggiuntive(BigDecimal speseaggiuntive) {
		this.speseaggiuntive = speseaggiuntive;
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


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	public BigDecimal getTipodocumentoidentita() {
		return this.tipodocumentoidentita;
	}

	public void setTipodocumentoidentita(BigDecimal tipodocumentoidentita) {
		this.tipodocumentoidentita = tipodocumentoidentita;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGO")
	public Sopralluoghidip getSopralluoghidip() {
		return this.sopralluoghidip;
	}

	public void setSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		this.sopralluoghidip = sopralluoghidip;
	}


	//bi-directional many-to-one association to Sanzioniarticoli
	@OneToMany(mappedBy="sanzioni")
	public List<Sanzioniarticoli> getSanzioniarticolis() {
		return this.sanzioniarticolis;
	}

	public void setSanzioniarticolis(List<Sanzioniarticoli> sanzioniarticolis) {
		this.sanzioniarticolis = sanzioniarticolis;
	}

	public Sanzioniarticoli addSanzioniarticoli(Sanzioniarticoli sanzioniarticoli) {
		getSanzioniarticolis().add(sanzioniarticoli);
		sanzioniarticoli.setSanzioni(this);

		return sanzioniarticoli;
	}

	public Sanzioniarticoli removeSanzioniarticoli(Sanzioniarticoli sanzioniarticoli) {
		getSanzioniarticolis().remove(sanzioniarticoli);
		sanzioniarticoli.setSanzioni(null);

		return sanzioniarticoli;
	}


	//bi-directional many-to-one association to Dlgs758iter
	@OneToMany(mappedBy="sanzioni")
	public List<Dlgs758iter> getDlgs758iters() {
		return this.dlgs758iters;
	}

	public void setDlgs758iters(List<Dlgs758iter> dlgs758iters) {
		this.dlgs758iters = dlgs758iters;
	}

	public Dlgs758iter addDlgs758iter(Dlgs758iter dlgs758iter) {
		getDlgs758iters().add(dlgs758iter);
		dlgs758iter.setSanzioni(this);

		return dlgs758iter;
	}

	public Dlgs758iter removeDlgs758iter(Dlgs758iter dlgs758iter) {
		getDlgs758iters().remove(dlgs758iter);
		dlgs758iter.setSanzioni(null);

		return dlgs758iter;
	}


	//bi-directional many-to-one association to Provvedimenti
	@OneToMany(mappedBy="sanzioni")
	public List<ProvvedimentiPrevnet> getProvvedimentis() {
		return this.provvedimentis;
	}

	public void setProvvedimentis(List<ProvvedimentiPrevnet> provvedimentis) {
		this.provvedimentis = provvedimentis;
	}

	public ProvvedimentiPrevnet addProvvedimenti(ProvvedimentiPrevnet provvedimenti) {
		getProvvedimentis().add(provvedimenti);
		provvedimenti.setSanzioni(this);

		return provvedimenti;
	}

	public ProvvedimentiPrevnet removeProvvedimenti(ProvvedimentiPrevnet provvedimenti) {
		getProvvedimentis().remove(provvedimenti);
		provvedimenti.setSanzioni(null);

		return provvedimenti;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getAnagrafiche1() {
		return this.anagrafiche1;
	}

	public void setAnagrafiche1(Anagrafiche anagrafiche1) {
		this.anagrafiche1 = anagrafiche1;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDENTE")
	public Anagrafiche getAnagrafiche4() {
		return this.anagrafiche4;
	}

	public void setAnagrafiche4(Anagrafiche anagrafiche4) {
		this.anagrafiche4 = anagrafiche4;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEDOCIDENTITA")
	public Comuni getComuni() {
		return this.comuni;
	}

	public void setComuni(Comuni comuni) {
		this.comuni = comuni;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Ditte getDitte1() {
		return this.ditte1;
	}

	public void setDitte1(Ditte ditte1) {
		this.ditte1 = ditte1;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDAENTE")
	public Ditte getDitte2() {
		return this.ditte2;
	}

	public void setDitte2(Ditte ditte2) {
		this.ditte2 = ditte2;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatori() {
		return this.operatori;
	}

	public void setOperatori(Operatori operatori) {
		this.operatori = operatori;
	}


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche procpratiche) {
		this.pratica = procpratiche;
	}


	//bi-directional many-to-one association to Provvedimenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVVEDIMENTO")
	public ProvvedimentiPrevnet getProvvedimenti() {
		return this.provvedimenti;
	}

	public void setProvvedimenti(ProvvedimentiPrevnet provvedimenti) {
		this.provvedimenti = provvedimenti;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZANOTIFICA")
	public Scadenze getScadenze1() {
		return this.scadenze1;
	}

	public void setScadenze1(Scadenze scadenze1) {
		this.scadenze1 = scadenze1;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZAOBLAZ")
	public Scadenze getScadenzaOblazione() {
		return this.scadenzaOblazione;
	}

	public void setScadenzaOblazione(Scadenze scadenzaOblazione) {
		this.scadenzaOblazione = scadenzaOblazione;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZAINGIUNZ")
	public Scadenze getScadenze3() {
		return this.scadenze3;
	}

	public void setScadenze3(Scadenze scadenze3) {
		this.scadenze3 = scadenze3;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZAINGIUNZPAGAM")
	public Scadenze getScadenze4() {
		return this.scadenze4;
	}

	public void setScadenze4(Scadenze scadenze4) {
		this.scadenze4 = scadenze4;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZACOMUNICAZIONE")
	public Scadenze getScadenze5() {
		return this.scadenze5;
	}

	public void setScadenze5(Scadenze scadenze5) {
		this.scadenze5 = scadenze5;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZARICHINGIUNZ")
	public Scadenze getScadenze6() {
		return this.scadenze6;
	}

	public void setScadenze6(Scadenze scadenze6) {
		this.scadenze6 = scadenze6;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOLOGIASANZIONE")
	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVINCIADOCIDENTITA")
	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOSANZIONE")
	public Tabelle getTabelle3() {
		return this.tabelle3;
	}

	public void setTabelle3(Tabelle tabelle3) {
		this.tabelle3 = tabelle3;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRUOLOTRASGHESSORE")
	public Tabelle getTabelle4() {
		return this.tabelle4;
	}

	public void setTabelle4(Tabelle tabelle4) {
		this.tabelle4 = tabelle4;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASL")
	public Tabelle getTabelle5() {
		return this.tabelle5;
	}

	public void setTabelle5(Tabelle tabelle5) {
		this.tabelle5 = tabelle5;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTATODOCIDENTITA")
	public Tabelle getTabelle6() {
		return this.tabelle6;
	}

	public void setTabelle6(Tabelle tabelle6) {
		this.tabelle6 = tabelle6;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="AUTCOMP")
	public Tabelle getTabelle7() {
		return this.tabelle7;
	}

	public void setTabelle7(Tabelle tabelle7) {
		this.tabelle7 = tabelle7;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDBOLLETTINO")
	public Tabelle getTabelle8() {
		return this.tabelle8;
	}

	public void setTabelle8(Tabelle tabelle8) {
		this.tabelle8 = tabelle8;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTRASGRESSORE")
	public Utenti getUtenti1() {
		return this.utenti1;
	}

	public void setUtenti1(Utenti utenti1) {
		this.utenti1 = utenti1;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOBBLIGATO")
	public Utenti getObbligatoInSolido() {
		return this.obbligatoInSolido;
	}

	public void setObbligatoInSolido(Utenti obbligatoInSolido) {
		this.obbligatoInSolido = obbligatoInSolido;
	}

}