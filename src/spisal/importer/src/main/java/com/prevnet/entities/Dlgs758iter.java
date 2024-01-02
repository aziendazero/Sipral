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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the DLGS758ITER database table.
 * 
 */
@Entity
@NamedQuery(name="Dlgs758iter.findAll", query="SELECT d FROM Dlgs758iter d")
public class Dlgs758iter implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idartprovv;
	private String chkaccorpaart;
	private String chknotificapagcontravv;
	private Date dataammissionepagamento;
	private Date datacomunicpm;
	private Date datainvioesito;
	private BigDecimal giorniinizialiart;
	private BigDecimal gruppo;
	private BigDecimal idstoricoutentelegfiducia;
	private String indirizzolegfiducia;
	private String note;
	private String numfascicolo;
	private BigDecimal optesito758;
	private String pagrateale;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private List<Dlgs758giorniproroga> dlgs758giorniprorogas;
	private Anagrafiche procura;
	private Anagrafiche anagrafiche2;
	private Anagrafiche anagrafiche3;
	private Comuni comuni;
	private Operatori operatori;
	private Scadenze scadenzaMinOttemperanza;
	private Scadenze scadenzaOttempTempoSup;
	private Scadenze scadenzaInvioEsito;
	private Scadenze scadenzaVerifica;
	private Scadenze scadenzaPagamento;
	private Scadenze scadenzaPmTerminePagamento;
	private Scadenze scadenzaPmNonPagamento;
	private Scadenze scadenzaInottemperante;
	private Utenti magistrato;
	private Utenti legaleFiducia;
	private Articoliprovvedimenti articoliprovvedimenti;
	private Ditte ditte;
	private Sanzioni sanzioni;

	public Dlgs758iter() {
	}


	@Id
	public long getIdartprovv() {
		return this.idartprovv;
	}

	public void setIdartprovv(long idartprovv) {
		this.idartprovv = idartprovv;
	}


	public String getChkaccorpaart() {
		return this.chkaccorpaart;
	}

	public void setChkaccorpaart(String chkaccorpaart) {
		this.chkaccorpaart = chkaccorpaart;
	}


	public String getChknotificapagcontravv() {
		return this.chknotificapagcontravv;
	}

	public void setChknotificapagcontravv(String chknotificapagcontravv) {
		this.chknotificapagcontravv = chknotificapagcontravv;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataammissionepagamento() {
		return this.dataammissionepagamento;
	}

	public void setDataammissionepagamento(Date dataammissionepagamento) {
		this.dataammissionepagamento = dataammissionepagamento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicpm() {
		return this.datacomunicpm;
	}

	public void setDatacomunicpm(Date datacomunicpm) {
		this.datacomunicpm = datacomunicpm;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainvioesito() {
		return this.datainvioesito;
	}

	public void setDatainvioesito(Date datainvioesito) {
		this.datainvioesito = datainvioesito;
	}


	public BigDecimal getGiorniinizialiart() {
		return this.giorniinizialiart;
	}

	public void setGiorniinizialiart(BigDecimal giorniinizialiart) {
		this.giorniinizialiart = giorniinizialiart;
	}


	public BigDecimal getGruppo() {
		return this.gruppo;
	}

	public void setGruppo(BigDecimal gruppo) {
		this.gruppo = gruppo;
	}


	public BigDecimal getIdstoricoutentelegfiducia() {
		return this.idstoricoutentelegfiducia;
	}

	public void setIdstoricoutentelegfiducia(BigDecimal idstoricoutentelegfiducia) {
		this.idstoricoutentelegfiducia = idstoricoutentelegfiducia;
	}


	public String getIndirizzolegfiducia() {
		return this.indirizzolegfiducia;
	}

	public void setIndirizzolegfiducia(String indirizzolegfiducia) {
		this.indirizzolegfiducia = indirizzolegfiducia;
	}

	@Column(name="NOTE", length=4000)
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNumfascicolo() {
		return this.numfascicolo;
	}

	public void setNumfascicolo(String numfascicolo) {
		this.numfascicolo = numfascicolo;
	}


	public BigDecimal getOptesito758() {
		return this.optesito758;
	}

	public void setOptesito758(BigDecimal optesito758) {
		this.optesito758 = optesito758;
	}


	public String getPagrateale() {
		return this.pagrateale;
	}

	public void setPagrateale(String pagrateale) {
		this.pagrateale = pagrateale;
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


	//bi-directional many-to-one association to Dlgs758giorniproroga
	@OneToMany(mappedBy="dlgs758iter")
	@IndexColumn(name="ordine")
	public List<Dlgs758giorniproroga> getDlgs758giorniprorogas() {
		return this.dlgs758giorniprorogas;
	}

	public void setDlgs758giorniprorogas(List<Dlgs758giorniproroga> dlgs758giorniprorogas) {
		this.dlgs758giorniprorogas = dlgs758giorniprorogas;
	}

	public Dlgs758giorniproroga addDlgs758giorniproroga(Dlgs758giorniproroga dlgs758giorniproroga) {
		getDlgs758giorniprorogas().add(dlgs758giorniproroga);
		dlgs758giorniproroga.setDlgs758iter(this);

		return dlgs758giorniproroga;
	}

	public Dlgs758giorniproroga removeDlgs758giorniproroga(Dlgs758giorniproroga dlgs758giorniproroga) {
		getDlgs758giorniprorogas().remove(dlgs758giorniproroga);
		dlgs758giorniproroga.setDlgs758iter(null);

		return dlgs758giorniproroga;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICAPROCURA")
	public Anagrafiche getProcura() {
		return this.procura;
	}

	public void setProcura(Anagrafiche procura) {
		this.procura = procura;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNELEGFIDUCIA")
	public Comuni getComuni() {
		return this.comuni;
	}

	public void setComuni(Comuni comuni) {
		this.comuni = comuni;
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


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADPMINOTTEMPERANZA")
	public Scadenze getScadenzaMinOttemperanza() {
		return this.scadenzaMinOttemperanza;
	}

	public void setScadenzaMinOttemperanza(Scadenze scadenze1) {
		this.scadenzaMinOttemperanza = scadenze1;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADPMOTTEMPTEMPOSUP")
	public Scadenze getScadenzaOttempTempoSup() {
		return this.scadenzaOttempTempoSup;
	}

	public void setScadenzaOttempTempoSup(Scadenze scadenzaOttempTempoSup) {
		this.scadenzaOttempTempoSup = scadenzaOttempTempoSup;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZAINVIOESITO")
	public Scadenze getScadenzaInvioEsito() {
		return this.scadenzaInvioEsito;
	}

	public void setScadenzaInvioEsito(Scadenze scadenzaInvioEsito) {
		this.scadenzaInvioEsito = scadenzaInvioEsito;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZAVERIFICA")
	public Scadenze getScadenzaVerifica() {
		return this.scadenzaVerifica;
	}

	public void setScadenzaVerifica(Scadenze scadenzaVerifica) {
		this.scadenzaVerifica = scadenzaVerifica;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZAPAGAMENTO")
	public Scadenze getScadenzaPagamento() {
		return this.scadenzaPagamento;
	}

	public void setScadenzaPagamento(Scadenze scadenzaPagamento) {
		this.scadenzaPagamento = scadenzaPagamento;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADPMTERMINEPAGAMENTO")
	public Scadenze getScadenzaPmTerminePagamento() {
		return this.scadenzaPmTerminePagamento;
	}

	public void setScadenzaPmTerminePagamento(Scadenze scadenzaPmTerminePagamento) {
		this.scadenzaPmTerminePagamento = scadenzaPmTerminePagamento;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADPMNONPAGAMENTO")
	public Scadenze getScadenzaPmNonPagamento() {
		return this.scadenzaPmNonPagamento;
	}

	public void setScadenzaPmNonPagamento(Scadenze scadenzaPmNonPagamento) {
		this.scadenzaPmNonPagamento = scadenzaPmNonPagamento;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADINOTTEMPERANTE")
	public Scadenze getScadenzaInottemperante() {
		return this.scadenzaInottemperante;
	}

	public void setScadenzaInottemperante(Scadenze scadenzaInottemperante) {
		this.scadenzaInottemperante = scadenzaInottemperante;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICAMAGISTRATO")
	public Utenti getMagistrato() {
		return this.magistrato;
	}

	public void setMagistrato(Utenti magistrato) {
		this.magistrato = magistrato;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGLEGFIDUCIA")
	public Utenti getLegaleFiducia() {
		return this.legaleFiducia;
	}

	public void setLegaleFiducia(Utenti legaleFiducia) {
		this.legaleFiducia = legaleFiducia;
	}


	//bi-directional one-to-one association to Articoliprovvedimenti
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="IDARTPROVV")
	public Articoliprovvedimenti getArticoliprovvedimenti() {
		return this.articoliprovvedimenti;
	}

	public void setArticoliprovvedimenti(Articoliprovvedimenti articoliprovvedimenti) {
		this.articoliprovvedimenti = articoliprovvedimenti;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDAANAGPROCURA")
	public Ditte getDitte() {
		return this.ditte;
	}

	public void setDitte(Ditte ditte) {
		this.ditte = ditte;
	}


	//bi-directional many-to-one association to Sanzioni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSANZIONEOTTEMPERANZA")
	public Sanzioni getSanzioni() {
		return this.sanzioni;
	}

	public void setSanzioni(Sanzioni sanzioni) {
		this.sanzioni = sanzioni;
	}

}