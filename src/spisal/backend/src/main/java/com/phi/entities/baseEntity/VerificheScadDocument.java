package com.phi.entities.baseEntity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.Audited;
import org.hibernate.annotations.Index;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

@javax.persistence.Entity
@Table(name = "verif_scad_document")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class VerificheScadDocument extends BaseEntity {

	private static final long serialVersionUID = 287691718L;
	

	/**
	*  javadoc for descr
	*/
	private String descr;

	@Column(name="descr")
	public String getDescr(){
		return descr;
	}

	public void setDescr(String descr){
		this.descr = descr;
	}

	/**
	*  javadoc for dataRif
	*/
	private Date dataRif;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_rif")
	public Date getDataRif(){
		return dataRif;
	}

	public void setDataRif(Date dataRif){
		this.dataRif = dataRif;
	}
	
	/**
	*  javadoc for content
	*/
	private byte[] content;

	@Lob
	@Column(name="content")
	public byte[] getContent(){
		return content;
	}

	public void setContent(byte[] content){
		this.content = content;
	}
	
	/**
	*  javadoc for filename
	*/
	private String filename;

	@Column(name="filename")
	public String getFilename(){
		return filename;
	}

	public void setFilename(String filename){
		this.filename = filename;
	}

	/**
	*  javadoc for filesize
	*/
	private Integer filesize;

	@Column(name="filesize")
	public Integer getFilesize(){
		return filesize;
	}

	public void setFilesize(Integer filesize){
		this.filesize = filesize;
	}

	/**
	*  javadoc for contentType
	*/
	private String contentType;

	@Column(name="content_type")
	public String getContentType(){
		return contentType;
	}

	public void setContentType(String contentType){
		this.contentType = contentType;
	}
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "VerificheScadDocument_sequence")
	@SequenceGenerator(name = "VerificheScadDocument_sequence", sequenceName = "VerificheScadDocument_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}