package com.phi.entities.baseEntity;

// Generated 15-lug-2015 13.47.19 by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;

@Entity
@Table(name = "misure")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Misure extends BaseEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9080807909293897692L;

	/**
	*  javadoc for valueDbl
	*/
	private Double valueDbl;

	@Column(name="value_dbl")
	public Double getValueDbl(){
		return valueDbl;
	}

	public void setValueDbl(Double valueDbl){
		this.valueDbl = valueDbl;
	}

	/**
	*  javadoc for um
	*/
	private CodeValuePhi um;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="um")
	@ForeignKey(name="FK_Misure_um")
	//@Index(name="IX_Misure_um")
	public CodeValuePhi getUm(){
		return um;
	}

	public void setUm(CodeValuePhi um){
		this.um = um;
	}

	/**
	*  javadoc for attivita
	*/
	private Attivita attivita;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="attivita_id")
	@ForeignKey(name="FK_Misure_attivita")
	//@Index(name="IX_Misure_attivita")
	public Attivita getAttivita(){
		return attivita;
	}

	public void setAttivita(Attivita attivita){
		this.attivita = attivita;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Misure_sequence")
	@SequenceGenerator(name = "Misure_sequence", sequenceName = "Misure_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	private CodeValue type;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="type")
	@ForeignKey(name="FK_Mis_type")
	//@Index(name="IX_Mis_type")
	public CodeValue getType() {
		return type;
	}

	public void setType(CodeValue type) {
		this.type = type;
	}
	
	private String value;
	
	@Column(name = "value")
	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
//	private String campionamento;
//	
//	@Column(name = "campionamento")
//	public String getCampionamento() {
//		return this.campionamento;
//	}
//
//	public void setCampionamento(String campionamento) {
//		this.campionamento = campionamento;
//	}
	
	private CodeValue misurazioneCv;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="misurazione_cv")
	@ForeignKey(name="FK_Mis_mis")
	//@Index(name="IX_Mis_mis")
	public CodeValue getMisurazioneCv() {
		return misurazioneCv;
	}

	public void setMisurazioneCv(CodeValue misurazioneCv) {
		this.misurazioneCv = misurazioneCv;
	}
	
	private CodeValue campionamentoCv;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="campionamento_cv")
	@ForeignKey(name="FK_Mis_camp")
	//@Index(name="IX_Mis_camp")
	public CodeValue getCampionamentoCv() {
		return campionamentoCv;
	}

	public void setCampionamentoCv(CodeValue campionamentoCv) {
		this.campionamentoCv = campionamentoCv;
	}
}
