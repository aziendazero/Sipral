package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the VISITESPECIALISTICHE database table.
 * 
 */
@Entity
public class Visitespecialistiche implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private long idvisitespecialistiche;

	@Temporal(TemporalType.DATE)
	private Date dataesito;

	@Temporal(TemporalType.DATE)
	private Date datavisita;

	private BigDecimal idfattorerischio;


	//private BigDecimal idschedaditta;

	private BigDecimal idstoricoutente;

	
	
	private String note;
	
	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDSCHEDADITTA", referencedColumnName="IDDITTE")
	private Ditte ditta;

	//bi-directional many-to-one association to Pratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA", referencedColumnName="IDPROCPRATICHE")
	private Pratiche pratica;
	
	//bi-directional many-to-one association to Dettaglivisite
	@OneToMany(mappedBy="visitespecialistiche")
	private List<Dettaglivisite> dettaglivisites;

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDUTENTE", referencedColumnName="IDANAGRAFICHE")
	private Anagrafiche anagraficaUtente;

	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGDITTA", referencedColumnName="IDANAGRAFICHE")
	private Anagrafiche anagraficaDitta;

	//bi-directional many-to-one association to Esamiobiettivi
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESAMEOBIETTIVO", referencedColumnName="IDESAMIOBIETTIVI")
	private Esamiobiettivi esamiobiettivi;

	//bi-directional many-to-one association to Medici
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMEDICOCURANTE", referencedColumnName="IDMEDICI")
	private Medici medici;

	//bi-directional many-to-one association to Operatori
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE", referencedColumnName="IDOPERATORI")
	private Operatori operatori;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDESITOVISITA", referencedColumnName="IDTABELLE")
	private Tabelle tabelle1;

	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDMOTIVOVISITA", referencedColumnName="IDTABELLE")
	private Tabelle tabelle2;
	
	public Visitespecialistiche() {
	}

	public long getIdvisitespecialistiche() {
		return this.idvisitespecialistiche;
	}

	public void setIdvisitespecialistiche(long idvisitespecialistiche) {
		this.idvisitespecialistiche = idvisitespecialistiche;
	}

	public Date getDataesito() {
		return this.dataesito;
	}

	public void setDataesito(Date dataesito) {
		this.dataesito = dataesito;
	}

	public Date getDatavisita() {
		return this.datavisita;
	}

	public void setDatavisita(Date datavisita) {
		this.datavisita = datavisita;
	}

	public BigDecimal getIdfattorerischio() {
		return this.idfattorerischio;
	}

	public void setIdfattorerischio(BigDecimal idfattorerischio) {
		this.idfattorerischio = idfattorerischio;
	}

	public BigDecimal getIdstoricoutente() {
		return this.idstoricoutente;
	}

	public void setIdstoricoutente(BigDecimal idstoricoutente) {
		this.idstoricoutente = idstoricoutente;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Ditte getDitta() {
	return this.ditta;
	}
	
	public void setDitta(Ditte ditta) {
	this.ditta = ditta;
	}
	
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}

	public List<Dettaglivisite> getDettaglivisites() {
		return this.dettaglivisites;
	}

	public void setDettaglivisites(List<Dettaglivisite> dettaglivisites) {
		this.dettaglivisites = dettaglivisites;
	}

	public Dettaglivisite addDettaglivisite(Dettaglivisite dettaglivisite) {
		getDettaglivisites().add(dettaglivisite);
		dettaglivisite.setVisitespecialistiche(this);

		return dettaglivisite;
	}

	public Dettaglivisite removeDettaglivisite(Dettaglivisite dettaglivisite) {
		getDettaglivisites().remove(dettaglivisite);
		dettaglivisite.setVisitespecialistiche(null);

		return dettaglivisite;
	}

	public Anagrafiche getAnagraficaUtente() {
		return this.anagraficaUtente;
	}

	public void setAnagraficaUtente(Anagrafiche anagrafiche1) {
		this.anagraficaUtente = anagrafiche1;
	}

	public Anagrafiche getAnagraficaDitta() {
		return this.anagraficaDitta;
	}

	public void setAnagraficaDitta(Anagrafiche anagrafiche2) {
		this.anagraficaDitta = anagrafiche2;
	}

	public Esamiobiettivi getEsamiobiettivi() {
		return this.esamiobiettivi;
	}

	public void setEsamiobiettivi(Esamiobiettivi esamiobiettivi) {
		this.esamiobiettivi = esamiobiettivi;
	}

	public Medici getMedici() {
		return this.medici;
	}

	public void setMedici(Medici medici) {
		this.medici = medici;
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

}