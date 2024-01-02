package com.phi.cs.catalog.adapter.nestedset;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.annotations.Parent;

import com.phi.entities.role.ServiceDeliveryLocation;

@Embeddable
public class ServiceDeliveryLocationNestedSetNodeInfo extends NestedSetNodeInfo<ServiceDeliveryLocation> {
	
    public ServiceDeliveryLocationNestedSetNodeInfo() {
    	super();
    }
	
    public ServiceDeliveryLocationNestedSetNodeInfo(ServiceDeliveryLocation owner) {
    	super(owner);
    }

    public ServiceDeliveryLocationNestedSetNodeInfo(ServiceDeliveryLocationNestedSetNodeInfo original) {
    	super(original);
    }
	
	@Parent
	public ServiceDeliveryLocation getOwner() {
		return owner;
	}

	private void setOwner(ServiceDeliveryLocation owner) {
		this.owner = owner;
	}

	@Column(name = "NS_THREAD", nullable = false, updatable = false)
	public Long getNsThread() {
		return nsThread;
	}

	public void setNsThread(Long nsThread) {
		this.nsThread = nsThread;
	}

	@Column(name = "NS_LEFT", nullable = false, updatable = false)
	public Long getNsLeft() {
		return nsLeft;
	}

	public void setNsLeft(Long nsLeft) {
		this.nsLeft = nsLeft;
	}

	@Column(name = "NS_RIGHT", nullable = false, updatable = false)
	public Long getNsRight() {
		return nsRight;
	}

	public void setNsRight(Long nsRight) {
		this.nsRight = nsRight;
	}

}
