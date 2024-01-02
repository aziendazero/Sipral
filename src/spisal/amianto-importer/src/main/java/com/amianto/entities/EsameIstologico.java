package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


/**
 * The persistent class for the esame_istologico database table.
 * 
 */
@Entity
@Table(name="esame_istologico")
public class EsameIstologico implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date data;
	private String referto;
	private Anagrafica anagrafica;
	private TipologiaEsameBiologico tipologia;

	public EsameIstologico() {
	}


	@Id
	@Column(unique=true, nullable=false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	@Lob
	public String getReferto() {
		return this.referto;
	}

	public void setReferto(String referto) {
		this.referto = referto;
	}

	@ManyToOne
	@JoinColumn(name = "id1", referencedColumnName = "id")
	public Anagrafica getAnagrafica() {
	    return anagrafica;
	}


	public void setAnagrafica(Anagrafica param) {
	    this.anagrafica = param;
	}


	@ManyToOne
	@NotFound(action=NotFoundAction.IGNORE)
	@JoinColumn(name = "tipologia_diagnostica", referencedColumnName = "id")
	public TipologiaEsameBiologico getTipologia() {
	    return tipologia;
	}


	public void setTipologia(TipologiaEsameBiologico param) {
	    this.tipologia = param;
	}

}