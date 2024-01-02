package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;


import java.util.ArrayList;
import java.util.List;
import com.phi.entities.baseEntity.VerificaImp;
import com.phi.entities.dataTypes.CodeValuePhi;

@javax.persistence.Entity
@Table(name = "imp_search_collector")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ImpSearchCollector extends BaseEntity {

	private static final long serialVersionUID = 154176878L;

	/**
	*  javadoc for code
	*/
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_ImpSearchCollector_code")
	//@Index(name="IX_ImpSearchCollector_code")
	public CodeValuePhi getCode(){
		return code;
	}

	public void setCode(CodeValuePhi code){
		this.code = code;
	}

	/**
	*  javadoc for denominazioneSA
	*/
	private String denominazioneSA;

	@Column(name="denominazione_sa")
	public String getDenominazioneSA(){
		return denominazioneSA;
	}

	public void setDenominazioneSA(String denominazioneSA){
		this.denominazioneSA = denominazioneSA;
	}

	/**
	*  javadoc for denominazioneSI
	*/
	private String denominazioneSI;

	@Column(name="denominazione_si")
	public String getDenominazioneSI(){
		return denominazioneSI;
	}

	public void setDenominazioneSI(String denominazioneSI){
		this.denominazioneSI = denominazioneSI;
	}
	
	/**
	*  javadoc for denominazioneSI
	*/
	private String denominazioneIS;

	@Column(name="denominazione_is")
	public String getDenominazioneIS(){
		return denominazioneIS;
	}

	public void setDenominazioneIS(String denominazioneIS){
		this.denominazioneIS = denominazioneIS;
	}

	/**
	*  javadoc for verificaImp
	*/
	private List<VerificaImp> verificaImp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="impSearchCollector", cascade=CascadeType.PERSIST)
	public List<VerificaImp> getVerificaImp(){
		return verificaImp;
	}

	public void setVerificaImp(List<VerificaImp> list){
		verificaImp = list;
	}

	public void addVerificaImp(VerificaImp verificaImp) {
		if (this.verificaImp == null) {
			this.verificaImp = new ArrayList<VerificaImp>();
		}
		// add the association
		if(!this.verificaImp.contains(verificaImp)) {
			this.verificaImp.add(verificaImp);
			// make the inverse link
			verificaImp.setImpSearchCollector(this);
		}
	}

	public void removeVerificaImp(VerificaImp verificaImp) {
		if (this.verificaImp == null) {
			this.verificaImp = new ArrayList<VerificaImp>();
			return;
		}
		//add the association
		if(this.verificaImp.contains(verificaImp)){
			this.verificaImp.remove(verificaImp);
			//make the inverse link
			verificaImp.setImpSearchCollector(null);
		}

	}

	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ImpSearchCollector_sequence")
	@SequenceGenerator(name = "ImpSearchCollector_sequence", sequenceName = "ImpSearchCollector_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for anno
	*/
	private String anno;

	@Column(name="anno")
	public String getAnno(){
		return anno;
	}

	public void setAnno(String anno){
		this.anno = anno;
	}

	/**
	*  javadoc for matricola
	*/
	private String matricola;

	@Column(name="matricola")
	public String getMatricola(){
		return matricola;
	}

	public void setMatricola(String matricola){
		this.matricola = matricola;
	}

	/**
	*  javadoc for sigla
	*/
	private String sigla;

	@Column(name="sigla")
	public String getSigla(){
		return sigla;
	}

	public void setSigla(String sigla){
		this.sigla = sigla;
	}
	
	/**
	*  javadoc for subTypeSoll
	*/
	private CodeValuePhi subTypeSoll;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeSoll")
	@ForeignKey(name="FK_Coll_subTypeSoll")
	//@Index(name="IX_Coll_subTypeSoll")
	public CodeValuePhi getSubTypeSoll(){
		return subTypeSoll;
	}

	public void setSubTypeSoll(CodeValuePhi subTypeSoll){
		this.subTypeSoll = subTypeSoll;
	}

	/**
	*  javadoc for subTypeTerra
	*/
	private CodeValuePhi subTypeTerra;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="subTypeTerra")
	@ForeignKey(name="FK_Coll_subTypeTerra")
	//@Index(name="IX_Coll_subTypeTerra")
	public CodeValuePhi getSubTypeTerra(){
		return subTypeTerra;
	}

	public void setSubTypeTerra(CodeValuePhi subTypeTerra){
		this.subTypeTerra = subTypeTerra;
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

}
