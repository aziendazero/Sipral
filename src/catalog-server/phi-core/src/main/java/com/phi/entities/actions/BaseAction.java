package com.phi.entities.actions;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import javax.persistence.Entity;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.CriteriaImpl.CriterionEntry;
import org.hibernate.impl.CriteriaImpl.OrderEntry;
import org.hibernate.impl.CriteriaImpl.Subcriteria;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.transform.ResultTransformer;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.catalog.adapter.resultTransformer.AliasToEntityMapResultTransformer;
import com.phi.cs.catalog.adapter.resultTransformer.SqlAliasToEntityMapResultTransformer;
import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.datamodel.PagedDataModel;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.DictionaryException;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.cs.lock.LockManager;
import com.phi.cs.paging.LazyList;
import com.phi.cs.view.bean.InputSuggestionBean;
import com.phi.cs.view.bean.hashes.SuggestionHash;
import com.phi.cs.vocabulary.Vocabularies;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.PhiRevisionEntity;
import com.phi.entities.auditedEntity.AuditedEntity;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.dataTypes.II;
import com.phi.entities.locatedEntity.LocatedEntity;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.events.PhiEvent;
import com.phi.parameters.ParameterManager;
import com.phi.ps.ProcessManager;
import com.phi.ps.ProcessManagerImpl;
import com.phi.security.UserBean;

/**
 * Base Action class.
 * All entities in PHI runtime are managed by a corresponding Action.
 * Manages read, create, delete, history, filtering, inject and eject, linking and unlinking of entity T.
 * All entities actions must override this. 
 * 
 * @author Zupan
 *
 * @param <T> Type of managed entity
 * @param <ID> Type of id of the entity
 */

public abstract class BaseAction<T, ID extends Serializable> implements ActionInterface<T>  {

	private static final Logger log = Logger.getLogger(BaseAction.class);
	private static final long serialVersionUID = -5618063694339959078L;

	protected String conversationName = null;
	protected static final String listSuffix = "List";
	protected static final String historyListSuffix = "HistoryList";
	
	private static final String USE_OBSCURATION_PARAM = "p.privacy.obscuration.use";

	protected static final String serviceDeliveryLocationId = "serviceDeliveryLocation.internalId";

	protected Class<T> entityClass;
	protected T entity;

	//Multiselect
	protected Map<T, Boolean> selectedEntities;	

	//Autocomplete
	protected Map<Object, InputSuggestionBean> autocomplete;

	//Read by criteria queries
	protected transient Criteria entityCriteria;

	//Selected columns
	private static final String selectRestriction = "select";
	@ShowInDesigner(description="Selected columns")
	protected List<String> select = new SelectList();
	protected transient ProjectionList entityProjections;
	protected transient ResultTransformer resultTranformer;

	//Filtering
	protected final Map<String, String> restrictionMap = new HashMap<String, String>();
	@ShowInDesigner(description="Where equal filter")
	protected HashMap<String, Object> equal = new FilterMap("eq");
	@ShowInDesigner(description="Where greater or equal filter")
	protected HashMap<String, Object> greaterEqual = new FilterMap("ge");
	@ShowInDesigner(description="Where greater filter")
	protected HashMap<String, Object> greater = new FilterMap("gt");
	@ShowInDesigner(description="Where less or equal filter")
	protected HashMap<String, Object> lessEqual = new FilterMap("le");
	@ShowInDesigner(description="Where like filter")
	protected HashMap<String, Object> like = new FilterMap("like");
	@ShowInDesigner(description="Where less filter")
	protected HashMap<String, Object> less = new FilterMap("lt");
	@ShowInDesigner(description="Where not equal filter")
	protected HashMap<String, Object> notEqual = new FilterMap("ne");
	protected static final String neRestriction = "ne";
	@ShowInDesigner(description="Where is/not null filter")
	protected HashMap<String, Object> isNull = new FilterMap("null");
	protected static final String inRestriction = "in";
	protected static final String notInRestriction = "notIn";
	@ShowInDesigner(description="Where in filter")
	protected HashMap<String, Object> in = new FilterMap(inRestriction);
	@ShowInDesigner(description="Where not in filter")
	protected HashMap<String, Object> notIn = new FilterMap(notInRestriction);
	
	private HashMap<String, Object> restrictionBackup = new HashMap<String, Object>();
	
	//Ordering
	protected static final String orderRestriction = "order";
	@ShowInDesigner(description="Order by")
	protected HashMap<String, Object> orderBy = new FilterMap(orderRestriction);
	public static enum OrderBy {ascending, descending, ascendingIgnoreCase, descendingIgnoreCase }

	//Page size
	protected Integer readPageSize = null;

	protected CatalogAdapter ca;
	
	protected PropertyUtilsBean propBean;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public BaseAction() {
		Type superClasss = getClass().getGenericSuperclass();
		ParameterizedType genericSuperclass = null;

		if (superClasss instanceof ParameterizedType) {
			genericSuperclass = (ParameterizedType)superClasss;
		} else if (superClasss instanceof Class) {
			genericSuperclass = (ParameterizedType)((Class)superClasss).getGenericSuperclass();
		}

		entityClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];

		conversationName = entityClass.getSimpleName();

		ca = CatalogPersistenceManagerImpl.instance();
		propBean = BeanUtilsBean.getInstance().getPropertyUtils();
		
		autocomplete = new SuggestionHash();

		restrictionMap.put("eq","=");
		restrictionMap.put("ge",">=");
		restrictionMap.put("gt",">");
		restrictionMap.put("le","<=");
		restrictionMap.put("like"," like ");
		restrictionMap.put("lt","<");
		restrictionMap.put("ne","<>");
		restrictionMap.put("null"," is ");
		restrictionMap.put(inRestriction," in ");
		restrictionMap.put(notInRestriction," not in "); 
	}

	public BaseAction(String conversationName) {
		this();
		this.conversationName = conversationName;
	}

	@ShowInDesigner(description="Create a new entity")
	public T newEntity() throws InstantiationException, IllegalAccessException {
		return entityClass.newInstance();
	}

	@ShowInDesigner(description="Save an object")
	public void create() throws PhiException {

		//Multiselect: 
		boolean multiselected = false;
		if (selectedEntities != null && selectedEntities.size() >0) {
			for (T ent : selectedEntities.keySet()) {
				if (ent != null && selectedEntities.containsKey(ent) && selectedEntities.get(ent)) {

					long t = System.currentTimeMillis();

					ProcessManager pm = ProcessManagerImpl.instance();

					if (pm.isStateless() && ent instanceof BaseEntity) {
						//If process is stateless uses alternative persistence context, avoiding pollution of ca.
						try {
							GenericAdapterLocalInterface ga = GenericAdapter.instance();
							@SuppressWarnings("unchecked")
							T merged = (T)ga.updateObject(ent);

							ca.evict(ent);

							inject(ca.get(merged.getClass(), ((BaseEntity)merged).getInternalId()));
							refresh();//refresh lazy collections (that must have cascade refresh)
						} catch (NamingException e) {
							log.error("Error getting GenericAdapter", e);
						}
						
					} else {
						ca.create(ent);
					}
					
					Events.instance().raiseEvent(PhiEvent.CREATE, ent);

					Object id = ca.getIdentifier(ent);
					log.info("[cid="+Conversation.instance().getId()+"] Created object of Class "+
							HibernateProxyHelper.getClassWithoutInitializingProxy(ent).getName()+" with internal_id: "+((id != null) ? id.toString() : "N.A.")+
							" in: "+(System.currentTimeMillis() -t)+" ms ");

					multiselected = true;
				}
			}
		}

		if (!multiselected && getEntity() != null) {
			long t = System.currentTimeMillis();
			
			ProcessManager pm = ProcessManagerImpl.instance();

			if (pm.isStateless() && entity instanceof BaseEntity ) {
				//If process is stateless uses alternative persistence context, avoiding pollution of ca.
				try {
					GenericAdapterLocalInterface ga = GenericAdapter.instance();
					if (((BaseEntity)entity).getInternalId() > 0){
						@SuppressWarnings("unchecked") 
						T merged = (T)ga.updateObject(entity);
						ca.evict(entity);
						inject(ca.get(merged.getClass(), ((BaseEntity)merged).getInternalId()));
					}
					else {
						Object newEnt = ga.create(entity);
						inject(ca.get(entity.getClass(), ((BaseEntity)newEnt).getInternalId()));
					}
					

					
					refresh();//refresh lazy collections (that must have cascade refresh)
				} catch (NamingException e) {
					log.error("Error getting GenericAdapter", e);
				}
				
			} else {
				ca.create(entity);
			}
			
			Events.instance().raiseEvent(PhiEvent.CREATE, entity);

			Object id = ca.getIdentifier(entity);
			log.info("[cid="+Conversation.instance().getId()+"] Created object of Class "+
					entityClass.getName()+" with internal_id: "+((id != null) ? id.toString() : "N.A.")+
					" in: "+(System.currentTimeMillis() -t)+" ms ");
		}				
	}

	@ShowInDesigner(description="Save an object, used in datagrid to save current row")
	public void create(T entity) throws PhiException {
		if (entity != null) {
			long t = System.currentTimeMillis();

			ca.create(entity);

			Object id = ca.getIdentifier(entity);
			log.info("[cid="+Conversation.instance().getId()+"] Created object of Class "+
					HibernateProxyHelper.getClassWithoutInitializingProxy(entity).getName()+" with internal_id: "+((id != null) ? id.toString() : "N.A.")+
					" in: "+(System.currentTimeMillis() -t)+" ms ");
		}
		
		Events.instance().raiseEvent(PhiEvent.CREATE, entity);
	}

	/**
	 * Executes a select() and put the result in conversation at conversationName
	 * @throws PhiException
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	@ShowInDesigner(description="Reads an object")
	public void read() throws PhiException {

		IdataModel<T> dm;

		List<T> lst = select();

		if (lst instanceof LazyList) {
			dm = new PagedDataModel<T>((LazyList)lst, conversationName);
		} else {
			dm = new PhiDataModel<T>(lst, conversationName);
		}

		Contexts.getConversationContext().set(getConversationName() + listSuffix, dm);
	}

	public T read(ID id) throws PhiException {
		
		entity = ca.get(entityClass, id);
		
		Events.instance().raiseEvent(PhiEvent.READ, entityClass.getSimpleName());
				
		return entity;		
	}

	@SuppressWarnings("unchecked")
	public void read(T example) throws PhiException {

		if (example == null) {
			read();
		}

		List<T> read = ca.read(example);
		PhiDataModel<T> dm = new PhiDataModel<T>(read, conversationName);

		Contexts.getConversationContext().set(getConversationName() + listSuffix, dm);		
	}

	/**
	 * Executes entityCriteria, return a list of T or a list of Map, based on current select, where and order restrictions.
	 * @return
	 * @throws PhiException
	 */
	@SuppressWarnings("rawtypes")
	public List select() throws PhiException {
		long t = System.currentTimeMillis(); 
		if (entityCriteria == null) {
			entityCriteria = ca.createCriteria(entityClass);
			if (useObscurationLvl2() && entityClass != null && "PatientEncounter".equals(entityClass.getSimpleName())) {
				setObscurationLvl2Restrictions(entityCriteria, new String[]{}, -1);
			}
		}
		
		if (!((CriteriaImpl)entityCriteria).iterateExpressionEntries().hasNext() && (readPageSize == null || readPageSize == 0)) {
			log.warn("Not paged read of " + entityClass.getSimpleName()+ " without restrictions !!!!! ");
		}		
		
		List ret;
		if (readPageSize == null || readPageSize == 0) {
			try {
				ret = entityCriteria.list(); 
			}
			catch (Exception e) {
				log.error("[cid="+Conversation.instance().getId()+"] error reading "+entityClass.getName()+": "+e.getMessage());
				throw new PhiException(e.getMessage(), ErrorConstants.APPLICATION_READ_OBJECT_ERR_CODE);
			}
			log.info("[cid="+Conversation.instance().getId()+"] " 
					+ "Read " + entityClass.getSimpleName() + ": " 
					+ (ret == null ? "0" : ret.size()) + " in "
					+ (System.currentTimeMillis() -t)
					+ " ms {entityCriteria: " + entityCriteria + "}");		
		} else {
			ret = new LazyList<T>(entityCriteria, readPageSize, Integer.MAX_VALUE, entityProjections, distinct, entityClass);
		}

		Events.instance().raiseEvent(PhiEvent.READ, entityClass.getSimpleName());
		
		return ret;
	}

	/**
	 * Executes a select(), return a list of T based on current where and order restrictions, ignoring select.
	 * @return
	 * @throws PhiException
	 */
	@SuppressWarnings("unchecked")
	public List<T> list() throws PhiException {

	     if (entityCriteria!=null) {
	      entityCriteria.setProjection(null);
	      resultTranformer = Criteria.ROOT_ENTITY;
	      entityCriteria.setResultTransformer(resultTranformer);
	     }
	     List<T> results = select();

	     if (entityProjections != null && entityCriteria!=null) { 
	      entityCriteria.setProjection(entityProjections);
	     }

	     return results;
	}

	// Default ordering for history is ascending: from older to newer
	// set historyOrderDescending to true if you want obtain the reversed order.
	private boolean historyOrderDescending = false;


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

		IdataModel<T> history =  historyFiltered(false, null);
				
		return history;
	}


	@ShowInDesigner(description="Read history of an entity, including current revision in the result list")
	public IdataModel<T> historyIncludeCurrent() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		if (getEntity() == null) {
			return null;
		}

		return historyFiltered(true, null);
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
		return historyFiltered(false, attributesChanged);
	}

	@ShowInDesigner(description="Read history of an entity, filtering only history step when one of the attribute in a given list is changed, including current revision in the result list")
	public IdataModel<T> historyFilteredIncludeCurrent (String...attributesChanged) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (getEntity() == null) {
			return null;
		}
		return historyFiltered(true, attributesChanged);
	}

	private IdataModel<T> historyFiltered (boolean includeCurrent, String...attributesChanged) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		long t = System.currentTimeMillis();
		if (getEntity() == null) {
			return null;
		}

		Serializable id = ca.getIdentifier(entity);
		String entityName = entityClass.getName();
		List<T> history = ca.getHistoryof(entityClass, id, attributesChanged);
		if (includeCurrent) {
			T current = getEntity();
			if (current instanceof BaseEntity) {
				PhiRevisionEntity rev = new PhiRevisionEntity();
				rev.setCurrent(true);
				((BaseEntity)current).setRevision(rev);
			}
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

		Contexts.getConversationContext().set(getConversationName() + historyListSuffix, dm);
		
		Events.instance().raiseEvent(PhiEvent.HISTORY, getEntity());
				return dm;
	}

	/**
	 * Used to avoid detached object
	 * @param entity
	 * @return
	 * @throws PhiException
	 */
	@SuppressWarnings("unchecked")
	public T merge(T entity) throws PhiException {
		if (getEntity() != null) {
			inject(ca.update(entity));
		}
		
			Events.instance().raiseEvent(PhiEvent.CREATE, entity);
		
		return this.entity;
	}
	
	public T update() throws PhiException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (getEntity() != null) {
			inject(update(entity));
		}
		
		return entity;
	}
	
	/**
	 * Make update of one entity, trying to avoid steal object exception by merging changes made in other further revisions.
	 * TO BE COMPLETED. [missing proper compare on CodeValue attributes and list of CodeValue.
	 * 
	 * If there is nothing to merge, the method create() the object
	 * if the method is able to merge changes, the object is created, with merged attributes.
	 * If the method find conflictual changes, the create() is executed, it will throw steal object exception. 
	 */
	public T update(T entity) throws PhiException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (entity == null) { 
			log.error("null entity not updated");
			return null;
		}
		
		if (!(entity instanceof BaseEntity)) {
			//try to use hiberate merge (steal object exception could be thrown)
			return merge(entity);
		}
		
		BaseEntity conversationEntity = (BaseEntity) entity; 
		long internalId = conversationEntity.getInternalId();
		long conversationVersionNum = conversationEntity.getVersion(); 

		//*********
		// Point 1: get revision from history, corresponding to the revision from which the object is started to be modified.
		//			get its revision number.
		//			[remove the line] than compare the conversationEntity with the conversationOriginalEntity, to know changes currently made by user
		//*********
		PhiRevisionEntity conversationOriginalRev = ca.getNthRevision(conversationEntity, conversationVersionNum);
		BaseEntity conversationOriginalEntity = (BaseEntity)ca.getRevision(conversationEntity, conversationOriginalRev.getId());
		long conversationRevisionNum = conversationOriginalRev.getId();
		
		//*********
		// Point 2: get the last revision from database (dbLastRevisionEntity).
		//          get its revision number.
		//          check if revision number of last one is equal to revision number from where user starts to make changes.
	    //			if the numbers are equal it means the user is working on the last revision. It can be saved.
		//			VERSION NUMBER is available only in object in conversation. can not be used to compare historical revisions.
		//*********
		PhiRevisionEntity dbLastRevision = ca.getLastRevision(entityClass, internalId);
		BaseEntity dbLastRevisionEntity = (BaseEntity)ca.getRevision(conversationEntity, dbLastRevision.getId());
		long dbLastRevisionNum = dbLastRevision.getId();
		
		if (conversationRevisionNum == dbLastRevisionNum) {			

			log.info("During update check, find match between current and last available revision number:"+conversationRevisionNum);
			create((T)conversationEntity);
			return (T)conversationEntity;
		}
		
		//*********
		//  Point 3: Revision number are difference. 
		//			get the difference written by the user in conversation (conversationEntity), from the revision where he has started to work (conversationOriginalEntity)
		//			get the difference between the last revision (dbLastRevisionEntity), and the revision where the user has started to work (conversationOriginalEntity)
		//          if the differences are made on disjunted list of attributes, the merge can be done.
		//			otherwise the difference are made on conflictual attributes.
		//
		//*********
		HashMap<String,Object> differenceWrittenInConv = getDifference(conversationOriginalEntity, conversationEntity);
		List<String> differenceAttributesInConv = new ArrayList<String>(differenceWrittenInConv.keySet());
		
		if (differenceWrittenInConv == null || differenceWrittenInConv.isEmpty()) {
			//object in conversation, need to be saved but nothing really different from original version.
			log.info("1");
		}
		
		HashMap<String,Object> differenceWrittenInDb = getDifference(conversationOriginalEntity, dbLastRevisionEntity);
		List<String> differenceAttributesInDb   = new ArrayList<String>(  differenceWrittenInDb.keySet());
		
		if (differenceWrittenInDb == null || differenceWrittenInDb.isEmpty()) {
			//object in db is not changed also if it's a newest revision...
			log.info("2");
		}
		
		if (Collections.disjoint(differenceAttributesInConv, differenceAttributesInDb)) {
			//find if two list are disjoint, in other words attributes cheanged in conversation are different 
			//from attributes changed in db, and merge can be done by applying  
			//differenceAttributesInConv ---> dbLastEntity
			ca.refresh((T)conversationEntity);
			for (String property : differenceAttributesInConv) {
				propBean.setProperty(conversationEntity, property, differenceWrittenInConv.get(property));
			}
			log.info("During update entity find compatible mergiable revision conversation:"+conversationRevisionNum+ " db revision: "+dbLastRevisionNum);
			ca.create((T)conversationEntity);
			
			Events.instance().raiseEvent(PhiEvent.CREATE, conversationEntity);
			
			return (T)conversationEntity;
		}

		//differenceAttributesInConv differenceAttributesInDb are not disjoint.
		//find attributes in common (conflictual changes, not mergiable!) 
		List<String> conflictualAttributes = new ArrayList(CollectionUtils.intersection(differenceAttributesInConv, differenceAttributesInDb));
		log.error("Unable to merge conversation version "+conversationVersionNum+ " with newest version. Conversation revision:"+conversationRevisionNum+", db revision: "+dbLastRevisionNum+". Conflictual attributes:"+conflictualAttributes);

		//*********
		//  make update, to ****** throw steal object *****
		//  throws steal object is needed because user cannot save the changed object.
		//*********
		create((T)conversationEntity);
		
		//this row should be never reached.
		return null;
	}

	/*
	 * Given two baseEntity entities, find difference attributes, 
	 * and put in the hashmap result the (attributeName, attributeValue) of newest object changed from old.
	 */
	private HashMap<String,Object> getDifference(BaseEntity old, BaseEntity newest) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		HashMap<String,Object> difference = new HashMap<String, Object>();
		if (old == null || newest == null)
			return null;
		
		PropertyDescriptor[] props = propBean.getPropertyDescriptors(entityClass);
		
		for (PropertyDescriptor prop : props) {
			if ("version".equals(prop.getName())) {
				//skip version property.
				continue;
			}
			Object oldValue = propBean.getProperty(old, prop.getName());
			Object newValue = propBean.getProperty(newest, prop.getName());
			
			if (newValue == null && oldValue == null)
				continue;
			
			if ((newValue == null && oldValue != null) || (newValue != null && oldValue == null)) {
			//if (newValue == null ^ oldValue == null) {
				difference.put(prop.getName(),newValue);
				continue;
			}

			//both != null.
			if (!(newValue instanceof Collection )) {
				//this is not a collaction. compare with equals.

				//TODO: if object is instance of base Entity...
				//TODO: if object is instance of CodeValue...
				if (newValue.equals(oldValue)) {
					continue;
				}
				log.debug("difference in non collection attribute "+prop.getName()+". OLD:"+oldValue+" NEW:"+ newValue);
				difference.put(prop.getName(),newValue);
				continue;
			}
			
			//Elements are collections!
			Collection newValueColl = (Collection) newValue;
			Collection oldValueColl = (Collection) oldValue;
			
			if (newValueColl.isEmpty() && oldValueColl.isEmpty()) {
				continue;
			}
							
			if ((newValueColl.size() > oldValueColl.size()) || (newValueColl.size() < oldValueColl.size())){
				log.warn("check2 "+prop.getName()+":"+ newValue);
				difference.put(prop.getName(),newValue);
				continue;
			}
			
			//***both list are not null, and with same size. Check contents.**///
			//try equals..
			if (newValue.equals(oldValue)) {
				continue;
			}
		
			//equals doesn't work properly on list, or list are really different. check it.
			//((ParameterizedType)prop.getWriteMethod().getGenericParameterTypes()[0]).getActualTypeArguments()[0]
			Type type = prop.getWriteMethod().getGenericParameterTypes()[0];
			if (! (type instanceof ParameterizedType)) {
				log.error("unable to detect type of parameter "+prop.getName());
				continue;
			}
			
     	    Class listType = (Class)((ParameterizedType)type).getActualTypeArguments()[0];
     	    if (!BaseEntity.class.isAssignableFrom(listType)) {
     	    	//TODO: liste di codeValue
     	    	//TODO: liste generiche.
     	    	log.warn("TO DO");
     	    }
     	    
 	    	List<Long> newIntId= new ArrayList<Long>();
 	    	List<Long> oldIntId= new ArrayList<Long>();
 	    	
 	    	for (Object o : newValueColl) {
 	    		BaseEntity e = (BaseEntity)o;
 	    		newIntId.add(e.getInternalId());
 	    	}
 	    	
 	    	for (Object o : oldValueColl) {
 	    		BaseEntity e = (BaseEntity)o;
 	    		oldIntId.add(e.getInternalId());
 	    	}
 	    	
 	    	Collection commonId = CollectionUtils.intersection(newIntId, oldIntId);
 	    	if (commonId.size() == newIntId.size()) {
 	    		//both list have all the elements in common (no intersection!): 
 	    		//so the list of internalId are equal and also the respective collections of entity. 
 	    		//no investigation on related entities.
 	    		continue;
 	    	}
 	    	
 	    	log.warn("check3 "+prop.getName()+":"+ newValue);
 	    	difference.put(prop.getName(),newValue);
 	    	
     	} //for (PropertyDescriptor prop : props) {
		
		return difference;
		
	}
		
	@ShowInDesigner(description="Delete an object")
	public void delete() throws PhiException {

		//Multiselect:
		boolean multiselected = false;
		if (selectedEntities != null && selectedEntities.size() >0) {
			for (T ent : selectedEntities.keySet()) {
				if (ent != null && selectedEntities.get(ent) == true) {

					long t = System.currentTimeMillis();
					Object id = ca.getIdentifier(ent);

					ca.delete(ent);
					
					Events.instance().raiseEvent(PhiEvent.DELETE, ent);

					log.info("[cid="+Conversation.instance().getId()+"] Deleted object of Class "+
							HibernateProxyHelper.getClassWithoutInitializingProxy(ent).getName()+" with internal_id: "+((id != null) ? id.toString() : "N.A.")+
							" in: "+(System.currentTimeMillis() -t)+" ms ");

					eject();
					multiselected = true;
				}
			}
		}

		if (!multiselected && getEntity() != null) {

			long t = System.currentTimeMillis();
			
			ProcessManager pm = ProcessManagerImpl.instance();
			Serializable id = ca.getIdentifier(entity);
			
			if (pm.isStateless() && entity instanceof BaseEntity) {
				//If process is stateless uses alternative persistence context, avoiding pollution of ca.
				try {
					GenericAdapterLocalInterface ga = GenericAdapter.instance();
					T toBeDeleted = (T)ga.get(HibernateProxyHelper.getClassWithoutInitializingProxy(entity), id);

					ga.delete(toBeDeleted);
					
				} catch (NamingException e) {
					log.error("Error getting GenericAdapter", e);
				}
				
			} else {
				ca.delete(entity);
			}
			Events.instance().raiseEvent(PhiEvent.DELETE, entity);

			
			log.info("[cid="+Conversation.instance().getId()+"] Deleted object of Class "+
					entityClass.getName()+" with internal_id: "+((id != null) ? id.toString() : "N.A.")+
					" in: "+(System.currentTimeMillis() -t)+" ms ");

			eject();
		}				
	}

	@SuppressWarnings("unchecked")
	@ShowInDesigner(description="Injects an object")
	public void inject(Object baseEntityOrMap) {
		Context conversation = Contexts.getConversationContext();

		if (baseEntityOrMap instanceof BaseEntity) {
			entity = (T)baseEntityOrMap;
		} else if (baseEntityOrMap instanceof Map) {
			entity = ca.load(entityClass, (Long)((Map)baseEntityOrMap).get("internalId"));
			//Remove map from event context, to avoid finding of map instead of entity
			Contexts.getEventContext().remove(getConversationName());
		} 

		conversation.set(conversationName, entity);
		log.info("[cid="+Conversation.instance().getId()+"] "+
				"Injected " + entity );
		
		Events.instance().raiseEvent(PhiEvent.INJECT, entity);
	}
	
	@ShowInDesigner(description="Injects first object of given list or datamodel")
	public void injectFirst(Object dmOrList) {
		//incase of null, empty list/data model, the method eject current object
		//to avoid editing of wrong object, already existing in conversation.
		if (dmOrList == null){
			eject();
			return;
		}
		
		List<T> entitylist = null;
		if (dmOrList instanceof IdataModel){
			if (((IdataModel)dmOrList).isEmpty()) {
				eject();
				return;
			}
			
			entitylist = ((IdataModel)dmOrList).getList();
		}
		
		if (dmOrList instanceof List) {
			entitylist = (List)dmOrList;
		}
		
		if (entitylist == null || entitylist.isEmpty() || entitylist.get(0) == null) {
			eject();
			return;
		}
		
		inject(entitylist.get(0));
	}
	
	@ShowInDesigner(description="Injects first object of current ClassList in conversation")
	public void injectFirst() {
		Context conversation = Contexts.getConversationContext();
		IdataModel<T> dm = (IdataModel<T>)conversation.get(getConversationName() + listSuffix);
		
		injectFirst(dm);
	}
	
	@ShowInDesigner(description="Injects an object and advance process")
	public void injectAndProceed(Object baseEntityOrMap, String mnemonicName) throws PhiException {

		inject(baseEntityOrMap);

		if ( mnemonicName != null && !mnemonicName.isEmpty() ){
			ProcessManagerImpl.instance().manageTask(mnemonicName);
		}
	}

	@ShowInDesigner(description="Injects an object with a specified conversation name")
	public void inject(T rimObj, String conversationName) {

		if ( conversationName == null || conversationName.isEmpty() )
			return;

		Contexts.getConversationContext().set(conversationName, rimObj);
		entity = rimObj;
		
		Events.instance().raiseEvent(PhiEvent.INJECT, entity);		
	}

	/**
	 * Inject objects from javascript
	 * this.injectId id of entityClass to inject
	 * @throws PhiException
	 */
	public void injectbyId() throws PhiException {
		if(injectIntoName == null)
			inject(ca.get(entityClass, injectId), conversationName);
		else 
			inject(ca.get(entityClass, injectId), injectIntoName);
	}
	
	/**
	 * Inject objects from javascript and signal process
	 * this.injectId id of entityClass to inject
	 * this.mnemonicName xxx;yyy button.value and button.mnemonic or xxx button.value
	 * @throws PhiException
	 */
	public void injectbyIdAndProceed() throws PhiException {
		injectbyId();

		if ( mnemonicName != null && !mnemonicName.isEmpty() ){
			ProcessManagerImpl.instance().manageTask(mnemonicName);
		}
	}

	@ShowInDesigner(description="Inject empty list with current class object list name")
	public void injectEmptyList() {
		injectEmptyList(getConversationName() + listSuffix);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ShowInDesigner(description="Inject an empty list in conversation, with a given name")
	public void injectEmptyList(String name) {
		IdataModel<?> dm = new PhiDataModel(new ArrayList<T>(), conversationName);
		Contexts.getConversationContext().set(name, dm);
	}



	/**
	 * Id of entityClass setted from javascript to use injectbyId or injectbyIdAndProceed
	 * Was of generic type ID but works only in JAVA7
	 * from javascript with JAVA6 arrives a String instead of Long
	 */
	private ID injectId = null;

	public void setId(String id) {

		if (id.matches("\\d*")) {
			this.injectId = (ID) new Long(id);
		} else {
			this.injectId = (ID)id;
		}
	}

	/**
	 * conversationName setted from javascript to use injectbyId or injectbyIdAndProceed
	 */
	private String injectIntoName = null;

	public void setInjectIntoName(String injectIntoName) {
		this.injectIntoName = injectIntoName;
	}
	
	/**
	 * mnemonicName setted from javascript to use injectbyIdAndProceed
	 */
	private String mnemonicName;

	public void setMnemonicName(String mnemonicName) {
		this.mnemonicName = mnemonicName;
	}

	@ShowInDesigner(description="Inject given list with current class object list name")
	public List injectList(List list) {
		return injectList(list, getConversationName() + listSuffix);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ShowInDesigner(description="Inject a list in conversation, with a given name")
	public List injectList(List list, String name) {

		Context conversation = Contexts.getConversationContext();

		//if (list == null || list.isEmpty()) { 
		if (list == null) {
			injectEmptyList(name);
			return ((IdataModel)conversation.get(name)).getList();
		}

		IdataModel<?> dm = new PhiDataModel(list, conversationName);

		conversation.set(name, dm);
		
		//can be an empty LazyList -> avoid NullPointerException
		if(!list.isEmpty() && list.get(0)!=null){
			Events.instance().raiseEvent(PhiEvent.READ, HibernateProxyHelper.getClassWithoutInitializingProxy(list.get(0)).getSimpleName());
		}
		
		
		return list;
	}
	
	@ShowInDesigner(description="Unproxy an object")
	public void unProxy() {
		if (getEntity() instanceof HibernateProxy) { //deproxy
			entity = ((T)((HibernateProxy)entity).getHibernateLazyInitializer().getImplementation());
		}
		inject(entity);
	}

	@ShowInDesigner(description="Ejects an object")
	public void eject() {
		Contexts.getConversationContext().remove(getConversationName());
		entity = null;
	}

	@ShowInDesigner(description="Ejects an object")
	public void ejectAndEvict() {
		ca.evict(entity); //remove from Hibernate cache, to avoid read from cache of nullified changes to the conversation object 
		eject();
	}

	@ShowInDesigner(description="Eject current class object list")
	public void ejectList() {
		ejectList( getConversationName() + listSuffix);
	}
	
	@ShowInDesigner(description="Ejects a list from conversation")
	public void ejectList(String name) {
		Contexts.getConversationContext().remove(name);
	}

	@ShowInDesigner(description="Eject current class object list")
	public void ejectListAndEvict() {
		ejectListAndEvict( getConversationName() + listSuffix);
	}
	
	@ShowInDesigner(description="Ejects a list from conversation")
	public void ejectListAndEvict(String name) {
		Context conversation = Contexts.getConversationContext();
		IdataModel<?> dataModel = (IdataModel<?>)conversation.get(name);
		if (dataModel!=null) {
			List<?> entityList = dataModel.getList();
			if (entityList != null) {
				Iterator<?> itr = entityList.iterator();
				while (itr.hasNext()) {
					Object next = itr.next();
					if(!next.equals(entity)){
						/*
						 * if next is not the current injected entity, 
						 * remove it from Hibernate cache, to avoid read from cache of nullified changes to the conversation object 
						 */
						ca.evict(next); 
					}
				}		
			}
		}
		conversation.remove(name);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ShowInDesigner(description="Link two objects")
	public boolean link(String attributeName, Object value) throws PhiException { 

		if (value == null) 
			return false;

		if (value instanceof List && ((List)value).isEmpty())
			return false;

		try {
			if (getEntity() == null) {
				//Create entity if doesn't exist 
				entity = entityClass.newInstance();
				inject(entity);
			}

			PropertyDescriptor pd;
			String currentAttributeName = attributeName;
			Class currentClass = entityClass;
			Object currentEntity = entity;

			//if attribute contains . split and create intermediate objects
			if (attributeName.contains(".")) {
				String[] elParts = attributeName.split("\\."); 

				Object nextEntity = null;

				for (int z = 0; z<elParts.length - 1; z++) {
					pd = propBean.getPropertyDescriptor(currentEntity, elParts[z]);
					Method wMethod = pd.getWriteMethod();

					Type[] typez = wMethod.getGenericParameterTypes();

					if (typez == null) {
						throw new PhiException(getConversationName() + "." + attributeName + " unable to determinate attribute type", ErrorConstants.APPLICATION_OBJECT_LINK_ERR_CODE);
					}
					if (typez[0] instanceof ParameterizedType ) {
						throw new PhiException(getConversationName() + "." + attributeName + " path contains Lists or Sets, cannot automatically pupulate intermediate path", ErrorConstants.APPLICATION_OBJECT_LINK_ERR_CODE);
					} else if (typez[0] instanceof Class) {
						//Take current intermediate object
						nextEntity = pd.getReadMethod().invoke(currentEntity);
						if (nextEntity == null) {
							//If doesen't exist: create intermediate object and link to parent
							nextEntity = ((Class)typez[0]).newInstance();
							wMethod.invoke(currentEntity, nextEntity);
						}
					}
					currentEntity = nextEntity;
					currentClass = HibernateProxyHelper.getClassWithoutInitializingProxy(currentEntity);
					currentAttributeName = elParts[z+1];
				}
			}

			pd = propBean.getPropertyDescriptor(currentEntity, currentAttributeName);

			if (pd == null) {
				log.error("Error linking objects, missing attribute "+attributeName+" into entity "+currentEntity);
				throw new PhiException(getConversationName() + "." + attributeName + " not exists", ErrorConstants.APPLICATION_OBJECT_LINK_ERR_CODE);
			}

			Class clazz = pd.getPropertyType();

			// If is a list search for addXxx method to add element to the list. Add methods sets also inverse relation.
			if (clazz.isAssignableFrom(Collection.class)  || clazz.isAssignableFrom(List.class)) { 

				String initialAttributeName = currentAttributeName;
				char[] stringArray = currentAttributeName.toCharArray();
				stringArray[0] = Character.toUpperCase(stringArray[0]);
				currentAttributeName = new String(stringArray);

				Method addMethod = null;
				List valueColl = null;
				boolean isList = false;
				
				if (value instanceof IdataModel) {
					value = ((IdataModel)value).getFullList();
				}

				if (value instanceof List) {
					valueColl = (List)value;
					if (!valueColl.isEmpty()) {
						addMethod = MethodUtils.getAccessibleMethod(currentClass, "add" + currentAttributeName, HibernateProxyHelper.getClassWithoutInitializingProxy(valueColl.get(0)));
					}
					isList = true;
				} else {
					addMethod = MethodUtils.getAccessibleMethod(currentClass, "add" + currentAttributeName, HibernateProxyHelper.getClassWithoutInitializingProxy(value));
				}

				if (addMethod != null) {
					if (isList) {
						for (Object v : valueColl) {
							addMethod.invoke(currentEntity, v);
						}
						//link a list of object to a relationship
						log.info("[cid="+Conversation.instance().getId()+"] "+
								"Linked List of "+ classNameAndListId(valueColl) + " to List relation "+ attributeName + " of "+entity );
					} else {
						//link single object to releationship.
						addMethod.invoke(currentEntity, value);
						log.info("[cid="+Conversation.instance().getId()+"] "+
								"Linked "+ value + " to List relation "+ attributeName + " of "+entity );
					}

					return true;
				}

				//If entity doesen't have an addXxx method use Collection.add method, inverse relation won't be populated!
				log.warn("Method add" + attributeName + " of " + currentClass + " not found! Linking with Collection.add(value)");

				Collection coll = (Collection)propBean.getProperty(currentEntity, initialAttributeName);

				if (coll == null) {
					log.warn("Empty collection!");
					coll = new ArrayList();
					propBean.setProperty(currentEntity, initialAttributeName, coll);
				}


				if (isList) {
					coll.addAll(valueColl); 
					//link a list of object to a relationship
					log.info("[cid="+Conversation.instance().getId()+"] "+
							"Linked List of "+ classNameAndListId(valueColl) + " to List relation "+ attributeName + " of "+entity + "  (inverse relation not set)");
				} else {
					if (!coll.contains(value)) {
						coll.add(value);
						log.info("[cid="+Conversation.instance().getId()+"] "+
								"Linked "+ value + " to List relation "+ attributeName + " of "+entity + "  (inverse relation not set)");
					} else {
						log.info("[cid="+Conversation.instance().getId()+"] "+
								"Link is already done of "+ value + " to List relation "+ attributeName + " of "+entity );
					}

				}
			} else { 
				//linked a simple object to another, with a single property (not List)
				propBean.setProperty(currentEntity, currentAttributeName, value);
				if (currentEntity instanceof AuditedEntity && ("author".equals(currentAttributeName) || "cancelledBy".equals(currentAttributeName))){
					UserBean ub = UserBean.instance();
					propBean.setProperty(currentEntity, currentAttributeName+"Role", ub.getCurrentSystemUser().getCode());
				}
				log.info("[cid="+Conversation.instance().getId()+"] "+
						"Linked "+ value + " to relation "+ currentAttributeName + " of "+entity );
			}


		} catch (PhiException e) {
			throw e;
		} catch (Exception e) {
			throw new PhiException(getConversationName() + "." + attributeName + " - " + value, e, ErrorConstants.APPLICATION_OBJECT_LINK_ERR_CODE);
		}

		return true;
	}

	private String classNameAndListId(List l) {
		if (l == null || l.isEmpty())
			return "[]";

		String IdList="[";
		String className = "";
		for (Object o : l) {
			if (className.isEmpty()) {
				className = HibernateProxyHelper.getClassWithoutInitializingProxy(o).getName();
			}
			IdList += ca.getIdentifier(o)+",";

		}
		String ret = className+ " " +IdList.substring(0,IdList.length()-1)+"]";
		return ret;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ShowInDesigner(description="UnLink two objects")
	public boolean unLink(String attributeName, Object value) throws PhiException {

		if (value == null)
			return false;

		if (value instanceof List && ((List)value).isEmpty())
			return false;


		if (getEntity() == null) {
			return false;
		}

		try {
			PropertyDescriptor pd = propBean.getPropertyDescriptor(getEntity(), attributeName);

			if (pd == null) {
				throw new PhiException(getConversationName() + "." + attributeName + " not exists", ErrorConstants.APPLICATION_OBJECT_UNLINK_ERR_CODE);
			}

			Class clazz = pd.getPropertyType();

			if (clazz.isAssignableFrom(Collection.class)  || clazz.isAssignableFrom(List.class)) {
				//TODO: not working with long binding, should be implemented for cycle for split it, like in link method.
				Class currentClass = entityClass; 
				Object currentEntity = entity;

				String initialAttributeName = attributeName;
				char[] stringArray = attributeName.toCharArray();
				stringArray[0] = Character.toUpperCase(stringArray[0]);
				attributeName = new String(stringArray);

				Method removeMethod = null;
				List valueColl = null;
				boolean isList = false;

				if (value instanceof IdataModel) {
					value = ((IdataModel)value).getFullList();
				}

				if (value instanceof List) {
					valueColl = (List)value;
					if (!valueColl.isEmpty()) {
						removeMethod = MethodUtils.getAccessibleMethod(currentClass, "remove" + attributeName, HibernateProxyHelper.getClassWithoutInitializingProxy(valueColl.get(0)));
						isList = true;
					}
				} else {
					removeMethod = MethodUtils.getAccessibleMethod(currentClass, "remove" + attributeName, HibernateProxyHelper.getClassWithoutInitializingProxy(value));
				}

				if (removeMethod != null) {
					if (isList) {
						List<Object> duplicate = new ArrayList (valueColl);
						for (Object v : duplicate) {
							removeMethod.invoke(currentEntity, v); 
						}
					} else {
						removeMethod.invoke(currentEntity, value);
					}
					return true;
				}

				//If entity doesen't have an removeXxx method use Collection.remove method, inverse relation won't be populated!
				log.warn("Method remove" + attributeName + " of " + currentClass + " not found! Linking with Collection.add(value)");

				Collection coll = (Collection)propBean.getProperty(currentEntity, initialAttributeName);

				if (coll == null) {
					log.warn("Empty collection!");
					coll = new ArrayList();
				}

				if (isList) {
					coll.removeAll(valueColl);
				} else {
					coll.remove(value);
				}

			} else {
				propBean.setProperty(getEntity(), attributeName, null);
				if (getEntity() instanceof AuditedEntity && ("author".equals(attributeName) || "cancelledBy".equals(attributeName))){
					propBean.setProperty(getEntity(), attributeName+"Role", null);
				}
			}

		} catch (Exception e) {
			throw new PhiException(getConversationName() + "." + attributeName + " - " + value, e, ErrorConstants.APPLICATION_OBJECT_UNLINK_ERR_CODE);
		}

		return true;
	}

	
	@ShowInDesigner(description="Refresh rim Object")
	public void refresh() throws PhiException {
		if (entity == null)
			getEntity();
		if (entity != null) 
			ca.refreshIfNeeded(entity); 
	}
	
	public void refresh(List<T> lst){
		if(lst!=null){
			for(T ent : lst){
				ca.refreshIfNeeded(ent);
			}
		}
	}


	public void setCodeValueOid(String attributeName, String oid) throws PhiException {

		if (attributeName == null || attributeName.isEmpty() || oid == null || oid.isEmpty()) {
			return;
		}

		Vocabularies voc = VocabulariesImpl.instance();
		CodeValue cv = voc.getCodeValueOid(oid);

		if (cv == null) {
			log.warn("Missing oid "+oid);
			return;
		}

		try {

			if (getEntity() == null) {
				entity = entityClass.newInstance();
				inject(entity);
			}

			propBean.setProperty(getEntity(), attributeName, cv);

		} catch (Exception e) {
			throw new PhiException("Error setCodeValueOid oid: " + oid + " to " + getConversationName() + "." + attributeName,
					e, ErrorConstants.PERSISTENCE_RIM_PROPERTY_TYPE_NOT_FOUND_ERR_CODE);
		}
	}

	public void setCodeValue(String attributeName, String codeSystemName, String domainName, String code ) throws PhiException, DictionaryException {

		if (    attributeName == null || attributeName.isEmpty() || codeSystemName == null || codeSystemName.isEmpty()  
				|| domainName == null ||    domainName.isEmpty() || code == null           || code.isEmpty()) {
			return;
		}

		Vocabularies voc = VocabulariesImpl.instance();
		CodeValue cv = voc.getCodeValueCsDomainCode(codeSystemName, domainName, code);

		if (cv == null) {
			return;
		}

		try {

			if (getEntity() != null) {
				propBean.setProperty(entity, attributeName, cv);
			} else {
				log.error("Error setCodeValue: no entity in conversation (request to set attribute:"+attributeName+" with cv: "+cv);
			}
		} catch (Exception e) {
			throw new PhiException("Error setCodeValue codeSystemName: " + codeSystemName + " domainName: " + domainName + " code: " + code + " to " + getConversationName() + "." + attributeName,
					e, ErrorConstants.PERSISTENCE_RIM_PROPERTY_TYPE_NOT_FOUND_ERR_CODE);
		}
	}


	protected String getConversationName() {

		return conversationName;
	}

	@SuppressWarnings("unchecked")
	public T getEntity() {

		T entityFromConversation = (T)Contexts.getConversationContext().get(getConversationName());

		if (entity == null) { //FIXME now checks every time in conv.... to have the right entity
			entity = entityFromConversation;
		} else if (entity != entityFromConversation && entityFromConversation != null) {
			entity = entityFromConversation;
			//			entityCtx = null;
		}

		return entity;
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


	public Map<Object, InputSuggestionBean> getAutocomplete() {
		return autocomplete;
	}

	public void setAutocomplete(Map<Object, InputSuggestionBean> autocomplete) {
		this.autocomplete = autocomplete;
	}


	/**
	 * Hashmap of temporary objects.
	 * Used in processes to store temporary values
	 */
	protected HashMap<String, Object> temporary;

	public HashMap<String, Object> getTemporary() {
		if (temporary == null)
			temporary = new HashMap<String, Object>();
		return temporary;
	}

	public void setTemporary(HashMap<String, Object> temporary) {
		this.temporary = temporary;
	}

	public List<String> getSelect() {
		return select;
	}

	public void setSelect(List<String> select) {
		this.select = select;
	}

	//Filtering with criteria

	public HashMap<String, Object> getEqual() {
		return equal;
	}

	public void setEqual(HashMap<String, Object> equal) {
		this.equal = equal;
	}

	public HashMap<String, Object> getGreaterEqual() {
		return greaterEqual;
	}

	public void setGreaterEqual(HashMap<String, Object> greaterEqual) {
		this.greaterEqual = greaterEqual;
	}

	public HashMap<String, Object> getGreater() {
		return greater;
	}

	public void setGreater(HashMap<String, Object> greater) {
		this.greater = greater;
	}

	public HashMap<String, Object> getLessEqual() {
		return lessEqual;
	}

	public void setLessEqual(HashMap<String, Object> lessEqual) {
		this.lessEqual = lessEqual;
	}

	public HashMap<String, Object> getLike() {
		return like;
	}

	public void setLike(HashMap<String, Object> like) {
		this.like = like;
	}

	public HashMap<String, Object> getLess() {
		return less;
	}

	public void setLess(HashMap<String, Object> less) {
		this.less = less;
	}

	public HashMap<String, Object> getNotEqual() {
		return notEqual;
	}

	public void setNotEqual(HashMap<String, Object> notEqual) {
		this.notEqual = notEqual;
	}

	public HashMap<String, Object> getIsNull() {
		return isNull;
	}

	public void setIsNull(HashMap<String, Object> isNull) {
		this.isNull = isNull;
	}
	
	public HashMap<String, Object> getIn() {
		return in;
	}
	
	public void setIn(HashMap<String, Object> in) {
		this.in = in;
	}
	
	public HashMap<String, Object> getNotIn() {
		return notIn;
	}
	
	public void setNotIn(HashMap<String, Object> notIn) {
		this.notIn = notIn;
	}
	
	@ShowInDesigner(description="cleanRestrictions")
	public void cleanRestrictions() {
		equal.clear();
		greaterEqual.clear();
		greater.clear();
		lessEqual.clear();
		like.clear();
		less.clear();
		notEqual.clear();
		isNull.clear();
		in.clear();
		notIn.clear();
		select.clear();
		orderBy.clear();

		readPageSize = 0;
		entityCriteria = null;
		entityProjections = null;
		distinct = false;
		
		
	}
	
	public void backupRestrictions() {
		restrictionBackup.put("eq", equal.clone());
		restrictionBackup.put("ge", greaterEqual.clone());
		restrictionBackup.put("gt", greater.clone());
		restrictionBackup.put("le", lessEqual.clone());
		restrictionBackup.put("like", like.clone());
		restrictionBackup.put("lt", less.clone());
		restrictionBackup.put("ne", notEqual.clone());
		restrictionBackup.put("null", isNull.clone());
		restrictionBackup.put(inRestriction, in.clone());
		restrictionBackup.put(notInRestriction, notIn.clone());
		restrictionBackup.put(selectRestriction, new ArrayList<String>(select));
		restrictionBackup.put(orderRestriction, orderBy.clone()); 
		restrictionBackup.put("distinct", distinct);		
		restrictionBackup.put("readPageSize", readPageSize);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void restoreRestrictions() {
	
		cleanRestrictions();

		if (restrictionBackup.isEmpty()) {
			return;
		}
		
		//Need to restore also the FilterMaps. Otherwise we lose filter values
		try{
			equal.putAll((HashMap)restrictionBackup.get("eq")); 
			greaterEqual.putAll((HashMap)restrictionBackup.get("ge"));
			greater.putAll((HashMap)restrictionBackup.get("gt"));
			lessEqual.putAll((HashMap)restrictionBackup.get("le"));
			like.putAll((HashMap)restrictionBackup.get("like"));
			less.putAll((HashMap)restrictionBackup.get("lt"));
			notEqual.putAll((HashMap)restrictionBackup.get("ne"));
			isNull.putAll((HashMap)restrictionBackup.get("null"));
			in.putAll((HashMap)restrictionBackup.get(inRestriction));
			notIn.putAll((HashMap)restrictionBackup.get(notInRestriction));
			select.addAll((List)restrictionBackup.get(selectRestriction));
			orderBy.putAll((HashMap)restrictionBackup.get(orderRestriction));
			distinct = (Boolean)restrictionBackup.get("distinct");
			readPageSize = (Integer)restrictionBackup.get("readPageSize");
			
		}catch(Exception e){
			log.error(entityClass.getSimpleName() + ".restoreRestrictions failed to restore a FilterMap");
		}
		
		
		entityCriteria = null;
		
		for (String rest : restrictionBackup.keySet()) {
			
			if (rest.equals("select")) {
				for (String select : (List<String>)(restrictionBackup.get(rest))) {
					addCriteria(rest, select, rest);  //?? da testare
				}
			} 
			else if (rest.equals("distinct")){
				setDistinct((Boolean)restrictionBackup.get(rest));
			} 
			else if (rest.equals("readPageSize")){
				setReadPageSize((Integer)restrictionBackup.get(rest));
			} 
			else {
				FilterMap filt = (FilterMap)restrictionBackup.get(rest);
				for (String expression : filt.keySet()) {
						addCriteria(rest, expression, filt.get(expression));	
				}
			}
		}
	}

	//Ordering with criteria

	public HashMap<String, Object> getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(HashMap<String, Object> orderBy) {
		this.orderBy = orderBy;
	}

	//Page size
	public Integer getReadPageSize() {
		return readPageSize;
	}

	public void setReadPageSize(Integer readPageSize) {
		this.readPageSize = readPageSize;

		if ((readPageSize == null || readPageSize == 0 ) && entityCriteria != null) {
			entityCriteria.setFirstResult(0);
			entityCriteria.setMaxResults(Integer.MAX_VALUE);
			// Uses reflection to reset maxResults, the only way...
//			try {
//				Field maxRes = entityCriteria.getClass().getDeclaredField("maxResults");
//				maxRes.setAccessible(true);
//				maxRes.set(entityCriteria, null);
//			} catch (Exception e) {
//				throw new IllegalArgumentException("Error resetting maxResults");
//			}
		}
	}

	//Filter methods with criteria query
	protected boolean filterBySdl = true;

	public void setFilterBySdl(boolean filterBySdl) {
		this.filterBySdl = filterBySdl;
	}

	private boolean distinct = false;

	/**
	 * Warning: be sure to set distinct parameter BEFORE setting some restriction criteria,
	 * otherwise it will not work.
	 * @param distinct
	 */
	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	
	protected void addLocationRestriction(){
		
		if (LocatedEntity.class.isAssignableFrom(entityClass)) {

			List<Long> sdlocIds = UserBean.instance().getSdLocs();

			if (filterBySdl && sdlocIds != null && !sdlocIds.isEmpty() ) {
				if (entityClass.isAssignableFrom(ServiceDeliveryLocation.class)) {
					entityCriteria.add(Restrictions.in("this.internalId", sdlocIds));
				} else {
					entityCriteria.add(Restrictions.or(Restrictions.isNull(serviceDeliveryLocationId), Restrictions.in(serviceDeliveryLocationId, sdlocIds)));
				}
			}
		}
		
	}
	
	
	/**
	 * Creates criteria query and subCriteria and restrictions
	 * @param restriction can be one contained in {@value #restrictionMap}
	 * @param expression el that leads from entity to restriction value
	 * @param value restriction value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addCriteria(String restriction, String expression, Object value) { 

		try {

			if (entityCriteria == null) {
				entityCriteria = ca.createCriteria(entityClass);
				addLocationRestriction();
				if (useObscurationLvl2() && entityClass != null && "PatientEncounter".equals(entityClass.getSimpleName())) {
					setObscurationLvl2Restrictions(entityCriteria, new String[]{}, -1);
				}
			}

			if ( restriction.equals(selectRestriction) ) {
				if (entityProjections == null) {
					entityProjections =  Projections.projectionList();

					if (distinct) {
						entityProjections.add(Projections.distinct(Projections.property("this.internalId")), "internalId-SELECT");
					} else {
						entityProjections.add(Projections.property("this.internalId"), "internalId-SELECT");
					}

					entityCriteria.setProjection(entityProjections);

					//entityCriteria.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
					resultTranformer = new AliasToEntityMapResultTransformer(entityClass);
					entityCriteria.setResultTransformer(resultTranformer);
				}

			} else {
				if (distinct && select.isEmpty()) {
					resultTranformer = Criteria.DISTINCT_ROOT_ENTITY;
					entityCriteria.setResultTransformer(resultTranformer);
				}
			}

			// Remove [] from id binding
			if (expression.contains("[")) {
				expression = expression.replace("[", ".");
				expression = expression.replace("]", "");
			}


			String[] elParts = expression.split("\\."); 

			Class currentClass = entityClass;
			Criteria currentCriteria = entityCriteria;

			for (int z = 0; z<elParts.length; z++) {

				PropertyDescriptor[] props = propBean.getPropertyDescriptors(currentClass);
				PropertyDescriptor pd = null;
				for(PropertyDescriptor prop : props){
					if(elParts[z].equals(prop.getName())){
						pd = prop;
						break;
					}
				}

				if (pd == null) {
					throw new IllegalArgumentException(entityClass + " does not contain property: " + elParts[z]);
				}

				Class nextType = pd.getPropertyType();

				boolean isList = false; 

				Type[] typez = pd.getWriteMethod().getGenericParameterTypes();

				if (typez != null && typez[0] instanceof ParameterizedType ) { // If type is list find the type of the list
					ParameterizedType pt = (ParameterizedType)typez[0];
					nextType = (Class)pt.getActualTypeArguments()[0];
					isList = true;
				}
				
				String alias = generateAliasFromExpression(elParts,z);

				if (nextType.getAnnotation(Entity.class) != null) {
					//Entity

					if (z + 1 < elParts.length) {
						//intermediate token: add subCriteria to produce JOIN

						if (nextType.isAssignableFrom(CodeValue.class)) { //FIX ME 
							nextType = CodeValuePhi.class;
						}

						currentClass = nextType;

						//Check if sub criteria is already present
						Criteria subcriteria = findSubCriteria(alias);

						//If not present add only not null restriction
						if (subcriteria == null) {

							if (value == null) {
								return;
							}

							if ( restriction.equals(selectRestriction) || restriction.equals(orderRestriction) ) {
								currentCriteria = currentCriteria.createCriteria(elParts[z], alias, Criteria.LEFT_JOIN);
							} else {
								currentCriteria = currentCriteria.createCriteria(elParts[z], alias);
							}

						} else {
							currentCriteria = subcriteria;
						}
						
						if (useObscurationLvl2() && !restriction.equals(orderRestriction) && currentClass != null && "PatientEncounter".equals(currentClass.getSimpleName())) {
							setObscurationLvl2Restrictions(currentCriteria, elParts, z);
						}
						//					}
					} else {
						//last token: add restriction to produce WHERE
						if (isList) {
							//Many to many where

							//Check if sub criteria is already present
							Criteria subcriteria = findSubCriteria(alias);

							//If not present add only not null restriction
							if (subcriteria == null) {

								if (value == null) {
									return;
								}

								currentCriteria = currentCriteria.createCriteria(elParts[z], alias);
							} else {

								if(value == null) {
									removeSubCriteria(entityCriteria, alias); 
								}

								currentCriteria = subcriteria;
							}

							String idName = null;
							Serializable id = null;
							if (nextType.isAssignableFrom(CodeValue.class)) {
								idName = "id";
								if (value != null) {
									id = ((CodeValue)value).getId();
								}
							} else {
								idName = "internalId";
								if (value != null) {
									id = ((BaseEntity)value).getInternalId();
								}
							}
							
							//Remove already present expression FIX
							removeExpression(alias, idName + restrictionMap.get(restriction));

							//If not null add new expression
							if (value != null) {
								Criterion criterion = buildCriterion(restriction, idName, id);
								currentCriteria.add(criterion);
							}

						} else {
							if (useObscurationLvl2() && !restriction.equals(orderRestriction) && nextType != null && "PatientEncounter".equals(nextType.getSimpleName())) {
								Criteria peCriteria = findSubCriteria(generateAliasFromExpression(elParts,z));
								if (peCriteria == null) {
									peCriteria = currentCriteria.createCriteria(elParts[z], generateAliasFromExpression(elParts,z), CriteriaSpecification.LEFT_JOIN);
								}
								setObscurationLvl2Restrictions(peCriteria, elParts, z);
							}
							//Entity where
							createRestriction (expression, elParts, z, restriction, value, currentCriteria); 
						}

					}
				} else if (nextType.isAssignableFrom(II.class)) {
					//Embedded attribute id

					Criteria subcriteria = findSubCriteria(alias);

					if (subcriteria == null) {

						if (value == null) {
							return;
						}


						if ( restriction.equals(orderRestriction) ) {
							currentCriteria = currentCriteria.createCriteria(elParts[z], alias, CriteriaSpecification.LEFT_JOIN);
							//FIXME: add or criterion for ID, to add not null restriction on join
						} else {
							currentCriteria = currentCriteria.createCriteria(elParts[z], alias);
						}

					} else {
						currentCriteria = subcriteria;
					}

					if (value != null) {

						removeExpression("Id", "root");
						currentCriteria.add(Restrictions.eq("root", elParts[z+1])); //FIXME: ordering generates where!!! fail

						elParts[z+1] = "extension";

						currentClass = II.class;

					} else {
						//FIXME: REMOVES ALL PREVIOUS II SUBCRITERIA AND EXPRESSIONS... NEED FIX!
						removeExpression("Id", "root");
						removeExpression("Id", "extension");
						removeSubCriteria(entityCriteria, "Id");

						return;
					}

				} else {
					createRestriction (expression, elParts, z, restriction, value, currentCriteria);
					break;
				}

			}

		} catch (Exception e) {
			throw new IllegalArgumentException("Error adding restriction " + restriction + " to expression: " + expression + " value: " + value + " for: " + entity, e);
		}

	}

	private String generateAliasFromExpression (String[] elParts, int z) {
		String alias = "";

		for (int i = 0; i<=z; i++) {
			String t = elParts[i];
			t = t.substring(0,1).toUpperCase()+t.substring(1,t.length());
			alias +=  t;
		}
		if (alias.isEmpty()) {
			return "this";
		}
		return alias;
	}

	private void createRestriction (String expression, String[] elParts, int z, String restriction, Object value, Criteria currentCriteria) { 
		//Calculate property name
		String propertyName = "";

		for (int zz = z; zz<elParts.length; zz++) {
			propertyName += elParts[zz] + ".";
		}

		propertyName = propertyName.substring(0, propertyName.length()-1);

		if ( restriction.equals(selectRestriction) ) {
			//SELECT: add select projection only if not present
			if (!ArrayUtils.contains(entityProjections.getAliases(), expression + "-SELECT")) { 
				entityProjections.add(Projections.property(generateAliasFromExpression(elParts,z-1) + "." + propertyName), expression + "-SELECT");
			}

		} else if ( restriction.equals(orderRestriction) ) {
			//ORDER BY: remove previous order and add new
			removeOrdering(generateAliasFromExpression(elParts,z-1), propertyName, value); 

			if (value == null) {
				return;
			}
			
			if ( value.equals(OrderBy.ascending.name()) ) {
				entityCriteria.addOrder( Order.asc(generateAliasFromExpression(elParts,z-1) + "." + propertyName) );
			} 
			else if ( value.equals(OrderBy.ascendingIgnoreCase.name()) ) {
				entityCriteria.addOrder( Order.asc(generateAliasFromExpression(elParts,z-1) + "." + propertyName).ignoreCase() );
			}
			else if (value.equals(OrderBy.descending.name())) {
				entityCriteria.addOrder( Order.desc(generateAliasFromExpression(elParts,z-1) + "." + propertyName));
			}
			else if (value.equals(OrderBy.descendingIgnoreCase.name())) {
				entityCriteria.addOrder( Order.desc(generateAliasFromExpression(elParts,z-1) + "." + propertyName).ignoreCase());
			}
			
		} else {
			//WHERE: remove restriction and add a new only if not null
			String restrictionString = "";
			if (notInRestriction.equals(restriction)) {
				restrictionString += "not ";
			}
			restrictionString += propertyName;
			if (notInRestriction.equals(restriction)) {
				restrictionString += " in";
			} else {
				if (neRestriction.equals(restriction)) {
					restrictionString += " is null or " + propertyName;
				}
				restrictionString += restrictionMap.get(restriction);
			}
			removeExpression(generateAliasFromExpression(elParts,z-1), restrictionString, value==null);

			//If not null add new expression
			if (value != null) {
				if ( (restriction.equals(inRestriction) || restriction.equals(notInRestriction) ) && value instanceof List && ((List)value).isEmpty()) {
					//do not add in/notIn restriction on empty list. 
				}
				else {
					Criterion criterion = buildCriterion(restriction, propertyName, value);
					currentCriteria.add(criterion);
				}
			}

		}
	}

	/**
	 * Find a subCriteria by alias
	 * @param ofCriteria
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected Criteria findSubCriteria(String alias) {

		Criteria subCriteria = null;

		Iterator<Criteria> subcriteria = ((CriteriaImpl)entityCriteria).iterateSubcriteria();

		while (subcriteria.hasNext()) {
			Criteria crit = subcriteria.next();
			if (crit.getAlias().equals(alias)) {
				subCriteria = crit;
				break;
			}
		}

		return subCriteria;
	}

	/**
	 * Removes a subCriteria by alias
	 * @param ofCriteria
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected void removeSubCriteria(Criteria ofCriteria, String alias) {

		Iterator<Criteria> subCriterias = ((CriteriaImpl)ofCriteria).iterateSubcriteria();

		while (subCriterias.hasNext()) {
			Criteria crit = subCriterias.next();
			if (crit.getAlias().equals(alias)) {
				boolean iHaveChildren = false;
				/*
				 * i cannot remove this subcriteria if other subcriterias have this as parent: 
				 * for example i could have an orderBy or a projection based on this subcriteria
				 */
				
				Iterator<Criteria> otherCriterias = ((CriteriaImpl)ofCriteria).iterateSubcriteria();
				while (otherCriterias.hasNext()) {
					Criteria otherCrit = otherCriterias.next();
					if (otherCrit instanceof Subcriteria && ((Subcriteria)otherCrit).getParent().getAlias().equals(alias)) {
						iHaveChildren = true;
						break;
					}
				}
				
				if(!iHaveChildren){
					subCriterias.remove();
					break;
				}
			}
		}
	}

	/**
	 * Create criterion to filter with criteria, if value is array produces or restrictions
	 * @param restriction
	 * @param propertyName
	 * @param value
	 * @return
	 */
	protected Criterion buildCriterion(String restriction, String propertyName, Object value) {

		if (value instanceof ArrayList ) {
			value = ((ArrayList)value).toArray();
		}
		
		if (value instanceof Object[] ) {

			Criterion criterion = null;
			
			if (restriction.equals(inRestriction)) { 
				criterion = Restrictions.in(propertyName, (Object[])value);
			}
			else if (restriction.equals(notInRestriction)) {
				criterion = Restrictions.not(Restrictions.in(propertyName, (Object[])value));
			}
			else {
				for (Object val : (Object[])value) {
	
					if (criterion == null) {
						if (val == null) {
							criterion = Restrictions.isNull(propertyName);
						} else {
							criterion = buildExpression(restriction, propertyName, val);
						}
					} else {
						if (val == null) {
							criterion = Restrictions.or(criterion, Restrictions.isNull(propertyName));
						} else {
							criterion = Restrictions.or(criterion, buildExpression(restriction, propertyName, val));
						}
					}
				}
			}
			return criterion;

		} else {
			if (!restriction.equals("null")) {
				return buildExpression(restriction, propertyName, value);
			} else {
				if (((Boolean)value) == true) {
					return Restrictions.isNull(propertyName);
				} else {
					return Restrictions.isNotNull(propertyName);
				}
			}
		}
	}

	/**
	 * Create expression to filter with criteria
	 * @param restriction
	 * @param propertyName
	 * @param value
	 * @return
	 */
	protected Criterion buildExpression(String restriction, String propertyName, Object value) {

		//Create expression
		Criterion expr;
		
		if(getTrimFilters() && value instanceof String){
			value = (Object)(value.toString().trim());			
		}
				
		if (restriction.equals("eq")) {
			expr = Restrictions.eq(propertyName, value);
		} else if (restriction.equals("ge")) {
			expr = Restrictions.ge(propertyName, value);
		} else if (restriction.equals("gt")) {
			expr = Restrictions.gt(propertyName, value);
		} else if (restriction.equals("le")) {
			expr = Restrictions.le(propertyName, value);
		} else if (restriction.equals("like")) {
			if (!value.toString().contains("%")) {
				if (fullLike) {
					value = "%" + value + "%";
				} else {
					value = value + "%";
				}
			}
			expr = Restrictions.like(propertyName, value).ignoreCase();
		} else if (restriction.equals("lt")) {
			expr = Restrictions.lt(propertyName, value);
		} else if (restriction.equals("ne")) {
			expr = Restrictions.or(Restrictions.isNull(propertyName), Restrictions.ne(propertyName, value));
		} else {
			throw new IllegalArgumentException("Invalid restriction: " + restriction);
		}

		return expr;
	}

	protected void removeExpression(String parentAlias, String expressionName) {
		removeExpression(parentAlias, expressionName, false);
	}

	/**
	 * Remove from entityCriteria the ExpressionEntry with parentAlias as alias and with expressionName that starts with expressionName
	 * @param parentAlias
	 * @param expressionName
	 */
	@SuppressWarnings("unchecked")
	protected void removeExpression(String parentAlias, String expressionName, boolean removeParentCriteria) {

		boolean expressionRemoved = false;
		boolean parentCriteriaNotContainOtherExpression = true;

		Criteria expressionCriteria = null;
		//Remove already present expression
		Iterator<CriterionEntry> expressionEntries = ((CriteriaImpl)entityCriteria).iterateExpressionEntries();
		while (expressionEntries.hasNext()) {
			CriterionEntry criterionEntry = expressionEntries.next();
			
			//Looking for just criterionEntry.getCriteria().getAlias() is not sufficient
			//we need also to look at criterionEntry.getCriteria().getParent().[.getParent()....].getAlias()
			boolean foundParentWithSameAlias = false;
			
			Criteria sub = criterionEntry.getCriteria();

			while(sub!=null){
				if(sub.getAlias().equals(parentAlias)){
					foundParentWithSameAlias=true;
					break;

				}else{
					if(sub instanceof Subcriteria){
						sub=((Subcriteria)sub).getParent(); // go up in the hierarchy
					}else{
						sub = null;	//no more parents
					}
				}
			}

			if (foundParentWithSameAlias) {
				if (criterionEntry.getCriteria().getAlias().equals(parentAlias) && criterionEntry.getCriterion().toString().startsWith(expressionName)) { //FIXME: based on toString: the only way....
					expressionCriteria = criterionEntry.getCriteria();
					expressionEntries.remove();
					expressionRemoved = true;
					//break; //cannot be breaked, otherwise I can fail to found other parentCriteria containing expression
				} else {
					parentCriteriaNotContainOtherExpression = false;
				}
			}
		}
		
		//devo cercare anche sulle projections
		if(parentCriteriaNotContainOtherExpression && entityProjections!=null){
			int i = 0;
			while(i<entityProjections.getLength()){
				String prj = entityProjections.getProjection(i).toString();
				if(prj.startsWith(parentAlias)){
					parentCriteriaNotContainOtherExpression=false;
					break;
				}else{
					i++;
				}
			}
		}

		if (removeParentCriteria && expressionRemoved && parentCriteriaNotContainOtherExpression) {
			/*
			 * if the criteria to which the removed expression belongs is a Subcriteria, then it has a parent criteria,
			 * that potentially needs to be removed
			 */
			if(expressionCriteria instanceof Subcriteria){
				removeParentSubCriteria(expressionCriteria);
			}
		}
	}
	
	/**
	 * Recursively remove from entityCriteria the parent Subcriteria of a removed expression (see above method)
	 * @param criteria - the Subcriteria that has to be removed, along with its own parents (if not used by other expressions)
	 */
	@SuppressWarnings("unchecked")
	private void removeParentSubCriteria(Criteria criteria){
		if(criteria instanceof Subcriteria){
			String parentAlias = criteria.getAlias();
			boolean parentCriteriaNotContainOtherExpression = true;
			Iterator<CriterionEntry> expressionEntries = ((CriteriaImpl)entityCriteria).iterateExpressionEntries();
			while (expressionEntries.hasNext()) {
				CriterionEntry criterionEntry = expressionEntries.next();
				
				//Looking for just criterionEntry.getCriteria().getAlias() is not sufficient
				//we need also to look at criterionEntry.getCriteria().getParent().[.getParent()....].getAlias()
				Criteria sub = criterionEntry.getCriteria();

				while(sub!=null){
					if(sub.getAlias().equals(parentAlias)){
						parentCriteriaNotContainOtherExpression = false;
						break;

					}else{
						if(sub instanceof Subcriteria){
							sub=((Subcriteria)sub).getParent(); // go up in the hierarchy
						}else{
							sub = null;	//no more parents
						}
					}
				}
			}
			
			if(parentCriteriaNotContainOtherExpression){
				removeSubCriteria(entityCriteria, parentAlias);
				removeParentSubCriteria(((Subcriteria)criteria).getParent());
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void removeOrdering(String parentAlias, String propertyName, Object ascending) {

		Iterator<OrderEntry> orderEntries = ((CriteriaImpl)entityCriteria).iterateOrderings();
		while (orderEntries.hasNext()) {
			OrderEntry orderEntry = orderEntries.next();

//			if (orderEntry.getOrder().toString().startsWith(parentAlias)) { //FIXME: based on toString: the only way....

				String order = orderEntry.getOrder().toString();

				String ascOrDesc = null;

				boolean toRemove = false;
				
				if (ascending == null) {

					if (order.startsWith(parentAlias + "." + propertyName)) { //FIXME: based on toString: the only way....
						toRemove = true;
					}
					
				} else {

					if(ascending.toString().equals(OrderBy.ascending.name()) || ascending.toString().equals(OrderBy.ascendingIgnoreCase.name())) {
						ascOrDesc = "asc";
					}

					if (ascending.toString().equals(OrderBy.descending.name()) || ascending.toString().equals(OrderBy.descendingIgnoreCase.name()) ) {
						ascOrDesc = "desc";
					}

					if (ascOrDesc != null && order.startsWith(parentAlias + "." + propertyName) && order.endsWith(ascOrDesc)) { //FIXME: based on toString: the only way....
						toRemove = true;
					}

				}
				
				if (toRemove) {
					orderEntries.remove();
					break;
				}
//			}
		}
	}

	private Boolean fullLike = false;
	private Boolean trimFilters = true; //sperimentale: proviamo a tenerlo abilitato sempre

	public Boolean getTrimFilters() {
		return trimFilters;
	}

	@ShowInDesigner(description="setTrimFilters")
	public void setTrimFilters(Boolean trimFilters) {
		this.trimFilters = trimFilters;
	}

	@ShowInDesigner(description="setFullLike")
	public void setFullLike( Boolean val) {
		fullLike = val;
	}

	public Boolean getFullLike() {
		return fullLike;
	}


	@ShowInDesigner(description="getLastOccurrenceWhere")
	public T getLastOccurrenceWhere(String attribute,  Object value) throws PersistenceException{
		if(getEntity()!=null){
			PhiRevisionEntity pre = ca.getLastRevisionOccurrenceWhere((BaseEntity)getEntity(), attribute, value);
			if(pre!=null){
				T temp = (T)ca.getRevision((BaseEntity)getEntity(), pre.getId());
				return temp;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}

	public class FilterMap extends HashMap<String, Object> implements Serializable {

		private static final long serialVersionUID = 1751606842344378989L;

		String filterOperator = null;

		public FilterMap(String filterOperator) {
			this.filterOperator = filterOperator;
		}

		@Override
		public Object get(Object key) {
			return super.get(key);
		}

		@Override
		public Object put(String key, Object value) {
			if(key.endsWith("patientEncounter") && value == null) {
				key += ".internalId";
				value = -1L;
			}
			if(key.endsWith("patient") && value == null) {
				key += ".internalId";
				value = -1L;
			}
			BaseAction.this.addCriteria(filterOperator, key, value);
			super.put(key, value);
			return "";
		}
		
		@Override
		public void putAll(Map map) {
			if(map!=null){
				for(Object key : map.keySet()){
					BaseAction.this.addCriteria(filterOperator, (String)key, map.get(key));
				}
			}
			super.putAll(map);
		}

		public void putOr(String key, Object... value) { // not using overloading because doesn't work from EL: https://issues.jboss.org/browse/JBSEAM-2301
			BaseAction.this.addCriteria(filterOperator, key, value);
			super.put(key, value);
		}

		@Override
		public Object remove(Object key) {
			BaseAction.this.addCriteria(filterOperator, key.toString(), null);
			return super.remove(key);
		}
	}

	class SelectList extends LinkedList<String> {

		private static final long serialVersionUID = 7239568758425523815L;

		@Override
		public boolean add(String select) {
			addCriteria(selectRestriction, select, selectRestriction);
			return super.add(select);
		}

		public void addMany(String... selectedColumns) { // not using overloading because doesn't work from EL: https://issues.jboss.org/browse/JBSEAM-2301
			for (String selectedCol : selectedColumns) {
				addCriteria(selectRestriction, selectedCol, selectRestriction);
				super.add(selectedCol);
			}
		}

	}

	
	/**
	 * lock/unlock current entity.
	 * if entity is not present, nothing is locked/unlocked.
	 * if locking is not possible, false is returned.
	 * for more action use LockerAnnotated  which instance and call Locker.java directly from process 
	 * (e.g. Locker.isLockedBy(className,internalId))
	 *
	 */
	
	@ShowInDesigner(description="lock current entity")
	public boolean lock() throws NamingException {
		if (getEntity() == null)
			return false;
		
		LockManager locker = (LockManager)Component.getInstance("Locker");
		return locker.lockIt(HibernateProxyHelper.getClassWithoutInitializingProxy(entity).getSimpleName(), ((BaseEntity)entity).getInternalId());
		
	}
	
	@ShowInDesigner(description="unlock current entity")
	public void unlock() throws NamingException {
		if (getEntity() == null)
			return;
		
		LockManager locker = (LockManager)Component.getInstance("Locker");
		locker.unlock(HibernateProxyHelper.getClassWithoutInitializingProxy(entity).getSimpleName(), ((BaseEntity)entity).getInternalId());
	}
	
	@ShowInDesigner(description="gets locking employee")
	public Object getLockingEmployee() throws NamingException {
		if (getEntity() == null)
			return null;
		
		LockManager locker = (LockManager)Component.getInstance("Locker");
		return locker.getLockingEmployee(HibernateProxyHelper.getClassWithoutInitializingProxy(entity).getSimpleName(), ((BaseEntity)entity).getInternalId());
	}

	@ShowInDesigner(description="gets locking employee role")
	public String getLockingRole() throws NamingException {
		if (getEntity() == null)
			return null;
		
		LockManager locker = (LockManager)Component.getInstance("Locker");
		return locker.getLockingRole(HibernateProxyHelper.getClassWithoutInitializingProxy(entity).getSimpleName(), ((BaseEntity)entity).getInternalId());
	}

	/**
	 * Used to remove "Salvataggio riuscito" on intermediate savings
	 */
	@ShowInDesigner(description="clears faces messages")
	public void removeFacesMessages(){
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc != null) {
			Iterator<FacesMessage> it = fc.getMessages();
			while ( it.hasNext() ) {
			    it.next();
			    it.remove();
			}
		}
	}

	private String nativeMysqlHistoryChangeInfo = 
			"	 select r.* from "+  
			"	 (select internal_id as internalId, revinfo.id,  revinfo.timestampDB, revinfo.username  "+ 
			"				from z_alfresco_document  "+
			"				join revinfo revinfo on revinfo.id = rev "+ 
			"	            where internal_id in (:internalIds)  "+
			"	            order by internal_id, revinfo.id desc) r "+
			"	inner join ( "+
			"		select p.internalId, max(p.timestampDB) as maxTime "+
			"	    from  (select internal_id as internalId, revinfo.id,  revinfo.timestampDB, revinfo.username "+ 
			"				from z_alfresco_document  "+
			"				join revinfo revinfo on revinfo.id = rev "+ 
			"	            where internal_id in (:internalIds)  "+
			"	            order by internal_id, revinfo.id desc) p "+
			"	    group by internalId) g "+
			"	ON r.internalId = g.internalId "+
			"   AND r.timestampDB = g.maxTime ";

	
	
	public void findLastChangeInfo (List<T> entities) {
		if (entities == null || entities.isEmpty()){
			return;
		}
		
		//List<Long> internalIds = new ArrayList<Long>();
		HashMap <Long,Integer> listSeq = new HashMap<Long, Integer>();
		for (int i=0; i <entities.size(); i++ ) {
			T e = entities.get(i);
			Long id = ((BaseEntity)e).getInternalId();
			listSeq.put(id, i);
		}
		
		List<Long> internalIds = new ArrayList<Long>(listSeq.keySet());
		String internalIdsS = StringUtils.join(internalIds, ","); 
		String sqlReplaced = nativeMysqlHistoryChangeInfo.replaceAll(":internalIds", internalIdsS);
		
		
		Query lastChangeInfoQry = ca.createHibernateNativeQuery(sqlReplaced);
		//lastChangeInfoQry.setParameter("internalIds",  new ArrayList<Long>(internalIds));
		
		lastChangeInfoQry.setResultTransformer(new SqlAliasToEntityMapResultTransformer());
		List<Map<String, Object>> lastChanges = lastChangeInfoQry.list();
		
		if (lastChanges != null) {
			for (Map objectChanges : lastChanges) {
				Long internalId = ((BigInteger)objectChanges.get("internalid")).longValue(); 
				Long lastChangeL = ((BigInteger)objectChanges.get("timestampdb")).longValue();
				String username =  (String)objectChanges.get("username");
				
				if (internalId != null && internalIds.contains(internalId)){
					T e = entities.get(listSeq.get(internalId));
					
					((BaseEntity)e).setEnversLastChangeBy(username);
					((BaseEntity)e).setEnversLastChangeDate(new Date(lastChangeL));
				}
			}
		}
		
	}
	
	//Execute the count of child object
	//Working only with select Restrictions
	public void readWithCount(String relationship) {
		
		String alias = relationship.substring(0,1).toUpperCase() + relationship.substring(1);
		Criteria subcriteria = findSubCriteria(alias);
		if (subcriteria == null) {
			entityCriteria.createAlias(relationship, alias,Criteria.LEFT_JOIN);
		}
		
		//Reset all projections, using configured select and related 'child' to be counted 
		ProjectionList pl = Projections.projectionList();
		pl.add(Projections.alias(Projections.count(alias+".internalId"), relationship+"Count"))
		  .add(Projections.alias(Projections.groupProperty("this.internalId"), "internalId"));
		
		List<String> projectionAliases = Arrays.asList(pl.getAliases());
		for (String s : select) {
			if (!projectionAliases.contains(s)){
				pl.add(Projections.alias(Projections.groupProperty("this."+s), s));
			}
		}
		
		entityCriteria.setProjection(pl); 
		entityCriteria.setResultTransformer(new AliasToEntityMapResultTransformer(entityClass));
		
		//FIXME: Hibernate resolve query using query alias (AS) of columns like _y1, _y3 in query clause.
		//these alias are not found in query, because restriction are defined without prefix this. 
		//to prefix this. to current criterion, can be done only with reflection.
		//criterion with this. prefix are removed (query can be repeated), only the current 'new' criterion are pre-fixed.
		//removing this. criterions, or prefix anywhere a this. is not tested overall, not working with distinct, in clause, and other.
		
		Iterator<CriterionEntry> criterions = ((CriteriaImpl)entityCriteria).iterateExpressionEntries();
		while (criterions.hasNext()) {
			CriterionEntry critEnt = criterions.next();
			Criterion crit = critEnt.getCriterion();
			
	        try {
				Field field = crit.getClass().getDeclaredField("propertyName");
				field.setAccessible(true);
		        String expressionPropertyName = (String)field.get(crit);
		        if (expressionPropertyName.startsWith("this.")) {
		        	criterions.remove();
		        }
		        else {
		        	field.set(crit, "this."+expressionPropertyName);
		        	//System.out.println("criterion TOSTRING: "+crit.toString()+ "  "+ expressionPropertyName + " changed with   this."+ expressionPropertyName);
		        }
			        
			} catch (Exception e) {
				log.error("error executing counted read:   "+e.getMessage());
				e.printStackTrace();
			} 
	        
	        
		}
		
		try {
			read();
		} catch (PhiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
		
		
	}
	public boolean setPropertyNull(String property) {
		return setPropertyNull(property, false);
	}

	public boolean setPropertyNull(String property, boolean returnValue) {
		T spd = getEntity();
		if (spd != null && property != null && !property.isEmpty()) {
			try {
				
				Method[] methods = entityClass.getMethods();
				for (Method method : methods) {
					String methodName = method.getName();
					String propMethod = "set" + 
							property.substring(0, 1).toUpperCase() + 
							(property.length()>1 ? property.substring(1) : "");
					if (propMethod.equals(methodName)) {
						method.invoke(spd, new Object[]{null});
						break;
					}
				}
			} catch (Exception e)  {
				log.error("Error executing method", e);
			}
		}
		return returnValue;
	}
	
    private void setObscurationLvl2Restrictions(Criteria currentCriteria, String[] elParts, int z) {

    	try {
    		int y = z+1;
    		String[] cvParts = new String[y+1];
    		for (int i=0; i<=z; i++) {
    			cvParts[i] = elParts[i];
    		}
    		cvParts[y] = "statusCode";

    		Criteria codeCriteria = findSubCriteria(generateAliasFromExpression(cvParts,y));

    		if (codeCriteria == null) {
    			codeCriteria = currentCriteria.createCriteria(cvParts[y], generateAliasFromExpression(cvParts,y), CriteriaSpecification.LEFT_JOIN);
    		}

    		Criterion restriction = Restrictions.or(
    				Restrictions.or(
    						Restrictions.isNull(generateAliasFromExpression(cvParts,y) + ".code"),
    						Restrictions.ne(generateAliasFromExpression(cvParts,y) + ".code", "completed")
    				),
    				Restrictions.and(
    						Restrictions.eq(generateAliasFromExpression(cvParts,y) + ".code", "completed"),
    						Restrictions.or(Restrictions.isNull(generateAliasFromExpression(cvParts,z) + ".reportObscurationLvl2"), Restrictions.ne(generateAliasFromExpression(cvParts,z) + ".reportObscurationLvl2", true))
    				)
    		);

    		Iterator<CriterionEntry> expressionEntries = ((CriteriaImpl)entityCriteria).iterateExpressionEntries();
    		while (expressionEntries.hasNext()) {
    			CriterionEntry criterionEntry = expressionEntries.next();
    			 /*
    			  * FIXME: Criterion.equals calls Object.equals: using to toString as WORKAROUND.
    			  * Needed a better solution... 
    			  */
    			if (restriction.toString().equals(criterionEntry.getCriterion().toString())) {
    				return;
    			}
    		}

    		codeCriteria.add(restriction);
    	} catch (Exception e) {
    		log.error("Error adding Obscuration level 2 restriction", e);
		}
    }
    
    protected boolean useObscurationLvl2() throws PhiException {
    	boolean result = true;
    	
    	// OBSCURATION LVL 2 BY PARAMETER
    	Boolean useObscurationLvl2 = (Boolean)ParameterManager.instance().getParameter(USE_OBSCURATION_PARAM, "value");
    	result &= useObscurationLvl2 != null && useObscurationLvl2;
    	
    	// OBSCURATION LVL 2 BY ROLE
    	EmployeeRole empRole = UserBean.instance().getCurrentSystemUser();
    	if (empRole != null) {
    		String empRoleCode = empRole.getCode().getCode();
    		result &= !"45".equals(empRoleCode); // EMPLOYEE ROLE 45 = EMERGENCY PROFILE
    	}
    	
    	return result;
    }
}