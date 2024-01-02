package com.sorves.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;


/**
 * The persistent class for the ESAMEALTRO database table.
 * 
 */
@Entity
@Table(name="esamealtro")
public class Esamealtro implements Serializable {
	private static final long serialVersionUID = 1L;

	private Date dataprel;
	private String descrizione;
	private long id;
	private Anagrafica anagrafica;
	private List<Tabdiagnosialtroesame> tabdiagnosialtroesame;
	private Tabesamealtroesame tipo;

	public Esamealtro() {
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
	@JoinColumn(name = "idanagrafe", referencedColumnName = "id")
	public Anagrafica getAnagrafica() {
	    return anagrafica;
	}

	public void setAnagrafica(Anagrafica param) {
	    this.anagrafica = param;
	}

	@ManyToMany
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinTable(
			name="esamealtrocod"
			, joinColumns={
				@JoinColumn(name="idesame")
				}
			, inverseJoinColumns={
				@JoinColumn(name="iddiagnosi")
				}
			)
	public List<Tabdiagnosialtroesame> getTabdiagnosialtroesame() {
	    return tabdiagnosialtroesame;
	}

	public void setTabdiagnosialtroesame(List<Tabdiagnosialtroesame> param) {
	    this.tabdiagnosialtroesame = param;
	}

	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "tipo", referencedColumnName = "id")
	public Tabesamealtroesame getTipo() {
	    return tipo;
	}

	public void setTipo(Tabesamealtroesame param) {
	    this.tipo = param;
	}

}