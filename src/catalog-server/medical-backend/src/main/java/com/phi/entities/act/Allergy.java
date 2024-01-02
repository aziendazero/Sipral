package com.phi.entities.act;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import org.hibernate.envers.NotAudited;

import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;

@Entity
@Table(name = "allergy")
@Audited
public class Allergy extends AuditedEntity {

	private static final long serialVersionUID = 18767868765L;

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Allergy_sequence")
	@SequenceGenerator(name = "Allergy_sequence", sequenceName = "Allergy_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/*@Override
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="serviceDeliveryLocation")
	@ForeignKey(name="FK_Allergy_sdloc")
	@Index(name="IX_Allergy_sdloc")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}

	@Override
	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation=serviceDeliveryLocation;
	}*/
	
	//property for PS
	private String description;
	private CodeValue category;
	private Date appearanceDate;
	//private PatientEncounter patientEncounter; //removed, implemented in Observation class

	//property coming from PHI_CI
	private CodeValue adverseReactionLevelCode;
	//private CodeValue genericAllergenCode;
	private CodeValue drugAllergenCode;

	private Patient patient;

	@Column(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String param) {
		this.description = param;
	}

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="category_code")
	@ForeignKey(name="FK_Allergy_category_cv")
	@Index(name="IX_Allergy_category_cv")
	public CodeValue getCategory() {
		return category;
	}

	public void setCategory(CodeValue param) {
		this.category = param;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="appearance_date")
	public Date getAppearanceDate() {
		return appearanceDate;
	}

	public void setAppearanceDate(Date param) {
		this.appearanceDate = param;
	}

	/**
	 *  javadoc for author
	 */
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="author_id")
	@ForeignKey(name="FK_Allergy_author")
	@Index(name="IX_Allergy_author")
	public Employee getAuthor(){
		return author;
	}

	public void setAuthor(Employee author){
		this.author = author;
	}


	/**
	 *  javadoc for authorRole
	 */
	@Override
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="authorRole")
	@ForeignKey(name="FK_Allergy_authorRole")
	@Index(name="IX_Allergy_authorRole")
	public CodeValueRole getAuthorRole(){
		return authorRole;
	}
	@Override
	public void setAuthorRole(CodeValueRole authorRole){
		this.authorRole = authorRole;
	}

	/**
	 *  javadoc for adverseReactionLevelCode
	 */

	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="adverse_reaction_level_code")
	@ForeignKey(name="FK_Allergy_adverseRLC")
	@Index(name="IX_Allergy_adverseRLC")
	public CodeValue getAdverseReactionLevelCode(){
		return adverseReactionLevelCode;
	}

	public void setAdverseReactionLevelCode(CodeValue adverseReactionLevelCode){
		this.adverseReactionLevelCode = adverseReactionLevelCode;
	}

	/**
	 *  javadoc for genericAllergenCode
	 */


	/*@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValueGeneric.class)
	@JoinColumn(name="generic_allergen_code")
	@ForeignKey(name="FK_Allergy_genericAC")
	@Index(name="IX_Allergy_genericAC")
	public CodeValue getGenericAllergenCode(){
		return genericAllergenCode;
	}

	public void setGenericAllergenCode(CodeValue genericAllergenCode){
		this.genericAllergenCode = genericAllergenCode;
	}*/

	/**
	 *  javadoc for drugAllergenCode
	 */


	@ManyToOne(fetch=FetchType.LAZY, targetEntity=CodeValuePhi.class)
	@JoinColumn(name="drug_allergen_code")
	@ForeignKey(name="FK_Allergy_drugAC")
	@Index(name="IX_Allergy_drugAC")
	public CodeValue getDrugAllergenCode(){
		return drugAllergenCode;
	}

	public void setDrugAllergenCode(CodeValue drugAllergenCode){
		this.drugAllergenCode = drugAllergenCode;
	}

	/**
	 *  javadoc for patient
	 */

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name="FK_Allergy_patient")
	@Index(name="IX_Allergy_patient")
	//@NotAudited
	public Patient getPatient(){
		return patient;
	}

	public void setPatient(Patient patient){
		this.patient = patient;
	}

	private Boolean confirmed;



	@Column(name="confirmed")
	public Boolean getConfirmed() {
		return confirmed;
	}
	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

	private String allergyGeneric;

	@Column(name="allergyGeneric")
	public String getAllergyGeneric() {
		return allergyGeneric;
	}

	public void setAllergyGeneric(String allergyGeneric) {
		this.allergyGeneric = allergyGeneric;
	}


	/**
	 *  javadoc for cancelled_by
	 */
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="cancelled_by_id")
	@ForeignKey(name="FK_Allergy_cancelled_by")
	@Index(name="IX_Allergy_cancelled_by")
	public Employee getCancelledBy(){
		return cancelledBy;
	}

	public void setCancelledBy(Employee cancelledBy){
		this.cancelledBy = cancelledBy;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cancelled_by_role")
	@ForeignKey(name="FK_llrgy_cnclld_by_rl")
	@Index(name="IX_llrgy_cnclld_by_rl")
	public CodeValueRole getCancelledByRole(){
		return cancelledByRole;
	}

	public void setCancelledByRole(CodeValueRole cancelledByRole){
		this.cancelledByRole = cancelledByRole;
	}

	public Boolean isNoAllergy;

	@Column(name="is_noAllergy")
	public Boolean getIsNoAllergy() {
		return isNoAllergy;
	}

	public void setIsNoAllergy(Boolean isNoAllergy) {
		this.isNoAllergy = isNoAllergy;
	}

	private String note;

	@Column(name="note", length = 2500)
	public String getNote() {
		return note;
	}

	public void setNote(String note) {		
		this.note = note;
	}

}