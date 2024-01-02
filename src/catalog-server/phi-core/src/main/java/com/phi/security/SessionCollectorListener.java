package com.phi.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Events;

import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.exception.PersistenceException;


/**
 * Useful to implement one session x user
 * See: http://seamframework.org/Documentation/HowDoIExpireSessionsOutClusterWideInSeam
 * @author Alex Zupan
 * @modified Francesco Rossi
 *
 */
public class SessionCollectorListener implements /*ServletContextListener,*/ HttpSessionListener {

	private static final Logger log = Logger.getLogger(SessionCollectorListener.class);
	public static final String sessionDestroyedEvent= "sessionDestroyed";
	
	//    public void contextInitialized(ServletContextEvent e) {
	//    	log.info("Context initialized: " + e);
	//    }
	//
	//    public void contextDestroyed(ServletContextEvent e) {
	//    	log.info("Context destroyed: " + e);
	//    }

	public void sessionCreated(HttpSessionEvent e) {
		String ctxPath = e.getSession().getServletContext().getContextPath();
		String webModule = ctxPath.substring(1, ctxPath.length());
		String jsess = e.getSession().getId();
		Lifecycle.beginCall();
		SessionManager.instance().updateHttpSession(jsess, e.getSession());
		Lifecycle.endCall();
		log.info("Session created; session id = " + jsess + " for web module: " + webModule);
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		String sessId = event.getSession().getId();
		
		/* a similar to this should be placed into RimPdm2CatalogAdapter when before sctx.setRollbackOnly(), 
		  to access to sctx instead of leaving @Resource annotation to SessionContext sctx
		  Component.getInstance(RimPdm2CatalogAdapter.class) fail because SessionContext is not existing when called from here
		  */
		
		String userInfo="";
		String lockInfo="";
		String username="";
		boolean errorOnSessionId=false; 
		
		boolean appContActive = false;
		
		try {
			
			appContActive = Contexts.isEventContextActive(); 
			if (!appContActive) {  
                Lifecycle.beginCall();  
            } 
            //CatalogAdapter ca = (CatalogAdapter)Component.getInstance(RimPdm2CatalogAdapter.class);
			
			GenericAdapterLocalInterface ga = GenericAdapter.instance();// (GenericAdapterLocalInterface)ic.lookup("CATALOG_SERVER/GenericAdapter/local");
			HashMap<String, Object> parameters=new HashMap<String, Object>(2);
			parameters.put("sessId", sessId);
			List<?> res = ga.executeHQL("select internalId, username from Employee where sessionId = :sessId", parameters);
			if (res == null || res.isEmpty()) {
				userInfo = "No users seems to be logged in this session.";
			}
			else {
				//cleanup Employee Table, to see on admin console, the right number of authenticated user.  .setSessionId
				ga.executeUpdateHql("update Employee set sessionId = null, host = null where sessionId = :sessId", parameters);
				
				
				Collection<String> userResult=new HashSet<String>();
				for (Object l : res) {
					if (l!= null) {
						Object[] l2 = (Object[])l;
						userResult.add((String)l2[1]);
					}
				}
				
				if (res.size() != 1) {
					errorOnSessionId=true;
					userInfo = "Session was owned by multiple Users. ";
					for (Object l : res) {
						if (l!= null && !((String)l).isEmpty()) {
							String usr = (String)l;
							SessionManager.instance().updateSession(usr, null, null, null);
						}
					}
				}
				else {
					username = (String)((Object[])res.get(0))[1];
					userInfo = "Session was owned by user "+username+ " (internalId: "+((Object[])res.get(0))[0]+").";
					SessionManager.instance().updateSession(username, null, null, null);
				}
				
				parameters.clear();
				parameters.put("usernames", userResult);
				ga.executeUpdateHql("delete from UniqueCheck where userName in (:usernames)", parameters);
				lockInfo ="Locks associated to "+(res.size() == 1 ? "this user": "these users")+", if any, are deleted.";
				
				
				
			}
		} catch (PersistenceException e) {
			log.error("PersistenceException during reading session info / updating employee table, deleting locks after session expiration. Error Message:"+e.getMessage());
		} catch (NamingException e1) {
			log.error("Error accessing GenericAdapterLocalInterface via CATALOG_SERVER/GenericAdapter/local jndi name. Error Message:"+e1.getMessage());
		}
		finally {
			SessionManager sm = SessionManager.instance();
			
			//update httpSession
			sm.updateHttpSession(sessId, null);
			
			//if user should be relogged, and its session is completed, it does'nt need to be relogged. 
			List<String> usersToBeRelogged = sm.getUserToBeRelogged();
			if (usersToBeRelogged != null && usersToBeRelogged.contains(username)) {
				usersToBeRelogged.remove(username);
			}	
			
			int loggedSess = sm.getSessionsSize();
			if (!appContActive) {  
                Lifecycle.endCall();
            }
			String message = ("Session expired destroyed: session id = " + sessId +". "+ userInfo+ " "+ lockInfo+ " Server logged session count: "+loggedSess);
			if (errorOnSessionId)
				log.error(message);
			else
				log.info(message);
			
			
		}
		
		
		//Events does not exists!! TODO: add Lifecycle call.
		if (Events.exists()) {
			Events.instance().raiseEvent(sessionDestroyedEvent);
		}
		
	}
}