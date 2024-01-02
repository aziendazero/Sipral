package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the RICORSIGIUDIZIMEDICI database table.
 * 
 */
@Entity
public class Ricorsigiudizimedici implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idricorsi;
	private String chkammissibile;
	private Date dataaccertamento;
	private Date dataconvocazione;
	private Date datanotifica;
	private Date dataricorso;
	private Date datavisita;
	private Date datavisitacollegiale;
	private Date datavisitaistruttoria;
	private Anagrafiche ditta;
	private Utenti datore;
	private Tabelle esito;
	private Utenti lavoratore;
	private BigDecimal idmacroprodotto;
	private Medici medico;
	private Tabelle motivo;
	private BigDecimal idpatologia;
	private Pratiche pratica;
	private BigDecimal idprotocollo;
	private Tabelle qualifica;
	private Fattoririschio rischiogen;
	private Ditte schedaditta;
	private BigDecimal idsirsap;
	private BigDecimal idstoricoutentedatore;
	private BigDecimal idstoricoutentelavoratore;
	private BigDecimal idtiporicorso;
	private String motivazione;
	private String noteesito;
	private String nprotocollo;

	public Ricorsigiudizimedici() {
	}


	@Id
	public long getIdricorsi() {
		return this.idricorsi;
	}

	public void setIdricorsi(long idricorsi) {
		this.idricorsi = idricorsi;
	}


	public String getChkammissibile() {
		return this.chkammissibile;
	}

	public void setChkammissibile(String chkammissibile) {
		this.chkammissibile = chkammissibile;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataaccertamento() {
		return this.dataaccertamento;
	}

	public void setDataaccertamento(Date dataaccertamento) {
		this.dataaccertamento = dataaccertamento;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataconvocazione() {
		return this.dataconvocazione;
	}

	public void setDataconvocazione(Date dataconvocazione) {
		this.dataconvocazione = dataconvocazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatanotifica() {
		return this.datanotifica;
	}

	public void setDatanotifica(Date datanotifica) {
		this.datanotifica = datanotifica;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataricorso() {
		return this.dataricorso;
	}

	public void setDataricorso(Date dataricorso) {
		this.dataricorso = dataricorso;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavisita() {
		return this.datavisita;
	}

	public void setDatavisita(Date datavisita) {
		this.datavisita = datavisita;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavisitacollegiale() {
		return this.datavisitacollegiale;
	}

	public void setDatavisitacollegiale(Date datavisitacollegiale) {
		this.datavisitacollegiale = datavisitacollegiale;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatavisitaistruttoria() {
		return this.datavisitaistruttoria;
	}

	public void setDatavisitaistruttoria(Date datavisitaistruttoria) {
		this.datavisitaistruttoria = datavisitaistruttoria;
	}

	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDAZIENDA")
	public Anagrafiche getDitta() {
		return this.ditta;
	}

	public void setDitta(Anagrafiche idazienda) {
		this.ditta = idazienda;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDATORE")
	public Utenti getDatore() {
		return this.datore;
	}

	public void setDatore(Utenti datore) {
		this.datore = datore;
	}


	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITO")
	public Tabelle getEsito() {
		return this.esito;
	}

	public void setEsito(Tabelle esito) {
		this.esito = esito;
	}


	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDLAVORATORE")
	public Utenti getLavoratore() {
		return this.lavoratore;
	}

	public void setLavoratore(Utenti lavoratore) {
		this.lavoratore = lavoratore;
	}


	public BigDecimal getIdmacroprodotto() {
		return this.idmacroprodotto;
	}

	public void setIdmacroprodotto(BigDecimal idmacroprodotto) {
		this.idmacroprodotto = idmacroprodotto;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICO")
	public Medici getMedico() {
		return this.medico;
	}

	public void setMedico(Medici medico) {
		this.medico = medico;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVO")
	public Tabelle getMotivo() {
		return this.motivo;
	}

	public void setMotivo(Tabelle motivo) {
		this.motivo = motivo;
	}


	public BigDecimal getIdpatologia() {
		return this.idpatologia;
	}

	public void setIdpatologia(BigDecimal idpatologia) {
		this.idpatologia = idpatologia;
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
	@JoinColumn(name="IDQUALIFICA")
	public Tabelle getQualifica() {
		return this.qualifica;
	}

	public void setQualifica(Tabelle qualifica) {
		this.qualifica = qualifica;
	}


	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDRISCHIOGEN")
	public Fattoririschio getRischiogen() {
		return this.rischiogen;
	}

	public void setRischiogen(Fattoririschio rischiogen) {
		this.rischiogen = rischiogen;
	}


	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA")
	public Ditte getSchedaditta() {
		return this.schedaditta;
	}

	public void setSchedaditta(Ditte schedaditta) {
		this.schedaditta = schedaditta;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}


	public BigDecimal getIdstoricoutentedatore() {
		return this.idstoricoutentedatore;
	}

	public void setIdstoricoutentedatore(BigDecimal idstoricoutentedatore) {
		this.idstoricoutentedatore = idstoricoutentedatore;
	}


	public BigDecimal getIdstoricoutentelavoratore() {
		return this.idstoricoutentelavoratore;
	}

	public void setIdstoricoutentelavoratore(BigDecimal idstoricoutentelavoratore) {
		this.idstoricoutentelavoratore = idstoricoutentelavoratore;
	}


	public BigDecimal getIdtiporicorso() {
		return this.idtiporicorso;
	}

	public void setIdtiporicorso(BigDecimal idtiporicorso) {
		this.idtiporicorso = idtiporicorso;
	}


	@Column(name="MOTIVO")
	public String getMotivazione() {
		return this.motivazione;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}


	public String getNoteesito() {
		return this.noteesito;
	}

	public void setNoteesito(String noteesito) {
		this.noteesito = noteesito;
	}


	public String getNprotocollo() {
		return this.nprotocollo;
	}

	public void setNprotocollo(String nprotocollo) {
		this.nprotocollo = nprotocollo;
	}

}