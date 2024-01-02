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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the OPERATORI database table.
 * 
 */
@Entity
public class Operatori implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idoperatori;
	private BigDecimal caricolavoromin;
	private String cf;
	private String changecryptpsw;
	private BigDecimal chiavesid;
	private String chkabilitato;
	private String chkasltutte;
	private String chkcambiopsw;
	private String chkignoreldap;
	private String chkmedico;
	private String chkoperatoresan;
	private String chkupg;
	private String chkveterinario;
	private String codiceDsm;
	private String codicefiscale;
	private String codicemtc;
	private String cognome;
	private String convenzione;
	private Date datafinecollaborazione;
	private Date datafinedispo;
	private Date datainitdispo;
	private Date datainiziocollaborazione;
	private String email;
	private BigDecimal idfw2user;
	private BigDecimal idmedico;
	private BigDecimal idoperatoresan;
	private BigDecimal idveterinario;
	private String matricola;
	private String nome;
	private String note;
	private String operativo;
	private String password;
	private String sigla;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private String telefono;
	private Date timestampinsmod;
	private BigDecimal titolostudio;
	private String usernament;
	private Tabelle distretto;
	private Tabelle tipocontratto;
	private Tabelle figuraprofessionale;
	private Tabelle branca;
	private Tabelle disciplina;
	private Tabelle ambito;
	private Tabelle livello;
	private Tabelle nomina;
	private List<Interventiattivitasvolta> interventiattivitasvoltas;

	public Operatori() {
	}


	@Id
	public long getIdoperatori() {
		return this.idoperatori;
	}

	public void setIdoperatori(long idoperatori) {
		this.idoperatori = idoperatori;
	}


	public BigDecimal getCaricolavoromin() {
		return this.caricolavoromin;
	}

	public void setCaricolavoromin(BigDecimal caricolavoromin) {
		this.caricolavoromin = caricolavoromin;
	}


	public String getCf() {
		return this.cf;
	}

	public void setCf(String cf) {
		this.cf = cf;
	}


	public String getChangecryptpsw() {
		return this.changecryptpsw;
	}

	public void setChangecryptpsw(String changecryptpsw) {
		this.changecryptpsw = changecryptpsw;
	}


	public BigDecimal getChiavesid() {
		return this.chiavesid;
	}

	public void setChiavesid(BigDecimal chiavesid) {
		this.chiavesid = chiavesid;
	}


	public String getChkabilitato() {
		return this.chkabilitato;
	}

	public void setChkabilitato(String chkabilitato) {
		this.chkabilitato = chkabilitato;
	}


	public String getChkasltutte() {
		return this.chkasltutte;
	}

	public void setChkasltutte(String chkasltutte) {
		this.chkasltutte = chkasltutte;
	}


	public String getChkcambiopsw() {
		return this.chkcambiopsw;
	}

	public void setChkcambiopsw(String chkcambiopsw) {
		this.chkcambiopsw = chkcambiopsw;
	}


	public String getChkignoreldap() {
		return this.chkignoreldap;
	}

	public void setChkignoreldap(String chkignoreldap) {
		this.chkignoreldap = chkignoreldap;
	}


	public String getChkmedico() {
		return this.chkmedico;
	}

	public void setChkmedico(String chkmedico) {
		this.chkmedico = chkmedico;
	}


	public String getChkoperatoresan() {
		return this.chkoperatoresan;
	}

	public void setChkoperatoresan(String chkoperatoresan) {
		this.chkoperatoresan = chkoperatoresan;
	}


	public String getChkupg() {
		return this.chkupg;
	}

	public void setChkupg(String chkupg) {
		this.chkupg = chkupg;
	}


	public String getChkveterinario() {
		return this.chkveterinario;
	}

	public void setChkveterinario(String chkveterinario) {
		this.chkveterinario = chkveterinario;
	}


	@Column(name="CODICE_DSM")
	public String getCodiceDsm() {
		return this.codiceDsm;
	}

	public void setCodiceDsm(String codiceDsm) {
		this.codiceDsm = codiceDsm;
	}


	public String getCodicefiscale() {
		return this.codicefiscale;
	}

	public void setCodicefiscale(String codicefiscale) {
		this.codicefiscale = codicefiscale;
	}


	public String getCodicemtc() {
		return this.codicemtc;
	}

	public void setCodicemtc(String codicemtc) {
		this.codicemtc = codicemtc;
	}


	public String getCognome() {
		return this.cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}


	public String getConvenzione() {
		return this.convenzione;
	}

	public void setConvenzione(String convenzione) {
		this.convenzione = convenzione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafinecollaborazione() {
		return this.datafinecollaborazione;
	}

	public void setDatafinecollaborazione(Date datafinecollaborazione) {
		this.datafinecollaborazione = datafinecollaborazione;
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
	public Date getDatainiziocollaborazione() {
		return this.datainiziocollaborazione;
	}

	public void setDatainiziocollaborazione(Date datainiziocollaborazione) {
		this.datainiziocollaborazione = datainiziocollaborazione;
	}


	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public BigDecimal getIdfw2user() {
		return this.idfw2user;
	}

	public void setIdfw2user(BigDecimal idfw2user) {
		this.idfw2user = idfw2user;
	}


	public BigDecimal getIdmedico() {
		return this.idmedico;
	}

	public void setIdmedico(BigDecimal idmedico) {
		this.idmedico = idmedico;
	}


	public BigDecimal getIdoperatoresan() {
		return this.idoperatoresan;
	}

	public void setIdoperatoresan(BigDecimal idoperatoresan) {
		this.idoperatoresan = idoperatoresan;
	}


	public BigDecimal getIdveterinario() {
		return this.idveterinario;
	}

	public void setIdveterinario(BigDecimal idveterinario) {
		this.idveterinario = idveterinario;
	}


	public String getMatricola() {
		return this.matricola;
	}

	public void setMatricola(String matricola) {
		this.matricola = matricola;
	}


	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getOperativo() {
		return this.operativo;
	}

	public void setOperativo(String operativo) {
		this.operativo = operativo;
	}


	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
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


	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}


	@Temporal(TemporalType.DATE)
	public Date getTimestampinsmod() {
		return this.timestampinsmod;
	}

	public void setTimestampinsmod(Date timestampinsmod) {
		this.timestampinsmod = timestampinsmod;
	}


	public BigDecimal getTitolostudio() {
		return this.titolostudio;
	}

	public void setTitolostudio(BigDecimal titolostudio) {
		this.titolostudio = titolostudio;
	}


	public String getUsernament() {
		return this.usernament;
	}

	public void setUsernament(String usernament) {
		this.usernament = usernament;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDISTRETTO")
	public Tabelle getDistretto() {
		return this.distretto;
	}

	public void setDistretto(Tabelle tabelle1) {
		this.distretto = tabelle1;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOCONTRATTO")
	public Tabelle getTipocontratto() {
		return this.tipocontratto;
	}

	public void setTipocontratto(Tabelle tabelle2) {
		this.tipocontratto = tabelle2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDFIGURAPROFESSIONALE")
	public Tabelle getFiguraprofessionale() {
		return this.figuraprofessionale;
	}

	public void setFiguraprofessionale(Tabelle tabelle3) {
		this.figuraprofessionale = tabelle3;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDBRANCA")
	public Tabelle getBranca() {
		return this.branca;
	}

	public void setBranca(Tabelle tabelle4) {
		this.branca = tabelle4;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDISCIPLINA")
	public Tabelle getDisciplina() {
		return this.disciplina;
	}

	public void setDisciplina(Tabelle tabelle5) {
		this.disciplina = tabelle5;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAMBITO")
	public Tabelle getAmbito() {
		return this.ambito;
	}

	public void setAmbito(Tabelle tabelle6) {
		this.ambito = tabelle6;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLIVELLO")
	public Tabelle getLivello() {
		return this.livello;
	}

	public void setLivello(Tabelle tabelle7) {
		this.livello = tabelle7;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="NOMINA")
	public Tabelle getNomina() {
		return this.nomina;
	}

	public void setNomina(Tabelle tabelle8) {
		this.nomina = tabelle8;
	}
	
	//bi-directional many-to-many association to Interventiattivitasvolta
	@ManyToMany
	@JoinTable(
		name="INTERVENTIATTIVITAOPERATORI"
		, joinColumns={
			@JoinColumn(name="IDOPERATORE")
			}
		, inverseJoinColumns={
			@JoinColumn(name="IDATTIVITASVOLTA")
			}
		)
	public List<Interventiattivitasvolta> getInterventiattivitasvoltas() {
		return this.interventiattivitasvoltas;
	}

	public void setInterventiattivitasvoltas(List<Interventiattivitasvolta> interventiattivitasvoltas) {
		this.interventiattivitasvoltas = interventiattivitasvoltas;
	}
}