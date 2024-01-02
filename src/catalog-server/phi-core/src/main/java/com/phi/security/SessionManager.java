package com.phi.security;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Statistics;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.security.Identity;

import com.phi.entities.actions.EmployeeAction;



@Name("SessionManager")
@Scope(ScopeType.APPLICATION)
@Startup
public class SessionManager {


	/**
	 * Session manager collects information about Session/Conversation of each users.
	 * The object are accessible from each hashmap.
	 * The hashmap are maintained properly by AccessControl/ProcessManager/ViewManager/SessionCollectionListner.. classes
	 * 
	 */
	private static final Logger log = Logger.getLogger(SessionManager.class);
	private HashMap<String, String> userJsess = new HashMap<String, String>();
	private HashMap<String, HttpSession> jsessHttpSession = new HashMap<String, HttpSession>();
	private HashMap<String,Context> userSession = new HashMap<String,Context>();
	private HashMap<String,Context> userConversation = new HashMap<String,Context>();
	private HashMap<String,Identity> userIdentity = new HashMap<String,Identity>();

	private HashMap<String,List<Integer>> userCid = new HashMap<String, List<Integer>>();

	/**
	 * Main method to trace session Contexts, jsessionId and Identity instaces at application level.
	 * The method needs 
	 */
	public void updateSession (String user, String jsess, Context session, Identity identity) {
		if (user == null || user.isEmpty()) { 
			log.error ("Empty or null username: unable to update SessionManager info");
			return;
		}

		setUserSession(user, session);
		setUserIdentity(user, identity);
		updateUserJsess(user, jsess);
		if (session == null) {
			userCid.remove(user);
			userConversation.remove(user);
		}
	}

	public void updateHttpSession (String jsess, HttpSession sess) {
		if (sess == null)
			jsessHttpSession.remove(jsess);

		else 
			jsessHttpSession.put(jsess,sess);
	}

	public void killSession (String user) {
		if (userIdentity.containsKey(user)) {
			Identity id = userIdentity.get(user);
			if (id.isLoggedIn()) {
				id.unAuthenticate();
				if(userJsess.containsKey(user)){
					String sName = userJsess.get(user);
					HttpSession s = jsessHttpSession.get(sName);
					s.invalidate();
				}
			}
		}
	}
	
	public void killSessionById(String jSession) {
		HttpSession s = jsessHttpSession.get(jSession);
		s.invalidate();
	}

	public List<Object[]> getSessions() {
		List<Object[]> ret = new ArrayList<Object[]>();
		for (String user : userSession.keySet()) {
			Object[] el = new Object[6];
			el[0] = user;
			el[1] = userJsess.get(user);
			el[2] = userSession.get(user);
			el[3] = userConversation.get(user);
			el[4] = userIdentity.get(user);
			el[5] = userCid.get(user);
			ret.add(el);
		}
		return ret;
	}

	public int getSessionsSize() {
		return userSession.size();
	}

	public List<Object[]> getHttpSessions() {
		List<Object[]> ret = new ArrayList<Object[]>();
		for (String jsess : jsessHttpSession.keySet()) {
			Object[] el = new Object[3];
			el[0] = jsess;
			el[1] = getUserByJsess(jsess);
			el[2] = jsessHttpSession.get(jsess);
			ret.add(el);
		}

		return ret;
	}

	public long getSize(Object o) { 
		if (o == null)
			return -1;
		long l = -1;
		//http://www.javamex.com/classmexer/
		//l =MemoryUtil.deepMemoryUsageOf(o);
		//long l =instrumentation.getObjectSize(o);
		return l;
	}



	/**
	 * Method to maintain cid stack history per user.
	 */
	public void updateConversation (String user, Integer cid, Context conv) {
		if (user == null || user.isEmpty()) {
			log.error ("Empty or null username: unable to update conversation");
			return;
		}

		if (conv == null) {
			userConversation.remove(user);
			userCid.remove(user);
			return;
		}

		List<Integer> l;
		if (!userCid.containsKey(user)) {
			l = new ArrayList<Integer>();
			userCid.put(user, l);
		}
		else {
			l = userCid.get(user);
		}

		l.add(cid);
		userConversation.put(user, conv);
	}



	private void setUserSession (String user, Context session) {
		if (session == null) 
			userSession.remove(user);
		else
			userSession.put(user, session);
	}

	public Context getSession (String user) {
		return userSession.get(user);
	}

	private void setUserIdentity (String user, Identity id) {
		if (id == null)
			userIdentity.remove(user);
		else 
			userIdentity.put(user, id);
	}

	public Identity getIdentity (String user) {
		return userIdentity.get(user);
	}


	private void updateUserJsess (String user, String jsess) {

		if (user == null || user.isEmpty()) {
			log.error("Unable to update session for null user "+jsess);
			return;
		}

		if (jsess == null || jsess.isEmpty()) {
			log.info("removed session for user "+user);
			userJsess.remove(jsess);
			return;
		}

		String prvSess = userJsess.get(user);
		if (prvSess == null || prvSess.isEmpty()) {
			userJsess.put(user,jsess);
			log.info("created session "+jsess+ " for user "+user);
			return;
		}

		userJsess.put(user,jsess);
		if (prvSess.equals(jsess)) {
			log.info("maintained session "+jsess+ " for user "+user);
		}
		else {
			log.info("updated session "+jsess+ " for user "+user+ " (previous session: "+prvSess+" )");
		}
	}



	public String getJsessByUser (String username) {
		if (username == null || username.isEmpty())
			return null;

		if (userJsess.containsKey(username)){
			return userJsess.get(username);
		}

		return null;
	}

	public String getUserByJsess (String jsess) {
		if (jsess == null || jsess.isEmpty()) 
			return null;
		
		if (userJsess.containsValue(jsess)) {
			for (Map.Entry<String, String> e : userJsess.entrySet()) {
			    if (e.getValue() == jsess) {
			    	return e.getKey();
			    }
			}
		}
		return null;
	}

	public static SessionManager instance() {
		return (SessionManager) Component.getInstance(SessionManager.class, ScopeType.APPLICATION);
	}

	//Stateful hashmap, can be used by any stateless bean to store stateful information.
	//Currently used by PerformanceLogger
	private HashMap<String,Object> timerStat = new HashMap<String, Object>();

	public HashMap<String, Object> getTimerStat() {
		return timerStat;
	}

	public void setTimerStat(HashMap<String, Object> timerSTat) {
		this.timerStat = timerSTat;
	}


	//List of user to be relogged. 
	//the force relogging is used from AdminConsole, to set a list of user which will be automatically relogged 
	//when they will go home, or ends a main process.
	//The user relogging system is useful for maintenance.
	//
	//See BaseAccessControl reloginUsers methods and checkExecuteRelogin.
	//
	private List<String> userToBeRelogged;


	public List<String> getUserToBeRelogged() { 
		return userToBeRelogged;
	}

	public void setUserToBeRelogged(List<String> userToBeRelogged) {
		this.userToBeRelogged = userToBeRelogged;
	}

	//use a conversation variable, EmployeeAction.temporary['logGrep'], to store grepped log. 
	public String getLogGrep() {
		EmployeeAction empa = EmployeeAction.instance();
		return (String)empa.getTemporary().get("logGrep");
	}

	public void clearLogGrep() {
		EmployeeAction empa = EmployeeAction.instance(); 
		empa.getTemporary().put("logGrep", null);
		empa.getTemporary().put("grep", null);
	}

	public void getLogGrep(String word) {

		String logGrep = "";
		String serverLogPath = "";
		
		//SSA applications use phi.deploy.dir (defined in /conf/custom.properties)
		//because deploy-apps dir is not inside jboss.server.home.dir
		if(System.getProperty("phi.deploy.dir")!=null){
			serverLogPath = System.getProperty("phi.deploy.dir")+File.separator+"log"+File.separator+"server.log";
		}else{
			serverLogPath = System.getProperty("jboss.server.home.dir")+File.separator+"log"+File.separator+"server.log";
		}	

		File serverLogFile = new File(serverLogPath);
		if (!serverLogFile.exists()) {
			logGrep = "server.log not found: "+serverLogFile.getAbsolutePath();
			return;
		}

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(serverLogFile));
			String line;
			StringBuffer greppedLines= new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				if (line.contains(word))
					greppedLines.append(line+"\n");
			}
			logGrep = greppedLines.toString();

			EmployeeAction empa = EmployeeAction.instance();
			empa.getTemporary().put("logGrep", logGrep);
		} 
		catch (FileNotFoundException e) {
			log.error("error finding server.log file");
		} catch (IOException e) {
			log.error("error reading server.log file");
		}
		finally {
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {
					log.error("error closing server.log buggeredReader");
				}
		}

	}

	public List<String[]> getCacheData(){
		List<String[]> out = new ArrayList<String[]>();

		CacheManager manager = CacheManager.getInstance();

		String[] cacheNames = manager.getCacheNames();

		if(cacheNames!=null && cacheNames.length>0){
			for(String name : cacheNames){
				String[] str = new String[4];

				Cache cache = manager.getCache(name);
				str[0] = name;
				Statistics stats = cache.getStatistics();
				if(stats!=null){
					str[1] = stats.getCacheHits() + " CacheHits";
					str[2] = stats.getCacheMisses() + " CacheMisses";
					str[3] = stats.getObjectCount() + " ObjectCount";
				}
				out.add(str);
			}
		}

		return out;		
	}

	public void clearCache(String name){
		CacheManager manager = CacheManager.getInstance();
		if(name!=null && manager.cacheExists(name)){
			manager.getCache(name).removeAll();
		}
	}
}
