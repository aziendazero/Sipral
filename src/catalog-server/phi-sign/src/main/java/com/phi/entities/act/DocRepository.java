package com.phi.entities.act;

import java.util.Date;

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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

//import org.apache.commons.lang.ArrayUtils;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CX;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.Employee;
import com.phi.entities.role.Patient;
import com.phi.entities.role.ServiceDeliveryLocation;
/**
 * <p>
 * Entity bean class to store documents related to patients. <br/>
 * The system will allow to provide the user with the previous versions of the document by using
 * the update tracking automatically managed by the Envers framework. 
 * </p>
 * <p>
 * PHI_DOC allows the user to digitally sign a document, replace it with a new version or deactivate
 * if by the substitution with a deactivation document. The deactivation document will be displayed 
 * instead of the invalidated document, and will be readonly.
 * The user will be able to choose if view in the list the deactivation documents or not [TODO]. 
 * </p>
 * <br/>
 * <p>
 * Fields introduced by the current version to the data model:
 * <ul>
 * <li>signature: digital signature
 * <li>versionNote: explanation of the updates which required the introduction of a new version
 * <li>isDeactivationDocument: signals that the document has been introduced to deactivate an invalid document
 * <li>patientId & encounterId: foreign key to the linked entities 
 * <li>applicationOwner: PHI_DOC code identifying the application which sent the document
 * <li>pdfFileContent: body of the physical document
 * <li>contentType: expresses the Mime type of the document
 * </ul>
 * Removed fields:
 * <ul>
 * <li>text
 * </ul>
 * </p>
 *
 */
@Entity
@Table(name = "docrepos")
@Audited
public class DocRepository extends BaseEntity implements LocatedEntity {
	
	private static final long serialVersionUID = 1084194008L;

	/**
	*  javadoc for signedBy
	*/
	private String signedBy;

	@Column(name="signed_by")
	public String getSignedBy(){
		return signedBy;
	}

	public void setSignedBy(String signedBy){
		this.signedBy = signedBy;
	}

	/**
	*  javadoc for versionSign
	*/
	private int versionSign;

	@Column(name="version_sign")
	public int getVersionSign(){
		return versionSign;
	}

	public void setVersionSign(int versionSign){
		this.versionSign = versionSign;
	}

	 //FIXME: used in amb and CI!
//	/**
//	*  javadoc for internalActivity
//	*/
//	private InternalActivity internalActivity;
//
//	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
//	@JoinColumn(name="internal_activity_id")
//	@ForeignKey(name="FK_DcRpsitry_intrnalActivty")
//	@Index(name="IX_DcRpsitry_intrnalActivty")
//	public InternalActivity getInternalActivity(){
//		return internalActivity;
//	}
//
//	public void setInternalActivity(InternalActivity internalActivity){
//		this.internalActivity = internalActivity;
//	}

	
	// ---------------------------------------
	// --- ABSTRACT METHODS IMPLEMENTATION ---
	// ---------------------------------------
	
	/**
	 * Unique ID inside PHI
	 */
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "Docrepos_sequence")
	@SequenceGenerator(name = "Docrepos_sequence", sequenceName = "Docrepos_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}

	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
    /**
     * Ambulatory where the document has been created in.
     */
	protected ServiceDeliveryLocation serviceDeliveryLocation;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "serv_del_loc")
	@ForeignKey(name = "FK_Docrepos_s")
	@Index(name = "IX_Docrepos_s")
	public ServiceDeliveryLocation getServiceDeliveryLocation() {
		return serviceDeliveryLocation;
	}


	public void setServiceDeliveryLocation(
			ServiceDeliveryLocation serviceDeliveryLocation) {
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}

	
	
	// ---------------------------
	// --- DOCUMENT PROPERTIES ---
	// ---------------------------
	
	/**
	 * Document date
	 */
	private Date docDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "doc_date")
	public Date getDocDate() {
		return docDate;
	}

	public void setDocDate(Date docDate) {
		this.docDate = docDate;
	}

	/**
	 * Title of the document
	 */
	private String title;

	@Column(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Document type code, values loaded from PHIDIC:PHIDOCDocumentType
	 */
	private CodeValuePhi code;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="code")
	@ForeignKey(name="FK_DocRepos_code")
	@Index(name="IX_DocRepos_code")	
	public CodeValuePhi getCode() {
		return code;
	}

	public void setCode(CodeValuePhi code) {
		this.code = code;
	}

	/**
	 * Free notes
	 */
	private String note;

	@Column(name = "note")
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note= note;
	}

	
	
	// --------------------
	// --- FOREIGN KEYS ---
	// --------------------
	
	/**
	 * Patient which the document is referred to
	 */
	private Patient patient;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="patient_id")
	@ForeignKey(name = "FK_Docrepos_pat")
	@Index(name="IX_Docrepos_pat")
	//@NotAudited
	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}	
	
	/**
	 * Encounter related to the patient which the document is referred to
	 */
	private PatientEncounter encounter;

	@ManyToOne(fetch = FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "patient_encounter")
	@ForeignKey(name = "FK_Docrepos_patenc")
	public PatientEncounter getEncounter() {
		return encounter;
	}

	public void setEncounter(PatientEncounter encounter) {
		this.encounter = encounter;
	}	
	
	
	
	// --------------------
	// --- FILE CONTENT ---
	// --------------------
	
	/**
	 * originalContent
	 */	
	private byte [] originalContent;
	
	@Lob
	@Column(name = "original_content")	
	public byte[] getOriginalContent() {
		return originalContent;
	}

	public void setOriginalContent(byte[] originalContent) {
//		this.originalContent = ArrayUtils.clone(originalContent);
		this.originalContent = originalContent;
	}
	
	/**
	 * content
	 */	
	private byte [] content;
	
	@Lob
	@Column(name = "content")	
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(byte[] content) {
//		this.content = ArrayUtils.clone(content);
		this.content =content;
	}
	
	/**
	 * contentType
	 */	
	private CodeValuePhi contentType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="content_type")
	@ForeignKey(name="FK_DocRepos_contentType")
	@Index(name="IX_DocRepos_contentType")
	public CodeValuePhi getContentType(){
		return contentType;
	}

	public void setContentType(CodeValuePhi contentType){
		this.contentType = contentType;
	}	
	
	/**
	 * Name of the document into fileSystem
	 */
	private String fileName;

	@Column(name = "file_name")
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	// FIXME READ ONLY FIELD - TO BE GENERATED ON THE CREATION AND NEVER MODIFIED 
	/**
	 * confidentiality
	 */	
	private CodeValuePhi confidentiality;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="confidentiality")
	@ForeignKey(name="FK_DocRepos_confid")
	@Index(name="IX_DocRepos_confid")
	public CodeValuePhi getConfidentiality(){
		return confidentiality;
	}

	public void setConfidentiality(CodeValuePhi confidentiality){
		this.confidentiality = confidentiality;
	}	
	
	
	
	// -----------------------
	// --- DOCUMENT ORIGIN ---
	// -----------------------
	
	/**
	*  ApplicationOwner
	*/
	private String applicationOwner;	
	
	@Column(name = "application_owner")	
	public String getApplicationOwner() {
		return applicationOwner;
	}

	public void setApplicationOwner(String applicationOwner) {
		this.applicationOwner = applicationOwner;
	}		
	// FIXME PHI_DOC DATA OF THE LOGGED USER
	
	
	/**
	 *  originatorCode
	 */
	private Employee originatorCode;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="originator_code_id")
	@ForeignKey(name="FK_DocRepos_org_code")
	@Index(name="IX_DocRepos_org_code")
	public Employee getOriginatorCode(){
		return originatorCode;
	}
	
	public void setOriginatorCode (Employee originatorCode){
		this.originatorCode = originatorCode;
	}	

	// DOCUMENT ID IN SENDER DB
	/**
	 * uniqueDocumentNumber
	 */	
	private String uniqueDocumentNumber;
	
	@Index(name="IX_DocRepos_unq_doc_num")
	@Column(name = "unique_doc_number")	
	public String getUniqueDocumentNumber() {
		return uniqueDocumentNumber;
	}

	public void setUniqueDocumentNumber(String uniqueDocumentNumber) {
		this.uniqueDocumentNumber = uniqueDocumentNumber;
	}
	// IL FIGLIO è IL SOSTITUTIVO DEL PADRE
	/**
	 * parentDocumentNumber
	 */	
	private String parentDocumentNumber;
	
	@Index(name="IX_DocRepos_par_doc_num")
	@Column(name = "parent_doc_number")	
	public String getParentDocumentNumber() {
		return parentDocumentNumber;
	}

	public void setParentDocumentNumber(String parentDocumentNumber) {
		this.parentDocumentNumber = parentDocumentNumber;
	}
	// NUMERO RICHIESTA LABORATORIO - TO BE IGNORED BY PHIDOC
	/**
	 * placerOrderNumber
	 */	
	private String placerOrderNumber;
    
	@Column(name = "placer_order_number")	
	public String getPlacerOrderNumber() {
		return placerOrderNumber;
	}

	public void setPlacerOrderNumber(String placerOrderNumber) {
		this.placerOrderNumber = placerOrderNumber;
	}    
	// AS ABOVE 
	/**
	 * fillerOrderNumber
	 */	
	private String fillerOrderNumber;
    
	@Column(name = "filler_order_number")	
	public String getFillerOrderNumber() {
		return fillerOrderNumber;
	}

	public void setFillerOrderNumber(String fillerOrderNumber) {
		this.fillerOrderNumber = fillerOrderNumber;
	}

	
	
	// -----------------------------------
	// --- VERSIONING AND DEACTIVATION ---
	// -----------------------------------
	
	/**
	 * Document version.
	 */
	private int docVersion;

	@Column(name = "doc_version")
	public int getDocVersion() {
		return docVersion;
	}

	public void setDocVersion(int docVersion) {
		this.docVersion = docVersion;
	}	
	
	/**
	 * reason for any replacement or cancellation applied on a signed document
	 */
	private String versionNote;

	@Column(name = "version_note")
	public String getVersionNote() {
		return versionNote;
	}

	public void setVersionNote(String versionNote) {
		this.versionNote = versionNote;
	}	
	
	/**
	 * completionStatus TXA-17
	 */	
	private CodeValuePhi completionStatus;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="completion_status")
	@ForeignKey(name="FK_DocRepos_complStatus")
	@Index(name="IX_DocRepos_complStatus")
	public CodeValuePhi getCompletionStatus(){
		return completionStatus;
	}

	public void setCompletionStatus(CodeValuePhi completionStatus){
		this.completionStatus = completionStatus;
	}		
	
	/**
	 * completionStatus TXA-19
	 */	
	private CodeValuePhi availabilityStatus;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="availability_status")
	@ForeignKey(name="FK_DocRepos_availStatus")
	@Index(name="IX_DocRepos_availStatus")
	public CodeValuePhi getAvailabilityStatus(){
		return availabilityStatus;
	}

	public void setAvailabilityStatus(CodeValuePhi availabilityStatus){
		this.availabilityStatus = availabilityStatus;
	}		
	
	
	// ----------------------
	// --- SIGNER DETAILS ---
	// ----------------------

	/**
	 * Sign Document date
	 */
	private Date signDocDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sign_doc_date")
	public Date getSignDocDate() {
		return signDocDate;
	}

	public void setSignDocDate(Date signDocDate) {
		this.signDocDate = signDocDate;
	}		
	
	/**
	*  assignedDocAuth
	*/
	private Employee assignedDocAuth;	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="assign_doc_auth_id")
	@ForeignKey(name="FK_DocRepos_adoc_auth")
	@Index(name="IX_DocRepos_adoc_auth")
	public Employee getAssignedDocAuth(){
		return assignedDocAuth;
	}
	
	public void setAssignedDocAuth (Employee assignedDocAuth){
		this.assignedDocAuth = assignedDocAuth;
	}	
	
	
	
	// ------------
	// --- MISC ---
	// ------------
	
	/**
	 * HL7 OBX.5 conclusion
	 */	
	private CodeValuePhi conclusion;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="conclusion")
	@ForeignKey(name="FK_DocRepos_conc")
	@Index(name="IX_DocRepos_conc")			
	public CodeValuePhi getConclusion() {
		return conclusion;
	}

	public void setConclusion(CodeValuePhi conclusion) {
		this.conclusion = conclusion;
	}
	
	/**
	 * pathology
	 */		
	private CodeValuePhi pathology;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="pathology")
	@ForeignKey(name="FK_DocRepos_pathgy")
	@Index(name="IX_DocRepos_pathgy")	
	public CodeValuePhi getPathology() {
		return pathology;
	}
	
	public void setPathology (CodeValuePhi pathology) {
		this.pathology = pathology;
	}

	
	/**
	 * HL7 OBX.5 outcome
	 */		
	private CodeValuePhi outcome;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="outcome")
	@ForeignKey(name="FK_DocRepos_outc")
	@Index(name="IX_DocRepos_out")	
	public CodeValuePhi getOutcome() {
		return outcome;
	}
	
	public void setOutcome(CodeValuePhi outcome) {
		this.outcome = outcome;
	}
	
	/**
	 * HL7 OBX.5 text
	 */		
	private String text;
	
	@Column(name = "text", length=2500)	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}	
	
	 /**
	 *  javadoc Courtesy Code HL7 PV1.22
	 *  is a number sequence
	 */	
	private String courtesyCode;	
	
	@Column(name="courtesy_code")
	public String getCourtesyCode() {
		return courtesyCode;
	}

	public void setCourtesyCode(String courtesyCode) {
		this.courtesyCode = courtesyCode;
	}
	
	 /**
	 *  javadoc Visit Number HL7 PV1.19
	 *  is a number sequence
	 */	
	private String visitNumber;	
	
	@Column(name="visit_number")
	public String getVisitNumber() {
		return visitNumber;
	}

	public void setVisitNumber(String visitNumber) {
		this.visitNumber = visitNumber;
	}	
	
	 /**
	 *  javadoc Alternate Visit ID HL7 PV1.50
	 */		
	private CX alternateVisitID;	
	
	@Embedded
	@AttributeOverrides({
	@AttributeOverride(name="id", column=@Column(name="alter_visit_id")),
	@AttributeOverride(name="ck", column=@Column(name="alter_visit_id_ck")),
	@AttributeOverride(name="itc", column=@Column(name="alter_visit_id_itc"))
	})
	public CX getAlternateVisitID(){
		return alternateVisitID;
	}	
	
	public void setAlternateVisitID (CX alternateVisitID){
		this.alternateVisitID = alternateVisitID;
	}		
	
	/**
	 * true when published to repository
	 */		
	private boolean publish;

	@Column(name="publish")	
	public boolean getPublish() {
		return publish;
	}

	public void setPublish(boolean publish) {
		this.publish = publish;
	}
	
	/**
	 * Last Publish Error
	 */		
	private String lastPublishError;

	@Column(name="last_publish_error", length=2500)	
	public String getLastPublishError() {
		return lastPublishError;
	}

	public void setLastPublishError(String lastPublishError) {
		this.lastPublishError = lastPublishError;
	}
	
	/**
	 * Published Date
	 */		
	private Date publishedDate;	
	
	@Temporal(TemporalType.TIMESTAMP)	
	@Column(name="published_date")	
	public Date getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(Date publishedDate) {
		this.publishedDate = publishedDate;
	}	
	
	/**
	 * Last Publish Error
	 */		
	private String procedureList;

	@Column(name="procedure_list", length=2500)	
	public String getProcedureList() {
		return procedureList;
	}

	public void setProcedureList(String procedureList) {
		this.procedureList = procedureList;
	}
	
	/**
	 * Flag for history exclusion
	 */
	private Boolean excludeHistory;

	@Column(name="exclude_hist")
	public Boolean getExcludeHistory() {
		return excludeHistory;
	}

	public void setExcludeHistory(Boolean excludeHistory) {
		this.excludeHistory = excludeHistory;
	}

	/**
	 * Discriminator for same report linked to multiple entities
	 */
	private String generatorEntityId;

	@Column(name="gen_entity_id")
	public String getGeneratorEntityId() {
		return generatorEntityId;
	}

	public void setGeneratorEntityId(String generatorEntityId) {
		this.generatorEntityId = generatorEntityId;
	}

	/**
	 * Document specialty code, values loaded from PHIDIC:VCOReportType
	 */
//	private CodeValuePhi docCode;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="doc_code")
//	@ForeignKey(name="FK_DocRepos_docCode")
//	@Index(name="IX_DocRepos_docCode")	
//	public CodeValuePhi getDocCode() {
//		return docCode;
//	}
//
//	public void setDocCode(CodeValuePhi docCode) {
//		this.docCode = docCode;
//	}

	/**
	 * Transfer discriminator
	 */
//	private Transfer transfer;
//
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="transfer")
//	@ForeignKey(name="FK_DocRepos_transfer")
//	@Index(name="IX_DocRepos_transfer")
//	public Transfer getTransfer() {
//		return transfer;
//	}
//
//	public void setTransfer(Transfer transfer) {
//		this.transfer = transfer;
//	}
	
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
	private Boolean availableToPatient = false;
	
	@Column(name = "avail_to_pat")
	public Boolean getAvailableToPatient() {
		return availableToPatient;
	}

	public void setAvailableToPatient(Boolean availableToPatient) {
		this.availableToPatient = availableToPatient;
	}
	private Boolean privateData = false;
	
	@Column(name = "priv_data")
	public Boolean getPrivateData() {
		return privateData;
	}

	public void setPrivateData(Boolean privateData) {
		this.privateData = privateData;
	}

	private Boolean fseObscuration = false;

	@Column(name = "fse_oscure")
	public Boolean getFseObscuration() {
		return fseObscuration;
	}

	public void setFseObscuration(Boolean fseObscuration) {
		this.fseObscuration = fseObscuration;
	}
	
	private String externalId;

	@Column(name="external_id")
	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
}
