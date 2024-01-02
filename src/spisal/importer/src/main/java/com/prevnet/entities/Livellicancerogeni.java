package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the LIVELLICANCEROGENI database table.
 * 
 */
@Entity
@NamedQuery(name="Livellicancerogeni.findAll", query="SELECT l FROM Livellicancerogeni l")
public class Livellicancerogeni implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idlivellicancerogeni;
	private String descrizione;
	private String imported;
	private BigDecimal valorecodificato;
	private List<Lavoratoriespostiagenti> lavoratoriespostiagentis;
	private Fattoririschio fattoririschio;
	private Tabelle metodo;
	private Tabelle unitaMisura;

	public Livellicancerogeni() {
	}


	@Id
	public long getIdlivellicancerogeni() {
		return this.idlivellicancerogeni;
	}

	public void setIdlivellicancerogeni(long idlivellicancerogeni) {
		this.idlivellicancerogeni = idlivellicancerogeni;
	}


	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}


	public String getImported() {
		return this.imported;
	}

	public void setImported(String imported) {
		this.imported = imported;
	}


	public BigDecimal getValorecodificato() {
		return this.valorecodificato;
	}

	public void setValorecodificato(BigDecimal valorecodificato) {
		this.valorecodificato = valorecodificato;
	}


	//bi-directional many-to-one association to Lavoratoriespostiagenti
	@OneToMany(mappedBy="livellicancerogeni")
	public List<Lavoratoriespostiagenti> getLavoratoriespostiagentis() {
		return this.lavoratoriespostiagentis;
	}

	public void setLavoratoriespostiagentis(List<Lavoratoriespostiagenti> lavoratoriespostiagentis) {
		this.lavoratoriespostiagentis = lavoratoriespostiagentis;
	}

	public Lavoratoriespostiagenti addLavoratoriespostiagenti(Lavoratoriespostiagenti lavoratoriespostiagenti) {
		getLavoratoriespostiagentis().add(lavoratoriespostiagenti);
		lavoratoriespostiagenti.setLivellicancerogeni(this);

		return lavoratoriespostiagenti;
	}

	public Lavoratoriespostiagenti removeLavoratoriespostiagenti(Lavoratoriespostiagenti lavoratoriespostiagenti) {
		getLavoratoriespostiagentis().remove(lavoratoriespostiagenti);
		lavoratoriespostiagenti.setLivellicancerogeni(null);

		return lavoratoriespostiagenti;
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