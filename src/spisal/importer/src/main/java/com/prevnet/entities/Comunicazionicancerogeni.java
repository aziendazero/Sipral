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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the COMUNICAZIONICANCEROGENI database table.
 * 
 */
@Entity
@NamedQuery(name="Comunicazionicancerogeni.findAll", query="SELECT c FROM Comunicazionicancerogeni c")
public class Comunicazionicancerogeni implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idcomunicazionicancerogeni;
	private String cessazione;
	private Date datacessazione;
	private Date datacomunicazione;
	private BigDecimal idcessazione;
	private String note;
	private BigDecimal totaledonne;
	private BigDecimal totaledonneesposte;
	private BigDecimal totalelavoratori;
	private BigDecimal totaleuomini;
	private BigDecimal totaleuominiesposti;
	private Anagrafiche anagrafiche;
	private Ditte ditte;
	private List<Lavoratoriespostiagenti> lavoratoriespostiagentis1;
	private List<Lavoratoriespostiagenti> lavoratoriespostiagentis2;
	private List<Preparaticancerogeni> preparaticancerogenis1;
	private List<Preparaticancerogeni> preparaticancerogenis2;
	private List<Sistemicancerogeni> sistemicancerogenis1;
	private List<Sistemicancerogeni> sistemicancerogenis2;
	private List<Sostanzecancerogene> sostanzecancerogenes1;
	private List<Sostanzecancerogene> sostanzecancerogenes2;

	public Comunicazionicancerogeni() {
	}


	@Id
	public long getIdcomunicazionicancerogeni() {
		return this.idcomunicazionicancerogeni;
	}

	public void setIdcomunicazionicancerogeni(long idcomunicazionicancerogeni) {
		this.idcomunicazionicancerogeni = idcomunicazionicancerogeni;
	}


	public String getCessazione() {
		return this.cessazione;
	}

	public void setCessazione(String cessazione) {
		this.cessazione = cessazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacessazione() {
		return this.datacessazione;
	}

	public void setDatacessazione(Date datacessazione) {
		this.datacessazione = datacessazione;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatacomunicazione() {
		return this.datacomunicazione;
	}

	public void setDatacomunicazione(Date datacomunicazione) {
		this.datacomunicazione = datacomunicazione;
	}


	public BigDecimal getIdcessazione() {
		return this.idcessazione;
	}

	public void setIdcessazione(BigDecimal idcessazione) {
		this.idcessazione = idcessazione;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getTotaledonne() {
		return this.totaledonne;
	}

	public void setTotaledonne(BigDecimal totaledonne) {
		this.totaledonne = totaledonne;
	}


	public BigDecimal getTotaledonneesposte() {
		return this.totaledonneesposte;
	}

	public void setTotaledonneesposte(BigDecimal totaledonneesposte) {
		this.totaledonneesposte = totaledonneesposte;
	}


	public BigDecimal getTotalelavoratori() {
		return this.totalelavoratori;
	}

	public void setTotalelavoratori(BigDecimal totalelavoratori) {
		this.totalelavoratori = totalelavoratori;
	}


	public BigDecimal getTotaleuomini() {
		return this.totaleuomini;
	}

	public void setTotaleuomini(BigDecimal totaleuomini) {
		this.totaleuomini = totaleuomini;
	}


	public BigDecimal getTotaleuominiesposti() {
		return this.totaleuominiesposti;
	}

	public void setTotaleuominiesposti(BigDecimal totaleuominiesposti) {
		this.totaleuominiesposti = totaleuominiesposti;
	}


	//bi-directional many-to-one association to Anagrafiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDANAGRAFICA")
	public Anagrafiche getAnagrafiche() {
		return this.anagrafiche;
	}

	public void setAnagrafiche(Anagrafiche anagrafiche) {
		this.anagrafiche = anagrafiche;
	}


	//bi-directional many-to-one association to Ditte
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDDITTA")
	public Ditte getDitte() {
		return this.ditte;
	}

	public void setDitte(Ditte ditte) {
		this.ditte = ditte;
	}


	//bi-directional many-to-one association to Lavoratoriespostiagenti
	@OneToMany(mappedBy="comunicazionicancerogeni1")
	public List<Lavoratoriespostiagenti> getLavoratoriespostiagentis1() {
		return this.lavoratoriespostiagentis1;
	}

	public void setLavoratoriespostiagentis1(List<Lavoratoriespostiagenti> lavoratoriespostiagentis1) {
		this.lavoratoriespostiagentis1 = lavoratoriespostiagentis1;
	}

	public Lavoratoriespostiagenti addLavoratoriespostiagentis1(Lavoratoriespostiagenti lavoratoriespostiagentis1) {
		getLavoratoriespostiagentis1().add(lavoratoriespostiagentis1);
		lavoratoriespostiagentis1.setComunicazionicancerogeni1(this);

		return lavoratoriespostiagentis1;
	}

	public Lavoratoriespostiagenti removeLavoratoriespostiagentis1(Lavoratoriespostiagenti lavoratoriespostiagentis1) {
		getLavoratoriespostiagentis1().remove(lavoratoriespostiagentis1);
		lavoratoriespostiagentis1.setComunicazionicancerogeni1(null);

		return lavoratoriespostiagentis1;
	}


	//bi-directional many-to-one association to Lavoratoriespostiagenti
	@OneToMany(mappedBy="comunicazionicancerogeni2")
	public List<Lavoratoriespostiagenti> getLavoratoriespostiagentis2() {
		return this.lavoratoriespostiagentis2;
	}

	public void setLavoratoriespostiagentis2(List<Lavoratoriespostiagenti> lavoratoriespostiagentis2) {
		this.lavoratoriespostiagentis2 = lavoratoriespostiagentis2;
	}

	public Lavoratoriespostiagenti addLavoratoriespostiagentis2(Lavoratoriespostiagenti lavoratoriespostiagentis2) {
		getLavoratoriespostiagentis2().add(lavoratoriespostiagentis2);
		lavoratoriespostiagentis2.setComunicazionicancerogeni2(this);

		return lavoratoriespostiagentis2;
	}

	public Lavoratoriespostiagenti removeLavoratoriespostiagentis2(Lavoratoriespostiagenti lavoratoriespostiagentis2) {
		getLavoratoriespostiagentis2().remove(lavoratoriespostiagentis2);
		lavoratoriespostiagentis2.setComunicazionicancerogeni2(null);

		return lavoratoriespostiagentis2;
	}


	//bi-directional many-to-one association to Preparaticancerogeni
	@OneToMany(mappedBy="comunicazionicancerogeni1")
	public List<Preparaticancerogeni> getPreparaticancerogenis1() {
		return this.preparaticancerogenis1;
	}

	public void setPreparaticancerogenis1(List<Preparaticancerogeni> preparaticancerogenis1) {
		this.preparaticancerogenis1 = preparaticancerogenis1;
	}

	public Preparaticancerogeni addPreparaticancerogenis1(Preparaticancerogeni preparaticancerogenis1) {
		getPreparaticancerogenis1().add(preparaticancerogenis1);
		preparaticancerogenis1.setComunicazionicancerogeni1(this);

		return preparaticancerogenis1;
	}

	public Preparaticancerogeni removePreparaticancerogenis1(Preparaticancerogeni preparaticancerogenis1) {
		getPreparaticancerogenis1().remove(preparaticancerogenis1);
		preparaticancerogenis1.setComunicazionicancerogeni1(null);

		return preparaticancerogenis1;
	}


	//bi-directional many-to-one association to Preparaticancerogeni
	@OneToMany(mappedBy="comunicazionicancerogeni2")
	public List<Preparaticancerogeni> getPreparaticancerogenis2() {
		return this.preparaticancerogenis2;
	}

	public void setPreparaticancerogenis2(List<Preparaticancerogeni> preparaticancerogenis2) {
		this.preparaticancerogenis2 = preparaticancerogenis2;
	}

	public Preparaticancerogeni addPreparaticancerogenis2(Preparaticancerogeni preparaticancerogenis2) {
		getPreparaticancerogenis2().add(preparaticancerogenis2);
		preparaticancerogenis2.setComunicazionicancerogeni2(this);

		return preparaticancerogenis2;
	}

	public Preparaticancerogeni removePreparaticancerogenis2(Preparaticancerogeni preparaticancerogenis2) {
		getPreparaticancerogenis2().remove(preparaticancerogenis2);
		preparaticancerogenis2.setComunicazionicancerogeni2(null);

		return preparaticancerogenis2;
	}


	//bi-directional many-to-one association to Sistemicancerogeni
	@OneToMany(mappedBy="comunicazionicancerogeni1")
	public List<Sistemicancerogeni> getSistemicancerogenis1() {
		return this.sistemicancerogenis1;
	}

	public void setSistemicancerogenis1(List<Sistemicancerogeni> sistemicancerogenis1) {
		this.sistemicancerogenis1 = sistemicancerogenis1;
	}

	public Sistemicancerogeni addSistemicancerogenis1(Sistemicancerogeni sistemicancerogenis1) {
		getSistemicancerogenis1().add(sistemicancerogenis1);
		sistemicancerogenis1.setComunicazionicancerogeni1(this);

		return sistemicancerogenis1;
	}

	public Sistemicancerogeni removeSistemicancerogenis1(Sistemicancerogeni sistemicancerogenis1) {
		getSistemicancerogenis1().remove(sistemicancerogenis1);
		sistemicancerogenis1.setComunicazionicancerogeni1(null);

		return sistemicancerogenis1;
	}


	//bi-directional many-to-one association to Sistemicancerogeni
	@OneToMany(mappedBy="comunicazionicancerogeni2")
	public List<Sistemicancerogeni> getSistemicancerogenis2() {
		return this.sistemicancerogenis2;
	}

	public void setSistemicancerogenis2(List<Sistemicancerogeni> sistemicancerogenis2) {
		this.sistemicancerogenis2 = sistemicancerogenis2;
	}

	public Sistemicancerogeni addSistemicancerogenis2(Sistemicancerogeni sistemicancerogenis2) {
		getSistemicancerogenis2().add(sistemicancerogenis2);
		sistemicancerogenis2.setComunicazionicancerogeni2(this);

		return sistemicancerogenis2;
	}

	public Sistemicancerogeni removeSistemicancerogenis2(Sistemicancerogeni sistemicancerogenis2) {
		getSistemicancerogenis2().remove(sistemicancerogenis2);
		sistemicancerogenis2.setComunicazionicancerogeni2(null);

		return sistemicancerogenis2;
	}


	//bi-directional many-to-one association to Sostanzecancerogene
	@OneToMany(mappedBy="comunicazionicancerogeni1")
	public List<Sostanzecancerogene> getSostanzecancerogenes1() {
		return this.sostanzecancerogenes1;
	}

	public void setSostanzecancerogenes1(List<Sostanzecancerogene> sostanzecancerogenes1) {
		this.sostanzecancerogenes1 = sostanzecancerogenes1;
	}

	public Sostanzecancerogene addSostanzecancerogenes1(Sostanzecancerogene sostanzecancerogenes1) {
		getSostanzecancerogenes1().add(sostanzecancerogenes1);
		sostanzecancerogenes1.setComunicazionicancerogeni1(this);

		return sostanzecancerogenes1;
	}

	public Sostanzecancerogene removeSostanzecancerogenes1(Sostanzecancerogene sostanzecancerogenes1) {
		getSostanzecancerogenes1().remove(sostanzecancerogenes1);
		sostanzecancerogenes1.setComunicazionicancerogeni1(null);

		return sostanzecancerogenes1;
	}


	//bi-directional many-to-one association to Sostanzecancerogene
	@OneToMany(mappedBy="comunicazionicancerogeni2")
	public List<Sostanzecancerogene> getSostanzecancerogenes2() {
		return this.sostanzecancerogenes2;
	}

	public void setSostanzecancerogenes2(List<Sostanzecancerogene> sostanzecancerogenes2) {
		this.sostanzecancerogenes2 = sostanzecancerogenes2;
	}

	public Sostanzecancerogene addSostanzecancerogenes2(Sostanzecancerogene sostanzecancerogenes2) {
		getSostanzecancerogenes2().add(sostanzecancerogenes2);
		sostanzecancerogenes2.setComunicazionicancerogeni2(this);

		return sostanzecancerogenes2;
	}

	public Sostanzecancerogene removeSostanzecancerogenes2(Sostanzecancerogene sostanzecancerogenes2) {
		getSostanzecancerogenes2().remove(sostanzecancerogenes2);
		sostanzecancerogenes2.setComunicazionicancerogeni2(null);

		return sostanzecancerogenes2;
	}

}