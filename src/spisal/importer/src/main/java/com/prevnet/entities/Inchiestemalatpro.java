package com.prevnet.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the INCHIESTEMALATPRO database table.
 * 
 */
@Entity
@NamedQuery(name="Inchiestemalatpro.findAll", query="SELECT i FROM Inchiestemalatpro i")
public class Inchiestemalatpro implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idinchiestemalatpro;
	private String allegati;
	private String attivitauo;
	private String chkarchivsede;
	private String chkinoltropm;
	private String chklesionicolpose;
	private String chknessocasualita;
	private String chkverbale758;
	private String chkviolazpresente;
	private String conclusioni;
	private String considerazioni;
	private Date dataaffidamentoinc;
	private Date dataesitoinc;
	private Date datainiziomalattia;
	private Date datasupplementoinc;
	private String disposizioni;
	private java.math.BigDecimal elencotesti;
	private java.math.BigDecimal iddittaprecedente;
	private java.math.BigDecimal idmagistrato;
	private Malattieprofessionali malattia;
	private Anagrafiche procura;
	private java.math.BigDecimal idschedadittaprecedente;
	private java.math.BigDecimal idschedaprocura;
	private String notedittaprecedente;
	private String noteinchiesta;
	private String numfascicolo;
	private String sintesifattrischio;
	private String sintesisit;
	private String storiaclinica;
	private String storialavorativa;
	private String tipoinchiesta;
	private String violazionisino;
	private Tabelle motivoArchiv;
	private Tabelle esitoInchiesta;
	private List<Malattieproupg> malattieproupgs;

	public Inchiestemalatpro() {
	}


	@Id
	public long getIdinchiestemalatpro() {
		return this.idinchiestemalatpro;
	}

	public void setIdinchiestemalatpro(long idinchiestemalatpro) {
		this.idinchiestemalatpro = idinchiestemalatpro;
	}


	public String getAllegati() {
		return this.allegati;
	}

	public void setAllegati(String allegati) {
		this.allegati = allegati;
	}


	public String getAttivitauo() {
		return this.attivitauo;
	}

	public void setAttivitauo(String attivitauo) {
		this.attivitauo = attivitauo;
	}


	public String getChkarchivsede() {
		return this.chkarchivsede;
	}

	public void setChkarchivsede(String chkarchivsede) {
		this.chkarchivsede = chkarchivsede;
	}


	public String getChkinoltropm() {
		return this.chkinoltropm;
	}

	public void setChkinoltropm(String chkinoltropm) {
		this.chkinoltropm = chkinoltropm;
	}


	public String getChklesionicolpose() {
		return this.chklesionicolpose;
	}

	public void setChklesionicolpose(String chklesionicolpose) {
		this.chklesionicolpose = chklesionicolpose;
	}


	public String getChknessocasualita() {
		return this.chknessocasualita;
	}

	public void setChknessocasualita(String chknessocasualita) {
		this.chknessocasualita = chknessocasualita;
	}


	public String getChkverbale758() {
		return this.chkverbale758;
	}

	public void setChkverbale758(String chkverbale758) {
		this.chkverbale758 = chkverbale758;
	}


	public String getChkviolazpresente() {
		return this.chkviolazpresente;
	}

	public void setChkviolazpresente(String chkviolazpresente) {
		this.chkviolazpresente = chkviolazpresente;
	}


	public String getConclusioni() {
		return this.conclusioni;
	}

	public void setConclusioni(String conclusioni) {
		this.conclusioni = conclusioni;
	}


	public String getConsiderazioni() {
		return this.considerazioni;
	}

	public void setConsiderazioni(String considerazioni) {
		this.considerazioni = considerazioni;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaffidamentoinc() {
		return this.dataaffidamentoinc;
	}

	public void setDataaffidamentoinc(Date dataaffidamentoinc) {
		this.dataaffidamentoinc = dataaffidamentoinc;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataesitoinc() {
		return this.dataesitoinc;
	}

	public void setDataesitoinc(Date dataesitoinc) {
		this.dataesitoinc = dataesitoinc;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainiziomalattia() {
		return this.datainiziomalattia;
	}

	public void setDatainiziomalattia(Date datainiziomalattia) {
		this.datainiziomalattia = datainiziomalattia;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatasupplementoinc() {
		return this.datasupplementoinc;
	}

	public void setDatasupplementoinc(Date datasupplementoinc) {
		this.datasupplementoinc = datasupplementoinc;
	}


	public String getDisposizioni() {
		return this.disposizioni;
	}

	public void setDisposizioni(String disposizioni) {
		this.disposizioni = disposizioni;
	}


	public java.math.BigDecimal getElencotesti() {
		return this.elencotesti;
	}

	public void setElencotesti(java.math.BigDecimal elencotesti) {
		this.elencotesti = elencotesti;
	}


	public java.math.BigDecimal getIddittaprecedente() {
		return this.iddittaprecedente;
	}

	public void setIddittaprecedente(java.math.BigDecimal iddittaprecedente) {
		this.iddittaprecedente = iddittaprecedente;
	}


	public java.math.BigDecimal getIdmagistrato() {
		return this.idmagistrato;
	}

	public void setIdmagistrato(java.math.BigDecimal idmagistrato) {
		this.idmagistrato = idmagistrato;
	}

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMALATTIA")
	public Malattieprofessionali getMalattia() {
		return this.malattia;
	}

	public void setMalattia(Malattieprofessionali malattia) {
		this.malattia = malattia;
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


	public java.math.BigDecimal getIdschedadittaprecedente() {
		return this.idschedadittaprecedente;
	}

	public void setIdschedadittaprecedente(java.math.BigDecimal idschedadittaprecedente) {
		this.idschedadittaprecedente = idschedadittaprecedente;
	}


	public java.math.BigDecimal getIdschedaprocura() {
		return this.idschedaprocura;
	}

	public void setIdschedaprocura(java.math.BigDecimal idschedaprocura) {
		this.idschedaprocura = idschedaprocura;
	}


	public String getNotedittaprecedente() {
		return this.notedittaprecedente;
	}

	public void setNotedittaprecedente(String notedittaprecedente) {
		this.notedittaprecedente = notedittaprecedente;
	}


	public String getNoteinchiesta() {
		return this.noteinchiesta;
	}

	public void setNoteinchiesta(String noteinchiesta) {
		this.noteinchiesta = noteinchiesta;
	}


	public String getNumfascicolo() {
		return this.numfascicolo;
	}

	public void setNumfascicolo(String numfascicolo) {
		this.numfascicolo = numfascicolo;
	}


	public String getSintesifattrischio() {
		return this.sintesifattrischio;
	}

	public void setSintesifattrischio(String sintesifattrischio) {
		this.sintesifattrischio = sintesifattrischio;
	}


	public String getSintesisit() {
		return this.sintesisit;
	}

	public void setSintesisit(String sintesisit) {
		this.sintesisit = sintesisit;
	}


	public String getStoriaclinica() {
		return this.storiaclinica;
	}

	public void setStoriaclinica(String storiaclinica) {
		this.storiaclinica = storiaclinica;
	}


	public String getStorialavorativa() {
		return this.storialavorativa;
	}

	public void setStorialavorativa(String storialavorativa) {
		this.storialavorativa = storialavorativa;
	}


	public String getTipoinchiesta() {
		return this.tipoinchiesta;
	}

	public void setTipoinchiesta(String tipoinchiesta) {
		this.tipoinchiesta = tipoinchiesta;
	}


	public String getViolazionisino() {
		return this.violazionisino;
	}

	public void setViolazionisino(String violazionisino) {
		this.violazionisino = violazionisino;
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
	@JoinColumn(name="IDESITOINCHIESTA")
	public Tabelle getEsitoInchiesta() {
		return this.esitoInchiesta;
	}

	public void setEsitoInchiesta(Tabelle esitoInchiesta) {
		this.esitoInchiesta = esitoInchiesta;
	}

}