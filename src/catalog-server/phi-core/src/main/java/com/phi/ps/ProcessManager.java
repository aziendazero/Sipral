package com.phi.ps;


import java.util.HashMap;
import java.util.Stack;

import javax.ejb.Local;

import org.jboss.virtual.VirtualFile;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import com.phi.cs.exception.PhiException;

@Local
public interface ProcessManager {
	
	public com.phi.ps.Node loadRootProcessNode(VirtualFile processesRootFolder, boolean useDbProcSecurity) throws PhiException;

	public String manageTask() throws PhiException;
	
	/**
	 * Manages jbpm tasks, must be called to execute a single task in a jbpm process, including 
	 * actions and scripts defined in file processdefinition.xml
	 * @param input - name of the current task
	 * @param taskInstance - instance of the current task
	 */
	public String manageTask(String input) throws PhiException;

	public String manageTask(String input, boolean restore) throws PhiException;

//	/**
//	 * Set the current process name
//	 * @param currentProcessDefinitionName - process name
//	 */
//	public void setCurrentProcessDefinitionName(String currentProcessDefinitionName);

	/**
     * It is used to configure the environment to handle a new instance of a jbpm business process, it starts a new conversation
     * deploys process definition, and creates a new process instance
     * 
     */
//	public String switchProcess();
	
	/**
	 * Gets the destination page name and performs a redirect towards it
	 * @param pageName destination page name
	 */
//	public void managePagination(String pageName) throws PhiException;
	
	
	/**
	 * start a Process. if RestoredProcess#getProcessInstance is not null
	 * the method resume the process instance
	 * @param aRestoredProcess a bean containing information about the process
	 * 		to start or resume
	 */
	public void startProcess(com.phi.ps.Node processInformationBean ) throws PhiException;
	
	/**
	 * start a Process from process name. 
	 * @param aRestoredProcess a bean containing information about the process
	 * 		to start or resume
	 */
	public void startProcessFromName(String processName ) throws PhiException;
	
	/**
	 * The method end the process instance and all of its
	 *  subprocesses and superProcesses.
	 *  
	 * @param aProcessInstance
	 */
//	public void endProcessInstance(ProcessInstance aProcessInstance) throws ApplicationException;
	
	/**
	 * 
	 * @param procDefPath
	 */
//	@Deprecated
//	public void startSubProcess(String procDefPath);
	
	/**
	 * End all open tasks belonging to the current process instance
	 * 
	 * @author rossi
	 */
//	public void endTaskInstance();
	
	/**
	 * select all process instances belonging to the actor which is logged in.
	 * Processes must not have subprocess active to be returned.
	 * Null is returned if no task instance is found.
	 * @param aPhiIdentity the logged user in the session
	 * @return the list of process instance 
	 */
//	public List<ProcessInstance> getProcessInstanceDependenciesPerActor(String aPhiIdentityId);
	
	/**
	 * This method is called when a "superstate-end" event occurs. All HL7 object
	 * in the conversation context are commited to HL7 db.
	 *  
	 * @author rossi
	 */
//	public void saveSuperState();
		
	/**
	 * The method deletes all process instance that belong to actor identified by 
	 * aPhiIdentityId and that started before the aDate
	 * 
	 * @param aPhiIdentityId the id identifying the actor which owns process 
	 * 						  instance
	 * @param aDate the date before which delete all unfinished process instance
	 * @author rossi
	 */
//	public void deleteUnfinishedProcessInstance(String aPhiIdentityId, Date aDate);
	
	/**
	 * Get rmims belonging to banner bean and put them in the process instance.
	 * This is useful when some rmim object called during the process are not 
	 * injected.
	 *  
	 */
//	public void synchronizeWithBannerBean();
	
	/**
	 * Method is called from inside a jbpm process. It ends the conversation and
	 * flush RIM data to db. It's necessary to call this method after the 
	 * task instances are terminated. In this way, jbpm db is consistent with
	 * reality.
	 * @author rossi
	 */
	public void endProcessExecution() throws PhiException ;
	
//	public void storeVariableInProcessContextInstance(String objName, Object object);
	
//	public void removeVariableFromProcessContextInstance(String objName);
	
//	public void createNestedConv() throws PhiException;
//	
//	public void deleteNestedConv() throws PhiException;
	
	public HashMap<String, ProcessDefinition> getProcessesCache();
	
	public HashMap<String, ProcessSecurity> getProcSecurity();

	public ProcessInstance getProcess();

	public void setProcess(ProcessInstance process);
	
	public String getCurrentProcess();

	public void setCurrentProcess(String currentProcess);

	public String getCurrentSubProcess();

	public void setCurrentSubProcess(String currentSubProcess);
	
	public Stack<String> getSubProcessStack();
	
	public boolean isStateless();

	public void setStateless(boolean stateless);
	
	public void startProcess() throws PhiException;
	
	public void saveSuperState() throws PhiException;
	
//	public ProcessDefinition getProcessDefinition(String name);
	
	public String getScreenShot();
	
//	public ClassLoader getPmClassLoader();
	
	public void beginConversation(String processDefinitionName);
}