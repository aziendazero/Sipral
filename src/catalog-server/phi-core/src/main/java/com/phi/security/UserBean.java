package com.phi.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.security.Identity;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.Application;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.role.Employee;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.parameters.ParameterManager;
import com.phi.security.selectItem.EmployeeRoleSelectItem;


/**
 * Used to store info about the logged in user
 *
 */
@BypassInterceptors
@Name("userBean")
@Scope(ScopeType.SESSION)
public class UserBean implements Serializable {

	private static final long serialVersionUID = -3129811371189865180L;
//	private static final Logger log = Logger.getLogger(UserBean.class);
	
	private Long employeeRoleId;
	private Long employeeId;
	private String username;
	
	private String roleCode;
	private String roleCodeId;
	private String roleName;
	
	private String nameFam;
	private String nameGiv;
	
	private Boolean isCoordinator;
	
	protected boolean passwordExpired = false;
	protected boolean userExpired = false;
	
	private String remoteHost;
	
	private boolean loggedByServlet = false;

	//Multi role user: contains a list of selectItems of enabled roles
	List<SelectItem> enabledRoles = new ArrayList<SelectItem>();
	
	//List of sdloc for selected role
	List<SelectItem> enabledSdlocs = new ArrayList<SelectItem>();

	public HashMap<String, String> flexSessionVar = new HashMap<String, String>();
	
	//Used to associate Service delivery locations (id of table Service_delivery_location)
	public List<Long> sdLocs = new ArrayList<Long>();
	
	//Used as parameter in calls for external module
	private Long externalUorgId;

	//If true after login the owner and role selection is shown 
	protected boolean loginOptions = false;
	
	//the application select by users
	private Application application; 
	
	private static final Logger log = Logger.getLogger(UserBean.class);
	
	
	/**
	 * Initialize default role GUEST, useful to execute processes without login
	 */
    @Create
    public void init() {
    	username = "guest";
    	roleCode = "GUEST";
    }
	
	public String getNameFam() {
		return nameFam;
	}

	public String getNameGiv() {
		return nameGiv;
	}
	
	public String getShortNameGiv() {
		if (nameGiv != null && !nameGiv.isEmpty()) {
			return nameGiv.charAt(0)+".";
		} else {
			return "";
		}
	}

	public String getUsername() {
		return username;
	}
	
	public String username() {
		return username;
	}

	public String getRole() {
		return roleCode;
	}
	
	public String getRoleCodeId() {
		return roleCodeId;
	}
	
	public String getRoleName() {
		return roleName;
	}

	public Boolean getIsCoordinator() {
		return isCoordinator;
	}

	/**
	 * Return enabled roles of currentsystemuser: a list of SelectItems.
	 * Only updated at login.
	 * Used to set role at login.
	 * @return
	 */
	public List<SelectItem> getEnabledRoles() {
		return enabledRoles;
	}
	
	/*package*/ void setEnabledRoles(List<SelectItem> enabledRoles) {
		this.enabledRoles = enabledRoles;
		
		if (enabledRoles.size() > 1) {
			loginOptions = true;
		}
	}
	

	public List<SelectItem> getEnabledSdlocs() {
		return enabledSdlocs;
	}

	/*package*/ void setEnabledSdlocs(List<SelectItem> enabledSdlocs) {
		this.enabledSdlocs = enabledSdlocs; 
		
		sdLocs.clear();
		if (enabledSdlocs == null)
			return;
		for (SelectItem sdl : enabledSdlocs) {
			sdLocs.add((Long)sdl.getValue());
		}
		
		if (enabledSdlocs.size() > 1) {
			loginOptions = true;
		}
	}
	
	/**
	 * Selected role at login
	 * @return
	 */
	public Long getSelectedRole() {
		if (employeeRoleId != null ) {
			return employeeRoleId;
		}
		else {
			return Long.parseLong("-1");
		}
	}

	public void setSelectedRole(Long id) {
		try {
			if (id != null && !id.equals(employeeRoleId)) {
				employeeRoleId = id;

				Identity identity = Identity.instance();
				identity.removeRole(getRole());

				for (SelectItem si : enabledRoles) {
					if (si.getValue().equals(id)) {
						EmployeeRoleSelectItem erSi = (EmployeeRoleSelectItem)si;
						this.roleCode = erSi.getCode();
						this.roleCodeId = erSi.getCodeId();
						this.roleName = erSi.getLabel();
						this.isCoordinator = erSi.getIsCoordinator();
						this.specialization = erSi.getSpecializationCode();
						break;
					}
				}
				
				identity.addRole(this.roleCode);
				
//				//FIXME: use AccessControl query
//				Query q = CatalogPersistenceManagerImpl.instance().createQuery("SELECT new javax.faces.model.SelectItem(enabledSdLoc.internalId, enabledSdLoc.name.giv) " +
//				"FROM EmployeeRole empRole " +
//				"JOIN empRole.enabledServiceDeliveryLocations enabledSdLoc " +
//				"WHERE empRole.internalId = :internalId");
//				
//				q.setParameter("internalId", employeeRoleId);
				
				List<SelectItem> enabledSdloc = BaseAccessControlAction.instance().findEnabledSdlocs(this);
				
				setEnabledSdlocs(enabledSdloc);

			}
		} catch (Exception e) {
			throw new IllegalStateException("Error setting system user!", e);
		}
	}
	
	public void setFlexSessionVar(String key, String value) {
		flexSessionVar.put(key, value);
	}
	
	public String getFlexSessionVar(String key) {
		return flexSessionVar.get(key);
	}
	
	public EmployeeRole getCurrentSystemUser() {
		if (employeeRoleId != null) {
			return CatalogPersistenceManagerImpl.instance().load(EmployeeRole.class, employeeRoleId);
		} else {
			return null;
		}
	}
	
	//currentEmployee
	public Employee getCurrentEmployee() {
		if (employeeId != null) { 
			return CatalogPersistenceManagerImpl.instance().load(Employee.class, employeeId);
		} else {
			return null;
		}
	}

	//used only by BaseAccessControl during login.
	/*package*/ void setCurrentSystemUser(Long employeeId, String nameFam, String nameGiv, String username, Long employeeRoleId, String roleCode, String roleCodeId, String roleName, Boolean isCoordinator) {
		this.employeeRoleId = employeeRoleId;
		this.employeeId = employeeId;
		this.roleCode = roleCode;
		this.roleCodeId = roleCodeId;
		this.roleName = roleName;
		this.username = username;
		this.nameFam = nameFam;
		this.nameGiv = nameGiv;
		this.isCoordinator = isCoordinator;
	}
	
	//Used only by inner integrationServlet and maybe by WSAccessControl for PHI_DOC 
	/*package*/ void setCurrentSystemUser(EmployeeRole currentSystemUser) {
		
		setSelectedRole(currentSystemUser.getInternalId());  //> (*) Call setSelectedRole which set roleCode, roleName, isCoordinator, specialization and enabledSdl
		
		//this.employeeRoleId = currentSystemUser.getInternalId()  // (*);
		//this.roleCode = currentSystemUser.getCode().getCode();   // (*) 
		//this.roleName = currentSystemUser.getCode().getCurrentTranslation(); // (*) 
		//this.isCoordinator = currentSystemUser.getIsCoordinator();//(*) 
		
		Employee emp = currentSystemUser.getEmployee();
		this.employeeId = emp.getInternalId();
		this.username =   emp.getUsername();
		if (emp.getName() != null) {
			this.nameFam = emp.getName().getFam();
			this.nameGiv = emp.getName().getGiv();
		} else {
			log.warn("Logged user with username \""+username+ "\" has null Family and Given name.");
		}

	}
	
	/*package*/ void clearCurrentSystemUser() {
		this.employeeRoleId = null;
		this.employeeId = null;
		this.roleCode = null;
		this.roleCodeId = null;
		this.roleName = null;
		this.username = null;

		sdLocs.clear();
		loginOptions = false;
		this.isCoordinator = null;
	}
	
	@SuppressWarnings("unchecked")
	public List<ServiceDeliveryLocation> getSdLocsByCode(String sdlCode) {
		List<ServiceDeliveryLocation> returnSdl = null;
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance(); 
		
		if (sdLocs != null && !sdLocs.isEmpty()) {
	
			Criteria sdlCrit = ca.createCriteria(ServiceDeliveryLocation.class)
					.add(Restrictions.in("internalId", sdLocs))
					.createCriteria("code")
					.add(Restrictions.eq("code", sdlCode));
			
			returnSdl = sdlCrit.list();
		}
		return returnSdl;
	}
	
	//Multiple sdloc selection: used in options.xhtml in combination with radio checkboxes or tree

	/**
	 * Used by query manager to filter queries
	 * @return a list of id of selected owners
	 */
	public List<Long> getSdLocs() {
		return sdLocs;
	}
	
	public void setSdLocs(List<Long> sdLocs) {
		this.sdLocs = sdLocs;
	}
	
	//Single sdloc selection: used in options.xhtml in combination with radio button
	
	public Long getSdLoc() {
		return sdLocs.get(0);
	}
	
	public void setSdLoc(Long sdLoc) {
		this.sdLocs.set(0, sdLoc);
	}
	
	public void setLoginOptions(boolean loginOptions){
		this.loginOptions = loginOptions;
		
		try {
			// Moved from BaseAccessControlAction to get the right role
			ParameterManager.instance().init();
		} catch (Exception e) {
			log.error("Error reading Application Parameters");
		}
	}
    
	public boolean hasLoginOptions() {
		return loginOptions;
	}


	public boolean isPasswordExpired() {
		return passwordExpired;
	}

	/*package*/ void setPasswordExpired(boolean passwordExpired) {
		this.passwordExpired = passwordExpired;
	}
	
	public boolean isUserExpired() {
		return userExpired;
	}

	/*package*/ void setUserExpired(boolean userExpired) {
		this.userExpired = userExpired;
	}
	
    public static UserBean instance() {
        return (UserBean) Component.getInstance(UserBean.class, ScopeType.SESSION);
    }
    
	
    public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	public Long getExternalUorgId() {
		return externalUorgId;
	}

	public void setExternalUorgId(Long externalUorgId) {
		this.externalUorgId = externalUorgId;
	}
	
	public Long getEmployeeId() {
		return employeeId;
	}
	
	public Long getEmployeeRoleId() {
		return employeeRoleId;
	}
	
	public String getRemoteHost() {
		return remoteHost;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}
	
	
	//mobile is a boolean property added to know if the user session is executed from a mobile device (mobile=true)
	//by default is false. This property is used by pages.xml and landing.seam to know if the user must 
	//be redirected to mobile version of login page.
	private boolean mobile;
    private boolean analizedUserAgent=false;
    
    public boolean isMobile(){
    	//  https://community.jboss.org/thread/191214?start=0&tstart=0
    	if (analizedUserAgent)
    		return mobile;
    	
    	Object reqOb = FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	String userAgent="";
    	if (reqOb instanceof HttpServletRequest) {
			HttpServletRequest req = (HttpServletRequest) reqOb;
			userAgent =req.getHeader("User-Agent");
    	}
    	if (userAgent.contains("Android")|| userAgent.contains("iPhone")|| userAgent.contains("iPad") || userAgent.contains("WCE")) {
    		mobile=true;
    	}else {
    		mobile=false;
    	}
    	analizedUserAgent=true;
    	return mobile;
    } 
	
    private boolean repositoryViewer;

	private String specialization;

	public boolean isRepositoryViewer() {
		return repositoryViewer;
	}

	public void setRepositoryViewer(boolean repositoryViewer) {
		this.repositoryViewer = repositoryViewer;
	}
	
	//removed, because made by sessionCollectionListner.
//	@Remove @Destroy
//	public void logoutLogger(){
//		if (username != null) {
//			try {
//				Locker.instance().unlockAll(username);
//			} catch (NamingException e) {
//				log.error("Unable to perform unlockAll for user: "+ username);
//			}
//			log.info("["+ username +"] Session expired");
//		}
//		else {
//			log.debug("Destroyed UserBean instance by Seam (Session expired).");
//		}
//	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}
	
	/**
	 * Method used to check if the current user role is contained in the input list roleList.
	 * Example: userBean.hasRoleIn('ADMIN','INFERMIERE');
	 * @param roleList
	 * @return
	 */
	public boolean hasRoleIn(String... roleList){
		
		if(roleCode!=null && roleList!=null && roleList.length>0){
			return Arrays.asList(roleList).contains(roleCode);
		
		}else{
			return false;
		}
	}

	public boolean getLoggedByServlet() {
		return loggedByServlet;
	}

	public void setLoggedByServlet(boolean loggedByServlet) {
		this.loggedByServlet = loggedByServlet;
	}
	
	/**
	 * Change current language
	 * @param lang language to set
	 */
	public void setLanguage(String lang) { 
		//JSF
		FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale(lang));
		//Seam
		LocaleSelector.instance().setLanguage(lang);
	}
	
	
}