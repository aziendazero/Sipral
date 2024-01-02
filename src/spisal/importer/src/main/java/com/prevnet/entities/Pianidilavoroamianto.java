package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the PIANIDILAVOROAMIANTO database table.
 * 
 */
@Entity
public class Pianidilavoroamianto implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idpianidilavoroamianto;
	private String chkrichvigilanza;
	private Date dataarrivo;
	private Date datainizio;
	private Date datarisposta;
	private String descrlavoro;
	private BigDecimal duratagg;
	private Pratiche pratica;
	private BigDecimal idscadenzaterminelavori;
	private BigDecimal idsirsap;
	private Tabelle idtipofabbricato;
	private Tabelle idtipomateriale;
	private Tabelle idtiporimozione;
	private BigDecimal kgindiscarica;
	private String note;
	private BigDecimal numlavoratori;
	private BigDecimal quantita;
	private String solettaportante;
	private String tipoamianto;
	private BigDecimal tipoanagcommit;
	private BigDecimal tipoanagsubapp;
	private String ubicazionelavoro;
	private BigDecimal ufpg;
	private String urgenza;
	private BigDecimal urgenzaaccettata;
	private List<Ubicazioniamianto> ubicazioniamiantos;
	private Anagrafiche dittaSubappaltata;
	private Anagrafiche dittaLavoro;
	private Anagrafiche dittaEsecutrice;
	private Anagrafiche cantiere;
	private Anagrafiche trasportatore;
	private Anagrafiche discarica;
	private Anagrafiche dittaCommittente;
	private Anagrafiche dittaAffidataria;
	private Anagrafiche respSorveglianza;
	private Cantieri schedaCantiere;
	private Comuni ubicazioneComune;
	private Ditte schedaDittaSubappaltata;
	private Ditte schedaDittaLavoro;
	private Ditte schedaDiscarica;
	private Ditte schedaTrasportatore;
	private Ditte schedaDittaEsecutrice;
	private Ditte schedaDittaAffidataria;
	private Ditte schedaDittaCommittente;
	private Tabelle tipoLavoro;
	private Tabelle tipoEdificio;
	private Tabelle unitam;
	private Utenti utRespLavori;
	private Utenti utRespSicurezza;
	private Utenti utRespSorveglianza;

	public Pianidilavoroamianto() {
	}


	@Id
	public long getIdpianidilavoroamianto() {
		return this.idpianidilavoroamianto;
	}

	public void setIdpianidilavoroamianto(long idpianidilavoroamianto) {
		this.idpianidilavoroamianto = idpianidilavoroamianto;
	}

	//bi-directional many-to-one association to Pratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}

	public String getChkrichvigilanza() {
		return this.chkrichvigilanza;
	}

	public void setChkrichvigilanza(String chkrichvigilanza) {
		this.chkrichvigilanza = chkrichvigilanza;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataarrivo() {
		return this.dataarrivo;
	}

	public void setDataarrivo(Date dataarrivo) {
		this.dataarrivo = dataarrivo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainizio() {
		return this.datainizio;
	}

	public void setDatainizio(Date datainizio) {
		this.datainizio = datainizio;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatarisposta() {
		return this.datarisposta;
	}

	public void setDatarisposta(Date datarisposta) {
		this.datarisposta = datarisposta;
	}


	public String getDescrlavoro() {
		return this.descrlavoro;
	}

	public void setDescrlavoro(String descrlavoro) {
		this.descrlavoro = descrlavoro;
	}


	public BigDecimal getDuratagg() {
		return this.duratagg;
	}

	public void setDuratagg(BigDecimal duratagg) {
		this.duratagg = duratagg;
	}


	public BigDecimal getIdscadenzaterminelavori() {
		return this.idscadenzaterminelavori;
	}

	public void setIdscadenzaterminelavori(BigDecimal idscadenzaterminelavori) {
		this.idscadenzaterminelavori = idscadenzaterminelavori;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="idtipofabbricato")
	public Tabelle getIdtipofabbricato() {
		return this.idtipofabbricato;
	}

	public void setIdtipofabbricato(Tabelle idtipofabbricato) {
		this.idtipofabbricato = idtipofabbricato;
	}

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="idtipomateriale")
	public Tabelle getIdtipomateriale() {
		return this.idtipomateriale;
	}

	public void setIdtipomateriale(Tabelle idtipomateriale) {
		this.idtipomateriale = idtipomateriale;
	}

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="idtiporimozione")
	public Tabelle getIdtiporimozione() {
		return this.idtiporimozione;
	}

	public void setIdtiporimozione(Tabelle idtiporimozione) {
		this.idtiporimozione = idtiporimozione;
	}


	public BigDecimal getKgindiscarica() {
		return this.kgindiscarica;
	}

	public void setKgindiscarica(BigDecimal kgindiscarica) {
		this.kgindiscarica = kgindiscarica;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getNumlavoratori() {
		return this.numlavoratori;
	}

	public void setNumlavoratori(BigDecimal numlavoratori) {
		this.numlavoratori = numlavoratori;
	}


	public BigDecimal getQuantita() {
		return this.quantita;
	}

	public void setQuantita(BigDecimal quantita) {
		this.quantita = quantita;
	}


	public String getSolettaportante() {
		return this.solettaportante;
	}

	public void setSolettaportante(String solettaportante) {
		this.solettaportante = solettaportante;
	}


	public String getTipoamianto() {
		return this.tipoamianto;
	}

	public void setTipoamianto(String tipoamianto) {
		this.tipoamianto = tipoamianto;
	}


	public BigDecimal getTipoanagcommit() {
		return this.tipoanagcommit;
	}

	public void setTipoanagcommit(BigDecimal tipoanagcommit) {
		this.tipoanagcommit = tipoanagcommit;
	}


	public BigDecimal getTipoanagsubapp() {
		return this.tipoanagsubapp;
	}

	public void setTipoanagsubapp(BigDecimal tipoanagsubapp) {
		this.tipoanagsubapp = tipoanagsubapp;
	}


	public String getUbicazionelavoro() {
		return this.ubicazionelavoro;
	}

	public void setUbicazionelavoro(String ubicazionelavoro) {
		this.ubicazionelavoro = ubicazionelavoro;
	}


	public BigDecimal getUfpg() {
		return this.ufpg;
	}

	public void setUfpg(BigDecimal ufpg) {
		this.ufpg = ufpg;
	}


	public String getUrgenza() {
		return this.urgenza;
	}

	public void setUrgenza(String urgenza) {
		this.urgenza = urgenza;
	}


	public BigDecimal getUrgenzaaccettata() {
		return this.urgenzaaccettata;
	}

	public void setUrgenzaaccettata(BigDecimal urgenzaaccettata) {
		this.urgenzaaccettata = urgenzaaccettata;
	}


	//bi-directional many-to-one association to Ubicazioniamianto
	@OneToMany(mappedBy="pianidilavoroamianto")
	public List<Ubicazioniamianto> getUbicazioniamiantos() {
		return this.ubicazioniamiantos;
	}

	public void setUbicazioniamiantos(List<Ubicazioniamianto> ubicazioniamiantos) {
		this.ubicazioniamiantos = ubicazioniamiantos;
	}

	public Ubicazioniamianto addUbicazioniamianto(Ubicazioniamianto ubicazioniamianto) {
		getUbicazioniamiantos().add(ubicazioniamianto);
		ubicazioniamianto.setPianidilavoroamianto(this);

		return ubicazioniamianto;
	}

	public Ubicazioniamianto removeUbicazioniamianto(Ubicazioniamianto ubicazioniamianto) {
		getUbicazioniamiantos().remove(ubicazioniamianto);
		ubicazioniamianto.setPianidilavoroamianto(null);

		return ubicazioniamianto;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTASUBAPPALTATA")
	public Anagrafiche getDittaSubappaltata() {
		return this.dittaSubappaltata;
	}

	public void setDittaSubappaltata(Anagrafiche dittaSubappaltata) {
		this.dittaSubappaltata = dittaSubappaltata;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTALAVORO")
	public Anagrafiche getDittaLavoro() {
		return this.dittaLavoro;
	}

	public void setDittaLavoro(Anagrafiche dittaLavoro) {
		this.dittaLavoro = dittaLavoro;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTAESECUTRICE")
	public Anagrafiche getDittaEsecutrice() {
		return this.dittaEsecutrice;
	}

	public void setDittaEsecutrice(Anagrafiche dittaEsecutrice) {
		this.dittaEsecutrice = dittaEsecutrice;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCANTIERE")
	public Anagrafiche getCantiere() {
		return this.cantiere;
	}

	public void setCantiere(Anagrafiche cantiere) {
		this.cantiere = cantiere;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTRASPORTATORE")
	public Anagrafiche getTrasportatore() {
		return this.trasportatore;
	}

	public void setTrasportatore(Anagrafiche trasportatore) {
		this.trasportatore = trasportatore;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDISCARICA")
	public Anagrafiche getDiscarica() {
		return this.discarica;
	}

	public void setDiscarica(Anagrafiche discarica) {
		this.discarica = discarica;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTACOMMITTENTE")
	public Anagrafiche getDittaCommittente() {
		return this.dittaCommittente;
	}

	public void setDittaCommittente(Anagrafiche dittaCommittente) {
		this.dittaCommittente = dittaCommittente;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTAAFFIDATARIA")
	public Anagrafiche getDittaAffidataria() {
		return this.dittaAffidataria;
	}

	public void setDittaAffidataria(Anagrafiche dittaAffidataria) {
		this.dittaAffidataria = dittaAffidataria;
	}


	//bi-directional many-to-one association to Cantieri
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDACANTIERE")
	public Cantieri getSchedaCantiere() {
		return this.schedaCantiere;
	}

	public void setSchedaCantiere(Cantieri schedaCantiere) {
		this.schedaCantiere = schedaCantiere;
	}


	//bi-directional many-to-one association to Comuni
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDCOMUNEUBIC")
	public Comuni getUbicazioneComune() {
		return this.ubicazioneComune;
	}

	public void setUbicazioneComune(Comuni ubicazioneComune) {
		this.ubicazioneComune = ubicazioneComune;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTASUBAPPALTATA")
	public Ditte getSchedaDittaSubappaltata() {
		return this.schedaDittaSubappaltata;
	}

	public void setSchedaDittaSubappaltata(Ditte schedaDittaSubappaltata) {
		this.schedaDittaSubappaltata = schedaDittaSubappaltata;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTALAVORO")
	public Ditte getSchedaDittaLavoro() {
		return this.schedaDittaLavoro;
	}

	public void setSchedaDittaLavoro(Ditte schedaDittaLavoro) {
		this.schedaDittaLavoro = schedaDittaLavoro;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADISCARICA")
	public Ditte getSchedaDiscarica() {
		return this.schedaDiscarica;
	}

	public void setSchedaDiscarica(Ditte schedaDiscarica) {
		this.schedaDiscarica = schedaDiscarica;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDATRASPORTATORE")
	public Ditte getSchedaTrasportatore() {
		return this.schedaTrasportatore;
	}

	public void setSchedaTrasportatore(Ditte schedaTrasportatore) {
		this.schedaTrasportatore = schedaTrasportatore;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTAESECUTRICE")
	public Ditte getSchedaDittaEsecutrice() {
		return this.schedaDittaEsecutrice;
	}

	public void setSchedaDittaEsecutrice(Ditte schedaDittaEsecutrice) {
		this.schedaDittaEsecutrice = schedaDittaEsecutrice;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTAAFFIDATARIA")
	public Ditte getSchedaDittaAffidataria() {
		return this.schedaDittaAffidataria;
	}

	public void setSchedaDittaAffidataria(Ditte schedaDittaAffidataria) {
		this.schedaDittaAffidataria = schedaDittaAffidataria;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTACOMMITTENTE")
	public Ditte getSchedaDittaCommittente() {
		return this.schedaDittaCommittente;
	}

	public void setSchedaDittaCommittente(Ditte schedaDittaCommittente) {
		this.schedaDittaCommittente = schedaDittaCommittente;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOLAVORO")
	public Tabelle getTipoLavoro() {
		return this.tipoLavoro;
	}

	public void setTipoLavoro(Tabelle tipoLavoro) {
		this.tipoLavoro = tipoLavoro;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOEDIFICIO")
	public Tabelle getTipoEdificio() {
		return this.tipoEdificio;
	}

	public void setTipoEdificio(Tabelle tipoEdificio) {
		this.tipoEdificio = tipoEdificio;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="UNITAM")
	public Tabelle getUnitam() {
		return this.unitam;
	}

	public void setUnitam(Tabelle unitam) {
		this.unitam = unitam;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRESLAVORI")
	public Utenti getUtRespLavori() {
		return this.utRespLavori;
	}

	public void setUtRespLavori(Utenti utRespLavori) {
		this.utRespLavori = utRespLavori;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRESSICUREZZA")
	public Utenti getUtRespSicurezza() {
		return this.utRespSicurezza;
	}

	public void setUtRespSicurezza(Utenti utRespSicurezza) {
		this.utRespSicurezza = utRespSicurezza;
	}


	//bi-directional many-to-one association to Utenti
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRESSORVEGLIANZA")
	public Utenti getUtRespSorveglianza() {
		return this.utRespSorveglianza;
	}

	public void setUtRespSorveglianza(Utenti utenti3) {
		this.utRespSorveglianza = utenti3;
	}

}