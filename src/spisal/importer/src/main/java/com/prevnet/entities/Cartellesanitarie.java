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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the CARTELLESANITARIE database table.
 * 
 */
@Entity
@NamedQuery(name="Cartellesanitarie.findAll", query="SELECT c FROM Cartellesanitarie c")
public class Cartellesanitarie implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long idcartellesanitarie;

	private String abitextrahobby;

	@Lob
	private String anamnesicancerogeni;

	private String anamnesifamiliari;

	private BigDecimal aperitivi;

	private String bevanda;

	private BigDecimal birra;

	private BigDecimal caffe;

	private String chkaltrabevanda;

	private String chkaltreabitvolutt;

	private String chkcaffe;

	private String chkespostocvm;

	@Temporal(TemporalType.DATE)
	private Date dataabitextrahobby;

	@Temporal(TemporalType.DATE)
	private Date dataanamnesifamiliare;

	@Temporal(TemporalType.DATE)
	private Date datacompilazione;

	@Temporal(TemporalType.DATE)
	private Date datamenarca;

	@Temporal(TemporalType.DATE)
	private Date dataprimavissport;

	@Temporal(TemporalType.DATE)
	private Date dataultimamesturazione;

	private String etafine;

	private String etainizio;

	private String flagalcool;

	private String flagbevandeecc;

	private String flagfarmaci;

	private String flagfumo;

	private BigDecimal idstoricoutente;

	private BigDecimal liquori;

	private String notealcol;

	private String notealtreabitvolutt;

	private String notebevandeecc;

	private String notefumo;

	private String notestoricolavori;

	private String notevaccinazioni;

	private BigDecimal vino;

	//bi-directional many-to-one association to Anamnesifisiologiche
	@OneToMany(mappedBy="cartellesanitarie")
	private List<Anamnesifisiologiche> anamnesifisiologiches;

	//bi-directional many-to-one association to Anamnesipatologiche
	@OneToMany(mappedBy="cartellesanitarie")
	private List<Anamnesipatologiche> anamnesipatologiches;

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUTENTE")
	private Anagrafiche anagrafiche;

	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	private Operatori operatori;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUSOALCOOL")
	private Tabelle tabelle1;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROTOCOLLOSAN")
	private Tabelle tabelle2;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITOPRIMAVISSPORT")
	private Tabelle tabelle3;

	//bi-directional many-to-one association to Cronologiacartellesan
	@OneToMany(mappedBy="cartellesanitarie")
	private List<Cronologiacartellesan> cronologiacartellesans;

	//bi-directional many-to-one association to Diagnosi
	@OneToMany(mappedBy="cartellesanitarie")
	private List<Diagnosi> diagnosis;

	//bi-directional many-to-one association to Farmaci
	@OneToMany(mappedBy="cartellesanitarie")
	private List<Farmaci> farmacis;

	//bi-directional many-to-one association to Storicoimpieghi
	@OneToMany(mappedBy="cartellesanitarie")
	private List<Storicoimpieghi> storicoimpieghis;

	public Cartellesanitarie() {
	}

	public long getIdcartellesanitarie() {
		return this.idcartellesanitarie;
	}

	public void setIdcartellesanitarie(long idcartellesanitarie) {
		this.idcartellesanitarie = idcartellesanitarie;
	}

	public String getAbitextrahobby() {
		return this.abitextrahobby;
	}

	public void setAbitextrahobby(String abitextrahobby) {
		this.abitextrahobby = abitextrahobby;
	}

	public String getAnamnesicancerogeni() {
		return this.anamnesicancerogeni;
	}

	public void setAnamnesicancerogeni(String anamnesicancerogeni) {
		this.anamnesicancerogeni = anamnesicancerogeni;
	}

	public String getAnamnesifamiliari() {
		return this.anamnesifamiliari;
	}

	public void setAnamnesifamiliari(String anamnesifamiliari) {
		this.anamnesifamiliari = anamnesifamiliari;
	}

	public BigDecimal getAperitivi() {
		return this.aperitivi;
	}

	public void setAperitivi(BigDecimal aperitivi) {
		this.aperitivi = aperitivi;
	}

	public String getBevanda() {
		return this.bevanda;
	}

	public void setBevanda(String bevanda) {
		this.bevanda = bevanda;
	}

	public BigDecimal getBirra() {
		return this.birra;
	}

	public void setBirra(BigDecimal birra) {
		this.birra = birra;
	}

	public BigDecimal getCaffe() {
		return this.caffe;
	}

	public void setCaffe(BigDecimal caffe) {
		this.caffe = caffe;
	}

	public String getChkaltrabevanda() {
		return this.chkaltrabevanda;
	}

	public void setChkaltrabevanda(String chkaltrabevanda) {
		this.chkaltrabevanda = chkaltrabevanda;
	}

	public String getChkaltreabitvolutt() {
		return this.chkaltreabitvolutt;
	}

	public void setChkaltreabitvolutt(String chkaltreabitvolutt) {
		this.chkaltreabitvolutt = chkaltreabitvolutt;
	}

	public String getChkcaffe() {
		return this.chkcaffe;
	}

	public void setChkcaffe(String chkcaffe) {
		this.chkcaffe = chkcaffe;
	}

	public String getChkespostocvm() {
		return this.chkespostocvm;
	}

	public void setChkespostocvm(String chkespostocvm) {
		this.chkespostocvm = chkespostocvm;
	}

	public Date getDataabitextrahobby() {
		return this.dataabitextrahobby;
	}

	public void setDataabitextrahobby(Date dataabitextrahobby) {
		this.dataabitextrahobby = dataabitextrahobby;
	}

	public Date getDataanamnesifamiliare() {
		return this.dataanamnesifamiliare;
	}

	public void setDataanamnesifamiliare(Date dataanamnesifamiliare) {
		this.dataanamnesifamiliare = dataanamnesifamiliare;
	}

	public Date getDatacompilazione() {
		return this.datacompilazione;
	}

	public void setDatacompilazione(Date datacompilazione) {
		this.datacompilazione = datacompilazione;
	}

	public Date getDatamenarca() {
		return this.datamenarca;
	}

	public void setDatamenarca(Date datamenarca) {
		this.datamenarca = datamenarca;
	}

	public Date getDataprimavissport() {
		return this.dataprimavissport;
	}

	public void setDataprimavissport(Date dataprimavissport) {
		this.dataprimavissport = dataprimavissport;
	}

	public Date getDataultimamesturazione() {
		return this.dataultimamesturazione;
	}

	public void setDataultimamesturazione(Date dataultimamesturazione) {
		this.dataultimamesturazione = dataultimamesturazione;
	}

	public String getEtafine() {
		return this.etafine;
	}

	public void setEtafine(String etafine) {
		this.etafine = etafine;
	}

	public String getEtainizio() {
		return this.etainizio;
	}

	public void setEtainizio(String etainizio) {
		this.etainizio = etainizio;
	}

	public String getFlagalcool() {
		return this.flagalcool;
	}

	public void setFlagalcool(String flagalcool) {
		this.flagalcool = flagalcool;
	}

	public String getFlagbevandeecc() {
		return this.flagbevandeecc;
	}

	public void setFlagbevandeecc(String flagbevandeecc) {
		this.flagbevandeecc = flagbevandeecc;
	}

	public String getFlagfarmaci() {
		return this.flagfarmaci;
	}

	public void setFlagfarmaci(String flagfarmaci) {
		this.flagfarmaci = flagfarmaci;
	}

	public String getFlagfumo() {
		return this.flagfumo;
	}

	public void setFlagfumo(String flagfumo) {
		this.flagfumo = flagfumo;
	}

	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}

	public BigDecimal getLiquori() {
		return this.liquori;
	}

	public void setLiquori(BigDecimal liquori) {
		this.liquori = liquori;
	}

	public String getNotealcol() {
		return this.notealcol;
	}

	public void setNotealcol(String notealcol) {
		this.notealcol = notealcol;
	}

	public String getNotealtreabitvolutt() {
		return this.notealtreabitvolutt;
	}

	public void setNotealtreabitvolutt(String notealtreabitvolutt) {
		this.notealtreabitvolutt = notealtreabitvolutt;
	}

	public String getNotebevandeecc() {
		return this.notebevandeecc;
	}

	public void setNotebevandeecc(String notebevandeecc) {
		this.notebevandeecc = notebevandeecc;
	}

	public String getNotefumo() {
		return this.notefumo;
	}

	public void setNotefumo(String notefumo) {
		this.notefumo = notefumo;
	}

	public String getNotestoricolavori() {
		return this.notestoricolavori;
	}

	public void setNotestoricolavori(String notestoricolavori) {
		this.notestoricolavori = notestoricolavori;
	}

	public String getNotevaccinazioni() {
		return this.notevaccinazioni;
	}

	public void setNotevaccinazioni(String notevaccinazioni) {
		this.notevaccinazioni = notevaccinazioni;
	}

	public BigDecimal getVino() {
		return this.vino;
	}

	public void setVino(BigDecimal vino) {
		this.vino = vino;
	}

	public List<Anamnesifisiologiche> getAnamnesifisiologiches() {
		return this.anamnesifisiologiches;
	}

	public void setAnamnesifisiologiches(List<Anamnesifisiologiche> anamnesifisiologiches) {
		this.anamnesifisiologiches = anamnesifisiologiches;
	}

	public Anamnesifisiologiche addAnamnesifisiologich(Anamnesifisiologiche anamnesifisiologich) {
		getAnamnesifisiologiches().add(anamnesifisiologich);
		anamnesifisiologich.setCartellesanitarie(this);

		return anamnesifisiologich;
	}

	public Anamnesifisiologiche removeAnamnesifisiologich(Anamnesifisiologiche anamnesifisiologich) {
		getAnamnesifisiologiches().remove(anamnesifisiologich);
		anamnesifisiologich.setCartellesanitarie(null);

		return anamnesifisiologich;
	}

	public List<Anamnesipatologiche> getAnamnesipatologiches() {
		return this.anamnesipatologiches;
	}

	public void setAnamnesipatologiches(List<Anamnesipatologiche> anamnesipatologiches) {
		this.anamnesipatologiches = anamnesipatologiches;
	}

	public Anamnesipatologiche addAnamnesipatologich(Anamnesipatologiche anamnesipatologich) {
		getAnamnesipatologiches().add(anamnesipatologich);
		anamnesipatologich.setCartellesanitarie(this);

		return anamnesipatologich;
	}

	public Anamnesipatologiche removeAnamnesipatologich(Anamnesipatologiche anamnesipatologich) {
		getAnamnesipatologiches().remove(anamnesipatologich);
		anamnesipatologich.setCartellesanitarie(null);

		return anamnesipatologich;
	}

	public Anagrafiche getAnagrafiche() {
		return this.anagrafiche;
	}

	public void setAnagrafiche(Anagrafiche anagrafiche) {
		this.anagrafiche = anagrafiche;
	}

	public Operatori getOperatori() {
		return this.operatori;
	}

	public void setOperatori(Operatori operatori) {
		this.operatori = operatori;
	}

	public Tabelle getTabelle1() {
		return this.tabelle1;
	}

	public void setTabelle1(Tabelle tabelle1) {
		this.tabelle1 = tabelle1;
	}

	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}

	public Tabelle getTabelle3() {
		return this.tabelle3;
	}

	public void setTabelle3(Tabelle tabelle3) {
		this.tabelle3 = tabelle3;
	}

	public List<Cronologiacartellesan> getCronologiacartellesans() {
		return this.cronologiacartellesans;
	}

	public void setCronologiacartellesans(List<Cronologiacartellesan> cronologiacartellesans) {
		this.cronologiacartellesans = cronologiacartellesans;
	}

	public Cronologiacartellesan addCronologiacartellesan(Cronologiacartellesan cronologiacartellesan) {
		getCronologiacartellesans().add(cronologiacartellesan);
		cronologiacartellesan.setCartellesanitarie(this);

		return cronologiacartellesan;
	}

	public Cronologiacartellesan removeCronologiacartellesan(Cronologiacartellesan cronologiacartellesan) {
		getCronologiacartellesans().remove(cronologiacartellesan);
		cronologiacartellesan.setCartellesanitarie(null);

		return cronologiacartellesan;
	}

	public List<Diagnosi> getDiagnosis() {
		return this.diagnosis;
	}

	public void setDiagnosis(List<Diagnosi> diagnosis) {
		this.diagnosis = diagnosis;
	}

	public Diagnosi addDiagnosi(Diagnosi diagnosi) {
		getDiagnosis().add(diagnosi);
		diagnosi.setCartellesanitarie(this);

		return diagnosi;
	}

	public Diagnosi removeDiagnosi(Diagnosi diagnosi) {
		getDiagnosis().remove(diagnosi);
		diagnosi.setCartellesanitarie(null);

		return diagnosi;
	}

	public List<Farmaci> getFarmacis() {
		return this.farmacis;
	}

	public void setFarmacis(List<Farmaci> farmacis) {
		this.farmacis = farmacis;
	}

	public Farmaci addFarmaci(Farmaci farmaci) {
		getFarmacis().add(farmaci);
		farmaci.setCartellesanitarie(this);

		return farmaci;
	}

	public Farmaci removeFarmaci(Farmaci farmaci) {
		getFarmacis().remove(farmaci);
		farmaci.setCartellesanitarie(null);

		return farmaci;
	}

	public List<Storicoimpieghi> getStoricoimpieghis() {
		return this.storicoimpieghis;
	}

	public void setStoricoimpieghis(List<Storicoimpieghi> storicoimpieghis) {
		this.storicoimpieghis = storicoimpieghis;
	}

	public Storicoimpieghi addStoricoimpieghi(Storicoimpieghi storicoimpieghi) {
		getStoricoimpieghis().add(storicoimpieghi);
		storicoimpieghi.setCartellesanitarie(this);

		return storicoimpieghi;
	}

	public Storicoimpieghi removeStoricoimpieghi(Storicoimpieghi storicoimpieghi) {
		getStoricoimpieghis().remove(storicoimpieghi);
		storicoimpieghi.setCartellesanitarie(null);

		return storicoimpieghi;
	}

}