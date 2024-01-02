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


/**
 * The persistent class for the esame_spirometrico database table.
 * 
 */
@Entity
@Table(name="esame_spirometrico")
public class EsameSpirometrico implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer codiceReferto;
	private Date data;
	private Integer fev1;
	private Integer fev1_vc;
	private String referto;
	private Integer vc;
	private Anagrafica anagrafica;

	public EsameSpirometrico() {
	}


	@Id
	@Column(unique=true, nullable=false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Column(name="codice_referto")
	public Integer getCodiceReferto() {
		return this.codiceReferto;
	}

	public void setCodiceReferto(Integer codiceReferto) {
		this.codiceReferto = codiceReferto;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	public Integer getFev1() {
		return this.fev1;
	}

	public void setFev1(Integer fev1) {
		this.fev1 = fev1;
	}


	@Column(name="`fev1-vc`")
	public Integer getFev1_vc() {
		return this.fev1_vc;
	}

	public void setFev1_vc(Integer fev1_vc) {
		this.fev1_vc = fev1_vc;
	}


	@Lob
	public String getReferto() {
		return this.referto;
	}

	public void setReferto(String referto) {
		this.referto = referto;
	}


	public Integer getVc() {
		return this.vc;
	}

	public void setVc(Integer vc) {
		this.vc = vc;
	}


	@ManyToOne
	@JoinColumn(name = "id1", referencedColumnName = "id")
	public Anagrafica getAnagrafica() {
	    return anagrafica;
	}


	public void setAnagrafica(Anagrafica param) {
	    this.anagrafica = param;
	}

}