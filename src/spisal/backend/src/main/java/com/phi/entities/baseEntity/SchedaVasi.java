package com.phi.entities.baseEntity;

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
import com.phi.entities.baseEntity.ImpRisc;

@javax.persistence.Entity
@Table(name = "scheda_vasi")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SchedaVasi extends BaseEntity {

	private static final long serialVersionUID = 1914559084L;

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
	*  javadoc for press
	*/
	private String press;

	@Column(name="press")
	public String getPress(){
		return press;
	}

	public void setPress(String press){
		this.press = press;
	}

	/**
	*  javadoc for classe
	*/
	private String classe;

	@Column(name="classe")
	public String getClasse(){
		return classe;
	}

	public void setClasse(String classe){
		this.classe = classe;
	}

	/**
	*  javadoc for codiceVaso
	*/
	private String codiceVaso1;

	@Column(name="codice_vaso_1")
	public String getCodiceVaso1(){
		return codiceVaso1;
	}

	public void setCodiceVaso1(String codiceVaso1){
		this.codiceVaso1 = codiceVaso1;
	}

	private String codiceVaso2;

	@Column(name="codice_vaso_2")
	public String getCodiceVaso2(){
		return codiceVaso2;
	}

	public void setCodiceVaso2(String codiceVaso2){
		this.codiceVaso2 = codiceVaso2;
	}

	private String codiceVaso3;

	@Column(name="codice_vaso_3")
	public String getCodiceVaso3(){
		return codiceVaso3;
	}

	public void setCodiceVaso3(String codiceVaso3){
		this.codiceVaso3 = codiceVaso3;
	}

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
	*  javadoc for impRisc
	*/
	private ImpRisc impRisc;

	@ManyToOne(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinColumn(name="imp_risc_id")
	@ForeignKey(name="FK_SchedaVasi_impRisc")
	//@Index(name="IX_SchedaVasi_impRisc")
	public ImpRisc getImpRisc(){
		return impRisc;
	}

	public void setImpRisc(ImpRisc impRisc){
		this.impRisc = impRisc;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SchedaVasi_sequence")
	@SequenceGenerator(name = "SchedaVasi_sequence", sequenceName = "SchedaVasi_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
