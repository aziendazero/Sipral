package com.phi.sign;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity

@Table(name = "signing_user_check")
public class SigningUserCheck implements Serializable{
	
	private static final long serialVersionUID = -1426228526452454977L;
	
	private long internalId;
	private Date creationDate;
	private long docId;
	private String userName;
	
	public SigningUserCheck() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "SigningUserCheck_sequence")
	@SequenceGenerator(name = "SigningUserCheck_sequence", sequenceName = "SigningUserCheck_sequence")
	@Column(name="internal_id")
	public Long getInternalId() {
		return internalId;
	}
	public void setInternalId(Long internalId){
		this.internalId= internalId;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	@Column(name="doc_id")
	public long getDocId() {
		return docId;
	}

	public void setDocId(long docId) {
		this.docId = docId;
	}
	
	@Column(name="user_name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
}
