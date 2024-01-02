package com.prevnet.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the FATTORIRISCHIO database table.
 * 
 */
@Entity
public class Fattoririschio implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idfattoririschio;
	private String codice;
	private String codiceregionale;
	//private Date datafinevalidita;
	//private Date datainiziovalidita;
	private String descrizione;
	private String esposizione;
	private String flagcancerogeno;
	private String legislazione;
	private String numerocas;
	private List<Lavoratoriespostiagenti> lavoratoriespostiagentis;
	private List<Livellicancerogeni> livellicancerogenis;
	private List<Preparaticancerogeni> preparaticancerogenis;
	private List<Sostanzecancerogene> sostanzecancerogenes;
	private List<Malattieprofessionali> malattieprofessionalis;

	public Fattoririschio() {
	}


	@Id
	public long getIdfattoririschio() {
		return this.idfattoririschio;
	}

	public void setIdfattoririschio(long idfattoririschio) {
		this.idfattoririschio = idfattoririschio;
	}


	public String getCodice() {
		return this.codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}


	public String getCodiceregionale() {
		return this.codiceregionale;
	}

	public void setCodiceregionale(String codiceregionale) {
		this.codiceregionale = codiceregionale;
	}

	/*
	@Temporal(TemporalType.DATE)
	public Date getDatafinevalidita() {
		return this.datafinevalidita;
	}

	public void setDatafinevalidita(Date datafinevalidita) {
		this.datafinevalidita = datafinevalidita;
	}


	@Temporal(TemporalType.DATE)
	public Date getDatainiziovalidita() {
		return this.datainiziovalidita;
	}

	public void setDatainiziovalidita(Date datainiziovalidita) {
		this.datainiziovalidita = datainiziovalidita;
	}
	*/

	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public String getEsposizione() {
		return this.esposizione;
	}

	public void setEsposizione(String esposizione) {
		this.esposizione = esposizione;
	}


	public String getFlagcancerogeno() {
		return this.flagcancerogeno;
	}

	public void setFlagcancerogeno(String flagcancerogeno) {
		this.flagcancerogeno = flagcancerogeno;
	}


	@Lob
	public String getLegislazione() {
		return this.legislazione;
	}

	public void setLegislazione(String legislazione) {
		this.legislazione = legislazione;
	}


	public String getNumerocas() {
		return this.numerocas;
	}

	public void setNumerocas(String numerocas) {
		this.numerocas = numerocas;
	}


	//bi-directional many-to-one association to Lavoratoriespostiagenti
	@OneToMany(mappedBy="fattoririschio")
	public List<Lavoratoriespostiagenti> getLavoratoriespostiagentis() {
		return this.lavoratoriespostiagentis;
	}

	public void setLavoratoriespostiagentis(List<Lavoratoriespostiagenti> lavoratoriespostiagentis) {
		this.lavoratoriespostiagentis = lavoratoriespostiagentis;
	}

	public Lavoratoriespostiagenti addLavoratoriespostiagenti(Lavoratoriespostiagenti lavoratoriespostiagenti) {
		getLavoratoriespostiagentis().add(lavoratoriespostiagenti);
		lavoratoriespostiagenti.setFattoririschio(this);

		return lavoratoriespostiagenti;
	}

	public Lavoratoriespostiagenti removeLavoratoriespostiagenti(Lavoratoriespostiagenti lavoratoriespostiagenti) {
		getLavoratoriespostiagentis().remove(lavoratoriespostiagenti);
		lavoratoriespostiagenti.setFattoririschio(null);

		return lavoratoriespostiagenti;
	}


	//bi-directional many-to-one association to Livellicancerogeni
	@OneToMany(mappedBy="fattoririschio")
	public List<Livellicancerogeni> getLivellicancerogenis() {
		return this.livellicancerogenis;
	}

	public void setLivellicancerogenis(List<Livellicancerogeni> livellicancerogenis) {
		this.livellicancerogenis = livellicancerogenis;
	}

	public Livellicancerogeni addLivellicancerogeni(Livellicancerogeni livellicancerogeni) {
		getLivellicancerogenis().add(livellicancerogeni);
		livellicancerogeni.setFattoririschio(this);

		return livellicancerogeni;
	}

	public Livellicancerogeni removeLivellicancerogeni(Livellicancerogeni livellicancerogeni) {
		getLivellicancerogenis().remove(livellicancerogeni);
		livellicancerogeni.setFattoririschio(null);

		return livellicancerogeni;
	}


	//bi-directional many-to-one association to Preparaticancerogeni
	@OneToMany(mappedBy="fattoririschio")
	public List<Preparaticancerogeni> getPreparaticancerogenis() {
		return this.preparaticancerogenis;
	}

	public void setPreparaticancerogenis(List<Preparaticancerogeni> preparaticancerogenis) {
		this.preparaticancerogenis = preparaticancerogenis;
	}

	public Preparaticancerogeni addPreparaticancerogeni(Preparaticancerogeni preparaticancerogeni) {
		getPreparaticancerogenis().add(preparaticancerogeni);
		preparaticancerogeni.setFattoririschio(this);

		return preparaticancerogeni;
	}

	public Preparaticancerogeni removePreparaticancerogeni(Preparaticancerogeni preparaticancerogeni) {
		getPreparaticancerogenis().remove(preparaticancerogeni);
		preparaticancerogeni.setFattoririschio(null);

		return preparaticancerogeni;
	}


	//bi-directional many-to-one association to Sostanzecancerogene
	@OneToMany(mappedBy="fattoririschio")
	public List<Sostanzecancerogene> getSostanzecancerogenes() {
		return this.sostanzecancerogenes;
	}

	public void setSostanzecancerogenes(List<Sostanzecancerogene> sostanzecancerogenes) {
		this.sostanzecancerogenes = sostanzecancerogenes;
	}

	public Sostanzecancerogene addSostanzecancerogene(Sostanzecancerogene sostanzecancerogene) {
		getSostanzecancerogenes().add(sostanzecancerogene);
		sostanzecancerogene.setFattoririschio(this);

		return sostanzecancerogene;
	}

	public Sostanzecancerogene removeSostanzecancerogene(Sostanzecancerogene sostanzecancerogene) {
		getSostanzecancerogenes().remove(sostanzecancerogene);
		sostanzecancerogene.setFattoririschio(null);

		return sostanzecancerogene;
	}


	//bi-directional many-to-one association to Malattieprofessionali
	@OneToMany(mappedBy="fattoririschio")
	public List<Malattieprofessionali> getMalattieprofessionalis() {
		return this.malattieprofessionalis;
	}

	public void setMalattieprofessionalis(List<Malattieprofessionali> malattieprofessionalis) {
		this.malattieprofessionalis = malattieprofessionalis;
	}

	public Malattieprofessionali addMalattieprofessionali(Malattieprofessionali malattieprofessionali) {
		getMalattieprofessionalis().add(malattieprofessionali);
		malattieprofessionali.setFattoririschio(this);

		return malattieprofessionali;
	}

	public Malattieprofessionali removeMalattieprofessionali(Malattieprofessionali malattieprofessionali) {
		getMalattieprofessionalis().remove(malattieprofessionali);
		malattieprofessionali.setFattoririschio(null);

		return malattieprofessionali;
	}

}