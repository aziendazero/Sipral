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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the SOPRALLUOGHIDIP database table.
 * 
 */
@Entity
public class Sopralluoghidip implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idsopralluoghidip;
	private BigDecimal annoconferimento;
	private BigDecimal annosopralluogo;
	private BigDecimal chiavesid;
	private String chkdestnonpresente;
	private String chkdocsicurezzapresente;
	private String chkdocsicurezzavalutato;
	private String chkpimus;
	private String chkpsicurezzapresente;
	private String chkpsicurezzavalutato;
	private Date dataentro;
	private Date datafine;
	private Date dataregistrazione;
	private Date datasopralluogo;
	private String dirsanitari;
	private String edilizia;
	private String effettuato;
	private String exportimpresa;
	private BigDecimal giorniscad;
	private BigDecimal idallevamento;
	private BigDecimal idallevamentoub;
	private BigDecimal idapg;
	private BigDecimal idautofunebri;
	private BigDecimal idautomezzi;
	private BigDecimal idcoloniefeline;
	private BigDecimal idimpianto;
	private BigDecimal idprocedura;
	private BigDecimal idpuntoprelievo;
	private BigDecimal idsettore;
	private BigDecimal idsicer;
	private BigDecimal idsirsap;
	private BigDecimal idsottostruttura;
	private BigDecimal idstoricoutente;
	private BigDecimal idstoricoutentepersonapresente;
	private BigDecimal idstoricoutenteubic;
	private BigDecimal idstruttura;
	private BigDecimal idufficio;
	private BigDecimal idverificheimp;
	private String import_;
	private BigDecimal minutiattivita;
	private BigDecimal nlavssl;
	private String nominacoordinatori;
	private String nominarespdibordo;
	private String note;
	private String noteattivitasvolta;
	private String notelocalizzato;
	private BigDecimal numconferimento;
	private BigDecimal numlavstranieri;
	private BigDecimal numlavvalutati;
	private BigDecimal numsopralluogo;
	private String numverbale;
	private String optinfrrilev;
	private String optssl;
	private Date orafineattivita;
	private Date orafinevisita;
	private Date oravisita;
	private BigDecimal orsaflag;
	private String riepilogosop;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private BigDecimal tipoAutomezzi;
	private String tipoanagrafica;
	private String tipovisionato;
	private String totpersonepresenti;
	private String ulteriorisopralluoghi;
	private String verbaleredatto;
	private List<Altridatipratica> altridatipraticas;
	private List<Attipolgiudsopralluoghi> attipolgiudsopralluoghis;
	private List<Ditteedilizia> ditteedilizias;
	private List<Misureambientali> misureambientalis;
	private List<Operatorisopralluoghi> operatorisopralluoghis;
	private List<Personapresentesop> personapresentesops;
	private List<Sitsopralluoghi> sitsopralluoghis;
	private List<Sanzioni> sanzionis;
	private Anagrafiche ricevente;
	private Anagrafiche visionato;
	private Anagrafiche anagrafica;
	private AttivitaPrevnet attivita;
	private Cantieri cantiere;
	private Comuni ubicComune;
	private Ditte ditta;
	private Ditte ubicDitta;
	private Localita ubicLocalita;
	private Pratiche pratiche;
	private Sopralluoghidip sopralluoghidip;
	private List<Sopralluoghidip> sopralluoghidips;
	private Tabelle valssl;
	private Tabelle manutenzione;
	private Tabelle asl;
	private Tabelle tipoEmergenza;
	private Tabelle esito;
	private Tabelle antincendio;
	private Tabelle sicurezza;
	private Tabelle igiene;
	private Tabelle valPimus;
	private Tabelle valDocSicurezza;
	private Tabelle valPianoSicurezza;
	private Tabelle opera;
	private Tabelle tipoStruttura;
	private Tabelle personaPresenteRuolo;
	private Tabelle tipoInterventoSop;
	private Utenti personaPresente;
	private List<Sopralluoghiinfrazioni> sopralluoghiinfrazionis;
	private Motivisopralluoghi motivo;
	private Motivisopralluoghi tipoMotivo;
	private List<ProvvedimentiPrevnet> provvedimenti;
	private List<Scadenze> scadenzes;
	private Scadenze scadenzaSoprVerifica;
	private Scadenze scadenzaSoprProgr;
	private Scadenze scadenzaData;
	
	public Sopralluoghidip() {
	}


	@Id
	public long getIdsopralluoghidip() {
		return this.idsopralluoghidip;
	}

	public void setIdsopralluoghidip(long idsopralluoghidip) {
		this.idsopralluoghidip = idsopralluoghidip;
	}


	public BigDecimal getAnnoconferimento() {
		return this.annoconferimento;
	}

	public void setAnnoconferimento(BigDecimal annoconferimento) {
		this.annoconferimento = annoconferimento;
	}


	public BigDecimal getAnnosopralluogo() {
		return this.annosopralluogo;
	}

	public void setAnnosopralluogo(BigDecimal annosopralluogo) {
		this.annosopralluogo = annosopralluogo;
	}


	public BigDecimal getChiavesid() {
		return this.chiavesid;
	}

	public void setChiavesid(BigDecimal chiavesid) {
		this.chiavesid = chiavesid;
	}


	public String getChkdestnonpresente() {
		return this.chkdestnonpresente;
	}

	public void setChkdestnonpresente(String chkdestnonpresente) {
		this.chkdestnonpresente = chkdestnonpresente;
	}


	public String getChkdocsicurezzapresente() {
		return this.chkdocsicurezzapresente;
	}

	public void setChkdocsicurezzapresente(String chkdocsicurezzapresente) {
		this.chkdocsicurezzapresente = chkdocsicurezzapresente;
	}


	public String getChkdocsicurezzavalutato() {
		return this.chkdocsicurezzavalutato;
	}

	public void setChkdocsicurezzavalutato(String chkdocsicurezzavalutato) {
		this.chkdocsicurezzavalutato = chkdocsicurezzavalutato;
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


	public String getChkpsicurezzavalutato() {
		return this.chkpsicurezzavalutato;
	}

	public void setChkpsicurezzavalutato(String chkpsicurezzavalutato) {
		this.chkpsicurezzavalutato = chkpsicurezzavalutato;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataentro() {
		return this.dataentro;
	}

	public void setDataentro(Date dataentro) {
		this.dataentro = dataentro;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafine() {
		return this.datafine;
	}

	public void setDatafine(Date datafine) {
		this.datafine = datafine;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataregistrazione() {
		return this.dataregistrazione;
	}

	public void setDataregistrazione(Date dataregistrazione) {
		this.dataregistrazione = dataregistrazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatasopralluogo() {
		return this.datasopralluogo;
	}

	public void setDatasopralluogo(Date datasopralluogo) {
		this.datasopralluogo = datasopralluogo;
	}


	public String getDirsanitari() {
		return this.dirsanitari;
	}

	public void setDirsanitari(String dirsanitari) {
		this.dirsanitari = dirsanitari;
	}


	public String getEdilizia() {
		return this.edilizia;
	}

	public void setEdilizia(String edilizia) {
		this.edilizia = edilizia;
	}


	public String getEffettuato() {
		return this.effettuato;
	}

	public void setEffettuato(String effettuato) {
		this.effettuato = effettuato;
	}


	public String getExportimpresa() {
		return this.exportimpresa;
	}

	public void setExportimpresa(String exportimpresa) {
		this.exportimpresa = exportimpresa;
	}


	public BigDecimal getGiorniscad() {
		return this.giorniscad;
	}

	public void setGiorniscad(BigDecimal giorniscad) {
		this.giorniscad = giorniscad;
	}


	public BigDecimal getIdallevamento() {
		return this.idallevamento;
	}

	public void setIdallevamento(BigDecimal idallevamento) {
		this.idallevamento = idallevamento;
	}


	public BigDecimal getIdallevamentoub() {
		return this.idallevamentoub;
	}

	public void setIdallevamentoub(BigDecimal idallevamentoub) {
		this.idallevamentoub = idallevamentoub;
	}


	public BigDecimal getIdapg() {
		return this.idapg;
	}

	public void setIdapg(BigDecimal idapg) {
		this.idapg = idapg;
	}


	public BigDecimal getIdautofunebri() {
		return this.idautofunebri;
	}

	public void setIdautofunebri(BigDecimal idautofunebri) {
		this.idautofunebri = idautofunebri;
	}


	public BigDecimal getIdautomezzi() {
		return this.idautomezzi;
	}

	public void setIdautomezzi(BigDecimal idautomezzi) {
		this.idautomezzi = idautomezzi;
	}


	public BigDecimal getIdcoloniefeline() {
		return this.idcoloniefeline;
	}

	public void setIdcoloniefeline(BigDecimal idcoloniefeline) {
		this.idcoloniefeline = idcoloniefeline;
	}

	public BigDecimal getIdimpianto() {
		return this.idimpianto;
	}

	public void setIdimpianto(BigDecimal idimpianto) {
		this.idimpianto = idimpianto;
	}


	public BigDecimal getIdprocedura() {
		return this.idprocedura;
	}

	public void setIdprocedura(BigDecimal idprocedura) {
		this.idprocedura = idprocedura;
	}


	public BigDecimal getIdpuntoprelievo() {
		return this.idpuntoprelievo;
	}

	public void setIdpuntoprelievo(BigDecimal idpuntoprelievo) {
		this.idpuntoprelievo = idpuntoprelievo;
	}

	public BigDecimal getIdsettore() {
		return this.idsettore;
	}

	public void setIdsettore(BigDecimal idsettore) {
		this.idsettore = idsettore;
	}


	public BigDecimal getIdsicer() {
		return this.idsicer;
	}

	public void setIdsicer(BigDecimal idsicer) {
		this.idsicer = idsicer;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}


	public BigDecimal getIdsottostruttura() {
		return this.idsottostruttura;
	}

	public void setIdsottostruttura(BigDecimal idsottostruttura) {
		this.idsottostruttura = idsottostruttura;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public BigDecimal getIdstoricoutentepersonapresente() {
		return this.idstoricoutentepersonapresente;
	}

	public void setIdstoricoutentepersonapresente(BigDecimal idstoricoutentepersonapresente) {
		this.idstoricoutentepersonapresente = idstoricoutentepersonapresente;
	}


	public BigDecimal getIdstoricoutenteubic() {
		return this.idstoricoutenteubic;
	}

	public void setIdstoricoutenteubic(BigDecimal idstoricoutenteubic) {
		this.idstoricoutenteubic = idstoricoutenteubic;
	}


	public BigDecimal getIdstruttura() {
		return this.idstruttura;
	}

	public void setIdstruttura(BigDecimal idstruttura) {
		this.idstruttura = idstruttura;
	}


	public BigDecimal getIdufficio() {
		return this.idufficio;
	}

	public void setIdufficio(BigDecimal idufficio) {
		this.idufficio = idufficio;
	}


	public BigDecimal getIdverificheimp() {
		return this.idverificheimp;
	}

	public void setIdverificheimp(BigDecimal idverificheimp) {
		this.idverificheimp = idverificheimp;
	}


	@Column(name="\"IMPORT\"")
	public String getImport_() {
		return this.import_;
	}

	public void setImport_(String import_) {
		this.import_ = import_;
	}


	public BigDecimal getMinutiattivita() {
		return this.minutiattivita;
	}

	public void setMinutiattivita(BigDecimal minutiattivita) {
		this.minutiattivita = minutiattivita;
	}


	public BigDecimal getNlavssl() {
		return this.nlavssl;
	}

	public void setNlavssl(BigDecimal nlavssl) {
		this.nlavssl = nlavssl;
	}


	public String getNominacoordinatori() {
		return this.nominacoordinatori;
	}

	public void setNominacoordinatori(String nominacoordinatori) {
		this.nominacoordinatori = nominacoordinatori;
	}


	public String getNominarespdibordo() {
		return this.nominarespdibordo;
	}

	public void setNominarespdibordo(String nominarespdibordo) {
		this.nominarespdibordo = nominarespdibordo;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNoteattivitasvolta() {
		return this.noteattivitasvolta;
	}

	public void setNoteattivitasvolta(String noteattivitasvolta) {
		this.noteattivitasvolta = noteattivitasvolta;
	}


	public String getNotelocalizzato() {
		return this.notelocalizzato;
	}

	public void setNotelocalizzato(String notelocalizzato) {
		this.notelocalizzato = notelocalizzato;
	}


	public BigDecimal getNumconferimento() {
		return this.numconferimento;
	}

	public void setNumconferimento(BigDecimal numconferimento) {
		this.numconferimento = numconferimento;
	}


	public BigDecimal getNumlavstranieri() {
		return this.numlavstranieri;
	}

	public void setNumlavstranieri(BigDecimal numlavstranieri) {
		this.numlavstranieri = numlavstranieri;
	}


	public BigDecimal getNumlavvalutati() {
		return this.numlavvalutati;
	}

	public void setNumlavvalutati(BigDecimal numlavvalutati) {
		this.numlavvalutati = numlavvalutati;
	}


	public BigDecimal getNumsopralluogo() {
		return this.numsopralluogo;
	}

	public void setNumsopralluogo(BigDecimal numsopralluogo) {
		this.numsopralluogo = numsopralluogo;
	}


	public String getNumverbale() {
		return this.numverbale;
	}

	public void setNumverbale(String numverbale) {
		this.numverbale = numverbale;
	}


	public String getOptinfrrilev() {
		return this.optinfrrilev;
	}

	public void setOptinfrrilev(String optinfrrilev) {
		this.optinfrrilev = optinfrrilev;
	}


	public String getOptssl() {
		return this.optssl;
	}

	public void setOptssl(String optssl) {
		this.optssl = optssl;
	}


	@Temporal(TemporalType.DATE)
	public Date getOrafineattivita() {
		return this.orafineattivita;
	}

	public void setOrafineattivita(Date orafineattivita) {
		this.orafineattivita = orafineattivita;
	}


	@Temporal(TemporalType.DATE)
	public Date getOrafinevisita() {
		return this.orafinevisita;
	}

	public void setOrafinevisita(Date orafinevisita) {
		this.orafinevisita = orafinevisita;
	}


	@Temporal(TemporalType.DATE)
	public Date getOravisita() {
		return this.oravisita;
	}

	public void setOravisita(Date oravisita) {
		this.oravisita = oravisita;
	}


	public BigDecimal getOrsaflag() {
		return this.orsaflag;
	}

	public void setOrsaflag(BigDecimal orsaflag) {
		this.orsaflag = orsaflag;
	}


	public String getRiepilogosop() {
		return this.riepilogosop;
	}

	public void setRiepilogosop(String riepilogosop) {
		this.riepilogosop = riepilogosop;
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


	@Column(name="TIPO_AUTOMEZZI")
	public BigDecimal getTipoAutomezzi() {
		return this.tipoAutomezzi;
	}

	public void setTipoAutomezzi(BigDecimal tipoAutomezzi) {
		this.tipoAutomezzi = tipoAutomezzi;
	}


	public String getTipoanagrafica() {
		return this.tipoanagrafica;
	}

	public void setTipoanagrafica(String tipoanagrafica) {
		this.tipoanagrafica = tipoanagrafica;
	}


	public String getTipovisionato() {
		return this.tipovisionato;
	}

	public void setTipovisionato(String tipovisionato) {
		this.tipovisionato = tipovisionato;
	}


	public String getTotpersonepresenti() {
		return this.totpersonepresenti;
	}

	public void setTotpersonepresenti(String totpersonepresenti) {
		this.totpersonepresenti = totpersonepresenti;
	}


	public String getUlteriorisopralluoghi() {
		return this.ulteriorisopralluoghi;
	}

	public void setUlteriorisopralluoghi(String ulteriorisopralluoghi) {
		this.ulteriorisopralluoghi = ulteriorisopralluoghi;
	}


	public String getVerbaleredatto() {
		return this.verbaleredatto;
	}

	public void setVerbaleredatto(String verbaleredatto) {
		this.verbaleredatto = verbaleredatto;
	}


	//bi-directional many-to-one association to Altridatipratica
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Altridatipratica> getAltridatipraticas() {
		return this.altridatipraticas;
	}

	public void setAltridatipraticas(List<Altridatipratica> altridatipraticas) {
		this.altridatipraticas = altridatipraticas;
	}

	public Altridatipratica addAltridatipratica(Altridatipratica altridatipratica) {
		getAltridatipraticas().add(altridatipratica);
		altridatipratica.setSopralluoghidip(this);

		return altridatipratica;
	}

	public Altridatipratica removeAltridatipratica(Altridatipratica altridatipratica) {
		getAltridatipraticas().remove(altridatipratica);
		altridatipratica.setSopralluoghidip(null);

		return altridatipratica;
	}


	//bi-directional many-to-one association to Attipolgiudsopralluoghi
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Attipolgiudsopralluoghi> getAttipolgiudsopralluoghis() {
		return this.attipolgiudsopralluoghis;
	}

	public void setAttipolgiudsopralluoghis(List<Attipolgiudsopralluoghi> attipolgiudsopralluoghis) {
		this.attipolgiudsopralluoghis = attipolgiudsopralluoghis;
	}

	public Attipolgiudsopralluoghi addAttipolgiudsopralluoghi(Attipolgiudsopralluoghi attipolgiudsopralluoghi) {
		getAttipolgiudsopralluoghis().add(attipolgiudsopralluoghi);
		attipolgiudsopralluoghi.setSopralluoghidip(this);

		return attipolgiudsopralluoghi;
	}

	public Attipolgiudsopralluoghi removeAttipolgiudsopralluoghi(Attipolgiudsopralluoghi attipolgiudsopralluoghi) {
		getAttipolgiudsopralluoghis().remove(attipolgiudsopralluoghi);
		attipolgiudsopralluoghi.setSopralluoghidip(null);

		return attipolgiudsopralluoghi;
	}


	//bi-directional many-to-one association to Ditteedilizia
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Ditteedilizia> getDitteedilizias() {
		return this.ditteedilizias;
	}

	public void setDitteedilizias(List<Ditteedilizia> ditteedilizias) {
		this.ditteedilizias = ditteedilizias;
	}

	public Ditteedilizia addDitteedilizia(Ditteedilizia ditteedilizia) {
		getDitteedilizias().add(ditteedilizia);
		ditteedilizia.setSopralluoghidip(this);

		return ditteedilizia;
	}

	public Ditteedilizia removeDitteedilizia(Ditteedilizia ditteedilizia) {
		getDitteedilizias().remove(ditteedilizia);
		ditteedilizia.setSopralluoghidip(null);

		return ditteedilizia;
	}


	//bi-directional many-to-one association to Misureambientali
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Misureambientali> getMisureambientalis() {
		return this.misureambientalis;
	}

	public void setMisureambientalis(List<Misureambientali> misureambientalis) {
		this.misureambientalis = misureambientalis;
	}

	public Misureambientali addMisureambientali(Misureambientali misureambientali) {
		getMisureambientalis().add(misureambientali);
		misureambientali.setSopralluoghidip(this);

		return misureambientali;
	}

	public Misureambientali removeMisureambientali(Misureambientali misureambientali) {
		getMisureambientalis().remove(misureambientali);
		misureambientali.setSopralluoghidip(null);

		return misureambientali;
	}


	//bi-directional many-to-one association to Operatorisopralluoghi
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Operatorisopralluoghi> getOperatorisopralluoghis() {
		return this.operatorisopralluoghis;
	}

	public void setOperatorisopralluoghis(List<Operatorisopralluoghi> operatorisopralluoghis) {
		this.operatorisopralluoghis = operatorisopralluoghis;
	}

	public Operatorisopralluoghi addOperatorisopralluoghi(Operatorisopralluoghi operatorisopralluoghi) {
		getOperatorisopralluoghis().add(operatorisopralluoghi);
		operatorisopralluoghi.setSopralluoghidip(this);

		return operatorisopralluoghi;
	}

	public Operatorisopralluoghi removeOperatorisopralluoghi(Operatorisopralluoghi operatorisopralluoghi) {
		getOperatorisopralluoghis().remove(operatorisopralluoghi);
		operatorisopralluoghi.setSopralluoghidip(null);

		return operatorisopralluoghi;
	}


	//bi-directional many-to-one association to Personapresentesop
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Personapresentesop> getPersonapresentesops() {
		return this.personapresentesops;
	}

	public void setPersonapresentesops(List<Personapresentesop> personapresentesops) {
		this.personapresentesops = personapresentesops;
	}

	public Personapresentesop addPersonapresentesop(Personapresentesop personapresentesop) {
		getPersonapresentesops().add(personapresentesop);
		personapresentesop.setSopralluoghidip(this);

		return personapresentesop;
	}

	public Personapresentesop removePersonapresentesop(Personapresentesop personapresentesop) {
		getPersonapresentesops().remove(personapresentesop);
		personapresentesop.setSopralluoghidip(null);

		return personapresentesop;
	}


	//bi-directional many-to-one association to Sitsopralluoghi
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Sitsopralluoghi> getSitsopralluoghis() {
		return this.sitsopralluoghis;
	}

	public void setSitsopralluoghis(List<Sitsopralluoghi> sitsopralluoghis) {
		this.sitsopralluoghis = sitsopralluoghis;
	}

	public Sitsopralluoghi addSitsopralluoghi(Sitsopralluoghi sitsopralluoghi) {
		getSitsopralluoghis().add(sitsopralluoghi);
		sitsopralluoghi.setSopralluoghidip(this);

		return sitsopralluoghi;
	}

	public Sitsopralluoghi removeSitsopralluoghi(Sitsopralluoghi sitsopralluoghi) {
		getSitsopralluoghis().remove(sitsopralluoghi);
		sitsopralluoghi.setSopralluoghidip(null);

		return sitsopralluoghi;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRICEVENTE")
	public Anagrafiche getRicevente() {
		return this.ricevente;
	}

	public void setRicevente(Anagrafiche ricevente) {
		this.ricevente = ricevente;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDVISIONATO")
	public Anagrafiche getVisionato() {
		return this.visionato;
	}

	public void setVisionato(Anagrafiche visionato) {
		this.visionato = visionato;
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


	//bi-directional many-to-one association to AttivitaPrevnet
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDATTIVITAESERCIZIO")
	public AttivitaPrevnet getAttivitaPrevnet() {
		return this.attivita;
	}

	public void setAttivitaPrevnet(AttivitaPrevnet attivita) {
		this.attivita = attivita;
	}


	//bi-directional many-to-one association to Cantieri
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDACANTIERE")
	public Cantieri getCantiere() {
		return this.cantiere;
	}

	public void setCantiere(Cantieri cantiere) {
		this.cantiere = cantiere;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEUBIC")
	public Comuni getUbicComune() {
		return this.ubicComune;
	}

	public void setUbicComune(Comuni ubicComune) {
		this.ubicComune = ubicComune;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getDitta() {
		return this.ditta;
	}

	public void setDitta(Ditte ditta) {
		this.ditta = ditta;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTAUBIC")
	public Ditte getUbicDitta() {
		return this.ubicDitta;
	}

	public void setUbicDitta(Ditte ubicDitta) {
		this.ubicDitta = ubicDitta;
	}


	//bi-directional many-to-one association to Localita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLOCALITAUBIC")
	public Localita getUbicLocalita() {
		return this.ubicLocalita;
	}

	public void setUbicLocalita(Localita ubicLocalita) {
		this.ubicLocalita = ubicLocalita;
	}


	//bi-directional many-to-one association to Pratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratiche() {
		return this.pratiche;
	}

	public void setPratiche(Pratiche pratiche) {
		this.pratiche = pratiche;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGOVERIFICA")
	public Sopralluoghidip getSopralluoghidip() {
		return this.sopralluoghidip;
	}

	public void setSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		this.sopralluoghidip = sopralluoghidip;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Sopralluoghidip> getSopralluoghidips() {
		return this.sopralluoghidips;
	}

	public void setSopralluoghidips(List<Sopralluoghidip> sopralluoghidips) {
		this.sopralluoghidips = sopralluoghidips;
	}

	public Sopralluoghidip addSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		getSopralluoghidips().add(sopralluoghidip);
		sopralluoghidip.setSopralluoghidip(this);

		return sopralluoghidip;
	}

	public Sopralluoghidip removeSopralluoghidip(Sopralluoghidip sopralluoghidip) {
		getSopralluoghidips().remove(sopralluoghidip);
		sopralluoghidip.setSopralluoghidip(null);

		return sopralluoghidip;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDVALSSL")
	public Tabelle getValssl() {
		return this.valssl;
	}

	public void setValssl(Tabelle valssl) {
		this.valssl = valssl;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMANUTENZIONE")
	public Tabelle getManutenzione() {
		return this.manutenzione;
	}

	public void setManutenzione(Tabelle manutenzione) {
		this.manutenzione = manutenzione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASL")
	public Tabelle getAsl() {
		return this.asl;
	}

	public void setAsl(Tabelle asl) {
		this.asl = asl;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="TIPOEMERGENZA")
	public Tabelle getTipoEmergenza() {
		return this.tipoEmergenza;
	}

	public void setTipoEmergenza(Tabelle tipoEmergenza) {
		this.tipoEmergenza = tipoEmergenza;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="ESITO")
	public Tabelle getEsito() {
		return this.esito;
	}

	public void setEsito(Tabelle esito) {
		this.esito = esito;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANTINCENDIO")
	public Tabelle getAntincendio() {
		return this.antincendio;
	}

	public void setAntincendio(Tabelle antincendio) {
		this.antincendio = antincendio;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSICUREZZA")
	public Tabelle getSicurezza() {
		return this.sicurezza;
	}

	public void setSicurezza(Tabelle sicurezza) {
		this.sicurezza = sicurezza;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDIGIENE")
	public Tabelle getIgiene() {
		return this.igiene;
	}

	public void setIgiene(Tabelle igiene) {
		this.igiene = igiene;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDVALUTPIMUS")
	public Tabelle getValPimus() {
		return this.valPimus;
	}

	public void setValPimus(Tabelle valPimus) {
		this.valPimus = valPimus;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDVALDOCSICUREZZA")
	public Tabelle getValDocSicurezza() {
		return this.valDocSicurezza;
	}

	public void setValDocSicurezza(Tabelle valDocSicurezza) {
		this.valDocSicurezza = valDocSicurezza;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDVALPIANOSICUREZZA")
	public Tabelle getValPianoSicurezza() {
		return this.valPianoSicurezza;
	}

	public void setValPianoSicurezza(Tabelle valPianoSicurezza) {
		this.valPianoSicurezza = valPianoSicurezza;
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
	@JoinColumn(name="IDRUOLOPERSONAPRESENTE")
	public Tabelle getPersonaPresenteRuolo() {
		return this.personaPresenteRuolo;
	}

	public void setPersonaPresenteRuolo(Tabelle personaPresenteRuolo) {
		this.personaPresenteRuolo = personaPresenteRuolo;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOINTERVENTOSOP")
	public Tabelle getTipoInterventoSop() {
		return this.tipoInterventoSop;
	}

	public void setTipoInterventoSop(Tabelle tipoInterventoSop) {
		this.tipoInterventoSop = tipoInterventoSop;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPERSONAPRESENTE")
	public Utenti getPersonaPresente() {
		return this.personaPresente;
	}

	public void setPersonaPresente(Utenti personaPresente) {
		this.personaPresente = personaPresente;
	}


	//bi-directional many-to-one association to Sopralluoghiinfrazioni
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Sopralluoghiinfrazioni> getSopralluoghiinfrazionis() {
		return this.sopralluoghiinfrazionis;
	}

	public void setSopralluoghiinfrazionis(List<Sopralluoghiinfrazioni> sopralluoghiinfrazionis) {
		this.sopralluoghiinfrazionis = sopralluoghiinfrazionis;
	}

	public Sopralluoghiinfrazioni addSopralluoghiinfrazioni(Sopralluoghiinfrazioni sopralluoghiinfrazioni) {
		getSopralluoghiinfrazionis().add(sopralluoghiinfrazioni);
		sopralluoghiinfrazioni.setSopralluoghidip(this);

		return sopralluoghiinfrazioni;
	}

	public Sopralluoghiinfrazioni removeSopralluoghiinfrazioni(Sopralluoghiinfrazioni sopralluoghiinfrazioni) {
		getSopralluoghiinfrazionis().remove(sopralluoghiinfrazioni);
		sopralluoghiinfrazioni.setSopralluoghidip(null);

		return sopralluoghiinfrazioni;
	}


	//bi-directional many-to-one association to Motivisopralluoghi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="MOTIVO")
	public Motivisopralluoghi getMotivo() {
		return this.motivo;
	}

	public void setMotivo(Motivisopralluoghi motivo) {
		this.motivo = motivo;
	}


	//bi-directional many-to-one association to Motivisopralluoghi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOMOTIVO")
	public Motivisopralluoghi getTipoMotivo() {
		return this.tipoMotivo;
	}

	public void setTipoMotivo(Motivisopralluoghi tipoMotivo) {
		this.tipoMotivo = tipoMotivo;
	}

	//bi-directional one-to-one association to Soggettiprovvedimento
	@OneToMany(mappedBy="sopralluogo", fetch=FetchType.LAZY)
	public List<ProvvedimentiPrevnet> getProvvedimenti() {
		return provvedimenti;
	}


	public void setProvvedimenti(List<ProvvedimentiPrevnet> provvedimenti) {
		this.provvedimenti = provvedimenti;
	}
	
	public ProvvedimentiPrevnet addProvvedimenti(ProvvedimentiPrevnet provvedimento) {
		getProvvedimenti().add(provvedimento);
		provvedimento.setSopralluogo(this);

		return provvedimento;
	}

	public ProvvedimentiPrevnet removeProvvedimenti(ProvvedimentiPrevnet provvedimento) {
		getProvvedimenti().remove(provvedimento);
		provvedimento.setSopralluogo(this);

		return provvedimento;
	}

	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Sanzioni> getSanzionis() {
		return this.sanzionis;
	}

	public void setSanzionis(List<Sanzioni> sanzionis) {
		this.sanzionis = sanzionis;
	}

	public Sanzioni addSanzioni(Sanzioni sanzioni) {
		getSanzionis().add(sanzioni);
		sanzioni.setSopralluoghidip(this);

		return sanzioni;
	}

	public Sanzioni removeSanzioni(Sanzioni sanzioni) {
		getSanzionis().remove(sanzioni);
		sanzioni.setSopralluoghidip(null);

		return sanzioni;
	}
	
	//bi-directional many-to-one association to Scadenze
	@OneToMany(mappedBy="sopralluoghidip")
	public List<Scadenze> getScadenzes() {
		return this.scadenzes;
	}

	public void setScadenzes(List<Scadenze> scadenzes) {
		this.scadenzes = scadenzes;
	}

	public Scadenze addScadenze(Scadenze scadenze) {
		getScadenzes().add(scadenze);
		scadenze.setSopralluoghidip(this);

		return scadenze;
	}

	public Scadenze removeScadenze(Scadenze scadenze) {
		getScadenzes().remove(scadenze);
		scadenze.setSopralluoghidip(null);

		return scadenze;
	}

	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZASOPRVERIFICA")
	public Scadenze getScadenzaSoprVerifica() {
		return this.scadenzaSoprVerifica;
	}

	public void setScadenzaSoprVerifica(Scadenze scadenzaSoprVerifica) {
		this.scadenzaSoprVerifica = scadenzaSoprVerifica;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADPROGSOP")
	public Scadenze getScadenzaSoprProgr() {
		return this.scadenzaSoprProgr;
	}

	public void setScadenzaSoprProgr(Scadenze scadenzaSoprProgr) {
		this.scadenzaSoprProgr = scadenzaSoprProgr;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDATASCAD")
	public Scadenze getScadenzaData() {
		return this.scadenzaData;
	}

	public void setScadenzaData(Scadenze scadenze3) {
		this.scadenzaData = scadenze3;
	}

}