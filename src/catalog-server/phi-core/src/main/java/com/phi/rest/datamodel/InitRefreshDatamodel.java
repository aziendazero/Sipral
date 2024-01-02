package com.phi.rest.datamodel;

import java.util.List;
import java.util.Map;

/**
 * Generic Dashboard init and refresh bean.
 * Transformed into Json string by NurseActivityRest and other Rest methods for dashboard comunication
 * 
 * @author alex.zupan
 */

public class InitRefreshDatamodel<T> {
	
	// Main result datamodel
	private ListDatamodel<T> main;
	
	//Map of additional query results
	private Map<String, List<Map<String, Object>>> additional;
	
	//Map of dictionary domains
	private Map<String, List<Map<String, Object>>> dictionaries;
	
	public ListDatamodel<T> getMain() {
		return main;
	}

	public void setMain(ListDatamodel<T> main) {
		this.main = main;
	}
	
	public Map<String, List<Map<String, Object>>> getAdditional() {
		return additional;
	}

	public void setAdditional(Map<String, List<Map<String, Object>>> additional) {
		this.additional = additional;
	}

	public Map<String, List<Map<String, Object>>> getDictionaries() {
		return dictionaries;
	}

	public void setDictionaries(Map<String, List<Map<String, Object>>> dictionaries) {
		this.dictionaries = dictionaries;
	}

}