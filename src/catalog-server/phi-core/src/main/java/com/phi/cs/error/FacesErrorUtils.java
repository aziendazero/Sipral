package com.phi.cs.error;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.StaleObjectStateException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Locale;
import org.jboss.seam.faces.FacesMessages;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.PhiRevisionEntity;



/** * Utility class for JavaServer Faces.
 * 
 * @author Davide Magni
 * @version 1.0
 */




@BypassInterceptors
@Name("FacesErrorUtils")
@Scope(ScopeType.EVENT)
public class FacesErrorUtils {

	private static final String bundleName = "bundle.error.messages";

	public static List<String> errorMessages = new ArrayList();
		
	public static String getMessage(String key) {
		ResourceBundle resBndl = ResourceBundle.getBundle(bundleName, Locale.instance(), Thread.currentThread().getContextClassLoader());

		try {
			return resBndl.getString(key.toString());
		} catch (MissingResourceException mre) {
			return key.toString();
		}
	}

	//
	//	/**
	//	 * Get value of locale.
	//	 * 
	//	 * @return the locale value
	//	 */
	//	public static Locale getLocale() {
	//		try{
	//			return org.jboss.seam.core.Locale.instance();
	//		}catch(Exception e){
	//			e.printStackTrace();
	//			return null;
	//		}
	//	}
	//
	//	/**
	//	 * Get servlet context.
	//	 * 
	//	 * @return the servlet context
	//	 */
	//	public static ServletContext getServletContext() {
	//		return (ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext();
	//	}
	//	
	//	/**
	//	 * Get managed bean based on the bean name.
	//	 * 
	//	 * @param beanName the bean name
	//	 * @return the managed bean associated with the bean name
	//	 */
	//	public static Object getManagedBean(String beanName) {
	//		Object o = getValueBinding(getJsfEl(beanName)).getValue(FacesContext.getCurrentInstance());
	//		
	//		return o;
	//	}  
	//	
	//	/**
	//	 * Remove the managed bean based on the bean name.
	//	 * 
	//	 * @param beanName the bean name of the managed bean to be removed
	//	 */
	//	public static void resetManagedBean(String beanName) {
	//		getValueBinding(getJsfEl(beanName)).setValue(FacesContext.getCurrentInstance(), null);
	//	}
	//	
	//	/**
	//	 * Store the managed bean inside the session scope.
	//	 * 
	//	 * @param beanName the name of the managed bean to be stored
	//	 * @param managedBean the managed bean to be stored
	//	 */
	//	public static void setManagedBeanInSession(String beanName, Object managedBean) {
	//		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(beanName, managedBean);
	//	}
	//	
	//	/**
	//	 * Get parameter value from request scope.
	//	 * 
	//	 * @param name the name of the parameter
	//	 * @return the parameter value
	//	 */
	//	public static String getRequestParameter(String name) {
	//		return (String)FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(name);
	//	}
	//	
	//	/**
	//	 * Add information message.
	//	 * 
	//	 * @param msg the information message
	//	 */
	//	public static void addInfoMessage(String msg) {
	//		addInfoMessage(null, msg);
	//	}
	//	
	//	/**
	//	 * Add information message to a sepcific client.
	//	 * 
	//	 * @param clientId the client id 
	//	 * @param msg the information message
	//	 */
	//	public static void addInfoMessage(String clientId, String msg) {
	//		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg));
	//	}
	//	
	//	/**
	//	 * Add error message.
	//	 * 
	//	 
	//	 */
	//	public static void addErrorMessage(String sumary, String detail) {
	//		addErrorMessage(null, sumary, detail);
	//	}
	//	
		/**
		 * Add error message to a specific client (used in SSA project).
		 * 
		 * @param clientId the client id 
		 */	
		public static void addErrorMessage(String clientId, String summary, String detail) {
			try{
				FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	//	
	//	/**
	//	 * Evaluate the integer value of a JSF expression.
	//	 * 
	//	 * @param el the JSF expression
	//	 * @return the integer value associated with the JSF expression
	//	 */
	//	public static Integer evalInt(String el) {
	//		if (el == null) {
	//			return null;
	//		}
	//		
	//		if (UIComponentTag.isValueReference(el)) {
	//			Object value = getElValue(el);
	//			
	//			if (value == null) {
	//				return null;
	//			}
	//			else if (value instanceof Integer) {
	//				return (Integer)value;
	//			}
	//			else {
	//				return new Integer(value.toString());
	//			}
	//		}
	//		else {
	//			return new Integer(el);
	//		}
	//	}
	//	
	//	private static Application getApplication() {
	//		ApplicationFactory appFactory = (ApplicationFactory)FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
	//		return appFactory.getApplication(); 
	//	}
	//	
	//	private static ValueBinding getValueBinding(String el) {
	//		return getApplication().createValueBinding(el);
	//	}
	//	
	//	private static Object getElValue(String el) {
	//		return getValueBinding(el).getValue(FacesContext.getCurrentInstance());
	//	}
	//	
	//	private static String getJsfEl(String value) {
	//		return "#{" + value + "}";
	//	}
	//
	//	public static PHIError getErrorMessage(Throwable exception) {
	//		PHIError pHIError = (PHIError) Component.getInstance("PHIError");
	//		try {
	//			ErrorManager.setLocale(getLocale());
	//			pHIError = ErrorManager.manageFrontEndError(exception);
	//			return pHIError;
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		return pHIError;
	//	}
	//	
	//	/**
	//	 * Read the Exception data and prepare the PHIError object with message loaded from bundle file 
	//	 * 
	//	 * @param exception - the exception received
	//	 * 
	//	 * @return PHIError object
	//	 */
	//	public static PHIError manageError(Throwable exception)   {		
	//			ErrorManager.setLocale(getLocale());
	//			PHIError phiError = ErrorManager.manageFrontEndError(exception);
	//
	//			return phiError;
	//	} 

	public static FacesMessage manageErrorMessage(String errKey)   {		

		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, Locale.instance(), Thread.currentThread().getContextClassLoader());

		FacesMessage errorMessage = new FacesMessage();
		errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);

		try {
			errorMessage.setDetail(bundle.getString("e"+errKey+"_cause"));
		} catch (MissingResourceException mre) {
			errorMessage.setDetail("No error found for key: e"+errKey+"_cause");
		}
		return errorMessage;
	} 

	public static String addErrorMessage(String errKey, String detail)   {		

		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, Locale.instance(), Thread.currentThread().getContextClassLoader());

		String msg;

		try {
			msg = bundle.getString("e"+errKey+"_description");
			FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, msg + " : "  + detail);
		} catch (MissingResourceException mre) {
			msg = "No error description found for key: e"+errKey+"_description";
			FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, msg);
		}
		
		errorMessages.add(msg);
		
		return msg;
	} 
	
	public static String addErrorMessage(String bundleKey)   {		

		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, Locale.instance(), Thread.currentThread().getContextClassLoader());

		String msg;

		try {
			msg = bundle.getString(bundleKey);
			FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, msg);
		} catch (MissingResourceException mre) {
			msg = "No error description found for key: " + bundleKey;
			FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, msg);
		}
		
		errorMessages.add(msg);
		
		return msg;
	} 
	
	/**
	 * Handles StaleObjectStateException and returns a proper message that explains
	 * which entity was locked and by which user.
	 */
	public String handleStaleObjectException(StaleObjectStateException e) {
		String rtn = getMessage("STALE_OBJECT_CAUSE");
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		String entityName = e.getEntityName();
		Serializable internalId = e.getIdentifier();
		Class<?> entityClass;
		try {
			entityClass = Class.forName(entityName);
			PhiRevisionEntity rev = ca.getLastRevision(entityClass, internalId);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			rtn = rtn.replace("{0}", entityName);
			rtn = rtn.replace("{1}", sdf.format(rev.getRevisionDate()));
			rtn = rtn.replace("{2}", rev.getUsername());
		} catch (Exception e1) {
			//log.error("Error handling StaleObjectException for entity "+entityName+"#"+internalId);
			rtn = getMessage("STALE_OBJECT_DESCRIPTION");
		}

		return rtn;
	}
	
	public static void claer()   {		

		FacesMessages.instance().clear();

	} 

	//	/**
	//	 * Read the Exception data and prepare the PHIError object with message loaded from bundle file and managed the pop-up eroor
	//	 * 
	//	 * @param exception - the exception received
	//	 * @return PHIError object
	//	 * 
	//	 * @deprecated because the pop-up error page is managed hard-coded in the lib and this is a drawbacks when the lib is change so a </br>
	//	 * fix is required
	//	 */
	//	public static void manageWarning(Throwable exception)   {
	//	
	//			ErrorManager.setLocale(getLocale());
	//			PHIError pHIError = ErrorManager.manageFrontEndError(exception);
	//			//FIXME: not used http error code and windows open cmd in the richfaces lib 
	//			HttpSession sess = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getSession();
	//			sess.setAttribute(ErrorConstants.PHI_ERROR_OBJECT_NAME, pHIError);
	//			try {
	//				((HttpServletResponse)FacesContext.getCurrentInstance().getExternalContext().getResponse()).sendError(409);
	//			} catch (IOException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//	}
	//
	public static String getUnauthorized() {

		ResourceBundle bundle = ResourceBundle.getBundle(bundleName, Locale.instance(), Thread.currentThread().getContextClassLoader());

		try {
			return bundle.getString("e"+ErrorConstants.SECURITY_NOT_AUTHORIZED_ERR_CODE+"_cause");
		} catch (MissingResourceException mre) {
			return "No error found for key: e"+ErrorConstants.SECURITY_NOT_AUTHORIZED_ERR_CODE+"_cause";
		}
	}

}
