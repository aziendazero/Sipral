package com.phi.ps;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.node.SubProcessResolver;
import org.jbpm.jpdl.JpdlException;

import com.phi.cs.util.CsConstants;

/**
 * Sub process resolver: find processes into TreeBean
 * 
 * Original version uses:
 * subProcessDefinition = jbpmContext.getGraphSession().findProcessDefinition(subProcessName, version);
 * or:
 * jbpmContext.getGraphSession().findLatestProcessDefinition(subProcessName);
 * 
 * To use Catalog server persistence use:
 * select pd from org.jbpm.graph.def.ProcessDefinition as pd where pd.name = :name order by pd.version desc
 * or:
 * select pd from org.jbpm.graph.def.ProcessDefinition as pd where pd.name = :name and pd.version = :version"
 * 
 * @author alex.zupan
 */
public class JbpmSubProcessResolver implements SubProcessResolver {

	private static final long serialVersionUID = 1414529993862050069L;
	private static final Logger log = Logger.getLogger(ProcessManagerImpl.class);
	
	@Override
	public ProcessDefinition findSubProcess(Element subProcessElement) {
		ProcessManager pm = ProcessManagerImpl.instance(); 
		ProcessDefinition subProcessDefinition = null;

		String subProcessName = subProcessElement.attributeValue("name");
		
		pm.getSubProcessStack().push(subProcessName);
		
		if (subProcessName.endsWith("?stateless=true")) {
			subProcessName = subProcessName.substring(0, subProcessName.length() - 15);
			pm.setStateless(true);
		}
		
		// now, we must be able to find the sub-process
		if (subProcessName != null) {
			HashMap<String, ProcessDefinition> psCache = pm.getProcessesCache();
			if (subProcessName.contains("/CORE/")){
				Context app = Contexts.getApplicationContext();	
				String customer = (String)app.get(CsConstants.CUSTOMER);
				
				if (customer != null && !customer.isEmpty()) {
					String customerProcess = subProcessName.replace("/CORE/", "/customer_"+customer+"/");

					if (psCache.containsKey(customerProcess))
							subProcessName=customerProcess;

				}
			}
			
			
			subProcessDefinition =psCache.get(subProcessName);
			pm.setCurrentSubProcess(subProcessName);
			TreeBean treeBean = TreeBean.instance();
			if(treeBean.getProcessPath()==null){
				treeBean.setProcessPath(new LinkedList<String>());
				if(treeBean.getNodesCache().get(pm.getCurrentProcess())!=null)
					treeBean.getProcessPath().addAll(treeBean.getNodesCache().get(pm.getCurrentProcess()).getProcessPath());
			}
			treeBean.getProcessPath().add(subProcessName);
			
			if (log.isInfoEnabled()) {
				log.info("[cid="+Conversation.instance().getId()+"] Starting subproc: " + subProcessName);
			}
			
		} else {
			throw new JpdlException("no sub-process name specfied in process-state: " + subProcessElement.asXML());
		}

		return subProcessDefinition;
	}
}