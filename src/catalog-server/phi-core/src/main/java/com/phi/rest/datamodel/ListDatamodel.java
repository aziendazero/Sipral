package com.phi.rest.datamodel;

import java.util.List;

public class ListDatamodel<T> {
	
	private List<T> entities;
	
	private String next;
	
	private String prev;
	
	
	public ListDatamodel(List<T> entities, String readUrl, Integer readPageSize, Integer page ) {
		
		if (readPageSize != null && page != 0) { //page == 0 -> not paginated 
		    if (entities != null && entities.size() > readPageSize) {
		    	next = readUrl + (page+1);
		    	entities.remove(readPageSize.intValue());
		    }
		    if (page>1) {
		    	prev = readUrl + (page-1);
		    }
		}
	    this.entities = entities; 
	}

	public List<T> getEntities() {
		return entities;
	}
	
//	public void setEntities(List<T> entities) {
//		this.entities = entities;
//	}
	
	public String getNext() {
		return next;
	}
	
//	public void setNext(String next) {
//		this.next = next;
//	}
	
	public String getPrev() {
		return prev;
	}
	
//	public void setPrev(String prev) {
//		this.prev = prev;
//	}
	
}