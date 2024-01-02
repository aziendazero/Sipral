package com.phi.rest.dmz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Locale;
import org.jboss.seam.core.Manager;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.servlet.ContextualHttpServletRequest;
import org.jboss.seam.servlet.ServletRequestSessionMap;
import org.jboss.seam.web.ServletContexts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.exception.AccountException;
import com.phi.cs.util.CsConstants;
import com.phi.entities.actions.AdviseMsgAction;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
import com.phi.json.HibernateModule;
import com.phi.rest.datamodel.HBSManager;
import com.phi.security.UserBean;


/**
 * Authentication Servlet
 * 
 * GET authentication status
 * POST login
 * DELETE logout
 * PUT set login options: selected role and selected ServicedeliveryLocations
 * 
 * 	<servlet>
 * 	    <description/>
 * 	    <servlet-name>AuthenticationServlet</servlet-name>
 * 	    <servlet-class>com.phi.rest.dmz.AuthenticationServlet</servlet-class>
 * 	</servlet>
 * 	
 * 	<servlet-mapping>
 * 	    <servlet-name>AuthenticationServlet</servlet-name>
 * 	    <url-pattern>/authentication</url-pattern>
 * 	</servlet-mapping>
 */
public class AuthenticationServlet extends HttpServlet {

	private static final long serialVersionUID = -3629978099764916416L;
	
	private static final Logger log = Logger.getLogger(AuthenticationServlet.class);
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();
	
	static {
		mapper.registerModule(new HibernateModule());
	}
	
	protected static final String APPLICATION_JSON_CHARSET_UTF8 = "application/json;charset=UTF-8";

	/**
	 * GET authentication status
	 */
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType(APPLICATION_JSON_CHARSET_UTF8);
		
		new ContextualHttpServletRequest(req) {
			@Override
			public void process() {			
				PrintWriter out = null;
				try {
					out = resp.getWriter();
					Map<String,Object> results = getDetails(req, false);
					
					if (Contexts.getEventContext() != null) {
						UserBean ub = UserBean.instance();
						if (ub.getLoggedByServlet()) {
							results.put("isLoggedByServlet", true);
						}
					}
					
					String jsonResults = mapper.writeValueAsString(results);
					out.print(jsonResults);

				} catch (Exception e) {
					log.error("Error getetting authenticated" , e);

					resp.setStatus(500);
					resp.setContentType("text/plain");
					
					out.print("Error getetting authenticated" + e.getMessage());
				} finally {
					if (out != null) {
						out.close();
					}
				}
			}
			
			@Override
			public void run() throws ServletException, IOException {
				// Force creation of the session BEFORE ServletLifecycle.beginRequest(request); 
				// Not needed on seam 2.3
				if (req.getSession(false) == null) {
					req.getSession(true);
				}

				super.run();
			}
		}.run();
	}

	/**
	 * POST login
	 */
	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType(APPLICATION_JSON_CHARSET_UTF8);
		
		new ContextualHttpServletRequest(req) {
			@Override
			public void process() {
				PrintWriter out = null;
				try {
					out = resp.getWriter();
		
					String username = req.getParameter("username");
					String password = req.getParameter("password");
					
					Identity identity = executeLogin(username, password);
					
//					if (identity.isLoggedIn()) {
//						setDefaultLanguage(); // set default language from employee
//					}
					
					String jsonResults = mapper.writeValueAsString(getResults(req, identity));
					out.print(jsonResults);
					
				} catch (Exception e) {
					log.error("Error login", e);
		
					resp.setStatus(500);
					resp.setContentType("text/plain");
		
					out.print("Error login " + e.getMessage());
		
				} finally {
					if (out != null) {
						out.close();
					}
				}
		
			}
		}.run();
		
	}
	
	/**
	 * DELETE logout
	 */
	@Override
	protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType(APPLICATION_JSON_CHARSET_UTF8);
		
		new ContextualHttpServletRequest(req) {
			@Override
			public void process() {
				PrintWriter out = null;
				try {
					out = resp.getWriter();
					
					Identity identity = Identity.instance();
		
					identity.logout();
					
					if (identity.isLoggedIn()) {
						throw new IllegalStateException("Error logout");
					} else {
						resp.setStatus(200);
					}
				    
					Cookie cookie = new Cookie("JSESSIONID", null);
					
					//generate solution path for cookie
				    String path = "/";
				    Context app = Contexts.getApplicationContext();
					if (app.isSet(CsConstants.SOLUTION_NAME)) {
						path += (String)app.get(CsConstants.SOLUTION_NAME);
					}
				    cookie.setPath(path);
				    cookie.setMaxAge(0); //Set expired cookie.

					resp.addCookie(cookie);
					
				} catch (Exception e) {
					log.error("Error logout", e);
		
					resp.setStatus(500);
					resp.setContentType("text/plain");
		
					out.print("Error logout " + e.getMessage());
		
				} finally {
					if (out != null) {
						out.close();
					}
				}
		
			}
		}.run();
		
	}
	
	/**
	 * PUT set login options: selected role and selected ServicedeliveryLocations
	 */
	@Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType(APPLICATION_JSON_CHARSET_UTF8);
		
		new ContextualHttpServletRequest(req) {
			@SuppressWarnings("unchecked")
			@Override
			public void process() {
				PrintWriter out = null;
				try {
					out = resp.getWriter();
					
					String jsonBody = IOUtils.toString(req.getReader());
					
					HashMap<String, Object> requestParams = mapper.readValue(jsonBody, HashMap.class);
					
					setLanguage((String)requestParams.get("lang"));
					
					UserBean ub = UserBean.instance();
					
					ub.setSelectedRole(new Long((Integer)requestParams.get("selectedRole")));
					
					List<Integer> sdlocs = (List<Integer>)requestParams.get("sdlocs");
					List<Long> sdlocsLong = new ArrayList<Long>();
					if (sdlocs != null) {
						for (Integer sdlocId: sdlocs) {
							sdlocsLong.add(sdlocId.longValue());
						}
						ub.setSdLocs(sdlocsLong);
					}
					
					ub.setLoginOptions(false);
					
					Map<String,Object> results = getDetails(req, true);
					String json = mapper.writeValueAsString(results);
					out.print(json);
	
				} catch (Exception e) {
					log.error("Error setting options", e);
		
					resp.setStatus(500);
					resp.setContentType("text/plain");
		
					out.print("Error setting options " + e.getMessage());
		
				} finally {
					if (out != null) {
						out.close();
					}
				}
		
			}
		}.run();
		
    }
	
	protected Map<String,Object> getDetails(HttpServletRequest req, boolean sessionExsists) throws ServletException, IOException {
		
		final Map<String,Object> results = new HashMap<String, Object>();
		
		results.put("sid", req.getSession().getId());
		
		if(sessionExsists) {
			Context sessCtc = Contexts.getSessionContext();
			getSessionDetails(sessCtc, results);
		} else {

			new ContextualHttpServletRequest(req) {
				@Override
				public void process() {
					Context sessCtc = Contexts.getSessionContext();
					if (sessCtc != null) {
						getSessionDetails(sessCtc, results);
					}
				}
			}.run();
		
		}

		return results;
	}
	
	private Map<String,Object> getSessionDetails(Context sessCtc, Map<String,Object> results) {
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Map<String,HashMap<String, Object>> param = (Map)sessCtc.get("Param");

		results.put("param", param);
		
		UserBean ub = UserBean.instance();
		
		Employee emp = ub.getCurrentEmployee();
		
		if (emp != null) {
			if (emp instanceof HibernateProxy) {
				emp = ((Employee)((HibernateProxy)emp).getHibernateLazyInitializer().getImplementation());
			}
			if (emp.getEmployeeRole() != null) { // get employee roles list
				for (int z = 0; z<emp.getEmployeeRole().size(); z++) {
					if (emp.getEmployeeRole().get(z) instanceof HibernateProxy) {
						emp.getEmployeeRole().set(z, ((EmployeeRole)((HibernateProxy)emp.getEmployeeRole().get(z)).getHibernateLazyInitializer().getImplementation()));
					}
					if (emp.getEmployeeRole().get(z).getCode() instanceof HibernateProxy) {
						emp.getEmployeeRole().get(z).setCode(((CodeValueRole)((HibernateProxy)emp.getEmployeeRole().get(z).getCode()).getHibernateLazyInitializer().getImplementation()));
					}
				}
			}
		}
		
		results.put("currentEmployee", emp);
		results.put("roleCode", ub.getRole()); 
		results.put("role", ub.getRoleName()); 
		results.put("language", LocaleSelector.instance().getLocaleString().substring(0, 2));
		
		Object tree = null;
		try {
			EmployeeRole er = ub.getCurrentSystemUser();
			if (er != null) {
				results.put("employeeRoleId", er.getInternalId());
				HBSManager hbs = new HBSManager();
				// FIXME use this: to avoid double serialization
				// tree = hbs.getTree(er.getInternalId(), false, true, null, null, null);
				tree = hbs.getJsonTree(er.getInternalId(), false, true, null, null, null);
			}
		} catch (Exception e) {
			log.error("Error getting HBS json tree", e);
		}
		
		results.put("sdLocs", tree);
		
		results.put("passwordExpired", ub.isPasswordExpired()); // if true go to change password!
		results.put("loginOptions", ub.hasLoginOptions()); // if true go to options!
		
		AdviseMsgAction msgAction = AdviseMsgAction.instance();
		
		results.put("message", msgAction.getMessage());
		// results.put("testMessage", msgAction.getTestMessage());
		
		return results;
	}

	protected void setLanguage(String lang) {
		if (("it").equals(lang)) {
			LocaleSelector.instance().setLocaleString("it_IT");	
		} else if (("de").equals(lang)) {
			LocaleSelector.instance().setLocaleString("de_DE");	
		} else if (("en").equals(lang)) {
			LocaleSelector.instance().setLocaleString("en_UK");	
		}
	}
	
//	protected void setDefaultLanguage() {
//		UserBean ub = UserBean.instance();
//		
//		Employee emp = ub.getCurrentEmployee();
//		
//		if (emp != null) {
//			if (emp instanceof HibernateProxy) {
//				emp = ((Employee)((HibernateProxy)emp).getHibernateLazyInitializer().getImplementation());
//			}
//			
//			if (emp.getDefaultLanguageCode() != null && emp.getDefaultLanguageCode().getCode() != null) {
//				setLanguage(emp.getDefaultLanguageCode().getCode());
//			}
//		}
//	}
	
	protected Identity executeLogin(String username, String password) {
		Identity identity = Identity.instance();
		Credentials phiCredentials = identity.getCredentials();

		phiCredentials.setUsername(username);
		phiCredentials.setPassword(password);
		
		//FIXME!!!!! langFr!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// If language isnt it en or de set default to avoid:
		// at com.phi.rest.dmz.AuthenticationServlet.doPost(AuthenticationServlet.java:154)
		// at com.phi.security.BaseAccessControlAction.findAdditionalRoles(BaseAccessControlAction.java:545)
		// Caused by: org.hibernate.QueryException: could not resolve property: langFr of: com.phi.entities.dataTypes.CodeValueRole [SELECT empRole.internalId, case when roleCode.langFr is not null then roleCode.langFr else roleCode.displayName end as name, roleCode.code, roleCode.id, empRole.isCoordinator, specialization.id FROM com.phi.entities.baseEntity.EmployeeRole empRole JOIN empRole.code roleCode LEFT JOIN empRole.specialization specialization WHERE empRole.employee.internalId = :employeeId AND empRole.effectiveTime.low is not null AND (empRole.effectiveTime.high is null OR empRole.effectiveTime.high > :today) ORDER BY 2 ASC]
		
		identity.login();
		return identity;
	}
	
	protected Map<String,Object> getResults(HttpServletRequest req, Identity identity) throws Exception {
		Map<String,Object> results;
		
		if (identity.isLoggedIn()) {
			
			results = getDetails(req, true); // get employee details
			
		} else {
			results = new HashMap<String, Object>();
			String error = ResourceBundle.getBundle("bundle.error.messages", new java.util.Locale(Locale.instance().getLanguage())).getString(AccountException.WRONG_ACCOUNT_MESSAGE);
			results.put("error", error);
			log.error("Login error: " + error);
		}
		
		return results;
	}
}
