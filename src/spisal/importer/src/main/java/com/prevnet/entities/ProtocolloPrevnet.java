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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the PROTOCOLLO database table.
 * 
 */
@Entity
@Table(name="PROTOCOLLO")
public class ProtocolloPrevnet implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idprotocollo;
	private BigDecimal annoprotocollo;
	private String archiviato;
	private String chkpol;
	private Date data;
	private Date dataemissione;
	private Date dataincarico;
	private Date datainizio;
	private Date dataprotocollocentrale;
	private Date datareg;
	private Date dataupd;
	private String descoggetto;
	private String evidenzia;
	private BigDecimal idlogin;
	private BigDecimal idoperatoreprot;
	private BigDecimal idpraticaass;
	private BigDecimal idprotasl;
	private BigDecimal idsettoreprot;
	private BigDecimal idstoricoutenteprovdest;
	private BigDecimal idstoricoutenteriferimento;
	private BigDecimal idufficioass;
	private BigDecimal idufficioincarico;
	private BigDecimal iduffreg;
	private BigDecimal iduffupd;
	private String incarico;
	private String inout;
	private String nfascicolo;
	private String note;
	private String noteprotocollocentrale;
	private BigDecimal nprotocollo;
	private String nprotocollocentrale;
	private String protocollomittente;
	private String semaforo;
	private BigDecimal stato;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private BigDecimal tipodoccant;
	private BigDecimal tipoprovdest;
	private BigDecimal tiporiferimento;
	private String trasmesso;
	private Anagrafiche anagProvDest;
	private Anagrafiche magistrato;
	private Anagrafiche anagProcura;
	private Anagrafiche anagRiferimento;
	private Ditte dittaRiferimento;
	private Ditte dittaProvDest;
	private Ditte dittaProcura;
	private Operatori operatoreSanitario;
	private Operatori operatreUPD;
	private Operatori operatoreReg;
	private Operatori operatoreAmm;
	private Operatori respUos;
	private Operatori coordinatoreTecnico;
	private Operatori operatoreReferente;
	private Operatori operatoreRdp;
	private Operatori operatoreSpedizione;
	private Operatori operatoreIncarico;
	private Operatori operatoreAss;
	private Tabelle asl;
	private Tabelle destinazione;
	private Tabelle tipoMittente;
	private Tabelle tipoMissiva;
	private Tabelle motivoAttesa;
	private Tabelle ambito;
	private Tabelle oggetto;
	private Tabelle archivio;

	public ProtocolloPrevnet() {
	}


	@Id
	public long getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(long idprotocollo) {
		this.idprotocollo = idprotocollo;
	}


	public BigDecimal getAnnoprotocollo() {
		return this.annoprotocollo;
	}

	public void setAnnoprotocollo(BigDecimal annoprotocollo) {
		this.annoprotocollo = annoprotocollo;
	}


	public String getArchiviato() {
		return this.archiviato;
	}

	public void setArchiviato(String archiviato) {
		this.archiviato = archiviato;
	}


	public String getChkpol() {
		return this.chkpol;
	}

	public void setChkpol(String chkpol) {
		this.chkpol = chkpol;
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
	public Date getDataemissione() {
		return this.dataemissione;
	}

	public void setDataemissione(Date dataemissione) {
		this.dataemissione = dataemissione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataincarico() {
		return this.dataincarico;
	}

	public void setDataincarico(Date dataincarico) {
		this.dataincarico = dataincarico;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizio() {
		return this.datainizio;
	}

	public void setDatainizio(Date datainizio) {
		this.datainizio = datainizio;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataprotocollocentrale() {
		return this.dataprotocollocentrale;
	}

	public void setDataprotocollocentrale(Date dataprotocollocentrale) {
		this.dataprotocollocentrale = dataprotocollocentrale;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatareg() {
		return this.datareg;
	}

	public void setDatareg(Date datareg) {
		this.datareg = datareg;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataupd() {
		return this.dataupd;
	}

	public void setDataupd(Date dataupd) {
		this.dataupd = dataupd;
	}


	public String getDescoggetto() {
		return this.descoggetto;
	}

	public void setDescoggetto(String descoggetto) {
		this.descoggetto = descoggetto;
	}


	public String getEvidenzia() {
		return this.evidenzia;
	}

	public void setEvidenzia(String evidenzia) {
		this.evidenzia = evidenzia;
	}


	public BigDecimal getIdlogin() {
		return this.idlogin;
	}

	public void setIdlogin(BigDecimal idlogin) {
		this.idlogin = idlogin;
	}


	public BigDecimal getIdoperatoreprot() {
		return this.idoperatoreprot;
	}

	public void setIdoperatoreprot(BigDecimal idoperatoreprot) {
		this.idoperatoreprot = idoperatoreprot;
	}


	public BigDecimal getIdpraticaass() {
		return this.idpraticaass;
	}

	public void setIdpraticaass(BigDecimal idpraticaass) {
		this.idpraticaass = idpraticaass;
	}


	public BigDecimal getIdprotasl() {
		return this.idprotasl;
	}

	public void setIdprotasl(BigDecimal idprotasl) {
		this.idprotasl = idprotasl;
	}


	public BigDecimal getIdsettoreprot() {
		return this.idsettoreprot;
	}

	public void setIdsettoreprot(BigDecimal idsettoreprot) {
		this.idsettoreprot = idsettoreprot;
	}


	public BigDecimal getIdstoricoutenteprovdest() {
		return this.idstoricoutenteprovdest;
	}

	public void setIdstoricoutenteprovdest(BigDecimal idstoricoutenteprovdest) {
		this.idstoricoutenteprovdest = idstoricoutenteprovdest;
	}


	public BigDecimal getIdstoricoutenteriferimento() {
		return this.idstoricoutenteriferimento;
	}

	public void setIdstoricoutenteriferimento(BigDecimal idstoricoutenteriferimento) {
		this.idstoricoutenteriferimento = idstoricoutenteriferimento;
	}


	public BigDecimal getIdufficioass() {
		return this.idufficioass;
	}

	public void setIdufficioass(BigDecimal idufficioass) {
		this.idufficioass = idufficioass;
	}


	public BigDecimal getIdufficioincarico() {
		return this.idufficioincarico;
	}

	public void setIdufficioincarico(BigDecimal idufficioincarico) {
		this.idufficioincarico = idufficioincarico;
	}


	public BigDecimal getIduffreg() {
		return this.iduffreg;
	}

	public void setIduffreg(BigDecimal iduffreg) {
		this.iduffreg = iduffreg;
	}


	public BigDecimal getIduffupd() {
		return this.iduffupd;
	}

	public void setIduffupd(BigDecimal iduffupd) {
		this.iduffupd = iduffupd;
	}


	public String getIncarico() {
		return this.incarico;
	}

	public void setIncarico(String incarico) {
		this.incarico = incarico;
	}


	@Column(name="\"INOUT\"")
	public String getInout() {
		return this.inout;
	}

	public void setInout(String inout) {
		this.inout = inout;
	}


	public String getNfascicolo() {
		return this.nfascicolo;
	}

	public void setNfascicolo(String nfascicolo) {
		this.nfascicolo = nfascicolo;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNoteprotocollocentrale() {
		return this.noteprotocollocentrale;
	}

	public void setNoteprotocollocentrale(String noteprotocollocentrale) {
		this.noteprotocollocentrale = noteprotocollocentrale;
	}


	public BigDecimal getNprotocollo() {
		return this.nprotocollo;
	}

	public void setNprotocollo(BigDecimal nprotocollo) {
		this.nprotocollo = nprotocollo;
	}


	public String getNprotocollocentrale() {
		return this.nprotocollocentrale;
	}

	public void setNprotocollocentrale(String nprotocollocentrale) {
		this.nprotocollocentrale = nprotocollocentrale;
	}


	public String getProtocollomittente() {
		return this.protocollomittente;
	}

	public void setProtocollomittente(String protocollomittente) {
		this.protocollomittente = protocollomittente;
	}


	public String getSemaforo() {
		return this.semaforo;
	}

	public void setSemaforo(String semaforo) {
		this.semaforo = semaforo;
	}


	public BigDecimal getStato() {
		return this.stato;
	}

	public void setStato(BigDecimal stato) {
		this.stato = stato;
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


	public BigDecimal getTipodoccant() {
		return this.tipodoccant;
	}

	public void setTipodoccant(BigDecimal tipodoccant) {
		this.tipodoccant = tipodoccant;
	}


	public BigDecimal getTipoprovdest() {
		return this.tipoprovdest;
	}

	public void setTipoprovdest(BigDecimal tipoprovdest) {
		this.tipoprovdest = tipoprovdest;
	}


	public BigDecimal getTiporiferimento() {
		return this.tiporiferimento;
	}

	public void setTiporiferimento(BigDecimal tiporiferimento) {
		this.tiporiferimento = tiporiferimento;
	}


	public String getTrasmesso() {
		return this.trasmesso;
	}

	public void setTrasmesso(String trasmesso) {
		this.trasmesso = trasmesso;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROVDEST")
	public Anagrafiche getAnagProvDest() {
		return this.anagProvDest;
	}

	public void setAnagProvDest(Anagrafiche anagProvDest) {
		this.anagProvDest = anagProvDest;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMAGISTRATO")
	public Anagrafiche getMagistrato() {
		return this.magistrato;
	}

	public void setMagistrato(Anagrafiche magistrato) {
		this.magistrato = magistrato;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCURA")
	public Anagrafiche getAnagProcura() {
		return this.anagProcura;
	}

	public void setAnagProcura(Anagrafiche anagProcura) {
		this.anagProcura = anagProcura;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRIFERIMENTO")
	public Anagrafiche getAnagRiferimento() {
		return this.anagRiferimento;
	}

	public void setAnagRiferimento(Anagrafiche anagRiferimento) {
		this.anagRiferimento = anagRiferimento;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTARIFERIMENTO")
	public Ditte getDittaRiferimento() {
		return this.dittaRiferimento;
	}

	public void setDittaRiferimento(Ditte dittaRiferimento) {
		this.dittaRiferimento = dittaRiferimento;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTAPROVDEST")
	public Ditte getDittaProvDest() {
		return this.dittaProvDest;
	}

	public void setDittaProvDest(Ditte dittaProvDest) {
		this.dittaProvDest = dittaProvDest;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDAPROCURA")
	public Ditte getDittaProcura() {
		return this.dittaProcura;
	}

	public void setDittaProcura(Ditte dittaProcura) {
		this.dittaProcura = dittaProcura;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPSAN")
	public Operatori getOperatoreSanitario() {
		return this.operatoreSanitario;
	}

	public void setOperatoreSanitario(Operatori operatoreSanitario) {
		this.operatoreSanitario = operatoreSanitario;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPUPD")
	public Operatori getOperatreUPD() {
		return this.operatreUPD;
	}

	public void setOperatreUPD(Operatori operatreUPD) {
		this.operatreUPD = operatreUPD;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPREG")
	public Operatori getOperatoreReg() {
		return this.operatoreReg;
	}

	public void setOperatoreReg(Operatori operatoreReg) {
		this.operatoreReg = operatoreReg;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPAMM")
	public Operatori getOperatoreAmm() {
		return this.operatoreAmm;
	}

	public void setOperatoreAmm(Operatori operatoreAmm) {
		this.operatoreAmm = operatoreAmm;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRESPUOS")
	public Operatori getRespUos() {
		return this.respUos;
	}

	public void setRespUos(Operatori respUos) {
		this.respUos = respUos;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOORDINATORETECN")
	public Operatori getCoordinatoreTecnico() {
		return this.coordinatoreTecnico;
	}

	public void setCoordinatoreTecnico(Operatori coordinatoreTecnico) {
		this.coordinatoreTecnico = coordinatoreTecnico;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDREFERENTE")
	public Operatori getOperatoreReferente() {
		return this.operatoreReferente;
	}

	public void setOperatoreReferente(Operatori operatoreReferente) {
		this.operatoreReferente = operatoreReferente;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRDP")
	public Operatori getOperatoreRdp() {
		return this.operatoreRdp;
	}

	public void setOperatoreRdp(Operatori operatoreRdp) {
		this.operatoreRdp = operatoreRdp;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORESPEDIZIONE")
	public Operatori getOperatoreSpedizione() {
		return this.operatoreSpedizione;
	}

	public void setOperatoreSpedizione(Operatori operatoreSpedizione) {
		this.operatoreSpedizione = operatoreSpedizione;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREINCARICO")
	public Operatori getOperatoreIncarico() {
		return this.operatoreIncarico;
	}

	public void setOperatoreIncarico(Operatori operatoreIncarico) {
		this.operatoreIncarico = operatoreIncarico;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREASS")
	public Operatori getOperatoreAss() {
		return this.operatoreAss;
	}

	public void setOperatoreAss(Operatori operatoreAss) {
		this.operatoreAss = operatoreAss;
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
	@JoinColumn(name="IDDESTINAZIONE")
	public Tabelle getDestinazione() {
		return this.destinazione;
	}

	public void setDestinazione(Tabelle destinazione) {
		this.destinazione = destinazione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOMITTENTE")
	public Tabelle getTipoMittente() {
		return this.tipoMittente;
	}

	public void setTipoMittente(Tabelle tipoMittente) {
		this.tipoMittente = tipoMittente;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOMISSIVA")
	public Tabelle getTipoMissiva() {
		return this.tipoMissiva;
	}

	public void setTipoMissiva(Tabelle tipoMissiva) {
		this.tipoMissiva = tipoMissiva;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOATTESA")
	public Tabelle getMotivoAttesa() {
		return this.motivoAttesa;
	}

	public void setMotivoAttesa(Tabelle motivoAttesa) {
		this.motivoAttesa = motivoAttesa;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAMBITO")
	public Tabelle getAmbito() {
		return this.ambito;
	}

	public void setAmbito(Tabelle ambito) {
		this.ambito = ambito;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOGGETTO")
	public Tabelle getOggetto() {
		return this.oggetto;
	}

	public void setOggetto(Tabelle oggetto) {
		this.oggetto = oggetto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDARCHIVIO")
	public Tabelle getArchivio() {
		return this.archivio;
	}

	public void setArchivio(Tabelle archivio) {
		this.archivio = archivio;
	}

}