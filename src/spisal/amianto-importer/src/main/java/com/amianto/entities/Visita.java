package com.amianto.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import com.amianto.entities.Anagrafica;


/**
 * The persistent class for the visita database table.
 * 
 */
@Entity
@Table(name="visita")
@NamedQuery(name="Visita.findAll", query="SELECT v FROM Visita v")
public class Visita implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String conclusioni;
	private Date dataConclusione;
	private Date dataVisita;
	private String esameObiettivo;
	private String raccordoAnamnestico;
	private Anagrafica anagrafica;

	public Visita() {
	}


	@Id
	@Column(unique=true, nullable=false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	@Lob
	public String getConclusioni() {
		return this.conclusioni;
	}

	public void setConclusioni(String conclusioni) {
		this.conclusioni = conclusioni;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_conclusione")
	public Date getDataConclusione() {
		return this.dataConclusione;
	}

	public void setDataConclusione(Date dataConclusione) {
		this.dataConclusione = dataConclusione;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_visita")
	public Date getDataVisita() {
		return this.dataVisita;
	}

	public void setDataVisita(Date dataVisita) {
		this.dataVisita = dataVisita;
	}


	@Column(name="esame_obiettivo", length=255)
	public String getEsameObiettivo() {
		return this.esameObiettivo;
	}

	public void setEsameObiettivo(String esameObiettivo) {
		this.esameObiettivo = esameObiettivo;
	}

	@Lob
	@Column(name="raccordo_anamnestico")
	public String getRaccordoAnamnestico() {
		return this.raccordoAnamnestico;
	}

	public void setRaccordoAnamnestico(String raccordoAnamnestico) {
		this.raccordoAnamnestico = raccordoAnamnestico;
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