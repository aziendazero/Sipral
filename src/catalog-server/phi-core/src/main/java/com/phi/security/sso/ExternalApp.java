package com.phi.security.sso;

import java.io.IOException;
import java.util.List;

import javax.ejb.Local;
import javax.faces.model.SelectItem;

import com.phi.cs.exception.PhiException;
import com.phi.entities.Application;

/**
 * A utility class to invoke an external app
 * @author francesco.rossi
 *
 */
@Local
public interface ExternalApp {

	/**
	 * invoke an external phi app creating a token
	 * @param url the url to invoke 
	 * @param application the application code: to retrieve ip and port for the server
	 * @throws IOException
	 */
	public void invoke(String url,Application application) throws IOException,PhiException;
	
	/**
	 * given userBean setting, it invokes the application selected during 
	 * login procedure
	 */
	public void invokeStatic(String application) throws IOException,PhiException;
	
	/**
	 * given userBean setting, it return  the application url to invoke after
	 * application selection during option phase
	 * @return 
	 */
	public String getUrlStatic(String application,boolean logout) throws IOException,PhiException;
	public List<SelectItem> getApplications();
	
	/**
	 * Generate a complete url to login to an app given previous parameters 
	 * inject via set& get (application and doLogout)
	 * @return an url to an external App with credential and configurations
	 * @throws IOException
	 * @throws PhiException
	 */
	public String getUrlStatic() throws IOException,PhiException;
	public String getApplication() ;
	/**
	 * the application name that must be used to form the http url
	 * @param application
	 */
	public void setApplication(String application) ;
	/**
	 * set if after generating an url you also have to logout
	 * @return true/false
	 */
	public boolean isDoLogout() ;

	public void setDoLogout(boolean doLogout);
	
	public String getResult() ;
	
}
