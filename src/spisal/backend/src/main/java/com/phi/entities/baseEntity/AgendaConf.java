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

import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;

import com.phi.entities.baseEntity.AppointmentSp;
@javax.persistence.Entity
@Table(name = "agenda_conf")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class AgendaConf extends BaseEntity implements LocatedEntity {

	private static final long serialVersionUID = 1275467602L;


	/**
	*  javadoc for appointmentSp
	*/
	private List<AppointmentSp> appointmentSp;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="agendaConf", cascade=CascadeType.PERSIST)
	public List<AppointmentSp> getAppointmentSp(){
		return appointmentSp;
	}

	public void setAppointmentSp(List<AppointmentSp> list){
		appointmentSp = list;
	}

	public void addAppointmentSp(AppointmentSp appointmentSp) {
		if (this.appointmentSp == null) {
			this.appointmentSp = new ArrayList<AppointmentSp>();
		}
		// add the association
		if(!this.appointmentSp.contains(appointmentSp)) {
			this.appointmentSp.add(appointmentSp);
			// make the inverse link
			appointmentSp.setAgendaConf(this);
		}
	}

	public void removeAppointmentSp(AppointmentSp appointmentSp) {
		if (this.appointmentSp == null) {
			this.appointmentSp = new ArrayList<AppointmentSp>();
			return;
		}
		//add the association
		if(this.appointmentSp.contains(appointmentSp)){
			this.appointmentSp.remove(appointmentSp);
			//make the inverse link
			appointmentSp.setAgendaConf(null);
		}

	}



//	/**
//	*  javadoc for lineaDiLavoro
//	*/
//	private ServiceDeliveryLocation lineaDiLavoro;
//
//	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="linea_di_lavoro_id")
//	@ForeignKey(name="FK_AgendaConf_lineaDiLavoro")
//	//@Index(name="IX_AgendaConf_lineaDiLavoro")
//	public ServiceDeliveryLocation getLineaDiLavoro(){
//		return lineaDiLavoro;
//	}
//
//	public void setLineaDiLavoro(ServiceDeliveryLocation lineaDiLavoro){
//		this.lineaDiLavoro = lineaDiLavoro;
//	}


	
	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_AgendaConf_sdloc")
	//@Index(name="IX_AgendaConf_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
	
	/**
	*  javadoc for validityEnd
	*/
	private Date validityEnd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="validity_end")
	public Date getValidityEnd(){
		return validityEnd;
	}

	public void setValidityEnd(Date validityEnd){
		this.validityEnd = validityEnd;
	}

	/**
	*  javadoc for validyStart
	*/
	private Date validyStart;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="validy_start")
	public Date getValidyStart(){
		return validyStart;
	}

	public void setValidyStart(Date validyStart){
		this.validyStart = validyStart;
	}


	/**
	*  javadoc for nome
	*/
	private String nome;

	@Column(name="nome")
	public String getNome(){
		return nome;
	}

	public void setNome(String nome){
		this.nome = nome;
	}

	/**
	*  javadoc for slotSize
	*/
	private Integer slotSize;

	@Column(name="slot_size")
	public Integer getSlotSize(){
		return slotSize;
	}

	public void setSlotSize(Integer slotSize){
		this.slotSize = slotSize;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AgendaConf_sequence")
	@SequenceGenerator(name = "AgendaConf_sequence", sequenceName = "AgendaConf_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
