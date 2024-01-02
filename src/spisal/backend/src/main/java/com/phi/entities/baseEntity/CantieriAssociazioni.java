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
@Table(name = "cantieri_associazioni")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class CantieriAssociazioni extends BaseEntity {

	private static final long serialVersionUID = 1703295857L;


	/**
	*  javadoc for cantiere
	*/
	private Cantiere cantiere;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cantiere_id")
	@ForeignKey(name="FK_CntieriAssocizion_cntere")
	//@Index(name="IX_CntieriAssocizion_cntere")
	public Cantiere getCantiere(){
		return cantiere;
	}

	public void setCantiere(Cantiere cantiere){
		this.cantiere = cantiere;
	}



	/**
	*  javadoc for procpratiche
	*/
	private Procpratiche procpratiche;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="procpratiche_id")
	@ForeignKey(name="FK_CnterAssoczon_procprtche")
	//@Index(name="IX_CnterAssoczon_procprtche")
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
	@ForeignKey(name="FK_CantieriAssociazioni_type")
	//@Index(name="IX_CantieriAssociazioni_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}




	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CntieriAssocizion_sequence")
	@SequenceGenerator(name = "CntieriAssocizion_sequence", sequenceName = "CntieriAssocizion_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
