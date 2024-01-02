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

import com.phi.entities.baseEntity.Provvedimenti;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.Person;

@javax.persistence.Entity
@Table(name = "iter758")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Iter758 extends BaseEntity {

	private static final long serialVersionUID = 703021324L;

	/**
	*  javadoc for legaleStr
	*/
	private String legaleStr;

	@Column(name="legale_str")
	public String getLegaleStr(){
		return legaleStr;
	}

	public void setLegaleStr(String legaleStr){
		this.legaleStr = legaleStr;
	}

	/**
	*  javadoc for procuraStr
	*/
	private String procuraStr;

	@Column(name="procura_str")
	public String getProcuraStr(){
		return procuraStr;
	}

	public void setProcuraStr(String procuraStr){
		this.procuraStr = procuraStr;
	}

	/**
	*  javadoc for magistratoStr
	*/
	private String magistratoStr;

	@Column(name="magistrato_str")
	public String getMagistratoStr(){
		return magistratoStr;
	}

	public void setMagistratoStr(String magistratoStr){
		this.magistratoStr = magistratoStr;
	}

//	/**
//	*  javadoc for dataNotifica
//	*/
//	private Date dataNotifica;
//
//	@Temporal(TemporalType.TIMESTAMP)
//	@Column(name="data_notifica")
//	public Date getDataNotifica(){
//		return dataNotifica;
//	}
//
//	public void setDataNotifica(Date dataNotifica){
//		this.dataNotifica = dataNotifica;
//	}

	/**
	*  javadoc for legale
	*/
	private Person legale;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="legale_id")
	@ForeignKey(name="FK_Iter758_legale")
	//@Index(name="IX_Iter758_legale")
	public Person getLegale(){
		return legale;
	}

	public void setLegale(Person legale){
		this.legale = legale;
	}

	/**
	*  javadoc for numeroFascicolo
	*/
	private String numeroFascicolo;

	@Column(name="numero_fascicolo")
	public String getNumeroFascicolo(){
		return numeroFascicolo;
	}

	public void setNumeroFascicolo(String numeroFascicolo){
		this.numeroFascicolo = numeroFascicolo;
	}

	/**
	*  javadoc for procura
	*/
	private CodeValuePhi procura;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="procura")
	@ForeignKey(name="FK_Iter758_procura")
	//@Index(name="IX_Iter758_procura")
	public CodeValuePhi getProcura(){
		return procura;
	}

	public void setProcura(CodeValuePhi procura){
		this.procura = procura;
	}

	/**
	*  javadoc for magistrato
	*/
	private CodeValuePhi magistrato;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="magistrato")
	@ForeignKey(name="FK_Iter758_magistrato")
	//@Index(name="IX_Iter758_magistrato")
	public CodeValuePhi getMagistrato(){
		return magistrato;
	}

	public void setMagistrato(CodeValuePhi magistrato){
		this.magistrato = magistrato;
	}

	/**
	*  javadoc for provvedimento
	*/
	private Provvedimenti provvedimento;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="provvedimento_id")
	@ForeignKey(name="FK_Iter758_provvedimento")
	//@Index(name="IX_Iter758_provvedimento")
	public Provvedimenti getProvvedimento(){
		return provvedimento;
	}

	public void setProvvedimento(Provvedimenti provvedimento){
		this.provvedimento = provvedimento;
	}
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Iter758_sequence")
	@SequenceGenerator(name = "Iter758_sequence", sequenceName = "Iter758_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
