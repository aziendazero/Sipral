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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the PROVVEDIMENTI database table.
 * 
 */
@Entity
@Table(name="PROVVEDIMENTI")
public class ProvvedimentiPrevnet implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idprovvedimenti;
	private Integer annoprovvedimento;
	private BigDecimal chiavesid;
	private String chkricorso301;
	private String confignote;
	private Date dataaccettazionericorso301;
	private Date dataarchiviazione;
	private Date datacomunicazionealcomune301;
	private Date datadepprocura;
	private Date datanotifica;
	private Date datanotificarigetto301;
	private Date dataprovv;
	private Date datareg;
	private Date dataricorso301;
	private Date datascadproxsopr;
	private Date dataverifica301;
	private String esitoricorso301;
	private String evasa;
	private String exportimpresa;
	private String fissascad;
	private BigDecimal giorniscad;
	private BigDecimal giornisospensione;
	private BigDecimal idcampionamentisopr;
	private BigDecimal idpassoprocedura;
	private BigDecimal idprelacque;
	private BigDecimal idprelalimento;
	private BigDecimal idprocedura;
	private BigDecimal idprotocollo;
	private BigDecimal idsettore;
	private BigDecimal idsirsap;
	private BigDecimal idstoricoutente;
	private BigDecimal idtipoanagrafica;
	private BigDecimal idvetmonitor;
	private String impresainfmalprof;
	private String note;
	private String noteaggsopr;
	private String notificamanuale;
	private String numeroprotocollo;
	private BigDecimal numprovvedimento;
	private String numverbale;
	private BigDecimal quantitaseq;
	private String sindacoprotempore;
	private String soggettononspecificato;
	private Date synchdate;
	private String synchflag;
	private Date timestampinsmod;
	
	private Anagrafiche anagrafica;
	private Ditte ditta;
	private Comuni comuneVerbContest301;
	private Comuni comunePagamento301;
	private Pratiche pratica;
	private Sopralluoghidip sopralluogo;
	private List<Operatoriprovvedimenti> operatoriprovvedimentis;
	private Tabelle tipoProvvedimento;
	private Tabelle esito;
	private Tabelle tipologia;
	private Tabelle asl;
	private Soggettiprovvedimento soggettiprovvedimento;
	private Scadenze scadenzaData;
	private Scadenze scadenzaNotifica301;
	private Sanzioni sanzioni;
	private List<Sanzioni> sanzionis;
	private List<Articoliprovvedimenti> articoliprovvedimentis;
	
	public ProvvedimentiPrevnet() {
	}


	@Id
	public long getIdprovvedimenti() {
		return this.idprovvedimenti;
	}

	public void setIdprovvedimenti(long idprovvedimenti) {
		this.idprovvedimenti = idprovvedimenti;
	}


	public Integer getAnnoprovvedimento() {
		return this.annoprovvedimento;
	}

	public void setAnnoprovvedimento(Integer annoprovvedimento) {
		this.annoprovvedimento = annoprovvedimento;
	}


	public BigDecimal getChiavesid() {
		return this.chiavesid;
	}

	public void setChiavesid(BigDecimal chiavesid) {
		this.chiavesid = chiavesid;
	}


	public String getChkricorso301() {
		return this.chkricorso301;
	}

	public void setChkricorso301(String chkricorso301) {
		this.chkricorso301 = chkricorso301;
	}


	public String getConfignote() {
		return this.confignote;
	}

	public void setConfignote(String confignote) {
		this.confignote = confignote;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaccettazionericorso301() {
		return this.dataaccettazionericorso301;
	}

	public void setDataaccettazionericorso301(Date dataaccettazionericorso301) {
		this.dataaccettazionericorso301 = dataaccettazionericorso301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataarchiviazione() {
		return this.dataarchiviazione;
	}

	public void setDataarchiviazione(Date dataarchiviazione) {
		this.dataarchiviazione = dataarchiviazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicazionealcomune301() {
		return this.datacomunicazionealcomune301;
	}

	public void setDatacomunicazionealcomune301(Date datacomunicazionealcomune301) {
		this.datacomunicazionealcomune301 = datacomunicazionealcomune301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatadepprocura() {
		return this.datadepprocura;
	}

	public void setDatadepprocura(Date datadepprocura) {
		this.datadepprocura = datadepprocura;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanotifica() {
		return this.datanotifica;
	}

	public void setDatanotifica(Date datanotifica) {
		this.datanotifica = datanotifica;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanotificarigetto301() {
		return this.datanotificarigetto301;
	}

	public void setDatanotificarigetto301(Date datanotificarigetto301) {
		this.datanotificarigetto301 = datanotificarigetto301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataprovv() {
		return this.dataprovv;
	}

	public void setDataprovv(Date dataprovv) {
		this.dataprovv = dataprovv;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatareg() {
		return this.datareg;
	}

	public void setDatareg(Date datareg) {
		this.datareg = datareg;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataricorso301() {
		return this.dataricorso301;
	}

	public void setDataricorso301(Date dataricorso301) {
		this.dataricorso301 = dataricorso301;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatascadproxsopr() {
		return this.datascadproxsopr;
	}

	public void setDatascadproxsopr(Date datascadproxsopr) {
		this.datascadproxsopr = datascadproxsopr;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataverifica301() {
		return this.dataverifica301;
	}

	public void setDataverifica301(Date dataverifica301) {
		this.dataverifica301 = dataverifica301;
	}


	public String getEsitoricorso301() {
		return this.esitoricorso301;
	}

	public void setEsitoricorso301(String esitoricorso301) {
		this.esitoricorso301 = esitoricorso301;
	}


	public String getEvasa() {
		return this.evasa;
	}

	public void setEvasa(String evasa) {
		this.evasa = evasa;
	}


	public String getExportimpresa() {
		return this.exportimpresa;
	}

	public void setExportimpresa(String exportimpresa) {
		this.exportimpresa = exportimpresa;
	}


	public String getFissascad() {
		return this.fissascad;
	}

	public void setFissascad(String fissascad) {
		this.fissascad = fissascad;
	}


	public BigDecimal getGiorniscad() {
		return this.giorniscad;
	}

	public void setGiorniscad(BigDecimal giorniscad) {
		this.giorniscad = giorniscad;
	}


	public BigDecimal getGiornisospensione() {
		return this.giornisospensione;
	}

	public void setGiornisospensione(BigDecimal giornisospensione) {
		this.giornisospensione = giornisospensione;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getAnagrafica() {
		return this.anagrafica;
	}

	public void setAnagrafica(Anagrafiche anagrafica) {
		this.anagrafica = anagrafica;
	}


	public BigDecimal getIdcampionamentisopr() {
		return this.idcampionamentisopr;
	}

	public void setIdcampionamentisopr(BigDecimal idcampionamentisopr) {
		this.idcampionamentisopr = idcampionamentisopr;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNECOMUNICVERBCONTEST301")
	public Comuni getComuneVerbContest301() {
		return this.comuneVerbContest301;
	}

	public void setcomuneVerbContest301(Comuni comuneVerbContest301) {
		this.comuneVerbContest301 = comuneVerbContest301;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEPAGAMENTO301")
	public Comuni getComunePagamento301() {
		return this.comunePagamento301;
	}

	public void setComunePagamento301(Comuni comunePagamento301) {
		this.comunePagamento301 = comunePagamento301;
	}

	public BigDecimal getIdpassoprocedura() {
		return this.idpassoprocedura;
	}

	public void setIdpassoprocedura(BigDecimal idpassoprocedura) {
		this.idpassoprocedura = idpassoprocedura;
	}


	public BigDecimal getIdprelacque() {
		return this.idprelacque;
	}

	public void setIdprelacque(BigDecimal idprelacque) {
		this.idprelacque = idprelacque;
	}


	public BigDecimal getIdprelalimento() {
		return this.idprelalimento;
	}

	public void setIdprelalimento(BigDecimal idprelalimento) {
		this.idprelalimento = idprelalimento;
	}


	public BigDecimal getIdprocedura() {
		return this.idprocedura;
	}

	public void setIdprocedura(BigDecimal idprocedura) {
		this.idprocedura = idprocedura;
	}


	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	public BigDecimal getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(BigDecimal idprotocollo) {
		this.idprotocollo = idprotocollo;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getDitta() {
		return this.ditta;
	}

	public void setDitta(Ditte ditta) {
		this.ditta = ditta;
	}


	public BigDecimal getIdsettore() {
		return this.idsettore;
	}

	public void setIdsettore(BigDecimal idsettore) {
		this.idsettore = idsettore;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSOPRALLUOGO")
	public Sopralluoghidip getSopralluogo() {
		return this.sopralluogo;
	}

	public void setSopralluogo(Sopralluoghidip sopralluogo) {
		this.sopralluogo = sopralluogo;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public BigDecimal getIdtipoanagrafica() {
		return this.idtipoanagrafica;
	}

	public void setIdtipoanagrafica(BigDecimal idtipoanagrafica) {
		this.idtipoanagrafica = idtipoanagrafica;
	}


	public BigDecimal getIdvetmonitor() {
		return this.idvetmonitor;
	}

	public void setIdvetmonitor(BigDecimal idvetmonitor) {
		this.idvetmonitor = idvetmonitor;
	}


	public String getImpresainfmalprof() {
		return this.impresainfmalprof;
	}

	public void setImpresainfmalprof(String impresainfmalprof) {
		this.impresainfmalprof = impresainfmalprof;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNoteaggsopr() {
		return this.noteaggsopr;
	}

	public void setNoteaggsopr(String noteaggsopr) {
		this.noteaggsopr = noteaggsopr;
	}


	public String getNotificamanuale() {
		return this.notificamanuale;
	}

	public void setNotificamanuale(String notificamanuale) {
		this.notificamanuale = notificamanuale;
	}


	public String getNumeroprotocollo() {
		return this.numeroprotocollo;
	}

	public void setNumeroprotocollo(String numeroprotocollo) {
		this.numeroprotocollo = numeroprotocollo;
	}


	public BigDecimal getNumprovvedimento() {
		return this.numprovvedimento;
	}

	public void setNumprovvedimento(BigDecimal numprovvedimento) {
		this.numprovvedimento = numprovvedimento;
	}


	public String getNumverbale() {
		return this.numverbale;
	}

	public void setNumverbale(String numverbale) {
		this.numverbale = numverbale;
	}


	public BigDecimal getQuantitaseq() {
		return this.quantitaseq;
	}

	public void setQuantitaseq(BigDecimal quantitaseq) {
		this.quantitaseq = quantitaseq;
	}


	public String getSindacoprotempore() {
		return this.sindacoprotempore;
	}

	public void setSindacoprotempore(String sindacoprotempore) {
		this.sindacoprotempore = sindacoprotempore;
	}


	public String getSoggettononspecificato() {
		return this.soggettononspecificato;
	}

	public void setSoggettononspecificato(String soggettononspecificato) {
		this.soggettononspecificato = soggettononspecificato;
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


	//bi-directional many-to-one association to Operatoriprovvedimenti
	@OneToMany(mappedBy="provvedimenti")
	public List<Operatoriprovvedimenti> getOperatoriprovvedimentis() {
		return this.operatoriprovvedimentis;
	}

	public void setOperatoriprovvedimentis(List<Operatoriprovvedimenti> operatoriprovvedimentis) {
		this.operatoriprovvedimentis = operatoriprovvedimentis;
	}

	public Operatoriprovvedimenti addOperatoriprovvedimenti(Operatoriprovvedimenti operatoriprovvedimenti) {
		getOperatoriprovvedimentis().add(operatoriprovvedimenti);
		operatoriprovvedimenti.setProvvedimenti(this);

		return operatoriprovvedimenti;
	}

	public Operatoriprovvedimenti removeOperatoriprovvedimenti(Operatoriprovvedimenti operatoriprovvedimenti) {
		getOperatoriprovvedimentis().remove(operatoriprovvedimenti);
		operatoriprovvedimenti.setProvvedimenti(null);

		return operatoriprovvedimenti;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOPROVVEDIMENTO")
	public Tabelle getTipoProvvedimento() {
		return this.tipoProvvedimento;
	}

	public void setTipoProvvedimento(Tabelle tipoProvvedimento) {
		this.tipoProvvedimento = tipoProvvedimento;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITO")
	public Tabelle getEsito() {
		return this.esito;
	}

	public void setEsito(Tabelle esito) {
		this.esito = esito;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOLOGIA")
	public Tabelle getTipologia() {
		return this.tipologia;
	}

	public void setTipologia(Tabelle tipologia) {
		this.tipologia = tipologia;
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


	//bi-directional one-to-one association to Soggettiprovvedimento
	@OneToOne(mappedBy="provvedimenti", fetch=FetchType.LAZY)
	public Soggettiprovvedimento getSoggettiprovvedimento() {
		return this.soggettiprovvedimento;
	}

	public void setSoggettiprovvedimento(Soggettiprovvedimento soggettiprovvedimento) {
		this.soggettiprovvedimento = soggettiprovvedimento;
	}

	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDATASCAD")
	public Scadenze getScadenzaData() {
		return this.scadenzaData;
	}

	public void setScadenzaData(Scadenze scadenzaData) {
		this.scadenzaData = scadenzaData;
	}


	//bi-directional many-to-one association to Scadenze
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCADENZANOTIFICA301")
	public Scadenze getScadenzaNotifica301() {
		return this.scadenzaNotifica301;
	}

	public void setScadenzaNotifica301(Scadenze scadenzaNotifica301) {
		this.scadenzaNotifica301 = scadenzaNotifica301;
	}
	
	//bi-directional many-to-one association to Sanzioni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSANZIONEAMMINISTRATIVA")
	public Sanzioni getSanzioni() {
		return this.sanzioni;
	}

	public void setSanzioni(Sanzioni sanzioni) {
		this.sanzioni = sanzioni;
	}


	//bi-directional many-to-one association to Sanzioni
	@OneToMany(mappedBy="provvedimenti")
	public List<Sanzioni> getSanzionis() {
		return this.sanzionis;
	}

	public void setSanzionis(List<Sanzioni> sanzionis) {
		this.sanzionis = sanzionis;
	}

	public Sanzioni addSanzioni(Sanzioni sanzioni) {
		getSanzionis().add(sanzioni);
		sanzioni.setProvvedimenti(this);

		return sanzioni;
	}

	public Sanzioni removeSanzioni(Sanzioni sanzioni) {
		getSanzionis().remove(sanzioni);
		sanzioni.setProvvedimenti(null);

		return sanzioni;
	}

	//bi-directional many-to-one association to Articoliprovvedimenti
	@OneToMany(mappedBy="provvedimenti")
	public List<Articoliprovvedimenti> getArticoliprovvedimentis() {
		return this.articoliprovvedimentis;
	}

	public void setArticoliprovvedimentis(List<Articoliprovvedimenti> articoliprovvedimentis) {
		this.articoliprovvedimentis = articoliprovvedimentis;
	}

	public Articoliprovvedimenti addArticoliprovvedimenti(Articoliprovvedimenti articoliprovvedimenti) {
		getArticoliprovvedimentis().add(articoliprovvedimenti);
		articoliprovvedimenti.setProvvedimenti(this);

		return articoliprovvedimenti;
	}

	public Articoliprovvedimenti removeArticoliprovvedimenti(Articoliprovvedimenti articoliprovvedimenti) {
		getArticoliprovvedimentis().remove(articoliprovvedimenti);
		articoliprovvedimenti.setProvvedimenti(null);

		return articoliprovvedimenti;
	}

}