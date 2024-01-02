package com.phi.entities.actions;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import com.phi.annotations.CodeValueExtension;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.dictionaryManager.CodeUsersValue;
import com.phi.entities.dataTypes.CodeSystem;
import com.phi.entities.dataTypes.CodeUsers;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValueCity;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("CodeValueAction")
@Scope(ScopeType.CONVERSATION)
public class CodeValueAction extends CodeValueBaseAction<CodeValue>{

	private static final long serialVersionUID = -301215865722L;
	private static final Logger log = Logger.getLogger(CodeValueAction.class);
	
	private static final String getFirstLevelDomainsHql = "SELECT code FROM CodeValue code JOIN code.codeSystem codSys WHERE code.parent IS NULL AND codSys.name = :csName AND code.status = 1  AND code.type <> 'C'" +
			" AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :csName AND cssub.status = 1) ";

	private static final String getDomainHql = "SELECT code FROM CodeValue code JOIN code.codeSystem codSys " +
			"WHERE code.status = 1 AND code.type <> 'C' " +
			"AND code.displayName =:domainName AND codSys.name=:codeSystemName " +
			"AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :codeSystemName AND cssub.status = 1) ";


	public static CodeValueAction instance() {
		return (CodeValueAction) Component.getInstance(CodeValueAction.class, ScopeType.CONVERSATION);
	}




	public List<SelectItem> getTypeList () {
		List<SelectItem> ret = new ArrayList<SelectItem>();
		ret.add(new SelectItem("S", "Dominio"));
		ret.add(new SelectItem("C", "Codice"));
		return ret;
	}

	public CodeValue newEntity(String codeValueClass) throws  ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class cvClass = Class.forName("com.phi.entities.dataTypes." + codeValueClass);
		return(CodeValue) cvClass.newInstance();
	}

//	public CodeValue get (String codeValueClass, String id) {
//		return (CodeValue) ca.get(codeValueClass, id);
//	}

	public List<SelectItem> getFirstLevelDomains(String codeSystem) throws PersistenceException {
		return getFirstLevelDomains(codeSystem, null);
	}

	public List<SelectItem> getFirstLevelDomains(String codeSystem, String parentDomain) throws PersistenceException {
		List<SelectItem> ret = new ArrayList<SelectItem>();
		CodeValueAction cva = CodeValueAction.instance();
		Collection<CodeValue> cvs = null;
		if (parentDomain == null) {
			try {
				HashMap<String, Object> pars = new HashMap<String, Object>();
				pars.put("csName", codeSystem);
				GenericAdapterLocalInterface ga = GenericAdapter.instance();
				cvs = ga.executeHQL(getFirstLevelDomainsHql, pars);
			} catch (Exception e) {
				log.error("Error finding parent domain", e);
			}
		} else {
			CodeValue domain = cva.getDomain(codeSystem, parentDomain);
			if (domain == null)
				return ret;
			cvs = domain.getChildren();
		}
		for (CodeValue cv : cvs) {
			if (!cv.getType().equals("C")) {
				SelectItem sel = new SelectItem(HibernateProxyHelper.getClassWithoutInitializingProxy(cv).getSimpleName()+"-"+cv.getId(), cv.getCurrentTranslation());
				ret.add(sel);
			}
		}
		return ret;
	}


	//	public void injectById(String id, String convName) throws PersistenceException {
	//		CodeSystemAction csa = CodeSystemAction.instance();
	//		
	//		
	//		String codeValueClass=csa.getCodeValueClass();
	//		if (codeValueClass == null || codeValueClass.isEmpty()) {
	//			codeValueClass="CodeValuePhi";
	//		}
	//		
	//		List res = ca.executeHQL("select cv from CodeValue where id = '"+id+"'");
	//		if (res != null && !res.isEmpty() && res.get(0) != null) {
	//			
	//			entity = (CodeValue) res.get(0);
	//			conversationName=convName;
	//			Contexts.getConversationContext().set(conversationName, entity); 
	//			
	//		}
	//	}

	//used to memorize the tree status
	//	public void closeNode() {
	//		if (cvIdNodeToBeClosed == null || cvIdNodeToBeClosed.isEmpty())
	//			return;
	//		
	//		HashMap<String, Boolean> treeNodeOpenStatus = (HashMap<String, Boolean>)Contexts.getConversationContext().get("treeNodeOpenStatus");
	//		if (treeNodeOpenStatus == null ) {
	//			return;
	//		}
	//		else {
	//			treeNodeOpenStatus.put (cvIdNodeToBeClosed, Boolean.FALSE);
	//		}
	//	}
	//	
	//	private String cvIdNodeToBeClosed;
	//	public void setCvIdNodeToBeClosed (String cvId) {
	//		cvIdNodeToBeClosed=cvId;
	//	}

	/**
	 * Used by Dictionary Manager web solution, to create a new codevalue
	 * @param parentId
	 * @param nodeType TOPLEVEL, DOMAIN or CODE
	 * @param name
	 * @param translation
	 * @param generateId
	 * @param readableId
	 * @param cs CodeSystem
	 * @return new CodeVlaue
	 */
	public CodeValue createNew(String parentId, String nodeType, String name, String translation, boolean generateId, boolean readableId, CodeSystem cs ) {
		CodeValue cv = null;
		try {
	//		CodeSystem cs = (CodeSystem)Contexts.getConversationContext().get("CodeSystem");
			CodeSystemAction csa = CodeSystemAction.instance();
	//		CodeValueAction cva = CodeValueAction.instance();
			cv = newEntity(cs.getCodeValueClass());
			
			
	//		cva.inject(cv);
			
	//		String id = request.getParameter("id");
	//		String node_type = request.getParameter("node_type");
	//		String name = request.getParameter("name");
	//		String translation = request.getParameter("translation");
			
			//if true the generated id depends on readableId id, if false id = parent.id + this.code
	//		boolean generateId = true;
	//		if ("false".equals(request.getParameter("generateId"))) {
	//			generateId = false;
	//		}
			
			//if true the generated id is based on first free abbreviation of the name
	//		boolean readableId = false;
	//		if ("true".equals(request.getParameter("readableId"))) {
	//			readableId = true;
	//		}
			
			if (!Pattern.matches("[a-zA-Z0-9]*", name)){
				if (Pattern.matches("[a-zA-Z0-9]*", name.replaceAll(" ",""))) {
					name=name.replaceAll(" ","");
				}
				else {
					name = "newCode_"+(new Date()).getTime();
				}
			}
			
			String version = "0";
			
			cv.setVersion(Integer.parseInt(version));
			cv.setCode(name);
			cv.setDisplayName(name);
			if("true".equals(translation)){	//in ssa, set also the translation
				cv.setLangIt(name);
			}
			cv.setValidFrom(new Date());
			cv.setCodeSystem(cs);
			
//			long childCount=-1; //set to -1 to allow to have additional childs. if the type is "C", childCount=0;
			String calculatedOid ="";
			
			CodeValue cvParent=null;
			if ("TOPLEVEL".equals(nodeType)) {
				//id passed is null!!
				cv.setType("T");
				String suggestedId = suggest(cs);
				calculatedOid =cs.getId()+"."+suggestedId;
				csa.checkConsistency(name, "T"); //check if there are other CodeValue (domain) with the same display name.
				version= cs.getVersion()+"";
			} else {
				
				String suggestedId ="";
				if (parentId == null) {
					//creating code at top level
					suggestedId = suggest(cs);
					calculatedOid = cs.getId()+"."+suggestedId;
					csa.checkConsistency(name,"C"); //check if there are other CodeValue with same Code at top level.
					version = cs.getVersion()+"";
				} 
				else {
					//creating code under other domain.
					cvParent = (CodeValue)CatalogPersistenceManagerImpl.getAdapter().get("com.phi.entities.dataTypes."+cs.getCodeValueClass(), parentId);
					//cvParent = cva.get("com.phi.entities.dataTypes."+cs.getCodeValueClass(), id);
					//Class parentClass = Class.forName ("com.phi.entities.dataTypes."+cs.getCodeValueClass());
					String oidParent = cvParent.getOid();
					if (generateId) {
						if(readableId){
							suggestedId = suggestByName(name, cvParent);
						} else {
							suggestedId = suggest(cvParent);
						}
					} else {
						suggestedId = name;
					}
					calculatedOid = oidParent+"."+suggestedId;
					
					cv.setParent(cvParent);
					
					if (parentId.contains("_V")) {
						version= parentId.substring(parentId.indexOf("_V")+2, parentId.length());
					} 
				}
				
				if ("DOMAIN".equals(nodeType)) {
					cv.setType("S");
					checkConsistency(name,cvParent,"S");  //check if there are other CodeValue with same Code in the same Domain (cvParent).
				} else if ("CODE".equals(nodeType)) {
					cv.setType("C");
					if (cvParent != null) {
						checkConsistency(name,cvParent,"C");  //check if there are other CodeValue with same Code in the same Domain (cvParent).
						cvParent.getChildren().add(cv);
					}
//					childCount=0;
				} else if ("PARAMETER".equals(nodeType)) {
					cv.setType("A");
				}
			}
			
			cv.setOid(calculatedOid);
			if (generateId) {
				if (readableId) {
					cv.setId(calculatedOid);
				} else {
					cv.setId(calculatedOid+"_V"+cs.getVersion());
					
				}
			} else {
				cv.setId(calculatedOid);
			}
			cv.setStatus(1);
			cv.setCreator(UserBean.instance().getUsername());
			cv.setDefaultChild(false);
			cv.setSequenceNumber(0);
			
		} catch (Exception e) {
			log.error("Error creating new code value with name "+name+ " in CodeSystem "+cs.getName());
		}
		return cv;
	}



	/**
	 * Used by Dictionary Manager web solution, to preset properties of a new code value, starting from selected CodeSystem.
	 */
	public void setProperties () {

		if (entity == null)
			return;

		CodeSystem cs = (CodeSystem)Contexts.getConversationContext().get("CodeSystem");
		if (cs == null)
			return;

		int version = cs.getVersion();
		String oid = temporary.get("parentId") + "." +temporary.get("idLast");
		String id = oid+"_V"+version;

		entity.setId(id);
		entity.setOid(oid);
		entity.setCreator(UserBean.instance().getUsername());
		entity.setDefaultChild(false);
		entity.setSequenceNumber(0);
		entity.setStatus(1);
		entity.setType((String)temporary.get("type"));

		if (entity.getValidFrom() == null) {
			entity.setValidFrom(new Date());
		}
		entity.setVersion(version);
		entity.setCodeSystem(cs);
		entity.setParent((CodeValue)temporary.get("parentCv"));

		temporary.put("type",null);
		temporary.put("parentName",null);
		temporary.put("parentCv",null);
		temporary.put("parentId",null);
		temporary.put("idLast",null);

	}

	/**
	 * Used by Dictionary Manager web solution, to prepare in conversation the id for a new code/domain, looking to existing codes in the selected domain.
	 */
	public void suggest() {

		List<String> alreadyUsed = new ArrayList<String>(); 
		temporary.put("alreadyUsed", alreadyUsed);  //cleanup
		temporary.put("idLast", "");

		String suggestedId = "";
		if (entity != null) {
			//CV is injected, get id to set as parent.
			temporary.put("parentId", entity.getOid());
			temporary.put("parentName", "[" + entity.getCode() + "] " +entity.getDisplayName());
			temporary.put("parentCv", entity);


			suggestedId = suggest(entity);
		}
		else {
			//adding top level
			CodeSystem cs = (CodeSystem)Contexts.getConversationContext().get("CodeSystem");
			temporary.put("parentId", cs.getId());
			temporary.put("parentName", null);
			temporary.put("parentCv", null);

			suggestedId = suggest(entity);

		}

		temporary.put("alreadyUsed", alreadyUsed);
		temporary.put("idLast",suggestedId);

	}


	//return a suggested id for a children of this codeValue, looking to existing ids.
	public String suggest(Object o) {

		List<String> alreadyUsed = new ArrayList<String>(); 
		if (o instanceof CodeSystem) {
			//injected during tree load
			List<Object[]> idAndTranslation = (List<Object[]>)Contexts.getConversationContext().get("listTopLevel");
			if (idAndTranslation == null)
				return "";

			for (Object [] element : idAndTranslation) {
				if (element != null && element[0] != null) {
					String idz = (String)element[0];
					idz=idz.substring(idz.lastIndexOf(".")+1,idz.length());
					if (idz.contains("_")){
						idz=idz.substring(0,idz.indexOf("_"));
					}
					alreadyUsed.add(idz);
				}
			}
		}
		else if (o instanceof CodeValue) {

			Collection<CodeValue> brother = ((CodeValue)o).getChildren();
			if (brother == null || brother.isEmpty())
				return "1";

			for (CodeValue cv : brother) {
				if (cv != null) {
					String idz = cv.getId();
					idz=idz.substring(idz.lastIndexOf(".")+1,idz.length());
					if (idz.contains("_")){
						idz=idz.substring(0,idz.indexOf("_"));
					}
					alreadyUsed.add(idz);
				}
			}
		}

		int guess=1;
		while (alreadyUsed.contains(guess+"")) {
			guess++;
		}
		return guess+"";

	}
	/**
	 * Suggest a valid id for codevalue
	 * Returns first 3 chars of name if not already used, otherwise retuns 4 chars, 5, ...
	 * @param name
	 * @param cvParent
	 * @return
	 */
	public String suggestByName(String name, CodeValue cvParent) {

		int minLength = 3;

		List<String> alreadyUsed = new ArrayList<String>();

		Collection<CodeValue> brothers = cvParent.getChildren();

		String guess = name.substring(0,minLength++);

		if (brothers == null || brothers.isEmpty()) {
			return guess.toLowerCase();
		}

		for (CodeValue brother : brothers) {
			String idz = brother.getId();
			idz=idz.substring(idz.lastIndexOf(".")+1,idz.length());
			alreadyUsed.add(idz);
		}

		while (alreadyUsed.contains(guess)) {
			guess = name.substring(0,minLength);
			minLength++;
		}

		return guess.toLowerCase();
	}

	/**
	 * Check if the code passed, is a valid code as child of parent cv.
	 * If one matching code is found, the method throws an exception, otherwise ends successfully.  
	 */
	public void checkConsistency(String code, CodeValue parentCv, String type) throws Exception {


		CodeValue elementToSkip = entity; //current codevalue must not be considered in the check.
//		String idToSkip = ""; 
//		if (elementToSkip != null) 
//			idToSkip = elementToSkip.getId();

		for (CodeValue cv : parentCv.getChildren()) { 
			if (cv.equals(elementToSkip))
				continue; 

			if (type.equals("C") ){
				if (cv.getCode().equalsIgnoreCase((String)code)) {
					String msg = FacesErrorUtils.getMessage("CodeValueActionErr1");
					msg = msg.replace("{0}", "" + cv.getId());
					msg = msg.replace("{1}", "" + cv.getCode());
					msg = msg.replace("{2}", "" + parentCv.getDisplayName());
					throw new Exception(msg);
				}
			}
			else if (type.equals("S") ){
				if (cv.getDisplayName().equalsIgnoreCase((String)code)) {
					String msg = FacesErrorUtils.getMessage("CodeValueActionErr2");
					msg = msg.replace("{0}", "" + cv.getId());
					msg = msg.replace("{1}", "" + cv.getCode());
					msg = msg.replace("{2}", "" + parentCv.getDisplayName());
					throw new Exception(msg);
				}
				if (cv.getCode().equalsIgnoreCase((String)code)) {
					String msg = FacesErrorUtils.getMessage("CodeValueActionErr3");
					msg = msg.replace("{0}", "" + cv.getId());
					msg = msg.replace("{1}", "" + cv.getCode());
					msg = msg.replace("{2}", "" + parentCv.getDisplayName());
					throw new Exception(msg);
				}
			}
		}
	}

	public List<SelectItem> getSequenceNumberCombo() {
		List <SelectItem> ret = new ArrayList<SelectItem>();
		ret.add(new SelectItem(0, "0"));
		if (entity != null && entity.getParent() != null) {
			Collection<CodeValue> brothers = entity.getParent().getChildren();
			int i = 0;
			for (CodeValue cv :  brothers) {
				if (cv.getType().equals(("C"))) {
					ret. add( new SelectItem(i, i+""));
					i++;
				}
			}
		}
		return ret;
	}

	/**
	 * Generates a list of name and value of code extensions
	 * Used in dictionary manager process to show extensions
	 * @return
	 * @throws PhiException
	 */
	public List<Map<String,Object>> getExtensions() throws PhiException {
		List<Map<String,Object>> extensions = new ArrayList<Map<String,Object>>();
		try {
			if (entity != null) {
				for (Method method : HibernateProxyHelper.getClassWithoutInitializingProxy(entity).getMethods()) {
					CodeValueExtension annotation = method.getAnnotation(CodeValueExtension.class);
					if (annotation != null) {
						Map<String,Object> extension = new HashMap<String,Object>();
						extension.put("name", annotation.name());
						extension.put("field", Introspector.decapitalize(method.getName().substring(3)));
						extensions.add(extension);
					}
				}
			}

		} catch (Exception e) {
			throw new PhiException("Error finding extensions", e, ErrorConstants.GENERIC_ERR_CODE);
		}
		return extensions;
	}

	/**
	 * Access to Code Extension Provincia (used in dictionary manager process)
	 */
	@Deprecated
	public String getProvince() {
		if(entity instanceof CodeValueCity){
			return ((CodeValueCity)entity).getProvince();

		}else{
			return "";
		}

	}

	@Deprecated
	public void setProvince(String province) {
		if(entity instanceof CodeValueCity){
			((CodeValueCity)entity).setProvince(province);
		}
	}

	/**
	 * Access to Code Extension Zip (used in dictionary manager process)
	 */
	@Deprecated
	public String getZip() {
		if(entity instanceof CodeValueCity){
			return ((CodeValueCity)entity).getZip();

		}else{
			return "";
		}
	}

	@Deprecated
	public void setZip(String zip) {
		if(entity instanceof CodeValueCity){
			((CodeValueCity)entity).setZip(zip);
		}
	}

	/**
	 * Access to Code Extension Distretto sanitario (used in dictionary manager process)
	 */
	@Deprecated
	public String getHealthDistrict() {
		if(entity instanceof CodeValueCity){
			return ((CodeValueCity)entity).getHealthDistrict();

		}else{
			return "";
		}
	}

	@Deprecated
	public void setHealthDistrict(String healthDistrict) {
		if(entity instanceof CodeValueCity){
			((CodeValueCity)entity).setHealthDistrict(healthDistrict);
		}
	}

	/**
	 * Access to Code Extension Codice catastale (used in dictionary manager process)
	 */
	@Deprecated
	public String getLandRegistry() {
		if(entity instanceof CodeValueCity){
			return ((CodeValueCity)entity).getLandRegistry();

		}else{
			return "";
		}
	}

	@Deprecated
	public void setLandRegistry(String landRegistry) {
		if(entity instanceof CodeValueCity){
			((CodeValueCity)entity).setLandRegistry(landRegistry);
		}
	}

	/**
	 * Access to Code Extension ISTAT (used in dictionary manager process)
	 */
	@Deprecated
	public String getIstat() {
		if(entity instanceof CodeValueCity){
			return ((CodeValueCity)entity).getIstat();

		}else{
			return "";
		}
	}

	@Deprecated
	public void setIstat(String istat) {
		if(entity instanceof CodeValueCity){
			((CodeValueCity)entity).setIstat(istat);
		}
	}


	/**
	 * This method is called by CvEditor process. 
	 * If previously another method inject in temporary hashmap a DictionaryManagerRole name, 
	 * the method set in temporary a list of allowed domains related to that role name.
	 * If list is empty, or no role name is found, filter will not be applied.
	 * The allowedDomains temporary variable is used by Ajax servlet to filter node during tree calculation.
	 * @throws PersistenceException 
	 */

	public void applyRestrictions() throws PersistenceException {

		try {
			//dictionaryManagerRole at runtime must be set in conversation, with a backend logic.
			//if dictionaryManagerRole is not found, no restriction are applied.
			//For example: EmployeeRoleXXXAction, based on role of logged user, apply a role of dictionary manger.
			String dictionaryManagerRole = (String) getTemporary().get("dictionaryManagerRole");
			if (dictionaryManagerRole == null || dictionaryManagerRole.isEmpty())
				return; 

			//SUPER USER:    is_super_user = true           can read/write everything, not filter applied.
			//OBSERVER USER: is_observer_user = true        can read/write only what is allowed to see
			//NORMAL USER:   both property above are false. can read only what is allowed to see
			GenericAdapterLocalInterface ga = GenericAdapter.instance();
			Query qry = ga.createNativeQuery("select is_super_user, is_observer_user from code_users where user_namedb = '"+dictionaryManagerRole+"'");
			List<Object[]> res = qry.getResultList();

			if (res == null || res.isEmpty()){
				return;
			}

			BigDecimal is_super_user= (BigDecimal)(res.get(0))[0];
			BigDecimal is_observer_user= (BigDecimal)(res.get(0))[1];

			if (is_super_user.toString().equals("1")) {
				//do not apply any restriction.
				return; 
			}
			if (is_observer_user.toString().equals("1")) {
				getTemporary().put("readOnly", true);
			}

			//list of allowedDomains calculated only for normal user and observer  
			qry = ga.createNativeQuery("select code_value_id from code_value_users join code_users on code_value_users.code_user_id = code_users.id and code_users.user_namedb = '"+dictionaryManagerRole+"'");
			List <String> results = qry.getResultList();

			if (results == null || results.isEmpty()){
				return;
			}

			getTemporary().put("allowedDomains",results);
			
		} catch (Exception e) {
			log.error("Error applyRestrictions", e);
		}

	}


	public void addToCodeValueUsers(String cvId) throws PersistenceException {
		addToCodeValueUsers(cvId,  (String) getTemporary().get("dictionaryManagerRole"));
	}

	public void removeFromCodeValueUsers(String cvId) throws PersistenceException  {
		removeFromCodeValueUsers (cvId,  (String) getTemporary().get("dictionaryManagerRole"));
	}


	/**
	 * Add passed codeValue to current dictionary manager user role.
	 * Must be inserted a row into table Code_Users_Value with relationship between code and user.
	 */
	public void addToCodeValueUsers(String cvId, String dictionaryManagerRole) throws PersistenceException {
		try {
			if (dictionaryManagerRole == null || dictionaryManagerRole.isEmpty())
				return; 
			GenericAdapterLocalInterface ga = GenericAdapter.instance();
			List <CodeUsers> cuList = ga.executeHQL( "select cu from CodeUsers cu where cu.userName = '"+dictionaryManagerRole+"'", null);
			if (cuList == null || cuList.isEmpty())
				return;
			CodeUsers cu = cuList.get(0);

			CodeSystem cs = (CodeSystem) Contexts.getConversationContext().get("CodeSystem");
			CodeValue cv = ga.get(CodeValue.class, cvId);

			CodeUsersValue cuv = new CodeUsersValue(); 
			cuv.setCodeUser(cu);
			cuv.setCodeValueId(cv);
			ga.createObject(cuv);
			//ga.flushSession(); //ga.flushSession() is already invoked inside ga.createObject in a NEW transaction

			//refresh current list of allowed domains
			List<String> allowedDomains = (List<String>)getTemporary().get("allowedDomains");
			allowedDomains.add(cvId);
			getTemporary().put("allowedDomains",allowedDomains);
		} catch (Exception e) {
			log.error("Error addToCodeValueUsers", e);
		}


	}



	public void removeFromCodeValueUsers(String cvId, String dictionaryManagerRole) throws PersistenceException  {
		try {
			if (dictionaryManagerRole == null || dictionaryManagerRole.isEmpty())
				return; 
			GenericAdapterLocalInterface ga = GenericAdapter.instance();
			//    	String q = "delete from code_value_users where code_user_id = (select id from code_users where user_namedb = '"
			//    			+dictionaryManagerRole+"') and code_value_id='"+cvId+"'";
			//    	
			//    	Query qry = ca.createNativeQuery(q);
			//		qry.executeUpdate();
			//    	


			//		List <CodeUsers> cuList = (List<CodeUsers>) ca.executeHQL( "select cu from CodeUsers cu where cu.userName = '"+dictionaryManagerRole+"'");
			//    	if (cuList == null || cuList.isEmpty())
			//    		return;
			//    	CodeUsers cu = cuList.get(0);

			List <CodeUsersValue> cuvList = ga.executeHQL( "select cuv from CodeUsersValue cuv where cuv.codeUser.userName = '"+dictionaryManagerRole+"' and cuv.codeValue.id = '"+cvId+"'", null);
			if (cuvList == null || cuvList.isEmpty())
				return;

			CodeUsersValue cuv = cuvList.get(0);
			if (cuv == null)
				return;

			ga.delete(cuv);
			//ga.flushSession(); //ga.flushSession() is already invoked inside ga.delete in a NEW transaction

			//refresh current list of allowed domains
			List<String> allowedDomains = (List<String>)getTemporary().get("allowedDomains");
			allowedDomains.add(cvId);
			getTemporary().put("allowedDomains",allowedDomains);
		} catch (NamingException e) {
			log.error("Error removeFromCodeValueUsers", e);
		}

	}

	public CodeValue createAndInjectNew(String codeSystemName, String domainDisplayName, String code) throws PhiException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		if (codeSystemName == null || codeSystemName.isEmpty() || domainDisplayName == null || domainDisplayName.isEmpty() || code == null || code.isEmpty()) {
			log.error("Impossible to create new CodeValue without complete paremters ("+codeSystemName+","+domainDisplayName+","+code+")");
			return null;
		}

		CodeSystemAction csa = CodeSystemAction.instance();
		CodeSystem cs = csa.getCodeSystem(codeSystemName);
		Contexts.getConversationContext().set("CodeSystem",cs);

		CodeValue domain = getDomain(codeSystemName, domainDisplayName);
		if (domain == null) {
			log.error("Impossible to create new CodeValue into unexisting domain "+domainDisplayName+ " of codeSystem "+codeSystemName);
			return null;
		}


		getTemporary().put("parentCv", domain.getId());

		for (CodeValue cv : domain.getChildren()) {
			if (cv.getCode().equals(code)){
				log.error("Unable to create new CodeValue with already existing code "+code+" in domain "+domainDisplayName+ " of codeSystem "+ codeSystemName + " [duplicate of "+cv.getId()+"]");
				return null;
			}
		}

		entity=domain;
		suggest();
		CodeValue newCv = newEntity(cs.getCodeValueClass());
		inject(newCv);
		getTemporary().put("type","C");
		setProperties();
		newCv.setCode(code);
		newCv.setDisplayName(code);
		create();
		//ca.flushSession();
		inject(newCv);
		return newCv;

	}


	public CodeValue getDomain (String codeSystemName, String domainName) throws PersistenceException  {

		try {
			if (codeSystemName == null || codeSystemName.isEmpty() || domainName == null || domainName.isEmpty()) {
				return null;
			}
			
			//GenericAdapterLocalInterface ga = GenericAdapter.instance();
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			
			HashMap<String, Object> pars = new HashMap<String, Object>();
			pars.put("domainName", domainName);
			pars.put("codeSystemName", codeSystemName);

			@SuppressWarnings("unchecked")
			List<CodeValue> res = ca.executeHQLwithParameters(getDomainHql, pars);
			if (res == null || res.isEmpty()) {
				log.error("Domain "+domainName+ " not found in CodeSystem "+codeSystemName);
				return null;
			}
			else  {
				return res.get(0);
			}
		} catch (Exception e) {
			log.error("Error getDomain", e);
		}
		return null;

	}

	/**
	 * Dato un albero di CodeValue e un cv, questo metodo ritorna il path che serve a jQuery
	 * per pre-espandere l'albero e mostrare tale cv selezionato
	 * @param cv
	 * @return
	 */
	public String getCodeAndParents(CodeValue cv){
		if(cv==null)
			return "";

		List<String> list = new ArrayList<String>();
		while(cv.getParent()!=null){
			cv = cv.getParent();
			list.add(cv.getId());
		}
		Collections.reverse(list);

		return list.toString().replaceAll("[\\[\\]]", "");
	}
}


