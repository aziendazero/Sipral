package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.role.Person;

@javax.persistence.Entity
@Table(name = "operaio_amianto")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class OperaioAmianto extends BaseEntity {

	private static final long serialVersionUID = 387354718L;


	/**
	*  javadoc for persona
	*/
	private Person persona;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persona_id")
	@ForeignKey(name="FK_OperaioAmianto_persona")
	//@Index(name="IX_OperaioAmianto_persona")
	public Person getPersona(){
		return persona;
	}

	public void setPersona(Person persona){
		this.persona = persona;
	}



	/**
	*  javadoc for esposto
	*/
	private Esposti esposto;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="esposto_id")
	@ForeignKey(name="FK_OperaioAmianto_esposto")
	//@Index(name="IX_OperaioAmianto_esposto")
	public Esposti getEsposto(){
		return esposto;
	}

	public void setEsposto(Esposti esposto){
		this.esposto = esposto;
	}



	/**
	*  javadoc for vigilanza
	*/
	private Vigilanza vigilanza;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="vigilanza_id")
	@ForeignKey(name="FK_OperaioAmianto_vigilanza")
	//@Index(name="IX_OperaioAmianto_vigilanza")
	public Vigilanza getVigilanza(){
		return vigilanza;
	}

	public void setVigilanza(Vigilanza vigilanza){
		this.vigilanza = vigilanza;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "OperaioAmianto_sequence")
	@SequenceGenerator(name = "OperaioAmianto_sequence", sequenceName = "OperaioAmianto_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
