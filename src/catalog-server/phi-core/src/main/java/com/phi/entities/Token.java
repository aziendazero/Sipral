package com.phi.entities;

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

import org.hibernate.envers.Audited;

@Entity
@Table(name="token")
@Audited
public class Token implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8227106774330197567L;
	
	String value;
	String userName;
	Date expirationTime=new Date();

	private long internalId;
	
	@Column(name="value")
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	@Column(name="userName")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
    @Column(name="expiration_time")
	public Date getExpirationTime() {
		return expirationTime;
	}
	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "token_sequence")
    @SequenceGenerator(name = "token_sequence", sequenceName = "token_sequence")
	@Column(name = "internal_id")
	public long getInternalId() {
		return internalId;
	}

	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((expirationTime == null) ? 0 : expirationTime.hashCode());
		result = prime * result + (int) (internalId ^ (internalId >>> 32));
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		Token other = (Token) obj;
		if (expirationTime == null) {
			if (other.expirationTime != null)
				return false;
		} else if (!expirationTime.equals(other.expirationTime))
			return false;
		if (internalId != other.internalId)
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
}
