package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the INTERVENTIATTIVITASVOLTA database table.
 * 
 */
@Entity
@NamedQuery(name="Interventiattivitasvolta.findAll", query="SELECT i FROM Interventiattivitasvolta i")
public class Interventiattivitasvolta implements Serializable {
	private static final long serialVersionUID = 1L;
	private long idattivitasvolta;
	private String classegruppo;
	private Date dataattivita;
	private String inorariofuoriorario;
	private String note;
	private BigDecimal oreattivita;
	private List<Operatori> operatoris;
	private Interventiformazione interventiformazione;
	private Tabelle tipoAttivita;

	public Interventiattivitasvolta() {
	}


	@Id
	public long getIdattivitasvolta() {
		return this.idattivitasvolta;
	}

	public void setIdattivitasvolta(long idattivitasvolta) {
		this.idattivitasvolta = idattivitasvolta;
	}


	public String getClassegruppo() {
		return this.classegruppo;
	}

	public void setClassegruppo(String classegruppo) {
		this.classegruppo = classegruppo;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataattivita() {
		return this.dataattivita;
	}

	public void setDataattivita(Date dataattivita) {
		this.dataattivita = dataattivita;
	}


	public String getInorariofuoriorario() {
		return this.inorariofuoriorario;
	}

	public void setInorariofuoriorario(String inorariofuoriorario) {
		this.inorariofuoriorario = inorariofuoriorario;
	}


	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public BigDecimal getOreattivita() {
		return this.oreattivita;
	}

	public void setOreattivita(BigDecimal oreattivita) {
		this.oreattivita = oreattivita;
	}


	//bi-directional many-to-many association to Operatori
	@ManyToMany(mappedBy="interventiattivitasvoltas")
	public List<Operatori> getOperatoris() {
		return this.operatoris;
	}

	public void setOperatoris(List<Operatori> operatoris) {
		this.operatoris = operatoris;
	}


	//bi-directional many-to-one association to Interventiformazione
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDINTERVENTO")
	public Interventiformazione getInterventiformazione() {
		return this.interventiformazione;
	}

	public void setInterventiformazione(Interventiformazione interventiformazione) {
		this.interventiformazione = interventiformazione;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOATTIVITA")
	public Tabelle getTipoAttivita() {
		return this.tipoAttivita;
	}

	public void setTipoAttivita(Tabelle tipoAttivita) {
		this.tipoAttivita = tipoAttivita;
	}

}