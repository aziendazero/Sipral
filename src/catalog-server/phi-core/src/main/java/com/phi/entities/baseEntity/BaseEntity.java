package com.phi.entities.baseEntity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.contexts.Contexts;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.phi.entities.PhiRevisionEntity;
import com.phi.entities.actions.BaseAction;
import com.phi.json.JsonProxyGenerator;
import com.phi.security.UserBean;
/**
 * Super class for all Entities to be managed by {@link BaseAction}
 * @author francesco.rossi
 *
 */
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonIdentityInfo(generator=JsonProxyGenerator.class, property="proxyString", scope=BaseEntity.class)
public abstract class BaseEntity implements Serializable{

	private static final long serialVersionUID = -1448336177050078939L;
	private static HashMap<String, Class<BaseEntity>> derivedClasses = new HashMap<String, Class<BaseEntity>>();

	public static HashMap<String, Class<BaseEntity>> getDerivedClasses() {
		return derivedClasses;
	}
	
	@SuppressWarnings("unchecked")
	public BaseEntity() {
		Class<BaseEntity> derivedClass = (Class<BaseEntity>)this.getClass();
        if (!derivedClasses.containsKey(derivedClass.getSimpleName())) {
        	derivedClasses.put(derivedClass.getSimpleName(), derivedClass);
        }
        creationDate = new Date();

        if (Contexts.isSessionContextActive()) {
			UserBean ub = UserBean.instance();
			if (ub!=null && ub.getUsername() != null) {
				createdBy = ub.getUsername();
			} else {
				createdBy = "PHI-RE";
			}
        }
	}
	
	protected long internalId;

	@Transient
	public abstract long getInternalId();

	public abstract void setInternalId(long internalId);

	protected long version;

    @Version
    @Column(name="version")
	public long getVersion() {
		return version; 
	}
	
	public void setVersion(long value) {
		version = value; 
	}

	protected Date creationDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	protected String createdBy;
	
    @Column(name="created_by")
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
//  Moved to com.phi.entities.locatedEntity.LocatedEntity
//	protected ServiceDeliveryLocation serviceDeliveryLocation;
//	
//	@Transient
//	public abstract ServiceDeliveryLocation getServiceDeliveryLocation();
//
//	public abstract void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation);
	
	protected boolean isActive = true;
	
    @Column(name="is_active", nullable=false)
	public boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	protected PhiRevisionEntity revision;

	@Transient
	public PhiRevisionEntity getRevision() {
		return revision;
	}

	public void setRevision(PhiRevisionEntity revision) {
		this.revision = revision;
	}

	@Override
	public String toString() {
		return getClass() + " [internalId=" + internalId + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result
				+ ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + (int) (internalId ^ (internalId >>> 32));
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result
				+ ((revision == null) ? 0 : revision.hashCode());
		result = prime * result + (int) (version ^ (version >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != HibernateProxyHelper.getClassWithoutInitializingProxy(obj))
			return false;
		BaseEntity other = (BaseEntity) obj;
		
		//Added to grant proxy equals to object
		if (internalId != 0) {
			if (other.getInternalId() != 0) {
				if (internalId == other.getInternalId()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			if (other.getInternalId() != 0) {
				return false;
			}
		}

		//two objects without id check fields:
		
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (isActive != other.isActive)
			return false;
		if (revision == null) {
			if (other.revision != null)
				return false;
		} else if (!revision.equals(other.revision))
			return false;
		if (version != other.version)
			return false;
		return true;
	}
	
	private String enversLastChangeBy;
	private Date enversLastChangeDate;

	@Transient
	public String getEnversLastChangeBy() {
		return enversLastChangeBy;
	}

	public void setEnversLastChangeBy(String enversLastChangeBy) {
		this.enversLastChangeBy = enversLastChangeBy;
	}

	@Transient
	public Date getEnversLastChangeDate() {
		return enversLastChangeDate;
	}

	public void setEnversLastChangeDate(Date enversLastChangeDate) {
		this.enversLastChangeDate = enversLastChangeDate;
	}
	
	
	
}