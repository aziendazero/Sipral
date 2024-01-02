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

import com.phi.entities.role.ServiceDeliveryLocation;
@javax.persistence.Entity
@Table(name = "aulss")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Aulss extends BaseEntity {

	private static final long serialVersionUID = 636023563L;


	/**
	*  javadoc for destinatariSpisal
	*/
	private DestinatariSpisal destinatariSpisal;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="destinatari_spisal_id")
	@ForeignKey(name="FK_Aulss_destinatariSpisal")
	@Index(name="IX_Aulss_destinatariSpisal")
	public DestinatariSpisal getDestinatariSpisal(){
		return destinatariSpisal;
	}

	public void setDestinatariSpisal(DestinatariSpisal destinatariSpisal){
		this.destinatariSpisal = destinatariSpisal;
	}



	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_lss_servicDlivryLocation")
	@Index(name="IX_lss_servicDlivryLocation")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}



	/**
	*  javadoc for sel
	*/
	private Boolean sel;

	@Column(name="sel")
	public Boolean getSel(){
		return sel;
	}

	public void setSel(Boolean sel){
		this.sel = sel;
	}

	/**
	*  javadoc for fineValidita
	*/
	private Date fineValidita;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fine_validita")
	public Date getFineValidita(){
		return fineValidita;
	}

	public void setFineValidita(Date fineValidita){
		this.fineValidita = fineValidita;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Aulss_sequence")
	@SequenceGenerator(name = "Aulss_sequence", sequenceName = "Aulss_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
