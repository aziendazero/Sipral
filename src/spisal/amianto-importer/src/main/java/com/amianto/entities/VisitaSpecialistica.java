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
 * The persistent class for the visita_specialistica database table.
 * 
 */
@Entity
@Table(name="visita_specialistica")
public class VisitaSpecialistica implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Date data;
	private String referto;
	private Anagrafica anagrafica;
	private TipoSpecialista tipoSpecialista;

	public VisitaSpecialistica() {
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
	@JoinColumn(name = "tipo_visita", referencedColumnName = "id")
	public TipoSpecialista getTipoSpecialista() {
	    return tipoSpecialista;
	}


	public void setTipoSpecialista(TipoSpecialista param) {
	    this.tipoSpecialista = param;
	}

}