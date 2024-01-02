package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the UTENTI database table.
 * 
 */
@Entity
public class Utenti implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idanagrafica;
	private String bacCapdomicilio;
	private String bacIndirizzodomicilio;
	private String bacTxtcivicodom;
	private String bacalign;
	private String capcom;
	private String capdomicilio;
	private String certazi;
	private String certcom;
	private String certmef;
	private String certnar;
	private String cf;
	private String chiaveEsterna;
	private String chkcfvalidato;
	private String chkconoscenzalinguaita;
	private String chkinvalido;
	private String cognome;
	private Date datafinedispo;
	private Date datainitdispo;
	private Date datamorte;
	private Date datanascita;
	private String deceduto;
	private String decedutomodificabile;
	private String dettaglicivicodom;
	private String email;
	private String flgCertificazioneasl;
	private BigDecimal idcircoscrizionedom;
	private BigDecimal idcircoscrizioneres;
	private BigDecimal idlocalitacom;
	private BigDecimal idstoricostp;
	private BigDecimal idviadom;
	private String indirizzocom;
	private String indirizzodomicilio;
	private String limbo;
	private String lingua;
	private String nome;
	private String note;
	private String numcartellamds;
	private BigDecimal numerocivicodom;
	private String numerossn;
	private String optssn;
	private String partitaiva;
	private String pinaac;
	private String sesso;
	private String statosiss;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private String telcellulare;
	private String telefono;
	private String telefonodomicilio;
	private Date timestampinsmod;
	private String txtcivicodom;
	private String txtcivicores;
	private String txtviadom;
	private String txtviares;
	private String usatodomiciliobac;
	private Anagrafiche anagrafiche;
	private Localita localitaRes;
	private Localita localitaDom;
	private Localita localitaNascita;
	private Tabelle professione;
	private Tabelle asl;
	private Tabelle provDomicilio;
	private Tabelle esteroNascita;
	private Tabelle esteroResidenza;
	private Tabelle esteroDomicilio;
	private Tabelle aslAppartenenza;
	private Tabelle cittadinanza;
	private Tabelle statoCivile;
	private Tabelle scolarita;
	private Tabelle nascitaProvincia;
	private Tabelle residenzaProvincia;
	private Tabelle esteroDomicilioBac;
	private Tabelle distretto;
	private Tabelle aslIscrizione;
	private Tabelle esenzioneTipo;
	private Tabelle esenzioneMotivo;
	private Comuni comuneNascita;
	private Comuni comuneDomicilio;
	private Comuni comuneDomicilioBac;
	private Medici medicoCurante;
	
	public Utenti() {
	}


	@Id
	public long getIdanagrafica() {
		return this.idanagrafica;
	}

	public void setIdanagrafica(long idanagrafica) {
		this.idanagrafica = idanagrafica;
	}


	@Column(name="BAC_CAPDOMICILIO")
	public String getBacCapdomicilio() {
		return this.bacCapdomicilio;
	}

	public void setBacCapdomicilio(String bacCapdomicilio) {
		this.bacCapdomicilio = bacCapdomicilio;
	}


	@Column(name="BAC_INDIRIZZODOMICILIO")
	public String getBacIndirizzodomicilio() {
		return this.bacIndirizzodomicilio;
	}

	public void setBacIndirizzodomicilio(String bacIndirizzodomicilio) {
		this.bacIndirizzodomicilio = bacIndirizzodomicilio;
	}


	@Column(name="BAC_TXTCIVICODOM")
	public String getBacTxtcivicodom() {
		return this.bacTxtcivicodom;
	}

	public void setBacTxtcivicodom(String bacTxtcivicodom) {
		this.bacTxtcivicodom = bacTxtcivicodom;
	}


	public String getBacalign() {
		return this.bacalign;
	}

	public void setBacalign(String bacalign) {
		this.bacalign = bacalign;
	}


	public String getCapcom() {
		return this.capcom;
	}

	public void setCapcom(String capcom) {
		this.capcom = capcom;
	}


	public String getCapdomicilio() {
		return this.capdomicilio;
	}

	public void setCapdomicilio(String capdomicilio) {
		this.capdomicilio = capdomicilio;
	}


	public String getCertazi() {
		return this.certazi;
	}

	public void setCertazi(String certazi) {
		this.certazi = certazi;
	}


	public String getCertcom() {
		return this.certcom;
	}

	public void setCertcom(String certcom) {
		this.certcom = certcom;
	}


	public String getCertmef() {
		return this.certmef;
	}

	public void setCertmef(String certmef) {
		this.certmef = certmef;
	}


	public String getCertnar() {
		return this.certnar;
	}

	public void setCertnar(String certnar) {
		this.certnar = certnar;
	}


	public String getCf() {
		return this.cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}


	@Column(name="CHIAVE_ESTERNA")
	public String getChiaveEsterna() {
		return this.chiaveEsterna;
	}

	public void setChiaveEsterna(String chiaveEsterna) {
		this.chiaveEsterna = chiaveEsterna;
	}


	public String getChkcfvalidato() {
		return this.chkcfvalidato;
	}

	public void setChkcfvalidato(String chkcfvalidato) {
		this.chkcfvalidato = chkcfvalidato;
	}


	public String getChkconoscenzalinguaita() {
		return this.chkconoscenzalinguaita;
	}

	public void setChkconoscenzalinguaita(String chkconoscenzalinguaita) {
		this.chkconoscenzalinguaita = chkconoscenzalinguaita;
	}


	public String getChkinvalido() {
		return this.chkinvalido;
	}

	public void setChkinvalido(String chkinvalido) {
		this.chkinvalido = chkinvalido;
	}


	public String getCognome() {
		return this.cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafinedispo() {
		return this.datafinedispo;
	}

	public void setDatafinedispo(Date datafinedispo) {
		this.datafinedispo = datafinedispo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainitdispo() {
		return this.datainitdispo;
	}

	public void setDatainitdispo(Date datainitdispo) {
		this.datainitdispo = datainitdispo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatamorte() {
		return this.datamorte;
	}

	public void setDatamorte(Date datamorte) {
		this.datamorte = datamorte;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanascita() {
		return this.datanascita;
	}

	public void setDatanascita(Date datanascita) {
		this.datanascita = datanascita;
	}


	public String getDeceduto() {
		return this.deceduto;
	}

	public void setDeceduto(String deceduto) {
		this.deceduto = deceduto;
	}


	public String getDecedutomodificabile() {
		return this.decedutomodificabile;
	}

	public void setDecedutomodificabile(String decedutomodificabile) {
		this.decedutomodificabile = decedutomodificabile;
	}


	public String getDettaglicivicodom() {
		return this.dettaglicivicodom;
	}

	public void setDettaglicivicodom(String dettaglicivicodom) {
		this.dettaglicivicodom = dettaglicivicodom;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	@Column(name="FLG_CERTIFICAZIONEASL")
	public String getFlgCertificazioneasl() {
		return this.flgCertificazioneasl;
	}

	public void setFlgCertificazioneasl(String flgCertificazioneasl) {
		this.flgCertificazioneasl = flgCertificazioneasl;
	}


	public BigDecimal getIdcircoscrizionedom() {
		return this.idcircoscrizionedom;
	}

	public void setIdcircoscrizionedom(BigDecimal idcircoscrizionedom) {
		this.idcircoscrizionedom = idcircoscrizionedom;
	}


	public BigDecimal getIdcircoscrizioneres() {
		return this.idcircoscrizioneres;
	}

	public void setIdcircoscrizioneres(BigDecimal idcircoscrizioneres) {
		this.idcircoscrizioneres = idcircoscrizioneres;
	}

	public BigDecimal getIdlocalitacom() {
		return this.idlocalitacom;
	}

	public void setIdlocalitacom(BigDecimal idlocalitacom) {
		this.idlocalitacom = idlocalitacom;
	}

	public BigDecimal getIdstoricostp() {
		return this.idstoricostp;
	}

	public void setIdstoricostp(BigDecimal idstoricostp) {
		this.idstoricostp = idstoricostp;
	}


	public BigDecimal getIdviadom() {
		return this.idviadom;
	}

	public void setIdviadom(BigDecimal idviadom) {
		this.idviadom = idviadom;
	}


	public String getIndirizzocom() {
		return this.indirizzocom;
	}

	public void setIndirizzocom(String indirizzocom) {
		this.indirizzocom = indirizzocom;
	}


	public String getIndirizzodomicilio() {
		return this.indirizzodomicilio;
	}

	public void setIndirizzodomicilio(String indirizzodomicilio) {
		this.indirizzodomicilio = indirizzodomicilio;
	}


	public String getLimbo() {
		return this.limbo;
	}

	public void setLimbo(String limbo) {
		this.limbo = limbo;
	}


	public String getLingua() {
		return this.lingua;
	}

	public void setLingua(String lingua) {
		this.lingua = lingua;
	}


	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNumcartellamds() {
		return this.numcartellamds;
	}

	public void setNumcartellamds(String numcartellamds) {
		this.numcartellamds = numcartellamds;
	}


	public BigDecimal getNumerocivicodom() {
		return this.numerocivicodom;
	}

	public void setNumerocivicodom(BigDecimal numerocivicodom) {
		this.numerocivicodom = numerocivicodom;
	}


	public String getNumerossn() {
		return this.numerossn;
	}

	public void setNumerossn(String numerossn) {
		this.numerossn = numerossn;
	}


	public String getOptssn() {
		return this.optssn;
	}

	public void setOptssn(String optssn) {
		this.optssn = optssn;
	}


	public String getPartitaiva() {
		return this.partitaiva;
	}

	public void setPartitaiva(String partitaiva) {
		this.partitaiva = partitaiva;
	}


	public String getPinaac() {
		return this.pinaac;
	}

	public void setPinaac(String pinaac) {
		this.pinaac = pinaac;
	}


	public String getSesso() {
		return this.sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}


	public String getStatosiss() {
		return this.statosiss;
	}

	public void setStatosiss(String statosiss) {
		this.statosiss = statosiss;
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


	public String getTelcellulare() {
		return this.telcellulare;
	}

	public void setTelcellulare(String telcellulare) {
		this.telcellulare = telcellulare;
	}


	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	public String getTelefonodomicilio() {
		return this.telefonodomicilio;
	}

	public void setTelefonodomicilio(String telefonodomicilio) {
		this.telefonodomicilio = telefonodomicilio;
	}


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	public String getTxtcivicodom() {
		return this.txtcivicodom;
	}

	public void setTxtcivicodom(String txtcivicodom) {
		this.txtcivicodom = txtcivicodom;
	}


	public String getTxtcivicores() {
		return this.txtcivicores;
	}

	public void setTxtcivicores(String txtcivicores) {
		this.txtcivicores = txtcivicores;
	}


	public String getTxtviadom() {
		return this.txtviadom;
	}

	public void setTxtviadom(String txtviadom) {
		this.txtviadom = txtviadom;
	}


	public String getTxtviares() {
		return this.txtviares;
	}

	public void setTxtviares(String txtviares) {
		this.txtviares = txtviares;
	}


	public String getUsatodomiciliobac() {
		return this.usatodomiciliobac;
	}

	public void setUsatodomiciliobac(String usatodomiciliobac) {
		this.usatodomiciliobac = usatodomiciliobac;
	}


	//bi-directional one-to-one association to Anagrafiche
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getAnagrafiche() {
		return this.anagrafiche;
	}

	public void setAnagrafiche(Anagrafiche anagrafiche) {
		this.anagrafiche = anagrafiche;
	}


	//bi-directional many-to-one association to Localita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLOCALITARES")
	public Localita getLocalitaRes() {
		return this.localitaRes;
	}

	public void setLocalitaRes(Localita localitaRes) {
		this.localitaRes = localitaRes;
	}


	//bi-directional many-to-one association to Localita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLOCALITADOMICILIO")
	public Localita getLocalitaDom() {
		return this.localitaDom;
	}

	public void setLocalitaDom(Localita localitaDom) {
		this.localitaDom = localitaDom;
	}


	//bi-directional many-to-one association to Localita
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLOCALITANASCITA")
	public Localita getLocalitaNascita() {
		return this.localitaNascita;
	}

	public void setLocalitaNascita(Localita localitaNascita) {
		this.localitaNascita = localitaNascita;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROFESSIONE")
	public Tabelle getProfessione() {
		return this.professione;
	}

	public void setProfessione(Tabelle professione) {
		this.professione = professione;
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
	@JoinColumn(name="IDPROVINCIADOMICILIO")
	public Tabelle getProvDomicilio() {
		return this.provDomicilio;
	}

	public void setProvDomicilio(Tabelle provDomicilio) {
		this.provDomicilio = provDomicilio;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESTERONASCITA")
	public Tabelle getEsteroNascita() {
		return this.esteroNascita;
	}

	public void setEsteroNascita(Tabelle esteroNascita) {
		this.esteroNascita = esteroNascita;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESTERORESIDENZA")
	public Tabelle getEsteroResidenza() {
		return this.esteroResidenza;
	}

	public void setEsteroResidenza(Tabelle esteroResidenza) {
		this.esteroResidenza = esteroResidenza;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESTERODOMICILIO")
	public Tabelle getEsteroDomicilio() {
		return this.esteroDomicilio;
	}

	public void setEsteroDomicilio(Tabelle esteroDomicilio) {
		this.esteroDomicilio = esteroDomicilio;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASLAPPARTENENZA")
	public Tabelle getAslAppartenenza() {
		return this.aslAppartenenza;
	}

	public void setAslAppartenenza(Tabelle aslAppartenenza) {
		this.aslAppartenenza = aslAppartenenza;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCITTADINANZA")
	public Tabelle getCittadinanza() {
		return this.cittadinanza;
	}

	public void setCittadinanza(Tabelle cittadinanza) {
		this.cittadinanza = cittadinanza;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSTATOCIVILE")
	public Tabelle getStatoCivile() {
		return this.statoCivile;
	}

	public void setStatoCivile(Tabelle statoCivile) {
		this.statoCivile = statoCivile;
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
	@JoinColumn(name="IDPROVINCIANASCITA")
	public Tabelle getNascitaProvincia() {
		return this.nascitaProvincia;
	}

	public void setNascitaProvincia(Tabelle nascitaProvincia) {
		this.nascitaProvincia = nascitaProvincia;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVINCIARES")
	public Tabelle getResidenzaProvincia() {
		return this.residenzaProvincia;
	}

	public void setResidenzaProvincia(Tabelle tabelle12) {
		this.residenzaProvincia = tabelle12;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="BAC_IDESTERODOMICILIO")
	public Tabelle getEsteroDomicilioBac() {
		return this.esteroDomicilioBac;
	}

	public void setEsteroDomicilioBac(Tabelle esteroDomicilioBac) {
		this.esteroDomicilioBac = esteroDomicilioBac;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDISTRETTO")
	public Tabelle getDistretto() {
		return this.distretto;
	}

	public void setDistretto(Tabelle distretto) {
		this.distretto = distretto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASLISCRIZIONE")
	public Tabelle getAslIscrizione() {
		return this.aslIscrizione;
	}

	public void setAslIscrizione(Tabelle aslIscrizione) {
		this.aslIscrizione = aslIscrizione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOESENZIONE")
	public Tabelle getEsenzioneTipo() {
		return this.esenzioneTipo;
	}

	public void setEsenzioneTipo(Tabelle esenzioneTipo) {
		this.esenzioneTipo = esenzioneTipo;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOESENZIONE")
	public Tabelle getEsenzioneMotivo() {
		return this.esenzioneMotivo;
	}

	public void setEsenzioneMotivo(Tabelle esenzioneMotivo) {
		this.esenzioneMotivo = esenzioneMotivo;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNENASCITA")
	public Comuni getComuneNascita() {
		return this.comuneNascita;
	}

	public void setComuneNascita(Comuni comuneNascita) {
		this.comuneNascita = comuneNascita;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEDOMICILIO")
	public Comuni getComuneDomicilio() {
		return this.comuneDomicilio;
	}

	public void setComuneDomicilio(Comuni comuneDomicilio) {
		this.comuneDomicilio = comuneDomicilio;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="BAC_IDCOMUNEDOMICILIO")
	public Comuni getComuneDomicilioBac() {
		return this.comuneDomicilioBac;
	}

	public void setComuneDomicilioBac(Comuni comuneDomicilioBac) {
		this.comuneDomicilioBac = comuneDomicilioBac;
	}

	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICOCURANTE")
	public Medici getMedicoCurante() {
		return this.medicoCurante;
	}

	public void setMedicoCurante(Medici medicoCurante) {
		this.medicoCurante = medicoCurante;
	}
}