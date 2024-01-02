package com.phi.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.AccountException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.Application;
import com.phi.entities.actions.EmployeeAction;
import com.phi.entities.role.Employee;
import com.phi.security.selectItem.EmployeeRoleSelectItem;

@Stateless
@BypassInterceptors
@Name("accessControl")
@Install(precedence=Install.DEPLOYMENT)
public class SpisalAccessControlAction extends BaseAccessControlAction implements AccessControl, Serializable {

	private static final long serialVersionUID = -5108453149187139378L;
	private static final Logger log = Logger.getLogger(SpisalAccessControlAction.class);

	@PersistenceContext(unitName="rimEntityManagerPDM2")
	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	/**
	 * Authentication method configured in components.xml
	 * @return true if the the user is valid 
	 */
	@SuppressWarnings("unchecked")
	public boolean authenticate() {
		RepositoryManager rm = RepositoryManager.instance();
		Identity identity = Identity.instance();
		Credentials credentials = identity.getCredentials();
	
		try {
		
			EmployeeAction eAction = EmployeeAction.instance();
			Employee emp = eAction.getEntity();

			Long employeeId;
			String nameFam;
			String nameGiv;
			String dbUsername;
			String sessionId;
			
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
					FacesErrorUtils.addErrorMessage(AccountException.DISABLED_ACCOUNT_MESSAGE);
					return false;
				}

				employeeId = (Long)employee[0];
				nameFam = (String)employee[1];
				nameGiv = (String)employee[2];
				dbUsername = (String)employee[3];
				sessionId = (String)employee[7];

			} else {
				employeeId = emp.getInternalId();
				nameFam = emp.getName().getFam();
				nameGiv = emp.getName().getGiv();
				dbUsername = emp.getUsername();
				sessionId = emp.getSessionId();				
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
			
			Query qryApps = em.createQuery(enabledApplicationHql);
			qryApps.setParameter("internalId", employeeId);
			List<Application> applicationList = qryApps.getResultList();

			if (applicationList!=null && applicationList.size()>0){
				ub.setApplication(applicationList.get(0));
			}

			/* Se l'utente ha solo un ruolo ed è abilitato solo su una ULSS bypassa le opzioni di login */
			/*if (employeeRoles.size()==1)
				ub.setLoginOptions(this.showLoginOptions());*/
			
			// H0071870 se l'utente ha solo un ruolo ed è abilitato solo su una UOC bypassa le opzioni di login
			ub.setLoginOptions(true);
			if (employeeRoles.size()==1){
				Long uocCounter = 0L;
				String uocCount = "SELECT COUNT(*) FROM ServiceDeliveryLocation sdl JOIN sdl.code type " + 
						"WHERE type.code = :code AND sdl.internalId IN (:sdLocs)";

				List<Long> longSdlLocs = new ArrayList<Long>();

				for(SelectItem sel : sdlocs){
					Long currentValue = (Long) sel.getValue();
					longSdlLocs.add(currentValue);
				}

				if (longSdlLocs != null && longSdlLocs.size()>1) {
					Query qry = em.createQuery(uocCount);
					qry.setParameter("sdLocs", longSdlLocs);
					qry.setParameter("code", "UOC");
					uocCounter = (Long) qry.getSingleResult();
					if(uocCounter > 1){
						ub.setLoginOptions(true);
					}else{
						qry.setParameter("code", "ARPAV");
						uocCounter = (Long) qry.getSingleResult();
						if(uocCounter > 1){
							ub.setLoginOptions(true);
						}else{
							qry.setParameter("code", "ULSS");
							uocCounter = (Long) qry.getSingleResult();
							if(uocCounter > 1){
								ub.setLoginOptions(true);
							}else{
								qry.setParameter("code", "REGIONE");
								uocCounter = (Long) qry.getSingleResult();
								if(uocCounter  > 1){
									ub.setLoginOptions(true);
								}else{
									ub.setLoginOptions(false);
								}
							}
						}
					}
				}
			}
			
			//One login x user
			sessionId = addUsr2Session(employeeId, sessionId);
			
			SessionManager sm = SessionManager.instance();
			sm.updateSession(dbUsername, sessionId, Contexts.getSessionContext(), identity);
			
			int loggedSess = sm.getSessionsSize();
			identity.addRole(roleCode);

			Conversation conversation = Conversation.instance();
			conversation.begin();
			conversation.changeFlushMode(FlushModeType.MANUAL);
			conversation.setDescription("HOME");
			Manager.instance().killAllOtherConversations(); 
			sm.updateConversation(ub.getUsername(), Integer.parseInt(conversation.getId()), Contexts.getConversationContext());
			
//			ParameterManager.instance().init();
			
			if (log.isDebugEnabled()) {
				log.debug("Authenticated: " + dbUsername);
			} 
			
			MDC.put("username", dbUsername);
			
			log.info("[cid="+conversation.getId()+"] Authenticated via PHI "+((sessionId == null) ? "" : "on http session "+sessionId) + " Server logged session count: "+loggedSess);
			
			return true;

		} catch (Exception e) {
			credentials.setUsername("");
			credentials.setPassword("");

			log.error("Error authenticating user: " + credentials.getUsername(), e);
			FacesErrorUtils.addErrorMessage("authenticate", FacesErrorUtils.getMessage(AccountException.CALL_ASSISTANCE_MESSAGE), e.getMessage());
			
			return false;
		}
	}
	
	protected void killSession(String webModule, String sessionId) throws AccountException {
		
	}
	
	/*
	 * true  - se tra le sdl c'è più di una ULSS 
	 * false - se tra le sdl c'è solo una ULSS
	 * */
	public boolean showLoginOptions() {
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		String countULSS = "SELECT sdl.internalId FROM ServiceDeliveryLocation sdl " +
				"WHERE sdl.code.code = 'ULSS' AND sdl.internalId IN (:sdLocs)";
		
		try {
			List<Long> sdLocs = UserBean.instance().getSdLocs();
			
			if (sdLocs != null && sdLocs.size()>1) {
				org.hibernate.Query qry = ca.createHibernateQuery(countULSS);
				
				qry.setParameterList("sdLocs", sdLocs);
				List<Object[]> res = qry.list();
				
				int number = res.size();
				
				if (number > 1)
					return true;
			}
			
			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}
	
}
