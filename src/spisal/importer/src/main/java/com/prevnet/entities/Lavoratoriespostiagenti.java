package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
 * The persistent class for the LAVORATORIESPOSTIAGENTI database table.
 * 
 */
@Entity
@NamedQuery(name="Lavoratoriespostiagenti.findAll", query="SELECT l FROM Lavoratoriespostiagenti l")
public class Lavoratoriespostiagenti implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idlavoratoriespostiagenti;
	private BigDecimal annofine;
	private BigDecimal annoinizio;
	private Date datacomunicazione;
	private Date datafine;
	private Date datainizio;
	private Date dataorains;
	private Date dataoramod;
	private Date dataoravalidaz;
	private String descrattivita;
	private String esposizione;
	private BigDecimal idlavoratoreespostocanc;
	private Tabelle metodo;
	private BigDecimal oreesposizione;
	private String valore;
	private Anagrafiche anagrafiche;
	private Comunicazionicancerogeni comunicazionicancerogeni1;
	private Comunicazionicancerogeni comunicazionicancerogeni2;
	private Fattoririschio fattoririschio;
	private Livellicancerogeni livellicancerogeni;
	private Operatori operatoreValidazione;
	private Operatori operatoreInserimento;
	private Operatori operatoreModifica;
	private Tabelle reparto;
	private Tabelle mansione;
	private Tabelle unitaMisura;

	public Lavoratoriespostiagenti() {
	}


	@Id
	public long getIdlavoratoriespostiagenti() {
		return this.idlavoratoriespostiagenti;
	}

	public void setIdlavoratoriespostiagenti(long idlavoratoriespostiagenti) {
		this.idlavoratoriespostiagenti = idlavoratoriespostiagenti;
	}


	public BigDecimal getAnnofine() {
		return this.annofine;
	}

	public void setAnnofine(BigDecimal annofine) {
		this.annofine = annofine;
	}


	public BigDecimal getAnnoinizio() {
		return this.annoinizio;
	}

	public void setAnnoinizio(BigDecimal annoinizio) {
		this.annoinizio = annoinizio;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicazione() {
		return this.datacomunicazione;
	}

	public void setDatacomunicazione(Date datacomunicazione) {
		this.datacomunicazione = datacomunicazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatafine() {
		return this.datafine;
	}

	public void setDatafine(Date datafine) {
		this.datafine = datafine;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizio() {
		return this.datainizio;
	}

	public void setDatainizio(Date datainizio) {
		this.datainizio = datainizio;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataorains() {
		return this.dataorains;
	}

	public void setDataorains(Date dataorains) {
		this.dataorains = dataorains;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataoramod() {
		return this.dataoramod;
	}

	public void setDataoramod(Date dataoramod) {
		this.dataoramod = dataoramod;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataoravalidaz() {
		return this.dataoravalidaz;
	}

	public void setDataoravalidaz(Date dataoravalidaz) {
		this.dataoravalidaz = dataoravalidaz;
	}


	public String getDescrattivita() {
		return this.descrattivita;
	}

	public void setDescrattivita(String descrattivita) {
		this.descrattivita = descrattivita;
	}


	public String getEsposizione() {
		return this.esposizione;
	}

	public void setEsposizione(String esposizione) {
		this.esposizione = esposizione;
	}


	public BigDecimal getIdlavoratoreespostocanc() {
		return this.idlavoratoreespostocanc;
	}

	public void setIdlavoratoreespostocanc(BigDecimal idlavoratoreespostocanc) {
		this.idlavoratoreespostocanc = idlavoratoreespostocanc;
	}

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMETODO")
	public Tabelle getMetodo() {
		return this.metodo;
	}

	public void setMetodo(Tabelle metodo) {
		this.metodo = metodo;
	}


	public BigDecimal getOreesposizione() {
		return this.oreesposizione;
	}

	public void setOreesposizione(BigDecimal oreesposizione) {
		this.oreesposizione = oreesposizione;
	}


	public String getValore() {
		return this.valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUTENTE")
	public Anagrafiche getAnagrafiche() {
		return this.anagrafiche;
	}

	public void setAnagrafiche(Anagrafiche anagrafiche) {
		this.anagrafiche = anagrafiche;
	}


	//bi-directional many-to-one association to Comunicazionicancerogeni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNICAZIONECANCEROGENI")
	public Comunicazionicancerogeni getComunicazionicancerogeni1() {
		return this.comunicazionicancerogeni1;
	}

	public void setComunicazionicancerogeni1(Comunicazionicancerogeni comunicazionicancerogeni1) {
		this.comunicazionicancerogeni1 = comunicazionicancerogeni1;
	}


	//bi-directional many-to-one association to Comunicazionicancerogeni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNICAZIONECESSAZIONE")
	public Comunicazionicancerogeni getComunicazionicancerogeni2() {
		return this.comunicazionicancerogeni2;
	}

	public void setComunicazionicancerogeni2(Comunicazionicancerogeni comunicazionicancerogeni2) {
		this.comunicazionicancerogeni2 = comunicazionicancerogeni2;
	}


	//bi-directional many-to-one association to Fattoririschio
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAGENTE")
	public Fattoririschio getFattoririschio() {
		return this.fattoririschio;
	}

	public void setFattoririschio(Fattoririschio fattoririschio) {
		this.fattoririschio = fattoririschio;
	}


	//bi-directional many-to-one association to Livellicancerogeni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLIVELLO")
	public Livellicancerogeni getLivellicancerogeni() {
		return this.livellicancerogeni;
	}

	public void setLivellicancerogeni(Livellicancerogeni livellicancerogeni) {
		this.livellicancerogeni = livellicancerogeni;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREVALIDAZ")
	public Operatori getOperatoreValidazione() {
		return this.operatoreValidazione;
	}

	public void setOperatoreValidazione(Operatori operatoreValidazione) {
		this.operatoreValidazione = operatoreValidazione;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREINS")
	public Operatori getOperatoreInserimento() {
		return this.operatoreInserimento;
	}

	public void setOperatoreInserimento(Operatori operatoreInserimento) {
		this.operatoreInserimento = operatoreInserimento;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATOREMOD")
	public Operatori getOperatoreModifica() {
		return this.operatoreModifica;
	}

	public void setOperatoreModifica(Operatori operatoreModifica) {
		this.operatoreModifica = operatoreModifica;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDREPARTO")
	public Tabelle getReparto() {
		return this.reparto;
	}

	public void setReparto(Tabelle reparto) {
		this.reparto = reparto;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMANSIONE")
	public Tabelle getMansione() {
		return this.mansione;
	}

	public void setMansione(Tabelle mansione) {
		this.mansione = mansione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUNITAMISURA")
	public Tabelle getUnitaMisura() {
		return this.unitaMisura;
	}

	public void setUnitaMisura(Tabelle unitaMisura) {
		this.unitaMisura = unitaMisura;
	}

}