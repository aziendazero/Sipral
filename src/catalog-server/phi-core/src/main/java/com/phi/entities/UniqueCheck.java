package com.phi.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 * a simple class to store information about a rimObject to prevent it from duplication.
 * It is composed by two attribute: a string representing the object real world
 * "class" (e.g : a Patient, or a Doctor) and a string representing the id.  
 * @author rossi
 *
 */

@Entity
@IdClass(UniqueCheckId.class)
@Table(name = "unique_check")
public class UniqueCheck implements Serializable {

	private static final long serialVersionUID = -1426228526452454972L;
	
	private String root;
	private String extension;
	private Long version;
	private Date creationDate;
	private String userName;
	private String userRole;
	
	public UniqueCheck() {
	}

	public UniqueCheck(String root, String extension) {
		this.root = root;
		this.extension = extension;
	}

	@Id
	@Column(name="root")
	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		if (root ==null || root.isEmpty())
			throw new NullPointerException("Cannot set a unique checker with null root");
		this.root = root;
	}

	@Id
	@Column(name="extension")
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		if (extension ==null || extension.isEmpty())
			throw new NullPointerException("Cannot set a unique checker with null extension");
		this.extension = extension;
	}
	
	@Version
    @Column(name="version")
	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name="user_name")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
    @Column(name="user_role")
	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}


	public String toString(){
		return "["+this.root + ","+this.extension+"]";
	}
	
		
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((extension == null) ? 0 : extension.hashCode());
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((userRole == null) ? 0 : userRole.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final UniqueCheck other = (UniqueCheck) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (userRole == null) {
			if (other.userRole != null)
				return false;
		} else if (!userRole.equals(other.userRole))
			return false;
		return true;
	}

}