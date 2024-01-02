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
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ANAGRAFICHE database table.
 * 
 */
@Entity
public class Anagrafiche implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idanagrafiche;
	private String assprofBdn;
	private String bloccadenominazione;
	private String cap;
	private String catastoMappa;
	private String catastoParticella;
	private BigDecimal chiavesid;
	private String chkValido;
	private String chkittica;
	private BigDecimal codcontabilita;
	private String codcontabilitaadd;
	private String codice;
	private String codiceAziendaBdn;
	private BigDecimal codiceImp;
	private String codiceInternoBdn;
	private String codiceUo;
	private String codicecobi;
	private String codiceinterno;
	private String codicelog80;
	private Date dataaggparix;
	private Date datacreazione;
	private Date datasemaforo;
	private Date datasemaforocancerogeni;
	private Date dataultimamodifica;
	private String denominazione;
	private String denominazioneimpresa;
	private String dettagliocivico;
	private String dittaattiva;
	private String excodice;
	private BigDecimal idaura;
	private String idfiscaleBdn;
	private BigDecimal idizs;
	private BigDecimal idlogin;
	private BigDecimal idmunicipio;
	private BigDecimal idoperatore;
	private BigDecimal idoperatoresemaforo;
	private BigDecimal idoperatoreultimamodifica;
	private BigDecimal idopersemaforocancerogeni;
	private BigDecimal idprotocollonotifica;
	private BigDecimal idvia;
	private String importstruttsan;
	private String indirizzo;
	private String mascherabdn;
	private String nocontabilita;
	private BigDecimal numerocivico;
	private String numprotocollonotifica;
	private BigDecimal operatore;
	private String optidentita;
	private String semaforo;
	private String semaforojava;
	private BigDecimal sottocodiceImp;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private BigDecimal tipo;
	private Anagrafiche riferimento;
	private List<Anagrafiche> sedi;
	private Localita localita;
	private Tabelle tipologiaDitta;
	private Utenti utenti;
	private Comuni comuni;
	private Interni interni;

	public Anagrafiche() {
	}


	@Id
	public long getIdanagrafiche() {
		return this.idanagrafiche;
	}

	public void setIdanagrafiche(long idanagrafiche) {
		this.idanagrafiche = idanagrafiche;
	}


	@Column(name="ASSPROF_BDN")
	public String getAssprofBdn() {
		return this.assprofBdn;
	}

	public void setAssprofBdn(String assprofBdn) {
		this.assprofBdn = assprofBdn;
	}


	public String getBloccadenominazione() {
		return this.bloccadenominazione;
	}

	public void setBloccadenominazione(String bloccadenominazione) {
		this.bloccadenominazione = bloccadenominazione;
	}


	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}


	@Column(name="CATASTO_MAPPA")
	public String getCatastoMappa() {
		return this.catastoMappa;
	}

	public void setCatastoMappa(String catastoMappa) {
		this.catastoMappa = catastoMappa;
	}


	@Column(name="CATASTO_PARTICELLA")
	public String getCatastoParticella() {
		return this.catastoParticella;
	}

	public void setCatastoParticella(String catastoParticella) {
		this.catastoParticella = catastoParticella;
	}


	public BigDecimal getChiavesid() {
		return this.chiavesid;
	}

	public void setChiavesid(BigDecimal chiavesid) {
		this.chiavesid = chiavesid;
	}


	@Column(name="CHK_VALIDO")
	public String getChkValido() {
		return this.chkValido;
	}

	public void setChkValido(String chkValido) {
		this.chkValido = chkValido;
	}


	public String getChkittica() {
		return this.chkittica;
	}

	public void setChkittica(String chkittica) {
		this.chkittica = chkittica;
	}


	public BigDecimal getCodcontabilita() {
		return this.codcontabilita;
	}

	public void setCodcontabilita(BigDecimal codcontabilita) {
		this.codcontabilita = codcontabilita;
	}


	public String getCodcontabilitaadd() {
		return this.codcontabilitaadd;
	}

	public void setCodcontabilitaadd(String codcontabilitaadd) {
		this.codcontabilitaadd = codcontabilitaadd;
	}


	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}


	@Column(name="CODICE_AZIENDA_BDN")
	public String getCodiceAziendaBdn() {
		return this.codiceAziendaBdn;
	}

	public void setCodiceAziendaBdn(String codiceAziendaBdn) {
		this.codiceAziendaBdn = codiceAziendaBdn;
	}


	@Column(name="CODICE_IMP")
	public BigDecimal getCodiceImp() {
		return this.codiceImp;
	}

	public void setCodiceImp(BigDecimal codiceImp) {
		this.codiceImp = codiceImp;
	}


	@Column(name="CODICE_INTERNO_BDN")
	public String getCodiceInternoBdn() {
		return this.codiceInternoBdn;
	}

	public void setCodiceInternoBdn(String codiceInternoBdn) {
		this.codiceInternoBdn = codiceInternoBdn;
	}


	@Column(name="CODICE_UO")
	public String getCodiceUo() {
		return this.codiceUo;
	}

	public void setCodiceUo(String codiceUo) {
		this.codiceUo = codiceUo;
	}


	public String getCodicecobi() {
		return this.codicecobi;
	}

	public void setCodicecobi(String codicecobi) {
		this.codicecobi = codicecobi;
	}


	public String getCodiceinterno() {
		return this.codiceinterno;
	}

	public void setCodiceinterno(String codiceinterno) {
		this.codiceinterno = codiceinterno;
	}


	public String getCodicelog80() {
		return this.codicelog80;
	}

	public void setCodicelog80(String codicelog80) {
		this.codicelog80 = codicelog80;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaggparix() {
		return this.dataaggparix;
	}

	public void setDataaggparix(Date dataaggparix) {
		this.dataaggparix = dataaggparix;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacreazione() {
		return this.datacreazione;
	}

	public void setDatacreazione(Date datacreazione) {
		this.datacreazione = datacreazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatasemaforo() {
		return this.datasemaforo;
	}

	public void setDatasemaforo(Date datasemaforo) {
		this.datasemaforo = datasemaforo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatasemaforocancerogeni() {
		return this.datasemaforocancerogeni;
	}

	public void setDatasemaforocancerogeni(Date datasemaforocancerogeni) {
		this.datasemaforocancerogeni = datasemaforocancerogeni;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataultimamodifica() {
		return this.dataultimamodifica;
	}

	public void setDataultimamodifica(Date dataultimamodifica) {
		this.dataultimamodifica = dataultimamodifica;
	}


	public String getDenominazione() {
		return this.denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}


	public String getDenominazioneimpresa() {
		return this.denominazioneimpresa;
	}

	public void setDenominazioneimpresa(String denominazioneimpresa) {
		this.denominazioneimpresa = denominazioneimpresa;
	}


	public String getDettagliocivico() {
		return this.dettagliocivico;
	}

	public void setDettagliocivico(String dettagliocivico) {
		this.dettagliocivico = dettagliocivico;
	}


	public String getDittaattiva() {
		return this.dittaattiva;
	}

	public void setDittaattiva(String dittaattiva) {
		this.dittaattiva = dittaattiva;
	}


	public String getExcodice() {
		return this.excodice;
	}

	public void setExcodice(String excodice) {
		this.excodice = excodice;
	}


	public BigDecimal getIdaura() {
		return this.idaura;
	}

	public void setIdaura(BigDecimal idaura) {
		this.idaura = idaura;
	}


	@Column(name="IDFISCALE_BDN")
	public String getIdfiscaleBdn() {
		return this.idfiscaleBdn;
	}

	public void setIdfiscaleBdn(String idfiscaleBdn) {
		this.idfiscaleBdn = idfiscaleBdn;
	}


	public BigDecimal getIdizs() {
		return this.idizs;
	}

	public void setIdizs(BigDecimal idizs) {
		this.idizs = idizs;
	}


	public BigDecimal getIdlogin() {
		return this.idlogin;
	}

	public void setIdlogin(BigDecimal idlogin) {
		this.idlogin = idlogin;
	}


	public BigDecimal getIdmunicipio() {
		return this.idmunicipio;
	}

	public void setIdmunicipio(BigDecimal idmunicipio) {
		this.idmunicipio = idmunicipio;
	}


	public BigDecimal getIdoperatore() {
		return this.idoperatore;
	}

	public void setIdoperatore(BigDecimal idoperatore) {
		this.idoperatore = idoperatore;
	}


	public BigDecimal getIdoperatoresemaforo() {
		return this.idoperatoresemaforo;
	}

	public void setIdoperatoresemaforo(BigDecimal idoperatoresemaforo) {
		this.idoperatoresemaforo = idoperatoresemaforo;
	}


	public BigDecimal getIdoperatoreultimamodifica() {
		return this.idoperatoreultimamodifica;
	}

	public void setIdoperatoreultimamodifica(BigDecimal idoperatoreultimamodifica) {
		this.idoperatoreultimamodifica = idoperatoreultimamodifica;
	}


	public BigDecimal getIdopersemaforocancerogeni() {
		return this.idopersemaforocancerogeni;
	}

	public void setIdopersemaforocancerogeni(BigDecimal idopersemaforocancerogeni) {
		this.idopersemaforocancerogeni = idopersemaforocancerogeni;
	}


	public BigDecimal getIdprotocollonotifica() {
		return this.idprotocollonotifica;
	}

	public void setIdprotocollonotifica(BigDecimal idprotocollonotifica) {
		this.idprotocollonotifica = idprotocollonotifica;
	}


	public BigDecimal getIdvia() {
		return this.idvia;
	}

	public void setIdvia(BigDecimal idvia) {
		this.idvia = idvia;
	}

	public String getImportstruttsan() {
		return this.importstruttsan;
	}

	public void setImportstruttsan(String importstruttsan) {
		this.importstruttsan = importstruttsan;
	}


	public String getIndirizzo() {
		return this.indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}


	public String getMascherabdn() {
		return this.mascherabdn;
	}

	public void setMascherabdn(String mascherabdn) {
		this.mascherabdn = mascherabdn;
	}


	public String getNocontabilita() {
		return this.nocontabilita;
	}

	public void setNocontabilita(String nocontabilita) {
		this.nocontabilita = nocontabilita;
	}


	public BigDecimal getNumerocivico() {
		return this.numerocivico;
	}

	public void setNumerocivico(BigDecimal numerocivico) {
		this.numerocivico = numerocivico;
	}


	public String getNumprotocollonotifica() {
		return this.numprotocollonotifica;
	}

	public void setNumprotocollonotifica(String numprotocollonotifica) {
		this.numprotocollonotifica = numprotocollonotifica;
	}


	public BigDecimal getOperatore() {
		return this.operatore;
	}

	public void setOperatore(BigDecimal operatore) {
		this.operatore = operatore;
	}


	public String getOptidentita() {
		return this.optidentita;
	}

	public void setOptidentita(String optidentita) {
		this.optidentita = optidentita;
	}


	public String getSemaforo() {
		return this.semaforo;
	}

	public void setSemaforo(String semaforo) {
		this.semaforo = semaforo;
	}


	public String getSemaforojava() {
		return this.semaforojava;
	}

	public void setSemaforojava(String semaforojava) {
		this.semaforojava = semaforojava;
	}


	@Column(name="SOTTOCODICE_IMP")
	public BigDecimal getSottocodiceImp() {
		return this.sottocodiceImp;
	}

	public void setSottocodiceImp(BigDecimal sottocodiceImp) {
		this.sottocodiceImp = sottocodiceImp;
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


	public BigDecimal getTipo() {
		return this.tipo;
	}

	public void setTipo(BigDecimal tipo) {
		this.tipo = tipo;
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
	@OneToMany(mappedBy="riferimento")
	public List<Anagrafiche> getSedi() {
		return this.sedi;
	}

	public void setSedi(List<Anagrafiche> sedi) {
		this.sedi = sedi;
	}

	public Anagrafiche addSede(Anagrafiche sede) {
		getSedi().add(sede);
		sede.setRiferimento(this);

		return sede;
	}

	public Anagrafiche removeSede(Anagrafiche sede) {
		getSedi().remove(sede);
		sede.setRiferimento(null);

		return sede;
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


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOLOGIADITTA")
	public Tabelle getTipologiaDitta() {
		return this.tipologiaDitta;
	}

	public void setTipologiaDitta(Tabelle tipologiaDitta) {
		this.tipologiaDitta = tipologiaDitta;
	}


	//bi-directional one-to-one association to Utenti
	@OneToOne(mappedBy="anagrafiche", fetch=FetchType.LAZY)
	public Utenti getUtenti() {
		return this.utenti;
	}

	public void setUtenti(Utenti utenti) {
		this.utenti = utenti;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNE")
	public Comuni getComuni() {
		return this.comuni;
	}

	public void setComuni(Comuni comuni) {
		this.comuni = comuni;
	}

	//bi-directional one-to-one association to Interni
	@OneToOne(mappedBy="anagrafiche", fetch=FetchType.LAZY)
	public Interni getInterni() {
		return this.interni;
	}

	public void setInterni(Interni interni) {
		this.interni = interni;
	}

}