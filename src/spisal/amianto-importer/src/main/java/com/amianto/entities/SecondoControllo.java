package com.amianto.entities;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * The persistent class for the secondo_controllo database table.
 * 
 */
@Entity
@Table(name="secondo_controllo")
@AttributeOverrides({
	@AttributeOverride(name="dataVisita", column=@Column(name="data_terza_visita")),
	@AttributeOverride(name="indagineRadiologica", column=@Column(name="terza_indagine_radiologica")),
	@AttributeOverride(name="referto", column=@Column(name="referto_3")),
	@AttributeOverride(name="lesione", column=@Column(name="lesione_3")),
	@AttributeOverride(name="numero",  column=@Column(name="numero_3")),
	@AttributeOverride(name="diametro", column=@Column(name="diametro_3")),
	@AttributeOverride(name="folow_upMotivo", column=@Column(name="`folow-up_motivo`"))
})
public class SecondoControllo extends VisitaGenerica implements Serializable {
	private static final long serialVersionUID = 1L;

	public SecondoControllo() {
	}
	
	@Column(name="evoluzione_3", length=50)
	public String getEvoluzione() {
		return this.evoluzione;
	}

	public void setEvoluzione(String evoluzione2) {
		this.evoluzione = evoluzione2;
	}
	
	@Column(name="terza_visita", length=250)
	public String getVisitaMedica() {
		return this.visitaMedica;
	}

	public void setVisitaMedica(String secondaVisita) {
		this.visitaMedica = secondaVisita;
	}
}