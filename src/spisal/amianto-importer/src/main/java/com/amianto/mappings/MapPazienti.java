package com.amianto.mappings;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="mapping_pazienti")
public class MapPazienti extends GenericMapping {
	private static final long serialVersionUID = 1L;
	
	/*
	 * Ogni riga di Anagrafica determina la creazione di una comunicazione (commId), di una pratica
	 * di MDL ad essa associata (praticaId) e di una Person in qualita di riferito a / paziente (idphi)
	 */
	private Long praticaId;
	private Long commId;
	
	/**
	 * Id entità di origine
	 * @return
	 */
	@Override
	@Id
	public int getIdsorves() {
		return idsorves;
	}
	@Override
	public void setIdsorves(int idsorves) {
		this.idsorves = idsorves;
	}
	
	public Long getPraticaId() {
		return praticaId;
	}
	public void setPraticaId(Long praticaId) {
		this.praticaId = praticaId;
	}
	public Long getCommId() {
		return commId;
	}
	public void setCommId(Long commId) {
		this.commId = commId;
	}
}
