package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

@javax.persistence.Entity
@Table(name = "facility")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Facility extends BaseEntity {

	private static final long serialVersionUID = 332673012L;
	
	
	
	//methods needed for BaseEntity extension
	
	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_Facility_sdloc")
	@Index(name = "IX_Facility_sdloc")
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Facility_sequence")
	@SequenceGenerator(name = "Facility_sequence", sequenceName = "Facility_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	/**
	*  System represents facility application to which send notification
	*/
	private String name;

	@Column(name="name",length=10)
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}
	
	/**
	*  System represents the application to which send notification
	*/
	private String application;

	@Column(name="application",length=10)
	public String getApplication(){
		return application;
	}

	public void setApplication(String application){
		this.application = application;
	}
	
	/**
	*  System represents facility application to which send notification
	*/
	private String code;

	@Column(name="code",length=10)
	public String getCode(){
		return code;
	}

	public void setCode(String code){
		this.code = code;
	}
}