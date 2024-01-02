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

import com.phi.entities.dataTypes.CodeValueIcd9;
import com.phi.entities.dataTypes.CodeValuePhi;

import com.phi.entities.baseEntity.ConclusioniMdl;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.AccertaMdl;

import com.phi.entities.baseEntity.ValutazioneConclusivaMdl;
@javax.persistence.Entity
@Table(name = "diag_mdl")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class DiagMdl extends BaseEntity {

	private static final long serialVersionUID = 1272115505L;


	/**
	*  javadoc for valutazioneConclusivaMdl
	*/
	private ValutazioneConclusivaMdl valutazioneConclusivaMdl;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="valutazione_conclusiva_mdl_id")
	@ForeignKey(name="FK_DgMdl_vlutzoneConclsvMdl")
	//@Index(name="IX_DgMdl_vlutzoneConclsvMdl")
	public ValutazioneConclusivaMdl getValutazioneConclusivaMdl(){
		return valutazioneConclusivaMdl;
	}

	public void setValutazioneConclusivaMdl(ValutazioneConclusivaMdl valutazioneConclusivaMdl){
		this.valutazioneConclusivaMdl = valutazioneConclusivaMdl;
	}



	/**
	*  javadoc for accertaMdl
	*/
	private AccertaMdl accertaMdl;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="accerta_mdl_id")
	@ForeignKey(name="FK_DiagMdl_accertaMdl")
	//@Index(name="IX_DiagMdl_accertaMdl")
	public AccertaMdl getAccertaMdl(){
		return accertaMdl;
	}

	public void setAccertaMdl(AccertaMdl accertaMdl){
		this.accertaMdl = accertaMdl;
	}



	/**
	*  javadoc for conclusioniMdl
	*/
	private ConclusioniMdl conclusioniMdl;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="conclusioni_mdl_id")
	@ForeignKey(name="FK_DiagMdl_conclusioniMdl")
	//@Index(name="IX_DiagMdl_conclusioniMdl")
	public ConclusioniMdl getConclusioniMdl(){
		return conclusioniMdl;
	}

	public void setConclusioniMdl(ConclusioniMdl conclusioniMdl){
		this.conclusioniMdl = conclusioniMdl;
	}


	/**
	*  javadoc for inail
	*/
	private CodeValuePhi inail;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="inail")
	@ForeignKey(name="FK_DiagMdl_inail")
	//@Index(name="IX_DiagMdl_inail")
	public CodeValuePhi getInail(){
		return inail;
	}

	public void setInail(CodeValuePhi inail){
		this.inail = inail;
	}

	/**
	*  javadoc for icd9
	*/
	private CodeValueIcd9 icd9;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="icd9")
	@ForeignKey(name="FK_DiagMdl_icd9")
	//@Index(name="IX_DiagMdl_icd9")
	public CodeValueIcd9 getIcd9(){
		return icd9;
	}

	public void setIcd9(CodeValueIcd9 icd9){
		this.icd9 = icd9;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "DiagMdl_sequence")
	@SequenceGenerator(name = "DiagMdl_sequence", sequenceName = "DiagMdl_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
