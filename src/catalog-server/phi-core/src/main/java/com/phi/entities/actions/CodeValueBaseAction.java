package com.phi.entities.actions;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxyHelper;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.view.bean.InputSuggestionBean;
import com.phi.cs.view.bean.hashes.SuggestionHash;
import com.phi.entities.dataTypes.CodeSystem;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.events.PhiEvent;
import com.phi.ps.ProcessManagerImpl;
import com.phi.security.UserBean;


@SuppressWarnings("serial")
public abstract class CodeValueBaseAction <T extends CodeValue> implements ActionInterface<T> {

	//private static final long serialVersionUID = -301215865722L;
	private static final Logger log = Logger.getLogger(CodeValueBaseAction.class);

	//======================== DERIVED FROM BaseAction =================================== //

	private static final String historyListSuffix = "HistoryList";
	private static final String listSuffix = "List";
	protected HashMap<String, Object> temporary;
	protected T entity;

	protected Map<Object, InputSuggestionBean> autocomplete;
	
	protected String conversationName = null;
//	protected CatalogAdapter ca;
	protected Class<T> entityClass;

	//Multiselect
	protected Map<T, Boolean> selectedEntities;	


	public CodeValueBaseAction ()  {

		Type superClasss = getClass().getGenericSuperclass();
		ParameterizedType genericSuperclass = null;

		if (superClasss instanceof ParameterizedType) {
			genericSuperclass = (ParameterizedType)superClasss;
		} else if (superClasss instanceof Class) {
			genericSuperclass = (ParameterizedType)((Class)superClasss).getGenericSuperclass();
		}

		entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
		conversationName = entityClass.getSimpleName();
//		ca = CatalogPersistenceManagerImpl.instance();
		
		autocomplete = new SuggestionHash();


	}
	
	public boolean containsCode (Collection<T> codeValueList, String... codes) {
		
		if (codeValueList == null || codeValueList.isEmpty() || codes == null || codes.length == 0)
			return false;

		for (T cv : codeValueList) {
			String code = cv.getCode();
			if (cv != null && code != null) {
				for (String c : codes) {
					if (cv.getCode().equals(c))
						return true;
				}
			}
		}
		
		return false;
	
	}

	public Map<Object, InputSuggestionBean> getAutocomplete() {
		return autocomplete;
	}

	public void setAutocomplete(Map<Object, InputSuggestionBean> autocomplete) {
		this.autocomplete = autocomplete;
	}

	private String injectId = null;

	public void setId(String id) {
		this.injectId = id;
	}
	

	private String injectEntity = null;

	public void setInjectEntity(String injectEntity) {
		this.injectEntity = injectEntity;
	}
	
	private String injectIntoName = null;

	public void setInjectIntoName(String injectIntoName) {
		this.injectIntoName = injectIntoName;
	}

	public void injectbyId() throws PhiException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		String intoName = conversationName;
		if(injectIntoName != null && !injectIntoName.isEmpty()) { 
			intoName = injectIntoName;
		}		
		if(injectEntity != null && !injectIntoName.isEmpty()) { 
			inject((T)ca.get("com.phi.entities.dataTypes." + injectEntity, injectId), intoName);
		} else {	
			inject(ca.get(entityClass, injectId), intoName);
		}

	}

	public void inject(T cv, String conversationName) {

		if ( conversationName == null || conversationName.isEmpty() )
			return;

		inject(cv);
	}

	public void inject(Object cvOrMap) {
		Context conversation = Contexts.getConversationContext();

		if (cvOrMap instanceof CodeValue) {
			entity = (T)cvOrMap;
		} else if (cvOrMap instanceof Map) {
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			entity = ca.load(entityClass, (String)((Map)cvOrMap).get("id"));
			//Remove map from event context, to avoid finding of map instead of entity
			Contexts.getEventContext().remove(conversationName);
		} 

		conversation.set(conversationName, entity);
	}

	public void eject() {
		Contexts.getConversationContext().remove(conversationName);
		entity = null;
	}

	public HashMap<String, Object> getTemporary() {
		if (temporary == null)
			temporary = new HashMap<String, Object>();
		return temporary;
	}

	public void setTemporary(HashMap<String, Object> temporary) {
		this.temporary = temporary;
	}


	public void injectAndProceed(Object cvOrMap, String mnemonicName) throws PhiException {

		inject(cvOrMap);

		if ( mnemonicName != null && !mnemonicName.isEmpty() ){
			ProcessManagerImpl.instance().manageTask(mnemonicName);
		}
	}

	public void refresh() throws PhiException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		if (entity == null)
			getEntity();
		if (entity != null) 
			ca.refreshIfNeeded(entity); 
	}

	public void refresh (T e) {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		ca.refreshIfNeeded(e);
	}

	public T getEntity() {

		T entityFromConversation = (T)Contexts.getConversationContext().get(conversationName);

		if (entity == null) {
			entity = entityFromConversation;
		} else if (entity != entityFromConversation && entityFromConversation != null) {
			entity = entityFromConversation;
		}

		return entity;
	}

	public void create() throws PhiException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		ca.create(entity);

		Object id = ca.getIdentifier(entity);
		log.info("[cid="+Conversation.instance().getId()+"] Created object of Class "+
				entityClass.getName()+" with internal_id: "+((id != null) ? id.toString() : "N.A.") );
	}

	public void create(T object) throws PhiException {
		entity = object;
		create();
	}

	public void delete () throws PhiException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		Object id = ca.getIdentifier(entity);

		ca.delete(entity);

		log.info("[cid="+Conversation.instance().getId()+"] Deleted object of Class "+
				entityClass.getName()+" with internal_id: "+((id != null) ? id.toString() : "N.A.") );

		eject();
	}

	public T read(String id) throws PhiException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		entity =  ca.get(entityClass, id);
		return entity;
	}

	public T get(Class<T> clazz, String id) throws PhiException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		T ent =  ca.get(entityClass, id);
		return ent;
	}
	
	public List<T> get(List<String> ids) throws PhiException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		List<T> cvs = new ArrayList<T>();
		if(ids != null && !ids.isEmpty()){
			for(String id : ids){ 
				T cv = ca.get(entityClass, id);
				cvs.add(cv);
			}

		}
		return cvs;
	}
	
	@ShowInDesigner(description="Eject current class object list")
	public void ejectList() {
		ejectList( conversationName + listSuffix);
	}
	
	@ShowInDesigner(description="Ejects a list from conversation")
	public void ejectList(String name) {
		Contexts.getConversationContext().remove(name);
	}

	public List injectList (List list) {
		return injectList(list, conversationName + listSuffix);
	}	
	public List injectList (List list, String name) {
		
		Context conversation = Contexts.getConversationContext();


		if (list == null) {
			injectEmptyList(name);
			return ((IdataModel)conversation.get(name)).getList();
		}

		IdataModel<?> dm = new PhiDataModel(list, conversationName);

		conversation.set(name, dm);
		
		if (!list.isEmpty()) {
			Events.instance().raiseEvent(PhiEvent.READ, HibernateProxyHelper.getClassWithoutInitializingProxy(list.get(0)).getSimpleName());
		}
		
		return list;
			
	}
	
	public void injectEmptyList(String name) {
		IdataModel<?> dm = new PhiDataModel(new ArrayList<T>(), conversationName);
		Contexts.getConversationContext().set(name, dm);
	}
	
	
	public T newEntity() throws InstantiationException, IllegalAccessException {
		return entityClass.newInstance();
	}

	public CodeValue newEntity(String codeValueClass) throws  ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class cvClass = Class.forName("com.phi.entities.dataTypes." + codeValueClass);
		return(CodeValue) cvClass.newInstance();
	}

	//=================================================================================== //

	public void flushSession() throws PersistenceException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		ca.flushSession();
	}

	//default ordering for history is ascending: from older to newer
	//set historyOrderDescending to true if you want obtain the reversed order.
	private boolean historyOrderDescending =false;


	public boolean getHistoryOrderDescending() {
		return historyOrderDescending;
	}

	public void setHistoryOrderDescending(boolean historyOrderDescending) {
		this.historyOrderDescending = historyOrderDescending;
	}

	/**
	 * Put in conversation a list containing all revisions of entity.
	 * The conversation name will be: getConversationName() + historySuffix
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@ShowInDesigner(description="Read history of an entity")
	public IdataModel<T> history() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (getEntity() == null) {
			return null;
		}

		return historyFiltered(false,null);
	}


	@ShowInDesigner(description="Read history of an entity, including current revision in the result list")
	public IdataModel<T> historyIncludeCurrent() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (getEntity() == null) {
			return null;
		}

		return historyFiltered(true,null);
	}

	/**
	 * History element returned will contains only steps when a change is detected in one of attributesChanged list.
	 * For example: if you need to get only the revisions when a person name or surname is changed (not other attributes)
	 * you can specify ('name.giv', 'name.fam') as input of this method. 
	 * @param attributesChanged  
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	@ShowInDesigner(description="Read history of an entity, filtering only history step when one of the attribute in a given list is changed.")
	public IdataModel<T> historyFiltered (String...attributesChanged) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (getEntity() == null) {
			return null;
		}
		return historyFiltered(false,attributesChanged);
	}

	@ShowInDesigner(description="Read history of an entity, filtering only history step when one of the attribute in a given list is changed, including current revision in the result list")
	public IdataModel<T> historyFilteredIncludeCurrent (String...attributesChanged) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (getEntity() == null) {
			return null;
		}
		return historyFiltered(true,attributesChanged);
	}

	private IdataModel<T> historyFiltered (boolean includeCurrent, String...attributesChanged) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
		long t = System.currentTimeMillis();
		if (getEntity() == null) {
			return null;
		}

		Serializable id = ca.getIdentifier(entity);
		String entityName = entityClass.getName();
		List<T> history = ca.getHistoryof(entityClass, /*entityName,*/ id, attributesChanged);
		if (includeCurrent) {
//			T current = getEntity();
//			if (current instanceof BaseEntity) {
//				PhiRevisionEntity rev = new PhiRevisionEntity();
//				rev.setCurrent(true);
//				((BaseEntity)current).setRevision(rev);
//			}
			history.add(getEntity());
		}

		if (historyOrderDescending) {
			Collections.reverse(history);
		}

		log.debug("[cid="+Conversation.instance().getId()+"] Readed History"+
				(includeCurrent ? "IncludeCurrent" : "")+" for "+ entityName + " internalId "+ id +
				" with " + history.size()+" results in " +(System.currentTimeMillis()-t) + " ms " +
				(attributesChanged == null || attributesChanged.length == 0 ? "" : attributesChanged));


		IdataModel<T> dm = new PhiDataModel<T>(history, conversationName);

		Contexts.getConversationContext().set(conversationName + historyListSuffix, dm);

		return dm;

	}
	/**
	 * Multiselect: used into checkBox inside tables
	 * For example: <h:selectBooleanCheckbox value="#{PatientAction.selectedEntities[Patient]}"/>
	 * @return
	 */
	public Map<T, Boolean> getSelectedEntities() {
		if (selectedEntities == null) {
			selectedEntities = new HashMap<T, Boolean>();
		}
		return selectedEntities;
	}

	public void setSelectedEntitiesList(List<T> objectList) {

		if (selectedEntities != null)
			selectedEntities.clear();
		else
			selectedEntities = new HashMap<T, Boolean>(); 


		if (objectList == null) 
			return;

		for (T t : objectList) {
			selectedEntities.put(t, true);
		}
	}

	public List<T> getSelectedEntitiesList() {
		List <T> ret = new ArrayList<T>();
		if (selectedEntities != null) {
			for (T t : selectedEntities.keySet()) {
				if (selectedEntities.containsKey(t) && selectedEntities.get(t))   //only selected entities must be returned. deselected changed object return null. 
					ret.add(t);
			}
		}
		return ret;
	}

	/**
	 * By default, just inject leaves
	 */
	public void injectFromTree(){
		try {
			injectbyId();
			if(entity!=null && !"C".equals(entity.getType()))
				eject();
		} catch (PhiException e) {
			log.error("Failed to inject Code from Tree");
		}
	}
}


