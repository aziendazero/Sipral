package com.prevnet.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the PARERIGENERICI database table.
 * 
 */
@Entity
@NamedQuery(name="Parerigenerici.findAll", query="SELECT p FROM Parerigenerici p")
public class Parerigenerici implements Serializable {
	private static final long serialVersionUID = 1L;
	private long progressivo;
	private String chkmulti;
	private Date data;
	private Date dataprot;
	private Operatori operatore;
	private Pratiche pratica;
	private BigDecimal idprot;
	private BigDecimal idsirsap;
	private String note;
	private String numprot;
	private Tabelle parere;
	private Tabelle tipoParere;
	private List<Parerigenericialtriop> parerigenericialtriops;

	public Parerigenerici() {
	}


	@Id
	public long getProgressivo() {
		return this.progressivo;
	}

	public void setProgressivo(long progressivo) {
		this.progressivo = progressivo;
	}


	public String getChkmulti() {
		return this.chkmulti;
	}

	public void setChkmulti(String chkmulti) {
		this.chkmulti = chkmulti;
	}


	@Temporal(TemporalType.DATE)
	@Column(name="\"DATA\"")
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	@Temporal(TemporalType.DATE)
	public Date getDataprot() {
		return this.dataprot;
	}

	public void setDataprot(Date dataprot) {
		this.dataprot = dataprot;
	}

	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDOPERATORE")
	public Operatori getOperatore() {
		return this.operatore;
	}

	public void setOperatore(Operatori operatore) {
		this.operatore = operatore;
	}
	
	//bi-directional many-to-one association to Procpratiche
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPROCPRATICA")
	public Pratiche getPratica() {
		return this.pratica;
	}

	public void setPratica(Pratiche pratica) {
		this.pratica = pratica;
	}


	public BigDecimal getIdprot() {
		return this.idprot;
	}

	public void setIdprot(BigDecimal idprot) {
		this.idprot = idprot;
	}


	public BigDecimal getIdsirsap() {
		return this.idsirsap;
	}

	public void setIdsirsap(BigDecimal idsirsap) {
		this.idsirsap = idsirsap;
	}


	@Lob
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}


	public String getNumprot() {
		return this.numprot;
	}

	public void setNumprot(String numprot) {
		this.numprot = numprot;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDPARERE")
	public Tabelle getParere() {
		return this.parere;
	}

	public void setParere(Tabelle parere) {
		this.parere = parere;
	}


	//bi-directional many-to-one association to Tabelle
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name="IDTIPOPARERE")
	public Tabelle getTipoParere() {
		return this.tipoParere;
	}

	public void setTipoParere(Tabelle tipoParere) {
		this.tipoParere = tipoParere;
	}


	//bi-directional many-to-one association to Parerigenericialtriop
	@OneToMany(mappedBy="parerigenerici")
	public List<Parerigenericialtriop> getParerigenericialtriops() {
		return this.parerigenericialtriops;
	}

	public void setParerigenericialtriops(List<Parerigenericialtriop> parerigenericialtriops) {
		this.parerigenericialtriops = parerigenericialtriops;
	}

	public Parerigenericialtriop addParerigenericialtriop(Parerigenericialtriop parerigenericialtriop) {
		getParerigenericialtriops().add(parerigenericialtriop);
		parerigenericialtriop.setParerigenerici(this);

		return parerigenericialtriop;
	}

	public Parerigenericialtriop removeParerigenericialtriop(Parerigenericialtriop parerigenericialtriop) {
		getParerigenericialtriops().remove(parerigenericialtriop);
		parerigenericialtriop.setParerigenerici(null);

		return parerigenericialtriop;
	}

}