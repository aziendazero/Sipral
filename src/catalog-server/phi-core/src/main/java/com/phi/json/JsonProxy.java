package com.phi.json;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using=JsonProxyDeserializer.class)
public class JsonProxy
{	
	private long internalId;
	private String entityName;
		
	public JsonProxy(long id, String name) {
		internalId = id;
		entityName = name;
	}
	
	public long getInternalId() {
		return internalId;
	}
	
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	
	public String getEntityName() {
		return entityName;
	}
	
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

}