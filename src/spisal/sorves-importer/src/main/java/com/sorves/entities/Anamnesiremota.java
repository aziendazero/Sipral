package com.sorves.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;



/**
 * The persistent class for the ANAMNESIREMOTA database table.
 * 
 */
@Entity
@Table(name="anamnesiremota")
public class Anamnesiremota implements Serializable {
	private static final long serialVersionUID = 1L;

	private String descrizione;
	private long id;
	private List<Tabsintomi> tabsintomi;

	private Anagrafica anagrafica;

	public Anamnesiremota() {
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

	@ManyToMany
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinTable(
			name="anamnesiremotacod"
			, joinColumns={
				@JoinColumn(name="idanamnesi")
				}
			, inverseJoinColumns={
				@JoinColumn(name="idsintomo")
				}
			)
	public List<Tabsintomi> getTabsintomi() {
	    return tabsintomi;
	}

	public void setTabsintomi(List<Tabsintomi> param) {
	    this.tabsintomi = param;
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