package com.phi.security.sso;



import com.phi.cs.exception.PhiException;
import com.phi.entities.Application;
import com.phi.entities.Token;
import com.phi.security.UserBean;
import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Event bean to invoke other app than the current one
 * @author francesco.rossi
 *
 */
@Name("externalAppCaller")
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Stateless
public class ExternalAppCaller implements ExternalApp {

	private static final Logger log = Logger.getLogger(ExternalAppCaller.class);

	public static final String SDLOC_ID_SELECTION_QUERY = "SELECT DISTINCT(ids.extension) from ServiceDeliveryLocation sdLoc JOIN sdLoc.id ids where sdLoc.internalId in  (:ids) and ids.root='HBS'";
	private static final String TOKEN2 = "?token=";
	private static final String USERNAME = "&username=";
	private static final String ROLE_CODE = "&roleCode=";
	private static final String SD_LOC_ID="&sdlLoginList=";
	//	@PersistenceContext(unitName="zzzCATALOG_SERVER.jar#rimEntityManagerPDM2")
	@PersistenceContext(unitName="rimEntityManagerPDM2")
	protected EntityManager em;

	private String application;
	private String result;
	private boolean doLogout;

	public void invoke(String url,Application appToCall) throws IOException, PhiException{
		String urlToInvoke=generateUrl(url, appToCall);
		FacesContext.getCurrentInstance().getExternalContext().redirect(urlToInvoke);

		logout();
		return;
	}

	private void logout() {
		Identity identity = Identity.instance();
		Credentials credentials = identity.getCredentials();
		credentials.setUsername(null); 
		credentials.setPassword(null);
		identity.logout();
	}

	public String generateUrl(String url, Application appToCall)
			throws PhiException {
		//create token
		TokenGenerator tokenGenerator= new UUIDTokenGenerator();
		String tokenValue= tokenGenerator.getToken();
		//save token
		Token token = new Token();
		token.setValue(tokenValue);
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
		token.setExpirationTime(cal.getTime());
		//get url
		//Application2 appToCall=em.find(Application2.class,application );
		if (appToCall==null){
			throw new PhiException("Url not found", "NOT FOUND");
		}

		//userBean.get
		UserBean userBean= UserBean.instance();
		token.setUserName(userBean.getUsername());
		em.persist(token);
		em.flush();

		//create tokenized url
		StringBuffer buffer= new StringBuffer();
		buffer.append(appToCall.getServer()).append(url!=null?url:appToCall.getUrl()).append(TOKEN2).
		append(tokenValue).append(USERNAME).append(userBean.getUsername()).
		append(ROLE_CODE).append(userBean.getRole());
		//build sdloc list, so that sdloc selection should not be repeated
		List<Long> sdLocList= userBean.getSdLocs();
		if (sdLocList!=null && !sdLocList.isEmpty()){
			buffer.append(SD_LOC_ID);
			javax.persistence.Query sdLocNameListQuery=em.createQuery(SDLOC_ID_SELECTION_QUERY);
			sdLocNameListQuery.setParameter("ids", sdLocList);
			List<String> result=sdLocNameListQuery.getResultList();
			//concatenate sdloc name
			int i=0;
			for(String sdLocName:result){
				buffer.append(sdLocName);
				if(i<result.size()-1){
					buffer.append(";");}
				i++;
			}
		}

		//add language selection
		buffer.append("&lang=").append(LocaleSelector.instance().getLanguage());
		log.debug(buffer.toString());
		return buffer.toString();
	}

	@Override
	public List<SelectItem> getApplications() {
		List<String> applicationList=em.createQuery("SELECT a.application FROM Application a").getResultList();
		List<SelectItem> selectItemList= new ArrayList<SelectItem>(applicationList.size());
		for (String application : applicationList){
			selectItemList.add(new SelectItem(application, application));
		}
		return selectItemList;
	}

	@Override
	public void invokeStatic(String application) throws IOException, PhiException {

		if (application !=null && !application.isEmpty()){
			this.invoke(null, (Application)em.createQuery("SELECT a FROM Application a WHERE a.application=:application")
					.setParameter("application", application)
					.getSingleResult());
		}
		else {
			this.invoke(null, UserBean.instance().getApplication());
		}
	}

	@Override
	public String getUrlStatic(String application,boolean logout) throws IOException,
	PhiException {
		String returnUrl;
		if (application !=null && !application.isEmpty()){
			returnUrl=this.generateUrl(null, (Application)em.createQuery("SELECT a FROM Application a WHERE a.application=:application")
					.setParameter("application", application)
					.getSingleResult());
		}
		else {
			returnUrl=this.generateUrl(null, UserBean.instance().getApplication());
		}
		if(logout){
			logout();
		}
		return returnUrl;
	}

	public String getUrlStatic() throws IOException,
	PhiException {
		this.result= this.getUrlStatic(this.application,this.doLogout);
		return this.result;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public boolean isDoLogout() {
		return doLogout;
	}

	public void setDoLogout(boolean doLogout) {
		this.doLogout = doLogout;
	}

	@Override
	public String getResult() {
		// TODO Auto-generated method stub
		return result;
	}


}
