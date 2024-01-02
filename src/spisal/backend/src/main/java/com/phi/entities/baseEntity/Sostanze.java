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
@Table(name = "sostanze")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Sostanze extends BaseEntity {

	private static final long serialVersionUID = 1383288076L;

	/**
	*  javadoc for agente
	*/
	private CodeValuePhi agente;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="agente")
	@ForeignKey(name="FK_Sostanze_agente")
	//@Index(name="IX_Sostanze_agente")
	public CodeValuePhi getAgente(){
		return agente;
	}

	public void setAgente(CodeValuePhi agente){
		this.agente = agente;
	}

	/**
	*  javadoc for bio
	*/
	private CodeValuePhi bio;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="bio")
	@ForeignKey(name="FK_Sostanze_bio")
	//@Index(name="IX_Sostanze_bio")
	public CodeValuePhi getBio(){
		return bio;
	}

	public void setBio(CodeValuePhi bio){
		this.bio = bio;
	}

	/**
	*  true se gruppo 3, false se gruppo 4
	*/
	private Boolean gruppo3;

	@Column(name="gruppo3")
	public Boolean getGruppo3(){
		return gruppo3;
	}

	public void setGruppo3(Boolean gruppo3){
		this.gruppo3 = gruppo3;
	}

	/**
	*  javadoc for quantita
	*/
	private String quantita;

	@Column(name="quantita")
	public String getQuantita(){
		return quantita;
	}

	public void setQuantita(String quantita){
		this.quantita = quantita;
	}

	/**
	*  javadoc for codiceCE
	*/
	private CodeValuePhi codiceCE;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="codiceCE")
	@ForeignKey(name="FK_sost_codiceCE")
	//@Index(name="IX_sost_codiceCE")
	public CodeValuePhi getCodiceCE(){
		return codiceCE;
	}

	public void setCodiceCE(CodeValuePhi codiceCE){
		this.codiceCE = codiceCE;
	}

	/**
	*  javadoc for sostanza
	*/
	private CodeValuePhi sostanza;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="sostanza")
	@ForeignKey(name="FK_sost_sostanza")
	//@Index(name="IX_sost_sostanza")
	public CodeValuePhi getSostanza(){
		return sostanza;
	}

	public void setSostanza(CodeValuePhi sostanza){
		this.sostanza = sostanza;
	}

	/**
	*  javadoc for tipologia
	*/
	private CodeValuePhi tipologia;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="tipologia")
	@ForeignKey(name="FK_sost_tipologia")
	//@Index(name="IX_sost_tipologia")
	public CodeValuePhi getTipologia(){
		return tipologia;
	}

	public void setTipologia(CodeValuePhi tipologia){
		this.tipologia = tipologia;
	}

	

	/**
	*  javadoc for schedaEsposti
	*/
	private SchedaEsposti schedaEsposti;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="schedaEsposti_id")
	@ForeignKey(name="FK_Sost_schedaEsp")
	//@Index(name="IX_Sost_schedaEsp")
	public SchedaEsposti getSchedaEsposti(){
		return schedaEsposti;
	}

	public void setSchedaEsposti(SchedaEsposti schedaEsposti){
		this.schedaEsposti = schedaEsposti;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Sostanze_sequence")
	@SequenceGenerator(name = "Sostanze_sequence", sequenceName = "Sostanze_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
