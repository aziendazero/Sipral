package com.phi.entities.baseEntity;

import java.io.Serializable;

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

import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueLaw;

@javax.persistence.Entity
@Table(name = "costo_diretto")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class CostoDiretto extends BaseEntity implements LocatedEntity, Serializable {

	private static final long serialVersionUID = 387574360L;

	/**
	*  javadoc for auto
	*/
	private Boolean auto;

	@Column(name="auto")
	public Boolean getAuto(){
		if (auto==null)
			return false;
		
		return auto;
	}

	public void setAuto(Boolean auto){
		this.auto = auto;
	}

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
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_CstDrett_servcDlvryLcatn")
	@Index(name="IX_CstDrett_servcDlvryLcatn")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}


	/**
	*  javadoc for tipoProvv
	*/
	private CodeValuePhi tipoProvv;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipoProvv")
	@ForeignKey(name="FK_CostoDiretto_tipoProvv")
	@Index(name="IX_CostoDiretto_tipoProvv")
	public CodeValuePhi getTipoProvv(){
		return tipoProvv;
	}

	public void setTipoProvv(CodeValuePhi tipoProvv){
		this.tipoProvv = tipoProvv;
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
	*  javadoc for conc
	*/
	private String conc;

	@Column(name="conc")
	public String getConc(){
		return conc;
	}

	public void setConc(String conc){
		this.conc = conc;
	}

	/**
	*  javadoc for qualifica
	*/
	private CodeValuePhi qualifica;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="qualifica")
	@ForeignKey(name="FK_CostoDiretto_qualifica")
	@Index(name="IX_CostoDiretto_qualifica")
	public CodeValuePhi getQualifica(){
		return qualifica;
	}

	public void setQualifica(CodeValuePhi qualifica){
		this.qualifica = qualifica;
	}

	/**
	*  javadoc for articolo
	*/
	private CodeValueLaw articolo;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="articolo")
	@ForeignKey(name="FK_CostoDiretto_articolo")
	@Index(name="IX_CostoDiretto_articolo")
	public CodeValueLaw getArticolo(){
		return articolo;
	}

	public void setArticolo(CodeValueLaw articolo){
		this.articolo = articolo;
	}

	/**
	*  javadoc for sottoTipo
	*/
	private CodeValuePhi sottotipoAtt;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="sottotipoAtt")
	@ForeignKey(name="FK_CostoDiretto_sottotipoAtt")
	@Index(name="IX_CostoDiretto_sottotipoAtt")
	public CodeValuePhi getSottotipoAtt(){
		return sottotipoAtt;
	}

	public void setSottotipoAtt(CodeValuePhi sottotipoAtt){
		this.sottotipoAtt = sottotipoAtt;
	}

	/**
	*  javadoc for tipo
	*/
	private CodeValuePhi tipoAtt;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipo_att")
	@ForeignKey(name="FK_CostoDiretto_tipoAtt")
	@Index(name="IX_CostoDiretto_tipoAtt")
	public CodeValuePhi getTipoAtt(){
		return tipoAtt;
	}

	public void setTipoAtt(CodeValuePhi tipoAtt){
		this.tipoAtt = tipoAtt;
	}

	/**
	*  javadoc for tipologia
	*/
	private CodeValuePhi tipologia;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tipologia")
	@ForeignKey(name="FK_CostoDiretto_tipologia")
	@Index(name="IX_CostoDiretto_tipologia")
	public CodeValuePhi getTipologia(){
		return tipologia;
	}

	public void setTipologia(CodeValuePhi tipologia){
		this.tipologia = tipologia;
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



	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "CostoDiretto_sequence")
	@SequenceGenerator(name = "CostoDiretto_sequence", sequenceName = "CostoDiretto_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
