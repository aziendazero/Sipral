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
@Table(name = "prestazioni_reg")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class PrestazioniReg extends BaseEntity {

	private static final long serialVersionUID = 1793548327L;

	/**
	*  javadoc for presCode
	*/
	private CodeValuePhi presCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="presCode")
	@ForeignKey(name="FK_PrestazioniReg_presCode")
	//@Index(name="IX_PrestazioniReg_presCode")
	public CodeValuePhi getPresCode(){
		return presCode;
	}

	public void setPresCode(CodeValuePhi presCode){
		this.presCode = presCode;
	}


	/**
	*  javadoc for visitaMedica
	*/
	private VisitaMedica visitaMedica;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="visita_medica_id")
	@ForeignKey(name="FK_PrstazioniRg_visitaMdica")
	//@Index(name="IX_PrstazioniRg_visitaMdica")
	public VisitaMedica getVisitaMedica(){
		return visitaMedica;
	}

	public void setVisitaMedica(VisitaMedica visitaMedica){
		this.visitaMedica = visitaMedica;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "PrestazioniReg_sequence")
	@SequenceGenerator(name = "PrestazioniReg_sequence", sequenceName = "PrestazioniReg_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
