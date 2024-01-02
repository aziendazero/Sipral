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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.act.Therapy;
import com.phi.entities.role.ServiceDeliveryLocation;

@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public abstract class GenericRequest extends BaseEntity {

	private static final long serialVersionUID = 2405442035058473874L;
	
	//methods needed for BaseEntity extension

	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_GenReq_sdloc")
	@Index(name = "IX_GenReq_sdloc")
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "GenReq_sequence")
	@SequenceGenerator(name = "GenReq_sequence", sequenceName = "GenReq_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	private Therapy therapy;

	/**
	 * In general, pharmacy requests are associated with a therapy (when the request is issued for a patient)
	 * OR with a ward (when it is issued for a ward)
	 * @return
	 */
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="therapy")
	@ForeignKey(name="FK_GenReq_therapy")
	@Index(name="IX_GenReq_therapy")
	public Therapy getTherapy() {
		return therapy;
	}

	public void setTherapy(Therapy therapy) {
		this.therapy = therapy;
	}

	private ServiceDeliveryLocation ward;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ward")
	@ForeignKey(name = "FK_GenReq_ward")
	@Index(name = "IX_GenReq_ward")
	public ServiceDeliveryLocation getWard() {
		return ward;
	}

	public void setWard(ServiceDeliveryLocation ward) {
		this.ward = ward;
	}
}
