package com.phi.entities.role;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AssociationOverride;
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
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.ED;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.II;
import com.phi.entities.dataTypes.II4Employee;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.entity.Organization;
import com.phi.parameters.ParameterManager;

import org.hibernate.annotations.IndexColumn;
@javax.persistence.Entity
@Table(name = "employee")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Employee extends BaseEntity {

	private static final long serialVersionUID = -1025706457633884896L;

	/**
	*  javadoc for restrictSdl
	*/
	private Boolean restrictSdl;

	@Column(name="restrict_sdl")
	public Boolean getRestrictSdl(){
		return restrictSdl;
	}

	public void setRestrictSdl(Boolean restrictSdl){
		this.restrictSdl = restrictSdl;
	}
	
	private static final Logger log = Logger.getLogger(Employee.class);

	/**
	*  javadoc for enabledCodes
	*/
	private List<CodeValuePhi> enabledCodes;

	@ManyToMany(fetch=FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name="Employee_enabledCodes", joinColumns = { @JoinColumn(name="Employee_id") }, inverseJoinColumns = { @JoinColumn(name="enabledCodes") })
	@ForeignKey(name="FK_enabledCodes_Employee", inverseName="FK_Employee_enabledCodes")
	@IndexColumn(name="list_index")
	public List<CodeValuePhi> getEnabledCodes(){
		return enabledCodes;
	}

	public void setEnabledCodes(List<CodeValuePhi> enabledCodes){
		this.enabledCodes = enabledCodes;
	}
	
	/**
	*  javadoc for signUser
	*/
	private String signUser;

	@Column(name="sign_user")
	public String getSignUser(){
		return signUser;
	}

	public void setSignUser(String signUser){
		this.signUser = signUser;
	}

	/**
	*  javadoc for signPassword
	*/
	private String signPassword;

	@Column(name="sign_password")
	public String getSignPassword(){
		return signPassword;
	}

	public void setSignPassword(String signPassword){ 
		
		this.signPassword = signPassword;
	}
	
	private String signClearPassword;
	
	@Transient
	public String getSignClearPassword(){
		return signClearPassword;
	}
	
	public void setSignClearPassword(String signClearPassword){
		this.signClearPassword = signClearPassword;
	}
	
	
		
	@PostLoad
	public void onPostLoad() {
		
		if (signPassword == null || signPassword.isEmpty()) {
			signClearPassword = signPassword;
			return;
		}
		
		try {
			String key = (String)ParameterManager.instance().getParameter("p.employees.globalsignsecret", "value");
			if (key == null || key.isEmpty()) {
				key = "Sign4tur3secretZ"; 
			}

			// Create key and cipher
			Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			
			// decrypt the signPassword
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
			
			//get signPassword sttring byte array, and decode from base64
			//encryptedM16 must have lenght multiple of 16 byte.
			Base64 codec = new Base64();
			byte[] encryptedM16 = codec.decode(signPassword.getBytes());
			
			String decrypted = new String(cipher.doFinal(encryptedM16));
			signClearPassword =  decrypted;
		}
		catch(Exception e) {
			log.error("error decrypting sign password "+signPassword +" for user "+getUsername());
			e.printStackTrace();
		}
		
	}
	
	
//	Alias form digital signature
	private String alias;
	
	@Column(name="alias")
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	//methods needed for BaseEntity extension
	private CodeValue code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_Employee_code")
	@Index(name="IX_Employee_code")
	public CodeValue getCode() {
		return code;
	}

	public void setCode(CodeValue code) {
		this.code = code;
	}
	
	/**
	*  javadoc for note
	*/
	private String note;

	@Column(name="note" , length=2000)
	public String getNote(){
		return note;
	}

	public void setNote(String note){
		this.note = note;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Employee_sequence")
	@SequenceGenerator(name = "Employee_sequence", sequenceName = "Employee_sequence")
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
	@ForeignKey(name = "FK_Employee_sdloc")
	@Index(name = "IX_Employee_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}
	
	@Override
	public void setServiceDeliveryLocation(
	ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/

	private CodeValue locationCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="location_code")
	@ForeignKey(name="FK_Employee_loc")
	@Index(name="IX_Employee_loc")
	public CodeValue getLocationCode() {
		return locationCode;
	}

	public void setLocationCode(CodeValue locationCode) {
		this.locationCode = locationCode;
	}
	
	private CodeValue jobCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="job_code")
	@ForeignKey(name="FK_Employee_jobCode")
	@Index(name="IX_Employee_jobCode")
	public CodeValue getJobCode() {
		return jobCode;
	}

	public void setJobCode(CodeValue jobCode) {
		this.jobCode = jobCode;
	}

//	private SC jobTitleName;
//
//	@Embedded
//	@AttributeOverride(name="data", column = @Column(name="jobTitleName_data"))
//	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="jobTitleName_code"))
//	//FIXME @ForeignKey(name="FK_Employee_jobTitleName")
//	public SC getJobTitleName() {
//		return jobTitleName;
//	}
//
//	public void setJobTitleName(SC jobTitleName) {
//		this.jobTitleName = jobTitleName;
//	}

	private CodeValue jobClassCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="job_class_code")
	@ForeignKey(name="FK_Employee_jobClassCode")
	@Index(name="IX_Employee_jobClassCode")
	public CodeValue getJobClassCode() {
		return jobClassCode;
	}

	public void setJobClassCode(CodeValue jobClassCode) {
		this.jobClassCode = jobClassCode;
	}

	private CodeValue occupationCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="occupation_code")
	@ForeignKey(name="FK_Employee_occupationCode")
	@Index(name="IX_Employee_occupationCode")
	public CodeValue getOccupationCode() {
		return occupationCode;
	}

	public void setOccupationCode(CodeValue occupationCode) {
		this.occupationCode = occupationCode;
	}

	private CodeValue salaryTypeCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="salary_type_code")
	@ForeignKey(name="FK_Employee_salaryTypeCode")
	@Index(name="IX_Employee_salaryTypeCode")
	public CodeValue getSalaryTypeCode() {
		return salaryTypeCode;
	}

	public void setSalaryTypeCode(CodeValue salaryTypeCode) {
		this.salaryTypeCode = salaryTypeCode;
	}

//	private MO salaryQuantity;
//
//	@Embedded
//	@AttributeOverride(name="value", column = @Column(name="salaryQuantity_value"))
//	@AssociationOverride(name="currency", joinColumns = @JoinColumn(name="salaryQuantity_currency"))
//	//FIXME @ForeignKey(name="FK_Employee_salaryQuantity_currency")
//	public MO getSalaryQuantity() {
//		return salaryQuantity;
//	}
//
//	public void setSalaryQuantity(MO salaryQuantity) {
//		this.salaryQuantity = salaryQuantity;
//	}

	private ED hazardExposureText;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="hazardExposureText_mediaType"))
	//FIXME	@ForeignKey(name="FK_Employee_hazardExposureText_mediaType")
	@AttributeOverrides({
	       @AttributeOverride(name="string", column = @Column(name="hazardExposureText_string")),
	       @AttributeOverride(name="bytes", column = @Column(name="hazardExposureText_bytes"))
	})
	public ED getHazardExposureText() {
		return hazardExposureText;
	}

	public void setHazardExposureText(ED hazardExposureText) {
		this.hazardExposureText = hazardExposureText;
	}

	private ED protectiveEquipmentText;

	@Embedded
	@AssociationOverride(name="mediaType", joinColumns = @JoinColumn(name="protectiveEquipmentText_mediaT")) //FIXME troncato a 30 x oracle
	//FIXME	@ForeignKey(name="FK_Employee_protectiveEquipmentText_mediaType")
	@AttributeOverrides({
	       @AttributeOverride(name="string", column = @Column(name="protectiveEquipmentText_string")),
	       @AttributeOverride(name="bytes", column = @Column(name="protectiveEquipmentText_bytes"))
	})
	public ED getProtectiveEquipmentText() {
		return protectiveEquipmentText;
	}

	public void setProtectiveEquipmentText(ED protectiveEquipmentText) {
		this.protectiveEquipmentText = protectiveEquipmentText;
	}
	

	// CUSTOM PROPERTIES
	private String username;

	@Column(name="username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	private String password;
	
	@Column(name="password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	private Date lastChangedPassword;
	
	@Column(name="last_changed_password")
	public Date getLastChangedPassword() {
		return lastChangedPassword;
	}

	public void setLastChangedPassword(Date lastChangedPassword) {
		this.lastChangedPassword = lastChangedPassword;
	}
	
	/**
	*  Current sessionId
	*  If null user is not currently logged id.
	*/
	private String sessionId;

	@Column(name="session_id")
	public String getSessionId(){
		return sessionId;
	}

	public void setSessionId(String sessionId){
		this.sessionId = sessionId;
	}
	
	private Date lastAccessDate;
	
	@Column(name="last_access_date")
	public Date getLastAccessDate() {
		return lastAccessDate;
	}

	public void setLastAccessDate(Date lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}
	
	private Integer loginCount;
	
	@Column(name="login_count")
	public Integer getLoginCount(){
		return loginCount;
	}
	
	public void setLoginCount(Integer loginCount){
		this.loginCount=loginCount;
	}
	
	/**
	*  Current host where employee is logged.
	*  If null user is not currently logged id.
	*/
	private String host;

	@Column(name="host")
	public String getHost(){
		return host;
	}

	public void setHost(String host){
		this.host = host;
	}
	
	/**
	*  externalId used by integrations
	*  store here id of an external system
	*/
	private String externalId;

	@Column(name="external_id", unique=true)
	public String getExternalId(){
		return externalId;
	}

	public void setExternalId(String externalId){
		this.externalId = externalId;
	}
	
	/**

	 *  additionalId used by integrations when externalId has been already used
	 *  store here id of an external system
	 */
	private String additionalId;

	@Column(name="additional_id")//, unique=true)
	@Index(name="IX_Emp_addId")
	public String getAdditionalId(){
		return additionalId;
	}

	public void setAdditionalId(String additionalId){
		this.additionalId = additionalId;
	}
	
	/**
	*  Gender Code
	*/
	private CodeValuePhi genderCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="gender_code")
	@ForeignKey(name="FK_Employee_genderCode")
	@Index(name="IX_Employee_genderCode")
	public CodeValuePhi getGenderCode(){
		return genderCode;
	}

	public void setGenderCode(CodeValuePhi genderCode){
		this.genderCode = genderCode;
	}
	
	/**
	*  Fiscal Code
	*/
	private String fiscalCode;

	@Column(name="fiscal_code")
	public String getFiscalCode() {
		return fiscalCode;
	}

	public void setFiscalCode(String username) {
		this.fiscalCode = username;
	}
	
	/**
	*  BirthTime
	*/
	private Date birthTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="birth_time")
	public Date getBirthTime() {
		return birthTime;
	}

	public void setBirthTime(Date birthTime) {
		this.birthTime = birthTime;
	}
	
	/**
	*  BirthPlace
	*/
	private AD birthPlace;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="birthPlace_code"))
	@AttributeOverrides({
	       @AttributeOverride(name="adl", column=@Column(name="birthPlace_adl")),
	       @AttributeOverride(name="bnr", column=@Column(name="birthPlace_bnr")),
	       @AttributeOverride(name="cen", column=@Column(name="birthPlace_cen")),
	       @AttributeOverride(name="cnt", column=@Column(name="birthPlace_cnt")),
	       @AttributeOverride(name="cpa", column=@Column(name="birthPlace_cpa")),
	       @AttributeOverride(name="cty", column=@Column(name="birthPlace_cty")),
	       @AttributeOverride(name="sta", column=@Column(name="birthPlace_sta")),
	       @AttributeOverride(name="stb", column=@Column(name="birthPlace_stb")),
	       @AttributeOverride(name="str", column=@Column(name="birthPlace_str")),
	       @AttributeOverride(name="zip", column=@Column(name="birthPlace_zip"))
	})
	public AD getBirthPlace() {
		return birthPlace;
	}

	public void setBirthPlace(AD birthPlace) {
		this.birthPlace = birthPlace;
	}
	
	// CUSTOM PROPERTIES END
	
	// SHORTCUTS
//	private Person person;
//
//	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//    @JoinColumn(name="person_id")
//	@ForeignKey(name="FK_Employee_Person")
//	@Index(name="IX_Employee_Person")
//	public Person getPerson() {
//		return person;
//	}
//
//	public void setPerson(Person person) {
//		this.person = person;
//	}
	
	private List<EmployeeRole> employeeRole;

	@JsonManagedReference
	@OneToMany(fetch=FetchType.LAZY, mappedBy="employee", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
	@Cascade(org.hibernate.annotations.CascadeType.EVICT)
	public List<EmployeeRole> getEmployeeRole() {
		return employeeRole;
	}

	public void setEmployeeRole(List<EmployeeRole> employeeRole) {
		this.employeeRole = employeeRole;
	}
	
	public void addEmployeeRole(EmployeeRole employeeRole) {
		// create the association set if it doesn't exist already
		if (this.employeeRole == null) {
			this.employeeRole = new ArrayList<EmployeeRole>();
		}
		// add the association to the association set
		if(!this.employeeRole.contains(employeeRole)) {
			this.employeeRole.add(employeeRole);
			// make the inverse link
			employeeRole.setEmployee(this);
		}
	}
	
	public void removeEmployeeRole(EmployeeRole employeeRole) {
		if (this.employeeRole == null) {
			this.employeeRole = new ArrayList<EmployeeRole>();
			return;
		}
		//add the association
		if(this.employeeRole.contains(employeeRole)){
			this.employeeRole.remove(employeeRole);
			//make the inverse link
			employeeRole.setEmployee(null);
		}

	}
	
	private Organization organization;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
    @JoinColumn(name="organization_id")
	@ForeignKey(name="FK_Employee_Organization")
	@Index(name="IX_Employee_Organization")
	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	
	// PHI_AMB
	
	 /**
	  *  javadoc for locationsCode
	  */
	private List<CodeValue> ageTypeCode;

	@ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinTable(name="employee_agetypecode", joinColumns = { @JoinColumn(name="Employee_id") }, inverseJoinColumns = { @JoinColumn(name="ageTypeCode") })
	@ForeignKey(name="FK_ageTypeCode_Employee", inverseName="FK_Employee_ageTypeCode")
	public List<CodeValue> getAgeTypeCode(){
		return ageTypeCode;
	}

	public void setAgeTypeCode(List<CodeValue> ageTypeCode){
		this.ageTypeCode = ageTypeCode;
	}

	public void addAgeTypeCode (CodeValue cv) {
		// create the association set if it doesn't exist already
		if (this.ageTypeCode == null) {
			this.ageTypeCode = new ArrayList<CodeValue>();
		}
		// add the association to the association set
		if(!this.ageTypeCode.contains(cv)) {
			this.ageTypeCode.add(cv);
		}
	}

	
	
	 /**
	 *  javadoc for locationsCode
	 */
	 private List<CodeValue> locationsCode;

	 @ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	 @JoinTable(name="employee_locationscode", joinColumns = { @JoinColumn(name="Employee_id") }, inverseJoinColumns = { @JoinColumn(name="locationsCode") })
	 @ForeignKey(name="FK_locationsCode_Employee", inverseName="FK_Employee_locationsCode")
	 public List<CodeValue> getLocationsCode(){
	  return locationsCode;
	 }

	 public void setLocationsCode(List<CodeValue> locationsCode){
	  this.locationsCode = locationsCode;
	 }
	
	 public void addLocationsCode (CodeValue cv) {
			// create the association set if it doesn't exist already
			if (this.locationsCode == null) {
				this.locationsCode = new ArrayList<CodeValue>();
			}
			// add the association to the association set
			if(!this.locationsCode.contains(cv)) {
				this.locationsCode.add(cv);
			}
		}
	 /*
		*  javadoc for statistics
		*/
		private Boolean statistics;
	
		@Column(name="statistics")
		public Boolean getStatistics(){
			return statistics;
		}
	
		public void setStatistics(Boolean statistics){
			this.statistics = statistics;
		}
	

		
	 /**
	  *  javadoc for student
	  */  
		private Boolean student;
	
		@Column(name="student")
		public Boolean getStudent(){
			return student;
		}
	
		public void setStudent(Boolean student){
			this.student = student;
		}
		
		private Boolean upg;
		
		@Column(name="upg")
		public Boolean getUpg(){
			return upg;
		}
		
		public void setUpg(Boolean upg){
			this.upg = upg;
		}
		
		private String structure;

		@Column(name="structure")
		public String getStructure() {
			return structure;
		}

		public void setStructure(String structure) {
			this.structure = structure;
		}
		
		private String title;

		@Column(name="title")
		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		/**
		*  javadoc for isNew
		*/
		private Boolean isNew;
	
		@Column(name="is_new")
		public Boolean getIsNew(){
			return isNew;
		}
	
		public void setIsNew(Boolean isNew){
			this.isNew = isNew;
		}
		
		//From Role
		
		/**
		 *  Address
		 */
		private AD addr;

		@Embedded
		@AttributeOverrides({
			@AttributeOverride(name="adl", column=@Column(name="addr_adl")),
			@AttributeOverride(name="bnr", column=@Column(name="addr_bnr")),
			@AttributeOverride(name="cen", column=@Column(name="addr_cen")),
			@AttributeOverride(name="cnt", column=@Column(name="addr_cnt")),
			@AttributeOverride(name="cpa", column=@Column(name="addr_cpa")),
			@AttributeOverride(name="cty", column=@Column(name="addr_cty")),
			@AttributeOverride(name="sta", column=@Column(name="addr_sta")),
			@AttributeOverride(name="stb", column=@Column(name="addr_stb")),
			@AttributeOverride(name="str", column=@Column(name="addr_str")),
			@AttributeOverride(name="zip", column=@Column(name="addr_zip"))
		})
		public AD getAddr(){
			return addr;
		}

		public void setAddr(AD addr){
			this.addr = addr;
		}
		
		private IVL<Date> effectiveTime;

		@Embedded
		@AttributeOverrides({
		       @AttributeOverride(name="low", column=@Column(name="effective_time_low")),
		       @AttributeOverride(name="high", column=@Column(name="effective_time_high")),
		       //@AttributeOverride(name="lowClosed", column=@Column(name="effective_time_lowClosed")),
		       //@AttributeOverride(name="highClosed", column=@Column(name="effective_time_highClosed"))
		})
		public IVL<Date> getEffectiveTime() {
			return effectiveTime;
		}

		public void setEffectiveTime(IVL<Date> effectiveTime) {
			this.effectiveTime = effectiveTime;
		}
		
		private Set<II> id;

		@OneToMany(targetEntity=II4Employee.class ,fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
		@Cascade(org.hibernate.annotations.CascadeType.EVICT)
		@JoinColumn(name="employee_id")
		@ForeignKey(name="FK_Employee_id")
		@Index(name="IX_Employee_id")
		@AuditJoinTable(name="z_employee_id_jt")
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
		
		private String tci;
		/**
		 * tci is "time to come in": it is a code assigned to every consultant to
		 * admit a patient for an inpatient encounter
		 * @return
		 */
		@Column(name="tci")
		public String getTci() {
			return tci;
		}

		public void setTci(String tci) {
			this.tci = tci;
		}

		private String tsPincode;
		/**
		 * pincode for sogei (sistema TS) authentication
		 * @return
		 */
		@Column(name="ts_pincode")
		public String getTsPincode() {
			return tsPincode;
		}

		public void setTsPincode(String tsPincode) {
			this.tsPincode = tsPincode;
		}

		private String tsPassword;
		/**
		 * password for sogei (sistema TS) authentication
		 * @return
		 */
		@Column(name="ts_password")
		public String getTsPassword() {
			return tsPassword;
		}

		public void setTsPassword(String tsPassword) {
			this.tsPassword = tsPassword;
		}
		
		private String tsCodiceAsl;
		/**
		 * codiceAsl for sogei (sistema TS) webservices
		 * @return
		 */
		@Column(name="ts_codice_asl")
		public String getTsCodiceAsl() {
			return tsCodiceAsl;
		}

		public void setTsCodiceAsl(String tsCodiceAsl) {
			this.tsCodiceAsl = tsCodiceAsl;
		}
		
		private String tsCodiceRegione;
		/**
		 * codiceRegione for sogei (sistema TS) webservices
		 * @return
		 */
		@Column(name="ts_codice_regione")
		public String getTsCodiceRegione() {
			return tsCodiceRegione;
		}

		public void setTsCodiceRegione(String tsCodiceRegione) {
			this.tsCodiceRegione = tsCodiceRegione;
		}
//		
//		/**
//		 *  javadoc for defaultLanguageCode
//		 */
//		 private CodeValuePhi defaultLanguageCode;
//
//		 @ManyToOne(fetch=FetchType.LAZY)
//		 @JoinColumn(name="defaultLanguageCode")
//		 @ForeignKey(name="FK_Employee_defaultLanguageCode")
//		 @Index(name="IX_Employee_defaultLanguageCode")
//		 public CodeValuePhi getDefaultLanguageCode(){
//		  return defaultLanguageCode;
//		 }
//
//		 public void setDefaultLanguageCode(CodeValuePhi defaultLanguageCode){
//		  this.defaultLanguageCode = defaultLanguageCode;
//		 }
		
		private List<CodeValue> alternateRole;

		@ManyToMany(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
		@JoinTable(name="employee_alternateRole", joinColumns = { @JoinColumn(name="Employee_id") }, inverseJoinColumns = { @JoinColumn(name="alternateRole_ID") })
		@ForeignKey(name="FK_alternateRole_Employee", inverseName="FK_Employee_alternateRole")
		public List<CodeValue> getAlternateRole(){
			return alternateRole;
		}

		public void setAlternateRole(List<CodeValue> alternateRole){
			this.alternateRole = alternateRole;
		}
}
