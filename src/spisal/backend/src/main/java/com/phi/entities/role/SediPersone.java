package com.phi.entities.role;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.Cariche;
import com.phi.entities.baseEntity.SituazioneProfessionale;
import com.phi.entities.baseEntity.SpisalAddr;
import com.phi.entities.dataTypes.AD;
import com.phi.entities.dataTypes.CodeValueCountry;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.EN;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.dataTypes.TEL;
import com.phi.entities.entity.Organization;

import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.role.Person;

/**
 * Entità non storicizzata via ENVERS perchè aggiornata principalmente via integrazione
 * @author 510087
 *
 */

@javax.persistence.Entity
@Table(name = "sedi_persone")
public class SediPersone extends BaseEntity {

	private static final long serialVersionUID = 1164311664L;

	/**
	*  javadoc for isGiuridica
	*/
	private Boolean isGiuridica=false;

	@Column(name="is_giuridica")
	public Boolean getIsGiuridica(){
		return isGiuridica;
	}

	public void setIsGiuridica(Boolean isGiuridica){
		this.isGiuridica = isGiuridica;
	}

	/**
	*  javadoc for cariche
	*/
	private List<Cariche> cariche;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="sediPersone")
	public List<Cariche> getCariche() {
		return cariche;
	}

	public void setCariche(List<Cariche>list){
		cariche = list;
	}

	public void addCariche(Cariche cariche) {
		if (this.cariche == null) {
			this.cariche = new ArrayList<Cariche>();
		}
		// add the association
		if(!this.cariche.contains(cariche)) {
			this.cariche.add(cariche);
			// make the inverse link
			cariche.setSediPersone(this);
		}
	}

	public void removeCariche(Cariche cariche) {
		if (this.cariche == null) {
			this.cariche = new ArrayList<Cariche>();
			return;
		}
		//add the association
		if(this.cariche.contains(cariche)){
			this.cariche.remove(cariche);
			//make the inverse link
			cariche.setSediPersone(null);
		}
	}

	/**
	*  javadoc for person
	*/
	private Person person;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="person_id")
	@ForeignKey(name="FK_SediPersone_person")
	@Index(name="IX_SediPersone_person")
	public Person getPerson(){
		return person;
	}

	public void setPerson(Person person){
		this.person = person;
	}
	
	/**
	*  javadoc for alternativeAddr
	*/
	private SpisalAddr alternativeAddr;

	@OneToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
	@Cascade(org.hibernate.annotations.CascadeType.EVICT)
	@JoinColumn(name="alternative_addr_id")
	@ForeignKey(name="FK_SediPersone_alternativeAddr")
	//@Index(name="IX_SediPersone_alternativeAddr")
	public SpisalAddr getAlternativeAddr(){
		return alternativeAddr;
	}

	public void setAlternativeAddr(SpisalAddr alternativeAddr){
		this.alternativeAddr = alternativeAddr;
	}



	/**
	*  javadoc for professionalSituation
	*/
	private SituazioneProfessionale professionalSituation;

	@OneToOne(fetch=FetchType.LAZY, cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH})
	@Cascade(org.hibernate.annotations.CascadeType.EVICT)
	@JoinColumn(name="professional_situation_id")
	@ForeignKey(name="FK_SediPrson_profssinalSituatin")
	//@Index(name="IX_SediPrson_profssinalSituatin")
	public SituazioneProfessionale getProfessionalSituation(){
		return professionalSituation;
	}

	public void setProfessionalSituation(SituazioneProfessionale professionalSituation){
		this.professionalSituation = professionalSituation;
	}

	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="code")
	@ForeignKey(name="FK_SediPersone_code")
	//@Index(name="IX_SediPersone_code")
	public CodeValuePhi getCode() {
		return code;
	}

	public void setCode(CodeValuePhi code) {
		this.code = code;
	}
	
	/**
	*  used by integrations
	*  HL7 creation msg date and time
	*/
	private Date HL7MsgDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="hl7_msg_date")
	public Date getHL7MsgDate() {
		return HL7MsgDate;
	}

	public void setHL7MsgDate(Date HL7MsgDate) {
		this.HL7MsgDate = HL7MsgDate;
	}

	/**
	*  used by integrations to store an external system id
	**/
	private String mpi;

	@Column(name="mpi")//, unique=true)
	public String getMpi(){
		return mpi;
	}

	public void setMpi(String externalId){
		this.mpi = externalId;
	}
	
	/**
	*  Fiscal Code
	*/
	private String fiscalCode;

	@Column(name="fiscal_code")
	public String getFiscalCode() {
		if (this.fiscalCode!=null)
			return fiscalCode;
		
		else if (this.person!=null)
			return this.person.getFiscalCode();
		
		return null;
	}

	public void setFiscalCode(String fiscalCode) {
		this.fiscalCode = fiscalCode;
	}
	
	/**
	*  used by integrations to store an external system id
	**/
	private String stp;

	@Column(name="stp")//, unique=true)
	@Index(name="IX_Stp")
	public String getStp(){
		return stp;
	}

	public void setStp(String stp){
		this.stp = stp;
	}
	
	private IVL<Date> stpDate;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="stp_date_begin")),
	    @AttributeOverride(name="high", column=@Column(name="stp_date_end"))
		//@AttributeOverride(name="lowClosed", column=@Column(name="effective_time_lowClosed")),
		//@AttributeOverride(name="highClosed", column=@Column(name="effective_time_highClosed"))
	})
	public IVL<Date> getStpDate() {
		return stpDate;
	}

	public void setStpDate(IVL<Date> stpDate) {
		this.stpDate = stpDate;
	}
	
	/**
	*  used by integrations to store an external system id
	**/
	private String cs;

	@Column(name="cs")//, unique=true)
	@Index(name="IX_Cs")
	public String getCs(){
		return cs;
	}

	public void setCs(String cs){
		this.cs = cs;
	}
	
	private String csRegion;

	@Column(name="cs_region")//, unique=true)
	@Index(name="IX_Cs_region")
	public String getCsRegion(){
		return csRegion;
	}

	public void setCsRegion(String csRegion){
		this.csRegion = csRegion;
	}
	
	private IVL<Date> csDate;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="cs_date_begin")),
	    @AttributeOverride(name="high", column=@Column(name="cs_date_end"))
		//@AttributeOverride(name="lowClosed", column=@Column(name="effective_time_lowClosed")),
		//@AttributeOverride(name="highClosed", column=@Column(name="effective_time_highClosed"))
	})
	public IVL<Date> getCsDate() {
		return csDate;
	}

	public void setCsDate(IVL<Date> csDate) {
		this.csDate = csDate;
	}
	
	/**
	*  used by integrations to store an external system id
	**/
	private String eni;

	@Column(name="eni")//, unique=true)
	@Index(name="IX_Eni")
	public String getEni(){
		return eni;
	}

	public void setEni(String eni){
		this.eni = eni;
	}
	
	private IVL<Date> eniDate;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="eni_date_begin")),
	    @AttributeOverride(name="high", column=@Column(name="eni_date_end"))
		//@AttributeOverride(name="lowClosed", column=@Column(name="effective_time_lowClosed")),
		//@AttributeOverride(name="highClosed", column=@Column(name="effective_time_highClosed"))
	})
	public IVL<Date> getEniDate() {
		return eniDate;
	}

	public void setEniDate(IVL<Date> eniDate) {
		this.eniDate = eniDate;
	}
	
	/**
	*  used by integrations to store an external system id
	**/
	private String teamPers;

	@Column(name="team_pers")//, unique=true)
	@Index(name="IX_Team_pers")
	public String getTeamPers(){
		return teamPers;
	}

	public void setTeamPers(String teamPers){
		this.teamPers = teamPers;
	}
	
	/**
	*  used by integrations to store an external system id
	**/
	private String teamInst;

	@Column(name="team_inst")//, unique=true)
	@Index(name="IX_Team_inst")
	public String getTeamInst(){
		return teamInst;
	}

	public void setTeamInst(String teamInst){
		this.teamInst = teamInst;
	}
	
	private IVL<Date> teamDate;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="low", column=@Column(name="team_date_begin")),
	    @AttributeOverride(name="high", column=@Column(name="team_date_end"))
		//@AttributeOverride(name="lowClosed", column=@Column(name="effective_time_lowClosed")),
		//@AttributeOverride(name="highClosed", column=@Column(name="effective_time_highClosed"))
	})
	public IVL<Date> getTeamDate() {
		return teamDate;
	}

	public void setTeamDate(IVL<Date> teamDate) {
		this.teamDate = teamDate;
	}
	
	/**
	*  used by integrations to store an external system id
	**/
	private String teamIdent;

	@Column(name="team_ident")//, unique=true)
	@Index(name="IX_Team_ident")
	public String getTeamIdent(){
		return teamIdent;
	}

	public void setTeamIdent(String teamIdent){
		this.teamIdent = teamIdent;
	}
	
	/**
	*  used by integrations to store an external system id
	**/
	private String teamCode;

	@Column(name="team_code")//, unique=true)
	@Index(name="IX_Team_code")
	public String getTeamCode(){
		return teamCode;
	}

	public void setTeamCode(String teamCode){
		this.teamCode = teamCode;
	}
	
	/**
	 *  Name
	 */
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
	public EN getName(){
		if (name != null)
			return name;
		else if (this.person!=null)
			return this.person.getName();
		
		return null;
	}

	public void setName(EN name){
		this.name = name;
	}
	
	/**
	*  BirthTime
	*/
	private Date birthTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="birth_time")
	public Date getBirthTime() {
		if (this.birthTime!=null)
			return birthTime;
		else if (this.person!=null)
			return this.person.getBirthTime();
		
		return null;
    }

	public void setBirthTime(Date birthTime) {
		this.birthTime = birthTime;
	}
	
	/**
	*  Gender Code
	**/
	private CodeValuePhi genderCode;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="gender_code")
	@ForeignKey(name="FK_SediPersone_genderCode")
	//@Index(name="IX_SediPersone_genderCode")
	public CodeValuePhi getGenderCode(){
		return genderCode;
	}

	public void setGenderCode(CodeValuePhi genderCode){
		this.genderCode = genderCode;
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
		if (this.birthPlace!=null)
			return birthPlace;
		
		else if (this.person!=null)
			return this.person.getBirthPlace();
		
		return null;
	}

	public void setBirthPlace(AD birthPlace) {
		this.birthPlace = birthPlace;
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
	 *  Country Of Address
	 */
	private CodeValueCountry countryOfAddr;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="country_of_addr")
	@ForeignKey(name="FK_SediPer_countryOfAddr")
	//@Index(name="IX_SediPer_countryOfAddr")
	public CodeValueCountry getCountryOfAddr(){
		return countryOfAddr;
	}

	public void setCountryOfAddr(CodeValueCountry countryOfAddr){
		this.countryOfAddr = countryOfAddr;
	}
	
	/**
	 *  Domicile Address
	 */
	private AD domicileAddr;

	@Embedded
	@AssociationOverride(name="code", joinColumns = @JoinColumn(name="dom_addr_code"))
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
	 *  Country Of Domicile
	 */
	private CodeValueCountry countryOfDomicile;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="country_of_dom")
	@ForeignKey(name="FK_SediPer_countryOfDom")
	//@Index(name="IX_SediPer_countryOfDom")
	public CodeValueCountry getCountryOfDomicile(){
		return countryOfDomicile;
	}

	public void setCountryOfDomicile(CodeValueCountry countryOfDomicile){
		this.countryOfDomicile = countryOfDomicile;
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
	
	private CodeValuePhi maritalStatusCode;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="marital_status_code")
	@ForeignKey(name="FK_SediPer_maritalStatus")
	public CodeValuePhi getMaritalStatusCode() {
		return maritalStatusCode;
	}

	public void setMaritalStatusCode(CodeValuePhi maritalStatusCode) {
		this.maritalStatusCode = maritalStatusCode;
	}
	
	/**
	 * javadoc for citizen
	 */
	private CodeValueCountry citizen;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity=CodeValueCountry.class)
	@JoinColumn(name = "citizen")
	@ForeignKey(name = "FK_SediPer_citizen")
	//@Index(name="IX_SediPer_citizen")
	public CodeValueCountry getCitizen() {
		return citizen;
	}

	public void setCitizen(CodeValueCountry citizen) {
		this.citizen = citizen;
	}
	
	/**
	 *  Country Of Birth
	 */
	private CodeValueCountry countryOfBirth;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="country_of_birth")
	@ForeignKey(name="FK_SediPer_countryOfBirth")
	//@Index(name="IX_SediPer_countryOfBirth")
	public CodeValueCountry getCountryOfBirth(){
		return countryOfBirth;
	}

	public void setCountryOfBirth(CodeValueCountry countryOfBirth){
		this.countryOfBirth = countryOfBirth;
	}
	
	/**
	 *  javadoc for Death date
	 */
	private Date deathDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="death_date", length=19)
	public Date getDeathDate() {
		return deathDate;
	}

	public void setDeathDate(Date deathDate) {
		this.deathDate = deathDate;
	}
	
	/**
	*  javadoc for deathIndicator
	*/
	private boolean deathIndicator = false;
	
    @Column(name="death_indicator", nullable=false)
	public boolean getDeathIndicator() {
		return deathIndicator;
	}

	public void setDeathIndicator(boolean deathIndicator) {
		this.deathIndicator = deathIndicator;
	}
	
	/**
	*  javadoc for toUpdate
	*/
	private boolean toUpdate = false;
	
    @Column(name="to_update", nullable=false)
	public boolean getToUpdate() {
		return toUpdate;
	}

	public void setToUpdate(boolean toUpdate) {
		this.toUpdate = toUpdate;
	}
	
	/**
	*  javadoc for reliability
	*/
	private String reliability;

	@Column(name="reliability" , length=2000)
	public String getReliability(){
		return reliability;
	}

	public void setReliability(String reliability){
		this.reliability = reliability;
	}
	
	/**
	*  Contatore incrementale per storico PARIX
	*/
	
	private long rev;

	@Column(name="rev")
	public long getRev(){
		return rev;
	}

	public void setRev(long rev){
		this.rev = rev;
	}
	
	/**
	*  javadoc for original organization (ASL appartenenza)
	*/
	private Organization originalOrg;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="aslApp")
	@ForeignKey(name="FK_SediPersone_aslApp")
	public Organization getOriginalOrg() {
		return originalOrg;
	}

	public void setOriginalOrg(Organization aslApp) {
		this.originalOrg = aslApp;
	}
	
	/**
	*  javadoc for current organization (ASL assiatenza)
	*/
	private Organization currentOrg;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="aslAss")
	@ForeignKey(name="FK_SediPersone_aslAss")
	public Organization getCurrentOrg() {
		return currentOrg;
	}

	public void setCurrentOrg(Organization aslAss) {
		this.currentOrg = aslAss;
	}
	
	//methods needed for BaseEntity extension
	private CodeValuePhi category;

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
    @JoinColumn(name="category")
	@ForeignKey(name="FK_SediPersone_cat")
	//@Index(name="IX_SediPersone_cat")
	public CodeValuePhi getCategory() {
		return category;
	}

	public void setCategory(CodeValuePhi category) {
		this.category = category;
	}

	/**
	 *  Doctor
	 */
	private Physician physician;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="Physician_id")
	@ForeignKey(name="FK_SediPrs_phcn")
	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	public Physician getPhysician() {
		return physician;
	}

	public void setPhysician(Physician physician) {
		this.physician = physician;
	}

	/**
	*  javadoc for alias
	*/
	private Person master;

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="master_id")
	@ForeignKey(name="FK_SediPersone_master")
	//@Index(name="IX_SediPersone_master")
	public Person getMaster(){
		return master;
	}

	public void setMaster(Person master){
		this.master = master;
	}

	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SediPersone_sequence")
	@SequenceGenerator(name = "SediPersone_sequence", sequenceName = "SediPersone_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
