package com.phi.security;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Manager;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.web.ServletContexts;

import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.AccountException;
import com.phi.cs.lock.Locker;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.util.CsConstants;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.Application;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.IVL;
import com.phi.entities.role.Employee;
import com.phi.security.ldap.AccessControlManagerLDAP;
import com.phi.security.selectItem.EmployeeRoleSelectItem;

/**
 * The authenticate-method specified for identity in components.xml
 * 
 * PASSWORD is stored in </br>
 * Employee.password</br>
 * 
 * USERNAME is stored in </br>
 * Employee.username</br>
 */

public abstract class BaseAccessControlAction implements AccessControl,Serializable {

	private static final long serialVersionUID = -3017710759585733038L;
	private static final Logger log = Logger.getLogger(BaseAccessControlAction.class);

	protected EntityManager em;

	protected boolean checkEmployeeValidDates = true;

	protected boolean checkPasswordExpired = true;
	protected boolean checkUserExpired = false;
	
	//FIXME: move into a stateFul Bean
	protected boolean externalAuthentication = false;

	protected String oldPassword = null;

	protected String newPassword = null;

	protected String confirmPassword = null;
	
	public static final String logoutEvent= "loggedOut";
	
	// Change password event name
	public static final String eventChangePassword = "accessControl.changePassword";
	
	//	protected boolean useMD5password = false;

	//	protected boolean useLDAPauth = false;

	// login query
	protected static final String loginHql = "SELECT internalId, name.fam, name.giv, username, password, lastChangedPassword, effectiveTime, sessionId, lastAccessDate, loginCount FROM Employee emp " +
			"WHERE emp.username = :user " +
			"AND emp.password = :pass";
	
	protected static final String countLogins = "SELECT internalId, loginCount FROM Employee emp WHERE emp.username = :username ";

	// update loginCount
	protected static final String updateLoginCountHql = "UPDATE Employee set loginCount = :loginCount WHERE username = :username";

	// login query via LDAP
	protected static final String checkEmpExistHql = "SELECT internalId, name.fam, name.giv, username, password, lastChangedPassword, effectiveTime, sessionId, lastAccessDate FROM Employee emp " +
			"WHERE emp.username = :user ";


	protected static final String additionalRolesHql = "SELECT empRole.internalId, " +
			"case when roleCode.langIt is not null then roleCode.langIt else roleCode.displayName end as name, " +
			"roleCode.code, " +
			"roleCode.id, " +
			"empRole.isCoordinator, specialization.id " +
			"FROM EmployeeRole empRole " +
			"JOIN empRole.code roleCode " +
			"LEFT JOIN empRole.specialization specialization " +
			"WHERE empRole.employee.internalId = :employeeId " +
			"AND empRole.effectiveTime.low is not null "+
			"AND (empRole.effectiveTime.high is null OR empRole.effectiveTime.high > :today) " +
			"ORDER BY 2 ASC"; 

	protected static final String enabledSdlHql = "SELECT new javax.faces.model.SelectItem(enabledSdLoc.internalId, enabledSdLoc.name.giv) " +
			"FROM EmployeeRole empRole " +
			"JOIN empRole.enabledServiceDeliveryLocations enabledSdLoc " +
			"WHERE empRole.internalId = :internalId";
	
	//Same as previous but take sdloc from CodeValueRole.enabledServiceDeliveryLocations instead of EmployeeRole.enabledServiceDeliveryLocations
	protected static final String enabledSdlFromCondevalueRoleHql = "SELECT new javax.faces.model.SelectItem(enabledSdLoc.internalId, enabledSdLoc.name.giv) " +
			"FROM CodeValueRole cvRole " +
			"JOIN cvRole.enabledServiceDeliveryLocations enabledSdLoc " +
			"WHERE cvRole.id = :id";

	protected static final String enabledSdlChildrenHql = "SELECT enabledSdLoc.internalId" +
			" FROM EmployeeRole empRole " +
			" JOIN empRole.enabledServiceDeliveryLocations enabledSdLoc " +
			" WHERE empRole.internalId = :internalId" +
			" AND enabledSdLoc.children is empty";

	protected static final String enabledApplicationHql = "SELECT app " +
			"FROM EmployeeRole empRole " +
			"JOIN empRole.application app " +
			"WHERE empRole.internalId = :internalId " +
			"ORDER BY app.application ASC";

	protected static final String hqlSetSessionId = "UPDATE Employee set sessionId = :sessionId, host = :host, lastAccessDate = :lastAccessDate, loginCount = :loginCount WHERE internalId = :internalId";

	//Admin console query
	protected static final String hqlConnectedUsers = "SELECT internalId, username, sessionId, host FROM Employee WHERE sessionId IS NOT NULL and host is not null";
	
	protected static final String hqlConnectedHostUsers = "SELECT internalId, username, sessionId, host FROM Employee WHERE sessionId IS NOT NULL and host = :host";

	private static final String CACHE_MANAGER_OBJECT_NAME_SINGLETON ="jboss.web:host=localhost,type=Manager,path=/";

	/**
	 * Check if a user is logged in
	 * If a user is logged in and try to login a second time, if the second credentials are different from logged user, return alreadyLogged
	 * Used by pages.xml to avoid double logins
	 * 
	 * See: http://support.insielmercato.it/mantis/view.php?id=18526
	 */
	public Object isLoggedIn() {

		Identity i = Identity.instance();

		if (i.isLoggedIn()) {

			UserBean ub = UserBean.instance();
			String ubu = ub.getUsername();
			Credentials c = (Credentials)Component.getInstance("org.jboss.seam.security.credentials");
			String secondaryUser = c.getUsername();

//			EmployeeAction.instance().getTemporary().put("secondaryUser", secondaryUser);

			Context eventCtx = Contexts.getEventContext();
			eventCtx.set("secondaryUser", secondaryUser);
			
			c.clear();

			//if Credentials.username is different from UserBean.username, means that a user has attemped to login twice from the same host
			if (!ubu.equals(secondaryUser)) {
				return "alreadyLogged";
			} else {
				return true;
			}
		} else {
			return false;
		}

	}

	/**
	 * Authentication method configured in components.xml
	 * @return true if the the user is valid 
	 */
	@SuppressWarnings("unchecked")
	public boolean authenticate() {
		RepositoryManager rm = RepositoryManager.instance();
		//boolean useLDAPauth = false;
		this.checkPasswordExpired = true;
		//define a seam.property userExpireInDays (usually =180) to check if the user has expired
		if(rm.getUserExpireInDays()!=null){
			this.checkUserExpired = true;	
		}
		UserBean.instance().setUserExpired(false);
		((Session)em.getDelegate()).setFlushMode(FlushMode.MANUAL);

		Identity identity = Identity.instance();
		Credentials credentials = identity.getCredentials();



		try {
//			EmployeeAction eAction = EmployeeAction.instance();
//			Employee emp = eAction.getEntity();
			
			Employee emp = (Employee)Contexts.getConversationContext().get("Employee");

			Long employeeId;
			String nameFam;
			String nameGiv;
			String dbUsername;
			Date lastChangedPassword;
			IVL<Date> effectiveTime;
			String sessionId;
			Date lastAccessDate;
			Integer loginCount;

			
			if (emp==null){
				String username = credentials.getUsername();
				String password = credentials.getPassword();

				//Check not empty username or password
				//When login fails authenticate is called a second time with empty username and password
				if (username.isEmpty() || password.isEmpty()) {
					return false;
				}


				Object[] employee = doAuthentication(username, password, rm);
				
				if (employee == null) {
					return false;
				}

				employeeId = (Long)employee[0];
				nameFam = (String)employee[1];
				nameGiv = (String)employee[2];
				dbUsername = (String)employee[3];
				lastChangedPassword = (Date)employee[5];
				effectiveTime = (IVL)employee[6];
				sessionId = (String)employee[7];
				lastAccessDate = (Date)employee[8];
				loginCount = (Integer)employee[9];
				
			} else {
				employeeId = emp.getInternalId();
				nameFam = emp.getName().getFam();
				nameGiv = emp.getName().getGiv();
				dbUsername = emp.getUsername();
				lastChangedPassword = emp.getLastChangedPassword();
				effectiveTime = emp.getEffectiveTime();
				sessionId = emp.getSessionId();				
				lastAccessDate = emp.getLastAccessDate();
				loginCount = emp.getLoginCount();
			}
			
			//Check if the user is valid. Extend this class and override isValidUser to change valid user logic
			int validity = isValidUser(effectiveTime);
			if (validity!=0) {

				if(validity==-1)
					FacesErrorUtils.addErrorMessage(AccountException.NOT_YET_ACTIVE_ACCOUNT_MESSAGE);
				if(validity==1)
					FacesErrorUtils.addErrorMessage(AccountException.EXPIRED_ACCOUNT_MESSAGE);
				return false;
			}

			List<EmployeeRoleSelectItem> employeeRoles = findAdditionalRoles(employeeId);

			if (employeeRoles == null) {
				FacesErrorUtils.addErrorMessage(AccountException.DISABLED_ACCOUNT_MESSAGE);
				return false;
			}

			//first result is set, to be selected in options.xhtml.
			Long employeeRoleId = (Long)employeeRoles.get(0).getValue();
			String roleCode = employeeRoles.get(0).getCode();
			String roleCodeId = employeeRoles.get(0).getCodeId();
			String roleName = employeeRoles.get(0).getLabel();
			Boolean isCoordinator = employeeRoles.get(0).getIsCoordinator();

			UserBean ub = UserBean.instance();

			ub.setCurrentSystemUser(employeeId, nameFam, nameGiv, dbUsername, employeeRoleId, roleCode, roleCodeId, roleName, isCoordinator);

			ub.setEnabledRoles((List)employeeRoles);
			ub.setSpecialization(employeeRoles.get(0).getSpecializationCode());
			
			List<SelectItem> sdlocs = findEnabledSdlocs(ub);
			ub.setEnabledSdlocs(sdlocs);
			//ub.setSdLocs(sdlocIds);  //selected sdlLocs at login are SdLocs
			
//			setDefLanguage(emp);
			
			Query qryApps = em.createQuery(enabledApplicationHql);
			qryApps.setParameter("internalId", employeeId);
			List<Application> applicationList = qryApps.getResultList();

			if (applicationList!=null && applicationList.size()>0){
				ub.setApplication(applicationList.get(0));
			}

			//One login x user
			sessionId = addUsr2Session(employeeId, sessionId);
			
			

			//do something after login. Extend this class and override afterLogin to change after login logic, for example to launch a process
			afterLogin(lastChangedPassword, lastAccessDate);
			
			SessionManager sm =SessionManager.instance();
			sm.updateSession(dbUsername, sessionId, Contexts.getSessionContext(), identity);
			
			int loggedSess = sm.getSessionsSize();
			
			if(ub.userExpired){
				return false;
			}

			identity.addRole(roleCode);


			Conversation conversation = Conversation.instance();
			conversation.begin();
			conversation.changeFlushMode(FlushModeType.MANUAL);
			conversation.setDescription("HOME");
			Manager.instance().killAllOtherConversations(); 
			sm.updateConversation(ub.getUsername(), Integer.parseInt(conversation.getId()), Contexts.getConversationContext());
			
			// Moved to UserBean to get the right role
			// ParameterManager.instance().init();
			
			
			if (log.isDebugEnabled()) {
				log.debug("Authenticated: " + dbUsername);
			} 
			
			MDC.put("username", dbUsername);
			
			String ip="";
			ServletContexts servletCtx = ServletContexts.instance();
			if (servletCtx != null) {
				HttpServletRequest httpRequest = servletCtx.getRequest();
				if(httpRequest!=null){
					String ipAddress = httpRequest.getHeader("X-FORWARDED-FOR");
					if (ipAddress == null) {
					    ipAddress = httpRequest.getRemoteAddr();
					}
					if(ipAddress!=null)
						ip=ipAddress;
				}
			}
			
			log.info("[cid="+conversation.getId()+"] Authenticated via PHI "+((sessionId == null) ? "" : "on http session "+sessionId) + " From ip: "+ip+" Server logged session count: "+loggedSess);
			
			
			return true;

		} catch (Exception e) {
			credentials.setUsername("");
			credentials.setPassword("");

			log.error("Error authenticating user: " + credentials.getUsername(), e);

			FacesErrorUtils.addErrorMessage("authenticate", FacesErrorUtils.getMessage(AccountException.CALL_ASSISTANCE_MESSAGE), e.getMessage());
			
			return false;
		}
	}
	
	
	/**
	 * Do the authentication for the current username and password
	 * @param username inserted username
	 * @param password inserted password
	 * @param rm the RepositoryManager instance
	 * @return an array with some informations for the employee authenticated which will be used 
	 * to save the UserBean instance associated with the session 
	 */
	protected Object[] doAuthentication(String username, String password, RepositoryManager rm) {
		boolean useLDAPauth = rm.getUse_LDAP_auth();
		Query loginQry = null;
		Integer checkLDAPresponse = -2;
		//Code to get authentication via LDAP
		if (useLDAPauth) {

			AccessControlManagerLDAP accessControlManagerLDAP = new AccessControlManagerLDAP();

			/** 
			 * checkLDAPresponse can be:
			 *  0  LDAP correct user/password and account is valid
			 *  1  LDAP wrong user or password
			 * -1  LDAP correct user/password but there's a problem in account (e.g. expired or any other LDAP account/server problem)
			 */
			checkLDAPresponse = accessControlManagerLDAP.validateUser(username, password);

			if (checkLDAPresponse==0){
				checkEmployeeValidDates = false;
				this.checkPasswordExpired = false;
				this.checkUserExpired = false;
				loginQry = em.createQuery(checkEmpExistHql);
				loginQry.setParameter("user", username);

			} else 	if (checkLDAPresponse==-1){
				FacesErrorUtils.addErrorMessage(AccountException.LDAP_GENERIC_ERROR);
				return null;

			} else 	if (checkLDAPresponse==1){
				FacesErrorUtils.addErrorMessage(AccountException.LDAP_ACCOUNT_DOESNT_EXIST);
				//return false;
			}
		}//Code to get authentication via PHI - No LDAP integration or no LDAP user.
		//To allow login for valid PHI users without LDAP credentials
		//Code to get authentication via PHI - No LDAP integration or no LDAP user.
		//To allow login for valid PHI users without LDAP credentials
		if ((!useLDAPauth)||(checkLDAPresponse==1)) {
			
			//code to make support both crypted and plain passwords
			boolean useMD5password= rm.isUsing_MD5_passwords();
			if (useMD5password) {
				password = Security.crypt(password);
			}
			
			loginQry = em.createQuery(loginHql);
		
			loginQry.setParameter("user", username);
			loginQry.setParameter("pass", password);
		}

		if (loginQry == null) {
			return null;
		}

		
		//Verify Brute force
		int loginLimit = Integer.parseInt(RepositoryManager.instance().getSeamProperty("loginCountLimit"));
		
		Query qCountLogins = em.createQuery(countLogins);
		qCountLogins.setParameter("username", username);
		
		@SuppressWarnings("unchecked")
		List<Object[]> loginCounts = qCountLogins.getResultList();
		Integer loginCount = 0;
		if (loginCounts.size() == 1 ){
			loginCount = (Integer)loginCounts.get(0)[1];
			if (loginCount == null) {
				loginCount = 0;
			}
			if (loginCount > loginLimit) {
				FacesErrorUtils.addErrorMessage(AccountException.NOT_YET_ACTIVE_ACCOUNT_MESSAGE);
				return null;
			}
		} else if (loginCounts.size() == 0) {
			log.error("username wrong "+username);
			return null;
		}
		
		
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> employeeLst = loginQry.getResultList();
		
		//Ensure single result
		if (employeeLst.size() != 1) {

			//if loginCount query returns exactly one result (= username exists)
			//but loginQuery (loginHql) returns empty list (= wrong user+pass)
			if (employeeLst.size() == 0 && loginCounts.size() == 1) {
				loginCount++;
				log.error("Wrong password for "+username + " increasing loginCounts to "+loginCount);
				Query qUpdLoginCount = em.createQuery(updateLoginCountHql);
				qUpdLoginCount.setParameter("username", username);
				qUpdLoginCount.setParameter("loginCount", loginCount);
				qUpdLoginCount.executeUpdate();
				em.flush();
			}
			
			if ((useLDAPauth)&&(checkLDAPresponse==0)&&(employeeLst.size()==0)){
				//Valid LDAP user without related phi user
				FacesErrorUtils.addErrorMessage(AccountException.LDAP_OK_NO_PHI_ACCOUNT);
			} /*else {
				removed because it's already shown borg.jboss.seam.loginFailed
				FacesErrorUtils.addErrorMessage(AccountException.WRONG_ACCOUNT_MESSAGE);

			}*/
			
			return null;
		}
		
		Object[] employee = employeeLst.get(0);
		
		String dbUsername = (String)employee[3];
		String dbPassword = (String)employee[4];
		
		//do this check if the user is logging in to PHI
		//Ensure case sensitivity of username and password:
		if (((!useLDAPauth)||(checkLDAPresponse==1)) && (!username.equals(dbUsername) || !password.equals(dbPassword))) {
			//FacesErrorUtils.addErrorMessage(AccountException.WRONG_ACCOUNT_MESSAGE);
			return null;
		}

		return employee;
	}

	/**
	 * Logout method.
	 * Called by seam "org.jboss.seam.security.loggedOut" event configured in components.xml
	 */
	public void logout() {

		UserBean ub = UserBean.instance();

		try {
			//save log Info before closing Session
			String logInfo = "[cid="+Conversation.instance().getId()+"] ";
			Locker.instance().unlockAll(ub.getUsername());
			Events.instance().raiseEvent(logoutEvent);
			
			//1. removeUsrFromSession not more needed, this operation is made in session collection listner
			//2. this method execute flush, and commit any other changes available into user transaction, so temporary changes are flushed also if not required
			//removeUsrFromSession(ub.getCurrentEmployee().getInternalId());

			if (log.isDebugEnabled()) {
				log.debug("logged out user "+ub.getUsername() + " with role "+ ub.getRoleName());
			}
			
			SessionManager sm = SessionManager.instance();
			List<String> usersToBeRelogged = sm.getUserToBeRelogged();
			String user = UserBean.instance().getUsername();
			if (usersToBeRelogged != null && usersToBeRelogged.contains(user)) {
				usersToBeRelogged.remove(user);  //.setUsersToBeRelogged
				//sm.updateStatefulProp(sm.userToBeRelogged, usersToBeRelogged);
			}
			removeCookie("JSESSIONID");
			log.info(logInfo + " Logged out");


		} catch (Exception e) {
			log.error("Error logging out user: " + ub.getUsername(), e);
			FacesErrorUtils.addErrorMessage(AccountException.CALL_ASSISTANCE_MESSAGE);
		}
	}

	/**
	 * Check employee effectiveTime interval is valid
	 * Override this method to do other checks.
	 * @param employeeEffectiveTime valid date range
	 * @return 1 if today is in range employeeEffectiveTime
	 */
	protected int isValidUser(IVL<Date> employeeEffectiveTime)  /*throws AccountException */{

		if (checkEmployeeValidDates) {

			Date todayDate = new Date();
			return checkDate(employeeEffectiveTime, todayDate);

		}

		return 0;
	}


	/**
	 * Override this method to do other checks.
	 * @param employeeId internalId of Employee
	 * @return valid employeeRoles attached to current employee
	 */
	protected List<EmployeeRoleSelectItem> findAdditionalRoles(Long employeeId) {
		
		String currentLang = "en";
		try {
			currentLang = LocaleSelector.instance().getLanguage();
		}
		catch (Exception e) {
			log.error("Error getting current language, using default "+currentLang, e);
		}

		String query = additionalRolesHql;

		if (!currentLang.equals("it")) {
			query = query.replace("roleCode.langIt", "roleCode.lang" + WordUtils.capitalize(currentLang));
		}

		Query loginQry = em.createQuery(query);
		loginQry.setParameter("employeeId", employeeId);
		loginQry.setParameter("today", new Date());

		@SuppressWarnings("unchecked")
		List<Object[]> roleIdCodeAndTranslation = loginQry.getResultList();

		List<EmployeeRoleSelectItem> values = new ArrayList<EmployeeRoleSelectItem>();

		for (Object[] cv : roleIdCodeAndTranslation) {
			EmployeeRoleSelectItem selItem = new EmployeeRoleSelectItem(cv[0], cv[1].toString(), cv[2].toString(), cv[3].toString(), (Boolean)cv[4],(String)cv[5]);
			values.add(selItem);
		}

		if (values.isEmpty()) { 
			return null;

		}
		return values;
	}
	
	/**
	 * Find the list of ServiceDeliveryLcoation internalId which are enabled for a given employeeRole
	 * @param ub -> the UserBean instance from where get the employeeRole InternalId
	 * @return the list of enabled sdlocs for the employeeRole selected
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> findEnabledSdlocs(UserBean ub) {
		
		String loginSdlocFromCondevalueRole =  RepositoryManager.instance().getSeamProperty("loginSdlocFromCondevalueRole");
		
		List<SelectItem> sdlocIds;// = new ArrayList<Object[]>();
		Query q;
		
		if ("true".equals(loginSdlocFromCondevalueRole)) {
			q = em.createQuery(enabledSdlFromCondevalueRoleHql);
			q.setParameter("id", ub.getRoleCodeId());
		} else {
			q = em.createQuery(enabledSdlHql);
			q.setParameter("internalId", ub.getEmployeeRoleId());
		}
		sdlocIds = q.getResultList();
		return sdlocIds;
	}

	@SuppressWarnings("unchecked")
	public List<Long> findEnabledLeavesSdlocs(UserBean ub) {
		List<Long> sdlocIds;// = new ArrayList<Object[]>();
		Query q = em.createQuery(enabledSdlChildrenHql);
		q.setParameter("internalId", ub.getEmployeeRoleId());
		sdlocIds = q.getResultList();

		return sdlocIds;
	}

	/**
	 * Checks if the password has expired
	 * Override this method to do something after a succesful login, like start a process.
	 * @param employeeLastChangedPassword Employee.lastChangedPassword
	 * @param employeeLastAccessDate Employee.lastAccessDate
	 */
	public void afterLogin(Date employeeLastChangedPassword,Date employeeLastAccessDate) {
		if (checkUserExpired) {

			UserBean ub = UserBean.instance();
			RepositoryManager rm = RepositoryManager.instance();

			if (employeeLastAccessDate != null)  {

				Calendar usrExpireDate = Calendar.getInstance();
				usrExpireDate.setTime(employeeLastAccessDate);
				usrExpireDate.add(Calendar.DATE, rm.getUserExpireInDays());

				Date todayDate = new Date();

				if (todayDate.after(usrExpireDate.getTime())) {
					ub.setUserExpired(true);
					ub.setLoginOptions(false);
					FacesErrorUtils.addErrorMessage(AccountException.EXPIRED_ACCOUNT_MESSAGE);
					return;
				}
			}
		}

		if (checkPasswordExpired) {

			UserBean ub = UserBean.instance();
			RepositoryManager rm = RepositoryManager.instance();

			if (employeeLastChangedPassword == null) {
				ub.setPasswordExpired(true);
				List<Long> enabledSdlocLeaves =	this.findEnabledLeavesSdlocs(ub);
				if (enabledSdlocLeaves.size() > 1) {
					ub.setLoginOptions(true);
				}
			
			} else {

				Calendar pwdExpireDate = Calendar.getInstance();
				pwdExpireDate.setTime(employeeLastChangedPassword);
				pwdExpireDate.add(Calendar.DATE, rm.getPasswordExpireInDays());

				Date todayDate = new Date();

				if (todayDate.after(pwdExpireDate.getTime())) {
					ub.setPasswordExpired(true);
				
					List<Long> enabledSdlocLeaves =	this.findEnabledLeavesSdlocs(ub);
					if (enabledSdlocLeaves.size() > 1) {
						ub.setLoginOptions(true);
					}
					
				}
			}
		}
	}


	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}
	
	public boolean isExternalAuthentication() {
		return externalAuthentication;
	}

	public void setExternalAuthentication(boolean externalAuthentication) {
		this.externalAuthentication = externalAuthentication;
	}

	/**
	 * Verify password from process
	 */
	public boolean verifyInsertedPassword () {
		Context conv = Contexts.getConversationContext();
		Employee emp =(Employee)conv.get("Employee");
		String oldPasswordInput;
		String newPasswordInput;
		String confirmPasswordInput;

//		EmployeeAction empAct = EmployeeAction.instance();
		Context eventCtx = Contexts.getEventContext();

		if (emp==null)
			return false;

		if (oldPassword == null || newPassword == null || confirmPassword == null) {
			return false;
		}

		RepositoryManager rm = RepositoryManager.instance();
		boolean useMD5password = rm.isUsing_MD5_passwords();

		if (useMD5password) {
			oldPasswordInput = Security.crypt(oldPassword);
			newPasswordInput = Security.crypt(newPassword);
			confirmPasswordInput = Security.crypt(confirmPassword);
		}
		else{
			oldPasswordInput = oldPassword;
			newPasswordInput = newPassword;
			confirmPasswordInput = confirmPassword;
		}

		if (!oldPasswordInput.equals(emp.getPassword())) { 
			//FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, AccountException.PASSWORD_OLD_INCORRECT);
//			empAct.getTemporary().put("errorMess",  ResourceBundle.getBundle("bundle.error.messages", new java.util.Locale(Locale.instance().getLanguage())).getString(AccountException.PASSWORD_OLD_INCORRECT) );
			eventCtx.set("errorMess",  ResourceBundle.getBundle("bundle.error.messages", new Locale(org.jboss.seam.core.Locale.instance().getLanguage())).getString(AccountException.PASSWORD_OLD_INCORRECT) );
			return false;
		}
		if (newPasswordInput.equals(oldPasswordInput)) { 
			//FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, AccountException.PASSWORD_OLD_NEW_EQUAL);
//			empAct.getTemporary().put("errorMess",  ResourceBundle.getBundle("bundle.error.messages", new java.util.Locale(Locale.instance().getLanguage())).getString(AccountException.PASSWORD_OLD_NEW_EQUAL) );
			eventCtx.set("errorMess",  ResourceBundle.getBundle("bundle.error.messages", new Locale(org.jboss.seam.core.Locale.instance().getLanguage())).getString(AccountException.PASSWORD_OLD_NEW_EQUAL) );
			return false;
		}
		if (!newPasswordInput.equals(confirmPasswordInput)) { 
			//FacesMessages.instance().add(FacesMessage.SEVERITY_ERROR, AccountException.PASSWORD_MISMATCH);
//			empAct.getTemporary().put("errorMess",  ResourceBundle.getBundle("bundle.error.messages", new java.util.Locale(Locale.instance().getLanguage())).getString(AccountException.PASSWORD_MISMATCH) );
			eventCtx.set("errorMess",  ResourceBundle.getBundle("bundle.error.messages", new Locale(org.jboss.seam.core.Locale.instance().getLanguage())).getString(AccountException.PASSWORD_MISMATCH) );
			return false;
		}
		return true;
	}

	public void changePassword() {
		UserBean ub = UserBean.instance();
		Employee emp = ub.getCurrentEmployee();
		changePassword(emp);
	}
	
	public void changePassword(String oldPassword, String newPassword, String confirmPassword) {
		this.setOldPassword(oldPassword);
		this.setNewPassword(newPassword);
		this.setConfirmPassword(confirmPassword);
		UserBean ub = UserBean.instance();
		Employee emp = ub.getCurrentEmployee();
		changePassword(emp);
	}
	
	/**
	 * Change password at login
	 */
	public void changePassword(Employee emp) {

		if (emp == null)
			return;

		if (oldPassword == null && newPassword == null && confirmPassword == null) {
			return;
		}

		RepositoryManager rm = RepositoryManager.instance();
		boolean useMD5password = rm.isUsing_MD5_passwords();

		if (useMD5password) {
			oldPassword = Security.crypt(oldPassword);
			newPassword = Security.crypt(newPassword);
			confirmPassword = Security.crypt(confirmPassword);
		}

		if (!oldPassword.equals(emp.getPassword())) { 
			//			throw new AccountException(AccountException.PASSWORD_OLD_INCORRECT);
			FacesErrorUtils.addErrorMessage(AccountException.PASSWORD_OLD_INCORRECT);
			return;
		}

		if (!newPassword.equals(confirmPassword)) { 
			//			throw new AccountException(AccountException.PASSWORD_MISMATCH);
			FacesErrorUtils.addErrorMessage(AccountException.PASSWORD_MISMATCH);
			return;
		}

		if (newPassword.equals(oldPassword)) { 
			//			throw new AccountException(AccountException.PASSWORD_OLD_NEW_EQUAL);
			FacesErrorUtils.addErrorMessage(AccountException.PASSWORD_OLD_NEW_EQUAL);
			return;
		}

		emp = em.find(Employee.class, emp.getInternalId());

		emp.setPassword(newPassword);
		emp.setLastChangedPassword(new Date());
		emp.setIsNew(false);	// first access ever: set isNew=false

		em.persist(emp);
		em.flush();

		//reset fields
		this.newPassword=null;
		this.oldPassword=null;
		this.confirmPassword=null;

		UserBean.instance().setPasswordExpired(false);
		UserBean.instance().setUserExpired(false);
		
		Events.instance().raiseEvent(eventChangePassword, emp.getInternalId());
	}

	/**
	 * One session per user
	 * Bind sessionId to employee, kill previous session of same employee
	 * return user httpSessionId
	 */
	public String addUsr2Session(Long employeeId, String previousSessionId) throws AccountException {

		try {

			String sessionId = null;

			String webModule = null;
			
			HttpServletRequest httpRequest = null;

			FacesContext facesCtx = FacesContext.getCurrentInstance();
			if (facesCtx != null) {
				ExternalContext externalCtx = facesCtx.getExternalContext();
				if (externalCtx != null) {
					HttpSession session = (HttpSession) externalCtx.getSession(false);
					if (session != null) {
						sessionId = session.getId();
					}
					Object request = externalCtx.getRequest();
					if (request instanceof HttpServletRequest) {
						httpRequest = (HttpServletRequest) request;
						if (httpRequest.getContextPath() != null) {
							webModule = httpRequest.getContextPath().substring(1, httpRequest.getContextPath().length());
						}
					}
				}
			}
			if (sessionId == null) {
				ServletContexts servletCtx = ServletContexts.instance();
				if (servletCtx != null) {
					httpRequest = servletCtx.getRequest();
					if (httpRequest != null) {
						HttpSession session = httpRequest.getSession();
						if (session != null) {
							sessionId = session.getId();
						}
						webModule = httpRequest.getContextPath().substring(1, httpRequest.getContextPath().length());
					}
				}
			}
			
			Context app = Contexts.getApplicationContext();
			if (!app.isSet(CsConstants.SOLUTION_NAME)) {
				app.set(CsConstants.SOLUTION_NAME, webModule);
			}

			if (httpRequest != null) {
				UserBean.instance().setRemoteHost(httpRequest.getRemoteHost());
			}

			if (sessionId == null) {
				log.warn("No session active.");
				return null;
			}

			//kill previousSession if different from current session
			if (previousSessionId != null && !previousSessionId.equals(sessionId)) {

				//1. removeUsrFromSession not more needed, this operation is made in session collection listner
				//2. this method execute flush, and commit any other changes available into user transaction, so temporary changes are flushed also if not required
				//removeUsrFromSession(employeeId);
				
				String cid = "--"; 
				if (Contexts.isConversationContextActive()) {
					Conversation conversation=Conversation.instance();
					cid = conversation.getId();
				}
				
				String usernam = "---";
				if (Contexts.isSessionContextActive()) {
					usernam = UserBean.instance().getUsername();
				}
				
				killSession(webModule, previousSessionId);
				
				//Usare session manager per killare la sessione anzichè mBean
//				SessionManager sm = SessionManager.instance();  //BRAGA
//						
//				String userByJsess = sm.getUserByJsess(previousSessionId);  //it should be equal to usernam
//				if (!usernam.equals(userByJsess)) {
//					log.error("Something wrong happens with sessions...");
//				}
//				
//				sm.killSession(usernam);
				
				log.warn("[cid="+cid+"] Previous Session has been killed; session id = " + previousSessionId + " employee id = " + employeeId);
			}

			//Save current session id into db  .setSessionId
			Query qSetSessionId = em.createQuery(hqlSetSessionId);
			qSetSessionId.setParameter("sessionId", sessionId);
			qSetSessionId.setParameter("internalId", employeeId);
			qSetSessionId.setParameter("host", InetAddress.getLocalHost().getHostName());
			qSetSessionId.setParameter("lastAccessDate", new Date());
			qSetSessionId.setParameter("loginCount", 0);
			/*int res = */ qSetSessionId.executeUpdate();
			em.flush();

			return sessionId;

		} catch (Exception e) {
			log.error("Problem adding user to session. Cannot authenticate user", e);
			throw new AccountException(AccountException.CALL_ASSISTANCE_MESSAGE);
		}
	}

	/**
	 * One session per user
	 * Kill a session
	 * Used by addUsr2Session to kill a previous session of the same user
	 */
	@SuppressWarnings("unchecked")
	protected void killSession(String webModule, String sessionId) throws AccountException {

		try {
			/*
			 * https://labs.consol.de/jmx4perl/2009/11/23/jboss-remote-jmx.html
			 * Sfortunatamente...non è possibile invocare via rmi l'mbean
			 */
			MBeanServer mbeanServer = MBeanServerLocator.locateJBoss();

			ObjectName objectName = new ObjectName(CACHE_MANAGER_OBJECT_NAME_SINGLETON + webModule);

			//kill session
			mbeanServer.invoke(objectName, "expireSession", new String[] { sessionId }, new String[] { "java.lang.String" });
			log.info("Killed session "+sessionId+ " from admin console.");
			

//			SessionManager sm = SessionManager.instance(); 
//			sm.killSessionById(sessionId);
			

			//if it was a dead session, the db is not updated by SessionCollectionListner. So update it anyway.
			//InitialContext ic = new InitialContext();
//			GenericAdapterLocalInterface ga = GenericAdapter.instance(); //(GenericAdapterLocalInterface)ic.lookup("CATALOG_SERVER/GenericAdapter/local");
//			HashMap<String, Object> parameters=new HashMap<String, Object>();
//			parameters.put("sessionId", sessionId);
//			ga.executeUpdateHql("update Employee set sessionId = null, host = null where sessionId = :sessionId", parameters);
			
			Query qry = em.createQuery("update Employee set sessionId = null, host = null where sessionId = :sessionId");
			qry.setParameter("sessionId", sessionId);
			qry.executeUpdate();
			
		} catch (IllegalStateException e) {
			//Seam lauches throw new IllegalStateException("Please end the HttpSession via org.jboss.seam.web.Session.instance().invalidate()");
			//at org.jboss.seam.contexts.ServletLifecycle.endSession(ServletLifecycle.java:186)
			//Nothing to do
		} catch (Exception e) {
			log.error("Problem killing session " + sessionId, e);
			throw new AccountException(AccountException.CALL_ASSISTANCE_MESSAGE);
		}
	}

	/**
	 * One session per user
	 * UnBind sessionId of employee
	 */
//	public void removeUsrFromSession(Long employeeId) throws AccountException {
//
//		try {
//
//			Query qSetSessionId = em.createQuery(hqlSetSessionId);
//			qSetSessionId.setParameter("sessionId", null);
//			qSetSessionId.setParameter("internalId", employeeId);
//			/*int res = */ qSetSessionId.executeUpdate();
//			em.flush();
//
//
//		} catch (Exception e) {
//			log.error("Problem removing user from session.", e);
//			throw new AccountException(AccountException.CALL_ASSISTANCE_MESSAGE);
//		}
//	}

	/**
	 * One session per user
	 * Used by adminConsole to show a list of connected users
	 */
	@SuppressWarnings("rawtypes")
	//@Override
	public List getSessions() throws AccountException {
		Query qConnectedUsers = em.createQuery(hqlConnectedUsers);
		return qConnectedUsers.getResultList();
	}

	/**
	 * Return the list of Session information of user connected to the current host
	 * internalId, username, sessionId, host
	 */
	public List getNodeSessions() throws AccountException {
		Query qConnectedUsers = em.createQuery(hqlConnectedHostUsers);
		qConnectedUsers.setParameter("host",FunctionsBean.instance().getHostName());
		return qConnectedUsers.getResultList();
	}
	
	/**
	 * @return list of usernames of Employee which have an active session stored in db (all nodes).
	 * @throws AccountException
	 */
	public List<String> connectedUsers() throws AccountException {
		List<Object[]> read = getSessions();
		List<String> ret = new ArrayList<String>();
		if (read == null || read.isEmpty())
			return ret;
		
		for (Object[] u : read) {
			if (u != null && u[1] != null) {
				ret.add((String)u[1]);
			}
		}
		return ret;
	}
	
	/**
	* Return list of usernames of Employee which have an active session stored in db for the current node.
	*/
	public List<String> connectedNodeUsers() throws AccountException {
		List<Object[]> read = getNodeSessions();
		List<String> ret = new ArrayList<String>();
		if (read == null || read.isEmpty())
			return ret;
		
		for (Object[] u : read) {
			if (u != null && u[1] != null) {
				ret.add((String)u[1]);
			}
		}
		return ret;
	}
	
	
	/**
	 * Re login all users of current nodes.
	 */
	public void reloginAllNodeUsers() throws AccountException {
		reloginUsers(connectedNodeUsers(),null);
	}
	
	/**
	 * Relogin specific user
	 */
	public void reloginUser(String username) throws AccountException {
		List<String> l = new ArrayList<String>();
		l.add(username);
		reloginUsers( l, null);
	}
	
	
	//
	/**
	 * Relogin Users is a funcitonality usefull to force users to be be auto relogged.
	 * Relogin means the users (when a main process ends or when a user goes to home) are automatically
	 * logged out, and redirected to an url, of integration servlet, which relogin them again.
	 * The reloginUsers method simply set the list of users to be relogged (can be called multiple times)
	 * in an hashmap contained in SessionManager application bean
	 * 
	 * The relogin action is triggerd by method checkExecuteRelogin of BaseAccessControlAction.
	 * checkExecuteRelogin is called by viewManager (when going home) and ProcessManagerImpl (when main process ends);
	 *  
	 *   
	 * can be used to force relogin of some/all users.
	 * the include list can be used to pass only some users to be relogged
	 * the exclude list can be used when you want to relogin all users except someone
	 * passing both null paramters/empty, all users are set to be relogged. 
	 */
	public void reloginUsers(List<String> include, List<String> exclude) throws AccountException {
		 
		List<String> usersToBeRelogged = new ArrayList<String>();
		
		if (include != null && !include.isEmpty()) {
			usersToBeRelogged = include;
		}
		else if (exclude != null && !exclude.isEmpty()) {
			usersToBeRelogged = connectedUsers();
			usersToBeRelogged.removeAll(exclude);
		}
		else {
			usersToBeRelogged = connectedUsers();
		}
		
		SessionManager sm = SessionManager.instance();
		
		List<String> currentReloadingUserList = sm.getUserToBeRelogged();
		if (currentReloadingUserList != null && !currentReloadingUserList.isEmpty()  ) {
			//make remove/add to remove duplicate.
			currentReloadingUserList.removeAll(usersToBeRelogged);
			usersToBeRelogged.addAll(currentReloadingUserList);
		}
		
		sm.setUserToBeRelogged(usersToBeRelogged);
		
	}
	
	
	
	/**
	 * One session per user
	 * Kill a session and clean user
	 * Used by AdminConsole to kick a user
	 */
	public void killSession(Long employeeId, String sessionId) throws AccountException {
		killSession((String)Contexts.getApplicationContext().get("SolutionName"), sessionId); //FIXMEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
		log.info("Killed session "+sessionId+ " from admin console.");
		
		//1. removeUsrFromSession not more needed, this operation is made in session collection listner
		//2. this method execute flush, and commit any other changes available into user transaction, so temporary changes are flushed also if not required
		//removeUsrFromSession(employeeId);  

	}

	//	private String getWebModule() {
	//
	//		ExternalContext eCtx = FacesContext.getCurrentInstance().getExternalContext();
	//		
	//		if (eCtx != null) {
	//			Object reqOb = eCtx.getRequest();
	//			if (reqOb instanceof HttpServletRequest) {
	//				HttpServletRequest req = (HttpServletRequest) reqOb;
	//				return req.getContextPath().substring(1, req.getContextPath().length());
	//			}
	//		}
	//		return null;
	//	}

	private int checkDate(IVL<Date> validInterval, Date todayDate) {
		if (validInterval == null) 	
			return -1;

		if (validInterval.getLow() == null) 
			return -1;

		Date validFromDate = validInterval.getLow();


		if (todayDate.before(validFromDate)) 
			return -1;

		Date validToDate = null;
		if (validInterval.getHigh() != null) 
			validToDate = validInterval.getHigh();


		if (validToDate!=null && todayDate.after(validToDate) ) 
			return 1;

		return 0;
	}

	public static AccessControl instance() {
		return (AccessControl) Component.getInstance(AccessControlAction.class, ScopeType.STATELESS);
	}

	
	/**
	 * Build reloing url, starting form current session
	 */
	public String reloginUrl() {
		// /InnerIntegrationServlet?username=fabrizia&lang=it&roleDisplay=AMMINISTRATIVO&uorg=3744&PatientEncounter=181559
		//FIXME: get all banner information.

		RepositoryManager repo = RepositoryManager.instance();
		
		//FIXME: the url part of servlet name, is configured in web.xml. 
		//There is no guarantee that the name is equal to the class name.
		//parsing web.xml 
		String baseServletName = "InnerIntegrationServlet";
		Object o = Component.getInstance(baseServletName);
		String servletName = baseServletName;
		if (o != null) {
			String simpleName = o.getClass().getSimpleName(); 
			if (simpleName.contains("_$$_")){
				simpleName=simpleName.substring(0,simpleName.indexOf("_$$_"));
			}
			servletName=simpleName;
		}
		
		UserBean ub = UserBean.instance(); 
		String username=ub.getUsername();
		
		String password=ub.getCurrentEmployee().getPassword();
		boolean useMD5password= repo.isUsing_MD5_passwords();
		boolean provideEncryptedPasswordToIntegrationServlet = "true".equals(repo.getSeamProperty("provideEncryptedPasswordToIntegrationServlet"));
		boolean enableIntegrationServletPasswordProtection = "true".equals(repo.getSeamProperty("enableIntegrationServletPasswordProtection"));
		
		//password are get from db, so must not be encrypted again.
		if (useMD5password && !provideEncryptedPasswordToIntegrationServlet) {
				log.error("incompatibile configuration for relogin");
		}
		if (!useMD5password && provideEncryptedPasswordToIntegrationServlet) {
			password = Security.crypt(password);
		}
		
		String lang = LocaleSelector.instance().getLanguage();
		
		String roleCode=ub.getCurrentSystemUser().getCode().getCode();
		String sdlInternalIdLoginList=  StringUtils.join(ub.getSdLocs(), ";");  //list of selected Sdlocs
		
		
		String ret = "/"+servletName+"?username="+username;
		if (enableIntegrationServletPasswordProtection) {
			ret+="&password="+password;
		}
		
		ret+="&lang="+lang+"&roleCode="+roleCode; 
		if (sdlInternalIdLoginList != null && !sdlInternalIdLoginList.isEmpty()) 
			ret+="&sdlInternalIdLoginList="+sdlInternalIdLoginList;
		
		return ret;
	}
	
	/**
	 * Execute relogin of one sessiong given url for redirect to inner integration servlet. 
	 */
	public void executeRelogin(String url){
		
		Identity id =Identity.instance(); 
		id.logout();  //calls logout() method of BaseAccessControl which already remove JSESSIONID
					  //removeCookie("JSESSIONID");
		Redirect r = Redirect.instance();
		r.setViewId(url);
		r.execute();
		
	}
	
		
	/**
	when user logout jsessionid cookie can be removed. After logout the user is redirected on login page.
	if jsessionid cookie is not removed the next get http request from client will contains a jsession id referring to expired session.
	with jb4 the httpget with expired jsession id seems to be allowed, and all authentication process is maintained wiht old jsessid, so
	finally the authenticated session will have the old jsessionId
	with jb5 the httpget with expired jsession id obatin a http response page with set-cookie a new jsession id (correct).
	remove the jsession id after logout is needed to unstick user from node, when balancer is present in the middle between client/server:
	after lotout, if the http get for login page is made with an expired jsession id, than jb4/5 (differently) manage the authentication but
	the problem is that the balancer still maintain the session on the same node, instead after logout the new httpget must generate a new
	session managed by balancer according to its rules.
	*/	
	private void removeCookie(String name) {
		String nullValue = null;
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    
	    HttpServletRequest request = null;
	    
	    if (facesContext != null) {
	    	request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
	    } else {
	    	ServletContexts servletCtx = ServletContexts.instance();
	    	request = servletCtx.getRequest();
	    }
	    Cookie cookie = null;

	    Cookie[] userCookies = request.getCookies();
	    if (userCookies != null && userCookies.length > 0 ) {
	        for (int i = 0; i < userCookies.length; i++) {
	            if (userCookies[i].getName().equals(name)) {
	                cookie = userCookies[i];
	                break;
	            }
	        }
	    }

	    //by setting null value, and maxAge(0) the cookie will be removed by browser.
	    if (cookie != null) {
	        cookie.setValue(nullValue);  
	    } else {
	        cookie = new Cookie(name, nullValue);
	        //cookie.setPath(request.getContextPath());  //FIXME: looks like cookie path in the request is null.  IE does not accept this cookie.
	    }
	    
	    //generate solution path for cookie
	    String path = "/";
	    Context app = Contexts.getApplicationContext();
		if (app.isSet(CsConstants.SOLUTION_NAME)) {
			path += (String)app.get(CsConstants.SOLUTION_NAME);
		}
	    cookie.setPath(path);

	    cookie.setMaxAge(0); //Set expired cookie.

	    HttpServletResponse response;
	    if (facesContext != null) {
	    	response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		    response.addCookie(cookie);
	    }
	  }

	
	
	/**
	 * 
	 * Check if the current user must be relogged (lookup in the hashp usersToBeRelogged of SessionManageR)
	 * If the user must be relogged, add to url for relogin current Patient/PatientEncounter/Appointment id in user's conversation
	 * and execute the user relogin.
	 * 
	 * checkExecuteRelogin is called by viewManager (when going home) and ProcessManagerImpl (when main process ends);
	 * 
	 * The hashmap usersToBeRelogged is updated in this moment, 
	 * when users logout, or when the user is forced to be relogged. 
	 * 
	 */
	public boolean checkExecuteRelogin() {
		SessionManager sm = SessionManager.instance();
		List<String> usersToBeRelogged = sm.getUserToBeRelogged(); 
		String user = UserBean.instance().getUsername(); 
		if (usersToBeRelogged != null && usersToBeRelogged.contains(user)) {
			
			//first update UsersToBeRelogged SessionManager Hashmap. The user wil lbe relogged at the end of this code section.
			usersToBeRelogged.remove(user);   //.setUserToBeRelogged
			
			String url = reloginUrl();
			
			//Add Patient,PatientEncounter and Appointment, if any.
			Context conv = Contexts.getConversationContext();
			
			for (String entityName : new String[]{"Patient", "PatientEncounter", "Appointment"} ) {
				if (conv.get(entityName) != null ) {
					BaseEntity entity = (BaseEntity) conv.get(entityName);
					if (entity != null && entity.getInternalId() > 0 ){
						url+="&"+entityName+"="+entity.getInternalId();
					}
				}
			}
			
			
			log.info("FORCE RELOGGING user:"+user +" with url:"+url);
			executeRelogin(url);
			return true;
		}
		return false;
	}
	
	/**
	 * Called from Angualar
	 * @return
	 */
	public String getReloginUrl() {
		SessionManager sm = SessionManager.instance();
		List<String> usersToBeRelogged = sm.getUserToBeRelogged(); 
		String user = UserBean.instance().getUsername(); 
		if (usersToBeRelogged != null && usersToBeRelogged.contains(user)) {
			
			//first update UsersToBeRelogged SessionManager Hashmap. The user wil lbe relogged at the end of this code section.
			usersToBeRelogged.remove(user);   //.setUserToBeRelogged
			
			String url = reloginUrl();
			
			//Add Patient,PatientEncounter and Appointment, if any.
			Context conv = Contexts.getConversationContext();
			
			for (String entityName : new String[]{"Patient", "PatientEncounter", "Appointment"} ) {
				if (conv.get(entityName) != null ) {
					BaseEntity entity = (BaseEntity) conv.get(entityName);
					if (entity != null && entity.getInternalId() > 0 ){
						url+="&"+entityName+"="+entity.getInternalId();
					}
				}
			}
			
			
			log.info("FORCE RELOGGING user:"+user +" with url:"+url);
			// executeRelogin(url);
			return url;
		}
		return null;
	}
	
	public boolean isAlreadyLoggedIn(){
		Identity identity = Identity.instance();
		RepositoryManager rm = RepositoryManager.instance();
		Credentials credentials = identity.getCredentials();
		String username = credentials.getUsername();
		String password = credentials.getPassword();
		boolean useMD5password= rm.isUsing_MD5_passwords();
		if (useMD5password && password!=null) {
			password = Security.crypt(password);
		}
		
		Query loginQry = em.createQuery(loginHql);
		
		loginQry.setParameter("user", username);
		loginQry.setParameter("pass", password);
		
		List<Object[]> employeeLst = loginQry.getResultList();
		
		//Ensure single result
		if (employeeLst.size() == 1) {

			Object[] employee = employeeLst.get(0);
			String sessionId = (String)employee[7];
			
			if(sessionId!=null && !sessionId.isEmpty()){
				return true;
			
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	public void doLogin(){
		if(isAlreadyLoggedIn()){
			return;
		
		}else{
			Identity identity = Identity.instance();
			identity.login();
		}
	}


//	/**
//	 * Sets user default language for applicarion
//	 * 
//	 * @param emp - logged employee
//	 * @throws Exception
//	 */
//	public String setDefLanguage (Employee emp) throws Exception {
//		String defaultLanguage = null;
//		FacesContext fc = FacesContext.getCurrentInstance();
//		
//		if(emp != null && emp.getDefaultLanguageCode() != null){
//			defaultLanguage = emp.getDefaultLanguageCode().getCode();
//
//			if ("it".equalsIgnoreCase(defaultLanguage.trim())){
//				LocaleSelector.instance().setLocaleString("it_IT");
//				if (fc != null && fc.getViewRoot() != null) {
//					fc.getViewRoot().setLocale(new Locale("it"));
//				}
//			} else if("de".equalsIgnoreCase(defaultLanguage.trim())){
//				LocaleSelector.instance().setLocaleString("de_DE");
//				if (fc != null && fc.getViewRoot() != null) {
//					fc.getViewRoot().setLocale(new Locale("de"));
//				}
//			}
//		} else {
//			LocaleSelector.instance().setLocaleString("it_IT");
//			if (fc != null && fc.getViewRoot() != null) {
//				fc.getViewRoot().setLocale(new Locale("it"));
//			}
//		}
//		return defaultLanguage;
//	}

}
