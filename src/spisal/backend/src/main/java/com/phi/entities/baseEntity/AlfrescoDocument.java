package com.phi.entities.baseEntity;

import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;

import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.entities.baseEntity.Destinatari;

import com.phi.entities.dataTypes.CodeValue;
@javax.persistence.Entity
@Table(name = "alfresco_document")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class AlfrescoDocument extends BaseEntity implements LocatedEntity {

	private static final long serialVersionUID = 1314193199L;
	
	/**
	*  javadoc for nrUpload
	*/
	private Integer nrUpload;

	@Column(name="nr_upload")
	public Integer getNrUpload(){
		return nrUpload;
	}

	public void setNrUpload(Integer nrUpload){
		this.nrUpload = nrUpload;
	}
	
	/**
	*  javadoc for typeArpav
	*/
	private CodeValuePhi typeArpav;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="typeArpav")
	@ForeignKey(name="FK_AlfrescoDocument_typeArpav")
	@Index(name="IX_AlfrescoDocument_typeArpav")
	public CodeValuePhi getTypeArpav(){
		return typeArpav;
	}

	public void setTypeArpav(CodeValuePhi typeArpav){
		this.typeArpav = typeArpav;
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

	/**
	*  javadoc for oggetto
	*/
	private String oggetto;

	@Column(name="oggetto" , length=2000)
	public String getOggetto(){
		return oggetto;
	}

	public void setOggetto(String oggetto){
		this.oggetto = oggetto;
	}


	/**
	*  javadoc for destinatari
	*/
	private List<Destinatari> destinatari;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="AlfrescoDocument_id")
	@ForeignKey(name="FK_dstinatar_AlfrscoDocumnt")
	//@Index(name="IX_dstinatar_AlfrscoDocumnt")
	public List<Destinatari> getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(List<Destinatari>list){
		destinatari = list;
	}


	/**
	*  javadoc for preventDelete
	*/
	private Boolean preventDelete;

	@Column(name="prevent_delete")
	public Boolean getPreventDelete(){
		return preventDelete;
	}

	public void setPreventDelete(Boolean preventDelete){
		this.preventDelete = preventDelete;
	}

	/**
	*  javadoc for fromTemplate
	*/
	private Boolean fromTemplate;

	@Column(name="from_template")
	public Boolean getFromTemplate(){
		return fromTemplate;
	}

	public void setFromTemplate(Boolean fromTemplate){
		this.fromTemplate = fromTemplate;
	}

	/**
	*  javadoc for nProtocollo
	*/
	private String nProtocollo;

	@Column(name="n_protocollo")
	public String getNProtocollo(){
		return nProtocollo;
	}

	public void setNProtocollo(String nProtocollo){
		this.nProtocollo = nProtocollo;
	}

	/**
	*  javadoc for lockedForSignature
	*/
	private Boolean lockedForSignature;

	@Column(name="locked_for_signature")
	public Boolean getLockedForSignature(){
		return lockedForSignature;
	}

	public void setLockedForSignature(Boolean lockedForSignature){
		this.lockedForSignature = lockedForSignature;
	}


	/**
	*  javadoc for signedDocumentLatest
	*/
	private SignedDocument signedDocumentLatest;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="signed_document_latest_id")
	@ForeignKey(name="FK_lfrscDcmnt_sgndDcmntLtst")
	//@Index(name="IX_lfrscDcmnt_sgndDcmntLtst")
	public SignedDocument getSignedDocumentLatest(){
		return signedDocumentLatest;
	}

	public void setSignedDocumentLatest(SignedDocument signedDocumentLatest){
		this.signedDocumentLatest = signedDocumentLatest;
	}



	/**
	*  javadoc for signedDocument
	*/
	private List<SignedDocument> signedDocument;

	@OneToMany(fetch=FetchType.LAZY, mappedBy="alfrescoDocument", cascade=CascadeType.PERSIST)
	public List<SignedDocument> getSignedDocument() {
		return signedDocument;
	}

	public void setSignedDocument(List<SignedDocument>list){
		signedDocument = list;
	}

	public void addSignedDocument(SignedDocument signedDocument) {
		if (this.signedDocument == null) {
			this.signedDocument = new ArrayList<SignedDocument>();
		}
		// add the association
		if(!this.signedDocument.contains(signedDocument)) {
			this.signedDocument.add(signedDocument);
			// make the inverse link
			signedDocument.setAlfrescoDocument(this);
		}
	}

	public void removeSignedDocument(SignedDocument signedDocument) {
		if (this.signedDocument == null) {
			this.signedDocument = new ArrayList<SignedDocument>();
			return;
		}
		//add the association
		if(this.signedDocument.contains(signedDocument)){
			this.signedDocument.remove(signedDocument);
			//make the inverse link
			signedDocument.setAlfrescoDocument(null);
		}
	}


	/**
	*  javadoc for signaturesPresent
	*/
	private Integer signaturesPresent;

	@Column(name="signatures_present")
	public Integer getSignaturesPresent(){
		return signaturesPresent;
	}

	public void setSignaturesPresent(Integer signaturesPresent){
		this.signaturesPresent = signaturesPresent;
	}

	/**
	*  javadoc for signaturesReqN
	*/
	private Integer signaturesReqN;

	@Column(name="signatures_req_n")
	public Integer getSignaturesReqN(){
		return signaturesReqN;
	}

	public void setSignaturesReqN(Integer signaturesReqN){
		this.signaturesReqN = signaturesReqN;
	}





	




	/**
	*  javadoc for documentStatus
	*/
	private CodeValuePhi documentStatus;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="documentStatus")
	@ForeignKey(name="FK_AlfrescoDocument_documentStatus")
	//@Index(name="IX_AlfrescoDocument_documentStatus")
	public CodeValuePhi getDocumentStatus(){
		
		return documentStatus;
	}

	public void setDocumentStatus(CodeValuePhi documentStatus){
		this.documentStatus = documentStatus;
	}

	
	/**
	*  javadoc for lastChange
	*/
	private Date lastChange;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="last_change")
	public Date getLastChange(){
		return lastChange;
	}

	public void setLastChange(Date lastChange){
		this.lastChange = lastChange;
	}

	/**
	*  javadoc for isSelected
	*/
	private Boolean isSelected;

	@Column(name="is_selected")
	public Boolean getIsSelected(){
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected){
		this.isSelected = isSelected;
	}

	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AlfrescoDocument_sequence")
	@SequenceGenerator(name = "AlfrescoDocument_sequence", sequenceName = "AlfrescoDocument_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	/**
	*  javadoc for serviceDeliveryLocation
	*/
	private ServiceDeliveryLocation serviceDeliveryLocation;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="service_delivery_location_id")
	@ForeignKey(name="FK_lfrscDcmnt_srvcDlvryLctn")
	//@Index(name="IX_lfrscDcmnt_srvcDlvryLctn")
	public ServiceDeliveryLocation getServiceDeliveryLocation(){
		return serviceDeliveryLocation;
	}

	public void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation){
		this.serviceDeliveryLocation = serviceDeliveryLocation;
	}
	
	/**
	*  javadoc for isTemplate
	*/
	private Boolean isTemplate;

	@Column(name="is_template")
	public Boolean getIsTemplate(){
		return isTemplate;
	}

	public void setIsTemplate(Boolean isTemplate){
		this.isTemplate = isTemplate;
	}
	

	/**
	*  javadoc for type
	*/
	private CodeValuePhi type;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type")
	@ForeignKey(name="FK_AlfrescoDocument_type")
	//@Index(name="IX_AlfrescoDocument_type")
	public CodeValuePhi getType(){
		return type;
	}

	public void setType(CodeValuePhi type){
		this.type = type;
	}

	/**
	*  javadoc for mimeType
	*/
	private String mimeType;

	@Column(name="mime_type")
	public String getMimeType(){
		return mimeType;
	}

	public void setMimeType(String mimeType){
		this.mimeType = mimeType;
	}

	/**
	*  javadoc for description
	*/
	private String description;

	@Column(name="description")
	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}
	
	/**
	*  javadoc for name
	*/
	private String name;

	@Column(name="name")
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	/**
	*  javadoc for path
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
	*  javadoc for nodeRef
	*/
	private String nodeRef;

	@Column(name="node_ref")
	public String getNodeRef(){
		return nodeRef;
	}

	public void setNodeRef(String nodeRef){
		this.nodeRef = nodeRef;
	}
	
	@Transient
	public String getNodeRefUrl(){
		if (nodeRef != null) {
			return nodeRef.replace("://", "/");
		} else {
			return null;
		}
	}

}
