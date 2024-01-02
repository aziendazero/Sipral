package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the TABELLE database table.
 * 
 */
@Entity
@Table(name="TABELLE")
public class Tabelle implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idtabelle;
	private BigDecimal casi;
	private String codiceregionale;
	private BigDecimal codicetabella;
	private Date datafine;
	private Date datainizio;
	private Date dataorainserimento;
	private Date dataoramodifica;
	private String descrizione;
	private BigDecimal idoperatoreinserimento;
	private BigDecimal idoperatoremodifica;
	private BigDecimal idprestazione;
	private String note;
	private BigDecimal ordine;
	private BigDecimal peso;
	private String riferimento;
	private String riferimentoimport;
	private String riferimentoizs;
	private String statistiche;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private String tutteprocedure;
	private String utcodice;
	private String valoredefault;
	private List<AttivitaPrevnet> attivitaPrevnet;
	private Tabelle tipoInfrazione;
	private Tabelle ruolo;
	private Tabelle prodotto;

	public Tabelle() {
	}


	@Id
	public long getIdtabelle() {
		return this.idtabelle;
	}

	public void setIdtabelle(long idtabelle) {
		this.idtabelle = idtabelle;
	}


	public BigDecimal getCasi() {
		return this.casi;
	}

	public void setCasi(BigDecimal casi) {
		this.casi = casi;
	}


	public String getCodiceregionale() {
		return this.codiceregionale;
	}

	public void setCodiceregionale(String codiceregionale) {
		this.codiceregionale = codiceregionale;
	}


	public BigDecimal getCodicetabella() {
		return this.codicetabella;
	}

	public void setCodicetabella(BigDecimal codicetabella) {
		this.codicetabella = codicetabella;
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
	public Date getDataorainserimento() {
		return this.dataorainserimento;
	}

	public void setDataorainserimento(Date dataorainserimento) {
		this.dataorainserimento = dataorainserimento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataoramodifica() {
		return this.dataoramodifica;
	}

	public void setDataoramodifica(Date dataoramodifica) {
		this.dataoramodifica = dataoramodifica;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public BigDecimal getIdoperatoreinserimento() {
		return this.idoperatoreinserimento;
	}

	public void setIdoperatoreinserimento(BigDecimal idoperatoreinserimento) {
		this.idoperatoreinserimento = idoperatoreinserimento;
	}


	public BigDecimal getIdoperatoremodifica() {
		return this.idoperatoremodifica;
	}

	public void setIdoperatoremodifica(BigDecimal idoperatoremodifica) {
		this.idoperatoremodifica = idoperatoremodifica;
	}


	public BigDecimal getIdprestazione() {
		return this.idprestazione;
	}

	public void setIdprestazione(BigDecimal idprestazione) {
		this.idprestazione = idprestazione;
	}

	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getOrdine() {
		return this.ordine;
	}

	public void setOrdine(BigDecimal ordine) {
		this.ordine = ordine;
	}


	public BigDecimal getPeso() {
		return this.peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}


	public String getRiferimento() {
		return this.riferimento;
	}

	public void setRiferimento(String riferimento) {
		this.riferimento = riferimento;
	}


	public String getRiferimentoimport() {
		return this.riferimentoimport;
	}

	public void setRiferimentoimport(String riferimentoimport) {
		this.riferimentoimport = riferimentoimport;
	}


	public String getRiferimentoizs() {
		return this.riferimentoizs;
	}

	public void setRiferimentoizs(String riferimentoizs) {
		this.riferimentoizs = riferimentoizs;
	}


	public String getStatistiche() {
		return this.statistiche;
	}

	public void setStatistiche(String statistiche) {
		this.statistiche = statistiche;
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


	public String getTutteprocedure() {
		return this.tutteprocedure;
	}

	public void setTutteprocedure(String tutteprocedure) {
		this.tutteprocedure = tutteprocedure;
	}


	public String getUtcodice() {
		return this.utcodice;
	}

	public void setUtcodice(String utcodice) {
		this.utcodice = utcodice;
	}


	public String getValoredefault() {
		return this.valoredefault;
	}

	public void setValoredefault(String valoredefault) {
		this.valoredefault = valoredefault;
	}

	//bi-directional many-to-many association to Attivita
	@ManyToMany
	@JoinTable(
		name="GRUPPIATTIVITA"
		, joinColumns={
			@JoinColumn(name="IDGRUPPO")
			}
		, inverseJoinColumns={
			@JoinColumn(name="IDATTIVITA")
			}
		)
	public List<AttivitaPrevnet> getAttivitaPrevnet() {
		return this.attivitaPrevnet;
	}

	public void setAttivitaPrevnet(List<AttivitaPrevnet> attivitaPrevnet) {
		this.attivitaPrevnet = attivitaPrevnet;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOINFRAZIONE")
	public Tabelle getTipoInfrazione() {
		return this.tipoInfrazione;
	}

	public void setTipoInfrazione(Tabelle tipoInfrazione) {
		this.tipoInfrazione = tipoInfrazione;
	}
	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRUOLO")
	public Tabelle getRuolo() {
		return this.ruolo;
	}

	public void setRuolo(Tabelle ruolo) {
		this.ruolo = ruolo;
	}

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPRODOTTO")
	public Tabelle getProdotto() {
		return this.prodotto;
	}

	public void setProdotto(Tabelle prodotto) {
		this.prodotto = prodotto;
	}

}