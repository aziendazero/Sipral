package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the LAVORATRICIMADRE database table.
 * 
 */
@Entity
public class Lavoratricimadre implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idlavoratricimadre;
	private String allegato;
	private Date dataconcepimento;
	private Date datainizioassenza;
	private Date datainserimento;
	private Date dataparto;
	private Date datarichiesta;
	private Date dataritornolavoro;
	private String descrizionemansione;
	private BigDecimal idesameobiettivo;
	private BigDecimal idmedicoacc;
	private BigDecimal idprotocollo;
	private BigDecimal idstoricoutente;
	private BigDecimal idstoricoutentelavoratrice;
	private BigDecimal idstoricoutenterichiedente;
	private String inizioassenzaprec;
	private String inizioassenzasucc;
	private String motivazioni;
	private String note;
	private String parere;
	private String quartocomma;
	private BigDecimal tipodatorelavoro;
	private String valutazionerischi;
	private Anagrafiche richiedente;
	private Anagrafiche dittaAnag;
	private Ditte ditta;
	private Ditte dittaRichiedente;
	private Operatori operatore;
	private Pratiche pratica;
	private Tabelle luogoVisita;
	private Tabelle mansione;
	private Utenti utente;

	public Lavoratricimadre() {
	}


	@Id
	public long getIdlavoratricimadre() {
		return this.idlavoratricimadre;
	}

	public void setIdlavoratricimadre(long idlavoratricimadre) {
		this.idlavoratricimadre = idlavoratricimadre;
	}


	public String getAllegato() {
		return this.allegato;
	}

	public void setAllegato(String allegato) {
		this.allegato = allegato;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataconcepimento() {
		return this.dataconcepimento;
	}

	public void setDataconcepimento(Date dataconcepimento) {
		this.dataconcepimento = dataconcepimento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizioassenza() {
		return this.datainizioassenza;
	}

	public void setDatainizioassenza(Date datainizioassenza) {
		this.datainizioassenza = datainizioassenza;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainserimento() {
		return this.datainserimento;
	}

	public void setDatainserimento(Date datainserimento) {
		this.datainserimento = datainserimento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataparto() {
		return this.dataparto;
	}

	public void setDataparto(Date dataparto) {
		this.dataparto = dataparto;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarichiesta() {
		return this.datarichiesta;
	}

	public void setDatarichiesta(Date datarichiesta) {
		this.datarichiesta = datarichiesta;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataritornolavoro() {
		return this.dataritornolavoro;
	}

	public void setDataritornolavoro(Date dataritornolavoro) {
		this.dataritornolavoro = dataritornolavoro;
	}


	@Lob
	public String getDescrizionemansione() {
		return this.descrizionemansione;
	}

	public void setDescrizionemansione(String descrizionemansione) {
		this.descrizionemansione = descrizionemansione;
	}


	public BigDecimal getIdesameobiettivo() {
		return this.idesameobiettivo;
	}

	public void setIdesameobiettivo(BigDecimal idesameobiettivo) {
		this.idesameobiettivo = idesameobiettivo;
	}


	public BigDecimal getIdmedicoacc() {
		return this.idmedicoacc;
	}

	public void setIdmedicoacc(BigDecimal idmedicoacc) {
		this.idmedicoacc = idmedicoacc;
	}


	public BigDecimal getIdprotocollo() {
		return this.idprotocollo;
	}

	public void setIdprotocollo(BigDecimal idprotocollo) {
		this.idprotocollo = idprotocollo;
	}


	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}


	public BigDecimal getIdstoricoutentelavoratrice() {
		return this.idstoricoutentelavoratrice;
	}

	public void setIdstoricoutentelavoratrice(BigDecimal idstoricoutentelavoratrice) {
		this.idstoricoutentelavoratrice = idstoricoutentelavoratrice;
	}


	public BigDecimal getIdstoricoutenterichiedente() {
		return this.idstoricoutenterichiedente;
	}

	public void setIdstoricoutenterichiedente(BigDecimal idstoricoutenterichiedente) {
		this.idstoricoutenterichiedente = idstoricoutenterichiedente;
	}


	public String getInizioassenzaprec() {
		return this.inizioassenzaprec;
	}

	public void setInizioassenzaprec(String inizioassenzaprec) {
		this.inizioassenzaprec = inizioassenzaprec;
	}


	public String getInizioassenzasucc() {
		return this.inizioassenzasucc;
	}

	public void setInizioassenzasucc(String inizioassenzasucc) {
		this.inizioassenzasucc = inizioassenzasucc;
	}


	public String getMotivazioni() {
		return this.motivazioni;
	}

	public void setMotivazioni(String motivazioni) {
		this.motivazioni = motivazioni;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getParere() {
		return this.parere;
	}

	public void setParere(String parere) {
		this.parere = parere;
	}


	public String getQuartocomma() {
		return this.quartocomma;
	}

	public void setQuartocomma(String quartocomma) {
		this.quartocomma = quartocomma;
	}


	public BigDecimal getTipodatorelavoro() {
		return this.tipodatorelavoro;
	}

	public void setTipodatorelavoro(BigDecimal tipodatorelavoro) {
		this.tipodatorelavoro = tipodatorelavoro;
	}


	public String getValutazionerischi() {
		return this.valutazionerischi;
	}

	public void setValutazionerischi(String valutazionerischi) {
		this.valutazionerischi = valutazionerischi;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRICHIEDENTE")
	public Anagrafiche getRichiedente() {
		return this.richiedente;
	}

	public void setRichiedente(Anagrafiche richiedente) {
		this.richiedente = richiedente;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Anagrafiche getDittaAnag() {
		return this.dittaAnag;
	}

	public void setDittaAnag(Anagrafiche dittaAnag) {
		this.dittaAnag = dittaAnag;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getDitta() {
		return this.ditta;
	}

	public void setDitta(Ditte ditta) {
		this.ditta = ditta;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDARICHIEDENTE")
	public Ditte getDittaRichiedente() {
		return this.dittaRichiedente;
	}

	public void setDittaRichiedente(Ditte dittaRichiedente) {
		this.dittaRichiedente = dittaRichiedente;
	}


	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatore() {
		return this.operatore;
	}

	public void setOperatore(Operatori operatore) {
		this.operatore = operatore;
	}


	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLUOGOVISITA")
	public Tabelle getLuogoVisita() {
		return this.luogoVisita;
	}

	public void setLuogoVisita(Tabelle luogoVisita) {
		this.luogoVisita = luogoVisita;
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


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Utenti getUtente() {
		return this.utente;
	}

	public void setUtente(Utenti utente) {
		this.utente = utente;
	}

}