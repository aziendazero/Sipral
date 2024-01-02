package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;

@javax.persistence.Entity
@Table(name = "tipologia_cantiere")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class TipologiaCantiere extends BaseEntity implements LocatedEntity{

	private static final long serialVersionUID = 594155909L;


	/**
	*  javadoc for type
	*/
	private String type;

	@Column(name="type")
	public String getType(){
		return type;
	}

	public void setType(String type){
		this.type = type;
	}

	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_typeC_sdloc")
	//@Index(name="IX_typeC_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}


	/**
	*  javadoc for endValidity
	*/
	private Date endValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_validity")
	public Date getEndValidity(){
		return endValidity;
	}

	public void setEndValidity(Date endValidity){
		this.endValidity = endValidity;
	}

	/**
	*  javadoc for startValidity
	*/
	private Date startValidity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_validity")
	public Date getStartValidity(){
		return startValidity;
	}

	public void setStartValidity(Date startValidity){
		this.startValidity = startValidity;
	}
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TipologiaCantiere_sequence")
	@SequenceGenerator(name = "TipologiaCantiere_sequence", sequenceName = "TipologiaCantiere_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
