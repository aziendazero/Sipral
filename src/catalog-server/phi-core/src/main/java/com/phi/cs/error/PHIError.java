package com.phi.cs.error;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.Locker;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("PHIError")
@Scope(ScopeType.CONVERSATION)
public class PHIError implements Serializable {

	private static final long serialVersionUID = 3131764899113045417L;
	
	private static final Logger log = Logger.getLogger(PHIError.class);
	
	protected ResourceBundle bundle = null;
	
	protected final String errorBundleName = "bundle.error.messages";
	
	protected Throwable exception;
	
	protected Long timeStamp ;
	protected String description;
	protected String cause;
	protected String action;
	
	@Create
	public void create() {
		bundle = ResourceBundle.getBundle(errorBundleName, new Locale(org.jboss.seam.core.Locale.instance().getLanguage()));
		exception = (Throwable)Component.getInstance("org.jboss.seam.caughtException");
		UserBean ub = UserBean.instance();
		try {
			Locker.instance().unlockAll(ub.getUsername());
		} catch (NamingException e) {
			log.error("Error unlocking resources locked by: " + ub.getUsername(), e);
		}

		if (exception != null) {
			fill(exception);
		}
	}
	
	public Long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public PhiException fill(Throwable exception) {
		
//		bundle = ResourceBundle.getBundle(errorBundleName, new Locale(org.jboss.seam.core.Locale.instance().getLanguage()));

		PhiException phiException = findPhiException(exception);
			
//		PHIError pHIError = PHIError.instance();

		setTimeStamp(Calendar.getInstance().getTimeInMillis());

		if (phiException != null) {
			setDescription(getViewErrDesc(phiException.getCode()) + " (" + bundle.getString("error") + " " + phiException.getCode() + ")");
			setCause(getViewErrCause(phiException.getCode()));
			setAction(getViewErrAction(phiException.getCode()));
		} else {
			setAction( bundle.getString("e102_action") ); // Call the customer service
		}
		//Removed exception message because isn't localized
//		Throwable lastEx = exception;
//		
//		while (lastEx != null && !lastEx.equals(lastEx.getCause())) {
//			
////			if (lastEx.getMessage() ) {
//				
//			String el = lastEx.getMessage();
//			if (el != null && !(el.contains(":")) ) {
//				if (el.contains("#{") && el.contains("}")) {
//				
//					el = el.substring(el.indexOf("#{") + 2);
//					el = el.substring(0, el.indexOf("}"));
//					
//					setDescription(el + (getDescription()==null ? "" :  " : " + getDescription()));
//					
//	//				}
//				} else/* if ( !(el.contains(":")) ) */{
//					setCause((getCause()==null ? "" : getCause()+" : ") + lastEx.getMessage()); 
//				}
//			}
//			lastEx = lastEx.getCause();
//		}

		return phiException;
	}
	
	private PhiException findPhiException(Throwable ex) {

		if (ex instanceof PhiException) {
			return (PhiException)ex;
		
		} else {
			if(ex.getCause()==null) {
				return null;
			} else {
				return findPhiException(ex.getCause());
			}
		}
	}
	
	/**
	 * This method retrives the "type" and the "side" error information from the bundle
     * 
     * @param code the error code
	 * @return the error description string
	 * @throws MissingResourceException if the bundle is missing
	 */
	public  String getViewErrType(String code) throws MissingResourceException {
		StringBuffer strBuf = new StringBuffer();
		try {
			strBuf.append(bundle.getString("e"+code+"_type"));
			strBuf.append(" ");
			strBuf.append(bundle.getString("error"));
			strBuf.append(" (");
			strBuf.append(bundle.getString("e"+code+"_side"));
			strBuf.append(")");
		} catch (MissingResourceException e) {
			strBuf.append(e.getMessage());
		}
		return strBuf.toString();	
	}

	/**
	 * This method retrives the "description" error information from the bundle
     * 
     * @param code the error code
	 * @return the error description string
	 * @throws MissingResourceException if the bundle is missing
	 */
	public String getViewErrDesc(String code) throws MissingResourceException {
		StringBuffer strBuf = new StringBuffer();
		try {
			strBuf.append(bundle.getString("e"+code+"_description"));
		} catch (MissingResourceException e) {
			strBuf.append(e.getMessage());
		}
		return strBuf.toString();	
	}

	/**
	 * This method retrives the "cause" error information from the bundle
     * 
     * @param code the error code
	 * @return the error cause string
	 * @throws MissingResourceException if the bundle is missing
	 */
	public String getViewErrCause(String code) throws MissingResourceException {
		StringBuffer strBuf = new StringBuffer();
		try{	
			strBuf.append(bundle.getString("e"+code+"_cause"));
		} catch (MissingResourceException e) {
			strBuf.append(e.getMessage());
		}
		return strBuf.toString();	
	}

    /**
     * This method retrives the "action" error information from the bundle
     * 
     * @param code the error code
     * @return the error action string
     * @throws MissingResourceException if the bundle is missing
     */
	public String getViewErrAction(String code) throws MissingResourceException {
		StringBuffer strBuf = new StringBuffer();
		try{
			strBuf.append(bundle.getString("e"+code+"_action"));
		} catch (MissingResourceException e) {
			strBuf.append(e.getMessage());
		}
		return strBuf.toString();	
	}
	
	public String getErrMessage(String code) throws MissingResourceException {
		StringBuffer strBuf = new StringBuffer();
		try{	
			strBuf.append(bundle.getString("e"+code+"_cause"));
		} catch (MissingResourceException e) {
			strBuf.append(e.getMessage());
		}
		return strBuf.toString();	
	}

    public static PHIError instance() {
        return (PHIError) Component.getInstance(PHIError.class, ScopeType.CONVERSATION);
    }
    
	@Override
	public String toString() {
		return "Description: " + description + "\n" +
				"Cause: " + cause + "\n" +
				"Action: " + action + "\n" +
				"TimeStamp: " + timeStamp;
	}
}