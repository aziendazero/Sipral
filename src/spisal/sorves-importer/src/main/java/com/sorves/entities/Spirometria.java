package com.sorves.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;


/**
 * The persistent class for the SPIROMETRIA database table.
 * 
 */
@Entity
@Table(name="spirometria")
public class Spirometria implements Serializable {
	private static final long serialVersionUID = 1L;
	private Date dataprel;
	private String descrizione;
	private long id;
	private Tabdiagnosispirometrica tipo;
	private Anagrafica anagrafica;

	public Spirometria() {
	}


	@Temporal(TemporalType.DATE)
	public Date getDataprel() {
		return this.dataprel;
	}

	public void setDataprel(Date dataprel) {
		this.dataprel = dataprel;
	}


	@Type(type="text")
	public String getDescrizione() {
		return this.descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "tipo", referencedColumnName = "id")
	public Tabdiagnosispirometrica getTipo() {
		return this.tipo;
	}

	public void setTipo(Tabdiagnosispirometrica tipo) {
		this.tipo = tipo;
	}


	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idanagrafe", referencedColumnName = "id")
	public Anagrafica getAnagrafica() {
	    return anagrafica;
	}


	public void setAnagrafica(Anagrafica param) {
	    this.anagrafica = param;
	}

}