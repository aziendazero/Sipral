package com.phi.flex;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.virtual.VirtualFile;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.catalog.adapter.sequence.SequenceManager;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.LockManager;
import com.phi.cs.lock.Locker;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.util.CsConstants;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.UniqueCheck;
import com.phi.entities.actions.ActionInterface;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.events.PhiEvent;
import com.phi.json.HibernateModule;
import com.phi.security.UserBean;

/**
 * @author russian
 *
 * Proxy class for interacting with the catalog server
 * This class must contain only methods independent from the phi solution and every method here except start/stop conversation
 * MUST return a string to identify the name of the invoking flex class, as last parameter if returning a collection.
 * (the flex response-event handler, asynchronous, must switch and behave accordingly)
 */

@Restrict("#{identity.isLoggedIn(false)}")
@Name("flexProxy")
@Path("/flexProxy")
@Scope(ScopeType.EVENT)
public class FlexProxy implements Serializable {
	
	private static final long serialVersionUID = 8207817576087323430L;
	private static final Logger log = Logger.getLogger(FlexProxy.class);
		
	private static final String MODULES_DIR_PATH = ".war/swf/modules";
	private static final String MDASHBOARD_SWF_PATH = ".war/swf/MDashboard.swf";
			
	public static final String ISO8601RegEx = "^(-?(?:[1-9][0-9]*)?[0-9]{4})-(1[0-2]|0[1-9])-(3[0-1]|0[1-9]|[1-2][0-9])T(2[0-3]|[0-1][0-9]):([0-5][0-9]):([0-5][0-9])(\\.[0-9]+)?(Z|[+-](?:2[0-3]|[0-1][0-9]):[0-5][0-9])?$";
	
	//Jackson parser
	protected static final ObjectMapper mapper = new ObjectMapper();

	static {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.registerModule(new HibernateModule());
	}
	
	
		//userData=#{flexProxy.getUserData()}&sdlTree=#{flexProxy.getSelectedSdlTree()}&serverTime=#{flexProxy.getServerTime()}&serverGMT=#{flexProxy.getServerGMT()}&language=#{localeSelector.language}
		//&patientId=#{Patient.internalId}&patientEncounterId=#{PatientEncounter.internalId}
		//&assignedSdlId=#{PatientEncounter.assignedSDL.internalId}
		//&temporarySDLId=#{PatientEncounter.temporarySDL.internalId}
		//&therapyId=#{PatientEncounter.therapy[0].internalId}
		//&conversationId=#{conversation.id}
		//&customer=#{CUSTOMER}
		//&solution=PHI_CI
		//&modulesToOpen=#{modulesToOpen}
		//&lastModifiedTimes=#{cacheManager.getModulesLastModifiedTimes("PHI_CI")}
		//&filterForPrivacy=#{repository.getSeamProperty("filterForPrivacy")}'
	@GET
	@Path("/flashvars")
	@Produces(MediaType.TEXT_PLAIN)
	public Response getFlashVars() {

		try {
			
			 String result = "";
			 try {		
			        		             
				 Map<String, String> lastModifiedTimes = new HashMap<String, String>();
				 
				 String modulesDirPath = RepositoryManager.instance().getEarPath() + "PHI_CI" + MODULES_DIR_PATH;
				 
				 File modulesDir = new File(modulesDirPath);
				 
				 if (!modulesDir.exists()) {
					 log.error("Error checking lastModifiedTimes, folder " + modulesDirPath + " doesen't exist");
					 //return "{}";
				 }
				 
				 for (File swf : modulesDir.listFiles()){
					  if (swf.isFile() && swf.getPath().contains(".swf")) {					  
						  lastModifiedTimes.put(swf.getName().substring(0, swf.getName().length() - 4), String.valueOf(swf.lastModified()));
					  }
				 }	
				 
				 ArrayList<Map<String, String>> proxyArray = new ArrayList<Map<String,String>>();			 
				 proxyArray.add(lastModifiedTimes);
				 
				 result = mapper.writeValueAsString(proxyArray);
			
			 } catch (Exception e) {
				
				 log.error("[cid="+Conversation.instance().getId()+"] Error getting last modified times", e);
				 throw new PhiException("[cid="+Conversation.instance().getId()+"] Error getting last modified times", e, ErrorConstants.APPLICATION_GENERIC_ERR_CODE);
			 }
			 
			 //String lastModifiedTime = "?_=" + mDashboardSwf.lastModified();
			
			StringBuilder sb = new StringBuilder();
			sb.append("userData=");
			sb.append(userData());
			sb.append("&sdlTree=");
			sb.append(selectedSdlTree());
			sb.append("&serverTime=");
			sb.append(getServerTime());
			sb.append("&serverGMT=");
			sb.append(getServerGMT());
			sb.append("&language=");
			sb.append(LocaleSelector.instance().getLanguage());
			sb.append("&patientId=");
			sb.append(""); //FIXME
			sb.append("&patientEncounterId=");
			sb.append(""); //FIXME
			sb.append("&assignedSdlId=");
			sb.append(""); //FIXME
			sb.append("&temporarySDLId=");
			sb.append(""); //FIXME
			sb.append("&therapyId=");
			sb.append(""); //FIXME
			sb.append("&conversationId=");
			sb.append(Conversation.instance().getId());
			sb.append("&customer=");
			sb.append(Contexts.getApplicationContext().get(CsConstants.CUSTOMER));
			sb.append("&solution=");
			sb.append("PHI_CI"); //FIXME
			sb.append("&modulesToOpen=");
			sb.append(Contexts.getConversationContext().get("modulesToOpen")); //FIXME
			sb.append("&lastModifiedTimes=");
			sb.append(result); //FIXME
			sb.append("&filterForPrivacy=");
			sb.append(RepositoryManager.instance().getSeamProperty("filterForPrivacy"));
			
			return Response.ok(sb).build();
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error getFlashVars ", e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error getFlashVars " + e.getMessage()).build();
		}	
	}
	
	/*
	// Query management
	*/
	
	// FIXME: remove: use paged version
	@POST
	@Path("/executeHQL")
	@Produces(MediaType.APPLICATION_JSON)
	public String executeHQL(@FormParam("hql") String hql, @FormParam("callerName") String callerName) throws PhiException {
		
 		String json = null;
		try {
/*			
			long startTime = 0, startMem = 0;
			if (log.isDebugEnabled()) {	
				log.info("[cid="+Conversation.instance().getId()+"] Read " + hql);
				startTime = System.currentTimeMillis();
				startMem = Runtime.getRuntime().freeMemory();
			}
*/			
//			CatalogAdapter rimpdm2Ca = CatalogPersistenceManagerImpl.instance();
			GenericAdapterLocalInterface rimpdm2Ca = GenericAdapter.instance();

			@SuppressWarnings("rawtypes")
			List results = rimpdm2Ca.executeHQL(hql, null);
/*			
			if (log.isDebugEnabled()) {
				long dT = System.currentTimeMillis() - startTime;
				log.info("[cid=" + Conversation.instance().getId() + "] Found " + results.size() + " Query -> dt: " + dT + " ms, dm: " + (startMem - Runtime.getRuntime().freeMemory()) + " B.");
			}
*/					
			json = mapper.writeValueAsString(results);
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error executing: " + hql, e);
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error executing: " + hql, e, ErrorConstants.PERSISTENCE_QUERY_TREE_CREATION_ERR_CODE);
		}
        
		Events.instance().raiseEvent(PhiEvent.QUERY, callerName);
		
		return json;
	}	
		
	// FIXME: remove: use paged version
	@POST
	@Path("/executeHQLwithParameters")
	@Produces(MediaType.APPLICATION_JSON)
	public String executeHQLwithParameters(@FormParam("hql") String hql, @FormParam("paramsKeys") String paramsKeysJSON, @FormParam("paramsValues") String paramsValuesJSON, @FormParam("callerName") String callerName) throws PhiException {
		
		try {
			Object[] paramsKeys = mapper.readValue(paramsKeysJSON, Object[].class);
			Object[] paramsValues = mapper.readValue(paramsValuesJSON, Object[].class);

			int valueIndex = 0;
			for (Object value : paramsValues){
				if (isISO8601(value.toString()))
				{
					paramsValues[valueIndex] = decodeISO8601(value.toString());
				}
				valueIndex++;
			}
/*
			long startTime = 0, startMem = 0;
			if (log.isDebugEnabled()) {	
				log.info("[cid="+Conversation.instance().getId()+"] Read " + hql);
				startTime = System.currentTimeMillis();
				startMem = Runtime.getRuntime().freeMemory();
			}
*/			
			//CatalogAdapter rimpdm2Ca = CatalogPersistenceManagerImpl.instance();
			GenericAdapterLocalInterface rimpdm2Ca = GenericAdapter.instance();
			
			HashMap<String,Object> params = new HashMap<String, Object>();

			for (int z = 0; z < paramsKeys.length; z++) {
				
				if (paramsValues[z] instanceof Object[] && ((Object[])paramsValues[z])[0].equals("CD"))
					//FIXME PHI 2
					paramsValues[z] = rimpdm2Ca.get(CodeValue.class, (String)((Object[])paramsValues[z])[1]); 
				
				if(((String)paramsKeys[z]).equals("internalId")){
					params.put((String)paramsKeys[z], Long.parseLong(paramsValues[z].toString()));
				} else {
					params.put((String)paramsKeys[z], paramsValues[z]);
				}
			}
			@SuppressWarnings("rawtypes")
			List results = rimpdm2Ca.executeHQL(hql, params);
/*	
			if (log.isDebugEnabled()) {
				long dT = System.currentTimeMillis() - startTime;
				log.info("[cid="+Conversation.instance().getId()+"] Found " + results.size() + " Query -> dt: " + dT + " ms, dm: " + (startMem - Runtime.getRuntime().freeMemory()) + " B.");
			}
*/
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, callerName);
			
			return json;
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error executing: " + hql + " parameters: " + paramsKeysJSON + " values: " + paramsValuesJSON, e);
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error executing: " + hql + " parameters: " + paramsKeysJSON + " values: " + paramsValuesJSON, e, ErrorConstants.PERSISTENCE_QUERY_TREE_CREATION_ERR_CODE);
		}
	}
	

	@POST
	@Path("/executePagedHQL")
	@Produces(MediaType.APPLICATION_JSON)
	public String executePagedHQL(@FormParam("hql") String hql, @FormParam("firstResult") int firstResult, @FormParam("maxResult") int maxResult, @FormParam("callerName") String callerName) throws PhiException {
		return executePagedHQLwithParams(hql, null, null, firstResult, maxResult, callerName);
	}
	

	@POST
	@Path("/executePagedHQLwithParams")
	@Produces(MediaType.APPLICATION_JSON)
	public String executePagedHQLwithParams(@FormParam("hql") String hql, @FormParam("paramsKeys") String paramsKeysJSON, @FormParam("paramsValues") String paramsValuesJSON, @FormParam("firstResult") int firstResult, @FormParam("maxResult") int maxResult, @FormParam("callerName") String callerName) throws PhiException
	{
		try {
/*
			long startTime = 0, startMem = 0;
			if (log.isDebugEnabled()) {	
				log.info("[cid="+Conversation.instance().getId()+"] Read " + hql);
				startTime = System.currentTimeMillis();
				startMem = Runtime.getRuntime().freeMemory();
			}
*/
			Object[] paramsKeys = null;
			Object[] paramsValues = null;
			
			if ((paramsKeysJSON != null) && (paramsValuesJSON != null))
			{
				
				paramsKeys = mapper.readValue(paramsKeysJSON, Object[].class);
				paramsValues = mapper.readValue(paramsValuesJSON, Object[].class);
				
				int valueIndex = 0;
				for (Object value : paramsValues){
					if (isISO8601(value.toString()))
					{
						paramsValues[valueIndex] = decodeISO8601(value.toString());
					}
					valueIndex++;
				}
			}
			//CatalogAdapter rimpdm2Ca = CatalogPersistenceManagerImpl.instance();
			GenericAdapterLocalInterface rimpdm2Ca = GenericAdapter.instance();
			
			HashMap<String,Object> params = null;
			
			if (paramsKeys != null && paramsValues != null) {
			
				params = new HashMap<String, Object>();
				
				for (int z = 0; z<paramsKeys.length; z++) {
					
					if (paramsValues[z] instanceof Object[] && ((Object[])paramsValues[z])[0].equals("CD"))
						//FIXME PHI 2
						//paramsValues[z] = rimpdm2Ca.get("com.phi.entities.dataTypes", (String)((Object[])paramsValues[z])[1]);
						log.error("Unsupported parameter " + paramsValues[z]);
					
					if(((String)paramsKeys[z]).equals("internalId")){
						params.put((String)paramsKeys[z], Long.parseLong(paramsValues[z].toString()));
					}else{
						params.put((String)paramsKeys[z], paramsValues[z]);
					}
				}
			}
			@SuppressWarnings("rawtypes")
			List results = rimpdm2Ca.executePagedHQL(hql,params,firstResult, maxResult);
/*
			if (log.isDebugEnabled()) {
				long dT = System.currentTimeMillis() - startTime;
				log.info("[cid="+Conversation.instance().getId()+"] Found " + results.size() + " Query -> dt: " + dT + " ms, dm: " + (startMem - Runtime.getRuntime().freeMemory()) + " B.");
			}
*/
			String json = mapper.writeValueAsString(results);
			
			Events.instance().raiseEvent(PhiEvent.QUERY, callerName);
			
			return json;
			
		} catch (Exception e) {
			log.error("[cid=" + Conversation.instance().getId() + "] Error executing paged: " + hql + " parameters: " + paramsKeysJSON + " values: " + paramsValuesJSON + " firstResult: " + firstResult + " maxResult: " + maxResult, e);
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error executing paged: " + hql + " parameters: " + paramsKeysJSON + " values: " + paramsValuesJSON + " firstResult: " + firstResult + " maxResult: " + maxResult, e, ErrorConstants.PERSISTENCE_QUERY_TREE_CREATION_ERR_CODE);
		}		
	}
	
	
	
	/*
	// Object management
	*/
	
	@POST
	@Path("/injectConvObject")	
	public Response injectConvObject(@FormParam("conversationName") String conversationName, @FormParam("className") String className, @FormParam("internalId") String id) throws PhiException {
		
		CatalogAdapter rimpdm2Ca = CatalogPersistenceManagerImpl.instance();
		Object obj;
		
		if (className.equals("CodeValue")) {			
			obj = rimpdm2Ca.get("com.phi.entities.dataTypes.CodeValue", id);
		} else {		
			className = resolveClassName(className);		
			obj = rimpdm2Ca.get(className, Long.parseLong(id));
		}
		
		/* 
		TODO: uncomment and change or remove injectConvObject link or setValues to PatientEncounter 
		with conversation names != "PatientEncounter" from dashboards
		 
		if ((Patient.class).equals(HibernateProxyHelper.getClassWithoutInitializingProxy(obj))  ) {
			PatientAction.instance().inject(obj);
		}
		else if ((PatientEncounter.class).equals(HibernateProxyHelper.getClassWithoutInitializingProxy(obj))  ) {
			PatientEncounterAction.instance().inject(obj);
		}
		else {
			Context conv = Contexts.getConversationContext();
			conv.set(conversationName, obj);
		}
		*/
			
		Context conv = Contexts.getConversationContext();
		conv.set(conversationName, obj);
			
		return Response.ok().build();		
	}
	

	@POST
	@Path("/injectConvList")	
	public Response injectConvList(@FormParam("conversationNameList") String conversationNameList, @FormParam("conversationName") String conversationName, @FormParam("jxpath") String jxpath) throws PhiException {
		
		Object obj = null;
		try {
			Context conv = Contexts.getConversationContext();
			obj = conv.get(conversationName);
			 
			//Collection list = (Collection)PropertyUtils.getProperty(obj, jxpath);
			//conv.set(conversationNameList, list);
			
			@SuppressWarnings("rawtypes")
			List list = (List)PropertyUtils.getProperty(obj, jxpath);
			
	    	if (list == null || list.isEmpty()) {
	    		conv.set(conversationNameList, null);
	    	}
	    	
	    	@SuppressWarnings({ "rawtypes", "unchecked" })
			IdataModel<?> dm = new PhiDataModel(list, conversationNameList);
	    	conv.set(conversationNameList, dm);
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error injectList " + obj, e);
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error injectList " + obj, e, ErrorConstants.GENERIC_ERR_CODE);
		}
		
		return Response.ok().build();
	}
			
	
	@POST
	@Path("/createConvObject")	
	public Response createConvObject(@FormParam("conversationName") String conversationName, @FormParam("className") String className) throws PhiException {
	
		Object obj = newInstance(className);

		ActionInterface<?> action = (ActionInterface<?>)Component.getInstance(conversationName+"Action");
		if (action != null) {
			action.inject(obj);
		}
		else {
			Context conv = Contexts.getConversationContext();
			conv.set(conversationName, obj);
		}
		
		return Response.ok().build();
	}
	
	
	//Fixme: estendere x supportare liste di codeValue
	@POST
	@Path("/setValue")
	public Response setValue(@FormParam("conversationName") String conversationName, @FormParam("jxpath") String jxpath, @FormParam("value") String valueJSON) throws PhiException {

		try {
			
			Object value = null;						
			if (valueJSON != null) {
				
				value = mapper.readValue(valueJSON, Object.class);
			
			}
			
			return setValue(conversationName, jxpath, value);
			
		} catch (Exception e) {
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error setting value from " + conversationName, e, ErrorConstants.GENERIC_ERR_CODE);
		}
	}
			
			
	private Response setValue(String conversationName, String jxpath, Object value) throws PhiException {
		
		long t = System.currentTimeMillis();
		Object obj = null;
		
		try {
			
			if (value != null && isISO8601(value.toString()))
			{
				value = decodeISO8601(value.toString());
			}	
			
			Context conv = Contexts.getConversationContext();
			obj = conv.get(conversationName);
			if (obj == null) {
				
				// FIXME: if conversationName is not == className??
				createConvObject(conversationName, conversationName);
				obj = conv.get(conversationName);
			}
			
			PropertyDescriptor pd = null;
			String currentAttributeName = jxpath;			
			Object currentEntity = obj;
			
			// If attribute contains '.', split and create intermediate objects
			if (jxpath.contains(".")) {
				String[] elParts = jxpath.split("\\."); 
		        Object nextEntity = null;
		        for (int i = 0; i < elParts.length - 1; i++) {
		        	pd = PropertyUtils.getPropertyDescriptor(obj, elParts[i]);
		        	Method writeMethod = pd.getWriteMethod();
		        	Type[] types = writeMethod.getGenericParameterTypes();
		     	    if (types == null) {
		     	    	throw new PhiException(conversationName + "." + jxpath + " unable to determinate attribute type", ErrorConstants.PERSISTENCE_GET_RIM_PROPERTY_TYPE_ERR_CODE);
		     	    }
		     	    @SuppressWarnings("rawtypes")
					Class newClass = null;
		     	    if (types[0] instanceof ParameterizedType) {
		     	    	ParameterizedType pt = (ParameterizedType)types[0];
		     	    	newClass = (Class)pt.getRawType();
		     	    } 
		     	    else if (types[0] instanceof Class) {
		     	    	newClass = (Class)types[0];
		     	    }		     	     
		     	     
		     	    //Take current intermediate object
	     	    	nextEntity = pd.getReadMethod().invoke(currentEntity);
	     	    	if (nextEntity == null) {
	     	    		//If doesn't exist: create intermediate object and link to parent
	     	    		nextEntity = newClass.newInstance();
	     	    		writeMethod.invoke(currentEntity, nextEntity);
	     	    	}		     	     
		     	     
				    currentEntity = nextEntity;				    
				    currentAttributeName = elParts[i + 1];
		        }
			}
			
			pd = PropertyUtils.getPropertyDescriptor(currentEntity, currentAttributeName);
			
			// CodeValue
			if (CodeValue.class.isAssignableFrom(pd.getPropertyType()) && value instanceof String) {
				String valueString = (String)value;
				if (valueString.contains(":")) {
					String[] codeSystemNameDomainNameCode = valueString.split(":");
					// FIXME: only codeSystemName:domainName:code or oid allowed: why also className:id?
					// className:id
					if (codeSystemNameDomainNameCode.length == 2) { 
						value = CatalogPersistenceManagerImpl.instance().load(codeSystemNameDomainNameCode[0], codeSystemNameDomainNameCode[1]);
					// codeSystemName:domainName:code
					} else if (codeSystemNameDomainNameCode.length == 3) { //codeSystemName:domainName:code
						value = VocabulariesImpl.instance().getCodeValueCsDomainCode(codeSystemNameDomainNameCode[0], codeSystemNameDomainNameCode[1], codeSystemNameDomainNameCode[2]);
					} else {
						throw new Exception("Code value must be codeSystemName:domainName:code or oid");
					}
					
				} else {
					value = VocabulariesImpl.instance().getCodeValueOid(valueString);
				}
			}
			
			// PQ
			if (pd.getPropertyType().isAssignableFrom(Double.class) && value instanceof Integer) {
				value = Double.parseDouble(value.toString());
			}
			
			PropertyUtils.setProperty(currentEntity, currentAttributeName, value);
			
			String stringValue = "null";
			
			if (value != null)
				stringValue = value.toString();
			
			log.info("[cid=" + Conversation.instance().getId() + "] " + " Setted " + stringValue + " to " + jxpath + " of " + conversationName + " in "+ (System.currentTimeMillis() - t) + " ms");
			
		}
		catch (Exception e) {
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error setting value from " + obj, e, ErrorConstants.GENERIC_ERR_CODE);
		}
		
		return Response.ok().build();
	}
	
	
	@POST
	@Path("/setValues")
	public Response setValues(@FormParam("conversationName") String conversationName, @FormParam("jxpathAndvalue") String jxpathAndvalueJSON) throws PhiException {
		try {
			@SuppressWarnings("unchecked")
			Map <String, Object> jxpathAndvalue = mapper.readValue(jxpathAndvalueJSON, HashMap.class);
			
			for (String jxpath : jxpathAndvalue.keySet()) {

				setValue(conversationName, jxpath, jxpathAndvalue.get(jxpath));

			}
		} catch (Exception e) {
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error setting values from " + conversationName, e, ErrorConstants.GENERIC_ERR_CODE);
		}
		
		return Response.ok().build();
	}		
	
	@POST
	@Path("/link")
	public Response link(@FormParam("conversationName") String conversationName, @FormParam("jxpath") String jxpath, @FormParam("conversationLinkName") String conversationLinkName) throws PhiException {
	
		long t = System.currentTimeMillis();
		Object obj = null;
		Object obj2link = null;
		String initialAttributeName = jxpath;
		
		try {
			Context conv = Contexts.getConversationContext();

			obj = conv.get(conversationName);

			if (obj == null) {
				// FIXME: if conversationName is not == className??
				createConvObject(conversationName, conversationName);
				obj = conv.get(conversationName);
			}

			obj2link = conv.get(conversationLinkName);

			if (obj2link == null) {
				throw new Exception("Object to link: " + conversationLinkName + " not in conversation");
			}

			if (PropertyUtils.getPropertyType(obj, jxpath).getName().equals("java.util.List")) 	{
				// Use addXXX method if present				
				char[] stringArray = jxpath.toCharArray();
				stringArray[0] = Character.toUpperCase(stringArray[0]);
				jxpath = new String(stringArray);

				Method addMethod = MethodUtils.getAccessibleMethod(obj.getClass(), "add" + jxpath, obj2link.getClass());

				if (addMethod != null) {
					addMethod.invoke(obj, obj2link);
					log.info("[cid="+Conversation.instance().getId()+"] " + " Linked " + obj2link + " to "+ initialAttributeName + " of "+ obj + " in "+ (System.currentTimeMillis() -t) +" ms");
					return Response.ok().build();
				}

				//If entity doesen't have an addXXX method use Collection.add method: inverse relation won't be populated
				log.warn("Method add" + jxpath + " of " + obj.getClass() + " not found! Linking with Collection.add(value)");

				@SuppressWarnings("rawtypes")
				Collection collection = (Collection) PropertyUtils.getProperty(obj, initialAttributeName);

				if (collection == null) {
					log.warn("Empty collection!");
					collection = new ArrayList();
				}

				collection.add(obj2link);

				PropertyUtils.setProperty(obj, initialAttributeName, collection);
				
			} else {
				
				PropertyUtils.setProperty(obj, jxpath, obj2link);
				
			}
			log.info("[cid=" + Conversation.instance().getId() + "] Linked " + obj2link + " to " + initialAttributeName + " of " + obj + " in "+ (System.currentTimeMillis() - t) + " ms");
			
		} catch (Exception e) {
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error linking object to " + obj, e, ErrorConstants.APPLICATION_OBJECT_LINK_ERR_CODE);
		}
		return Response.ok().build();
	}
	
	
	@POST
	@Path("/persist")
	@Produces(MediaType.TEXT_PLAIN)
	public Long persist(@FormParam("conversationName") String conversationName) throws PhiException {
		
//		long t = System.currentTimeMillis();
		Object obj = null;
		try {
			
			Context conv = Contexts.getConversationContext();
			
			obj = conv.get(conversationName);
			
			if (obj == null) {
				throw new PhiException("[cid="+Conversation.instance().getId()+"] Unable to persist null obj ", ErrorConstants.GENERIC_ERR_CODE);
			}

			CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter();
			
			ca.create(obj);
			ca.flushSession();
			
			Events.instance().raiseEvent(PhiEvent.CREATE, obj);
			
			return (Long)ca.getIdentifier(obj);
			
		} catch (Exception e) {
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error persisting object " + obj, e, ErrorConstants.GENERIC_ERR_CODE);
		}
	}
	
	
	
	/*
	// Utilities
	*/
	
	//FIXME: da togliere, cambiare chiamate a report da dashboard (AMB_POC e AMB_AmbulatoryCalendar)
	@POST
	@Path("/storeSessionVar")
	public void storeSessionVar(@FormParam("varName") String varName, @FormParam("varValue") String varValue) throws PhiException {
			
		UserBean ub = UserBean.instance();
		ub.setFlexSessionVar(varName, varValue);

	}
	
	
	@POST
	@Path("/retrieveSessionVar")
	@Produces(MediaType.APPLICATION_JSON)
	public String retrieveSessionVar(@FormParam("varName") String varName) throws PhiException{
		try {
			
			UserBean ub = UserBean.instance();
					
			String json = mapper.writeValueAsString(ub.getFlexSessionVar(varName));
			
			return json;
			
		} catch (Exception e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error retrieving session var: " + varName, e);
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error retrieving session var: " + varName, e, ErrorConstants.GENERIC_ERR_CODE);
		}	
	}
		
	
	/*
	// Other functions
	*/
	
	public static Boolean isISO8601(String src){
		return src.matches(ISO8601RegEx);
	}
	
	public static Date decodeISO8601(String src) throws ParseException {
		/*JAVA 7:
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		df.setTimeZone(tz);
		Date date = df.parse(src);		
		Date date = DatatypeConverter.parseDateTime(src).getTime();
		*/
		
		// Joda Time
    	DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    	Date date = fmt.parseDateTime(src).toDate();
		
		return date;		
	}
	
	private String resolveClassName(String className) {
		if (BaseEntity.getDerivedClasses().containsKey(className)) {	     
			return BaseEntity.getDerivedClasses().get(className).getName();
		} else {
			return "com.phi.entities." + className;
		}
	}
	
	private Object newInstance(String className) throws PhiException {
		
		Object obj;
		
	    try {
			if (BaseEntity.getDerivedClasses().containsKey(className)) {
			     
				Class<BaseEntity> clazz = BaseEntity.getDerivedClasses().get(className);
			    obj = clazz.newInstance();
			     
			} else {
				
				obj = Class.forName("com.phi.entities." + className).newInstance();
			}
		} catch (Exception e) {
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error istantiating new class " + className, e, ErrorConstants.APPLICATION_CREATION_OBJECT_ERR_CODE);
		}
	    
	    return obj;
	}
	
	@Deprecated
	public String getUserData() throws PhiException {
		String result = userData();
        //Replace needed because this json is written into home html swf flashvars
        result = result.toString().replace("\"", "&quot;").replace("'", "&#39;").replace("<", "&lt;").replace(">", "&gt;");
        return result;
	}
		
	private String userData() throws PhiException {
		
		String result = "";

		try {
		
	        UserBean ub = UserBean.instance();
	             
	        Object[] row = new Object[10];
	        
	        row[0] = ub.getCurrentSystemUser().getInternalId();
	        row[1] = ub.getCurrentEmployee().getInternalId();
	        row[2] = ub.getUsername();
	        row[3] = ub.getNameFam();
	        row[4] = ub.getNameGiv();
	        row[5] = ub.getRole();
	        row[6] = ub.getRoleName();
	        row[7] = ub.getCurrentSystemUser().getCode().getId();
	        row[8] = ub.getIsCoordinator();
	        row[9] = ub.getSpecialization();
	        List<Object[]> userData = new ArrayList<Object[]>();
	        userData.add(row);
	        
	        result = mapper.writeValueAsString(userData);

		} catch (Exception e) {
			
			log.error("[cid=" + Conversation.instance().getId() + "] Error getting user details", e);
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error getting user details", e, ErrorConstants.APPLICATION_GENERIC_ERR_CODE);
		}

		return result;
	}
		
	@Deprecated
	public String getSelectedSdlTree() throws PhiException {
		String jsonTree = selectedSdlTree();
		return jsonTree.replace("\"", "&quot;").replace("'", "&#39;").replace("<", "&lt;").replace(">", "&gt;");
	}
	
	private String selectedSdlTree() throws PhiException {
	
		try {
			String jsonTree = "";
			
			UserBean ub = UserBean.instance();			
			List<Long> sdLocs = ub.getSdLocs();
		
		List<String> favoriteSdl =  new ArrayList<String>();
		if (ub.getCurrentSystemUser() != null && ub.getCurrentSystemUser().getFavoriteSdl() != null && !ub.getCurrentSystemUser().getFavoriteSdl().isEmpty()) {
			favoriteSdl =  Arrays.asList(ub.getCurrentSystemUser().getFavoriteSdl().split(","));
		}
			
			if (sdLocs != null && !sdLocs.isEmpty()) { 
				
				jsonTree = "{\"children\":[";
			
				String sdlocIds = sdLocs.get(0).toString();
				
				for (int sdl = 1; sdl<sdLocs.size(); sdl++) { 
					sdlocIds += "," + sdLocs.get(sdl);
				}

				String nsQueryLight = 	"select count(n1.internalId) as nestedSetLevel, n1.internalId, n1.name.giv, n1.code.code, n1.waitingListSupported, n1.g2Strt0, n1.hybridManagementSupported " +
										"from ServiceDeliveryLocation n1, ServiceDeliveryLocation n2 " +
										"where n1.nodeInfo.nsThread = n2.nodeInfo.nsThread " +
										"and n1.nodeInfo.nsLeft between n2.nodeInfo.nsLeft and n2.nodeInfo.nsRight " +
										"and n1.internalId IN (" + sdlocIds + ") " +
										"and n1.isActive = 1 " +
										"group by " +
										"n1.nodeInfo.nsLeft, " +
										"n1.nodeInfo.nsRight, " +
										"n1.nodeInfo.nsThread, " +
										"n1.internalId, " +
										"n1.name.giv, " +
										"n1.code.code, " +
										"n1.waitingListSupported, " +
										"n1.g2Strt0, " +
										"n1.hybridManagementSupported " +
										"order by n1.nodeInfo.nsLeft asc)";
	
//				CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
				GenericAdapterLocalInterface ga = GenericAdapter.instance();
				@SuppressWarnings("unchecked")
				List<Object[]> rawNestedSet = ga.executeHQL(nsQueryLight, null); 

				Long prevDepth = 0l;
				 
				if (!rawNestedSet.isEmpty()) {
					
					Long realDepth = 0l;
					HashMap<Long, Long>realDepths = new HashMap<Long, Long>();
				
					for (int z = 0; z < rawNestedSet.size(); z++) {
					Object[] depthIdName = rawNestedSet.get(z);
						Long currDepth = (Long)depthIdName[0];
						Long currId = (Long)depthIdName[1];
						String currName = (String)depthIdName[2];
						String currCode = (String)depthIdName[3];
						Boolean wl = (Boolean)depthIdName[4];
						String g2Strt0 = (String)depthIdName[5];
						Boolean hybridManagementSupported = (Boolean)depthIdName[6];
						
						if (currDepth.equals(prevDepth)) {
							jsonTree += "]},";
						} else if (currDepth > prevDepth) {
							realDepth++;
						} else if (currDepth < prevDepth) {
							Long previousRealDepth = 0l;
							//Go backward to find node with depth smaller or equal to current
							for (int x = z-1; x>=0; x--) {
							Long previousDbDepth =(Long)rawNestedSet.get(x)[0];
								if (previousDbDepth.equals(currDepth)) {
									previousRealDepth = realDepths.get(previousDbDepth);
									break;
								} else if (previousDbDepth < currDepth) {
									previousRealDepth = realDepths.get(previousDbDepth) + 1;
									break;
								}
							}
							//Calculate real difference between depths
							Long realDifference = realDepth-previousRealDepth;
							for (int toClose = 0; toClose <= realDifference; toClose++) {
								jsonTree += "]}";
							}
							jsonTree += ",";
							realDepth= realDepth - realDifference;
						}
			
						realDepths.put(currDepth, realDepth);
						
						jsonTree += "{\"name\":\"" + currName + "\",\"id\":" + currId + ",\"code\":\"" + currCode + "\",\"wl\":" + wl + ",\"g2Strt0\":";
						if (g2Strt0==null) {		
							jsonTree += "null";
						} else {
							jsonTree += "\"" + g2Strt0 + "\"";
						}
						
					if (favoriteSdl.contains(Long.toString(currId))) {
						jsonTree += ",\"isSelected\":true";
					}

						jsonTree += ",\"hybridManagementSupported\":" + hybridManagementSupported + ",\"children\":[" ;

						prevDepth = currDepth;
					}
					
					for (Long toClose = 1l; toClose <= realDepth; toClose++) {
						jsonTree += "]}";
					}
				}
				
				jsonTree += "]}";
			
			}
			return jsonTree;
		} catch (NamingException e) {
			log.error("[cid="+Conversation.instance().getId()+"] Error getSelectedSdlTree", e);
			throw new PhiException("[cid="+Conversation.instance().getId()+"] Error getSelectedSdlTree", e, ErrorConstants.PERSISTENCE_QUERY_TREE_CREATION_ERR_CODE);
		}
		
	}
	
	public Long getServerTime() {
	     Calendar cal = Calendar.getInstance();
	     return cal.getTime().getTime();
	 }
	 
	 public int getServerGMT() {
	     Calendar cal = Calendar.getInstance(); 
	     return cal.getTimeZone().getOffset(cal.getTime().getTime());
	 }
	 
	 public String getMDashboardLastModifiedTime(String solutionName) {
		 String lastModifiedTime = "";
		 try {

			 VirtualFile mDashboardSwf = RepositoryManager.instance().getEar().getChild(solutionName + MDASHBOARD_SWF_PATH);
			 
			 lastModifiedTime = "?_=" + mDashboardSwf.getLastModified();

		} catch (IOException e) {
			 log.error("[cid="+Conversation.instance().getId()+"] Error getting MDashboard last modified time", e);
		}
		 return lastModifiedTime;
	 }
	 
	 public String getModulesLastModifiedTimes(String solutionName) throws PhiException {
		 
		 String result = "";
		 try {		
		        		             
			 Map<String, String> lastModifiedTimes = new HashMap<String, String>();
			 
			 VirtualFile modules = RepositoryManager.instance().getEar().getChild(solutionName + MODULES_DIR_PATH);
			 
			 if (!modules.exists()) {
				 log.error("[cid="+Conversation.instance().getId()+"] Error getting last modified times, folder doesen't exist");
				 return "[]";
			 }
			 for (VirtualFile swf : modules.getChildren()) {
				 if (swf.isLeaf() && swf.getName().contains(".swf")) {					  
					  lastModifiedTimes.put(swf.getName().substring(0, swf.getName().length() - 4), String.valueOf(swf.getLastModified()));
				  }
			 }	
			 
			 ArrayList<Map<String, String>> proxyArray = new ArrayList<Map<String,String>>();			 
			 proxyArray.add(lastModifiedTimes);
			 
			 result = mapper.writeValueAsString(proxyArray);
		
		 } catch (Exception e) {
			 log.error("[cid="+Conversation.instance().getId()+"] Error getting last modified times", e);
		 }

		 return result.replace("\"", "&quot;").replace("'", "&#39;").replace("<", "&lt;").replace(">", "&gt;");
	 }		
		
	 
	/*
	// Lock functions
	*/
	 

	/**
	* Set lock on a BaseEntity. Return "true" if lock is set, "false" if is already locked by another user
	* @param rootName
	* @param internalId
	* @throws Exception, NamingException
	*/
	public String lockRimObj(String rootName, String internalId) throws Exception, NamingException {
		UserBean userBean = (UserBean) Component.getInstance("userBean");
		LockManager delegate = Locker.instance();
		UniqueCheck result = null;
        try {
        	result = delegate.lock(rootName, internalId, userBean.getUsername(), true);
		} catch (RuntimeException e) {
			log.error("Problem while locking "+ rootName + " object");
		}
		if (result==null || !result.getUserName().equals(userBean.getUsername())){
			log.info((result!=null?"user "+result.getUserName():"")+" currently locks on " +rootName + " " + internalId);

			return "Locked by " + result.getUserName();
		}
		else{
			return "now locked";
		}
	}
	
	/**
	 * Check if a BaseEntity is locked.
	 * @param rootName
	 * @param internalId 
	 * @throws NamingException
	 */
	public String isRimObjLocked(String rootName, String internalId) throws NamingException {
		LockManager delegate = Locker.instance();
		//return delegate.isLocked(rootName, internalId); 
		return delegate.isLockedBy(rootName, internalId);
	}
	
	
	/**
	 * Unlock BaseEntity
	 * @param rootName
	 * @param internalId
	 * @throws Exception, NamingException
	 */
	public void unlockRimObj(String rootName, String internalId) throws Exception, NamingException {		
		LockManager delegate = Locker.instance();
		UserBean userBean = (UserBean) Component.getInstance("userBean");
		delegate.unlock(rootName, internalId, userBean.getUsername());
	}	
	
	/**
	 * Return next value of a sequence called name.
	 * @param sequenceName
	 */
	public String nextOfSequence(String sequenceName) {
		SequenceManager sqm = ((SequenceManager)Component.getInstance("SequenceManager"));
		sqm.setName(sequenceName);
		return sqm.nextOf(sequenceName);
	}
	
	/**
	 * Unlock Sequence
	 * @param sequenceName
	 * @throws NamingException
	 */
	
	public void unlockSequence(String sequenceName) throws NamingException {		
		LockManager delegate = Locker.instance();
		UserBean userBean = (UserBean) Component.getInstance("userBean");
		delegate.unlock(sequenceName, userBean.getUsername());
	}
	
}