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

@javax.persistence.Entity
@Table(name = "ditte_associazioni")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class DitteAssociazioni extends BaseEntity {

	private static final long serialVersionUID = 1703486872L;


	/**
	*  javadoc for personeGiuridiche
	*/
	private PersoneGiuridiche personeGiuridiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="persone_giuridiche_id")
	@ForeignKey(name="FK_DttAssocazon_prsonGurdch")
	//@Index(name="IX_DttAssocazon_prsonGurdch")
	public PersoneGiuridiche getPersoneGiuridiche(){
		return personeGiuridiche;
	}

	public void setPersoneGiuridiche(PersoneGiuridiche personeGiuridiche){
		this.personeGiuridiche = personeGiuridiche;
	}






	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_DtteAssocazon_procpratch")
	//@Index(name="IX_DtteAssocazon_procpratch")
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
	@ForeignKey(name="FK_DittAsscizini_type")
	//@Index(name="IX_DittAsscizini_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}


	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DittAsscizini_sequence")
	@SequenceGenerator(name = "DittAsscizini_sequence", sequenceName = "DittAsscizini_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
