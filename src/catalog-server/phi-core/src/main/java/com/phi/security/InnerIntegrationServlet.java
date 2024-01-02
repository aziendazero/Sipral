package com.phi.security;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.model.SelectItem;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.servlet.ContextualHttpServletRequest;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryGetter;
import com.phi.cs.repository.RepositoryManager;
import com.phi.entities.Token;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.actions.EmployeeAction;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.dataTypes.PQ;
import com.phi.entities.role.Employee;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.ps.ProcessManager;
import com.phi.ps.TreeBean;
import com.phi.ps.ViewManager;
import com.phi.security.sso.TokenGenerator;
import com.phi.security.sso.UUIDTokenGenerator;



/**
 * Integration for PHI authentication and process Start
 * 
 * Define servlet in solution web.xml :
	<servlet>
		<servlet-name>InnerIntegrationServlet</servlet-name>
		<servlet-class>com.phi.cs.integration.InnerIntegrationServlet</servlet-class>
	</servlet>
	<servlet-mapping>
		<servlet-name>InnerIntegrationServlet</servlet-name>
		<url-pattern>/InnerIntegrationServlet</url-pattern>
	</servlet-mapping>
 * 
 * 
 * PatientAction.temporary.injectedPatIdVisitNumb=XXXX
 * 
 * Usage examples (phi solution name, user/roles, processes, etc..) depends from your environment:
 * http://localhost:8080/PHI_AMB/integrationServlet?username=admin&password=admin1&roleCode=1
 * http://localhost:8080/PHI_AMB/integrationServlet?username=admin&password=5f4dcc3b5aa765d61d8327deb882cf99&roleCode=1
 * http://localhost:8080/PHI_AMB/integrationServlet?username=admin&password=admin1&roleCode=1&PatientAction.temporary.isEnabled=138
 * http://localhost:8080/PHI_AMB/integrationServlet?username=admin&roleCode=1&lang=it&processName=MOD_Patients%2FCORE%2FPROCESSES%2FCreate_Patient&sdlLoginList=15;19&Patient=17
 * http://localhost:8080/PHI_AMB/integrationServlet?username=admin&roleCode=1&lang=it&processName=MOD_Patients%2FCORE%2FPROCESSES%2FSearch_Patients&sdlLoginList=15;19&PatientList=17;74
 * http://localhost:8080/PHI_AMB/integrationServlet?token=9CF4BED7-F520-11E2-8221-6C3BE5F32BB1
 * 
 * parameters:
 * username:  user for the employee you want to log-in:  e.g. username=admin --no more mandatory if skipUsernameCheck from seam.properties is set to true
 * password:  password (if it is exipred, authentication fails). Mandatory only if set enableIntegrationServletPasswordProtection=true in seam.properties
 * roleCode:  code of the Code of the EmployeeRole used for log in. e.g: "1" --mandatory
 * lang:  language used for login, according to standard Java Locale.  e.g.: "en"  --optional (defalt english)
 * processName:   process name to be started according to processManagerImpl processName. e.g. "MOD_Patients/CORE/PROCESSES/Create_Patient" --optional
 * sdlLoginList:      list of sdl extension id, mapped into table service_delivery_location_id, used as parameter for login selection, instead of sdlLoginList --requested by Birmingham   
 * sdlInternalIdLoginList:   list of internal Ids of ServiceDeliveryLocation that must be selected at login, separated by ; e.g:  10;13;1293  --optional: if not passed all allowed sdl for role are selected.
 * token: check TOKEN SECTION
 * 
 * moduleToOpen: name of DashBoard module to be opened as defalut once called the servlet. e.g. 
 * 10.172.254.48:8180/PHI_CI/InnerIntegrationServlet?username=fabrizia&lang=it&roleDisplay=AMMINISTRATIVO&uorg=3744&PatientEncounter=181559&moduleToOpen=DrugPrescriber
 *
 * additional Paramters:
 * 
 * Allowed values: 
		ClassAction.temporary.tempValName=abcd -->  set temporary variable with given tempValName name and value abcd in ClassAction
		if temporary value contains externalId, inject also, es: http://localhost:8080/PHI_AMB/InnerIntegrationServlet?username=admin&password=admin&roleCode=16&PatientAction.temporary.externalId=RRK_3172e8e2-f7c9-482b-ab36-102ab1475b04
		Class.property.prop=value  --> inject the object of class with provided property value
		Class=54354 ---> inject object of Class with provided internalId
		ClassList=4343;4354;222  --> inject list of object of Class, with given internal ids
 *  
 *  Examples
 *      &Patient=15&PatientEncounter=19&ServiceDeliveryLocationList=18;34&PatientAction.temporary.variableName=true
 *      &Patient.externalId=434324&PatientEncounter.g2Epsd=65465&ServiceDeliveryLocation.g2Strt0=372897
 *  
 *  //removed: inject flex object into flexProxy with f_ like &f_variable=value
 *  
 * see method injectAdditionalPar for more details
 *    
 * 
 * TOKEN SECTION
 * added token security management that must be added to the URL.
 * localhost:8080/PHI_DOC/integration.lisa?action=token&username=maxtest : URL to get TOKEN
 * 
 * token=NOTOKEN can be used to skipped token
 * 
 * @author Francesco Bragagna
 */

@Name("InnerIntegrationServlet")
public class InnerIntegrationServlet extends HttpServlet {

	private static final long serialVersionUID = -952155915457914225L;

	private static final Logger log = Logger.getLogger(InnerIntegrationServlet.class);


	//	private String username = "";
	//	private String lang = "";

	//	private EmployeeRole empRole = null;
	//	private Employee emp = null;
	//	private List<Long> loginRequestedSdlId;

	//	private FacesContextFactory facesContextFactory;
	//	private Lifecycle lifecycle;
	//	private FacesContext context;

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
				try {
					doWork(req,resp);
				} catch (Exception e) {
					writeResponse(resp, e.getMessage());
				}
			}
		}.run();

	} 

	/*
	 * Moved from inside dContextualHttpServletRequest, where it was a class
	 *  method, to here, as method of ImportServlet class
	 */
	@SuppressWarnings("unchecked")
	public void doWork(HttpServletRequest req, HttpServletResponse resp) throws Exception{

		try {
			
			log.info("[cid="+Conversation.instance().getId()+"] " + req.getRequestURL() + (req.getQueryString() != null ? "?"+req.getQueryString():""));
			
			RepositoryGetter rm = RepositoryManager.instance(); 
			CatalogAdapter ca=CatalogPersistenceManagerImpl.instance();

			boolean enableIntegrationServletPasswordProtection = "true".equals(rm.getSeamProperty("enableIntegrationServletPasswordProtection"));
			boolean provideEncryptedPasswordToIntegrationServlet = "true".equals(rm.getSeamProperty("provideEncryptedPasswordToIntegrationServlet"));
			boolean skipUsernameCheck = "true".equals(rm.getSeamProperty("skipUsernameCheck"));
			boolean md5_passwords = "true".equals(rm.getSeamProperty("MD5_passwords"));

			String username = req.getParameter("username"); 
			if (username==null) username = (String)req.getAttribute("username");
			String token = req.getParameter("token"); 
			if (token==null) token = (String)req.getAttribute("token");
			String action = req.getParameter("action"); 
			if (action==null) action = (String)req.getAttribute("action");

			String singlePage=(String)req.getParameter("singlePage");
			if (singlePage==null) singlePage = (String)req.getAttribute("singlePage");

			LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
			FacesContextFactory facesContextFactory = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
			Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
			// create faces context
			FacesContext context = facesContextFactory.getFacesContext(getServletContext(),req,resp,lifecycle);

			// Get UserBean instance and set LoggedByServelt as True. It will be useful
			// in the login procedure to double check if the user is trying to login by servelt			
			UserBean ub = (UserBean) Component.getInstance("userBean");

			//set in userBean (Session bean) the flag to remember that the user is logged from servlet.
			ub.setLoggedByServlet(true);

			/////// 
			///////  START GETTING PARAMETER FROM REQUEST
			///////

			String password = req.getParameter("password");

			//CHECK TOKEN
			if ("token".equals(action) && !"NOTOKEN".equals(token)){
				//token generation
				String tokenVal = tokenGen(username);
				writeResponse(resp,tokenVal);
				return;
			}

			if (!enableIntegrationServletPasswordProtection && token!=null && !"NOTOKEN".equals(token)){
				String userNameFromToken=tokenIsValid(token,username, skipUsernameCheck);
				if (userNameFromToken==null){
					log.error("Error: token not valid! [validation failed]");
					writeResponse(resp, "Error: token not valid! [validation failed]");
					return;
				}
				else{
					username=userNameFromToken;
				}
			}

			String roleCode = req.getParameter("roleCode");
			if (roleCode==null) roleCode = (String)req.getAttribute("roleCode");

			//optional roleDisplay instead of roleCode

			String roleDisplay = req.getParameter("roleDisplay");
			if (roleDisplay != null && !roleDisplay.isEmpty()) {
				Criteria c = ca.createCriteria(CodeValueRole.class);
				c.add(Restrictions.eq("displayName", roleDisplay));
				List<CodeValueRole> l = c.list();
				if (l == null || l.size() < 1) {
					log.error("Unable to find role with DisplayName="+roleDisplay);
					writeResponse(resp, "Invalid roleDisplay:"+ roleDisplay +";");
					return;
				}
				else {
					CodeValueRole cv = l.get(0);
					roleCode=cv.getCode();
				}
			}

			String processName = req.getParameter("processName");
			if (processName==null) processName = (String)req.getAttribute("processName");

			Contexts.getConversationContext().set("processNameInnerServlet",processName);

			String lang = req.getParameter("lang");
			if (lang==null) lang = (String)req.getAttribute("lang");
			String defaultLanguage = "en";
			if (lang==null || lang.isEmpty())  
				lang= defaultLanguage;

			////
			// START CHECKING MANDATORY PARAMETERS:  username and roleCode.
			////

			if ((username==null && !skipUsernameCheck) || "".equals(username) || roleCode ==null || roleCode.equals("") ) {
				log.error("Error: user or role not specified!");
				writeResponse(resp, "Error: user or role not specified! (received: username="+username+"; roleCode:"+ roleCode +";");
				return;
			}

			Employee emp= getEmployee(resp, ca, username);

			if (emp == null) {
				//log error and writeResponse alredy written in getEmployee method.
				return;
			}

			EmployeeAction eAction = EmployeeAction.instance();
			eAction.inject(emp);

			//password not checed if enableIntegrationServletPasswordProtection=false.
			if (enableIntegrationServletPasswordProtection) {
				//check password.
				String message = "Authentication failure. (Password check is active)"; 
				if (password == null || password.isEmpty() ){ 
					log.error(message);
					writeResponse(resp, message);
					return;
				}

				if (md5_passwords) {
					if (!provideEncryptedPasswordToIntegrationServlet)
						password = Security.crypt(password);
				}

				if (!emp.getPassword().equals(password)) {
					log.error(message);
					writeResponse(resp, message);
					return;
				}
			}


			//getEmployeeRole is needed to check before authentication if the role paramer passed by servlet is valid.
			//in this way this method can return an error message to servlet caller. Otherwise doLogin() method called later
			//which calls AccessControlBaseAction.authenticate() does not return any error.

			EmployeeRole empRole= getEmployeeRole(emp, roleCode, resp);
			if (empRole == null){
				return;
			}

			/////// 
			//// Prepare credentials.
			//////

			Identity identity = Identity.instance();
			Credentials phiCredentials = identity.getCredentials();

			try {
				phiCredentials.setUsername(emp.getUsername());
				phiCredentials.setPassword(emp.getPassword());

			} catch (Exception e) {
				String message = "Error setting username and password";
				log.error(message,e);
				writeResponse(resp, message);
				return;
			}

			///////
			//// AUTHENTICATE 
			//// (with SEAM identity: it calls AccessControl authenticate().
			///////

			//set language before login
			LocaleSelector.instance().setLanguage(lang); 

			String login = identity.login();
			if (login==null) {
				log.error("Error executing identity.login().");
				writeResponse(resp, "Error executing login()");
				return;
			}

			//SELECTED LOGIN for different solutions. If sdl id list is not specified as a parameter
			//the list will be retrieve considering the role specified in the url

			List<Long> loginRequestedSdlId = new ArrayList<Long>();
			loginRequestedSdlId = getSdlId4Login(req,ca,empRole);

			// Like in options page, set role and service delivery location after login.
			// accessControl authenticate method set as role the first passed.
			ub.setCurrentSystemUser(empRole);

			//DO NOT CALL TREEBEAN:
			//TreeBean instance is executed when home is properly redirected by pages and rendered.
			//it's called by home.xhtml method treeBean.processPath()
			//TreeBean.instance().getProcessList();
			//FIX: TreeBean is created only first time session is created. If Servlet is reused on already active session
			//the process list is not updated, if the user change its role with another url.
			TreeBean t = TreeBean.instance();
			t.init();
			t.resetProcessList(t.getProcessList());


			//set as selected SDLoc, the sdloc passed as paramter sdlLoginList
			//check before if the passed list is valid.
			//if no sdlLoginList is passed, use all the enabled.
			List<Long> enabledSdlocIds = new ArrayList<Long>();
			for (SelectItem it : ub.getEnabledSdlocs()) {
				enabledSdlocIds.add((Long)it.getValue());
			}

			if (loginRequestedSdlId.isEmpty()) {
				//if no valid SDL passed, or empty parameter, set as selected all the enabled.
				ub.setSdLocs(enabledSdlocIds);
			}
			else if(ub.getExternalUorgId() != null) {
//				means that a list of sdloc has been already loaded and filtered depending on uorg and role; see line 369: loginRequestedSdlId = getSdlId4Login(req,ca,empRole);
				ub.setSdLocs(loginRequestedSdlId);
			}
			else {
				if (enabledSdlocIds.containsAll(loginRequestedSdlId)) {
					//all the valid passed Sdl id, are contained in enabledSdloc. Set list passed as selected. 
					ub.setSdLocs(loginRequestedSdlId);
				}
				else {
					//some sdl selected are not enabled for this role. set intersection, if any.
					String req1 = Arrays.toString(loginRequestedSdlId.toArray());
					loginRequestedSdlId.retainAll(enabledSdlocIds);
					if (loginRequestedSdlId.isEmpty()) {
						//intersection is empty. set all enabled. 
						log.error("SDL id requested " +req1+" is not valid for this user role. All enabled SDL are set as selected.");
						ub.setSdLocs(enabledSdlocIds);
					}
					else {
						//intersection has lost some id, but contains some valid. use them.
						log.error("Not all SDL requested " +req1+" are valid for this user role. only some "+Arrays.toString(loginRequestedSdlId.toArray())+" are set as selected.");
						ub.setSdLocs(loginRequestedSdlId);
					}
				}
			}

			//PHI CI Treviso set UNITA' ORGANIZZATIVA
			//FIXME: missing check if uorg exists and if the user is really allowed to it
			String unitaOrg = req.getParameter("uorg");
			if(unitaOrg != null){
				ub.setExternalUorgId(Long.parseLong(unitaOrg)); 
			}


			//when user is login with servlet NO OPTION page is required
			ub.setLoginOptions(false); 

			if(singlePage!=null && "true".equals(singlePage)){
				ub.setRepositoryViewer(true);
			}

			String convIdParam = Conversation.instance().getId();

			if(singlePage!=null && "true".equals(singlePage)){
				resp.sendRedirect( "homeRepository.seam?cid=" + convIdParam);
			}
			else{
				resp.sendRedirect( "home.seam?cid=" + convIdParam); //153
			}


			if(!"true".equals(singlePage)){
				//??? chiedere a sara.
				ViewManager apc = null;
				if(context != null) {
					apc = (ViewManager)((HttpSession)context.getExternalContext().getSession(false)).getAttribute("ViewManager");
				}
				if (apc != null) {
					apc.setViewId("home");
				}
			}


			//Patient and patientEncounter can be passed as html attributes instead of parameters. First inject them, if any.
			Map<String,Object> attributeMap = new HashMap<String,Object>();
			if (req.getAttribute("Patient") != null)
				attributeMap.put("Patient", req.getAttribute("Patient"));

			if (req.getAttribute("PatientEncounter") != null)
				attributeMap.put("PatientEncounter", req.getAttribute("PatientEncounter"));
			injectAdditionalPar(attributeMap);

			/////
			//  Inject additional object in conversation.
			//  additional parameters are couple key/value, 
			//  each of them represents a object instance to be injected with a specific className, or attribute
			//  see injectAdditionalParameter java doc
			/////

			Map<String,Object> parameterMap = req.getParameterMap(); 
			injectAdditionalPar(parameterMap);


			//////
			//  Start process, if any
			/////

			if (processName != null) {
				//Start process
				ProcessManager procMan =  (ProcessManager)Component.getInstance("processManager");
				try {
					procMan.beginConversation(processName);
					procMan.startProcessFromName(processName);
				} catch (PhiException e) {
					log.error("Error starting process ", e);
				}
			}

			log.info("logged from servlet user: "+ username + " with role code: "+ roleCode + (processName != null && !processName.isEmpty() ? " for process "+ processName : ""));


		} catch (Exception e) {
			log.error("Error login via servlet ",e);
		}
	}


	protected String tokenIsValid(String token,String usernameIn, boolean skipUserNameCheck){
		boolean tokenIsValid=false;
		log.info("token: " + token + " - userName: " + usernameIn);
		String usernameFromDb=null;
		try {	
			if(token!=null){
				//"select username,expiration_time from token where username=:1 and value=:2";
				CatalogAdapter ca=CatalogPersistenceManagerImpl.instance();
				Criteria criteria=ca.createCriteria(Token.class);
				criteria.add(Restrictions.eq("value", token));
				if(!skipUserNameCheck){
					criteria.add(Restrictions.eq("userName", usernameIn));}
				Token tokenObject= (Token)criteria.uniqueResult();
				if(tokenObject!=null && tokenObject.getExpirationTime().after(new Date()) ){
					log.info("token is valid!");
					tokenIsValid=true;
					usernameFromDb=tokenObject.getUserName();
					ca.delete(tokenObject);
					ca.flushSession();
				}
			}
		}catch (Exception e) {
			log.error("Token is valid check error:",e);
		}
		return usernameFromDb;
	}

	private String tokenGen(String username){
		String tokenValue="";

		try {
			CatalogAdapter ca=CatalogPersistenceManagerImpl.instance();
			TokenGenerator tokenGenerator= new UUIDTokenGenerator();
			tokenValue= tokenGenerator.getToken();
			//save token
			Token token = new Token();
			token.setValue(tokenValue);
			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(new Date()); // sets calendar time/date
			cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
			token.setExpirationTime(cal.getTime());

			//userBean.get
			token.setUserName(username);
			ca.create(token);
			ca.flushSession();
		} catch (PersistenceException e) {
			log.error("Error durong token gen", e);
		}

		return tokenValue;
	}


	//	private boolean doLogin(HttpServletResponse resp, String singlePage) {
	//
	//		
	//	}
	//
	//
	//
	//	public boolean authenticate() throws Exception {
	//		return this.authenticate(null);
	//	}
	//
	//	/* 
	//	 * call method authenticate of AccessControl
	//	 */
	//	public boolean authenticate(String singlePage) throws Exception {
	//
	//		
	//	}


	/**
	 * Inject additional object in the conversation to be created.
	 * Each couple key/value of addtional paramter represents:
	 * 
	 * Allowed values: 
		XxxxAction.temporary.tempValName=abcd -->  set temporary value in XxxxAction
		Class.property.prop=value  --> inject the object of class with provided property value
		Class=54354 ---> inject object of Class with provided internalId
		ClassList=4343;4354;222  --> inject list of object of Class, with given internal ids
	 * 
	 * 
	 * @param parameterMap
	 */
	protected void injectAdditionalPar(Map<String,Object> parameterMap) {

		if (parameterMap.isEmpty())
			return;

		Context conversation = Contexts.getConversationContext();
		CatalogAdapter ca;
		UserBean ub = (UserBean) Component.getInstance("userBean");

		//		try {
		ca = CatalogPersistenceManagerImpl.instance();
		//		} catch (ApplicationException e) {
		//			log.error("error getting Catalog Adapter. No additional parameter added.");
		//			return;
		//		}


		Set<String> keys =  parameterMap.keySet();

		//// FIRST: find Patient or PatientEncounter keys, which must be injected first to above Conversation cleaning.
		String patEncKey = "";
		String patientKey = "";
		for (String key : keys) {
			if (key.equals("PatientEncounter") || key.contains("PatientEncounter.") ){
				if (patEncKey.isEmpty())
					patEncKey = key;
				else {
					log.error("multiple keys referring to PatientEncouter! "+key);
				}
			}

			if (key.equals("Patient") || key.contains("Patient.") ){
				if (patientKey.isEmpty())
					patientKey = key;
				else {
					log.error("multiple keys referring to Patient! "+key);
				}
			}
		}

		//First inject PatientEncounter or Patient
		if (!patEncKey.isEmpty()) {
			//ignore patientKey, just inject PatientEncounter, which inject also patient.
			Object keyValue = parameterMap.get(patEncKey);
			String value = "";
			if (keyValue instanceof String[]){
				value = ((String[]) keyValue)[0]; 
			}else if (keyValue instanceof String){
				value = (String)keyValue;
			}else if (keyValue instanceof Long){
				value = (String)keyValue.toString();
			}

			if (patEncKey.equals("PatientEncounter")) {
				if (value == null || value.isEmpty()){
					log.error("unabe to inject empty patient encounter id");
				}
				else {
					//PatientEncounterAction patEncAct = PatientEncounterAction.instance();
					BaseAction patEncAct = (BaseAction)Component.getInstance("PatientEncounterAction");
					patEncAct.setId(value);
					try {
						//inject PatientEncounter by id
						patEncAct.injectbyId();
					} catch (PhiException e) {
						log.error("error injecting PatientEncounter "+value+" from Servlet");
					}
				}
			}
			else {
				//look for keys like PatientEncounter.g2Epsd=65465 
				try {
					//PatientEncounterAction patEncAct = PatientEncounterAction.instance();
					BaseAction patEncAct = (BaseAction)Component.getInstance("PatientEncounterAction");
					String property = patEncKey.substring("PatientEncounter.".length(), patEncKey.length());
					
					//Object val = changeClass(value, PatientEncounter.class, property);
					patEncAct.getEqual().put(property, value);
					
					List<BaseEntity> listPatEnc = patEncAct.list();
					if (listPatEnc == null || listPatEnc.isEmpty()) {
						log.error("Error reading PatientEncounter, no result found. Parameter:"+patEncKey+" value:"+value);
					}
					else if (listPatEnc.size() != 1) 
						log.error ("Multiple result for this paramenter. Injecting first found.  Parameter:"+patEncKey+" value:"+value+ " patientEncounter internalId: "+listPatEnc.get(0).getInternalId());

					//inject PatientEncounter by property
					else
					patEncAct.inject(listPatEnc.get(0));
				} catch (PhiException e) {
					log.error("Error reading PatientEncounter. Parameter:"+patEncKey+" value:"+value);
				}

			}
		}
		else if (!patientKey.isEmpty()){
			//inject Patient, PatientEncounter reference are not provided.

			Object keyValue = parameterMap.get(patientKey);
			String value = "";
			if (keyValue instanceof String[]){
				value = ((String[]) keyValue)[0]; 
			}else if (keyValue instanceof String){
				value = (String)keyValue;
			}else if (keyValue instanceof Long){
				value = (String)keyValue.toString();
			}

			if (patientKey.equals("Patient")) {
				if (value == null || value.isEmpty()){
					log.error("unabe to inject empty patient encounter id");
				}
				else {
					//PatientAction patientAct = PatientAction.instance();
					BaseAction patientAct = (BaseAction)Component.getInstance("PatientAction");
					patientAct.setId(value);
					try {
						//inject Patient by id
						patientAct.injectbyId();
					} catch (PhiException e) {
						log.error("error injecting Patient "+value+" from Servlet");
					}
				}
			}
			else {
				//look for keys like Patient.externalId=434324 or e.g. PatientEncounter.id.ANAG=12345
				try {
					//PatientAction patientAct = PatientAction.instance();
					BaseAction patientAct = (BaseAction)Component.getInstance("PatientAction");
					String property = patientKey.substring("Patient.".length(), patientKey.length());
					//Object val = changeClass(value, Patient.class, property);
					patientAct.getEqual().put(property, value);
					List<BaseEntity> listPatient = patientAct.list();
					if (listPatient == null || listPatient.isEmpty()) {
						log.error("Error reading Patient, no result found. Parameter:"+patientKey+" value:"+value);
					}
					else if (listPatient.size() != 1) 
						log.warn ("Multiple result for this paramenter. Injecting first found.  Parameter:"+patientKey+" value:"+value+ " Patient internalId: "+listPatient.get(0).getInternalId());

					//inject Patient by property
					else
						patientAct.inject(listPatient.get(0));
				} catch (PhiException e) {
					log.error("Error reading Patient. Parameter:"+patientKey+" value:"+value);
				}

			}
		}



		//inject remainging objects: 
		//XxxxAction.temporary.tempValName=abcd  set temporary value
		//Class.property.prop=value  --> inject the object whit this property value
		//Class=54354 ---> inject object with this internalId
		//ClassList=4343;4354;222


		for (String key : keys) {
			if (key.equals(patEncKey) || key.equals(patientKey)) {
				continue;
			}
			if (key.equals("username") || key.equals("password") || key.equals("roleCode") || key.equals("roleDisplay")  || key.equals("lang")  || key.equals("processName") || key.equals("sdlLoginList") || key.equals("sdlInternalIdLoginList") || key.equals("sdlExtensionLoginList") || key.equals("token") || key.equals("uorg")) 
				continue;

			//			Inject Eventual Dashboard Module name in conversation, to be opened as default after the servlet call
			if (key.equals("moduleToOpen")) {
				//adding variable in conversation;
				Object keyValue = parameterMap.get(key);
				String value = "";
				if (keyValue instanceof String[]){
					value = ((String[]) keyValue)[0]; 
				}else if (keyValue instanceof String){
					value = (String)keyValue;
				}else if (keyValue instanceof Long){
					value = (String)keyValue.toString();
				}
				conversation.set("modulesToOpen","swf/modules/"+ value +".swf");
				continue;
			}

			String value = null;
			Object keyValue = parameterMap.get(key);
			if (keyValue instanceof String[]){
				value = ((String[]) keyValue)[0]; 
			}else if (keyValue instanceof String){
				value = (String)keyValue;
			}else if (keyValue instanceof Long){
				value = (String)keyValue.toString();
			}

			if (value == null || value.isEmpty()) {
				log.error("additional paramter "+key+" not added, beacuse value is null or empty. ");
				continue;
			}

			//needed for BzIntegrationServlet.java
			if (key.startsWith("c_")){
				//adding variable in conversation;
				conversation.set(key.substring(2,key.length()), value);
				continue;
			}

			if (!key.substring(0,1).matches("[A-Z]")){
				log.error("additional parameters must start with uppercase letter, because rapresents a class name. Paramter skipped: "+key ); 
				continue;
			}


			if (key.contains(".")) {
				//XxxxAction.temporary.tempValName=abcd  set temporary value
				//Class.property=value  --> inject the object whit this property
				//Class=54354 ---> inject object with this internalId
				String[] keyParts = key.split("\\.");
				String className= keyParts[0]; 
				if (!className.substring(0,1).matches("[A-Z]")) {
					log.error("wrong class name: "+className);
					continue;
				}

				if (className.endsWith("Action") && keyParts.length == 3 && "temporary".equals(keyParts[1])) {
					//PatientAction.temporary.temporaryVariableName=value

					String temporaryValName = keyParts[2];
					try {
						BaseAction actionClass = (BaseAction) Component.getInstance(Class.forName("com.phi.entities.actions." + className), ScopeType.CONVERSATION);
						if (actionClass != null) {
							HashMap temporary = actionClass.getTemporary();
							temporary.put(temporaryValName, value);
							actionClass.setTemporary(temporary);
							//If temporary value contains externalId, inject
							//FIXME: this can eject other entities
							if (temporaryValName.equals("externalId")) {
								Criteria c = ca.createCriteria(actionClass.newEntity().getClass());
								c.add(Restrictions.eq(temporaryValName, value));
								List res = c.list();
								if (res.size()==1) {
									actionClass.inject(res.get(0));
								}
							}
						}
						else {
							log.error("class not found "+className);
						}
					} catch (Exception e) {
						log.error("additional temporary parameter for not valid className: "+className);
					}

				}
				else {
					//try make a read and inject of an object, given its property value.
					//e.g. Patient.name.fam=Rossi 
					//if multiple result, first is taken. Paramter passed is expected to identify an object in unique way.
					//e.g. ServiceDeliveryLocation.g2Strt0=372897 or ServiceDeliveryLocation.id.STRT0_ADT=123456
					try {
						BaseAction actionClass = (BaseAction) Component.getInstance(Class.forName("com.phi.entities.actions." + className+"Action"), ScopeType.CONVERSATION);
						String property = key.substring(className.length()+1,key.length());
						Object val = changeClass(value, actionClass.newEntity().getClass(), property);
						actionClass.getEqual().put(property, val);
						List<Object> objects = actionClass.list();
						if (objects == null || objects.isEmpty()) {
							log.error("Error reading ojbect, no result found. Parameter:"+key+" value:"+value);
						}
						if (objects.size() != 1) 
							log.warn ("Multiple result for this paramenter. Injecting first found.  Parameter:"+key+" value:"+value);

						actionClass.inject(objects.get(0));
					} catch (ClassNotFoundException e) {
						log.error("Class name not found for parameter "+key);
					} catch (PhiException e) {
						log.error("Error reading ojbect. Parameter:"+key+" value:"+value+ e);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				continue;

			}

			else if (key.endsWith("List")) {
				//ClassList=4343;4354;222
				String className = key.replace("List", "");
				Class<BaseEntity> clazz = BaseEntity.getDerivedClasses().get(className);
				List<BaseEntity> list = new ArrayList<BaseEntity>();
				for (String objId : value.split(";")) {
					try {
						long id = Long.valueOf(objId);
						BaseEntity rimObject = ca.get(clazz, id);
						list.add(rimObject);
					} catch (Exception e) {
						log.error("additional paramter "+key+" not complete, beacuse one value of list is not a valid object id: "+objId);
					}

				}
				PhiDataModel<BaseEntity> dm = new PhiDataModel<BaseEntity>(list, className);
				conversation.set(key, dm);
			}
			else {
				//Class=4324432
				Class<BaseEntity> clazz = BaseEntity.getDerivedClasses().get(key);

				if (clazz == null) {
					log.error("additional paramter "+key+" not added, because is an invalid Class attribute.");
					continue;
				}

				try {
					long id = Long.valueOf(value);
					BaseEntity baseEntity = ca.get(clazz, id);

					//Removed dependencies to Patient and PatientEncounter to have a CS without entities
					/*if (baseEntity!=null && ("Patient".equals(key))){
						PatientAction.instance().inject((Patient)baseEntity);
					}else if (baseEntity!=null && ("PatientEncounter".equals(key))){
						PatientEncounterAction.instance().inject((PatientEncounter)baseEntity);
					}*/

					//Generic implementation:
					if (baseEntity!=null) {
						BaseAction<?, ?> action = (BaseAction<?, ?>)Component.getInstance(key + "Action", true);
						action.inject(baseEntity);
					}

				} catch (Exception e) {
					log.error("additional paramter "+key+" not added, beacuse value is not a valid object id: "+value);
				}

			}

		}
	}

	protected Object changeClass (String value, Class c, String property) {
		boolean possiblePQ=false;
		boolean possibleED=false;
		if (property.contains(".")) {
			if (property.endsWith(".value")) {
				//possibile PQ. try to replace.
				possiblePQ=true;
				property=property.replace(".value","");
			}
			else if (property.endsWith(".string")) {
				possibleED=true;
				property=property.replace(".string","");
			}
			else {
				//name.fam? name.giv? other properties? try to use String.
				return value;
			}
		}
		PropertyDescriptor[] props = PropertyUtils.getPropertyDescriptors(c);
		PropertyDescriptor pd = null;
		Object ret= null;
		for(PropertyDescriptor prop : props){
			if(property.equals(prop.getName())){
				pd = prop;
			}
		}
		Class propertyClass = pd.getPropertyType();
		if (possiblePQ && propertyClass.equals(PQ.class)) {
			ret = Double.parseDouble(value);
		}
		else if (propertyClass.equals(String.class)) {
			ret = value;
		}
		else if (propertyClass.equals(Integer.class)) {
			ret= Integer.parseInt(value);
		} else if (propertyClass.equals(Long.class)) {
			ret= Long.parseLong(value);
		}
		else if (propertyClass.equals(Double.class)) {
			ret= Double.parseDouble(value);
		}

		return ret;
	}

	public void writeResponse(HttpServletResponse resp, String responseBody) {
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


	
//	private List<Long> getSdlId4Login(HttpServletRequest req, CatalogAdapter ca) {
//		return getSdlId4Login(req,ca,null);
//	}

	/**
	 * Define the way to get the list of Sdl Internal Id for the Login procedure
	 */
	//FIXME: the parameter empRole is not used in this definition, but is mandatory in th override method MoIntegrationServlet.getSdlId4Login
	protected List<Long> getSdlId4Login(HttpServletRequest req, CatalogAdapter ca, EmployeeRole empRole) {

		//SELECTED LOGIN for different solutions... 

		String sdlIntIdLoginList = req.getParameter("sdlInternalIdLoginList"); 
		if (sdlIntIdLoginList==null) 
			sdlIntIdLoginList = (String)req.getAttribute("sdlInternalIdLoginList");

		String sdlExtensionLoginList = req.getParameter("sdlLoginList");
		if (sdlExtensionLoginList==null) 
			sdlExtensionLoginList = (String)req.getAttribute("sdlLoginList");

		//loginRequestedSdlId is the list of sdl requested to be selected at login, from servlet paramter.
		//loginRequestedSdlId could be the id passed via sdlLoginList or the id corresponding the extensions of sdlExtensionLoginList
		List<Long> loginRequestedSdlId = new ArrayList<Long>();
		if (sdlIntIdLoginList != null) {
			List<String> loginRequestedSdlIdString = new ArrayList(Arrays.asList(sdlIntIdLoginList.split(";")));
			Iterator<String> itr = loginRequestedSdlIdString.iterator();
			while (itr.hasNext()) {
				loginRequestedSdlId.add(Long.valueOf(itr.next()));
			}		
		}

		String customer = (String)Contexts.getApplicationContext().get("CUSTOMER"); 
		if (sdlExtensionLoginList != null && !sdlExtensionLoginList.isEmpty() ) {
			String[] splittedSdloc= sdlExtensionLoginList.split(";");
			if(splittedSdloc!=null && splittedSdloc.length>0){
				String root= "HBS";
				if ("TREVISO".equals(customer) || "MODENA".equals(customer) ) {
					root = "STRT0_ADT";
				}

				Criteria criteria=ca.createCriteria(ServiceDeliveryLocation.class);
				criteria.createCriteria("id").add(Restrictions.in("extension",Arrays.asList(splittedSdloc))).add(Restrictions.eq("root",root));
				criteria.setProjection( Projections.projectionList()
						//.add( Projections.property("internalId"), "internalId" )
						.add(Projections.distinct(Projections.property("internalId")),"internalId"));
				loginRequestedSdlId=criteria.list();
			}
		}

		return loginRequestedSdlId;
	}


	//	private void initContext(){
	//		LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
	//		facesContextFactory = (FacesContextFactory)FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
	//		lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
	//	}



	private Employee getEmployee(HttpServletResponse resp, CatalogAdapter ca, String username) {
		//Search Employee in PHI

		Criteria c = ca.createCriteria(Employee.class);
		c.add(Restrictions.eq("username", username).ignoreCase());
		List<Employee> foundedEmp = c.list();

		if (foundedEmp.size() == 1) {

			Employee employee = foundedEmp.get(0);

			if (employee.getEffectiveTime() != null) {
				Date startValidity = employee.getEffectiveTime().getLow();
				Date endValidity = employee.getEffectiveTime().getHigh();
				Date now=new Date();
				if (startValidity != null && startValidity.after(now)) {
					String message ="User validity not match. start validy: "+startValidity + " is not actually valid.";
					log.error(message);
					writeResponse(resp, message);
					return null;
				}

				if (endValidity != null && endValidity.before(now)) {
					String message ="User validity not match. end validy: "+startValidity+ " is not actually valid";
					log.error(message);
					writeResponse(resp, message);
					return null;
				}
			}

			return employee;

		} else  {
			//Employee not found 
			String message= "Error searching employee in PHI with username: " + username + " found null or multiple result: "+foundedEmp.size()+" employee found.";
			log.error(message);
			writeResponse(resp, message);
			return null;
		}
	}


	private EmployeeRole getEmployeeRole(Employee emp, String roleCode, HttpServletResponse resp) {

		EmployeeRole ret = null;
		String username= emp.getUsername();

		boolean roleFound=false;
		for (EmployeeRole empRole : emp.getEmployeeRole()) {
			if (empRole.getCode().getCode().equals(roleCode)) {
				roleFound=true;
				ret=empRole;
				break;
			}
		}
		if (!roleFound) {
			String message = "Role "+roleCode+ " not found for username "+username; 
			log.error(message);
			writeResponse(resp, message);
			return null;
		}

		Date startValidity = ret.getEffectiveTime().getLow();
		Date endValidity = ret.getEffectiveTime().getHigh();
		Date now=new Date();
		if (startValidity != null && startValidity.after(now)) {
			String message ="Role "+ roleCode+" validity not match. start validy: "+startValidity + " is not actually valid.";
			log.error(message);
			writeResponse(resp, message);
			return null;
		}

		if (endValidity != null && endValidity.before(now)) {
			String message ="Role "+ roleCode+" validity not match. end validy: "+startValidity+ " is not actually valid";
			log.error(message);
			writeResponse(resp, message);
			return null;
		}

		return ret;
	}

}
