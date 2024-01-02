package com.phi.entities.baseEntity;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.envers.Audited;
import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.OneToMany;
import javax.persistence.ManyToOne;
import javax.persistence.ManyToMany;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.IndexColumn;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.envers.AuditJoinTable;
import com.phi.entities.role.Employee;
@javax.persistence.Entity
@Table(name = "tariffario")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Audited
public class Tariffario extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4387114505723436302L;

	/**
	*  javadoc for dataRifEnd
	*/
	private Date dataRifEnd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_rif_end")
	public Date getDataRifEnd(){
		return dataRifEnd;
	}

	public void setDataRifEnd(Date dataRifEnd){
		this.dataRifEnd = dataRifEnd;
	}


	/**
	*  javadoc for auth
	*/
	private Employee auth;

	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name="auth_id")
	@ForeignKey(name="FK_Tariffario_auth")
	//@Index(name="IX_Tariffario_auth")
	public Employee getAuth(){
		return auth;
	}

	public void setAuth(Employee auth){
		this.auth = auth;
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
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ConfGene_sequence")
	@SequenceGenerator(name = "ConfGene_sequence", sequenceName = "ConfGene_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}
	@Override
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

}
