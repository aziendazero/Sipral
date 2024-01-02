package com.phi.entities.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jbpm.graph.def.Action;
import org.jbpm.graph.def.ProcessDefinition;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.util.CsConstants;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.cs.view.converter.PhiConvertDateTime;
import com.phi.entities.baseEntity.ProcSecurity;
import com.phi.entities.baseEntity.ProcSecurityRole;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.events.PhiEvent;
import com.phi.ps.Node;
import com.phi.ps.ProcessManager;
import com.phi.ps.ProcessManagerImpl;
import com.phi.ps.ProcessSecurity;
import com.phi.ps.ProcessSecurityRole;
import com.phi.ps.TreeBean;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("ProcSecurityAction")
@Scope(ScopeType.CONVERSATION)
public class ProcSecurityAction extends BaseAction<ProcSecurity, Long> {

	private static final long serialVersionUID = 1011522500L;
	private static final Logger log = Logger.getLogger(ProcSecurityAction.class);
	
	public static ProcSecurityAction instance() {
		return (ProcSecurityAction) Component.getInstance(ProcSecurityAction.class, ScopeType.CONVERSATION);
	}
	
	
	private static final String getDomainHql = "SELECT code FROM CodeValueRole code JOIN code.parent parent JOIN parent.codeSystem codSys " +
			"WHERE parent.status = 1 AND parent.type <> 'C' AND code.status = 1 " +
			"AND parent.displayName =:domainName AND codSys.name=:codeSystemName " +
			"AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :codeSystemName AND cssub.status = 1)";
	
	private static final String hqlBySdloc = "SELECT DISTINCT code FROM CodeValueRole code JOIN code.parent parent JOIN parent.codeSystem codSys " +
			"JOIN code.enabledServiceDeliveryLocations enabledSdl " +
			"WHERE parent.status = 1 AND parent.type <> 'C' " +
			"AND parent.displayName =:domainName AND codSys.name=:codeSystemName " +
			"AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :codeSystemName AND cssub.status = 1) " +
			"AND enabledSdl.internalId IN (:sdLocs) ";
	
	private String customer;
	
	private String domainName;
	private String codeSystemName;
	
	private List<Long> sdLocs;
	
	public ProcSecurityAction() {
		super();
		
		Context appl = Contexts.getApplicationContext();

		customer = (String)appl.get("CUSTOMER");
		if (customer == null || customer.isEmpty()){
			customer = "CORE";
		}
		
		domainName = (String)appl.get("dictionary.defaultKey");
		codeSystemName = (String)appl.get("dictionary.defaultName");
		
		sdLocs = UserBean.instance().getSdLocs();
		
	}

	public void readFromFs() {
		List <ProcSecurity> psList = new ArrayList<ProcSecurity>();
		injectList(psList);
	}
	

	/**
	 * Calculate breadcrumb
	 * @param ps process
	 * @return translated breadcrumb
	 */
	public String calculateBreadcrumb(ProcSecurity ps) {
		FunctionsBean f = FunctionsBean.instance();
		StringBuilder breadcrumb = new StringBuilder();
    	if (ps != null) {
    		String[] folders = ps.getPath().split("/");
    		StringBuilder relativePath = new StringBuilder();
    		for (int i=0; i<folders.length; i++) {
    			relativePath.append(folders[i]);
    			
    			if (!(folders[i].equals("CORE") || folders[i].equals("PROCESSES") || folders[i].startsWith("customer_"))) {
	    			breadcrumb.append(f.getTranslation(relativePath.toString()));
	    			if (i != folders.length-1) {
	    				breadcrumb.append(" → ");
	    			}
    			}
    			relativePath.append("/");
    		}
    	}

    	return breadcrumb.toString();
    }
	
	public void injectFromProcessManager() throws PersistenceException {
		
		ProcessManager pm  = ProcessManagerImpl.instance();
		HashMap<String,ProcessDefinition> cache = pm.getProcessesCache();
		List <ProcSecurity> psList = new ArrayList<ProcSecurity>();
		
		//get a list of CodeValue, containing the list of 
		List<CodeValueRole> availableRoles = getCurrentRoles();
		HashMap<String, CodeValueRole> rolesCodeAndCv = getCurrentCustomerRolesHash(availableRoles);
		
		// for each process in cache of ProcessManager, populate a ProcSecurity Entity.
		// key of cache is the process path.
		for (String key : cache.keySet()) {
			ProcSecurity ps = new ProcSecurity();
			ProcessDefinition psDef = cache.get(key);
			ps.setPath(key); 
			
			Action secAction = psDef.getProcessDefinition().getAction("security");
			if (secAction == null || secAction.getActionExpression().isEmpty()) {
				ps.setSecurityString("");
			}
			else {
				ps.setSecurityString(secAction.getActionExpression());
				List<String> currentCustomerRoles = getCustomerRolesFromExpr(secAction.getActionExpression(), customer);
				List<ProcSecurityRole> roles = new ArrayList<ProcSecurityRole>();
				for (String code : currentCustomerRoles) {
					if (code.equals("ALWAYS_EXECUTABLE")){
						ps.setAlwaysExecutable(true);
						continue;
					}
					if (rolesCodeAndCv.containsKey(code)) {
						CodeValueRole cv = rolesCodeAndCv.get(code);
						ProcSecurityRole psr = new ProcSecurityRole();
						psr.setRole(cv);
						roles.add(psr);  //FIXME: in the future we can use CodeValueRoles...
					}
				}
				ps.setProcSecurityRole(roles);
			}
			
			//show only current customer process, and save them with a customer property. 
			//this allow to have on same db different customer's process security.
			ps.setCustomer(customer);
			ps.setMacroprocess(true);
			psList.add(ps);
			log.debug("--> "+key);
		}
		
		
		injectList(psList);
		
	}
	
	public void insertPs (String relativePath, String securityExpression, List<ProcessSecurityRole> roles, String customer, List<String> parameters) throws PhiException {

		List<CodeValueRole> availableRoles = getCurrentRoles();
		HashMap<String, CodeValueRole> rolesCodeAndCv = getCurrentCustomerRolesHash(availableRoles);
		
		ProcSecurity ps = new ProcSecurity();
		ps.setPath(relativePath); 
		ps.setSecurityString(securityExpression);
		
		ps.setCustomer(customer);
		
		List<ProcSecurityRole> rolesCv = new ArrayList<ProcSecurityRole>();
		for (ProcessSecurityRole psr : roles) {
			if (psr.getRole().equals("ALWAYS_EXECUTABLE")){
				ps.setAlwaysExecutable(true);
				continue;
			}
					
			if (rolesCodeAndCv.containsKey(psr.getRole())) {
				CodeValueRole cv = rolesCodeAndCv.get(psr.getRole());
				ProcSecurityRole psrEntity = new ProcSecurityRole();
				psrEntity.setRole(cv);
				rolesCv.add(psrEntity);  //FIXME: in the future we can use CodeValueRoles...
			}
		}
		ps.setProcSecurityRole(rolesCv);
		
		if (parameters != null) {
			
			ps.setParameter(new ArrayList<CodeValueParameter>());
		
			for (String parameter : parameters) {
				CodeValueParameter cvp = ca.get(CodeValueParameter.class, parameter);
				ps.getParameter().add(cvp);
			}
		}
		
		create(ps);
		ca.flushSession();
	}

	
	public void updatePs (String relativePath, List<String> parameters) throws PhiException {
		Criteria crit = ca.createCriteria(ProcSecurity.class);
		crit.add(Restrictions.eq("path", relativePath));
		List<ProcSecurity> procSecs = crit.list();
		
		if (procSecs.size() != 1) {
			throw new PhiException("Unable to find ProcSecurity with path " + relativePath, ErrorConstants.PROCESS_DEFINITION_LOAD_ERR_CODE);
		}
		
		ProcSecurity ps = procSecs.get(0);
		
		ps.getParameter().clear();
		
		if (parameters != null) {
			for (String parameter : parameters) {
				CodeValueParameter cvp = ca.get(CodeValueParameter.class, parameter);
				if (cvp == null) {
					throw new PhiException("Process " + relativePath + "requires parameter " + parameter + " but that parameter doesen't exsist", ErrorConstants.PROCESS_DEFINITION_LOAD_ERR_CODE);
				}
				ps.getParameter().add(cvp);
			}
		}
		
		create(ps);
		ca.flushSession();
	}

	/**
	 * Used to display available roles
	 * @return list of CodeValueRole filtered by enabled sdl selected al login
	 * @throws PersistenceException
	 */
	public List<CodeValueRole> getCurrentRoles() throws PersistenceException {

		Map<String, Object> pars = new HashMap<String, Object>();
		pars.put("domainName", domainName);
		pars.put("codeSystemName", codeSystemName);
		
		String hql = getDomainHql;
		
/*		if (sdLocs != null && !sdLocs.isEmpty()) {
			hql = hqlBySdloc;
			pars.put("sdLocs", sdLocs);
		}*/

		List<CodeValueRole> res = ca.executeHQLwithParameters(hql, pars); 

		return res;
	}
	
	
	private HashMap<String, CodeValueRole> getCurrentCustomerRolesHash(List<CodeValueRole> roles) {
		HashMap<String, CodeValueRole> codeAndCv = new HashMap<String, CodeValueRole>();
		for (CodeValue cv : roles) {
			codeAndCv.put(cv.getCode(),(CodeValueRole) cv);
		}
		return codeAndCv;
	}
	
	
	public String getCoreAndCustomerRoles(String securityString, String customer) {
		List<String> roles = new ArrayList<String>();
		String ret = "";
		
		//get only the current customer roles.
		roles = getCustomerRolesFromExpr(securityString, "CORE");
		if (!roles.isEmpty())
			ret="CORE:"+ StringUtils.join(roles,",")+ "  ";
		
		if (customer != null && !customer.isEmpty()) {
			roles = getCustomerRolesFromExpr(securityString, customer);
			ret+=customer+":"+StringUtils.join(roles,",");
		}
			
		return ret;
	}
	
	/**
	 * Identical to ContentsTabSecurity logic of Designer.
	 * @param expression
	 * @param customerName
	 * @return
	 */
	private List<String> getCustomerRolesFromExpr(String expression, String customerName) {
		List<String> ret = new ArrayList<String>();
		
		if (expression == null || expression.isEmpty() || customerName.isEmpty())
			return ret;
		
		if (!customerName.equals("CORE") && !expression.contains(customerName+":"))
			return ret;
		
		if (customerName.equals("CORE")) {
			if (!expression.contains("|") && !expression.contains(":")) {
				return Arrays.asList(expression.split(";"));
			}
			else if (!expression.contains("|") && expression.contains(":")) {
				return ret;
			}
			else  {
				//expression.contains("|")) 
				String coreRoles = expression.substring(0, expression.indexOf("|"));
				if (coreRoles.contains(":")) {  //like:  A:1;2|B:3;4
					return ret;
				}
				else {
					return Arrays.asList(coreRoles.split(";"));
				}
			}
		}
		
		else {
		
			//Customer is not CORE, and expression contains customer:
			
			String customerRoles= expression.substring(expression.indexOf(customerName+":"),expression.length());
			if (customerRoles.contains("|")) {
				//other customer remained in string
				customerRoles = customerRoles.substring(0, customerRoles.indexOf("|"));
			}
			customerRoles=customerRoles.replace(customerName+":", "");
			
			if (customerRoles.isEmpty())
				return ret;
			
			return Arrays.asList(customerRoles.split(";"));
		}
	}
	
	
	public void createAll(List<ProcSecurity> l) throws PhiException {
		
		//List<ProcSecurity> l = ((PhiDataModel<ProcSecurity>)Contexts.getConversationContext().get("ProcSecurityList")).getList();
		for (ProcSecurity p : l ) {
			create(p);
		}
		
		String earPath = (String)Contexts.getApplicationContext().get(CsConstants.CATALOG_SERVER_EAR_PATH); //FIXME DOESENT WORK!
		File earFile = new File(earPath);
	    if (earFile.exists()) {
            earFile.setLastModified(new Date().getTime());
        }
	}
	
	public void updateProcSecurityCache(ProcSecurity proc) throws PhiException {
		
		String pathToSet = proc.getPath();
//		String[] rolesToSet = codesToStrings(proc.getRoles());
		
		ProcessSecurity processSecurity = new ProcessSecurity();
		processSecurity.setProcSecurityRole(proc.getProcSecurityRole());
		
		ProcessManager procMan = ProcessManagerImpl.instance();  
		procMan.getProcSecurity().put(pathToSet, processSecurity);
		
		RepositoryManager repo = RepositoryManager.instance();

		repo.getProcSecurity().get(customer).put(pathToSet, processSecurity); //FIXME repo.getProcSecurity().get(cust) is null on first import of processes into db, restart of jboss is needed, because repository init populates this
		
		Node rootNode = repo.getRootProcessNode();
		updateTreeBeanCacheNode(rootNode, cleanPath(pathToSet), pathToSet, processSecurity.getEnabledRoles());
	}
	
	
	
	
	private void updateTreeBeanCacheNode(Node rootNode, List<String> pathElements, String fullPath, List<ProcessSecurityRole> rolesToSet) {
		
		if (pathElements.size() == 1){
			for (Node n : rootNode.getChildren()) {
				if (n.isLeaf() &&  fullPath.equals(n.getPath())) {
					n.setRoles(rolesToSet);
					HashMap <String, Node> treeBeanNodeCache = TreeBean.instance().getNodesCache();
					if (treeBeanNodeCache.containsKey(fullPath))
						treeBeanNodeCache.get(fullPath).setRoles(rolesToSet);
					else 
						treeBeanNodeCache.put(fullPath,n);
					break;
				}
			}
		}
		else if (pathElements.size() > 1){
			String childFolder = pathElements.get(0);
			List<String> subPath = pathElements.subList(1, pathElements.size());
			for (Node n : rootNode.getChildren()){
				if (n.isLeaf())
					continue;
				if (n.getPath().equals(childFolder)) {
					updateTreeBeanCacheNode(n, subPath, fullPath, rolesToSet);
					break;
				}
			}
			
		}
		else {
			log.error ("ERRORE parsing "+fullPath);
		}
		
	}
	
	private List<String> cleanPath (String path) {
		String [] elements = path.split("/");
		List<String> ret = new ArrayList<String>();
		
		for (String element : elements) {
			if (element == null || element.isEmpty() || "CORE".equals(element) || "PROCESSES".equals(element) || element.contains("customer_")) {
				continue;
			}
			ret.add(element);
		}
		
		return ret;
	}
	
	private static final String csvHeader = "\"CUSTOMER\",\"PATH\",\"ORIGINALSECSTRING\",\"ROLESCONCAT\",\"ALWAYSEXEC\"\n";
	//array index for the splitted string:       0            1           2                     3              4
	
	public StringBuffer exportAllCsv() throws PhiException{
		
		StringBuffer buf = new StringBuffer();
		buf.append(csvHeader);
		
		backupRestrictions();
		cleanRestrictions();

		equal.put("customer", customer);
		List<ProcSecurity> procList = list();
		restoreRestrictions();
		
		if (procList == null || procList.isEmpty())
			return buf;
		
		
		
		for (ProcSecurity pssec : procList) {
			
			String rolesConcatenation =StringUtils.join(pssec.getProcSecurityRole(),";");
			String csvRow = "\""+pssec.getCustomer()+"\",\""+pssec.getPath()+"\",\""+pssec.getSecurityString()+"\",\""+ rolesConcatenation +"\",\""+pssec.getAlwaysExecutable()+"\"\n";
			buf.append(csvRow);
		}
		
		return buf;
	}
	
	public boolean updateProcSecurity (String csvLine) throws PhiException {
		
		if (csvLine == null || csvLine.isEmpty() || csvLine.equals(csvHeader) || csvLine.length() < 2)
			return false;
		
		String tmp = csvLine.substring(1,csvLine.length()-1);
		String[] csvElement= tmp.split("\",\"");
		if (csvElement.length != csvHeader.split("\",\"").length)
			return false;
		
		backupRestrictions();
		cleanRestrictions();
		
		//get a list of CodeValue, containing the list of 
		List<CodeValueRole> availableRoles = getCurrentRoles();
		HashMap<String, CodeValueRole> rolesCodeAndCv = getCurrentCustomerRolesHash(availableRoles);
		
		equal.put("customer", csvElement[0]);
		equal.put("path", csvElement[1]);
		List <ProcSecurity> procList = list();
		restoreRestrictions();
		
		ProcSecurity process = null;
		if (procList.isEmpty() || procList.get(0) == null) {
			process = new ProcSecurity();  //create new missing process sec
			process.setCustomer(csvElement[0]);
			process.setPath( csvElement[1]);
		}
		else {
			process = procList.get(0); //update existing process sec
		}
		
		process.setSecurityString(csvElement[2]);
		
		String [] csvRoles = csvElement[3].split(";");
		List<ProcSecurityRole> roles = new ArrayList<ProcSecurityRole>();
		
		for (String code : csvRoles) {
			if (code == null || code.isEmpty() || code.equals("null") || code.equals("ALWAYS_EXECUTABLE")){
				continue;
			}
			if (rolesCodeAndCv.containsKey(code)) {
				CodeValueRole cv = rolesCodeAndCv.get(code);
				ProcSecurityRole psr = new ProcSecurityRole();
				psr.setRole(cv);
				roles.add(psr);  //FIXME: in the future we can use CodeValueRoles...
			}
		}
		process.setProcSecurityRole(roles);
		
		String alwaysEx = csvElement[4];
		boolean alwaysExecutable = false;
		if (alwaysEx != null && !alwaysEx.isEmpty() && alwaysEx.equals("true"))
			alwaysExecutable = true;
		process.setAlwaysExecutable(alwaysExecutable);
		
		create(process);
		ca.flushSession();
		
		return true;
	}

	public void deleteAll() throws PhiException {
		cleanRestrictions();

		equal.put("customer", customer);
		List<ProcSecurity> procList = list();
		restoreRestrictions();
		
		for (ProcSecurity proc : procList) {
			ca.delete(proc);
			
			Events.instance().raiseEvent(PhiEvent.DELETE, proc);
		}
		ca.flushSession();
		restoreRestrictions();
		
	}
	
}