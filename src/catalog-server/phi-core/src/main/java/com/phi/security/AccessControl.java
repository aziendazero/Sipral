package com.phi.security;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.faces.model.SelectItem;

import com.phi.cs.exception.AccountException;
import com.phi.entities.role.Employee;


/**
 * Interface to handle acces to homepage, implements Authorization and Security roles
 */

@Local
public interface AccessControl {

	public Object isLoggedIn();

	//Authentication methods
	
	public boolean authenticate();
	
	public void logout();
	
	public void afterLogin(Date employeeLastChangedPassword, Date employeeLastAccessDate);

	//Change password methods
	
	public String getNewPassword();

	public void setNewPassword(String newPassword);
	
	public String getOldPassword();

	public void setOldPassword(String oldPassword);
	
	public String getConfirmPassword();

	public void setConfirmPassword(String confirmPassword);
	
	public boolean verifyInsertedPassword();
	
	public void changePassword();
	
	public void changePassword(Employee emp);
	
	public void changePassword(String oldPassword, String newPassword, String confirmPassword);

	//One session per user methods
	
	public String addUsr2Session(Long employeeId, String sessionId) throws AccountException;
	
	//1. removeUsrFromSession not more needed, this operation is made in session collection listner
	//2. this method execute flush, and commit any other changes available into user transaction, so temporary changes are flushed also if not required
	//public void removeUsrFromSession(Long employeeId) throws AccountException;
	
	//Admin Console methods
	
	@SuppressWarnings("rawtypes")
	public List getSessions() throws AccountException;
	public List getNodeSessions() throws AccountException;
	public List<String> connectedNodeUsers() throws AccountException;
	
	public void killSession(Long employeeId, String sessionId) throws AccountException;
	
	public boolean isExternalAuthentication();

	public void setExternalAuthentication(boolean externalAuthentication);
	
	public void reloginAllNodeUsers() throws AccountException;
	public void reloginUser(String username) throws AccountException;
	public void reloginUsers(List<String> include, List<String> exclude) throws AccountException;
	public String reloginUrl();
	public void executeRelogin(String url);
	public boolean checkExecuteRelogin();
	public String getReloginUrl();
	
	public List<SelectItem> findEnabledSdlocs(UserBean ub);
	public List<Long> findEnabledLeavesSdlocs(UserBean ub);
	public boolean isAlreadyLoggedIn();
	public void doLogin();
}