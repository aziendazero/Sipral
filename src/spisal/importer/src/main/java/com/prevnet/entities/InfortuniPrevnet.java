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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the INFORTUNI database table.
 * 
 */
@Entity
@Table(name="infortuni")
public class InfortuniPrevnet implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idinfortuni;
	private BigDecimal annipermanenzastraniero;
	private String chkappalto;
	private String chkarchivsede;
	private String chkcollettivo;
	private String chkcolpalav;
	private String chkdecessolavoro;
	private String chkdecessoospedale;
	private String chkimmediata;
	private String chkinchiesta;
	private String chkinfocarente;
	private String chkinoltropm;
	private String chkinsufvigil;
	private String chkistruzformaz;
	private String chklesionicolpose;
	private String chkmisureprocedurali;
	private String chkmisuretecn;
	private String chkprognosiriservata;
	private String chkquerelaparteoffesa;
	private String chkresponabterzi;
	private String chkricovero;
	private String chkrispostainail;
	private String chkstatoinfortunio;
	private String chkverbale758;
	private String chkvigilanza;
	private String codicepat;
	private String correlazione;
	private String daanalizzare;
	private Date dataaffidamentoinc;
	private Date dataanalisi;
	private Date dataarchiviazione;
	private Date dataassunzione;
	private Date dataesitoinc;
	private Date datafineinchiesta;
	private Date datainf;
	private Date datainizioinchiesta;
	private Date datainoltro;
	private Date dataoradecesso;
	private Date datasegnalazione;
	private Date datasupplementoinc;
	private String descrevento;
	private String descrinf;
	private String diagnosips;
	private String disposizioni;
	private BigDecimal esitoitem;
	private BigDecimal gginvaliditatemp;
	private Tabelle condRischio;
	private Comuni luogoDecesso;
	private Comuni luogoInf;
	private BigDecimal idoperatoreanalisi;
	private BigDecimal idsirsap;
	private BigDecimal idsopralluogo;
	private BigDecimal idstoricoutenteinf;
	private String indirizzoinf;
	private String inviatoprocura;
	private String mansioneinf;
	private String note;
	private String noteforma;
	private String noteinchiesta;
	private String noteprognosi;
	private String numcasoinail;
	private String numfascicolo;
	private BigDecimal numpersonecoinvolte;
	private String optanzianitamansione;
	private String opttipoincidente;
	private BigDecimal oraordinale;
	private String percentualeinvalidita;
	private String procedibilita;
	private BigDecimal prognosiiniziale;
	private BigDecimal prognositot;
	private String repartoarealuogoinf;
	private String stradaaltro;
	private String straniero;
	private String tipoinchiesta;
	private String tipoluogoinf;
	private String ulterioriinfortunati;
	private Pratiche pratiche;
	private Tabelle permanenzaItalia;
	private Tabelle motivoArchiviazione;
	private Tabelle anzianitaMansione;
	private Tabelle attPrevalenteAzienda;
	private Tabelle inailTipoLuogo;
	private Tabelle inailTipoLavoro;
	private Tabelle inailAttFisica;
	private Tabelle inailAgenteAtt;
	private Tabelle inailDeviazione;
	private Tabelle inailAgenteDev;
	private Tabelle inailContatto;
	private Tabelle inailAgenteCon;
	private Tabelle tipoInfortunio;
	private Tabelle scolarita;
	private Tabelle forma;
	private Tabelle motivoProcedibile;
	private Tabelle classeAddetti;
	private Tabelle rapportoLavoro;
	private Tabelle comportamento;
	private Tabelle attSvoltaDuranteInf;
	private Tabelle motivoArchiv;
	private Tabelle qualificaLav;
	private Tabelle rapportoLav;
	private Tabelle mansioneInf;
	private Tabelle esitoInchiesta;
	private Tabelle esitoInfortunio;
	private Tabelle azioneIntrapresa;
	private Tabelle inailSede;
	private Tabelle inailTipo;
	private Tabelle inailAgente;
	private Tabelle segnalazione;
	private Tabelle inailModalita;
	private Infortunirelazioni infortunirelazioni;
	private List<Infortunitestimoni> infortunitestimonis;
	private List<Infortuniupg> infortuniupgs;
	private List<Prognosi> prognosis;
	private Anagrafiche procura;
	private Anagrafiche azienda;
	private Anagrafiche anagrLuogoInf;
	private Ditte ditta;
	private Ditte dittaLuogoInf;
	private Operatori operatore;
	private Utenti magistrato;
	private Utenti utenteInf;
	private Utenti indagato;
	private Agentimateriali agenteMateriale;
	private Agentimateriali contattoAmbiente;

	public InfortuniPrevnet() {
	}


	@Id
	public long getIdinfortuni() {
		return this.idinfortuni;
	}

	public void setIdinfortuni(long idinfortuni) {
		this.idinfortuni = idinfortuni;
	}


	public BigDecimal getAnnipermanenzastraniero() {
		return this.annipermanenzastraniero;
	}

	public void setAnnipermanenzastraniero(BigDecimal annipermanenzastraniero) {
		this.annipermanenzastraniero = annipermanenzastraniero;
	}


	public String getChkappalto() {
		return this.chkappalto;
	}

	public void setChkappalto(String chkappalto) {
		this.chkappalto = chkappalto;
	}


	public String getChkarchivsede() {
		return this.chkarchivsede;
	}

	public void setChkarchivsede(String chkarchivsede) {
		this.chkarchivsede = chkarchivsede;
	}


	public String getChkcollettivo() {
		return this.chkcollettivo;
	}

	public void setChkcollettivo(String chkcollettivo) {
		this.chkcollettivo = chkcollettivo;
	}


	public String getChkcolpalav() {
		return this.chkcolpalav;
	}

	public void setChkcolpalav(String chkcolpalav) {
		this.chkcolpalav = chkcolpalav;
	}


	public String getChkdecessolavoro() {
		return this.chkdecessolavoro;
	}

	public void setChkdecessolavoro(String chkdecessolavoro) {
		this.chkdecessolavoro = chkdecessolavoro;
	}


	public String getChkdecessoospedale() {
		return this.chkdecessoospedale;
	}

	public void setChkdecessoospedale(String chkdecessoospedale) {
		this.chkdecessoospedale = chkdecessoospedale;
	}


	public String getChkimmediata() {
		return this.chkimmediata;
	}

	public void setChkimmediata(String chkimmediata) {
		this.chkimmediata = chkimmediata;
	}


	public String getChkinchiesta() {
		return this.chkinchiesta;
	}

	public void setChkinchiesta(String chkinchiesta) {
		this.chkinchiesta = chkinchiesta;
	}


	public String getChkinfocarente() {
		return this.chkinfocarente;
	}

	public void setChkinfocarente(String chkinfocarente) {
		this.chkinfocarente = chkinfocarente;
	}


	public String getChkinoltropm() {
		return this.chkinoltropm;
	}

	public void setChkinoltropm(String chkinoltropm) {
		this.chkinoltropm = chkinoltropm;
	}


	public String getChkinsufvigil() {
		return this.chkinsufvigil;
	}

	public void setChkinsufvigil(String chkinsufvigil) {
		this.chkinsufvigil = chkinsufvigil;
	}


	public String getChkistruzformaz() {
		return this.chkistruzformaz;
	}

	public void setChkistruzformaz(String chkistruzformaz) {
		this.chkistruzformaz = chkistruzformaz;
	}


	public String getChklesionicolpose() {
		return this.chklesionicolpose;
	}

	public void setChklesionicolpose(String chklesionicolpose) {
		this.chklesionicolpose = chklesionicolpose;
	}


	public String getChkmisureprocedurali() {
		return this.chkmisureprocedurali;
	}

	public void setChkmisureprocedurali(String chkmisureprocedurali) {
		this.chkmisureprocedurali = chkmisureprocedurali;
	}


	public String getChkmisuretecn() {
		return this.chkmisuretecn;
	}

	public void setChkmisuretecn(String chkmisuretecn) {
		this.chkmisuretecn = chkmisuretecn;
	}


	public String getChkprognosiriservata() {
		return this.chkprognosiriservata;
	}

	public void setChkprognosiriservata(String chkprognosiriservata) {
		this.chkprognosiriservata = chkprognosiriservata;
	}


	public String getChkquerelaparteoffesa() {
		return this.chkquerelaparteoffesa;
	}

	public void setChkquerelaparteoffesa(String chkquerelaparteoffesa) {
		this.chkquerelaparteoffesa = chkquerelaparteoffesa;
	}


	public String getChkresponabterzi() {
		return this.chkresponabterzi;
	}

	public void setChkresponabterzi(String chkresponabterzi) {
		this.chkresponabterzi = chkresponabterzi;
	}


	public String getChkricovero() {
		return this.chkricovero;
	}

	public void setChkricovero(String chkricovero) {
		this.chkricovero = chkricovero;
	}


	public String getChkrispostainail() {
		return this.chkrispostainail;
	}

	public void setChkrispostainail(String chkrispostainail) {
		this.chkrispostainail = chkrispostainail;
	}


	public String getChkstatoinfortunio() {
		return this.chkstatoinfortunio;
	}

	public void setChkstatoinfortunio(String chkstatoinfortunio) {
		this.chkstatoinfortunio = chkstatoinfortunio;
	}


	public String getChkverbale758() {
		return this.chkverbale758;
	}

	public void setChkverbale758(String chkverbale758) {
		this.chkverbale758 = chkverbale758;
	}


	public String getChkvigilanza() {
		return this.chkvigilanza;
	}

	public void setChkvigilanza(String chkvigilanza) {
		this.chkvigilanza = chkvigilanza;
	}


	public String getCodicepat() {
		return this.codicepat;
	}

	public void setCodicepat(String codicepat) {
		this.codicepat = codicepat;
	}


	public String getCorrelazione() {
		return this.correlazione;
	}

	public void setCorrelazione(String correlazione) {
		this.correlazione = correlazione;
	}


	public String getDaanalizzare() {
		return this.daanalizzare;
	}

	public void setDaanalizzare(String daanalizzare) {
		this.daanalizzare = daanalizzare;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaffidamentoinc() {
		return this.dataaffidamentoinc;
	}

	public void setDataaffidamentoinc(Date dataaffidamentoinc) {
		this.dataaffidamentoinc = dataaffidamentoinc;
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
	public Date getDataassunzione() {
		return this.dataassunzione;
	}

	public void setDataassunzione(Date dataassunzione) {
		this.dataassunzione = dataassunzione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataesitoinc() {
		return this.dataesitoinc;
	}

	public void setDataesitoinc(Date dataesitoinc) {
		this.dataesitoinc = dataesitoinc;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafineinchiesta() {
		return this.datafineinchiesta;
	}

	public void setDatafineinchiesta(Date datafineinchiesta) {
		this.datafineinchiesta = datafineinchiesta;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getDatainf() {
		return this.datainf;
	}

	public void setDatainf(Date datainf) {
		this.datainf = datainf;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizioinchiesta() {
		return this.datainizioinchiesta;
	}

	public void setDatainizioinchiesta(Date datainizioinchiesta) {
		this.datainizioinchiesta = datainizioinchiesta;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainoltro() {
		return this.datainoltro;
	}

	public void setDatainoltro(Date datainoltro) {
		this.datainoltro = datainoltro;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataoradecesso() {
		return this.dataoradecesso;
	}

	public void setDataoradecesso(Date dataoradecesso) {
		this.dataoradecesso = dataoradecesso;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getDatasegnalazione() {
		return this.datasegnalazione;
	}

	public void setDatasegnalazione(Date datasegnalazione) {
		this.datasegnalazione = datasegnalazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatasupplementoinc() {
		return this.datasupplementoinc;
	}

	public void setDatasupplementoinc(Date datasupplementoinc) {
		this.datasupplementoinc = datasupplementoinc;
	}


	public String getDescrevento() {
		return this.descrevento;
	}

	public void setDescrevento(String descrevento) {
		this.descrevento = descrevento;
	}


	@Lob
	public String getDescrinf() {
		return this.descrinf;
	}

	public void setDescrinf(String descrinf) {
		this.descrinf = descrinf;
	}


	public String getDiagnosips() {
		return this.diagnosips;
	}

	public void setDiagnosips(String diagnosips) {
		this.diagnosips = diagnosips;
	}


	public String getDisposizioni() {
		return this.disposizioni;
	}

	public void setDisposizioni(String disposizioni) {
		this.disposizioni = disposizioni;
	}


	public BigDecimal getEsitoitem() {
		return this.esitoitem;
	}

	public void setEsitoitem(BigDecimal esitoitem) {
		this.esitoitem = esitoitem;
	}


	public BigDecimal getGginvaliditatemp() {
		return this.gginvaliditatemp;
	}

	public void setGginvaliditatemp(BigDecimal gginvaliditatemp) {
		this.gginvaliditatemp = gginvaliditatemp;
	}


	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCONDRISCHIO")
	public Tabelle getCondRischio() {
		return this.condRischio;
	}

	public void setCondRischio(Tabelle condRischio) {
		this.condRischio = condRischio;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLUOGODECESSO")
	public Comuni getLuogoDecesso() {
		return this.luogoDecesso;
	}

	public void setLuogoDecesso(Comuni luogoDecesso) {
		this.luogoDecesso = luogoDecesso;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLUOGOINF")
	public Comuni getLuogoInf() {
		return this.luogoInf;
	}

	public void setLuogoInf(Comuni luogoInf) {
		this.luogoInf = luogoInf;
	}


	public BigDecimal getIdoperatoreanalisi() {
		return this.idoperatoreanalisi;
	}

	public void setIdoperatoreanalisi(BigDecimal idoperatoreanalisi) {
		this.idoperatoreanalisi = idoperatoreanalisi;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}


	public BigDecimal getIdsopralluogo() {
		return this.idsopralluogo;
	}

	public void setIdsopralluogo(BigDecimal idsopralluogo) {
		this.idsopralluogo = idsopralluogo;
	}


	public BigDecimal getIdstoricoutenteinf() {
		return this.idstoricoutenteinf;
	}

	public void setIdstoricoutenteinf(BigDecimal idstoricoutenteinf) {
		this.idstoricoutenteinf = idstoricoutenteinf;
	}


	public String getIndirizzoinf() {
		return this.indirizzoinf;
	}

	public void setIndirizzoinf(String indirizzoinf) {
		this.indirizzoinf = indirizzoinf;
	}


	public String getInviatoprocura() {
		return this.inviatoprocura;
	}

	public void setInviatoprocura(String inviatoprocura) {
		this.inviatoprocura = inviatoprocura;
	}


	public String getMansioneinf() {
		return this.mansioneinf;
	}

	public void setMansioneinf(String mansioneinf) {
		this.mansioneinf = mansioneinf;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public void setNoteforma(String noteforma) {
		this.noteforma = noteforma;
	}


	public String getNoteinchiesta() {
		return this.noteinchiesta;
	}

	public void setNoteinchiesta(String noteinchiesta) {
		this.noteinchiesta = noteinchiesta;
	}


	public String getNoteprognosi() {
		return this.noteprognosi;
	}

	public void setNoteprognosi(String noteprognosi) {
		this.noteprognosi = noteprognosi;
	}


	public String getNumcasoinail() {
		return this.numcasoinail;
	}

	public void setNumcasoinail(String numcasoinail) {
		this.numcasoinail = numcasoinail;
	}


	public String getNumfascicolo() {
		return this.numfascicolo;
	}

	public void setNumfascicolo(String numfascicolo) {
		this.numfascicolo = numfascicolo;
	}


	public BigDecimal getNumpersonecoinvolte() {
		return this.numpersonecoinvolte;
	}

	public void setNumpersonecoinvolte(BigDecimal numpersonecoinvolte) {
		this.numpersonecoinvolte = numpersonecoinvolte;
	}


	public String getOptanzianitamansione() {
		return this.optanzianitamansione;
	}

	public void setOptanzianitamansione(String optanzianitamansione) {
		this.optanzianitamansione = optanzianitamansione;
	}


	public String getOpttipoincidente() {
		return this.opttipoincidente;
	}

	public void setOpttipoincidente(String opttipoincidente) {
		this.opttipoincidente = opttipoincidente;
	}


	public BigDecimal getOraordinale() {
		return this.oraordinale;
	}

	public void setOraordinale(BigDecimal oraordinale) {
		this.oraordinale = oraordinale;
	}


	public String getPercentualeinvalidita() {
		return this.percentualeinvalidita;
	}

	public void setPercentualeinvalidita(String percentualeinvalidita) {
		this.percentualeinvalidita = percentualeinvalidita;
	}


	public String getProcedibilita() {
		return this.procedibilita;
	}

	public void setProcedibilita(String procedibilita) {
		this.procedibilita = procedibilita;
	}


	public BigDecimal getPrognosiiniziale() {
		return this.prognosiiniziale;
	}

	public void setPrognosiiniziale(BigDecimal prognosiiniziale) {
		this.prognosiiniziale = prognosiiniziale;
	}


	public BigDecimal getPrognositot() {
		return this.prognositot;
	}

	public void setPrognositot(BigDecimal prognositot) {
		this.prognositot = prognositot;
	}


	public String getRepartoarealuogoinf() {
		return this.repartoarealuogoinf;
	}

	public void setRepartoarealuogoinf(String repartoarealuogoinf) {
		this.repartoarealuogoinf = repartoarealuogoinf;
	}


	public String getStradaaltro() {
		return this.stradaaltro;
	}

	public void setStradaaltro(String stradaaltro) {
		this.stradaaltro = stradaaltro;
	}


	public String getStraniero() {
		return this.straniero;
	}

	public void setStraniero(String straniero) {
		this.straniero = straniero;
	}


	public String getTipoinchiesta() {
		return this.tipoinchiesta;
	}

	public void setTipoinchiesta(String tipoinchiesta) {
		this.tipoinchiesta = tipoinchiesta;
	}


	public String getTipoluogoinf() {
		return this.tipoluogoinf;
	}

	public void setTipoluogoinf(String tipoluogoinf) {
		this.tipoluogoinf = tipoluogoinf;
	}


	public String getUlterioriinfortunati() {
		return this.ulterioriinfortunati;
	}

	public void setUlterioriinfortunati(String ulterioriinfortunati) {
		this.ulterioriinfortunati = ulterioriinfortunati;
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


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPERMANENZAITALIA")
	public Tabelle getPermanenzaItalia() {
		return this.permanenzaItalia;
	}

	public void setPermanenzaItalia(Tabelle permanenzaItalia) {
		this.permanenzaItalia = permanenzaItalia;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOARCHIVIAZIONE")
	public Tabelle getMotivoArchiviazione() {
		return this.motivoArchiviazione;
	}

	public void setMotivoArchiviazione(Tabelle motivoArchiviazione) {
		this.motivoArchiviazione = motivoArchiviazione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANZIANITAMANSIONE")
	public Tabelle getAnzianitaMansione() {
		return this.anzianitaMansione;
	}

	public void setAnzianitaMansione(Tabelle anzianitaMansione) {
		this.anzianitaMansione = anzianitaMansione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITAPREVALENTEAZIENDA")
	public Tabelle getAttPrevalenteAzienda() {
		return this.attPrevalenteAzienda;
	}

	public void setAttPrevalenteAzienda(Tabelle attPrevalenteAzienda) {
		this.attPrevalenteAzienda = attPrevalenteAzienda;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILTIPOLUOGO")
	public Tabelle getInailTipoLuogo() {
		return this.inailTipoLuogo;
	}

	public void setInailTipoLuogo(Tabelle inailTipoLuogo) {
		this.inailTipoLuogo = inailTipoLuogo;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILTIPOLAVORO")
	public Tabelle getInailTipoLavoro() {
		return this.inailTipoLavoro;
	}

	public void setInailTipoLavoro(Tabelle inailTipoLavoro) {
		this.inailTipoLavoro = inailTipoLavoro;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILATTFISICA")
	public Tabelle getInailAttFisica() {
		return this.inailAttFisica;
	}

	public void setInailAttFisica(Tabelle inailAttFisica) {
		this.inailAttFisica = inailAttFisica;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILAGENTEATT")
	public Tabelle getInailAgenteAtt() {
		return this.inailAgenteAtt;
	}

	public void setInailAgenteAtt(Tabelle inailAgenteAtt) {
		this.inailAgenteAtt = inailAgenteAtt;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILDEVIAZIONE")
	public Tabelle getInailDeviazione() {
		return this.inailDeviazione;
	}

	public void setInailDeviazione(Tabelle inailDeviazione) {
		this.inailDeviazione = inailDeviazione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILAGENTEDEV")
	public Tabelle getInailAgenteDev() {
		return this.inailAgenteDev;
	}

	public void setInailAgenteDev(Tabelle inailAgenteDev) {
		this.inailAgenteDev = inailAgenteDev;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILCONTATTO")
	public Tabelle getInailContatto() {
		return this.inailContatto;
	}

	public void setInailContatto(Tabelle inailContatto) {
		this.inailContatto = inailContatto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILAGENTECON")
	public Tabelle getInailAgenteCon() {
		return this.inailAgenteCon;
	}

	public void setInailAgenteCon(Tabelle inailAgenteCon) {
		this.inailAgenteCon = inailAgenteCon;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOINFORTUNIO")
	public Tabelle getTipoInfortunio() {
		return this.tipoInfortunio;
	}

	public void setTipoInfortunio(Tabelle tipoInfortunio) {
		this.tipoInfortunio = tipoInfortunio;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCOLARITA")
	public Tabelle getScolarita() {
		return this.scolarita;
	}

	public void setScolarita(Tabelle scolarita) {
		this.scolarita = scolarita;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDFORMA")
	public Tabelle getForma() {
		return this.forma;
	}

	public void setForma(Tabelle forma) {
		this.forma = forma;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOPROCEDIBILE")
	public Tabelle getMotivoProcedibile() {
		return this.motivoProcedibile;
	}

	public void setMotivoProcedibile(Tabelle motivoProcedibile) {
		this.motivoProcedibile = motivoProcedibile;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCLASSEADDETTI")
	public Tabelle getClasseAddetti() {
		return this.classeAddetti;
	}

	public void setClasseAddetti(Tabelle classeAddetti) {
		this.classeAddetti = classeAddetti;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRAPPORTOLAVORO")
	public Tabelle getRapportoLavoro() {
		return this.rapportoLavoro;
	}

	public void setRapportoLavoro(Tabelle rapportoLavoro) {
		this.rapportoLavoro = rapportoLavoro;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMPORTAMENTO")
	public Tabelle getComportamento() {
		return this.comportamento;
	}

	public void setComportamento(Tabelle comportamento) {
		this.comportamento = comportamento;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVSVOLTADURANTEINF")
	public Tabelle getAttSvoltaDuranteInf() {
		return this.attSvoltaDuranteInf;
	}

	public void setAttSvoltaDuranteInf(Tabelle attSvoltaDuranteInf) {
		this.attSvoltaDuranteInf = attSvoltaDuranteInf;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOARCHIV")
	public Tabelle getMotivoArchiv() {
		return this.motivoArchiv;
	}

	public void setMotivoArchiv(Tabelle motivoArchiv) {
		this.motivoArchiv = motivoArchiv;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDQUALIFICALAV")
	public Tabelle getQualificaLav() {
		return this.qualificaLav;
	}

	public void setQualificaLav(Tabelle qualificaLav) {
		this.qualificaLav = qualificaLav;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRAPPORTOLAV")
	public Tabelle getRapportoLav() {
		return this.rapportoLav;
	}

	public void setRapportoLav(Tabelle rapportoLav) {
		this.rapportoLav = rapportoLav;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMANSIONEINF")
	public Tabelle getMansioneInf() {
		return this.mansioneInf;
	}

	public void setMansioneInf(Tabelle mansioneInf) {
		this.mansioneInf = mansioneInf;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITOINCHIESTA")
	public Tabelle getEsitoInchiesta() {
		return this.esitoInchiesta;
	}

	public void setEsitoInchiesta(Tabelle esitoInchiesta) {
		this.esitoInchiesta = esitoInchiesta;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITOINFORTUNIO")
	public Tabelle getEsitoInfortunio() {
		return this.esitoInfortunio;
	}

	public void setEsitoInfortunio(Tabelle esitoInfortunio) {
		this.esitoInfortunio = esitoInfortunio;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAZIONEINTRAPRESA")
	public Tabelle getAzioneIntrapresa() {
		return this.azioneIntrapresa;
	}

	public void setAzioneIntrapresa(Tabelle azioneIntrapresa) {
		this.azioneIntrapresa = azioneIntrapresa;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILSEDE")
	public Tabelle getInailSede() {
		return this.inailSede;
	}

	public void setInailSede(Tabelle inailSede) {
		this.inailSede = inailSede;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILTIPO")
	public Tabelle getInailTipo() {
		return this.inailTipo;
	}

	public void setInailTipo(Tabelle inailTipo) {
		this.inailTipo = inailTipo;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILAGENTE")
	public Tabelle getInailAgente() {
		return this.inailAgente;
	}

	public void setInailAgente(Tabelle inailAgente) {
		this.inailAgente = inailAgente;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSEGNALAZIONE")
	public Tabelle getSegnalazione() {
		return this.segnalazione;
	}

	public void setSegnalazione(Tabelle segnalazione) {
		this.segnalazione = segnalazione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILMODALITA")
	public Tabelle getInailModalita() {
		return this.inailModalita;
	}

	public void setInailModalita(Tabelle inailModalita) {
		this.inailModalita = inailModalita;
	}


	//bi-directional one-to-one association to Infortunirelazioni
	@OneToOne(mappedBy="infortuniPrevnet", fetch=FetchType.LAZY)
	public Infortunirelazioni getInfortunirelazioni() {
		return this.infortunirelazioni;
	}

	public void setInfortunirelazioni(Infortunirelazioni infortunirelazioni) {
		this.infortunirelazioni = infortunirelazioni;
	}


	//bi-directional many-to-one association to Infortunitestimoni
	@OneToMany(mappedBy="infortuniPrevnet")
	public List<Infortunitestimoni> getInfortunitestimonis() {
		return this.infortunitestimonis;
	}

	public void setInfortunitestimonis(List<Infortunitestimoni> infortunitestimonis) {
		this.infortunitestimonis = infortunitestimonis;
	}

	public Infortunitestimoni addInfortunitestimoni(Infortunitestimoni infortunitestimoni) {
		getInfortunitestimonis().add(infortunitestimoni);
		infortunitestimoni.setInfortuniPrevnet(this);

		return infortunitestimoni;
	}

	public Infortunitestimoni removeInfortunitestimoni(Infortunitestimoni infortunitestimoni) {
		getInfortunitestimonis().remove(infortunitestimoni);
		infortunitestimoni.setInfortuniPrevnet(null);

		return infortunitestimoni;
	}


	//bi-directional many-to-one association to Infortuniupg
	@OneToMany(mappedBy="infortuniPrevnet")
	public List<Infortuniupg> getInfortuniupgs() {
		return this.infortuniupgs;
	}

	public void setInfortuniupgs(List<Infortuniupg> infortuniupgs) {
		this.infortuniupgs = infortuniupgs;
	}

	public Infortuniupg addInfortuniupg(Infortuniupg infortuniupg) {
		getInfortuniupgs().add(infortuniupg);
		infortuniupg.setInfortuniPrevnet(this);

		return infortuniupg;
	}

	public Infortuniupg removeInfortuniupg(Infortuniupg infortuniupg) {
		getInfortuniupgs().remove(infortuniupg);
		infortuniupg.setInfortuniPrevnet(null);

		return infortuniupg;
	}


	//bi-directional many-to-one association to Prognosi
	@OneToMany(mappedBy="infortuniPrevnet")
	@OrderBy("idprognosi ASC")
	public List<Prognosi> getPrognosis() {
		return this.prognosis;
	}

	public void setPrognosis(List<Prognosi> prognosis) {
		this.prognosis = prognosis;
	}

	public Prognosi addPrognosi(Prognosi prognosi) {
		getPrognosis().add(prognosi);
		prognosi.setInfortuniPrevnet(this);

		return prognosi;
	}

	public Prognosi removePrognosi(Prognosi prognosi) {
		getPrognosis().remove(prognosi);
		prognosi.setInfortuniPrevnet(null);

		return prognosi;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCURA")
	public Anagrafiche getProcura() {
		return this.procura;
	}

	public void setProcura(Anagrafiche procura) {
		this.procura = procura;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAZIENDA")
	public Anagrafiche getAzienda() {
		return this.azienda;
	}

	public void setAzienda(Anagrafiche azienda) {
		this.azienda = azienda;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRLUOGOINF")
	public Anagrafiche getAnagrLuogoInf() {
		return this.anagrLuogoInf;
	}

	public void setAnagrLuogoInf(Anagrafiche anagrLuogoInf) {
		this.anagrLuogoInf = anagrLuogoInf;
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


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTALUOGOINF")
	public Ditte getDittaLuogoInf() {
		return this.dittaLuogoInf;
	}

	public void setDittaLuogoInf(Ditte dittaLuogoInf) {
		this.dittaLuogoInf = dittaLuogoInf;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatore() {
		return this.operatore;
	}

	public void setOperatore(Operatori operatore) {
		this.operatore = operatore;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMAGISTRATO")
	public Utenti getMagistrato() {
		return this.magistrato;
	}

	public void setMagistrato(Utenti magistrato) {
		this.magistrato = magistrato;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUTENTEINF")
	public Utenti getUtenteInf() {
		return this.utenteInf;
	}

	public void setUtenteInf(Utenti utenteInf) {
		this.utenteInf = utenteInf;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINDAGATO")
	public Utenti getIndagato() {
		return this.indagato;
	}

	public void setIndagato(Utenti indagato) {
		this.indagato = indagato;
	}
	
	//bi-directional many-to-one association to Agentimateriali
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILAGENTEMATERIALE")
	public Agentimateriali getAgenteMateriale() {
		return this.agenteMateriale;
	}

	public void setAgenteMateriale(Agentimateriali agenteMateriale) {
		this.agenteMateriale = agenteMateriale;
	}


	//bi-directional many-to-one association to Agentimateriali
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINAILCONTATTOAMBIENTE")
	public Agentimateriali getContattoAmbiente() {
		return this.contattoAmbiente;
	}

	public void setContattoAmbiente(Agentimateriali contattoAmbiente) {
		this.contattoAmbiente = contattoAmbiente;
	}

}