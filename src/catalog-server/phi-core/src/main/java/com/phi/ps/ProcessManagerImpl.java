package com.phi.ps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.dom4j.xpath.DefaultXPath;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.VirtualFileFilter;
import org.jbpm.JbpmConfiguration;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.ApplicationException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.Locker;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.util.CsConstants;
import com.phi.cs.view.banner.Banner;
import com.phi.cs.view.bean.ButtonBean;
import com.phi.entities.actions.ActionInterface;
import com.phi.entities.actions.ProcSecurityAction;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.security.AccessControlAction;
import com.phi.security.SessionManager;
import com.phi.security.UserBean;


/**
 * Handles jbpm processes and tasks lifecycle and manages pagination with programmatic redirect
 *
 * @author Massimo Frossi
 */

@BypassInterceptors
@Name("processManager")
@Scope(ScopeType.CONVERSATION)
public class ProcessManagerImpl implements ProcessManager, Serializable {

	private static final long serialVersionUID = -9184980054066437844L;

	private static final Logger log = Logger.getLogger(ProcessManagerImpl.class);
	private Map<String, String[]> convMap = new HashMap<String, String[]>(); 
	
	private ProcessInstance process;

	private String currentProcess;
	
	private String currentSubProcess;
	
	/**
	 * If stateless is true BaseAction.create and delete will use Generic adapter
	 * In this way subProcess can persist separately from main process
	 */
	private boolean stateless = false;
	
	/**
	 * Contains stack of sub processes, useful to reset stateless to false when exiting from stateless subProcess
	 */
	private Stack<String> subProcessStack = new Stack<String>();
	
	private boolean readOnly = false;
	
	private HashMap<String, ProcessDefinition> processesCache = null; // key is process path
	private List<String> solutionsName = new ArrayList<String>();
	private HashMap<String,String> moduleOrder = new HashMap<String, String>(); //key is process path
	
 
	//reference to procSecurity Hash of RepositoryManager.
	//Into repository manager an hashmap of hashmap is contained, key is custor name. Here is the hashmap of the current customer.
	//useDbProcSecurity is true, if the security on db is found (see Respository Manager)
//	private boolean useDbProcSecurity = false;
	private HashMap<String, ProcessSecurity> procSecurity = new HashMap<String, ProcessSecurity>();
	private List<String> alwaysExecutableProcesses = new ArrayList<String>();
	
	//Special 'role' strings can be added to security Process String: ALWAYS_EXECUTABLE
	//in this way you can have a process never visibile (or visibile only by user with specific role), 
	//but always executable, for example from dashboard.
	private static final String ALWAYS_EXECUTABLE_PROPERTY_NAME = "ALWAYS_EXECUTABLE";
	private static final String MODULE_ORDER_FILE_NAME = ".modulesOrder";
	
	//public static final String beginConversationEvent = "beginConversation";

	
    public static ProcessManager instance() {
        return (ProcessManager) Component.getInstance(ProcessManagerImpl.class, ScopeType.CONVERSATION);
    }
    
	@Create
	public void create() {

//		JbpmContext threadLocalJbpmContext = JbpmConfiguration.getInstance().getCurrentJbpmContext();
//		// Open only one JbpmContext for each thread
//		if (threadLocalJbpmContext == null) {
//			JbpmConfiguration.getInstance().createJbpmContext();
//		}
		
		//Take from repository manager reference to information regaring process security.
		RepositoryManager repo = RepositoryManager.instance();
		processesCache = repo.getProcessesCache();
//		useDbProcSecurity= repo.getUseDbProcSecurity();
		
	}
	
//	@Destroy
//	public void destroy() {
//		// Since current Jbpm context is stored in a ThreadLocal variable and
//		// one session can switch thread if we close JbpmContex can happen that a session 
//		// closes JbpmContex of another session...
//		//JbpmConfiguration.getInstance().getCurrentJbpmContext().close();
//	}

	/**
	 * Load process definitions deployed if deploy_PHI/processes
	 * Executed by repository init, only once
	 * @throws PhiException 
	 */
	public com.phi.ps.Node loadRootProcessNode(VirtualFile rootProcessFile, boolean useDbProcSecurity) throws PhiException {

		com.phi.ps.Node rootNode = new com.phi.ps.Node();
		
		try {
			if (rootProcessFile.isLeaf()) {
				throw new FileNotFoundException("Unable to find " + rootProcessFile.getName());
			}

			List<VirtualFile> solutions = rootProcessFile.getChildren(warfilter);

			if (solutions.isEmpty()) {
				throw new IllegalStateException("No solution deployed under " + rootProcessFile.getName());
			}
			
			RepositoryManager repo = RepositoryManager.instance();
			Context appl = Contexts.getApplicationContext();
			String customer = (String) appl.get(CsConstants.CUSTOMER);
			
			for (VirtualFile solution : solutions) {
				
				solutionsName.add(solution.getName());
				loadModuleOrder(solution);
				
				//Warning: these operations are made for each solution, but they are not managed for multiple solution together.
				//Actually only one solution is supported at runtime.
				
				procSecurity = repo.getProcSecurity().get(customer);
				alwaysExecutableProcesses = repo.getAlwaysExecutableProcessesPerCust().get(customer);
				
				if (useDbProcSecurity && (procSecurity == null || procSecurity.isEmpty())) {
					log.error("Process security from database is enabled by Repository Manager, but not found security for current customer. Used security from jpdl files. Check ProcSecurity table.");
					useDbProcSecurity=false;
				}
				
				loadSubNodes(rootNode, solution, solution, customer, useDbProcSecurity);
			}

		} catch (Exception e) {
			log.error("Unable to read processes",e);
		}	
		
		return rootNode;
	}
	
	private void loadSubNodes(com.phi.ps.Node folderNode, VirtualFile folderFile, VirtualFile rootProcessFile, String customer, boolean useDbProcSecurity) throws IOException, URISyntaxException{
		
	    List<VirtualFile> processesAndSubfolders = folderFile.getChildren(processesfilter);
	    
	    String relativePath = null;

	    processesAndSubfolders = putCoreAsLast(processesAndSubfolders);
	    
	    for (VirtualFile processeOrSubfolder : processesAndSubfolders) {
	    	
	    	relativePath = rootProcessFile.toURI().relativize(processeOrSubfolder.toURI()).getPath();
	    	
	    	if (relativePath.endsWith("/")) {
	    		relativePath = relativePath.substring(0, relativePath.length()-1);
	    	}
	    	
	    	try {
	    		
				if (!processeOrSubfolder.isLeaf()) {
					
					String folderName = processeOrSubfolder.getName();
					if (folderName.equals("CORE") || folderName.equals("PROCESSES") || folderName.startsWith("customer_")) { //SKIP CORE, PROCESS or CLIENT folder NODE CREATION
						if (customer.isEmpty() && folderName.startsWith("customer_")) {
							//skip any customer process if customer is CORE.
							log.debug("Skipped folder "+folderName+" because current customer is CORE. "+processeOrSubfolder.getPathName());
							continue;
						}
						if (!customer.isEmpty() && folderName.startsWith("customer_") && ! folderName.equals("customer_"+customer)) {
							//Skip folder of other customer.
							log.debug("Current customer: " +customer+ ". Skipped folder of other customer: "+processeOrSubfolder.getPathName());
							continue;
						}
						loadSubNodes(folderNode, processeOrSubfolder, rootProcessFile, customer, useDbProcSecurity);
					} else {
					
						com.phi.ps.Node subFolderNode = new com.phi.ps.Node();
						subFolderNode.setPath(relativePath);
						folderNode.getChildren().add(subFolderNode);
						subFolderNode.setParent(folderNode);
						
						String moduleOrderValue = moduleOrder.get(subFolderNode.getPath());
						if (moduleOrderValue != null) {
							subFolderNode.setSortOrder(Integer.parseInt(moduleOrderValue));
						}
						
						loadSubNodes(subFolderNode, processeOrSubfolder, rootProcessFile, customer, useDbProcSecurity);
					
					}
					
				} else {
					
					if (relativePath.endsWith(".jpdl.xml")) {
						relativePath = relativePath.replace(".jpdl.xml", "");
					}
					
					if (alreadyContainsProcess (folderNode, processeOrSubfolder.getName().replace(".jpdl.xml", ""), customer )) {
						log.info("Skipped process of CORE folder: "+relativePath+ " because already existing specialized process in customer folder.");
						continue;
					}
					
					com.phi.ps.Node process = loadProcess(processeOrSubfolder, relativePath, useDbProcSecurity);
					if (process == null)
						continue; 
					process.setParent(folderNode); 
					VirtualFile iconFile = processeOrSubfolder.getParent().getChild(processeOrSubfolder.getName().replace(".jpdl.xml", ".icon.jpg"));

					if (iconFile != null && iconFile.exists()) {
						process.setImagePath(rootProcessFile.toURI().relativize(iconFile.toURI()).getPath());
					}
					
					folderNode.getChildren().add(process);

				}
				
			} catch (Exception e) {
				log.error("Unable to read processe" + processeOrSubfolder.getPathName(),e);
			}
	    	
	    }
	}
	
	
	private boolean alreadyContainsProcess (Node folder, String processName, String customer) {
		
		String folderPath = folder.getPath();
		String [] pathElements = folderPath.split("/");
		if (folderPath.contains("CORE") && pathElements.length >3) {
			//the path of current process is under a folder, under a module.
			//search process with same name, in other part of the module  //, in a folder with same name.
			//String folderName = pathElements[pathElements.length-1];
			
			String customerProcessName = folderPath.replace("CORE","customer_"+customer)+"/"+processName;
//			for (String k : processesCache.keySet()){
//				log.info("-->  "+k);
//			}
			if (processesCache.containsKey(customerProcessName)){
				return true;
			}
			return false;
			
			//get parent until the path is simply MOD_
//			Node moduleNode = folder.getParent();
//			while (moduleNode != null && moduleNode.getPath().contains("/")){
//				moduleNode = moduleNode.getParent();
//			}
//			
//			boolean found = findLeafNodes(moduleNode, processName);
//			return found;
			
			//going deep in the node structure until found a folder with same name, in customer folder.
			
			//Recursively find 'granparent' node, which represents Module node.
			//when found look again recursively to search a node with same path and same name.
			
			
		}
		else {
			if (folder == null || folder.getChildren().isEmpty())
				return false;
			
			for (Node n : folder.getChildren()) {
				String path = n.getPath();
				path = path.substring(path.lastIndexOf("/")+1,path.length());
				if (processName.equals(path)) {
					return true;
				}
			}
			return false;
		}
	}
	

	
	
	private boolean findLeafNodes(Node node, String name) {
		boolean ret = false;
	    if (node != null) {
	        if (node.isLeaf()) {
	        	String p =node.getPath();
	        	String n = p.substring(p.lastIndexOf("/")+1,p.length());
	        	if (name.equals(n)) {
	        		return true;
	        	}
	        }	
	        List<Node> children = node.getChildren();
	        if (children != null) {
	            for (Node child: children) {
	            	if (findLeafNodes(child, name))
	            		return true;
	            }
	        }
	    }
	    return false;
	}
	
	
	private com.phi.ps.Node loadProcess(VirtualFile processDefinitionFile, String relativePath, boolean useDbProcSecurity) throws FileNotFoundException, PersistenceException {

		com.phi.ps.Node processNode = new com.phi.ps.Node();
			
		processNode.setLeaf(true);
		
		List<String> parameters = null;
		
		String processDefinitionFileLastModified = "";
		try {
			processDefinitionFileLastModified = String.valueOf(processDefinitionFile.getLastModified());
		} catch (IOException e1) {
			log.error("Error getting lastModified of " + processDefinitionFile.getName(), e1);
		}

		ProcessDefinition processDefinition = processesCache.get(relativePath);//tb.getProcessDefinition(relativePath);

		if (processDefinition == null || !processDefinitionFileLastModified.equals(processDefinition.getDescription())) {

			try {
                processDefinition = ProcessDefinition.parseXmlInputStream(processDefinitionFile.openStream());
			}
			catch (Exception e) {
				log.error("Error reading process definition: "+processDefinitionFile);
			}
			
			//Set the last modified time of the processdefinition.xml into description.
			processDefinition.setDescription(processDefinitionFileLastModified);
			
			processDefinition.setName(relativePath);
			
			processesCache.put(relativePath, processDefinition);//tb.addProcessDefinition(relativePath, processDefinition);
			
			
			//Used parameters:
//			
//			parameters = new ArrayList<String>();
//			
//			Event startStateNodeEnter = processDefinition.getStartState().getEvent("node-enter");
//			if (startStateNodeEnter != null) {
//				List actions = startStateNodeEnter.getActions();
//				if (actions != null) {
//					for (Object action : actions) {
//						if (action instanceof Script) {
//							Script script = (Script) action;
//							if ("used-parameter".equals(script.getName())) {
//								Set<VariableAccess> variables = script.getVariableAccesses();
//								for (VariableAccess var : variables) {
//									parameters.add(var.getVariableName());
//								}
//								
//								//ProcessManagerImpl is updating an existent process from fileSystemem
//								//update database with new parameters information.
//								ProcSecurityAction psa = ProcSecurityAction.instance();
//								try {
//									psa.updatePs(relativePath, parameters);
//								} catch (PhiException e) {
//									log.error("Unable to update ProcSecurity db, the process "+relativePath+" with parameters "+parameters, e);
//								}
//								
//								break;
//							}
//						}
//					}
//				}
//			}
		}
		
		processNode.setProcessDefinition(processDefinition);
		processNode.setPath(relativePath);
		
		List<ProcessSecurityRole> roles = null;
		
		if (useDbProcSecurity && procSecurity.containsKey(relativePath)) {
			ProcessSecurity processSecurity = procSecurity.get(relativePath);
			roles = processSecurity.getEnabledRoles();
			if (alwaysExecutableProcesses.contains(relativePath)) {
				processNode.setAlways_executable(true);
			}


	
		} 
		else {
			Action security = processDefinition.getAction("security");
			String securityExpression = "";
			
			if (security != null && security.getActionExpression() != null){
				securityExpression = security.getActionExpression();
			}
				 
				
//				if (securityExpression!=null){
					
					//parse new security string in one of the following format:  
					//Cust1:role1;role2|Cust2:role9;role1;role7  
					//role1;role5|CUST2:role3;role5
					//role4;role6
					
					String customer =(String)Contexts.getApplicationContext().get("CUSTOMER");
					String customerExpression ="";
					if (securityExpression.isEmpty())
						customerExpression="";
					
					if (customer != null && !customer.isEmpty()) {
						//take customer roles.
						if (securityExpression.contains(customer+":")) {
							customerExpression=securityExpression.substring(securityExpression.indexOf(customer+":"),securityExpression.length());
							customerExpression=customerExpression.substring(customerExpression.indexOf(":")+1,customerExpression.length());
							if (customerExpression.contains("|"))
								customerExpression=customerExpression.substring(0,customerExpression.indexOf("|"));
						}
						else {
							customerExpression="";
						}
					} 
					
					//if customer role are not found, instead core role are found, use them.
					if (customerExpression.isEmpty()) {
					
						//take core roles
						if (!securityExpression.contains("|")&& !securityExpression.contains(":")) {
							customerExpression=securityExpression;
						}
						else {
							if(securityExpression.contains("|")){
								customerExpression=securityExpression.substring(0,securityExpression.indexOf("|"));
							}
							if (customerExpression.contains(":")) {
								customerExpression="";
							}
						}
					}
					
					String[] rolez = customerExpression.split(";");
					roles = new ArrayList<ProcessSecurityRole>();
					for (String role : rolez) {
						if (!role.isEmpty()) {
							ProcessSecurityRole psr = new ProcessSecurityRole();
							psr.setRole(role);
							roles.add(psr);
						}
					}
					
					if (useDbProcSecurity && !procSecurity.containsKey(relativePath)) {
						//this is a new process, not present in procSecurity cache. 
						//update the ProcessManagerImpl cache, and so the application cache contained into RepositoryManager. 
						ProcessSecurity processSecurity = new ProcessSecurity();
						processSecurity.setEnabledRoles(roles);
						procSecurity.put(relativePath, processSecurity);
						
						//ProcessManagerImpl is reading a new process from fileSystemem, and its security is missing in the SecurityDB.
						//update database with new process security information.
						ProcSecurityAction psa = ProcSecurityAction.instance();
						try {
							psa.insertPs(relativePath, securityExpression, roles, customer, parameters);
						} catch (PhiException e) {
							log.error("Unable to insert into ProcSecurity db for customer "+customer+" the process "+relativePath+" with roles "+roles);
						}
						
					}
					
					if (roles != null && roles.size() > 0) {
						//FIXME: ALWAYS_EXECUTABLE role is not set if security is taken from proc security.s
						//introduced 'special role' for Dashbord or integration.
						//if process contains it, the role is removed from list of roles, but the process
						//node take the property always_executable = true. 
						Iterator<ProcessSecurityRole> rolesIter = roles.iterator();
						while (rolesIter.hasNext()) {
							ProcessSecurityRole psr = rolesIter.next();
							if (ALWAYS_EXECUTABLE_PROPERTY_NAME.equals(psr.getRole())) {
								processNode.setAlways_executable(true);
								rolesIter.remove();
							}
						}
					}
//				}
//			}
		}
		
		
		
		processNode.setRoles(roles);
		
		
		
		if (processDefinition.getStartState() == null ) {
			log.error("process "+processDefinitionFile + " is invalid, because start state is missing. Process skipped.");
			return null;
		}
		
		//Sort order:
		String startNodeDesc = processDefinition.getStartState().getDescription();
		
		if (startNodeDesc != null) {
			try {
				processNode.setSortOrder(Integer.parseInt(startNodeDesc));
			} catch (NumberFormatException e) {
				log.warn("Error parsing start state description: " + e);
			}
		}
		log.debug("Loaded processNode: "+processNode.getProcessPath()+ " - Definition: "+processNode.getProcessDefinition().getName() + " - ROLES: "+processNode.getRoles());
		return processNode;
	}
	
	private void loadModuleOrder(VirtualFile soltionFolder) {
		String moduleOrderFilePath = soltionFolder.getPathName()+  File.separator + MODULE_ORDER_FILE_NAME;
		
		if ((new File (moduleOrderFilePath)).exists()) {
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(moduleOrderFilePath));
			
			    String line;
				while ((line = bufferedReader.readLine()) != null) {
					String[] pathAndOrd= line.split(":");
					if (pathAndOrd != null && pathAndOrd.length==2)
						moduleOrder.put(pathAndOrd[0], pathAndOrd[1]);
				}
			
			} catch (FileNotFoundException e) {
				log.error("Error reading Module order file"+moduleOrderFilePath+ ": FileNotFoundException, reading skipped.");
			} catch (IOException e) {
				log.error("Error reading Module order file"+moduleOrderFilePath+ ": IOException, reading skipped.");
			}
		}
	}
	
	private List<VirtualFile> putCoreAsLast(List<VirtualFile> processOrSubFolder) {
		//search if any elelemtn in the array is a CORE folder. 
		//if any, put it as last element of File array.
		int coreFolerIndex = -1;
		for (int i=0; i<processOrSubFolder.size(); i++) {
			String path = processOrSubFolder.get(i).getPathName();
			if (path.endsWith("CORE") || path.endsWith("CORE"+File.separator)) {
				coreFolerIndex=i;
				break;
			}
		}
		
		//if found, exchange array places between core folder and last element. 
		if (coreFolerIndex != -1 && coreFolerIndex != processOrSubFolder.size()-1) {
			VirtualFile last = processOrSubFolder.get(processOrSubFolder.size()-1);
			processOrSubFolder.set(processOrSubFolder.size()-1, processOrSubFolder.get(coreFolerIndex));
			processOrSubFolder.set(coreFolerIndex, last);
		}
		return processOrSubFolder;
	}
	
	private Properties readBackendSeamProperty(VirtualFile solution) {
		
		Properties seamPropertiesBackend = null;

		try {
			
			VirtualFile ear = solution.getParent();
			
			VirtualFile backend = null;
			for (VirtualFile filez : ear.getChildren()) {
				if (filez.getName().endsWith("backend.jar") && !filez.getName().equals("medical-backend.jar")) {
					backend = filez;
					break;
				}
			}

			if (backend == null) {
				return null;
			}
			VirtualFile seamPropertiesFile = backend.getChild("seam.properties");
			if (seamPropertiesFile == null) {
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
	
	public void startProcess() throws PhiException {
		//getPmClassLoader();
		
		if ((currentProcess!=null)&&(!currentProcess.equals(""))) {
			startProcessFromName(currentProcess);
		}
	}
	
	
//	private ClassLoader cl;
//	
//	public ClassLoader getPmClassLoader(){
//		if (cl==null) { 
//			cl =Thread.currentThread().getContextClassLoader();
//		}
//
//		return cl;
//	}
	
	
	public void startProcessFromName(String aProcessDefinitionName) throws PhiException {

		com.phi.ps.Node processInformationBean = TreeBean.instance().getNodesCache().get(aProcessDefinitionName);
		if (processInformationBean == null) {
			log.error("Error Loading process "+aProcessDefinitionName+ " unable to find in nodes cache or error in TreeBean class instance.");
			return;
		}

		startProcess(processInformationBean);
	}


	public void startProcess(com.phi.ps.Node processInformationBean) throws PhiException { 

		Conversation conversation=Conversation.instance();
		TreeBean treeBean = TreeBean.instance();
		treeBean.setProcessPath(null);//as viewManager.goHome()
		try {
			if (processInformationBean.getProcessDefinition() != null) {

				JbpmConfiguration.getInstance().createJbpmContext();
				
				beginConversation(processInformationBean.getProcessDefinition().getName());

				if (log.isInfoEnabled()){
					log.info("[cid="+conversation.getId()+"] Starting process: " + processInformationBean.getProcessDefinition().getName());
				}
				process = new ProcessInstance(processInformationBean.getProcessDefinition());
				process.signal();

			} else {
				throw new ApplicationException( ErrorConstants.PROCESS_EXECUTION_ERR_INTERNAL_MSG, ErrorConstants.PROCESS_EXECUTION_ERR_CODE);
			}
		} catch (Exception e) {
			if (e.getCause() instanceof javax.mail.MessagingException) {
				log.error("Error sending mail message: "+e.getCause().getMessage());
				//Store in conversation current exception information
				Contexts.getConversationContext().set("MessagingException", e);
				
				//in case of error on Mail node (message delivery), process must be continue.
				//Problem of mail server must not affect phi process handling.
				//go on with the process.
				try {
					process.signal();
				}
				catch (Exception e1) {
					//failure on the next node after mail node, are managed in the same way above.
					throw new PhiException("Error starting process " + processInformationBean.getProcessDefinition().getName(), e, ErrorConstants.PROCESS_STARTING_ERR_CODE );	
				}
			}
			else {
				throw new PhiException("[cid="+conversation.getId()+"] Error starting process " + processInformationBean.getProcessDefinition().getName() +  ". Cause: " + e.getMessage(), e, ErrorConstants.PROCESS_STARTING_ERR_CODE );
			}
		}
		finally {
			JbpmConfiguration.getInstance().close();
			
			currentProcess = processInformationBean.getProcessDefinition().getName();
			
			readOnly = processInformationBean.getReadOnly();
			
			if(treeBean.getProcessPath()==null){
				treeBean.setProcessPath(new LinkedList<String>());
				treeBean.getProcessPath().addAll(processInformationBean.getProcessPath());
			}else{
				//do nothing...usually when first node is subprocess
			}
			
		}

	}
	
	public String manageTask() throws PhiException {
		return manageTask(null, false);
	}
	
	public String manageTask(String outcome) throws PhiException {
		return manageTask(outcome, false);
	}
	
	/**
	 * Manages jbpm tasks, must be called to execute a single task in a jbpm process, including
	 * actions and scripts defined in file processdefinition.xml
	 * @param outcome - button.name;button.mnemonic
	 * @param restore - not implemented
	 */
	public String manageTask(String outcome, boolean restore) throws PhiException {

		try {
			
			JbpmConfiguration.getInstance().createJbpmContext();
			
			if (outcome != null) {
				ButtonBean button = ButtonBean.instance();
				if (outcome.indexOf(";")>0){
					String[] btnVals = outcome.split(";");
					button.setValue(btnVals[0]);
					button.setMnemonic(btnVals[1]);
				} else {
					button.setValue(outcome);
				}
			}
	
			if (process != null) {
				process.signal();
			} 
		} catch (Exception e) {
			
			if (e.getCause() instanceof javax.mail.MessagingException) {
				log.error("Error sending mail message: "+e.getCause().getMessage());
				//Store in conversation current exception information
				Contexts.getConversationContext().set("MessagingException", e.getCause());
				
				//in case of error on Mail node (message delivery), process must be continue.
				//Problem of mail server must not affect phi process handling.
				//go on with the process.
				try {
					process.signal();
				}
				catch (Exception e1) {
					//failre on the next node after mail node, are managed in the same way above.
					throw new PhiException("Error Managing task " + outcome, e1, ErrorConstants.PROCESS_EXECUTION_ERR_CODE );
				}
			}
			else {
				Conversation conversation=Conversation.instance();
				throw new PhiException("[cid="+conversation.getId()+"] Error Main Process: " + getCurrentProcess() + " , Current Process: " + getProcess().getProcessDefinition().getName() +  ". Error Managing task: " + outcome + ". Cause: " + e.getMessage(), e, ErrorConstants.PROCESS_EXECUTION_ERR_CODE );
			}
			
		}
		finally{
			JbpmConfiguration.getInstance().close();
		}
		
		return outcome;
	}

	/**
	 * @see ProcessManager#endProcessExecution()
	 */
	public void endProcessExecution() throws PhiException {

		try {

			if (process != null) {
				Token superProcessToken = process.getSuperProcessToken();
				UserBean userBean = (UserBean)Component.getInstance("userBean");
				Conversation conversation=Conversation.instance();
				
				
				if (superProcessToken == null) {
					flushSession();
					
					log.info("[cid="+conversation.getId()+"] End main process: "+process.getProcessDefinition().getName());
					
					endConversation();
					beginConversation("HOME");
					
					Banner banner = Banner.instance();
					banner.refresh();
					
					ViewManager apc = ViewManager.instance();
					apc.setHome(); 
					apc.cleanPopup();
					
					currentSubProcess = "";
				
					//unlock all only when main process is terminated.
					Locker.instance().unlockAll(userBean.getUsername());
					
					//check if user must be reconnected for operative reason.
					AccessControlAction.instance().checkExecuteRelogin();
					
					TreeBean treeBean = TreeBean.instance();
					treeBean.setProcessPath(null);//as viewManager.goHome()
					
				} else {
					ProcessInstance superProcess = superProcessToken.getProcessInstance();
					if ( superProcess != null && superProcess.getProcessDefinition() != null){
						String superPr = superProcess.getProcessDefinition().getName();
						log.info("[cid="+conversation.getId()+"] Ending sub process: "+process.getProcessDefinition().getName() + "; back to process: "+superPr);
						currentSubProcess = superPr;
						TreeBean treeBean = TreeBean.instance();
						treeBean.getProcessPath().remove(treeBean.getProcessPath().size()-1);
					}
				}

				
			}

		} catch (Exception e) {
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error ending process " + process.getProcessDefinition().getName(), e, ErrorConstants.PROCESS_EXECUTION_ERR_CODE );
		}
	}

	public void saveSuperState() throws PhiException {
		if (!stateless) {
			try {
				flushSession();
			} catch (Exception e) {
				throw new PhiException("Error saving super state! ", e, ErrorConstants.PROCESS_DEFINITION_SUPER_STATE_ERR_CODE );
			}
		}
	}
	
	/**
	 * method for flushing in the db all rim object in the conversation context
	 *  belonging to rim-object db. If catalog adapter is null no operation is 
	 *  done.
	 *  @throws PersistenceException if rim object cannot be flushed on db
	 * @throws ApplicationException 
	 */
	private boolean flushSession() throws PersistenceException {
		/*Useful to flush all the Data in the EntityManger in the DB just 
		 *before the super state ends
		 */
		CatalogAdapter catalogAdapter = CatalogPersistenceManagerImpl.instance();
		if (catalogAdapter!=null) {
			return catalogAdapter.flushSession();
		}
		return false;
	}


	/**
	 * Method for starting conversation
	 */
	public void beginConversation(String processDefinitionName){
		/*
		 * Every time this method is called, conversation attribute has been 
		 * already instanced. So there is no need to call Conversation.instance().
		 */
		Conversation conversation=Conversation.instance();
		if (!conversation.isLongRunning()){  
			conversation.begin();
			conversation.changeFlushMode(FlushModeType.MANUAL);
			conversation.killAllOthers();
			SessionManager.instance().updateConversation(UserBean.instance().getUsername(), Integer.parseInt(conversation.getId()), Contexts.getConversationContext());
		}
		conversation.setDescription(processDefinitionName); 
		
	}
	
	private void endConversation(){
		Context conversationContext = Contexts.getConversationContext();
		String[] convNames = conversationContext.getNames();
		if(convNames!=null){
			for(String name : convNames){
				Object obj = conversationContext.get(name);
				if(!"ProcessManagerImpl".equals(name) && obj.getClass().getName().startsWith("com.phi")){
					conversationContext.remove(name);
				}
			}
		}
		
		CatalogAdapter catalogAdapter = CatalogPersistenceManagerImpl.instance();
		if (catalogAdapter!=null) {
			catalogAdapter.remove();
		}
		Conversation conversation=Conversation.instance();
		conversation.root();
		conversation.leave();
		

	}
	

	public HashMap<String, ProcessDefinition> getProcessesCache() {
		return processesCache;
	}

	public HashMap<String, ProcessSecurity> getProcSecurity() {
		return procSecurity;
	}
	
	public ProcessInstance getProcess() {
		return process;
	}

	public void setProcess(ProcessInstance process) {
		this.process = process;
	}

	public String getCurrentProcess() {
		return currentProcess;
	}

	public void setCurrentProcess(String currentProcess) {
		this.currentProcess = currentProcess;
	}


	public String getCurrentSubProcess() {
		return currentSubProcess;
	}

	public void setCurrentSubProcess(String currentSubProcess) {
		this.currentSubProcess = currentSubProcess;
	}

	
	public boolean isStateless() {
		return stateless;
	}

	public void setStateless(boolean stateless) {
		this.stateless = stateless;
	}

	public Stack<String> getSubProcessStack() {
		return subProcessStack;
	}
	
	public boolean getReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	private VirtualFileFilter processesfilter = new VirtualFileFilter() {
        public boolean accepts(VirtualFile file) {
            try {
				//if the file extension is .xml or directory return true, else false
				if ((!file.isLeaf() && file.getPathName().contains("MOD_") && !file.getName().equals("FORMS")) || file.getName().endsWith(".jpdl.xml") || file.getName().equals("seam.properties"))  {
				    return true;
				}
			} catch (IOException e) {
				log.error("Error filtering processes", e);
			}
			return false;
        }
    };
    
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


    /**
     * Returns html rapresentation of current taskNode of process and super process 
     */
    public String getScreenShot() { 
    	
    	String result = "";
    	
    	if (process != null && process.getProcessDefinition() != null) {
			String processDefinitionName = process.getProcessDefinition().getName();
			
			TaskMgmtInstance taskMgmtInstance = (TaskMgmtInstance)process.getInstances().get("org.jbpm.taskmgmt.exe.TaskMgmtInstance");
			String currentNodeName = "unknown";
			
			if(taskMgmtInstance != null 
					&& taskMgmtInstance.getTaskInstances() != null 
					&& !taskMgmtInstance.getTaskInstances().isEmpty()) {
				
				for(TaskInstance task : taskMgmtInstance.getTaskInstances()){
					if(task.isSignalling()){
						currentNodeName = task.getName();
					}
				}
			}
			
    		result = getScreenShot(processDefinitionName, currentNodeName);
    		
    		Token supeProcessToken = process.getSuperProcessToken();

    		while (supeProcessToken != null) {
    			processDefinitionName = supeProcessToken.getProcessInstance().getProcessDefinition().getName();
    			currentNodeName = supeProcessToken.getNode().getName();
    			
    			result = getScreenShot(processDefinitionName, currentNodeName) + result;
    			
    			supeProcessToken = supeProcessToken.getProcessInstance().getSuperProcessToken();
    		}
    	}
    	
    	return result;
    }
    
    
    /**
     * Returns html rapresentation of currentNodeName of processDefinitionName
     */
    private String getScreenShot(String processDefinitionName, String currentNodeName){
    	StringBuffer sbString = new StringBuffer ();
    	try {

    		if (process != null) {
    			
    			sbString.append ("<span>Process: " + processDefinitionName + "</span><br/>"); 
    			sbString.append ("<span>Node: " + currentNodeName + "</span><br/>"); 

    			String absPath = RepositoryManager.instance().getEarPath() + Contexts.getApplicationContext().get(CsConstants.SOLUTION_NAME)+ ".war/";

    			String processImagePath = "../" + processDefinitionName + ".jpg";

    			String gpdPath = absPath + processDefinitionName + ".gpd.xml";

    			int lastIndexSlash = gpdPath.lastIndexOf("/");
    			gpdPath = gpdPath.substring(0, lastIndexSlash+1) + "." + gpdPath.substring(lastIndexSlash+1);

    			File gpdFile = new File(gpdPath);
    			
    			if (gpdFile.exists()) {
	    			//initialize dom4j
	    			Element rootDiagramElement = new SAXReader().read(gpdFile).getRootElement();

	    			Integer[] minXY = getGlobalMinXY(rootDiagramElement);
	    			int minX = minXY[0];
	    			int minY = minXY[1];

	    	    	XPath xPath = new DefaultXPath ("//node [@name = '"+ currentNodeName + "']");
	    	    	Element node = (Element)xPath.selectSingleNode (rootDiagramElement);
	    	    	
	    	    	int xSuperState = 0;
	    	    	int ySuperState = 0;
	    	    	if ("node-container".equals(node.getParent().getName())) {
	    	    		//If parent is super state coordinate are relative to superstate
	    	    		xSuperState = Integer.valueOf(node.getParent().attribute("x").getValue()).intValue() + 8;
	    	    		ySuperState = Integer.valueOf(node.getParent().attribute("y").getValue()).intValue() + 8;
	    	    	}
	    	    	
	    	    	int[] boxConstraint = new int [4];
	    	    	boxConstraint[0] = Integer.valueOf(node.attribute("x").getValue()).intValue() + xSuperState;//- 4;
	    	    	boxConstraint[1] = Integer.valueOf(node.attribute("y").getValue()).intValue () + ySuperState;//- 4;
	    	    	boxConstraint[2] = Integer.valueOf(node.attribute("width").getValue()).intValue () - 4;//+ 4;
	    	    	boxConstraint[3] = Integer.valueOf(node.attribute("height").getValue()).intValue () - 4;//+ 4;
	
	//    			//extractImageDimension
	    			int[] imageDimension = new int[2];
	    			imageDimension[0]= Integer.valueOf (rootDiagramElement.attribute ("width").getValue()).intValue();
	    			imageDimension[1]= Integer.valueOf (rootDiagramElement.attribute ("height").getValue()).intValue();
	
	    			if (minX < 0) {
	    				boxConstraint [0] -= minX;
	    			}
	    			if (minY < 0) {
	    				boxConstraint [1] -= minY;
	    			}
	
	    			//drawing code
	    			String imageLink = processImagePath;

	    			sbString.append ("<div style =\"position:relative;text-align:left;\">");
	    			sbString.append("<img src=\""+ imageLink +"\" alt=\"Process\" width =\""+ imageDimension [0]+ "\" height =\""+ imageDimension [1]+ "\" >");
	    			sbString.append ("<div style =\"position:absolute;");
	    			sbString.append ("left:"+ boxConstraint [0]+ "px;top:"+ boxConstraint [1]+ "px;width:"+ boxConstraint [2]+ "px;height:"+ boxConstraint [3]+ "px;");
	    			sbString.append ("z-index:1;border-color:red;border-width:4;");
	    			sbString.append ("border-style:groove;background-color:transparent;\">");
	    			sbString.append ("</div>");
	    			sbString.append ("</div>");
    			
    			} else {
    				sbString.append ("<span>Preview unavailable, *.gpd.xml files not deployed</span>"); 
    			}

    		}

    	}catch (Exception e){
    		log.error("Error getting screenShot of process: " + process.getProcessDefinition().getName());
    	}
    	return sbString.toString ();
    }

    public void saveConversationState(){
		String subProcess = getCurrentSubProcess();
		convMap.put(subProcess,Contexts.getConversationContext().getNames());
	}
	
	public void restoreConversationState(){
		String subProcess = getCurrentSubProcess();
		Context conversation = Contexts.getConversationContext();
		String[] currentEntries = conversation.getNames();
		if(currentEntries!=null && currentEntries.length>0 && convMap.get(subProcess)!=null){
			List<String> allowedEntries = Arrays.asList(convMap.get(subProcess));
			for(String entry : currentEntries){
				if(allowedEntries.contains(entry))
					continue;
				
				if(entry.endsWith("List")){
					conversation.remove(entry);
				
				}else {
					Object convEntry = conversation.get(entry);
					if(convEntry instanceof BaseEntity){
						ActionInterface<?> action = (ActionInterface<?>)Component.getInstance(entry+"Action");
						if(action == null){
							conversation.remove(entry);
						
						}else{
							action.eject();
						}
					}
				}
			}
		}

		convMap.remove(subProcess);
	}

	private Integer[] getNodeMinXY(Element rootDiagramElement){
		int minX=0;
		int minY=0;
		XPath xPathx = new DefaultXPath ("/root-container/*[not(preceding-sibling::*/@x <= @x) and not(following-sibling::*/@x < @x)]");
		DefaultElement nodex = (DefaultElement)xPathx.selectSingleNode (rootDiagramElement);

		XPath xPathy = new DefaultXPath ("/root-container/*[not(preceding-sibling::*/@y <= @y) and not(following-sibling::*/@y < @y)]");
		DefaultElement nodey = (DefaultElement)xPathy.selectSingleNode (rootDiagramElement);

		if (nodex != null) {
			minX = Integer.parseInt(nodex.attributeValue("x"));
		}
		if (nodey != null) {
			minY = Integer.parseInt(nodey.attributeValue("y"));
		}
		return new Integer[]{minX,minY};
	}
	
	private Integer[] getGlobalMinXY(Element rootDiagramElement){
		int minX=0;
		int minY=0;
		
		XPath allNodes = new DefaultXPath ("//node|node-container");
		XPath allLabels = new DefaultXPath ("../label");
		XPath allBendpoints = new DefaultXPath ("edge/bendpoint");
		for(Object child : allNodes.selectNodes(rootDiagramElement)){
			if(child instanceof Element){
				DefaultElement node = (DefaultElement) child;
				int xSuperState = 0;
				int ySuperState = 0;
				if ("node-container".equals(node.getParent().getName())) {
    	    		//If parent is super state coordinate are relative to superstate
    	    		xSuperState = Integer.valueOf(node.getParent().attribute("x").getValue()).intValue() + 8;
    	    		ySuperState = Integer.valueOf(node.getParent().attribute("y").getValue()).intValue() + 8;
				}
				
				int x = Integer.valueOf(node.attribute("x").getValue()).intValue() + xSuperState;
				int y = Integer.valueOf(node.attribute("y").getValue()).intValue () + ySuperState;
				int xc = Integer.valueOf(node.attribute("width").getValue()).intValue()/2+x;
				int yc = Integer.valueOf(node.attribute("height").getValue()).intValue()/2+y;
				
				if(minX>x)
					minX=x;
				if(minY>y)
					minY=y;
				
				List bendpoints = allBendpoints.selectNodes(child);
				//BENDPOINTS
				for(Object point : bendpoints){
					if(point instanceof Element){
    					  	    					
						DefaultElement nodePoint = (DefaultElement) point;
						
						int xp = Integer.valueOf(nodePoint.attribute("w1").getValue()).intValue() + xc;
    					int yp = Integer.valueOf(nodePoint.attribute("h1").getValue()).intValue() + yc;
    					
    					if(minX>xp)
    						minX=xp;
    					if(minY>yp)
    						minY=yp;
					}
				}
				
				if(!bendpoints.isEmpty()){
					if(bendpoints.size()%2==0){
						Object midPoint1 = bendpoints.get((bendpoints.size())/2-1);
						Object midPoint2 = bendpoints.get((bendpoints.size()+2)/2-1);
						if(midPoint1 instanceof Element && midPoint2 instanceof Element){
  	    					
							DefaultElement nodeMidPoint1 = (DefaultElement) midPoint1;
							DefaultElement nodeMidPoint2 = (DefaultElement) midPoint2;
							
							int xmp1 = Integer.valueOf(nodeMidPoint1.attribute("w1").getValue()).intValue() + xc;
	    					int ymp1 = Integer.valueOf(nodeMidPoint1.attribute("h1").getValue()).intValue() + yc;
	    					int xmp2 = Integer.valueOf(nodeMidPoint2.attribute("w1").getValue()).intValue() + xc;
	    					int ymp2 = Integer.valueOf(nodeMidPoint2.attribute("h1").getValue()).intValue() + yc;
	    					
	    					int xmp = xmp1<xmp2?xmp1:xmp2 + Math.abs(xmp2-xmp1)/2;
	    					int ymp = ymp1<ymp2?ymp1:ymp2 + Math.abs(ymp2-ymp1)/2;
	    					
	    					//LABEL
	    					for(Object label : allLabels.selectNodes(midPoint1)){
	    						if(label instanceof Element){
	    							DefaultElement nodeLabel = (DefaultElement) label;
	    							
	    							int xl = Integer.valueOf(nodeLabel.attribute("x").getValue()).intValue() + xmp;
	    	    					int yl = Integer.valueOf(nodeLabel.attribute("y").getValue()).intValue() + ymp-2;
	    	    					
	    	    					if(minX>xl)
	    	    						minX=xl;
	    	    					if(minY>yl)
	    	    						minY=yl;
	    						}
	    					}
						}
					}else{
						Object midPoint = bendpoints.get((bendpoints.size()+1)/2-1);
						if(midPoint instanceof Element){
  	    					
							DefaultElement nodeMidPoint = (DefaultElement) midPoint;
							
							int xmp = Integer.valueOf(nodeMidPoint.attribute("w1").getValue()).intValue() + xc;
	    					int ymp = Integer.valueOf(nodeMidPoint.attribute("h1").getValue()).intValue() + yc;
	    					//LABEL
	    					for(Object label : allLabels.selectNodes(midPoint)){
	    						if(label instanceof Element){
	    							DefaultElement nodeLabel = (DefaultElement) label;
	    							
	    							int xl = Integer.valueOf(nodeLabel.attribute("x").getValue()).intValue() + xmp;
	    	    					int yl = Integer.valueOf(nodeLabel.attribute("y").getValue()).intValue() + ymp;
	    	    					
	    	    					if(minX>xl)
	    	    						minX=xl;
	    	    					if(minY>yl)
	    	    						minY=yl;
	    						}
	    					}
						}
					}
				}
			}
		}
		
		return new Integer[]{minX,minY};
	}
}