package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.role.ServiceDeliveryLocation;

@javax.persistence.Entity
@Table(name = "sdlconf")
@Audited
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class SdlConf extends BaseEntity {

	private static final long serialVersionUID = 1473074113L;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SdlConf_sequence")
	@SequenceGenerator(name = "SdlConf_sequence", sequenceName = "SdlConf_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/*@JsonBackReference(value="sdloc")
	@Override
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_SdlConf_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	
	/**
	*  javadoc for configuredSDL
	*/
	private ServiceDeliveryLocation configuredSDL;

//	@JsonBackReference(value="configuredSdl")
	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="configured_sdl_id")
	@ForeignKey(name="FK_SdlConf_cnfguredSDL")
	@Index(name="IX_SdlConf_cnfguredSDL")
	public ServiceDeliveryLocation getConfiguredSDL(){
		return configuredSDL;
	}

	public void setConfiguredSDL(ServiceDeliveryLocation configuredSDL){
		this.configuredSDL = configuredSDL;
	}
	
	/**
	*  javadoc for rehabProc
	*/
	private boolean rehabProc=false;

	@Column(name="rehab_proc")
	public boolean getRehabProc(){
		return rehabProc;
	}

	public void setRehabProc(boolean rehabProc){
		this.rehabProc = rehabProc;
	}

	
	
}