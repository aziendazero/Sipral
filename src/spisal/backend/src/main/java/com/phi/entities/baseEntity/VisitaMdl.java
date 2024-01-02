package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.VisitaSp;
import com.phi.entities.baseEntity.VisitaExt;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.baseEntity.ConclusioniMdl;

@javax.persistence.Entity
@Table(name = "visita_mdl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class VisitaMdl extends BaseEntity {

	private static final long serialVersionUID = 427616156L;


	/**
	*  javadoc for conclusioniMdl
	*/
	private ConclusioniMdl conclusioniMdl;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="conclusioni_mdl_id")
	@ForeignKey(name="FK_VisitaMdl_conclusioniMdl")
	//@Index(name="IX_VisitaMdl_conclusioniMdl")
	public ConclusioniMdl getConclusioniMdl(){
		return conclusioniMdl;
	}

	public void setConclusioniMdl(ConclusioniMdl conclusioniMdl){
		this.conclusioniMdl = conclusioniMdl;
	}


	/**
	*  javadoc for primaVisita
	*/
	private Boolean primaVisita;

	@Column(name="prima_visita")
	public Boolean getPrimaVisita(){
		return primaVisita;
	}

	public void setPrimaVisita(Boolean primaVisita){
		this.primaVisita = primaVisita;
	}

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_VisitaMdl_code")
	//@Index(name="IX_VisitaMdl_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}


	/**
	*  javadoc for visitaSp
	*/
	private VisitaSp visitaSp;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="visita_sp_id")
	@ForeignKey(name="FK_VisitaMdl_visitaSp")
	//@Index(name="IX_VisitaMdl_visitaSp")
	public VisitaSp getVisitaSp(){
		return visitaSp;
	}

	public void setVisitaSp(VisitaSp visitaSp){
		this.visitaSp = visitaSp;
	}



	/**
	*  javadoc for visitaExt
	*/
	private VisitaExt visitaExt;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="visita_ext_id")
	@ForeignKey(name="FK_VisitaMdl_visitaExt")
	//@Index(name="IX_VisitaMdl_visitaExt")
	public VisitaExt getVisitaExt(){
		return visitaExt;
	}

	public void setVisitaExt(VisitaExt visitaExt){
		this.visitaExt = visitaExt;
	}




	/**
	*  javadoc for attivita
	*/
	private Attivita attivita;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="attivita_id")
	@ForeignKey(name="FK_VisitaMdl_attivita")
	//@Index(name="IX_VisitaMdl_attivita")
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "VisitaMdl_sequence")
	@SequenceGenerator(name = "VisitaMdl_sequence", sequenceName = "VisitaMdl_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
