package com.phi.entities.baseEntity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.role.ServiceDeliveryLocation;
@javax.persistence.Entity
@Table(name = "classification")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Classification extends BaseEntity {

	private static final long serialVersionUID = 1833433153L;

	/**
	*  javadoc for classificationCodes
	*/
	private List<CodeValuePhi> classificationCodes;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="Clssfctn_clssfctnCodes", joinColumns = { @JoinColumn(name="Classification_id") }, inverseJoinColumns = { @JoinColumn(name="classificationCodes") })
	@ForeignKey(name="FK_clssfctnCodes_Clssfctn", inverseName="FK_Clssfctn_clssfctnCodes")
	public List<CodeValuePhi> getClassificationCodes(){
		return classificationCodes;
	}

	public void setClassificationCodes(List<CodeValuePhi> classificationCodes){
		this.classificationCodes = classificationCodes;
	}


	/**
	*  javadoc for sdlConfAMB
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="sdl_id")
	@ForeignKey(name="FK_Clssfctn_sdlLoc")
	@Index(name="IX_Clssfctn_sdlLoc")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}




	/**
	*  javadoc for languageCode
	*/
	private CodeValuePhi languageCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="languageCode")
	@ForeignKey(name="FK_Clssfctn_languagCode")
	@Index(name="IX_Clssfctn_languagCode")
	public CodeValuePhi getLanguageCode(){
		return languageCode;
	}

	public void setLanguageCode(CodeValuePhi languageCode){
		this.languageCode = languageCode;
	}

	
	/**
	*  javadoc for title
	*/
	private String title;

	@Column(name="title")
	public String getTitle(){
		return title;
	}

	public void setTitle(String title){
		this.title = title;
	}

	/**
	*  javadoc for text
	*/
	private String text;

	@Column(name="text",length=2500)
	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text = text;
	}
	
	
	
	//methods needed for BaseEntity extension
	
	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_CClssfctn_sdloc")
	@Index(name = "IX_Clssfctn_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Clssfctn_sequence")
	@SequenceGenerator(name = "Clssfctn_sequence", sequenceName = "Clssfctn_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
