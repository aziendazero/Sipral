package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ESAMIOBIETTIVI database table.
 * 
 */
@Entity
public class Esamiobiettivi implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idesamiobiettivi;
	private String acuitavisivacorrettadx;
	private String acuitavisivacorrettasx;
	private String acuitavisivadx;
	private String acuitavisivalontano;
	private String acuitavisivasx;
	private String acuitavisivavicino;
	private BigDecimal altezza;
	private String appcardiocircolatorio;
	private String appdigerente;
	private String applocomotore;
	private String appneurosensoriale;
	private String apprespiratorio;
	private String appuditivo;
	private String appurinario;
	private BigDecimal bmi;
	private String chiudicartella;
	private String chkAttivaecg;
	private String chkAttivairi;
	private String chkAttivaspirometria;
	private String chkAttivaurine;
	private String chkObbligolenti;
	private String chkaudiometria;
	private String chkvisitaotorino;
	private String cutemucose;
	private String discrimcromatica;
	private String eccripfm;
	private String eccrippq;
	private String eccripqt;
	private String eccripreferto;
	private String eccsfofm;
	private String eccsfopq;
	private String eccsfoqt;
	private String eccsforeferto;
	private String esameoculare;
	private String esitourine;
	private BigDecimal eta;
	private BigDecimal fc;
	private BigDecimal idstoricoutente;
	private String iri;
	private BigDecimal irivalore;
	private String noteaccertstrumentali;
	private String notearti;
	private String noteaudiometria;
	private String noteecg;
	private String notevisitaotorino;
	private BigDecimal numacuitavisivacorrettadx;
	private BigDecimal numacuitavisivacorrettasx;
	private BigDecimal numacuitavisivadx;
	private BigDecimal numacuitavisivasx;
	private String oculomotricita;
	private String optcvfvc;
	private String ortoscopia;
	private String osservazioni;
	private BigDecimal pacuoredx;
	private BigDecimal pacuoresx;
	private BigDecimal peso;
	private String pressartriposo;
	private BigDecimal pressionearteriosaripmax;
	private BigDecimal pressionearteriosaripmin;
	private BigDecimal pressionearteriosasformax;
	private BigDecimal pressionearteriosasformin;
	private BigDecimal spirocvmis;
	private BigDecimal spirocvteo;
	private BigDecimal spiromvvmis;
	private BigDecimal spiromvvteo;
	private String spironote;
	private BigDecimal spirovemscvmis;
	private BigDecimal spirovemscvteo;
	private BigDecimal spirovemsmis;
	private BigDecimal spirovemsteo;
	private String trofismo;
	private BigDecimal vocesuss4;
	private Medici medici1;
	private Medici medici2;
	private Medici medici3;
	private Operatori operatori1;
	private Operatori operatori2;
	private Tabelle costituzioneFisica;
	private Tabelle tabelle2;
	private Tabelle tabelle3;
	private Tabelle tabelle4;
	private Tabelle tabelle5;
	private Tabelle tabelle6;
	private Utenti utenti;
	private List<Malattieprofessionali> malattieprofessionalis;

	public Esamiobiettivi() {
	}


	@Id
	public long getIdesamiobiettivi() {
		return this.idesamiobiettivi;
	}

	public void setIdesamiobiettivi(long idesamiobiettivi) {
		this.idesamiobiettivi = idesamiobiettivi;
	}


	public String getAcuitavisivacorrettadx() {
		return this.acuitavisivacorrettadx;
	}

	public void setAcuitavisivacorrettadx(String acuitavisivacorrettadx) {
		this.acuitavisivacorrettadx = acuitavisivacorrettadx;
	}


	public String getAcuitavisivacorrettasx() {
		return this.acuitavisivacorrettasx;
	}

	public void setAcuitavisivacorrettasx(String acuitavisivacorrettasx) {
		this.acuitavisivacorrettasx = acuitavisivacorrettasx;
	}


	public String getAcuitavisivadx() {
		return this.acuitavisivadx;
	}

	public void setAcuitavisivadx(String acuitavisivadx) {
		this.acuitavisivadx = acuitavisivadx;
	}


	public String getAcuitavisivalontano() {
		return this.acuitavisivalontano;
	}

	public void setAcuitavisivalontano(String acuitavisivalontano) {
		this.acuitavisivalontano = acuitavisivalontano;
	}


	public String getAcuitavisivasx() {
		return this.acuitavisivasx;
	}

	public void setAcuitavisivasx(String acuitavisivasx) {
		this.acuitavisivasx = acuitavisivasx;
	}


	public String getAcuitavisivavicino() {
		return this.acuitavisivavicino;
	}

	public void setAcuitavisivavicino(String acuitavisivavicino) {
		this.acuitavisivavicino = acuitavisivavicino;
	}


	public BigDecimal getAltezza() {
		return this.altezza;
	}

	public void setAltezza(BigDecimal altezza) {
		this.altezza = altezza;
	}


	public String getAppcardiocircolatorio() {
		return this.appcardiocircolatorio;
	}

	public void setAppcardiocircolatorio(String appcardiocircolatorio) {
		this.appcardiocircolatorio = appcardiocircolatorio;
	}


	public String getAppdigerente() {
		return this.appdigerente;
	}

	public void setAppdigerente(String appdigerente) {
		this.appdigerente = appdigerente;
	}


	public String getApplocomotore() {
		return this.applocomotore;
	}

	public void setApplocomotore(String applocomotore) {
		this.applocomotore = applocomotore;
	}


	public String getAppneurosensoriale() {
		return this.appneurosensoriale;
	}

	public void setAppneurosensoriale(String appneurosensoriale) {
		this.appneurosensoriale = appneurosensoriale;
	}


	public String getApprespiratorio() {
		return this.apprespiratorio;
	}

	public void setApprespiratorio(String apprespiratorio) {
		this.apprespiratorio = apprespiratorio;
	}


	public String getAppuditivo() {
		return this.appuditivo;
	}

	public void setAppuditivo(String appuditivo) {
		this.appuditivo = appuditivo;
	}


	public String getAppurinario() {
		return this.appurinario;
	}

	public void setAppurinario(String appurinario) {
		this.appurinario = appurinario;
	}


	public BigDecimal getBmi() {
		return this.bmi;
	}

	public void setBmi(BigDecimal bmi) {
		this.bmi = bmi;
	}


	public String getChiudicartella() {
		return this.chiudicartella;
	}

	public void setChiudicartella(String chiudicartella) {
		this.chiudicartella = chiudicartella;
	}


	@Column(name="CHK_ATTIVAECG")
	public String getChkAttivaecg() {
		return this.chkAttivaecg;
	}

	public void setChkAttivaecg(String chkAttivaecg) {
		this.chkAttivaecg = chkAttivaecg;
	}


	@Column(name="CHK_ATTIVAIRI")
	public String getChkAttivairi() {
		return this.chkAttivairi;
	}

	public void setChkAttivairi(String chkAttivairi) {
		this.chkAttivairi = chkAttivairi;
	}


	@Column(name="CHK_ATTIVASPIROMETRIA")
	public String getChkAttivaspirometria() {
		return this.chkAttivaspirometria;
	}

	public void setChkAttivaspirometria(String chkAttivaspirometria) {
		this.chkAttivaspirometria = chkAttivaspirometria;
	}


	@Column(name="CHK_ATTIVAURINE")
	public String getChkAttivaurine() {
		return this.chkAttivaurine;
	}

	public void setChkAttivaurine(String chkAttivaurine) {
		this.chkAttivaurine = chkAttivaurine;
	}


	@Column(name="CHK_OBBLIGOLENTI")
	public String getChkObbligolenti() {
		return this.chkObbligolenti;
	}

	public void setChkObbligolenti(String chkObbligolenti) {
		this.chkObbligolenti = chkObbligolenti;
	}


	public String getChkaudiometria() {
		return this.chkaudiometria;
	}

	public void setChkaudiometria(String chkaudiometria) {
		this.chkaudiometria = chkaudiometria;
	}


	public String getChkvisitaotorino() {
		return this.chkvisitaotorino;
	}

	public void setChkvisitaotorino(String chkvisitaotorino) {
		this.chkvisitaotorino = chkvisitaotorino;
	}


	public String getCutemucose() {
		return this.cutemucose;
	}

	public void setCutemucose(String cutemucose) {
		this.cutemucose = cutemucose;
	}


	public String getDiscrimcromatica() {
		return this.discrimcromatica;
	}

	public void setDiscrimcromatica(String discrimcromatica) {
		this.discrimcromatica = discrimcromatica;
	}


	public String getEccripfm() {
		return this.eccripfm;
	}

	public void setEccripfm(String eccripfm) {
		this.eccripfm = eccripfm;
	}


	public String getEccrippq() {
		return this.eccrippq;
	}

	public void setEccrippq(String eccrippq) {
		this.eccrippq = eccrippq;
	}


	public String getEccripqt() {
		return this.eccripqt;
	}

	public void setEccripqt(String eccripqt) {
		this.eccripqt = eccripqt;
	}


	public String getEccripreferto() {
		return this.eccripreferto;
	}

	public void setEccripreferto(String eccripreferto) {
		this.eccripreferto = eccripreferto;
	}


	public String getEccsfofm() {
		return this.eccsfofm;
	}

	public void setEccsfofm(String eccsfofm) {
		this.eccsfofm = eccsfofm;
	}


	public String getEccsfopq() {
		return this.eccsfopq;
	}

	public void setEccsfopq(String eccsfopq) {
		this.eccsfopq = eccsfopq;
	}


	public String getEccsfoqt() {
		return this.eccsfoqt;
	}

	public void setEccsfoqt(String eccsfoqt) {
		this.eccsfoqt = eccsfoqt;
	}


	public String getEccsforeferto() {
		return this.eccsforeferto;
	}

	public void setEccsforeferto(String eccsforeferto) {
		this.eccsforeferto = eccsforeferto;
	}


	public String getEsameoculare() {
		return this.esameoculare;
	}

	public void setEsameoculare(String esameoculare) {
		this.esameoculare = esameoculare;
	}


	public String getEsitourine() {
		return this.esitourine;
	}

	public void setEsitourine(String esitourine) {
		this.esitourine = esitourine;
	}


	public BigDecimal getEta() {
		return this.eta;
	}

	public void setEta(BigDecimal eta) {
		this.eta = eta;
	}


	public BigDecimal getFc() {
		return this.fc;
	}

	public void setFc(BigDecimal fc) {
		this.fc = fc;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public String getIri() {
		return this.iri;
	}

	public void setIri(String iri) {
		this.iri = iri;
	}


	public BigDecimal getIrivalore() {
		return this.irivalore;
	}

	public void setIrivalore(BigDecimal irivalore) {
		this.irivalore = irivalore;
	}


	public String getNoteaccertstrumentali() {
		return this.noteaccertstrumentali;
	}

	public void setNoteaccertstrumentali(String noteaccertstrumentali) {
		this.noteaccertstrumentali = noteaccertstrumentali;
	}


	public String getNotearti() {
		return this.notearti;
	}

	public void setNotearti(String notearti) {
		this.notearti = notearti;
	}


	public String getNoteaudiometria() {
		return this.noteaudiometria;
	}

	public void setNoteaudiometria(String noteaudiometria) {
		this.noteaudiometria = noteaudiometria;
	}


	public String getNoteecg() {
		return this.noteecg;
	}

	public void setNoteecg(String noteecg) {
		this.noteecg = noteecg;
	}


	public String getNotevisitaotorino() {
		return this.notevisitaotorino;
	}

	public void setNotevisitaotorino(String notevisitaotorino) {
		this.notevisitaotorino = notevisitaotorino;
	}


	public BigDecimal getNumacuitavisivacorrettadx() {
		return this.numacuitavisivacorrettadx;
	}

	public void setNumacuitavisivacorrettadx(BigDecimal numacuitavisivacorrettadx) {
		this.numacuitavisivacorrettadx = numacuitavisivacorrettadx;
	}


	public BigDecimal getNumacuitavisivacorrettasx() {
		return this.numacuitavisivacorrettasx;
	}

	public void setNumacuitavisivacorrettasx(BigDecimal numacuitavisivacorrettasx) {
		this.numacuitavisivacorrettasx = numacuitavisivacorrettasx;
	}


	public BigDecimal getNumacuitavisivadx() {
		return this.numacuitavisivadx;
	}

	public void setNumacuitavisivadx(BigDecimal numacuitavisivadx) {
		this.numacuitavisivadx = numacuitavisivadx;
	}


	public BigDecimal getNumacuitavisivasx() {
		return this.numacuitavisivasx;
	}

	public void setNumacuitavisivasx(BigDecimal numacuitavisivasx) {
		this.numacuitavisivasx = numacuitavisivasx;
	}


	public String getOculomotricita() {
		return this.oculomotricita;
	}

	public void setOculomotricita(String oculomotricita) {
		this.oculomotricita = oculomotricita;
	}


	public String getOptcvfvc() {
		return this.optcvfvc;
	}

	public void setOptcvfvc(String optcvfvc) {
		this.optcvfvc = optcvfvc;
	}


	public String getOrtoscopia() {
		return this.ortoscopia;
	}

	public void setOrtoscopia(String ortoscopia) {
		this.ortoscopia = ortoscopia;
	}


	public String getOsservazioni() {
		return this.osservazioni;
	}

	public void setOsservazioni(String osservazioni) {
		this.osservazioni = osservazioni;
	}


	public BigDecimal getPacuoredx() {
		return this.pacuoredx;
	}

	public void setPacuoredx(BigDecimal pacuoredx) {
		this.pacuoredx = pacuoredx;
	}


	public BigDecimal getPacuoresx() {
		return this.pacuoresx;
	}

	public void setPacuoresx(BigDecimal pacuoresx) {
		this.pacuoresx = pacuoresx;
	}


	public BigDecimal getPeso() {
		return this.peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}


	public String getPressartriposo() {
		return this.pressartriposo;
	}

	public void setPressartriposo(String pressartriposo) {
		this.pressartriposo = pressartriposo;
	}


	public BigDecimal getPressionearteriosaripmax() {
		return this.pressionearteriosaripmax;
	}

	public void setPressionearteriosaripmax(BigDecimal pressionearteriosaripmax) {
		this.pressionearteriosaripmax = pressionearteriosaripmax;
	}


	public BigDecimal getPressionearteriosaripmin() {
		return this.pressionearteriosaripmin;
	}

	public void setPressionearteriosaripmin(BigDecimal pressionearteriosaripmin) {
		this.pressionearteriosaripmin = pressionearteriosaripmin;
	}


	public BigDecimal getPressionearteriosasformax() {
		return this.pressionearteriosasformax;
	}

	public void setPressionearteriosasformax(BigDecimal pressionearteriosasformax) {
		this.pressionearteriosasformax = pressionearteriosasformax;
	}


	public BigDecimal getPressionearteriosasformin() {
		return this.pressionearteriosasformin;
	}

	public void setPressionearteriosasformin(BigDecimal pressionearteriosasformin) {
		this.pressionearteriosasformin = pressionearteriosasformin;
	}


	public BigDecimal getSpirocvmis() {
		return this.spirocvmis;
	}

	public void setSpirocvmis(BigDecimal spirocvmis) {
		this.spirocvmis = spirocvmis;
	}


	public BigDecimal getSpirocvteo() {
		return this.spirocvteo;
	}

	public void setSpirocvteo(BigDecimal spirocvteo) {
		this.spirocvteo = spirocvteo;
	}


	public BigDecimal getSpiromvvmis() {
		return this.spiromvvmis;
	}

	public void setSpiromvvmis(BigDecimal spiromvvmis) {
		this.spiromvvmis = spiromvvmis;
	}


	public BigDecimal getSpiromvvteo() {
		return this.spiromvvteo;
	}

	public void setSpiromvvteo(BigDecimal spiromvvteo) {
		this.spiromvvteo = spiromvvteo;
	}


	public String getSpironote() {
		return this.spironote;
	}

	public void setSpironote(String spironote) {
		this.spironote = spironote;
	}


	public BigDecimal getSpirovemscvmis() {
		return this.spirovemscvmis;
	}

	public void setSpirovemscvmis(BigDecimal spirovemscvmis) {
		this.spirovemscvmis = spirovemscvmis;
	}


	public BigDecimal getSpirovemscvteo() {
		return this.spirovemscvteo;
	}

	public void setSpirovemscvteo(BigDecimal spirovemscvteo) {
		this.spirovemscvteo = spirovemscvteo;
	}


	public BigDecimal getSpirovemsmis() {
		return this.spirovemsmis;
	}

	public void setSpirovemsmis(BigDecimal spirovemsmis) {
		this.spirovemsmis = spirovemsmis;
	}


	public BigDecimal getSpirovemsteo() {
		return this.spirovemsteo;
	}

	public void setSpirovemsteo(BigDecimal spirovemsteo) {
		this.spirovemsteo = spirovemsteo;
	}


	public String getTrofismo() {
		return this.trofismo;
	}

	public void setTrofismo(String trofismo) {
		this.trofismo = trofismo;
	}


	public BigDecimal getVocesuss4() {
		return this.vocesuss4;
	}

	public void setVocesuss4(BigDecimal vocesuss4) {
		this.vocesuss4 = vocesuss4;
	}


	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDMEDICO")
	public Medici getMedici1() {
		return this.medici1;
	}

	public void setMedici1(Medici medici1) {
		this.medici1 = medici1;
	}


	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICOSOSTITUTIVO")
	public Medici getMedici2() {
		return this.medici2;
	}

	public void setMedici2(Medici medici2) {
		this.medici2 = medici2;
	}


	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDCARDIOLOGO")
	public Medici getMedici3() {
		return this.medici3;
	}

	public void setMedici3(Medici medici3) {
		this.medici3 = medici3;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCARDIOLOGO")
	public Operatori getOperatori1() {
		return this.operatori1;
	}

	public void setOperatori1(Operatori operatori1) {
		this.operatori1 = operatori1;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICO")
	public Operatori getOperatori2() {
		return this.operatori2;
	}

	public void setOperatori2(Operatori operatori2) {
		this.operatori2 = operatori2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOSTITUZIONEFISICA")
	public Tabelle getCostituzioneFisica() {
		return this.costituzioneFisica;
	}

	public void setCostituzioneFisica(Tabelle tabelle1) {
		this.costituzioneFisica = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDREFERTO")
	public Tabelle getTabelle2() {
		return this.tabelle2;
	}

	public void setTabelle2(Tabelle tabelle2) {
		this.tabelle2 = tabelle2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOLORE")
	public Tabelle getTabelle3() {
		return this.tabelle3;
	}

	public void setTabelle3(Tabelle tabelle3) {
		this.tabelle3 = tabelle3;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDENSITA")
	public Tabelle getTabelle4() {
		return this.tabelle4;
	}

	public void setTabelle4(Tabelle tabelle4) {
		this.tabelle4 = tabelle4;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDREAZIONE")
	public Tabelle getTabelle5() {
		return this.tabelle5;
	}

	public void setTabelle5(Tabelle tabelle5) {
		this.tabelle5 = tabelle5;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDASPETTO")
	public Tabelle getTabelle6() {
		return this.tabelle6;
	}

	public void setTabelle6(Tabelle tabelle6) {
		this.tabelle6 = tabelle6;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUTENTE")
	public Utenti getUtenti() {
		return this.utenti;
	}

	public void setUtenti(Utenti utenti) {
		this.utenti = utenti;
	}


	//bi-directional many-to-one association to Malattieprofessionali
	@OneToMany(mappedBy="esamiobiettivi")
	public List<Malattieprofessionali> getMalattieprofessionalis() {
		return this.malattieprofessionalis;
	}

	public void setMalattieprofessionalis(List<Malattieprofessionali> malattieprofessionalis) {
		this.malattieprofessionalis = malattieprofessionalis;
	}

	public Malattieprofessionali addMalattieprofessionali(Malattieprofessionali malattieprofessionali) {
		getMalattieprofessionalis().add(malattieprofessionali);
		malattieprofessionali.setEsamiobiettivi(this);

		return malattieprofessionali;
	}

	public Malattieprofessionali removeMalattieprofessionali(Malattieprofessionali malattieprofessionali) {
		getMalattieprofessionalis().remove(malattieprofessionali);
		malattieprofessionali.setEsamiobiettivi(null);

		return malattieprofessionali;
	}

}