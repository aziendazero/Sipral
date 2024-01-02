package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;



/**
 * The persistent class for the ANAMNESILAVORATIVA database table.
 * 
 */
@Entity
@Table(name="anamnesilavorativa")
public class Anamnesilavorativa implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private long id;
	private Anagrafica anagrafica;
	private List<Esposizione> esposizioni;
	
	public Anamnesilavorativa() {
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
	@JoinColumn(name = "idanagrafe", referencedColumnName = "id")
	public Anagrafica getAnagrafica() {
	    return anagrafica;
	}

	public void setAnagrafica(Anagrafica param) {
	    this.anagrafica = param;
	}
	
	@OneToMany(mappedBy = "anamnesilavorativa")
	public List<Esposizione> getEsposizioni() {
	    return esposizioni;
	}

	public void setEsposizioni(List<Esposizione> param) {
	    this.esposizioni = param;
	}


}