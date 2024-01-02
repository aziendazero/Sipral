package com.phi.cs.repository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileFilter;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.Locker;
import com.phi.cs.util.CsConstants;
import com.phi.entities.baseEntity.ProcSecurity;
import com.phi.ps.Node;
import com.phi.ps.PhiExpressionEvaluator;
import com.phi.ps.ProcessManagerImpl;
import com.phi.ps.ProcessSecurity;
import com.sun.faces.application.ApplicationAssociate;
import com.sun.faces.application.ApplicationResourceBundle;


/**
 * Manages access to resources stored in the file system
 */

@BypassInterceptors
@Name("repository")
@Scope(ScopeType.APPLICATION)
@Startup
public class RepositoryManager implements Serializable, RepositoryGetter {

	private static final long serialVersionUID = -6212495240876591237L;
	
	private static final String CATALOG_SERVER_JAR = "CATALOG_SERVER.jar";
	private static final String PHI_CORE_JAR = "phi-core.jar"; //Deployed by maven

	private static final String SEAM_PROPERTIES_FILE = "seam.properties";
	
	private static final String MANIFEST_FILE = "META-INF/MANIFEST.MF";
	
	private static final Logger log = Logger.getLogger(RepositoryManager.class);
	
	private static final String procSecurityHql = "SELECT DISTINCT ps FROM ProcSecurity ps LEFT JOIN FETCH ps.procSecurityRole pr LEFT JOIN FETCH pr.role";

	//Constants used to described the end path string
//	private final static String PHI_PATH = "deploy-apps" + File.separator + "CATALOG_SERVER.ear";
	//private final static String EAR_PATH = "deploy" + File.separator + "CATALOG_SERVER.ear"; //Deployed by maven

	//basicPhiRootVirtualFile point to tmp Folder of jboss
	//earDeployFolder is the ear folder where CATALOG_SERVER.ear is deployed and touched.
	private VirtualFile basicPhiRootVirtualFile = null;
	private File earDeployFolder = null;
	

	private String pdfFolder = null;
	private String solutionName = "";
	private String authorizationFilePath = null;
	private String versionNumber = "";
	//Variable used to define rules repository
//	private String processAuthorizationFrom = "";
//	private String rulesFrom = "";
	private int passwordExpireInDays = 30; //default value is 30 days.
	private Integer userExpireInDays = null; //default value may be 180 days.

	private boolean resuming = false;
	
	private static boolean encryptPatientData = true;
	private static boolean using_MD5_passwords = false;
	private static boolean use_LDAP_auth = false;
	
	private long basicPhiRootLastModified = -1l;
	
	private Node rootProcessNode;
	
	private HashMap<String, ProcessDefinition> processesCache = new HashMap<String, ProcessDefinition>();
	
	
	//hashmap of hashmap. External Hashmap key is custor name. Internal hashmap contains :
	// KEY: processPath.  VALUE: String[] roles enabled.
	//loaded at startup, maintained when EAR is touched (basicPhiRootFile.lastModified() > basicPhiRootLastModified)
	//this hashmap is sometime accessed and edited by ProcSecurityAction.updateProcSecurityCache when the security are changed at runtime by ProcSecurity process.
	//useDbProcSecurity is true, if the security on db is found.
	private boolean useDbProcSecurity = false;
	private HashMap<String,HashMap<String,ProcessSecurity>> procSecurityPerCust = new HashMap<String, HashMap<String, ProcessSecurity>>(); //maintain the mapping between process path and its security role code arrays, one hashmap for each customer.
	private HashMap<String,List<String>> alwaysExecutableProcessesPerCust = new HashMap<String, List<String>>();
	
	private Properties seamProperties = new Properties();
	
	private static String securityAuthenticationLDAP = null;
	private static String initialContextFactoryLDAP = null;
	private static String domainLDAP = null;
	private static String searchBaseLDAP = null;
	private static String hostLDAP = null;
	private static String portLDAP = null;
	
	
	
	/**
	 * Initialize Repository with content of seam.properties
	 * and META-INF/MANIFEST.MF
	 * set Jbpm expression evaluator
	 */
	@Create
	public void init() {

		log.info("JAVA VERSION: "+System.getProperty("java.version")+ " "+System.getProperty("sun.arch.data.model")+ "bit");
		try {
			JbpmExpressionEvaluator.setExpressionEvaluator( new PhiExpressionEvaluator() );
			
			//UNLOCK ALL 
			try {
				Locker.instance().unlockAll();
			} catch (Exception e) {
				log.error("Error unlocking all entities on server restart");
			}
			
			InputStream seamPropertiesIs = null;
			try {
				URL seamPropertiesUrl = findUrl(CATALOG_SERVER_JAR, SEAM_PROPERTIES_FILE);
				if (seamPropertiesUrl == null) {
					//Deployed by maven
					seamPropertiesUrl = RepositoryManager.findUrl(PHI_CORE_JAR, SEAM_PROPERTIES_FILE);
				}
				if (seamPropertiesUrl != null) {
					seamPropertiesIs = seamPropertiesUrl.openStream();
					seamProperties.load(seamPropertiesIs);
		
					String resumingParameter = (String)seamProperties.get(CsConstants.RESUMING_ENABLED);
					if ((resumingParameter != null) && (resumingParameter.equals("true")))
						resuming = true;
					
					String pdfFolder = (String)seamProperties.get(CsConstants.PDF_FOLDER);
					if ((pdfFolder != null) && (pdfFolder.length()>0))
						this.pdfFolder = pdfFolder;
					
					String MD5_password_string = (String)seamProperties.get(CsConstants.MD5_passwords);
					if ((MD5_password_string != null) && (MD5_password_string.equals("true")))
						using_MD5_passwords = true;
					
					String encryptPatientDataString = (String)seamProperties.get(CsConstants.encryptPatientData);
					if ((encryptPatientDataString != null) && (encryptPatientDataString.equals("false")))
						encryptPatientData = false;
					
					String use_LDAP_auth_string = (String)seamProperties.get(CsConstants.use_LDAP_auth);
					if ((use_LDAP_auth_string != null) && (use_LDAP_auth_string.equals("true")))
						use_LDAP_auth = true;
					
					String securityAuthenticationLDAP_string = (String)seamProperties.get(CsConstants.securityAuthenticationLDAP);
					if (securityAuthenticationLDAP_string != null)
						securityAuthenticationLDAP = securityAuthenticationLDAP_string;
					
					String initialContextFactoryLDAP_string = (String)seamProperties.get(CsConstants.initialContextFactoryLDAP);
					if (initialContextFactoryLDAP_string != null)
						initialContextFactoryLDAP = initialContextFactoryLDAP_string;
					
					String domainLDAP_string = (String)seamProperties.get(CsConstants.domainLDAP);
					if (domainLDAP_string != null)
						domainLDAP = domainLDAP_string;

					String searchBaseLDAP_string = (String)seamProperties.get(CsConstants.searchBaseLDAP);
					if (searchBaseLDAP_string != null)
						searchBaseLDAP = searchBaseLDAP_string;
					
					String hostLDAP_string = (String)seamProperties.get(CsConstants.hostLDAP);
					if (hostLDAP_string != null)
						hostLDAP = hostLDAP_string;
					
					String portLDAP_string = (String)seamProperties.get(CsConstants.portLDAP);
					if (portLDAP_string != null)
						portLDAP = portLDAP_string;
					
//					String processAuthorizationFrom_string = (String)seamProperties.get(CsConstants.PROCESS_AUTHORIZATION_FROM);
//					if ((processAuthorizationFrom_string != null) && (processAuthorizationFrom_string.length()>0))
//						processAuthorizationFrom = processAuthorizationFrom_string;
//					
//					String rulesFrom_string = (String)seamProperties.get(CsConstants.RULES_FROM);
//					if ((rulesFrom_string != null) && (rulesFrom_string.length()>0))
//						rulesFrom = rulesFrom_string;

					String passwordExpireInDays_string = (String)seamProperties.get("passwordExpireInDays");
					if ((passwordExpireInDays_string != null) && (passwordExpireInDays_string.length()>0)) {
						try {
							passwordExpireInDays= Integer.parseInt(passwordExpireInDays_string);
						} catch (NumberFormatException e) {
							log.error("Number format exception converting passwordExpireInDays seam property. Value is: " + passwordExpireInDays_string + e);
						}
					}
					
					String userExpireInDays_string = (String)seamProperties.get("userExpireInDays");
					if ((userExpireInDays_string != null) && (userExpireInDays_string.length()>0)) {
						try {
							userExpireInDays= Integer.parseInt(userExpireInDays_string);
						} catch (NumberFormatException e) {
							log.error("Number format exception converting userExpireInDays seam property. Value is: " + userExpireInDays_string + e);
						}
					}

					basicPhiRootVirtualFile = VFS.getRoot(seamPropertiesUrl);
					basicPhiRootVirtualFile = basicPhiRootVirtualFile.getParent().getParent();

					Contexts.getApplicationContext().set(CsConstants.CATALOG_SERVER_EAR_PATH, getEarPath());
					
					earDeployFolder = getEarDeployPath(seamPropertiesUrl);
				}
				//FIXME: try to load processes here, to have all seam properties readed before login
				//rootProcessNode = getRootProcessNode();
				
				getClassLoader();
			} finally {
				if (seamPropertiesIs != null) {
					seamPropertiesIs.close();
				}
			}
			
			InputStream manifestIs = null;
			try {
				VirtualFile vf = VFS.getVirtualFile(basicPhiRootVirtualFile.toURI(), MANIFEST_FILE);				
				if (vf != null) {
					manifestIs = vf.openStream();
					Manifest manifest = new Manifest(manifestIs);
					Attributes attr = manifest.getMainAttributes();
					versionNumber = attr.getValue("Implementation-Version");
				}
			} finally {
				if (manifestIs != null) {
					manifestIs.close();
				}
			}
			
			List<VirtualFile> solutions = basicPhiRootVirtualFile.getChildren(warfilter);
			String customer="CORE";
			for (VirtualFile solution : solutions) {
				Properties seamProp = readBackendSeamProperty(solution);
				if (seamProp != null){
					customer=(String)seamProp.get("CUSTOMER");
					if (customer == null || customer.isEmpty() || customer.contains("{")) {   //above error in token substitution on seam.properties where CUSTOMER=${CUSTOMER}
						customer="CORE";
					}
				
				//taken from dictionary manager information about CUSTOMER and used codeSystem/domain for user's roles.
				//used by ProcSecurityAction.getCurrentRoles
					Context appl = Contexts.getApplicationContext();
					appl.set(CsConstants.CUSTOMER, customer);
					appl.set("dictionary.defaultKey",    (String)seamProp.get("dictionary.defaultKey"));   //e.g. EmployeeFunction
					appl.set("dictionary.defaultName",   (String)seamProp.get("dictionary.defaultName"));  //e.g. PHIDIC
					
					// Put all the seam properties of backend in the RepositoryManager seam properties 
					this.updateSeamProperties(seamProp);
				}
			}
			
			//execute query to update clean-up Users sessions in Employee table, only for this host  .setSessionId
			
			//GenericAdapterLocalInterface ga = GenericAdapter.instance();//(GenericAdapterLocalInterface)ic.lookup("CATALOG_SERVER/GenericAdapter/local");
			//ga.executeUpdateHql("update Employee set sessionId = null, host = null where sessionId is not null and host = '"+InetAddress.getLocalHost().getHostName()+"'", null);
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			ca.executeUpdateHql("update Employee set sessionId = null, host = null where sessionId is not null and host = '"+InetAddress.getLocalHost().getHostName()+"'", null);
			
			
		} catch(IOException e){
			log.error("Error initializing RepositoryManager: unable to read seam.properties or META-INF/MANIFEST.MF", e);
	    } catch (Exception e) {
			log.error("Error initializing RepositoryManager", e);
		}
	}
	
	private ClassLoader cl;
	
	public ClassLoader getClassLoader(){
		if (cl==null) { 
			cl =Thread.currentThread().getContextClassLoader();
		}

		return cl;
	}
	
	/**
	 * Get tree of processes
	 * Reload processes only if CATALOG_SERVER.ear folder lastModified is newer than last read
	 * @return
	 * @throws PhiException 
	 */
	public Node getRootProcessNode() throws PhiException {
		try {
			//File basicPhiRootFile = new File( basicPhiRoot );
			
			log.debug("PhiRootFile: ("+basicPhiRootVirtualFile.getLastModified()+") " +basicPhiRootVirtualFile.getName()+ " -->" + basicPhiRootVirtualFile.getPathName());
			if (earDeployFolder.lastModified() > basicPhiRootLastModified) {
				
				List<ProcSecurity> procSecurityList = null;
				try {
					GenericAdapterLocalInterface ga = GenericAdapter.instance();
					procSecurityList = ga.executeHQL(procSecurityHql, null);
				} catch (Exception e) {
					log.error("Unable to read process security from db", e);
				}
				
				if (procSecurityList != null && !procSecurityList.isEmpty()) {
					useDbProcSecurity=true;
					loadSecurityHashMap(procSecurityList);
				}
				
				rootProcessNode = ProcessManagerImpl.instance().loadRootProcessNode(basicPhiRootVirtualFile, useDbProcSecurity);
				log.debug("New process found - rootProcessNode: "+rootProcessNode.getPath() + " ["+rootProcessNode.getChildren().toString()+"] ");

				ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());
				
				ApplicationAssociate aa = ApplicationAssociate.getCurrentInstance();
				if (aa != null) {
					ApplicationResourceBundle appBundle = aa.getResourceBundles().get("msg");
	
					try {
						Field field =  appBundle.getClass().getDeclaredField("resources");
						field.setAccessible(true);
						Map<Locale, ResourceBundle> resources = (Map)field.get(appBundle);
						resources.clear();
					} catch (Exception e) {
						log.error("Unable to clean label cache!", e);
					}
				}
				
				basicPhiRootLastModified = earDeployFolder.lastModified();
			}
			if (rootProcessNode.getChildren() != null) {
				log.debug("rootProcessNode childs:"+rootProcessNode.getChildren().size());
			} else {
				log.error("rootProcessNode: no childs found!");
			}
			return rootProcessNode;
			
		} catch (Exception e) {
			log.error("Error getting Root Process Node", e);
		}
		return null;
	}

	public HashMap<String, ProcessDefinition> getProcessesCache() {
		return processesCache;
	}
	
	public HashMap<String,HashMap<String, ProcessSecurity>> getProcSecurity() {
		return  procSecurityPerCust;
	}
	
	public HashMap<String,List<String>> getAlwaysExecutableProcessesPerCust() {
		return  alwaysExecutableProcessesPerCust;
	}
	
	public boolean getUseDbProcSecurity() {
		return useDbProcSecurity;
	}

	/**
	 * Find path of a folder/file in a phisolution/folderName, works also in farm.
	 */
	public static URL findUrl(String folderName, String fileName) {
		URL url;
		try {
			Enumeration<URL> urlLst = Thread.currentThread().getContextClassLoader().getResources(fileName);
			while (urlLst.hasMoreElements()) {
				url = urlLst.nextElement();
				if (url.getPath().toLowerCase().contains(folderName.toLowerCase()))
					return url;
			}		
		} catch (Exception e) {
			log.error("Unable to find " + fileName + " in folder: " + folderName, e);
		}
		return null;
	}

	
	public String getEarPath() {
		try {
			return basicPhiRootVirtualFile.toURI().getPath();
		} catch (Exception e) {
			log.error("Error getting ear path", e);
		}
		return basicPhiRootVirtualFile.getPathName();
	}
	
	private File getEarDeployPath (URL seamPropUrl) {
		
		String seamPropPath = seamPropUrl.getPath();
		String[] paths = seamPropPath.split("/");
		paths = (String[])ArrayUtils.remove(paths, paths.length-1);
		paths = (String[])ArrayUtils.remove(paths, paths.length-1);
		
		String path = StringUtils.join(paths, "/");
		File earFolder= new File(path);
		return earFolder;
	}
	
	public VirtualFile getEar() {
		return basicPhiRootVirtualFile;
	}

	/* (non-Javadoc)
	 * @see com.phi.cs.repository.RepositoryGetter#getSeamProperty(java.lang.String)
	 */
	@Override
	public String getSeamProperty(String key) {
		if (key == null || key.equals("")) {
			return null;
		}
		
		if (seamProperties == null) {
			return null;
		}
		
		return (String)seamProperties.get(key);
	}
	
	public void updateSeamProperties(Properties backendSeamProperties) {
		if (seamProperties != null && backendSeamProperties != null) {
			seamProperties.putAll(backendSeamProperties);
			
			String MD5_password_string = (String)backendSeamProperties.get(CsConstants.MD5_passwords);
			if ((MD5_password_string != null) && (MD5_password_string.equals("true")))
				using_MD5_passwords = true;
			else if ((MD5_password_string != null) && (MD5_password_string.equals("false")))
				using_MD5_passwords = false;
		}
	}
	

	public String getSolutionName() { 
		return solutionName;
	}

	public void setSolutionName(String solutionName) {
		this.solutionName = solutionName;
	}

	public String getVersionNumber() { 
		return versionNumber;
	}
	
	public String getAuthorizationFilePath() {
		return authorizationFilePath;
	}

	public void setAuthorizationFilePath(String authorizationFilePath) {
		this.authorizationFilePath = authorizationFilePath;
	}
	
	public boolean isResuming() {
		return resuming;
	}

	public void setResuming(boolean resuming) {
		this.resuming = resuming;
	}

	public String getPdfFolder() {
		return pdfFolder;
	}

	public boolean isUsing_MD5_passwords() {
		return using_MD5_passwords;
	}
	
	public boolean isUsing_EncryptPatientData() {
		return encryptPatientData;
	}

	public void setUsing_MD5_passwords(boolean using_MD5_passwords) {
		RepositoryManager.using_MD5_passwords = using_MD5_passwords;
	}
	
	public boolean getUse_LDAP_auth() {
		return use_LDAP_auth;
	}

	public void setUse_LDAP_auth(boolean use_LDAP_auth) {
		RepositoryManager.use_LDAP_auth = use_LDAP_auth;
	}
	
//	public String getProcessAuthorizationFrom() {
//		return processAuthorizationFrom;
//	}
//
//	public void setProcessAuthorizationFrom(String processAuthorizationFrom) {
//		this.processAuthorizationFrom = processAuthorizationFrom;
//	}
//
//	public String getRulesFrom() {
//		return rulesFrom;
//	}
//
//	public void setRulesFrom(String rulesFrom) {
//		this.rulesFrom = rulesFrom;
//	}
	
    public static RepositoryManager instance() {
        return (RepositoryManager) Component.getInstance(RepositoryManager.class, ScopeType.APPLICATION);
    }

	public int getPasswordExpireInDays() {
		return passwordExpireInDays;
	}
	
	public Integer getUserExpireInDays() {
		return userExpireInDays;
	}

	
	/**
	 * Populate procSecurityPerCust with data from db entities ProcSecurity
	 * procSecurityPerCust is used by process manager and tree bean to show enabled processes
	 * @param procSecurityList
	 */
    private void loadSecurityHashMap(List<ProcSecurity> procSecurityList) {
    	String customer="";
    	HashMap <String, ProcessSecurity> customerSec = new HashMap<String, ProcessSecurity>();
    	List <String> alwaysExecutableProc = new ArrayList<String>();
    	for (ProcSecurity ps : procSecurityList) {
    		String currCust = ps.getCustomer();
    		if (customer.isEmpty())
    			customer = currCust;
    		if (!customer.equals(currCust)) {
    			procSecurityPerCust.put(customer, customerSec);
    			alwaysExecutableProcessesPerCust.put(customer,alwaysExecutableProc);
    			customerSec = new HashMap<String, ProcessSecurity>();
    			alwaysExecutableProc = new ArrayList<String>();
    			customer=currCust;
    		}

    		ProcessSecurity processSecurity = new ProcessSecurity();
    		
    		processSecurity.setProcSecurityRole(ps.getProcSecurityRole());
    		
    		customerSec.put(ps.getPath() , processSecurity);
    		if (ps.getAlwaysExecutable() != null && ps.getAlwaysExecutable())
    			alwaysExecutableProc.add(ps.getPath());
    	}
    	
    	procSecurityPerCust.put(customer, customerSec);
    	alwaysExecutableProcessesPerCust.put(customer,alwaysExecutableProc);
    }
    
    
    //LDAP getter and setter
	public String getSecurityAuthenticationLDAP() {
		return securityAuthenticationLDAP;
	}

	public void setSecurityAuthenticationLDAP(String securityAuthenticationLDAP) {
		RepositoryManager.securityAuthenticationLDAP = securityAuthenticationLDAP;
	}
	
	public String getinItialContextFactoryLDAP() {
		return initialContextFactoryLDAP;
	}

	public void setInitialContextFactoryLDAP(String initialContextFactoryLDAP) {
		RepositoryManager.initialContextFactoryLDAP = initialContextFactoryLDAP;
	}
	
	public String getDomainLDAP() {
		return domainLDAP;
	}

	public void setDomainLDAP(String domainLDAP) {
		RepositoryManager.domainLDAP = domainLDAP;
	}
	
	public String getSearchBaseLDAP() {
		return searchBaseLDAP;
	}

	public void setSearchBaseLDAP(String searchBaseLDAP) {
		RepositoryManager.searchBaseLDAP = searchBaseLDAP;
	}
	
	public String getHostLDAP() {
		return hostLDAP;
	}

	public void setHostLDAP(String hostLDAP) {
		RepositoryManager.hostLDAP = hostLDAP;
	}

	public String getPortLDAP() {
		return portLDAP;
	}

	public void setPortLDAP(String portLDAP) {
		RepositoryManager.portLDAP = portLDAP;
	}


	public String getSeamProperty(String key,String defaultValue) {
		String resultFound=this.getSeamProperty(key);
		if (resultFound==null){
			resultFound=defaultValue;
		}
		return resultFound;
	}
	
	public void clearLabelCache(String... msgType) {
		ResourceBundle.clearCache(Thread.currentThread().getContextClassLoader());

		if (msgType != null) {
			for (String msgs : msgType) {
				ApplicationResourceBundle appBundle = ApplicationAssociate.getCurrentInstance().getResourceBundles().get(msgs);

				try {
					Field field =  appBundle.getClass().getDeclaredField("resources");
					field.setAccessible(true);
					Map<Locale, ResourceBundle> resources = (Map)field.get(appBundle);
					
					File file = new File(getEarPath()+getSeamProperty("solution_name")+".war/WEB-INF/classes/");
			        URL[] urls = { file.toURI().toURL() };
			        ClassLoader loader = new URLClassLoader(urls);
			        for (Locale locale : resources.keySet()) {
			        	try {
			        		resources.put(locale, ResourceBundle.getBundle("bundle."+("msg".equals(msgs) ? "label" : msgs)+".messages", locale, loader));
			        	} catch (MissingResourceException e) {
							log.warn("Unable to reload labels for bundle: "+msgs);
						}
			        }
					field.setAccessible(false);

				} catch (Exception e) {
					log.error("Unable to clean label cache!", e);
				}
			}
		}
	}
	
	private Properties readBackendSeamProperty(VirtualFile solution) {
		
		Properties seamPropertiesBackend = null;

		try {
			
			VirtualFile ear = solution.getParent();
			
			VirtualFile backend = null;
			for (VirtualFile filez : ear.getChildren()) {
				if (filez.getName().endsWith("backend.jar") && !filez.getName().equals("medical-backend.jar")) {
					log.info("Reading backend seam properties for solution "+filez.getName());
					backend = filez;
					break;
				}
			}

			if (backend == null) {
				log.info("No backend found for seam properties");
				return null;
			}
			VirtualFile seamPropertiesFile = backend.getChild("seam.properties");
			if (seamPropertiesFile == null) {
				log.info("No backend found for seam properties");
				return null;
			}

			InputStream seamPropertiesIs= null;
			try {
				seamPropertiesIs= seamPropertiesFile.openStream();	
				seamPropertiesBackend =  new Properties();
				seamPropertiesBackend.load(seamPropertiesIs);
			}
			finally {
				if (seamPropertiesIs != null) {
					seamPropertiesIs.close();
				}
			}	
		} catch (Exception e) {
			log.error("unable to read seam properties from "+solution);
		}
		
		return seamPropertiesBackend;
		
		
	}
	
	private VirtualFileFilter warfilter = new VirtualFileFilter() {
        public boolean accepts(VirtualFile file) {
            try {
				//if the file extension is .xml or directory return true, else false
				if (!file.isLeaf() && file.getName().endsWith(".war")) {
				    return true;
				}
			} catch (IOException e) {
				log.error("Error filtering wars", e);
			}
			return false;
        }
    };
}