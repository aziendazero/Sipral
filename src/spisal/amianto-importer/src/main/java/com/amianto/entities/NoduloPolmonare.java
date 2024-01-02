package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the nodulo_polmonare database table.
 * 
 */
@Entity
@Table(name="nodulo_polmonare")
@NamedQuery(name="NoduloPolmonare.findAll", query="SELECT n FROM NoduloPolmonare n")
public class NoduloPolmonare implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer nodulo;
	private String calcifico;
	private Date data;
	private String densita;
	private String diametroMin_max;
	private String doublingTime;
	private String forma;
	private String gravita;
	private String incremento;
	private String sede;
	private String superficie;
	private Integer volumeTotalecm3;
	private Anagrafica anagrafica;

	public NoduloPolmonare() {
	}


	@Id
	@Column(unique=true, nullable=false)
	public Integer getNodulo() {
		return this.nodulo;
	}

	public void setNodulo(Integer nodulo) {
		this.nodulo = nodulo;
	}


	@Column(length=255)
	public String getCalcifico() {
		return this.calcifico;
	}

	public void setCalcifico(String calcifico) {
		this.calcifico = calcifico;
	}


	@Temporal(TemporalType.TIMESTAMP)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}


	@Column(length=255)
	public String getDensita() {
		return this.densita;
	}

	public void setDensita(String densita) {
		this.densita = densita;
	}


	@Column(name="`diametro_min-max`", length=255)
	public String getDiametroMin_max() {
		return this.diametroMin_max;
	}

	public void setDiametroMin_max(String diametroMin_max) {
		this.diametroMin_max = diametroMin_max;
	}


	@Column(name="doubling_time", length=255)
	public String getDoublingTime() {
		return this.doublingTime;
	}

	public void setDoublingTime(String doublingTime) {
		this.doublingTime = doublingTime;
	}


	@Column(length=255)
	public String getForma() {
		return this.forma;
	}

	public void setForma(String forma) {
		this.forma = forma;
	}


	@Column(length=255)
	public String getGravita() {
		return this.gravita;
	}

	public void setGravita(String gravita) {
		this.gravita = gravita;
	}

	@Column(length=255)
	public String getIncremento() {
		return this.incremento;
	}

	public void setIncremento(String incremento) {
		this.incremento = incremento;
	}


	@Column(length=255)
	public String getSede() {
		return this.sede;
	}

	public void setSede(String sede) {
		this.sede = sede;
	}


	@Column(length=255)
	public String getSuperficie() {
		return this.superficie;
	}

	public void setSuperficie(String superficie) {
		this.superficie = superficie;
	}


	@Column(name="volume_totalecm3")
	public Integer getVolumeTotalecm3() {
		return this.volumeTotalecm3;
	}

	public void setVolumeTotalecm3(Integer volumeTotalecm3) {
		this.volumeTotalecm3 = volumeTotalecm3;
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