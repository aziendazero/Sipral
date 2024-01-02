package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the ESPOSIZIONECOD database table.
 * 
 */
@Entity
@Table(name="esposizionecod")
public class Esposizionecod implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String sottogruppo;
	private String tipo;
	private List<Esposizione> esposizione;
	private Tabcancerogeni tabcancerogeni;

	public Esposizionecod() {
	}

	@Id
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSottogruppo() {
		return this.sottogruppo;
	}

	public void setSottogruppo(String sottogruppo) {
		this.sottogruppo = sottogruppo;
	}

	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@OneToMany(mappedBy = "esposizionecod")
	public List<Esposizione> getEsposizione() {
	    return esposizione;
	}

	public void setEsposizione(List<Esposizione> param) {
	    this.esposizione = param;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "idcancerogeno", referencedColumnName = "id")
	public Tabcancerogeni getTabcancerogeni() {
	    return tabcancerogeni;
	}

	public void setTabcancerogeni(Tabcancerogeni param) {
	    this.tabcancerogeni = param;
	}

}