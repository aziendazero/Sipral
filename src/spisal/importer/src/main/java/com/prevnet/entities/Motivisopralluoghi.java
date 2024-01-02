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
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the MOTIVISOPRALLUOGHI database table.
 * 
 */
@Entity
public class Motivisopralluoghi implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idmotivisop;
	private String codiceregionale;
	private Date datafine;
	private Date datainizio;
	private String descrizione;
	private BigDecimal idprestazione;
	private BigDecimal idprogrammazione;
	private BigDecimal idriferimento;
	private BigDecimal idsettore;
	private String note;
	private Date riferimento;
	private String riferimentoimport;
	private String riferimentoizs;
	private Date synchdate;
	private String synchflag;
	private String synchid;
	private Date timestampinsmod;
	private String tutteprocedure;
	private String utcodice;
	private List<Altridatipratica> altridatipraticas1;
	private List<Altridatipratica> altridatipraticas2;
	private List<Assegnsoprall> assegnsopralls1;
	private List<Assegnsoprall> assegnsopralls2;
	private Tabelle tabelle;
	private List<Sopralluoghidip> sopralluoghidips1;
	private List<Sopralluoghidip> sopralluoghidips2;

	public Motivisopralluoghi() {
	}


	@Id
	public long getIdmotivisop() {
		return this.idmotivisop;
	}

	public void setIdmotivisop(long idmotivisop) {
		this.idmotivisop = idmotivisop;
	}


	public String getCodiceregionale() {
		return this.codiceregionale;
	}

	public void setCodiceregionale(String codiceregionale) {
		this.codiceregionale = codiceregionale;
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


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public BigDecimal getIdprestazione() {
		return this.idprestazione;
	}

	public void setIdprestazione(BigDecimal idprestazione) {
		this.idprestazione = idprestazione;
	}


	public BigDecimal getIdprogrammazione() {
		return this.idprogrammazione;
	}

	public void setIdprogrammazione(BigDecimal idprogrammazione) {
		this.idprogrammazione = idprogrammazione;
	}


	public BigDecimal getIdriferimento() {
		return this.idriferimento;
	}

	public void setIdriferimento(BigDecimal idriferimento) {
		this.idriferimento = idriferimento;
	}


	public BigDecimal getIdsettore() {
		return this.idsettore;
	}

	public void setIdsettore(BigDecimal idsettore) {
		this.idsettore = idsettore;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	@Temporal(TemporalType.DATE)
	public Date getRiferimento() {
		return this.riferimento;
	}

	public void setRiferimento(Date riferimento) {
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


	//bi-directional many-to-one association to Altridatipratica
	@OneToMany(mappedBy="motivisopralluoghi1")
	public List<Altridatipratica> getAltridatipraticas1() {
		return this.altridatipraticas1;
	}

	public void setAltridatipraticas1(List<Altridatipratica> altridatipraticas1) {
		this.altridatipraticas1 = altridatipraticas1;
	}

	public Altridatipratica addAltridatipraticas1(Altridatipratica altridatipraticas1) {
		getAltridatipraticas1().add(altridatipraticas1);
		altridatipraticas1.setMotivisopralluoghi1(this);

		return altridatipraticas1;
	}

	public Altridatipratica removeAltridatipraticas1(Altridatipratica altridatipraticas1) {
		getAltridatipraticas1().remove(altridatipraticas1);
		altridatipraticas1.setMotivisopralluoghi1(null);

		return altridatipraticas1;
	}


	//bi-directional many-to-one association to Altridatipratica
	@OneToMany(mappedBy="motivisopralluoghi2")
	public List<Altridatipratica> getAltridatipraticas2() {
		return this.altridatipraticas2;
	}

	public void setAltridatipraticas2(List<Altridatipratica> altridatipraticas2) {
		this.altridatipraticas2 = altridatipraticas2;
	}

	public Altridatipratica addAltridatipraticas2(Altridatipratica altridatipraticas2) {
		getAltridatipraticas2().add(altridatipraticas2);
		altridatipraticas2.setMotivisopralluoghi2(this);

		return altridatipraticas2;
	}

	public Altridatipratica removeAltridatipraticas2(Altridatipratica altridatipraticas2) {
		getAltridatipraticas2().remove(altridatipraticas2);
		altridatipraticas2.setMotivisopralluoghi2(null);

		return altridatipraticas2;
	}


	//bi-directional many-to-one association to Assegnsoprall
	@OneToMany(mappedBy="motivisopralluoghi1")
	public List<Assegnsoprall> getAssegnsopralls1() {
		return this.assegnsopralls1;
	}

	public void setAssegnsopralls1(List<Assegnsoprall> assegnsopralls1) {
		this.assegnsopralls1 = assegnsopralls1;
	}

	public Assegnsoprall addAssegnsopralls1(Assegnsoprall assegnsopralls1) {
		getAssegnsopralls1().add(assegnsopralls1);
		assegnsopralls1.setMotivisopralluoghi1(this);

		return assegnsopralls1;
	}

	public Assegnsoprall removeAssegnsopralls1(Assegnsoprall assegnsopralls1) {
		getAssegnsopralls1().remove(assegnsopralls1);
		assegnsopralls1.setMotivisopralluoghi1(null);

		return assegnsopralls1;
	}


	//bi-directional many-to-one association to Assegnsoprall
	@OneToMany(mappedBy="motivisopralluoghi2")
	public List<Assegnsoprall> getAssegnsopralls2() {
		return this.assegnsopralls2;
	}

	public void setAssegnsopralls2(List<Assegnsoprall> assegnsopralls2) {
		this.assegnsopralls2 = assegnsopralls2;
	}

	public Assegnsoprall addAssegnsopralls2(Assegnsoprall assegnsopralls2) {
		getAssegnsopralls2().add(assegnsopralls2);
		assegnsopralls2.setMotivisopralluoghi2(this);

		return assegnsopralls2;
	}

	public Assegnsoprall removeAssegnsopralls2(Assegnsoprall assegnsopralls2) {
		getAssegnsopralls2().remove(assegnsopralls2);
		assegnsopralls2.setMotivisopralluoghi2(null);

		return assegnsopralls2;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOPRODOTTO")
	public Tabelle getTabelle() {
		return this.tabelle;
	}

	public void setTabelle(Tabelle tabelle) {
		this.tabelle = tabelle;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@OneToMany(mappedBy="motivo")
	public List<Sopralluoghidip> getSopralluoghidips1() {
		return this.sopralluoghidips1;
	}

	public void setSopralluoghidips1(List<Sopralluoghidip> sopralluoghidips1) {
		this.sopralluoghidips1 = sopralluoghidips1;
	}

	public Sopralluoghidip addSopralluoghidips1(Sopralluoghidip sopralluoghidips1) {
		getSopralluoghidips1().add(sopralluoghidips1);
		sopralluoghidips1.setMotivo(this);

		return sopralluoghidips1;
	}

	public Sopralluoghidip removeSopralluoghidips1(Sopralluoghidip sopralluoghidips1) {
		getSopralluoghidips1().remove(sopralluoghidips1);
		sopralluoghidips1.setMotivo(null);

		return sopralluoghidips1;
	}


	//bi-directional many-to-one association to Sopralluoghidip
	@OneToMany(mappedBy="tipoMotivo")
	public List<Sopralluoghidip> getSopralluoghidips2() {
		return this.sopralluoghidips2;
	}

	public void setSopralluoghidips2(List<Sopralluoghidip> sopralluoghidips2) {
		this.sopralluoghidips2 = sopralluoghidips2;
	}

	public Sopralluoghidip addSopralluoghidips2(Sopralluoghidip sopralluoghidips2) {
		getSopralluoghidips2().add(sopralluoghidips2);
		sopralluoghidips2.setTipoMotivo(this);

		return sopralluoghidips2;
	}

	public Sopralluoghidip removeSopralluoghidips2(Sopralluoghidip sopralluoghidips2) {
		getSopralluoghidips2().remove(sopralluoghidips2);
		sopralluoghidips2.setTipoMotivo(null);

		return sopralluoghidips2;
	}

}