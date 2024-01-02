package com.phi.entities.baseEntity;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;
import org.hibernate.envers.Audited;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.role.Employee;
@javax.persistence.Entity
@Table(name = "signed_document")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class SignedDocument extends BaseEntity {

	private static final long serialVersionUID = 1516816408L;


	/**
	*  javadoc for signer
	*/
	private List<Employee> signer;

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="SignedDocument_id")
	@ForeignKey(name="FK_signer_SignedDocument")
	//@Index(name="IX_signer_SignedDocument")
	public List<Employee> getSigner() {
		return signer;
	}

	public void setSigner(List<Employee>list){
		signer = list;
	}


	/**
	*  javadoc for testo
	*/
	private String testo;

	@Column(name="testo" , length=2000)
	public String getTesto(){
		return testo;
	}

	public void setTesto(String testo){
		this.testo = testo;
	}

	/**
	*  javadoc for dataInvioProtocollo
	*/
	private Date dataInvioProtocollo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_invio_protocollo")
	public Date getDataInvioProtocollo(){
		return dataInvioProtocollo;
	}

	public void setDataInvioProtocollo(Date dataInvioProtocollo){
		this.dataInvioProtocollo = dataInvioProtocollo;
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
	*  javadoc for isSigned
	*/
	private Boolean isSigned;

	@Column(name="is_signed")
	public Boolean getIsSigned(){
		return isSigned;
	}

	public void setIsSigned(Boolean isSigned){
		this.isSigned = isSigned;
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
	*  javadoc for dataProtocollo
	*/
	private Date dataProtocollo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_protocollo")
	public Date getDataProtocollo(){
		return dataProtocollo;
	}

	public void setDataProtocollo(Date dataProtocollo){
		this.dataProtocollo = dataProtocollo;
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


	/**
	*  javadoc for alfrescoDocument
	*/
	private AlfrescoDocument alfrescoDocument;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="alfresco_document_id")
	@ForeignKey(name="FK_SgndDocumnt_alfrscDcumnt")
	//@Index(name="IX_SgndDocumnt_alfrscDcumnt")
	public AlfrescoDocument getAlfrescoDocument(){
		return alfrescoDocument;
	}

	public void setAlfrescoDocument(AlfrescoDocument alfrescoDocument){
		this.alfrescoDocument = alfrescoDocument;
	}

	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SignedDocument_sequence")
	@SequenceGenerator(name = "SignedDocument_sequence", sequenceName = "SignedDocument_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
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
