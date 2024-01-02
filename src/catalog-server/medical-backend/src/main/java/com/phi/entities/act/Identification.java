package com.phi.entities.act;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.Contact;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;

@javax.persistence.Entity
@Table(name = "identification")
@Audited
public class Identification extends BaseEntity {

	private static final long serialVersionUID = 520706431L;

	private String elseViewAids;
	private String elseHearingAids;
	private String elseWalkingAids;
	private String elseWhat;
	private String elseWhom;
	private String elseAids;
	private String support;
	private String infoForSup;
	private String numChild;
	private String otherHomeKind;
	private String otherProvenance;
	private String otherNursingHome;
	private String elseProvenance;
	private List<CodeValuePhi> aidsIden;
	private String otherIden;
	private String hourRest;
	private String numNutrition;
	private Boolean confirmed;
	private String elseLang;
	private CodeValuePhi provenanceCode;// casa propria ,altro ospedale, ecc
	private CodeValuePhi homeKindCode;
	private Employee author;
	private CodeValuePhi languageCode;
	private String note;
	private String otherHospital;
	private CodeValueRole authorRole;
	private String emergencyNumbers;
	private Boolean confusion = false;
	private Boolean memoryDeficit = false;
	
	@Column(name="confusion")
	public Boolean getConfusion(){
		return confusion;
	}

	public void setConfusion(Boolean confusion){
		this.confusion = confusion;
	}
	
	@Column(name="memoryDeficit")
	public Boolean getMemoryDeficit(){
		return memoryDeficit;
	}

	public void setMemoryDeficit(Boolean memoryDeficit){
		this.memoryDeficit = memoryDeficit;
	}
	
	/**
	*  javadoc for emergencyNumbers
	*/

	@Column(name = "emergency_numbers")
	public String getEmergencyNumbers() {
		return emergencyNumbers;
	}

	public void setEmergencyNumbers(String emergencyNumbers) {
		this.emergencyNumbers = emergencyNumbers;
	}

	
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Identification_sequence")
	@SequenceGenerator(name = "Identification_sequence", sequenceName = "Identification_sequence")
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
	@ForeignKey(name = "FK_iden_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}*/
	
	/**
	*  javadoc for author
	*/
	

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Iden_author")
	@Index(name="IX_Iden_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@ForeignKey(name="FK_Iden_authorRole")
	@JoinColumn(name="authorRole")
	@Index(name="IX_Iden_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "provenance_code")
	@ForeignKey(name = "FK_iden_provenance")
	@Index(name="IX_Iden_provenance")
	public CodeValuePhi getProvenanceCode() {
		return provenanceCode;
	}

	public void setProvenanceCode(CodeValuePhi provenanceCode) {
		this.provenanceCode = provenanceCode;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "home_kind_code")
	@ForeignKey(name = "FK_Iden_home_kind")
	@Index(name="IX_Iden_home_kind")
	public CodeValuePhi getHomeKindCode() {
		return homeKindCode;
	}

	public void setHomeKindCode(CodeValuePhi homeKindCode) {
		this.homeKindCode = homeKindCode;
	}

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="language_code")
	@ForeignKey(name="FK_Iden_lac")
	@Index(name="IX_Iden_lac") //FIXME: accorciato FK_Iden_languageCode
	public CodeValuePhi getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(CodeValuePhi languageCode) {
		this.languageCode = languageCode;
	}
	private PatientEncounter patientEncounter;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="patient_encounter")
	@ForeignKey(name="FK_ident_Pat_Enc")
	@Index(name="IX_ident_Pat_Enc")
	public PatientEncounter getPatientEncounter() {
	    return patientEncounter;
	}
	public void setPatientEncounter(PatientEncounter param) {
	    this.patientEncounter = param;
	}
	
	@Column(name = "confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	

	@Column(name = "note", length = 2500)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = "numChild")
	public String getNumChild() {
		return numChild;
	}

	public void setNumChild(String numChild) {
		this.numChild = numChild;
	}
	
	@Column(name = "otherHomeKind")
	public String getOtherHomeKind() {
		return otherHomeKind;
	}

	public void setOtherHomeKind(String otherHomeKind) {
		this.otherHomeKind = otherHomeKind;
	}
	
	@Column(name = "otherProvenance")
	public String getOtherProvenance() {
		return otherProvenance;
	}

	public void setOtherProvenance(String otherProvenance) {
		this.otherProvenance = otherProvenance;
	}
	
	@Column(name = "otherNursingHome")
	public String getOtherNursingHome() {
		return otherNursingHome;
	}

	public void setOtherNursingHome(String otherNursingHome) {
		this.otherNursingHome = otherNursingHome;
	}

	@Column(name = "hourRest")
	public String getHourRest() {
		return hourRest;
	}

	public void setHourRest(String hourRest) {
		this.hourRest = hourRest;
	}

	@Column(name = "numNutrition")
	public String getNumNutrition() {
		return numNutrition;
	}

	public void setNumNutrition(String numNutrition) {
		this.numNutrition = numNutrition;
	}

	@Column(name = "otherIden")
	public String getOtherIden() {
		return otherIden;
	}

	public void setOtherIden(String otherIden) {
		this.otherIden = otherIden;
	}

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinTable(name = "iden_aidsIden", joinColumns = { @JoinColumn(name = "iden_id") }, inverseJoinColumns = { @JoinColumn(name = "aidsIden") })
	@ForeignKey(name = "FK_aidsIden_iden", inverseName = "FK_iden_aidsIden")
	public List<CodeValuePhi> getAidsIden() {
		return aidsIden;
	}

	public void setAidsIden(List<CodeValuePhi> aidsIden) {
		this.aidsIden = aidsIden;
	}

	@Column(name = "elseViewAids")
	public String getElseViewAids() {
		return elseViewAids;
	}

	public void setElseViewAids(String elseViewAids) {
		this.elseViewAids = elseViewAids;
	}

	@Column(name = "elseHearingAids")
	public String getElseHearingAids() {
		return elseHearingAids;
	}

	public void setElseHearingAids(String elseHearingAids) {
		this.elseHearingAids = elseHearingAids;
	}

	@Column(name = "elseWalkingAids")
	public String getElseWalkingAids() {
		return elseWalkingAids;
	}

	public void setElseWalkingAids(String elseWalkingAids) {
		this.elseWalkingAids = elseWalkingAids;
	}

	@Column(name = "elseWhat")
	public String getElseWhat() {
		return elseWhat;
	}

	public void setElseWhat(String elseWhat) {
		this.elseWhat = elseWhat;
	}

	@Column(name = "elseWhom")
	public String getElseWhom() {
		return elseWhom;
	}

	public void setElseWhom(String elseWhom) {
		this.elseWhom = elseWhom;
	}

	@Column(name = "elseAids")
	public String getElseAids() {
		return elseAids;
	}

	public void setElseAids(String elseAids) {
		this.elseAids = elseAids;
	}
	@Column(name = "elseProvenance")
	public String getElseProvenance() {
		return elseProvenance;
	}

	public void setElseProvenance(String elseProvenance) {
		this.elseProvenance = elseProvenance;
	}
	
	//From PHI CI
	
	
	
	@Column(name = "otherHospital")
	public String getOtherHospital() {
		return otherHospital;
	}

	public void setOtherHospital(String otherHospital) {
		this.otherHospital = otherHospital;
	}

	@Column(name = "support", length = 2500)
	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	@Column(name = "infoForSup", length = 2500)
	public String getInfoForSup() {
		return infoForSup;
	}
	
	@Column(name = "elseLang")
	public String getElseLang() {
		return elseLang;
	}

	public void setElseLang(String elseLang) {
		this.elseLang = elseLang;
	}
	public void setInfoForSup(String infoForSup) {
		this.infoForSup = infoForSup;
	}

	/**
	*  javadoc for contact
	*/
	private List<Contact> contact;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="identification", cascade=CascadeType.PERSIST)
	public List<Contact> getContact() {
		return contact;
	}

	public void setContact(List<Contact>list){
		contact = list;
	}

	public void addContact(Contact contact) {
		if (this.contact == null) {
			this.contact = new ArrayList<Contact>();
		}
		// add the association
		if(!this.contact.contains(contact)) {
			this.contact.add(contact);
			// make the inverse link
			contact.setIdentification(this);
		}
	}

	public void removeContact(Contact contact) {
		if (this.contact == null) {
			this.contact = new ArrayList<Contact>();
			return;
		}
		//add the association
		if(this.contact.contains(contact)){
			this.contact.remove(contact);
			//make the inverse link
			contact.setIdentification(null);
		}
	}
	
	//From Act:
	
	private Date availabilityTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="availability_time")
	public Date getAvailabilityTime() {
		return availabilityTime;
	}

	public void setAvailabilityTime(Date availabilityTime) {
		this.availabilityTime = availabilityTime;
	}
	
	
	private Boolean paceMaker = false;

	@Column(name="pacemaker")
	public Boolean getPaceMaker(){
		return paceMaker;
	}

	public void setPaceMaker(Boolean paceMaker){
		this.paceMaker = paceMaker;
	}
	
}