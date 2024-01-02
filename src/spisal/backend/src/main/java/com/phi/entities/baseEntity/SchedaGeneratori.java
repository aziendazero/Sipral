package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.baseEntity.ImpRisc;
import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "scheda_generatori")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SchedaGeneratori extends BaseEntity {

	private static final long serialVersionUID = 1914525535L;

	/**
	*  javadoc for potGlobNom
	*/
	private Double potGlobNom;

	@Column(name="pot_glob_nom")
	public Double getPotGlobNom(){
		return potGlobNom;
	}

	public void setPotGlobNom(Double potGlobNom){
		this.potGlobNom = potGlobNom;
	}

	/**
	*  javadoc for potGlob
	*/
	private Double potGlob;

	@Column(name="pot_glob")
	public Double getPotGlob(){
		return potGlob;
	}

	public void setPotGlob(Double potGlob){
		this.potGlob = potGlob;
	}

	/**
	*  javadoc for codiceCombCv
	*/
	private CodeValuePhi codiceCombCv;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="codiceCombCv")
	@ForeignKey(name="FK_SchedaGeneratori_codiceCombCv")
	//@Index(name="IX_SchedaGeneratori_codiceCombCv")
	public CodeValuePhi getCodiceCombCv(){
		return codiceCombCv;
	}

	public void setCodiceCombCv(CodeValuePhi codiceCombCv){
		this.codiceCombCv = codiceCombCv;
	}

	/**
	*  javadoc for potenza
	*/
	/*private String potenza;

	@Column(name="potenza")
	public String getPotenza(){
		return potenza;
	}

	public void setPotenza(String potenza){
		this.potenza = potenza;
	}*/

	/**
	*  javadoc for codiceComb
	*/
	private String codiceComb;

	@Column(name="codice_comb")
	public String getCodiceComb(){
		return codiceComb;
	}

	public void setCodiceComb(String codiceComb){
		this.codiceComb = codiceComb;
	}



	/**
	*  javadoc for pressMax
	*/
	private String pressMax;

	@Column(name="press_max")
	public String getPressMax(){
		return pressMax;
	}

	public void setPressMax(String pressMax){
		this.pressMax = pressMax;
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
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_SchedaGeneratori_type")
	//@Index(name="IX_SchedaGeneratori_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
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
	@ForeignKey(name="FK_SchedaGeneratori_impRisc")
	//@Index(name="IX_SchedaGeneratori_impRisc")
	public ImpRisc getImpRisc(){
		return impRisc;
	}

	public void setImpRisc(ImpRisc impRisc){
		this.impRisc = impRisc;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SchedaGeneratori_sequence")
	@SequenceGenerator(name = "SchedaGeneratori_sequence", sequenceName = "SchedaGeneratori_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
