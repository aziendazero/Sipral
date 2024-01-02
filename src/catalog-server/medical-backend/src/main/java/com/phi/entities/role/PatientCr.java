package com.phi.entities.role;

import java.util.Date;

import javax.persistence.AssociationOverride;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.entity.Organization;

@javax.persistence.Entity
@Table(name = "patient_cr")
@Audited
public class PatientCr extends BaseEntity /*Role*/ {

	private static final long serialVersionUID = 148708092L;

	//methods needed for BaseEntity extension
	
		/**
		*  javadoc for imported
		*/
		private Boolean imported;

		@Column(name="imported")
		public Boolean getImported(){
			return imported;
		}

		public void setImported(Boolean imported){
			this.imported = imported;
		}
		
		/**
		*  javadoc for currentOrgCode
		*/
		private CodeValuePhi currentOrgCode;

		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="currentOrgCode")
		@ForeignKey(name="FK_PatientCr_currentOrgCode")
		@Index(name="IX_PatientCr_currentOrgCode")
		public CodeValuePhi getCurrentOrgCode(){
			return currentOrgCode;
		}

		public void setCurrentOrgCode(CodeValuePhi currentOrgCode){
			this.currentOrgCode = currentOrgCode;
		}
		
		/**
		*  javadoc for jobTitle
		*/
		private String jobTitle;

		@Column(name="job_title")
		public String getJobTitle(){
			return jobTitle;
		}

		public void setJobTitle(String jobTitle){
			this.jobTitle = jobTitle;
		}

		@Override
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO, generator = "Patient_sequence")
		@SequenceGenerator(name = "Patient_sequence", sequenceName = "Patient_sequence")
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
		@ForeignKey(name = "FK_Patient_sdloc")
		@Index(name="IX_Patient_sdloc")
		public ServiceDeliveryLocation getServiceDeliveryLocation() {
			return serviceDeliveryLocation;
		}

		@Override
		public void setServiceDeliveryLocation(
				ServiceDeliveryLocation serviceDeliveryLocation) {
			this.serviceDeliveryLocation = serviceDeliveryLocation;
		}*/


		/**
		 *  javadoc for statusCode
		 */
		private CodeValuePhi statusCode;

		@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
		@JoinColumn(name="statusCode")
		@ForeignKey(name="FK_PatientCr_statusCode")
		@Index(name="IX_PatientCr_statusCode")
		public CodeValuePhi getStatusCode(){
			return statusCode;
		}

		public void setStatusCode(CodeValuePhi statusCode){
			this.statusCode = statusCode;
		}

		/**
		 *  Name
		 */
		private EN name;

		@Embedded
		@AttributeOverrides({
			@AttributeOverride(name="fam", column=@Column(name="name_fam_cr")), //encrypted
			@AttributeOverride(name="giv", column=@Column(name="name_giv_cr")), //encrypted
			@AttributeOverride(name="pfx", column=@Column(name="name_pfx")),
			@AttributeOverride(name="sfx", column=@Column(name="name_sfx")),
			@AttributeOverride(name="del", column=@Column(name="name_del")),
			@AttributeOverride(name="formatted", column=@Column(name="name_formatted"))
		})
		public EN getName(){
			return name;
		}

		public void setName(EN name){
			this.name = name;
		}

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

		
		/**
		 *  Domicile Address
		 */
		private AD domicileAddr;

		@Embedded
		@AssociationOverride(name="code", joinColumns = @JoinColumn(name="domicile_code"))
		@AttributeOverrides({
			@AttributeOverride(name="adl", column=@Column(name="dom_addr_adl")),
			@AttributeOverride(name="bnr", column=@Column(name="dom_addr_bnr")),
			@AttributeOverride(name="cen", column=@Column(name="dom_addr_cen")),
			@AttributeOverride(name="cnt", column=@Column(name="dom_addr_cnt")),
			@AttributeOverride(name="cpa", column=@Column(name="dom_addr_cpa")),
			@AttributeOverride(name="cty", column=@Column(name="dom_addr_cty")),
			@AttributeOverride(name="sta", column=@Column(name="dom_addr_sta")),
			@AttributeOverride(name="stb", column=@Column(name="dom_addr_stb")),
			@AttributeOverride(name="str", column=@Column(name="dom_addr_str")),
			@AttributeOverride(name="zip", column=@Column(name="dom_addr_zip"))
		})
		public AD getDomicileAddr(){
			return domicileAddr;
		}

		public void setDomicileAddr(AD domicileAddr){
			this.domicileAddr = domicileAddr;
		}
		
		/**
		 *  Telecom
		 */
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
		public TEL getTelecom(){
			return telecom;
		}

		public void setTelecom(TEL telecom){
			this.telecom = telecom;
		}

		// CUSTOM PROPERTIES	

		/**
		 *  externalId used by integrations
		 *  store here id of an external system
		 */
		private String externalId;

		@Column(name="external_id")//, unique=true)
		@Index(name="IX_PatientCr_ExtId")
		public String getExternalId(){
			return externalId;
		}

		public void setExternalId(String externalId){
			this.externalId = externalId;
		}

		/**
		 *  G2 anag is used to save the codice_interno o angr0 of LISA IM DB
		 */
		private String g2Anag;

		@Column(name="g2_anag", unique=true)
		public String getG2Anag(){
			return g2Anag;
		}

		public void setG2Anag(String g2Anag){
			this.g2Anag = g2Anag;
		}
		/**
		 *  javadoc for teamId
		 */
		private String teamId;

		@Column(name="team_id")//, unique=true)
		public String getTeamId(){
			return teamId;
		}

		public void setTeamId(String teamId){
			this.teamId = teamId;
		}


		/**

		 *  additionalId used by integrations when externalId has been already used
		 *  store here id of an external system
		 */
		private String additionalId;

		@Column(name="additional_id")//, unique=true)
		@Index(name="IX_PatientCr_addId")
		public String getAdditionalId(){
			return additionalId;
		}

		public void setAdditionalId(String additionalId){
			this.additionalId = additionalId;
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

		/**
		 *  Country Of Birth
		 */
		private CodeValueCountry countryOfBirth;

		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="country_of_birth")
		@ForeignKey(name="FK_PatientCr_countryOfBirth")
		@Index(name="IX_PatientCr_countryOfBirth")
		public CodeValueCountry getCountryOfBirth(){
			return countryOfBirth;
		}

		public void setCountryOfBirth(CodeValueCountry countryOfBirth){
			this.countryOfBirth = countryOfBirth;
		}

		/**
		 *  Foreign BirthPlace
		 */
		private Boolean foreignBirthPlace;

		@Column(name="foreign_birthPlace")
		public Boolean getForeignBirthPlace() {
			return foreignBirthPlace;
		}

		public void setForeignBirthPlace(Boolean foreignBirthPlace) {
			this.foreignBirthPlace = foreignBirthPlace;
		}

		/**
		 *  Language
		 */
		private CodeValuePhi language;

		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="language")
		@ForeignKey(name="FK_PatientCr_language")
		@Index(name="IX_PatientCr_language")
		public CodeValuePhi getLanguage() {
			return language;
		}

		public void setLanguage(CodeValuePhi language) {
			this.language = language;
		}

		/**
		 *  Gender Code
		 */
		private CodeValuePhi genderCode;

		@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
		@JoinColumn(name="gender_code")
		@ForeignKey(name="FK_PatientCr_genderCode")
		@Index(name="IX_PatientCr_genderCode")
		public CodeValuePhi getGenderCode(){
			return genderCode;
		}

		public void setGenderCode(CodeValuePhi genderCode){
			this.genderCode = genderCode;
		}

		/**
		 *  Deceased Time
		 */
		private Date deceasedTime;

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name="deceased_time", length=19)
		public Date getDeceasedTime() {
			return deceasedTime;
		}

		public void setDeceasedTime(Date deceasedTime) {
			this.deceasedTime = deceasedTime;
		}

		/**
		 *  Fiscal Code
		 */
		private String fiscalCodeCr;

		@Column(name="fiscal_code_cr") //encrypted
		public String getFiscalCodeCr() {
			return fiscalCodeCr;
		}

		public void setFiscalCodeCr(String fiscalCodeCr) {
			this.fiscalCodeCr = fiscalCodeCr;
		}

		/**
		 *  noAllergy: if patient claimed that not have allergies noAllergy =true
		 *  It may be null because it has never been asked for	
		 */
		private Boolean noAllergy;

		@Column(name="noAllergy")
		public Boolean getNoAllergy() {
			return noAllergy;
		}
		public void setNoAllergy(Boolean noAllergy) {
			this.noAllergy = noAllergy;
		}

		/**
		 *  Generic exemption
		 */
		private String genericExemption;

		@Column(name="generic_exemption")
		public String getGenericExemption(){
			return genericExemption;
		}

		public void setGenericExemption(String genericExemption){
			this.genericExemption = genericExemption;
		}

		/**
		 *  Internal Consent
		 */
		private Boolean internalConsent;

		@JoinColumn(name = "internalConsent")
		public Boolean getInternalConsent() {
			return internalConsent;
		}

		public void setInternalConsent(Boolean internalConsent) {
			this.internalConsent = internalConsent;
		}

		/**
		 *  External Consent
		 */
		private Boolean externalConsent;

		@JoinColumn(name = "externalConsent")
		public Boolean getExternalConsent() {
			return externalConsent;
		}

		public void setExternalConsent(Boolean externalConsent) {
			this.externalConsent = externalConsent;
		}

		private String note1;
		private String note2;
		private String note3;
		private Date date1;
		private Date date2;
		private Date date3;

		@Column(name = "note1")
		public String getNote1() {
			return note1;
		}

		public void setNote1(String note1) {
			this.note1 = note1;
		}

		@Column(name = "note2")
		public String getNote2() {
			return note2;
		}

		public void setNote2(String note2) {
			this.note2 = note2;
		}

		@Column(name = "note3")
		public String getNote3() {
			return note3;
		}

		public void setNote3(String note3) {
			this.note3 = note3;
		}

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name="date1")
		public Date getDate1() {
			return date1;
		}

		public void setDate1(Date date1) {
			this.date1 = date1;
		}

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name="date2")
		public Date getDate2() {
			return date2;
		}

		public void setDate2(Date date2) {
			this.date2 = date2;
		}

		@Temporal(TemporalType.TIMESTAMP)
		@Column(name="date3")
		public Date getDate3() {
			return date3;
		}

		public void setDate3(Date date3) {
			this.date3 = date3;
		}

		// CUSTOM PROPERTIES END


		// SHORTCUTS

		/**
		 *  Doctor
		 */
		private Employee doctor;

		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="doctor_id")
		@ForeignKey(name="FK_PatientCr_doctor")
		@Index(name="IX_PatientCr_doctor")
		public Employee getDoctor() {
			return doctor;
		}

		public void setDoctor(Employee doctor) {
			this.doctor = doctor;
		}


		// SHORTCUTS END
		private Organization originalOrg;

		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="aslApp")
		@ForeignKey(name="FK_PatientCr_aslApp")
		public Organization getOriginalOrg() {
			return originalOrg;
		}

		public void setOriginalOrg(Organization aslApp) {
			this.originalOrg = aslApp;
		}

		private Organization currentOrg;

		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="aslAss")
		@ForeignKey(name="FK_PatientCr_aslAss")
		public Organization getCurrentOrg() {
			return currentOrg;
		}

		public void setCurrentOrg(Organization aslAss) {
			this.currentOrg = aslAss;
		}
		
		
		private ServiceDeliveryLocation district;

		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumn(name="district")
		@ForeignKey(name="FK_PatientCr_district")	
		public ServiceDeliveryLocation getDistrict() {
			return district;
		}

		public void setDistrict(ServiceDeliveryLocation district) {
			this.district = district;
		}

		
		/**
		 * stp id for stranger
		 */
		private String stp;

		@Column(name="stp")
		public String getStp() {
			return stp;
		}
		public void setStp(String stp) {
			this.stp = stp;
		}

		/**
		 * id for document supplied for patient by anagraphic
		 */
		private String doc;

		@Column(name="doc")
		public String getDoc() {
			return doc;
		}
		public void setDoc(String doc) {
			this.doc = doc;
		}
		
		/**
		 * id for health card
		 */
		private String healthCardId;

		@Column(name="health_card_id")
		public String getHealthCardId() {
			return healthCardId;
		}
		public void setHealthCardId(String healthCardId) {
			this.healthCardId = healthCardId;
		}
		
		
		private CodeValuePhi maritalStatusCode;

		@ManyToOne(fetch=FetchType.LAZY)
	    @JoinColumn(name="marital_status_code")
		@ForeignKey(name="FK_PatCr_maritalStatus")
		public CodeValuePhi getMaritalStatusCode() {
			return maritalStatusCode;
		}

		public void setMaritalStatusCode(CodeValuePhi maritalStatusCode) {
			this.maritalStatusCode = maritalStatusCode;
		}
		
		private CodeValue livingArrangementCode;

		@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	    @JoinColumn(name="living_arrangement_code")
		@ForeignKey(name="FK_PatCr_livingArrangmnt")
		@Index(name="IX_PatCr_livingArrangmnt") 
		public CodeValue getLivingArrangementCode() {
			return livingArrangementCode;
		}

		public void setLivingArrangementCode(CodeValue livingArrangementCode) {
			this.livingArrangementCode = livingArrangementCode;
		}
		
		private CodeValue educationLevelCode;

		@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	    @JoinColumn(name="education_level_code")
		@ForeignKey(name="FK_PatCr_educationLevel")
		@Index(name="IX_PatCr_educationLevel")
		public CodeValue getEducationLevelCode() {
			return educationLevelCode;
		}

		public void setEducationLevelCode(CodeValue educationLevelCode) {
			this.educationLevelCode = educationLevelCode;
		}
		
		private CodeValuePhi religiousAffiliationCode;

		@ManyToOne(fetch=FetchType.LAZY)
	    @JoinColumn(name="relig_affil_code")
		@ForeignKey(name="FK_patCr_relig_affil")
		@Index(name="IX_patCr_relig_affil")
		public CodeValuePhi getReligiousAffiliationCode() {
			return religiousAffiliationCode;
		}

		public void setReligiousAffiliationCode(CodeValuePhi religiousAffiliationCode) {
			this.religiousAffiliationCode = religiousAffiliationCode;
		}
		
		/**
		 * javadoc for citizen
		 */
		private CodeValue citizen;

		@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValueCountry.class)
		@JoinColumn(name = "citizen")
		@ForeignKey(name = "FK_PatCr_citizen")
		@Index(name="IX_PatCr_citizen")
		public CodeValue getCitizen() {
			return citizen;
		}

		public void setCitizen(CodeValue citizen) {
			this.citizen = citizen;
		}
}