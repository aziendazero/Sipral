 package com.amianto.entities;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the primo_controllo database table.
 * 
 */
@Entity
@Table(name="primo_controllo")
@AttributeOverrides({
	@AttributeOverride(name="dataVisita", column=@Column(name="data_seconda_visita")),
	@AttributeOverride(name="indagineRadiologica", column=@Column(name="seconda_indagine_radiologica")),
	@AttributeOverride(name="referto", column=@Column(name="referto_2")),
	@AttributeOverride(name="lesione", column=@Column(name="lesione_2")),
	@AttributeOverride(name="numero",  column=@Column(name="numero_2")),
	@AttributeOverride(name="diametro", column=@Column(name="diametro_2")),
	@AttributeOverride(name="folow_upMotivo", column=@Column(name="`folow-up_motivo`"))
})
public class PrimoControllo extends VisitaGenerica implements Serializable {
	private static final long serialVersionUID = 1L;

	public PrimoControllo() {
	}
	
	@Column(name="evoluzione_2", length=50)
	public String getEvoluzione() {
		return this.evoluzione;
	}

	public void setEvoluzione(String evoluzione2) {
		this.evoluzione = evoluzione2;
	}
	
	@Column(name="seconda_visita", length=250)
	public String getVisitaMedica() {
		return this.visitaMedica;
	}

	public void setVisitaMedica(String secondaVisita) {
		this.visitaMedica = secondaVisita;
	}
}