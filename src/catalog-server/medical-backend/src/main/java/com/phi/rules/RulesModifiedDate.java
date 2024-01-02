package com.phi.rules;

import org.drools.KnowledgeBase;


/**
 *  This object is useful during the rules load process at the beginning of the application.
 *  The utility is to save the last modified attribute in order to be compared in case of one or more updates.  
 * 
 */
public class RulesModifiedDate {

	private long lastModified;
	private KnowledgeBase kbase;
	
	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public KnowledgeBase getKbase() {
		return kbase;
	}

	public void setKbase(KnowledgeBase kbase) {
		this.kbase = kbase;
	}
	
	public RulesModifiedDate(long lastModified,KnowledgeBase kbase){
		this.lastModified = lastModified;
		this.kbase = kbase;
	}
	
}
