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

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;
import com.phi.entities.role.SediPersone;


@javax.persistence.Entity
@Table(name = "persone_associazioni")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PersoneAssociazioni extends BaseEntity {

	private static final long serialVersionUID = 1704860285L;


	/**
	*  javadoc for sediPersone
	*/
	private SediPersone sediPersone;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sedi_persone_id")
	@ForeignKey(name="FK_PersAssoc_SediPers")
	//@Index(name="IX_PersAssoc_SediPers")
	public SediPersone getSediPersone(){
		return sediPersone;
	}

	public void setSediPersone(SediPersone sediPersone){
		this.sediPersone = sediPersone;
	}
	
	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_PersonAssociazioni_prson")
	//@Index(name="IX_PersonAssociazioni_prson")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}



	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_Prsnssciazini_prcpratich")
	//@Index(name="IX_Prsnssciazini_prcpratich")
	public Procpratiche getProcpratiche(){
		return procpratiche;
	}

	public void setProcpratiche(Procpratiche procpratiche){
		this.procpratiche = procpratiche;
	}


	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_PersnAsscitns_type")
	//@Index(name="IX_PersnAsscitns_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}







	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PersnAsscitns_sequence")
	@SequenceGenerator(name = "PersnAsscitns_sequence", sequenceName = "PersnAsscitns_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
