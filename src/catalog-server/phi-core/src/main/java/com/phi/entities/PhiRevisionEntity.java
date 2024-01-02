package com.phi.entities;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import com.phi.cs.catalog.adapter.PhiRevisionListener;

@Entity
@RevisionEntity(PhiRevisionListener.class)
@Table(name="revinfo")
public class PhiRevisionEntity implements Serializable {

	private static final long serialVersionUID = 4056059470021035855L;

	private int id;

	private long timestamp;
	
	private Date revisionDate;
	
	private String username;
	
	//true when the selected entity is the current revision of the entity.
	private boolean current;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "revinfo_sequence")
    @SequenceGenerator(name = "revinfo_sequence", sequenceName = "revinfo_sequence")
	@RevisionNumber
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@RevisionTimestamp
	@Column(name = "timestampDB")
	public long getTimestamp() {
		return this.timestamp;
	}
	
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		this.revisionDate = new Date(timestamp);
	}

	@Transient
	public Date getRevisionDate() {
		return revisionDate;
	}

	@Transient
	public boolean getCurrent() {
		return current;
	}
	
	public void setCurrent (boolean val) {
		this.current = val;
	}
	
	@Column(name = "username")
	public String getUsername() { 
		return username; 
	}
	
	public void setUsername(String username) {
		this.username = username; 
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + id;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhiRevisionEntity other = (PhiRevisionEntity) obj;
		if (id != other.id)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return DateFormat.getDateTimeInstance().format(getRevisionDate()) + " by " + username; 
	}
}