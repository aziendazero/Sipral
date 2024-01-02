package com.phi.rest.datamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic Dashboard inject data model bean.
 * 
 * @author alex.zupan
 */

public class InjectDatamodel<T> {
	
	// Main result datamodel
	private T entity;
	
	//Banner entities
	private Map<String, Object> banner = new HashMap<String, Object>();
	private Map<String, Object> bannerEntities = new HashMap<String, Object>();

	//Map of additional query results
	private Map<String, List<Map<String, Object>>> additional;

	

	
	public T getEntity() {
		return entity;
	}

	public void setEntity(T jsonEntity) {
		this.entity = jsonEntity;
	}
	
	public Map<String, Object> getBanner() {
		return banner;
	}

	public void setBanner(Map<String, Object> banner) {
		this.banner = banner;
	}

	public Map<String, Object> getBannerEntities() {
		return bannerEntities;
	}

	public void setBannerEntities(Map<String, Object> bannerEntities) {
		this.bannerEntities = bannerEntities;
	}
	
	public Map<String, List<Map<String, Object>>> getAdditional() {
		return additional;
	}

	public void setAdditional(Map<String, List<Map<String, Object>>> additional) {
		this.additional = additional;
	}

}