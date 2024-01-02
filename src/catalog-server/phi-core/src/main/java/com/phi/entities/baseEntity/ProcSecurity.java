package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValueParameter;

/**
 * Defines roles and servecedeliverylocations enabled to execute a process
 */

@javax.persistence.Entity
@Table(name = "proc_security")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ProcSecurity extends BaseEntity {

	private static final long serialVersionUID = 1011522300L;

	/**
	*  If true is a main process
	*  False is a subprocess
	*  Used only to filter processes in ProcessSecurity process
	*/
	private Boolean macroprocess;

	@Column(name="macroprocess")
	public Boolean getMacroprocess(){
		return macroprocess;
	}

	public void setMacroprocess(Boolean macroprocess){
		this.macroprocess = macroprocess;
	}

	
	/**
	*  customer
	*/
	private String customer;

	@Column(name="customer" )
	public String getCustomer(){
		return customer;
	}

	public void setCustomer(String customer){
		this.customer = customer;
	}
	
	/**
	*  javadoc for securityString
	*/
	private String securityString;

	@Column(name="security_string" )
	public String getSecurityString(){
		return securityString;
	}

	public void setSecurityString(String securityString){
		this.securityString = securityString;
	}

	/**
	*  alwaysExecutable
	*  used to execute a process from dashboard
	*/
	private Boolean alwaysExecutable;

	@Column(name="always_executable")
	public Boolean getAlwaysExecutable(){
		return alwaysExecutable;
	}

	public void setAlwaysExecutable(Boolean alwaysExecutable){
		this.alwaysExecutable = alwaysExecutable;
	}
	
	/**
	*  Defines enabled roles and if process is readOnly for a role or not
	*/
	private List<ProcSecurityRole> procSecurityRole;

	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.REMOVE })
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	@JoinColumn(name="ProcSecurity_id")
	@ForeignKey(name="FK_prcSecurityRl_PrcScurity")
	@Index(name="IX_prcSecurityRl_PrcScurity")
	@AuditJoinTable(name="z_ProcSecurity_ProcSecRole")
	public List<ProcSecurityRole> getProcSecurityRole() {
		return procSecurityRole;
	}

	public void setProcSecurityRole(List<ProcSecurityRole> list){
		procSecurityRole = list;
	}
	
	public void addProcSecurityRole (ProcSecurityRole procSecurityRole) {
		// create the association set if it doesn't exist already
		if (this.procSecurityRole == null) {
			this.procSecurityRole = new ArrayList<ProcSecurityRole>();
		}
		// add the association to the association set
		if(!this.procSecurityRole.contains(procSecurityRole)) {
			this.procSecurityRole.add(procSecurityRole);
		}
	}

	public void removeProcSecurityRole (ProcSecurityRole procSecurityRole) {
		// if it doesn't exist already return
		if (this.procSecurityRole == null) {
			return;
		}
		// remove the association to the association set
		if(this.procSecurityRole.contains(procSecurityRole)) {
			this.procSecurityRole.remove(procSecurityRole);
		}
	}
	
//	/**
//	*  Enabled serviceDeliveryLocations for a process
//	*/
//	private List<ServiceDeliveryLocation> serviceDeliveryLocations;
//
//	@ManyToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinTable(name="ProcSecurity_Sdloc",
//		joinColumns = { @JoinColumn(name="ProcSecurity_id") },
//		inverseJoinColumns = { @JoinColumn(name="ServiceDeliveryLocation_id") })
//	@ForeignKey(name="FK_PrcScrty_srvcDlvryLcatns", inverseName="FK_SrvcDlvryLcatn_PrcScurty")
//	public List<ServiceDeliveryLocation> getServiceDeliveryLocations() {
//		return serviceDeliveryLocations;
//	}
//
//	public void setServiceDeliveryLocations(List<ServiceDeliveryLocation>list){
//		serviceDeliveryLocations = list;
//	}
//	
//	public void addServiceDeliveryLocations (ServiceDeliveryLocation enabledServiceDeliveryLocations) {
//		// create the association set if it doesn't exist already
//		if (this.serviceDeliveryLocations == null) {
//			this.serviceDeliveryLocations = new ArrayList<ServiceDeliveryLocation>();
//		}
//		// add the association to the association set
//		if(!this.serviceDeliveryLocations.contains(enabledServiceDeliveryLocations)) {
//			this.serviceDeliveryLocations.add(enabledServiceDeliveryLocations);
//		}
//	}
//
//	public void removeServiceDeliveryLocations (ServiceDeliveryLocation enabledServiceDeliveryLocations) {
//		// if it doesn't exist already return
//		if (this.serviceDeliveryLocations == null) {
//			return;
//		}
//		// remove the association to the association set
//		if(this.serviceDeliveryLocations.contains(enabledServiceDeliveryLocations)) {
//			this.serviceDeliveryLocations.remove(enabledServiceDeliveryLocations);
//		}
//	}
	
	/**
	*  Path of the process
	*/
	private String path;

	@Column(name="path")
	public String getPath(){
		return path;
	}

	public void setPath(String path){
		this.path = path;
	}
	
	/**
	*  Parameters associated with this process
	*/
	private List<CodeValueParameter> parameter;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="proc_security_parameter", joinColumns = { @JoinColumn(name="proc_security_id") }, inverseJoinColumns = { @JoinColumn(name="parameter_id") })
	@ForeignKey(name="FK_parameter_ProcSecurity", inverseName="FK_ProcSecurity_parameter")
	//@IndexColumn(name="list_index")
	public List<CodeValueParameter> getParameter(){
		return parameter;
	}

	public void setParameter(List<CodeValueParameter> parameter){
		this.parameter = parameter;
	}

	//methods needed for BaseEntity extension
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ProcSecurity_sequence")
	@SequenceGenerator(name = "ProcSecurity_sequence", sequenceName = "ProcSecurity_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	@Override
	public String toString() {
		return "ProcSecurity [path=" + path + ", procSecurityRole=" + procSecurityRole + "]";
	}
	
	

}
