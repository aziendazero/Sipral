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

import java.io.Serializable;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.dataTypes.CodeValuePhi;

import com.phi.entities.dataTypes.CodeValue;
@javax.persistence.Entity
@Table(name = "costo_indiretto")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class CostoIndiretto extends BaseEntity implements LocatedEntity, Serializable {

	private static final long serialVersionUID = 387421738L;

	/**
	*  javadoc for annoAl
	*/
	private Integer annoAl;

	@Column(name="anno_al")
	public Integer getAnnoAl(){
		return annoAl;
	}

	public void setAnnoAl(Integer annoAl){
		this.annoAl = annoAl;
	}

	/**
	*  javadoc for tipoLineaDiLavoro
	*/
	private CodeValuePhi tipoLineaDiLavoro;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoLineaDiLavoro")
	@ForeignKey(name="FK_Costo_tipoLinea")
	@Index(name="IX_Costo_tipoLinea")
	public CodeValuePhi getTipoLineaDiLavoro(){
		return tipoLineaDiLavoro;
	}

	public void setTipoLineaDiLavoro(CodeValuePhi tipoLineaDiLavoro){
		this.tipoLineaDiLavoro = tipoLineaDiLavoro;
	}

	/**
	*  javadoc for lineaDiLavoro
	*/
	private CodeValuePhi lineaDiLavoro;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="lineaDiLavoro")
	@ForeignKey(name="FK_Costo_linea")
	@Index(name="IX_Costo_linea")
	public CodeValuePhi getLineaDiLavoro(){
		return lineaDiLavoro;
	}

	public void setLineaDiLavoro(CodeValuePhi lineaDiLavoro){
		this.lineaDiLavoro = lineaDiLavoro;
	}

	/**
	*  javadoc for peso
	*/
	private String peso;

	@Column(name="peso")
	public String getPeso(){
		return peso;
	}

	public void setPeso(String peso){
		this.peso = peso;
	}

	/**
	*  javadoc for tipologia
	*/
	private CodeValuePhi tipologia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologia")
	@ForeignKey(name="FK_CostoIndiretto_tipologia")
	@Index(name="IX_CostoIndiretto_tipologia")
	public CodeValuePhi getTipologia(){
		return tipologia;
	}

	public void setTipologia(CodeValuePhi tipologia){
		this.tipologia = tipologia;
	}

	/**
	*  javadoc for qualifica
	*/
	private CodeValuePhi qualifica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="qualifica")
	@ForeignKey(name="FK_CostoIndiretto_qualifica")
	@Index(name="IX_CostoIndiretto_qualifica")
	public CodeValuePhi getQualifica(){
		return qualifica;
	}

	public void setQualifica(CodeValuePhi qualifica){
		this.qualifica = qualifica;
	}

	/**
	*  javadoc for anno
	*/
	private Integer anno;

	@Column(name="anno")
	public Integer getAnno(){
		return anno;
	}

	public void setAnno(Integer anno){
		this.anno = anno;
	}


	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_Cstndrett_srvcDlvryLcatn")
	@Index(name="IX_Cstndrett_srvcDlvryLcatn")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CostoIndiretto_sequence")
	@SequenceGenerator(name = "CostoIndiretto_sequence", sequenceName = "CostoIndiretto_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
