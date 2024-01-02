package com.phi.entities.baseEntity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.act.PatientEncounter;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.tracedEntity.TracedEntity;

@javax.persistence.Entity
@Table(name = "transfer")
@Audited
public class Transfer extends BaseEntity implements TracedEntity {

	private static final long serialVersionUID = 2142114502L;
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "transfer_sequence")
	@SequenceGenerator(name = "transfer_sequence", sequenceName = "transfer_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}

	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/*@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_transfer_sdloc")
	@Index(name="IX_transfer_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	private Employee author;
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "author")
	@ForeignKey(name = "FK_Transfer_Author")
	@Index(name="IX_Transfer_Author")
	public Employee getAuthor() {
		return author;
	}

	public void setAuthor(Employee param) {
		this.author = param;
	}

	private PatientEncounter patientEncounter;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "patient_encounter")
	@ForeignKey(name = "FK_Transfer_Pat_Enc")
	@Index(name="IX_Transfer_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
		return patientEncounter;
	}

	public void setPatientEncounter(PatientEncounter param) {
		this.patientEncounter = param;
	}

	private Date registrationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "registration_date")
	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	private Date effectiveDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "effective_date")
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	private ServiceDeliveryLocation SDLocFrom;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "SDLocFrom")
	@ForeignKey(name = "FK_SDLoc_From")
	@Index(name="IX_SDLoc_From")
	public ServiceDeliveryLocation getSDLocFrom() {
		return SDLocFrom;
	}

	public void setSDLocFrom(ServiceDeliveryLocation SDLocFrom) {
		this.SDLocFrom = SDLocFrom;
	}

	private ServiceDeliveryLocation SDLocTo;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "SDLocTo")
	@ForeignKey(name = "FK_SDLoc_To")
	@Index(name="IX_SDLoc_To")
	public ServiceDeliveryLocation getSDLocTo() {
		return SDLocTo;
	}

	public void setSDLocTo(ServiceDeliveryLocation SDLocTo) {
		this.SDLocTo = SDLocTo;
	}
	
	/**
	*  javadoc for isSupport
	*/
	
	private Boolean isSupport = false;

	@Column(name="is_support")
	public Boolean getIsSupport(){
		return isSupport;
	}

	public void setIsSupport(Boolean isSupport){
		this.isSupport = isSupport;
	}
	
	private ServiceDeliveryLocation SDLocSupport;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "SDLocSupport")
	@ForeignKey(name = "FK_SDLoc_Support")
	@Index(name="IX_SDLoc_Support")
	public ServiceDeliveryLocation getSDLocSupport() {
		return SDLocSupport;
	}

	public void setSDLocSupport(ServiceDeliveryLocation SDLocSupport) {
		this.SDLocSupport = SDLocSupport;
	}
	
	// methods needed for TracedEntity implementation
	/**
	 * javadoc for createdByLocation
	 */
	private String createdByLocation;

	@Column(name = "created_by_location")
	public String getCreatedByLocation() {
		return createdByLocation;
	}

	public void setCreatedByLocation(String createdByLocation) {
		this.createdByLocation = createdByLocation;
	}

	/**
	 * javadoc for modifiedBy
	 */
	private String modifiedBy;

	@Column(name = "modified_by")
	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * javadoc for modificationDate
	 */
	private Date modificationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "modification_date")
	public Date getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(Date modificationDate) {
		this.modificationDate = modificationDate;
	}

	/**
	 * javadoc for modifiedByLocation
	 */
	private String modifiedByLocation;

	@Column(name = "modified_by_location")
	public String getModifiedByLocation() {
		return modifiedByLocation;
	}

	public void setModifiedByLocation(String modifiedByLocation) {
		this.modifiedByLocation = modifiedByLocation;
	}

}
