package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.WordUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Locale;

import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.entities.dataTypes.CodeSystem;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("CodeSystemAction")
@Scope(ScopeType.CONVERSATION)
public class CodeSystemAction extends BaseAction<CodeSystem, Long> {

	private static final long serialVersionUID = -6605734160319477005L;

	public static CodeSystemAction instance() {
		return (CodeSystemAction) Component.getInstance(CodeSystemAction.class, ScopeType.CONVERSATION);
	}
	
	public CodeSystem getCodeSystem(String codeSystemName) throws PhiException {
		if (codeSystemName == null || codeSystemName.isEmpty())
			return null;
		equal.put("name", codeSystemName);
		List<CodeSystem> ls = list();
		if (ls==null || ls.isEmpty())
			return null;
		else
			return ls.get(0);
	}
	
	public List<SelectItem> getAllCodeSystemCombo() throws PhiException {
		//TODO: can be applied restrictions based on role of user.
		List<SelectItem> ret = new ArrayList<SelectItem>();
		getOrderBy().put("name", "ascending");
		List<CodeSystem> csList = list();
		getOrderBy().clear();
		if (csList == null || csList.isEmpty())
			return ret;
		
//		injectCs(csList.get(0).getId()); //Inject first to show selected
		
		for (CodeSystem cs : csList) {
			if (cs != null) {
				ret.add(new SelectItem(cs.getId(),cs.getName())); 
			}
		}
		return ret;
		
	}
	
	/**
	 * Different listing strategy (used in SSA project).
	 */
	public List<SelectItem> getAllCodeSystemCombo4SSA() throws PhiException {
		//TODO: can be applied restrictions based on role of user.
		List<SelectItem> ret = new ArrayList<SelectItem>();
		((FilterMap)getEqual()).putOr("name", "PHIDIC", "COMUNI", "COMUNICATASTALITRENTO");
		getOrderBy().put("idx", "ascending");
		List<CodeSystem> csList = list();
		getOrderBy().clear();
		if (csList == null || csList.isEmpty())
			return ret;
		
		for (CodeSystem cs : csList) {
			if (cs != null) {
				ret.add(new SelectItem(cs.getId().toString(),cs.getDescription())); 
			}
		}
		return ret;
		
	}
	
	private String codeValueClass = ""; 
	
	public String getCodeValueClass() {
		return codeValueClass;
	}
	
	public void injectCs (String id) throws PersistenceException {
		
		List res = ca.executeHQL("Select cv from CodeSystem cv where cv.id = '"+id+"'"); 
		if (res != null && !res.isEmpty() && res.get(0) != null) {
			
			entity = (CodeSystem) res.get(0);
			conversationName="CodeSystem";
			codeValueClass = entity.getCodeValueClass();
			Contexts.getConversationContext().set(conversationName, entity); 
		}
		
	}

	public List<Object[]> getTopLevel () throws PersistenceException {
		
		if (entity == null) {
			return null;
		}
		
		String QRY_TOP_ELEMENT_CODESYSTEM = "SELECT cv.id, cv.displayName, cv.description, cv.langIt, cv.type, cv.code FROM CodeValueClass cv " +
				"JOIN cv.codeSystem codSys " +
				"WHERE cv.parent is null " +
				"AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :name AND cssub.status = 1) " +
				"and codSys.name = :name ORDER BY cv.code, cv.displayName";
		
		String codeValueClassHQL= CodeSystemAction.instance().getCodeValueClass();
		if (codeValueClassHQL == null || codeValueClassHQL.isEmpty() || codeValueClassHQL.equals ("CodeValuePhi")) {
			codeValueClassHQL = "CodeValue";
		} 
		
		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("name", entity.getName());
		
		String translatedQuery=QRY_TOP_ELEMENT_CODESYSTEM.replace("CodeValueClass", codeValueClass);
		String currentLang =Locale.instance().getLanguage();
		if (!currentLang.equals("it")) {
			translatedQuery = translatedQuery.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(currentLang));
		}
		
		List<Object[]> idAndTranslation = ca.executeHQLwithParameters(translatedQuery, pars );
		Contexts.getConversationContext().set("listTopLevel",idAndTranslation);
		return idAndTranslation;
		
	}
	
	public List<Object[]> getTopLevel (Date validityDate) throws PersistenceException {
		
		if (entity == null) {
			return null;
		}
		
		String QRY_TOP_ELEMENT_CODESYSTEM = "SELECT cv.id, cv.displayName, cv.description, cv.langIt, cv.type, cv.code FROM CodeValueClass cv " +
				"JOIN cv.codeSystem codSys " +
				"WHERE cv.parent is null " +
				"AND codSys.version = (SELECT MAX(cssub.version) FROM CodeSystem as cssub WHERE cssub.name = :name AND cssub.status = 1) " +
				"AND (cv.validFrom is null OR cv.validFrom <= :date) AND (cv.validTo is null OR cv.validTo >= :date) " +
				"and codSys.name = :name ORDER BY cv.code, cv.displayName";
		
		String codeValueClassHQL= CodeSystemAction.instance().getCodeValueClass();
		if (codeValueClassHQL == null || codeValueClassHQL.isEmpty() || codeValueClassHQL.equals ("CodeValuePhi")) {
			codeValueClassHQL = "CodeValue";
		} 
		
		HashMap<String, Object> pars = new HashMap<String, Object>();
		pars.put("name", entity.getName());
		pars.put("date", validityDate);
		
		String translatedQuery=QRY_TOP_ELEMENT_CODESYSTEM.replace("CodeValueClass", codeValueClass);
		String currentLang =Locale.instance().getLanguage();
		if (!currentLang.equals("it")) {
			translatedQuery = translatedQuery.replace("cv.langIt", "cv.lang" + WordUtils.capitalize(currentLang));
		}
		
		List<Object[]> idAndTranslation = ca.executeHQLwithParameters(translatedQuery, pars );
		Contexts.getConversationContext().set("listTopLevel",idAndTranslation);
		return idAndTranslation;
		
	}

	
	/**
	 * Check if the name passed is:
	 *  - a valid code as top level code if type is "C"
	 *  - a valid code as top level domain if type is "S"
	 * 
	 * If one matching code is found, the method throws an exception, otherwise ends successfully.  
	 */
	public void checkConsistency(String name, String type) throws Exception {
	
		Context conv = Contexts.getConversationContext();
		
		List<Object[]>  toplevelentry = (List<Object[]>)conv.get("listTopLevel"); //get result of query QRY_TOP_ELEMENT_CODESYSTEM
		CodeValue elementToSkip = (CodeValue)conv.get("CodeValue"); //current codevalue must not be considered in the check.
		String idToSkip = ""; 
		if (elementToSkip != null) 
			idToSkip = elementToSkip.getId();
		
		if (toplevelentry == null)
			toplevelentry = getTopLevel();
		
		for (Object[] entry : toplevelentry) { 
			if (entry[0].equals(idToSkip))
				continue; 
			if (type.equals("C") ){
				if (name.equalsIgnoreCase((String)entry[5])){
					throw new Exception("Already existing code "+entry[0]+ " with code "+entry[5]+ " at top level");
				}
			} else if (type.equals("T")){
				if (name.equalsIgnoreCase((String)entry[1])) {
					Exception e = new Exception("Already existing top level domain  "+entry[0]+ " with display name "+entry[1]+ " at top level");
					throw e;
				}
				if ( !"-".equals(name) && name.equalsIgnoreCase((String)entry[5])  ) {
					//do not allow two top levels with same code. Not mandatory, but restriction applied for feature implementation.
					Exception e = new Exception("Already existing top level domain  "+entry[0]+ " with code "+entry[5]+ " at top level");
					throw e;
				}
			}
		}
		
		
	}
	
	
}
