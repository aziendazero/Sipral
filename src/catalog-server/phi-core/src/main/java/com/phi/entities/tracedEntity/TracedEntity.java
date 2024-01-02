package com.phi.entities.tracedEntity;

import java.util.Date;

import javax.persistence.Transient;

/**
 * Entities that implements TracedEntity have tracking attributes: 
 * createdByLocation : host or ip address of user who creates
 * modifiedBy : username of the Employee who have modified
 * modificationDate : date of last modify
 * modifiedByLocation : host or ip address of user who modified
 * 
 * @author Alex Zupan
 */

public interface TracedEntity {
	
	@Transient
	public long getVersion();
	
	@Transient
	public String getCreatedByLocation();
	
	public void setCreatedByLocation(String createdByLocation);

	@Transient
	public String getModifiedBy();
	
	public void setModifiedBy(String modifiedBy);
	
	@Transient
	public Date getModificationDate();

	public void setModificationDate(Date modificationDate);
	
	@Transient
	public String getModifiedByLocation();
	
	public void setModifiedByLocation(String modifiedByLocation);

}
