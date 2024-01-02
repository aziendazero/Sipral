package com.phi.ps;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.Redirect;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.Locker;
import com.phi.cs.util.CsConstants;
import com.phi.cs.view.banner.Banner;
import com.phi.events.PhiEvent;
import com.phi.security.AccessControlAction;
import com.phi.security.SessionManager;
import com.phi.security.UserBean;

/**
 * Stores current viewId: current xhtml page to display
 */

@Name("ViewManager")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class ViewManager implements Serializable {

	private static final long serialVersionUID = -1220016251474919715L;
	private static final Logger processManagerImplLog = Logger.getLogger(ProcessManagerImpl.class);

	private static String formExtension = ".xhtml";

	private String viewId = "home_main_container.xhtml";
	private String viewIdWoExt = CsConstants.HOME_LBL;

	private String popupViewId = "empty.xhtml";
	private String popupViewIdWoExt = "empty";

	private HashMap <String, Boolean> existingForm = new HashMap<String, Boolean>();

	boolean atHome = true;

	public void cleanPopup () {
		popupViewId = "empty.xhtml";
		popupViewIdWoExt = "empty";
	}
	
	public void setViewId(String viewId) throws PhiException {

		if (viewId.contains("/CORE/")){
			Context app = Contexts.getApplicationContext();	
			String customer = (String)app.get(CsConstants.CUSTOMER);

			if (customer != null && !customer.isEmpty()) {
				String customerViewId = viewId.replace("/CORE/", "/customer_"+customer+"/");
				String solution = (String)app.get(CsConstants.SOLUTION_NAME);
				String earPath = (String)app.get(CsConstants.CATALOG_SERVER_EAR_PATH);

				if (!existingForm.containsKey(customerViewId)) {
					//read form and check if exists.
					File f = new File (earPath+solution+".war"+customerViewId+".xhtml"); 
					existingForm.put(customerViewId, f.exists());
				}
				if ( existingForm.get(customerViewId)) {
					//customer form exists.
					viewId=customerViewId;
				}
			}
		}

		this.viewId = viewId + formExtension;
		this.viewIdWoExt = viewId;

		//popupViewId = "empty.xhtml";
		//popupViewIdWoExt = "empty";
		cleanPopup();

		Conversation conversation = Conversation.instance();

		UserBean ub = UserBean.instance();

		if(viewIdWoExt.equalsIgnoreCase(CsConstants.HOME_LBL)){
			//Going to home...
			try {
				this.viewId = "home_main_container.xhtml";
				atHome = true;

				TreeBean.instance().setProcessPath(null);

				Locker.instance().unlockAll(ub.getUsername());
				
				Context conversationContext = Contexts.getConversationContext();
				String[] convNames = conversationContext.getNames();
				if(convNames!=null){
					for(String name : convNames){
						Object obj = conversationContext.get(name);
						if(!"ProcessManagerImpl".equals(name) && obj.getClass().getName().startsWith("com.phi")){
							conversationContext.remove(name);
						}
					}
				}
				CatalogAdapter catalogAdapter = CatalogPersistenceManagerImpl.instance();
				if (catalogAdapter!=null) {
					catalogAdapter.remove();
				}
				conversation.leave();
				
				conversation.begin();
				conversation.changeFlushMode(FlushModeType.MANUAL);
				conversation.setDescription("HOME"); 
				conversation.killAllOthers();
				SessionManager.instance().updateConversation(UserBean.instance().getUsername(), Integer.parseInt(conversation.getId()), Contexts.getConversationContext());
				Banner banner = Banner.instance();
				banner.refresh();
				
				processManagerImplLog.info("[cid="+conversation.getId()+"] Going to home directly.");

			} catch (Exception e) {
				throw new PhiException("Error Going to home ", e, ErrorConstants.PROCESS_EXECUTION_ERR_CODE );
			}

		} else { 
			atHome = false;
			processManagerImplLog.info("[cid="+conversation.getId()+"] loading form "+viewId+formExtension); 

		}

		Events.instance().raiseEvent(PhiEvent.VIEWCHANGED, viewId);		
	}

	public String getViewId() {
		return viewId;
	}

	public String getViewIdWithoutExtension() {
		return viewIdWoExt;
	}

	void setHome() {
		this.viewId = "home_main_container.xhtml";
		viewIdWoExt =  CsConstants.HOME_LBL;
		atHome = true;
	}

	public void goHome() throws PhiException {
		
		AccessControlAction.instance().checkExecuteRelogin();
		
		//not executed code if relogged. Otherwise go home normally.
		setViewId(CsConstants.HOME_LBL);
	}

	public boolean isHome() {
		return atHome;
	}

	public String getPopupViewId() {
		return popupViewId;
	}

	public void setPopupViewId(String popupViewId) {
		this.popupViewId = popupViewId + formExtension;
		this.popupViewIdWoExt = popupViewId;
	}

	public String getPopupViewIdWoExt() {
		return popupViewIdWoExt;
	}

	public static ViewManager instance() {
		return (ViewManager) Component.getInstance("ViewManager", ScopeType.SESSION);
	}

	//used by change role process to redirect to home, and force the reload of entire page 
	//needed to reload process panel.
	public void redirectTo(String view) throws PhiException {
		
		goHome();
		Redirect redirect = Redirect.instance();
		redirect.setViewId(view);  //e.g. home.seam
		redirect.execute(); 
	}

}