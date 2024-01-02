package com.phi.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.actions.EmployeeAction;
import com.phi.entities.role.Employee;

/**
 * Integration servlet called by Central Authentication Service (CAS)
 * 
 * For more info about CAS, see:
 * https://wiki.jasig.org/display/CASC/CAS+Client+for+Java+3.1
 * 
 * For more info about Jasig CAS Client for Java in the web.xml, see:
 * https://wiki.jasig.org/display/CASC/Configuring+the+Jasig+CAS+Client+for+Java+in+the+web.xml
 * 
 * Define servlet in solution web.xml:
 * 
 * <servlet>
		<servlet-name>CASIntegrationServlet</servlet-name>
		<servlet-class>com.phi.cs.integration.CASIntegrationServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>CASIntegrationServlet</servlet-name>
		<url-pattern>/CASIntegrationServlet</url-pattern>
	</servlet-mapping>

 * Call servlet via CAS by configuring CAS Authentication Filter in the web.xml
 * 
 * 	<filter>
   			<filter-name>CAS Authentication Filter</filter-name>
    		<filter-class>org.jasig.cas.client.authentication.AuthenticationFilter</filter-class>
    		<init-param>
    			<param-name>casServerLoginUrl</param-name>
      			<param-value>https://localhost:8443/cas-server-webapp-3.4.8/login</param-value>
    		</init-param>
    		<init-param>
      			<param-name>service</param-name>
      			<param-value>http://localhost:8080/spisal/CasIntegrationServlet</param-value>
    		</init-param>
      		<init-param>
      			<param-name>renew</param-name>
      			<param-value>false</param-value>
    		</init-param>
    		<init-param>
     	 		<param-name>gateway</param-name>
      			<param-value>false</param-value>
    		</init-param>
  		</filter>

 * 
 * @author Antonio Gulotta
 */

@Name("CasIntegrationServlet")
public class CasIntegrationServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4525706385399555731L;
	private static final Logger log = Logger.getLogger(CasIntegrationServlet.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doGet(req, resp);
		this.service(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
		this.service(req, resp);
	}

	protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {

		new ContextualHttpServletRequest(req) {

			@Override
			public void process() {
				String username = null;
				
				try {	
					if (req.getUserPrincipal() != null) {
						//Request coming from CAS
						//FIXME: Find something stronger
						username = req.getUserPrincipal().getName();
					
						if ((username != null) && !("".equals(username)))
							doWork(req, resp);
						else
							writeResponse(resp, "Error: user not specified from CAS! (received username: " + username);

					} else 
						writeResponse(resp, "Error: user principal not specified from CAS! (received req.getUserPrincipal(): " + req.getUserPrincipal());
					
				} catch (Exception e) {
					writeResponse(resp, e.getMessage());
				}
			}
		}.run();

	} 

	public void doWork(HttpServletRequest req, HttpServletResponse resp) throws Exception{
		try {
			CatalogAdapter ca=CatalogPersistenceManagerImpl.instance();
			String convIdParam = Conversation.instance().getId();
			
			//Get username from cas HttpServletRequest
			String 	username = req.getUserPrincipal().getName();
			
			//Get local employee from username
			Employee emp = getEmployee(resp, ca, username);

			if (emp == null){
				//log error and writeResponse already written in getEmployee method.
				//FacesErrorUtils.addErrorMessage(AccountException.CAS_OK_NO_PHI_ACCOUNT);
				//resp.sendRedirect( "common/jsp/login.seam?cid=" + convIdParam);
				return;
			}
				
			EmployeeAction employeeAction = EmployeeAction.instance();
			employeeAction.inject(emp);
			
			Identity identity = Identity.instance();
			Credentials phiCredentials = identity.getCredentials();

			try {
				phiCredentials.setUsername(emp.getUsername());
				phiCredentials.setPassword(emp.getPassword());
			
			} catch (Exception e) {
				String message = "Error setting username and password";
				log.error(message, e);
				writeResponse(resp, message);

				//resp.sendRedirect( "home.seam?cid=" + convIdParam);
				return;
			}

			/* 	
			 * AUTHENTICATE
			 * (with SEAM identity: it calls AccessControl authenticate().
			 * 
			 */

			String login = identity.login();
			if (login == null) {
				String message = "Error executing identity.login()";
				log.error(message);
				writeResponse(resp, message);
				
				//resp.sendRedirect( "common/jsp/login.seam?cid=" + convIdParam);				
				return;
			}
			
			// Like in options page, set role and service delivery location after login.
			// accessControl authenticate method set as role the first passed.
			UserBean ub = (UserBean) Component.getInstance("userBean");
			
			//ub.setCurrentSystemUser(empRole);
			
			//when user is login with servlet NO OPTION page is required
			//ub.setLoginOptions(false);
			/* Se l'utente ha solo un ruolo ed è abilitato solo su una ULSS bypassa le opzioni di login */
			if (ub.getEnabledRoles().size()==1)
				ub.setLoginOptions(this.showLoginOptions());
			
			else 
				ub.setLoginOptions(true); 

			//set in userBean (Session bean) the flag to remember that the user is logged via servlet.
			ub.setLoggedByServlet(true);
			
			resp.sendRedirect( "home.seam?cid=" + convIdParam);

			log.info("Logged from servlet user: "+ username);

		} catch (Exception e) {
			log.error("Error login via servlet ",e);
		}
	}

	private Employee getEmployee(HttpServletResponse resp, CatalogAdapter ca, String username) {
		
		Criteria c = ca.createCriteria(Employee.class);
		c.add(Restrictions.eq("username", username).ignoreCase());
		
		@SuppressWarnings("unchecked")
		List<Employee> listEmployee = c.list();

		if (listEmployee.size() == 1)
			return listEmployee.get(0);

		else  {
			//Employee not found 
			String message= "Error searching employee in PHI with username: " + username + ". Employee found: " + listEmployee.size();
			log.error(message);
			
			writeResponse(resp, message);
			return null;
		}
	}
	
	private void writeResponse(HttpServletResponse resp, String responseBody) {
		try {
			resp.setContentType("text/html");
			java.io.PrintWriter out;
			try {
				out = resp.getWriter();
				out.println("<html>");
				out.println("<head>");
				out.println("<title>PHI Integration</title>");
				out.println("</head>");
				out.println("<body>");
				out.println(responseBody);
				out.println("</body>");
				out.println("</html>");

				out.close();
			} catch (IOException e) {
				log.error("Error writing response", e);
			}
		} catch (Exception e) {
			log.error("Error during login",e);
		}
	}
	
	/*
	 * true  - se tra le sdl c'è più di una ULSS 
	 * false - se tra le sdl c'è solo una ULSS
	 * */
	public boolean showLoginOptions() {
		
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		Long uocCounter = 0L;
		String uocCount = "SELECT COUNT(*) FROM ServiceDeliveryLocation sdl JOIN sdl.code type " + 
				"WHERE type.code = :code AND sdl.internalId IN (:sdLocs)";
		
		try {
			List<Long> longSdlLocs = UserBean.instance().getSdLocs();
			
			if (longSdlLocs != null &&longSdlLocs.size()>1) {

				if (longSdlLocs != null && longSdlLocs.size()>1) {
					Query qry = ca.createQuery(uocCount);
					qry.setParameter("sdLocs", longSdlLocs);
					qry.setParameter("code", "UOC");
					uocCounter = (Long) qry.getSingleResult();
					if(uocCounter > 1){
						return true;
					}else{
						qry.setParameter("code", "ARPAV");
						uocCounter = (Long) qry.getSingleResult();
						if(uocCounter > 1){
							return true;
						}else{
							qry.setParameter("code", "ULSS");
							uocCounter = (Long) qry.getSingleResult();
							if(uocCounter > 1){
								return true;
							}else{
								qry.setParameter("code", "REGIONE");
								uocCounter = (Long) qry.getSingleResult();
								if(uocCounter  > 1){
									return true;
								}
							}
						}
					}
				}
			}
			
			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		}
	}

}
