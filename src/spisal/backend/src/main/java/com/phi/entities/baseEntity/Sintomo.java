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

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.ForeignKey;
import com.phi.entities.baseEntity.VisitaMedica;
import com.phi.entities.dataTypes.CodeValuePhi;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@javax.persistence.Entity
@Table(name = "sintomi")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Sintomo extends BaseEntity {

	private static final long serialVersionUID = 815049342L;

	/**
	*  javadoc for peggioramento
	*/
	private Boolean peggioramento;

	@Column(name="peggioramento")
	public Boolean getPeggioramento(){
		return peggioramento;
	}

	public void setPeggioramento(Boolean peggioramento){
		this.peggioramento = peggioramento;
	}

	/**
	*  javadoc for data
	*/
	private Date data;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data")
	public Date getData(){
		return data;
	}

	public void setData(Date data){
		this.data = data;
	}

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_Sintomo_code")
	//@Index(name="IX_Sintomo_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}


	/**
	*  javadoc for visitaMedica
	*/
	private VisitaMedica visitaMedica;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="visita_medica_id")
	@ForeignKey(name="FK_Sintomo_visitaMedica")
	//@Index(name="IX_Sintomo_visitaMedica")
	public VisitaMedica getVisitaMedica(){
		return visitaMedica;
	}

	public void setVisitaMedica(VisitaMedica visitaMedica){
		this.visitaMedica = visitaMedica;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Sintomo_sequence")
	@SequenceGenerator(name = "Sintomo_sequence", sequenceName = "Sintomo_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
