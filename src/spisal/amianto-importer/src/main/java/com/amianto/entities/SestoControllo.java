package com.amianto.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


/**
 * The persistent class for the sesto_controllo database table.
 * 
 */
@Entity
@Table(name="sesto_controllo")
@AttributeOverrides({
	@AttributeOverride(name="dataVisita", column=@Column(name="data_visita")),
	@AttributeOverride(name="indagineRadiologica", column=@Column(name="indagine_radiologica"))
})
public class SestoControllo extends VisitaGenerica implements Serializable {
	private static final long serialVersionUID = 1L;

	public SestoControllo() {
	}
	
	@Column(name="evoluzione", length=50)
	public String getEvoluzione() {
		return this.evoluzione;
	}

	public void setEvoluzione(String evoluzione2) {
		this.evoluzione = evoluzione2;
	}
	
	@Column(name="visita_medica", length=250)
	public String getVisitaMedica() {
		return this.visitaMedica;
	}

	public void setVisitaMedica(String secondaVisita) {
		this.visitaMedica = secondaVisita;
	}
}