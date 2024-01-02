package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.phi.entities.Application;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.role.Employee;
import com.phi.entities.role.ServiceDeliveryLocation;

@javax.persistence.Entity
@Table(name = "employee_role")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class EmployeeRole extends BaseEntity {

	private static final long serialVersionUID = 1643557211L;

	//methods needed for BaseEntity extension

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "EmployeeRole_sequence")
	@SequenceGenerator(name = "EmployeeRole_sequence", sequenceName = "EmployeeRole_sequence")
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
	@ForeignKey(name = "FK_EmployeeRole_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/

	/**
	 *  role code for employee
	 */
	private CodeValueRole code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_EmployeeRole_code")
	@Index(name="IX_EmployeeRole_code")
	public CodeValueRole getCode(){
		return code;
	}

	public void setCode(CodeValueRole code){
		this.code = code;
	}

	/**
	*  Doctor specialization Code
	*/
	private CodeValuePhi specialization;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="specialization")
	@ForeignKey(name="FK_EmployeeRole_specializ")
	@Index(name="IX_EmployeeRole_specializ")
	public CodeValuePhi getSpecialization(){
		return specialization;
	}

	public void setSpecialization(CodeValuePhi specialization){
		this.specialization = specialization;
	}	
	
	/**
	 *  effectiveTime: role valid date interval
	 */
	private IVL<Date> effectiveTime;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="effective_time_low")),
		@AttributeOverride(name="high", column=@Column(name="effective_time_high")),
		@AttributeOverride(name="lowClosed", column=@Column(name="effective_time_lowClosed")),
		@AttributeOverride(name="highClosed", column=@Column(name="effective_time_highClosed"))
	})
	public IVL<Date> getEffectiveTime(){
		return effectiveTime;
	}

	public void setEffectiveTime(IVL<Date> effectiveTime){
		this.effectiveTime = effectiveTime;
	}

	private Boolean isCoordinator;

	@Column(name="is_coordinator")
	public Boolean getIsCoordinator() {
		return isCoordinator;
	}

	public void setIsCoordinator(Boolean isCoordinator) {
		this.isCoordinator = isCoordinator;
	}

	// SHORTCUTS
	private Employee employee;
	
	@JsonBackReference
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="employee_id")
	@ForeignKey(name="FK_EmployeeRole_Person")
	@Index(name="IX_EmployeeRole_Person")
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	private List<ServiceDeliveryLocation> enabledServiceDeliveryLocations;

	@ManyToMany(fetch=FetchType.LAZY/*, cascade=CascadeType.PERSIST*/)
	@JoinTable(name="employeerole_servicedelivery", 
	joinColumns = { @JoinColumn(name="empRole_id", nullable=false, updatable=false) }, 
	inverseJoinColumns = { @JoinColumn(name="sdloc_id", nullable=false, updatable=false) })
	@ForeignKey(name="FK_EmployeeRole_empRole", inverseName="FK_sdloc_employeeRole")
	@IndexColumn(name="list_index")
	public List<ServiceDeliveryLocation> getEnabledServiceDeliveryLocations() {
		return enabledServiceDeliveryLocations;
	}

	public void setEnabledServiceDeliveryLocations(List<ServiceDeliveryLocation> enabledServiceDeliveryLocations) {
		this.enabledServiceDeliveryLocations=enabledServiceDeliveryLocations;
	}

	public void addEnabledServiceDeliveryLocations (ServiceDeliveryLocation enabledServiceDeliveryLocations) {
		// create the association set if it doesn't exist already
		if (this.enabledServiceDeliveryLocations == null) {
			this.enabledServiceDeliveryLocations = new ArrayList<ServiceDeliveryLocation>();
		}
		// add the association to the association set
		if(!this.enabledServiceDeliveryLocations.contains(enabledServiceDeliveryLocations)) {
			this.enabledServiceDeliveryLocations.add(enabledServiceDeliveryLocations);
		}
	}

	public void removeEnabledServiceDeliveryLocations (ServiceDeliveryLocation enabledServiceDeliveryLocations) {
		// if it doesn't exist already return
		if (this.enabledServiceDeliveryLocations == null) {
			return;
		}
		// remove the association to the association set
		if(this.enabledServiceDeliveryLocations.contains(enabledServiceDeliveryLocations)) {
			this.enabledServiceDeliveryLocations.remove(enabledServiceDeliveryLocations);
		}
	}
	
	
	/*private List<AdministrativeOrg> enabledAdministrativeOrgs;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="employeeRole_adminOrg", 
	joinColumns = { @JoinColumn(name="empRole_id", nullable=false, updatable=false) }, 
	inverseJoinColumns = { @JoinColumn(name="adminOrg_id", nullable=false, updatable=false) })
	@ForeignKey(name="FK_EmployeeRole_adminOrg", inverseName="FK_adminOrg_employeeRole")
	public List<AdministrativeOrg> getEnabledAdministrativeOrgs() {
		return enabledAdministrativeOrgs;
	}

	public void setEnabledAdministrativeOrgs(List<AdministrativeOrg> enabledAdministrativeOrgs) {
		this.enabledAdministrativeOrgs=enabledAdministrativeOrgs;
	}

	public void addEnabledAdministrativeOrgs (AdministrativeOrg enabledAdministrativeOrgs) {
		// create the association set if it doesn't exist already
		if (this.enabledAdministrativeOrgs == null) {
			this.enabledAdministrativeOrgs = new ArrayList<AdministrativeOrg>();
		}
		// add the association to the association set
		if(!this.enabledAdministrativeOrgs.contains(enabledAdministrativeOrgs)) {
			this.enabledAdministrativeOrgs.add(enabledAdministrativeOrgs);
		}
	}

	public void removeEnabledAdministrativeOrgs (AdministrativeOrg enabledAdministrativeOrgs) {
		// if it doesn't exist already return
		if (this.enabledAdministrativeOrgs == null) {
			return;
		}
		// remove the association to the association set
		if(this.enabledAdministrativeOrgs.contains(enabledAdministrativeOrgs)) {
			this.enabledAdministrativeOrgs.remove(enabledAdministrativeOrgs);
		}
	}*/
	
	/**
	*  javadoc for direzioni
	*/
	private List<CodeValuePhi> enabledOffices;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="employeerole_offices", joinColumns = { @JoinColumn(name="employeeRole_id") }, inverseJoinColumns = { @JoinColumn(name="offices") })
	@ForeignKey(name="FK_offices_employeeRole", inverseName="FK_employeeRole_offices")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getEnabledOffices(){
		return enabledOffices;
	}

	public void setEnabledOffices(List<CodeValuePhi> enabledOffices){
		this.enabledOffices = enabledOffices;
	}
	
	/**
	 * SSA ManagInstit id (weak relation)
	 */
	private Long managInstit;
	
	@Column(name = "manag_instit_id")
	public Long getManagInstit() {
		return managInstit;
	}

	public void setManagInstit(Long managInstit) {
		this.managInstit = managInstit;
	}
	
	/**
	 * SSA ManagInstit name
	 */
	private String managInstitName;
	
	@Column(name = "manag_instit_name")
	public String getManagInstitName() {
		return managInstitName;
	}

	public void setManagInstitName(String managInstitName) {
		this.managInstitName = managInstitName;
	}
	

	private List<Application> application;

	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="emprole_application", joinColumns = { @JoinColumn(name="employeeRole_id") }, inverseJoinColumns = { @JoinColumn(name="application_id") })
	@ForeignKey(name="FK_application_EmployeeRole", inverseName="FK_EmployeeRole_application")
	public List<Application> getApplication() {
		return application;
	}

	public void setApplication(List<Application>list){
		application = list;
	}
	
	/**
	*  javadoc for favoriteSdl
	*/
	private String favoriteSdl;

	@Column(name="favorite_sdl")
	public String getFavoriteSdl(){
		return favoriteSdl;
	}

	public void setFavoriteSdl(String favoriteSdl){
		this.favoriteSdl = favoriteSdl;
	}


	
	@Override
	public String toString() {
		if (getCode() != null) {
			return getCode().toString();
		}
		return "";
	}
}