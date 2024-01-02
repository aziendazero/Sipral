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
@Table(name = "time_band")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class TimeBand extends BaseEntity {

	private static final long serialVersionUID = 32653907L;

	/**
	*  javadoc for color
	*/
	private String color;

	@Column(name="color")
	public String getColor(){
		return color;
	}

	public void setColor(String color){
		this.color = color;
	}

	/**
	*  javadoc for startTime
	*/
	private Date startTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_time")
	public Date getStartTime(){
		return startTime;
	}

	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}

	/**
	*  javadoc for endTime
	*/
	private Date endTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_time")
	public Date getEndTime(){
		return endTime;
	}

	public void setEndTime(Date endTime){
		this.endTime = endTime;
	}

	/**
	*  javadoc for startDate
	*/
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date")
	public Date getStartDate(){
		return startDate;
	}

	public void setStartDate(Date startDate){
		this.startDate = startDate;
	}

	/**
	*  javadoc for endDate
	*/
	private Date endDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	public Date getEndDate(){
		return endDate;
	}

	public void setEndDate(Date endDate){
		this.endDate = endDate;
	}

	/**
	*  javadoc for monday
	*/
	private Boolean monday;

	@Column(name="monday")
	public Boolean getMonday(){
		return monday;
	}

	public void setMonday(Boolean monday){
		this.monday = monday;
	}

	/**
	*  javadoc for tuesday
	*/
	private Boolean tuesday;

	@Column(name="tuesday")
	public Boolean getTuesday(){
		return tuesday;
	}

	public void setTuesday(Boolean tuesday){
		this.tuesday = tuesday;
	}

	/**
	*  javadoc for wednesday
	*/
	private Boolean wednesday;

	@Column(name="wednesday")
	public Boolean getWednesday(){
		return wednesday;
	}

	public void setWednesday(Boolean wednesday){
		this.wednesday = wednesday;
	}

	/**
	*  javadoc for thursday
	*/
	private Boolean thursday;

	@Column(name="thursday")
	public Boolean getThursday(){
		return thursday;
	}

	public void setThursday(Boolean thursday){
		this.thursday = thursday;
	}

	/**
	*  javadoc for friday
	*/
	private Boolean friday;

	@Column(name="friday")
	public Boolean getFriday(){
		return friday;
	}

	public void setFriday(Boolean friday){
		this.friday = friday;
	}

	/**
	*  javadoc for saturday
	*/
	private Boolean saturday;

	@Column(name="saturday")
	public Boolean getSaturday(){
		return saturday;
	}

	public void setSaturday(Boolean saturday){
		this.saturday = saturday;
	}

	/**
	*  javadoc for sunday
	*/
	private Boolean sunday;

	@Column(name="sunday")
	public Boolean getSunday(){
		return sunday;
	}

	public void setSunday(Boolean sunday){
		this.sunday = sunday;
	}


	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_TmeBand_srvcDlvryLocaton")
	@Index(name="IX_TmeBand_srvcDlvryLocaton")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "TimeBand_sequence")
	@SequenceGenerator(name = "TimeBand_sequence", sequenceName = "TimeBand_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
