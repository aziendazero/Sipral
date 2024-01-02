package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.role.Operatore;

import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
@javax.persistence.Entity
@Table(name = "vigilanza_rdp_type")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class VigilanzaRdpType extends BaseEntity {

	private static final long serialVersionUID = 1017588856L;

	/**
	*  javadoc for vigilanzaType
	*/
	private CodeValuePhi vigilanzaType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="vigilanzaType")
	@ForeignKey(name="FK_VigilanzaRdpType_vigilanzaType")
	//@Index(name="IX_VigilanzaRdpType_vigilanzaType")
	public CodeValuePhi getVigilanzaType(){
		return vigilanzaType;
	}

	public void setVigilanzaType(CodeValuePhi vigilanzaType){
		this.vigilanzaType = vigilanzaType;
	}


	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_VglnzRdpTyp_srvcDlvryLctn")
	//@Index(name="IX_VglnzRdpTyp_srvcDlvryLctn")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}



	/**
	*  javadoc for operatore
	*/
	private Operatore operatore;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="operatore_id")
	@ForeignKey(name="FK_VglanzaRdpType_operatore")
	//@Index(name="IX_VglanzaRdpType_operatore")
	public Operatore getOperatore(){
		return operatore;
	}

	public void setOperatore(Operatore operatore){
		this.operatore = operatore;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "VigilanzaRdpType_sequence")
	@SequenceGenerator(name = "VigilanzaRdpType_sequence", sequenceName = "VigilanzaRdpType_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
