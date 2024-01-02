package com.phi.util;

import java.io.Serializable;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ProgettoAssociatoCounter")
@Scope(ScopeType.SESSION)
public class ProgettoAssociatoCounter implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3167319705455892149L;
	private String counter;
	
	/**
	 * Initialize default role GUEST, useful to execute processes without login
	 */
    @Create
    public void init() {
    	counter = "";
    }
	
	public String getCounter() {
		return counter;
	}
	
	public void setCounter(String counter) {
		this.counter = counter;
	}

}