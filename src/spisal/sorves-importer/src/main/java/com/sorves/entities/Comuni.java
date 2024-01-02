package com.sorves.entities;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the COMUNI database table.
 * 
 */
@Entity
@Table(name="comuni")
public class Comuni implements Serializable {
	private static final long serialVersionUID = 1L;

	private long codice;
	private String cap;
	private String codreg;
	private String codusl;
	private String descrizione;
	private String istat;
	private String provincia;

	public Comuni() {
	}

	@Id
	public long getCodice() {
		return this.codice;
	}

	public void setCodice(long codice) {
		this.codice = codice;
	}

	public String getCap() {
		return this.cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getCodreg() {
		return this.codreg;
	}

	public void setCodreg(String codreg) {
		this.codreg = codreg;
	}

	public String getCodusl() {
		return this.codusl;
	}

	public void setCodusl(String codusl) {
		this.codusl = codusl;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getIstat() {
		return this.istat;
	}

	public void setIstat(String istat) {
		this.istat = istat;
	}

	public String getProvincia() {
		return this.provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

}