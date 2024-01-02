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


import java.util.ArrayList;
import java.util.List;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.baseEntity.Fattura;
@javax.persistence.Entity
@Table(name = "file_acc_enti")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class FileAccEnti extends BaseEntity {

	private static final long serialVersionUID = 68424920L;

	/**
	*  javadoc for csv
	*/
	private Boolean csv;

	@Column(name="csv")
	public Boolean getCsv(){
		return csv;
	}

	public void setCsv(Boolean csv){
		this.csv = csv;
	}


	/**
	*  javadoc for fattura
	*/
	private Fattura fattura;

	@OneToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="fattura_id")
	@ForeignKey(name="FK_FileAccEnti_fattura")
	//@Index(name="IX_FileAccEnti_fattura")
	public Fattura getFattura(){
		return fattura;
	}

	public void setFattura(Fattura fattura){
		this.fattura = fattura;
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
	
	
	//methods needed for baseEntity implementation
	
	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "FileAccEnti_sequence")
	@SequenceGenerator(name = "FileAccEnti_sequence", sequenceName = "FileAccEnti_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
