package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.ImpPress;
@javax.persistence.Entity
@Table(name = "scheda_generatori_indiv")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SchedaGeneratoriIndiv extends BaseEntity {

	private static final long serialVersionUID = 908570377L;

	/**
	*  javadoc for numero
	*/
	private Integer numero;

	@Column(name="numero")
	public Integer getNumero(){
		return numero;
	}

	public void setNumero(Integer numero){
		this.numero = numero;
	}


	/**
	*  javadoc for impPress
	*/
	private ImpPress impPress;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="imp_press_id")
	@ForeignKey(name="FK_SchdGnrtoriIndiv_impPrss")
	//@Index(name="IX_SchdGnrtoriIndiv_impPrss")
	public ImpPress getImpPress(){
		return impPress;
	}

	public void setImpPress(ImpPress impPress){
		this.impPress = impPress;
	}


	/**
	*  javadoc for producibilita
	*/
	private String producibilita;

	@Column(name="producibilita")
	public String getProducibilita(){
		return producibilita;
	}

	public void setProducibilita(String producibilita){
		this.producibilita = producibilita;
	}

	/**
	*  javadoc for superficie
	*/
	private String superficie;

	@Column(name="superficie")
	public String getSuperficie(){
		return superficie;
	}

	public void setSuperficie(String superficie){
		this.superficie = superficie;
	}

	/**
	*  javadoc for capacita
	*/
	private String capacita;

	@Column(name="capacita")
	public String getCapacita(){
		return capacita;
	}

	public void setCapacita(String capacita){
		this.capacita = capacita;
	}

	/**
	*  javadoc for pressBar1
	*/
	private String pressBar1;

	@Column(name="press_bar1")
	public String getPressBar1(){
		return pressBar1;
	}

	public void setPressBar1(String pressBar1){
		this.pressBar1 = pressBar1;
	}

	/**
	*  javadoc for costruttore
	*/
	private String costruttore;

	@Column(name="costruttore")
	public String getCostruttore(){
		return costruttore;
	}

	public void setCostruttore(String costruttore){
		this.costruttore = costruttore;
	}

	/**
	*  javadoc for numeroFabbrica
	*/
	private String numeroFabbrica;

	@Column(name="numero_fabbrica")
	public String getNumeroFabbrica(){
		return numeroFabbrica;
	}

	public void setNumeroFabbrica(String numeroFabbrica){
		this.numeroFabbrica = numeroFabbrica;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SchedaGeneratoriIndiv_sequence")
	@SequenceGenerator(name = "SchedaGeneratoriIndiv_sequence", sequenceName = "SchedaGeneratoriIndiv_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
