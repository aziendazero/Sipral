package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@javax.persistence.Entity
@Table(name = "malattia_agente")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class MalattiaAgente extends BaseEntity {

	private static final long serialVersionUID = 794636532L;

	/**
	*  javadoc for codiceAgente
	*/
	private String codiceAgente;

	@Column(name="codice_agente")
	public String getCodiceAgente(){
		return codiceAgente;
	}

	public void setCodiceAgente(String codiceAgente){
		this.codiceAgente = codiceAgente;
	}

	/**
	*  javadoc for codiceMalattia
	*/
	private String codiceMalattia;

	@Column(name="codice_malattia")
	public String getCodiceMalattia(){
		return codiceMalattia;
	}

	public void setCodiceMalattia(String codiceMalattia){
		this.codiceMalattia = codiceMalattia;
	}
	
	
	//methods needed for baseEntity imp	lementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "MalattiaAgente_sequence")
	@SequenceGenerator(name = "MalattiaAgente_sequence", sequenceName = "MalattiaAgente_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
