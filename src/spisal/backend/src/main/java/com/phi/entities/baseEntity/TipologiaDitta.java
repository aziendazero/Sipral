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
@Table(name = "tipologia_ditta")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class TipologiaDitta extends BaseEntity implements LocatedEntity{

	private static final long serialVersionUID = 683047283L;

	/**
	*  javadoc for UOS
	*/
	private ServiceDeliveryLocation uos;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="uos")
	@ForeignKey(name="FK_typeDit_uos")
	//@Index(name="IX_typeDit_uos")
	public ServiceDeliveryLocation getUos(){
		return uos;
	}

	public void setUos(ServiceDeliveryLocation uos){
		this.uos = uos;
	}
	
	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_typeDit_sdloc")
	//@Index(name="IX_typeDit_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

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
	*  javadoc for notes
	*/
	private String notes;

	@Column(name="notes",length=2000)
	public String getNotes(){
		return notes;
	}

	public void setNotes(String notes){
		this.notes = notes;
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TipologiaDitta_sequence")
	@SequenceGenerator(name = "TipologiaDitta_sequence", sequenceName = "TipologiaDitta_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
