package com.phi.entities.role;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.Target;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.phi.cs.catalog.adapter.nestedset.NestedSetNode;
import com.phi.cs.catalog.adapter.nestedset.NestedSetNodeInfo;
import com.phi.cs.catalog.adapter.nestedset.ServiceDeliveryLocationNestedSetNodeInfo;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.SdlConf;
import com.phi.entities.baseEntity.TimeBand;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueNanda;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.II;
import com.phi.entities.dataTypes.II4ServiceDeliveryLocation;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.entity.Organization;
import com.phi.entities.locatedEntity.LocatedEntity;

@Entity
@Table(name = "service_delivery_location")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class ServiceDeliveryLocation extends BaseEntity implements NestedSetNode<ServiceDeliveryLocation>, LocatedEntity {

	private static final long serialVersionUID = 6892599915773382137L;

	//methods needed for BaseEntity extension
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ServiceDeliveryLocation_sequence")
	@SequenceGenerator(name = "ServiceDeliveryLocation_sequence", sequenceName = "ServiceDeliveryLoc_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	protected ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serviceDeliveryLocation")
	@ForeignKey(name = "FK_service_deliver_serviceDeli")
	@Index(name = "IX_service_deliver_serviceDeli")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
	/**
	*  javadoc for logo
	*/
	private byte[] logo;

	@Lob
	@Column(name="logo")
	public byte[] getLogo(){
		return logo;
	}

	public void setLogo(byte[] logo){
		this.logo = logo;
	}
	
	/**
	*  javadoc for header
	*/
	private String header;

	@Column(name="header", length=4000)
	public String getHeader(){
		return header;
	}

	public void setHeader(String header){
		this.header = header;
	}

	/**
	*  javadoc for footer
	*/
	private String footer;

	@Column(name="footer", length=4000)
	public String getFooter(){
		return footer;
	}

	public void setFooter(String footer){
		this.footer = footer;
	}

	/**
	*  javadoc for color
	*/
	private Integer color;

	@Column(name="color")
	public Integer getColor(){
		return color;
	}

	public void setColor(Integer color){
		this.color = color;
	}
	
	private ServiceDeliveryLocation parent;
	private List<ServiceDeliveryLocation> children;

	//private List<ProcedureDefinition> procedureDefinition;
	private Employee responsible;
	private Organization organization;

//	/**
//	 * new param to add favorite diagnoses
//	 * */
//	private List<CodeValueIcd9> favoriteICD9Diag;
//	//
//	@ManyToMany(fetch = FetchType.LAZY,targetEntity=CodeValueIcd9.class)
//	@JoinTable(name = "sdl_favorICD9Diag", joinColumns = { @JoinColumn(name = "sdl_id") }, inverseJoinColumns = { @JoinColumn(name = "FavorICD9Diag") })
//	@ForeignKey(name = "FK_favorICD9Diag_sdl", inverseName = "FK_sdl_favorICD9Diag")
//	public  List<CodeValueIcd9> getFavoriteICD9Diag() {
//
//
//		return favoriteICD9Diag;
//	}
//
//	public void setFavoriteICD9Diag(List<CodeValueIcd9> favoriteICD9Diag) {
//		this.favoriteICD9Diag = favoriteICD9Diag;
//	}
//
//	public void addFavoriteICD9Diag(CodeValueIcd9 cv) {
//
//		if (favoriteICD9Diag== null) {
//			favoriteICD9Diag = new ArrayList<CodeValueIcd9>();
//		} 
//		if (!favoriteICD9Diag.contains(cv)) {
//			favoriteICD9Diag.add(cv);
//		}
//	}
	
	
	
	/**
	*  javadoc for listScalesType
	*/
	private List<CodeValuePhi> listScalesType;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="Sdloc_listScalesType", joinColumns = { @JoinColumn(name="Sdloc_id") }, inverseJoinColumns = { @JoinColumn(name="listScalesType") })
	@ForeignKey(name="FK_listScalesType_Sdloc", inverseName="FK_Sdloc_listScalesType")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getListScalesType(){
		return listScalesType;
	}

	public void setListScalesType(List<CodeValuePhi> listScalesType){
		this.listScalesType = listScalesType;
	}
	
	
	/**
	*  javadoc for configuration
	*/
	private SdlConf configuration;

	//@JsonManagedReference(value="configuredSdl")
	@JsonIgnore
	@OneToOne(fetch=FetchType.LAZY, mappedBy="configuredSDL", cascade=CascadeType.PERSIST)
	public SdlConf getConfiguration(){
		return configuration;
	}

	public void setConfiguration(SdlConf configuration){
		this.configuration = configuration;
	}

	
	
	
	/*@ManyToMany(mappedBy = "procedureSDL", cascade=CascadeType.PERSIST)
	public List<ProcedureDefinition> getProcedureDefinition() {
		return procedureDefinition;
	}

	public void setProcedureDefinition(List<ProcedureDefinition> procedureDefinition) {
		this.procedureDefinition = procedureDefinition;
	}

	public void addProcedureDefinition(ProcedureDefinition procedureDefinition) {
		if (this.procedureDefinition == null) {
			this.procedureDefinition = new ArrayList<ProcedureDefinition>();
		}
		// add the association
		if(!this.procedureDefinition.contains(procedureDefinition)) {
			this.procedureDefinition.add(procedureDefinition);
			procedureDefinition.addProcedureSDL(this);
		}
	}
	
	public void removeProcedureDefinition(ProcedureDefinition procedureDefinition) {
		
		if(this.procedureDefinition.contains(procedureDefinition)){
			this.procedureDefinition.remove(procedureDefinition);
			//make the inverse link
			procedureDefinition.getProcedureSDL().remove(this);
		}
	}*/
	

	public ServiceDeliveryLocation() {
		nodeInfo = new ServiceDeliveryLocationNestedSetNodeInfo(this);
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Parent_SDL")
	@ForeignKey(name = "FK_SDL_to_Parent_SDL")
	@Index(name="IX_SDL_to_Parent_SDL")
	public ServiceDeliveryLocation getParent() {
		return parent;
	}

	public void setParent(ServiceDeliveryLocation parent) {
		this.parent = parent;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Responsible")
	@ForeignKey(name = "FK_SDL_to_Employee")
	@Index(name="IX_SDL_to_Employee")
	public Employee getResponsible() {
		return responsible;
	}

	public void setResponsible(Employee employee) {
		this.responsible = employee;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Organization")
	@ForeignKey(name = "FK_SDL_to_Organization")
	@Index(name="IX_SDL_to_Organization")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "parent")
	public List<ServiceDeliveryLocation> getChildren() {
		return children;
	}

	public void setChildren(List<ServiceDeliveryLocation> children) {
		this.children = children;
	}

	public void addChildren(ServiceDeliveryLocation serviceDeliveryLocation) {
		if (this.children == null) {
			this.children = new ArrayList<ServiceDeliveryLocation>();
		}
		// add the association
		if (!this.children.contains(serviceDeliveryLocation)) {
			this.children.add(serviceDeliveryLocation);
			// make the inverse link
			serviceDeliveryLocation.setParent(this);
		}
	}

		// CUSTOM PROPERTIES
	private CodeValuePhi area;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "area")
	@ForeignKey(name = "FK_SDL_Area")
	@Index(name="IX_SDL_Area")
	public CodeValuePhi getArea() {
		return area;
	}

	public void setArea(CodeValuePhi area) {
		this.area = area;
	}

	private CodeValuePhi branch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "branch")
	@ForeignKey(name = "FK_SDL_Branch")
	@Index(name="IX_SDL_Branch")
	public CodeValuePhi getBranch() {
		return branch;
	}

	public void setBranch(CodeValuePhi branch) {
		this.branch = branch;
	}

	private CodeValuePhi discipline;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "discipline")
	@ForeignKey(name = "FK_SDL_Discipline")
	public CodeValuePhi getDiscipline() {
		return discipline;
	}

	public void setDiscipline(CodeValuePhi discipline) {
		this.discipline = discipline;
	}
	
	private CodeValuePhi specialization;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "specialization")
	@ForeignKey(name = "FK_SDL_Specialization")
	@Index(name="IX_SDL_Specialization")
	public CodeValuePhi getSpecialization() {
		return specialization;
	}

	public void setSpecialization(CodeValuePhi specialization) {
		this.specialization = specialization;
	}

	/**
	 *  G2 STRT0 is used to save the STRT0 of LISA IM DB
	 */
	private String g2Strt0;

	@Column(name="g2_strt0")
	public String getG2Strt0(){
		return g2Strt0;
	}

	public void setG2Strt0(String g2Strt0){
		this.g2Strt0 = g2Strt0;
	}
	
	private List<CodeValue> internalCostCenter;


	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST,targetEntity=CodeValuePhi.class)
	@JoinTable(name = "sdl_internal_cost_center", joinColumns = { @JoinColumn(name = "ServiceDeliveryLocation_id") }, inverseJoinColumns = { @JoinColumn(name = "internal_cost_center") })
	@ForeignKey(name = "FK_sdl_intCostCen_sdl", inverseName = "FK_sdl_intCostCen_cv")
	public List<CodeValue> getInternalCostCenter() {
		return internalCostCenter;
	}

	public void setInternalCostCenter(List<CodeValue> internalCostCenter) {
		this.internalCostCenter = internalCostCenter;
	}

	private List<CodeValue> externalCostCenter;


	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST,targetEntity=CodeValuePhi.class)
	@JoinTable(name = "sdl_external_cost_center", joinColumns = { @JoinColumn(name = "ServiceDeliveryLocation_id") }, inverseJoinColumns = { @JoinColumn(name = "external_cost_center") })
	@ForeignKey(name = "FK_sdl_extCostCen_sdl", inverseName = "FK_sdl_extCostCen_cv")
	public List<CodeValue> getExternalCostCenter() {
		return externalCostCenter;
	}

	public void setExternalCostCenter(List<CodeValue> externalCostCenter) {
		this.externalCostCenter = externalCostCenter;
	}

	// private CodeValue encounterCode;
	//
	// @ManyToOne(fetch=FetchType.LAZY)
	// @JoinColumn(name="encounter_code")
	// @ForeignKey(name="FK_SDL_encounter_code")
	// public CodeValue getEncounterCode() {
	// return encounterCode;
	// }
	// public void setEncounterCode(CodeValue encounterCode) {
	// this.encounterCode = encounterCode;
	// }

	private List<CodeValue> encounterCode; // WE NEED TO USE LIST OR CONVERTER
	// DOESN'T WORK!!!

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
	@JoinTable(name = "sdl_encounter_code", joinColumns = { @JoinColumn(name = "ServiceDeliveryLocation_id") }, inverseJoinColumns = { @JoinColumn(name = "encounter_code") })
	@ForeignKey(name = "FK_sdl_encounterCode_sdl", inverseName = "FK_sdl_encounterCode_cv")
	/*
	 * Lists need an OrderBy annotation, as stated at
	 * http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/
	 * Section: 2.2.5.3.4. Indexed collections (List, Map)
	 * 
	 * "id" is the CodeValue's id
	 */
	//	@OrderBy("id")
	// @IndexColumn(name="listIndex") //Not JPA
	public List<CodeValue> getEncounterCode() {
		return encounterCode;
	}

	public void setEncounterCode(List<CodeValue> encounterCode) {
		this.encounterCode = encounterCode;
	}

	private List<CodeValue> priorityCode;
	private List<CodeValue> admissionCode;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
	@JoinTable(name = "sdl_priority_code", joinColumns = { @JoinColumn(name = "ServiceDeliveryLocation_id") }, inverseJoinColumns = { @JoinColumn(name = "priority_code") })
	@ForeignKey(name = "FK_sdl_priorityCode_sdl", inverseName = "FK_sdl_priorityCode_cv")
	public List<CodeValue> getPriorityCode() {
		return priorityCode;
	}

	public void setPriorityCode(List<CodeValue> priorityCode) {
		this.priorityCode = priorityCode;
	}

	public void addPriorityCode (CodeValue cv) {
		// create the association set if it doesn't exist already
		if (this.priorityCode == null) {
			this.priorityCode = new ArrayList<CodeValue>();
		}
		// add the association to the association set
		if(!this.priorityCode.contains(cv)) {
			this.priorityCode.add(cv);
		}
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
	@JoinTable(name = "sdl_admission_code", joinColumns = { @JoinColumn(name = "ServiceDeliveryLocation_id") }, inverseJoinColumns = { @JoinColumn(name = "admission_code") })
	@ForeignKey(name = "FK_sdl_admissionCode_sdl", inverseName = "FK_sdl_admissionCode_cv")
	//	@OrderBy("id")
	public List<CodeValue> getAdmissionCode() {
		return admissionCode;
	}

	public void setAdmissionCode(List<CodeValue> admissionCode) {
		this.admissionCode = admissionCode;
	}



	private List<CodeValue> reasonCode;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
	@JoinTable(name="sdl_reason_code", joinColumns = { @JoinColumn(name="ServiceDeliveryLocation_id") }, inverseJoinColumns = { @JoinColumn(name="reason_code") })
	@ForeignKey(name="FK_reasonCode_sdl", inverseName="FK_reasonCode_cv_sdl")
	//	@OrderBy("id")
	public List<CodeValue> getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(List<CodeValue> reasonCode) {
		this.reasonCode = reasonCode;
	}

	public void addReasonCode (CodeValue cv) {
		// create the association set if it doesn't exist already
		if (this.reasonCode == null) {
			this.reasonCode = new ArrayList<CodeValue>();
		}
		// add the association to the association set
		if(!this.reasonCode.contains(cv)) {
			this.reasonCode.add(cv);
		}
	}
	
	/**
	 *  Properties to know if a serviceDeliveryLocation allows or not a hybrid management. 
	 *  If true, both inpatient (IMP, DH, etc.) and outpatient (AMB) management 
	 */
	private Boolean hybridManagementSupported = false;

	@Column(name="hybrid_management_supported")
	public Boolean getHybridManagementSupported(){
		return hybridManagementSupported;
	}

	public void setHybridManagementSupported(Boolean hybridManagementSupported){
		this.hybridManagementSupported = hybridManagementSupported;
	}
	
	/**
	 *  properties to know if the serviceDeliveryLocation
	 *  allows or not the creation and the receiving of 
	 *  internal Activity requests 
	 */
	private Boolean intActSupported;

	@Column(name="int_act_supported")
	public Boolean getIntActSupported(){
		return intActSupported;
	}

	public void setIntActSupported(Boolean intActSupported){
		this.intActSupported = intActSupported;
	}

	private Boolean waitingListSupported;
	private Boolean slotSupported;
	private Boolean mandatoryClassification;

	@Column(name="waitingListSupported")
	public Boolean getWaitingListSupported() {
		return waitingListSupported;
	}

	public void setWaitingListSupported(Boolean waitingListSupported) {
		this.waitingListSupported = waitingListSupported;
	}
	@Column(name="slotSupported")
	public Boolean getSlotSupported() {
		return slotSupported;
	}

	public void setSlotSupported(Boolean slotSupported) {
		this.slotSupported = slotSupported;
	}

	
	@Column(name="mandatoryClassification")
	public Boolean getMandatoryClassification() {
		return mandatoryClassification;
	}

	public void setMandatoryClassification(Boolean mandatoryClassification) {
		this.mandatoryClassification = mandatoryClassification;
	}


	/**
	 * This code is used to assign a code representing a combination 
	 * of type of report which the SDL can execute.
	 */
	private CodeValue reportTypeCombo;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name = "reportTypeCombo")
	@ForeignKey(name = "FK_SDL_reportType")
	@Index(name="IX_SDL_reportType")
	public CodeValue getReportTypeCombo() {
		return reportTypeCombo;
	}

	public void setReportTypeCombo(CodeValue reportTypeCombo){
		this.reportTypeCombo = reportTypeCombo;
	}

	/**
	 * Collection of CodeValue representing the list of examination type
	 * allowed to be executed in this ServiceDeliveryLocation.
	 */
	private List<CodeValue> examinationType;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
	@JoinTable(name = "sdl_examination_type", joinColumns = { @JoinColumn(name = "ServiceDeliveryLocation_id") }, inverseJoinColumns = { @JoinColumn(name = "examination_type") })
	@ForeignKey(name = "FK_sdl_examination_type_sdl", inverseName = "FK_sdl_examination_type_cv")
	public List<CodeValue> getExaminationType() {
		return examinationType;
	}

	public void setExaminationType(List<CodeValue> examinationType) {
		this.examinationType = examinationType;
	}

	public void addExaminationType (CodeValue cv) {
		// create the association set if it doesn't exist already
		if (this.examinationType == null) {
			this.examinationType = new ArrayList<CodeValue>();
		}
		// add the association to the association set
		if(!this.examinationType.contains(cv)) {
			this.examinationType.add(cv);
		}
	}


	/* 
	 * Used for Ambulatories to set their default about the creation of notes, reports and so on...
	 * I.E., if this property is TRUE, then the Diary Notes are private by default, even if
	 * an employee can choose to set them NOT private.
	 */
	private Boolean isPrivacyDefault;	

	@Column(name="isPrivacyDefault")
	public Boolean getIsPrivacyDefault() {
		return isPrivacyDefault;
	}

	public void setIsPrivacyDefault(Boolean isPrivacyDefault) {
		this.isPrivacyDefault = isPrivacyDefault;
	}



	//Nested set
	private NestedSetNodeInfo<ServiceDeliveryLocation> nodeInfo;

	@JsonManagedReference
	@Override
	@Embedded
	@Target(ServiceDeliveryLocationNestedSetNodeInfo.class)
	public NestedSetNodeInfo<ServiceDeliveryLocation> getNodeInfo() {
		return nodeInfo;
	}

	public void setNodeInfo(NestedSetNodeInfo<ServiceDeliveryLocation> nodeInfo) {
		this.nodeInfo = nodeInfo;
	}

	@Override
	@Transient
	@JsonIgnore
	public NestedSetNodeInfo<ServiceDeliveryLocation> getParentNodeInfo() {
		if (parent == null) {
			return null;
		} else {
			return parent.getNodeInfo();
		}
	}

	@Override
	@Transient
	@JsonIgnore
	public String[] getPropertiesForGroupingInQueries() {
		return 	new String[] {"createdBy", "creationDate", "isActive", "version", "addr", "certificateText", "classCode", "code", 
				"effectiveTime", "name", "negationInd", "player.internalId", "scoper.internalId", "quantity", "serviceDeliveryLocation.internalId",
				"statusCode", "telecom", "area", "branch", "mandatoryClassification", "organization", "parent", "responsible", "organization","waitingListSupported"};
	}

	@Override
	@Transient
	@JsonIgnore
	public String[] getLazyPropertiesForGroupingInQueries() {
		return 	new String[] { "name"};
	}

	/**
	 * From PHI CI
	 */




	private List<CodeValue> favoritedDiagnoses;
	//
	@ManyToMany(fetch = FetchType.LAZY,targetEntity=CodeValueNanda.class)
	@JoinTable(name = "sdl_favorDiag", joinColumns = { @JoinColumn(name = "sdl_id") }, inverseJoinColumns = { @JoinColumn(name = "FavorDiag") })
	@ForeignKey(name = "FK_favorDiag_sdl", inverseName = "FK_sdl_favorDiag")
	public  List<CodeValue> getFavoritedDiagnoses() {


		return favoritedDiagnoses;
	}

	public void setFavoritedDiagnoses(List<CodeValue> favoritedDiagnoses) {
		this.favoritedDiagnoses = favoritedDiagnoses;
	}

	public void addFavoritedDiagnoses(CodeValue cv) {

		if (favoritedDiagnoses== null) {
			favoritedDiagnoses = new ArrayList<CodeValue>();
		} 
		if (!favoritedDiagnoses.contains(cv)) {
			favoritedDiagnoses.add(cv);
		}
	}
	private List<CodeValue> favoriteLep;
	//
	@ManyToMany(fetch = FetchType.LAZY,targetEntity=CodeValueNanda.class)
	@JoinTable(name = "sdl_favorLep", joinColumns = { @JoinColumn(name = "sdl_id") }, inverseJoinColumns = { @JoinColumn(name = "FavorLep") })
	@ForeignKey(name = "FK_favorLep_sdl", inverseName = "FK_sdl_favorLep")
	public  List<CodeValue> getFavoriteLep() {
		return favoriteLep;
	}

	public void setFavoriteLep(List<CodeValue> favoriteLep) {
		this.favoriteLep = favoriteLep;
	}

	public void addFavoriteLep(CodeValue cv) {

		if (favoriteLep== null) {
			favoriteLep = new ArrayList<CodeValue>();
		} 
		if (!favoriteLep.contains(cv)) {
			favoriteLep.add(cv);
		}
	}

	//From Role
	
	private AD addr;

	@Embedded
	public AD getAddr() {
		return addr;
	}

	public void setAddr(AD addr) {
		this.addr = addr;
	}
	
	private IVL<Date> effectiveTime;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="low", column=@Column(name="effective_time_low")),
	       @AttributeOverride(name="high", column=@Column(name="effective_time_high")),
			@AttributeOverride(name="lowClosed", column=@Column(name="effective_time_lowClosed")),
			@AttributeOverride(name="highClosed", column=@Column(name="effective_time_highClosed"))
	})
	public IVL<Date> getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(IVL<Date> effectiveTime) {
		this.effectiveTime = effectiveTime;
	}
	
	private CodeValue classCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="class_code")
	@ForeignKey(name="FK_SDL_classCode")
	@Index(name="IX_SDL_classCode")
	public CodeValue getClassCode() {
		return classCode;
	}

	public void setClassCode(CodeValue classCode) {
		this.classCode = classCode;
	}
	
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_SDL_code")
	@Index(name="IX_SDL_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	private List<CodeValue> confidentialityCode;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST, targetEntity=CodeValuePhi.class)
    @JoinTable(name="sdl_confidentiality_code", joinColumns = { @JoinColumn(name="role_id") }, inverseJoinColumns = { @JoinColumn(name="confidentiality_code") })
	@ForeignKey(name="FK_sdl_confidentia_role_id", inverseName="FK_SDL_confidentialityCode_cv")
//	@OrderBy("id")
	public List<CodeValue> getConfidentialityCode() {
		return confidentialityCode;
	}

	public void setConfidentialityCode(List<CodeValue> confidentialityCode) {
		this.confidentialityCode = confidentialityCode;
	}
	
	public void addConfidentialityCode (CodeValue cv) {
		// create the association set if it doesn't exist already
		if (this.confidentialityCode == null) {
			this.confidentialityCode = new ArrayList<CodeValue>();
		}
		// add the association to the association set
		if(!this.confidentialityCode.contains(cv)) {
			this.confidentialityCode.add(cv);
		}
	}
	
	
	private Set<II> id;

	@OneToMany(targetEntity=II4ServiceDeliveryLocation.class ,fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="sdloc_id")
	@ForeignKey(name="FK_SDL_id")
	@Index(name="IX_SDL_id")
	@AuditJoinTable(name="z_sdloc_id_jt")
	public Set<II> getId() {
		return id;
	}

	public void setId(Set<II> id) {
		this.id = id;
	}

	public II getId(String root) {
		if (getId() != null) {
			for (II ii : getId()) {
				if (ii.getRoot().toString().equals(root))
					return ii;
			}
		}
		return null;
	}

	public void addId(II id) {
		if (id == null)
			return;
		
		if (this.id == null) {
			this.id = new HashSet<II>();
		}
		
		this.id.add(id);
	}
	
	private EN name;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="fam", column=@Column(name="name_fam")),
	       @AttributeOverride(name="giv", column=@Column(name="name_giv")),
	       @AttributeOverride(name="pfx", column=@Column(name="name_pfx")),
	       @AttributeOverride(name="sfx", column=@Column(name="name_sfx")),
	       @AttributeOverride(name="del", column=@Column(name="name_del")),
	       @AttributeOverride(name="formatted", column=@Column(name="name_formatted"))
	})
	public EN getName() {
		return name;
	}

	public void setName(EN name) {
		this.name = name;
	}

	private TEL telecom;

	@Embedded
	@AttributeOverrides({
	       @AttributeOverride(name="as", column=@Column(name="telecom_as")),
	       @AttributeOverride(name="bad", column=@Column(name="telecom_bad")),
	       @AttributeOverride(name="dir", column=@Column(name="telecom_dir")),
	       @AttributeOverride(name="ec", column=@Column(name="telecom_ec")),
	       @AttributeOverride(name="h", column=@Column(name="telecom_h")),
	       @AttributeOverride(name="hp", column=@Column(name="telecom_hp")),
	       @AttributeOverride(name="hv", column=@Column(name="telecom_hv")),
	       @AttributeOverride(name="mail", column=@Column(name="telecom_mail")),
	       @AttributeOverride(name="mc", column=@Column(name="telecom_mc")),
	       @AttributeOverride(name="pg", column=@Column(name="telecom_pg")),
	       @AttributeOverride(name="pub", column=@Column(name="telecom_pub")),
	       @AttributeOverride(name="sip", column=@Column(name="telecom_sip")),
	       @AttributeOverride(name="tmp", column=@Column(name="telecom_tmp")),
	       @AttributeOverride(name="wp", column=@Column(name="telecom_wp"))
	})
	public TEL getTelecom() {
		return telecom;
	}

	public void setTelecom(TEL telecom) {
		this.telecom = telecom;
	}
	
	/**
	*  javadoc for timeBands
	*/
	private List<TimeBand> timeBands;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="serviceDeliveryLocation", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
	@Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	public List<TimeBand> getTimeBands(){
		return timeBands;
	}

	public void setTimeBands(List<TimeBand> list){
		timeBands = list;
	}

	public void addTimeBands(TimeBand timeBands) {
		if (this.timeBands == null) {
			this.timeBands = new ArrayList<TimeBand>();
		}
		// add the association
		if(!this.timeBands.contains(timeBands)) {
			this.timeBands.add(timeBands);
			// make the inverse link
			timeBands.setServiceDeliveryLocation(this);
		}
	}

	public void removeTimeBands(TimeBand timeBands) {
		if (this.timeBands == null) {
			this.timeBands = new ArrayList<TimeBand>();
			return;
		}
		//add the association
		if(this.timeBands.contains(timeBands)){
			this.timeBands.remove(timeBands);
			//make the inverse link
			timeBands.setServiceDeliveryLocation(null);
		}

	}
	
	@Override
	public String toString() {
		if (name != null && name.getGiv() != null) {
			return name.getGiv();
		} else {
			return super.toString();
		}
	}
}
