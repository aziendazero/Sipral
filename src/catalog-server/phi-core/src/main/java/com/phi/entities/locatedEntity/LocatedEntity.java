package com.phi.entities.locatedEntity;

import javax.persistence.Transient;

import com.phi.entities.role.ServiceDeliveryLocation;

/**
 * Adds serviceDeliveryLocation relation to BaseEntity
 * 
 * This relation was into BaseEntity, but since only 30 classes of 300+ uses the relation, was moved here.
 * 
 * Used by query meanager to add restriction to serviceDeliveryLocation based on logged sdlocs.
 * 
 * @author Alex Zupan
 */

public interface LocatedEntity {
	
	@Transient
	public abstract ServiceDeliveryLocation getServiceDeliveryLocation();

	public abstract void setServiceDeliveryLocation(ServiceDeliveryLocation serviceDeliveryLocation);
	
}
