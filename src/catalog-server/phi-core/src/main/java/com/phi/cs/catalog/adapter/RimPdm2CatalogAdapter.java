package com.phi.cs.catalog.adapter;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.ejb.QueryImpl;
import org.hibernate.engine.CollectionEntry;
import org.hibernate.engine.EntityEntry;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.engine.StatefulPersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.util.IdentityMap;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.phi.cs.catalog.adapter.nestedset.NestedSetNode;
import com.phi.cs.catalog.adapter.nestedset.query.NestedSetNodeWrapper;
import com.phi.cs.catalog.adapter.nestedset.query.NestedSetQueryBuilder;
import com.phi.cs.catalog.adapter.nestedset.query.NestedSetResultTransformer;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.PhiRevisionEntity;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.II;
import com.phi.entities.tracedEntity.TracedEntity;
import com.phi.security.UserBean;

/** 
 * This Adapter interacts with the RIM database
 */
@Name("rimPdm2CA")
@Stateful
public class RimPdm2CatalogAdapter implements CatalogAdapter, Serializable {

	private static final long serialVersionUID = 2023685499269991189L;

	//private final static Logger baseActionlogger = Logger.getLogger(BaseAction.class);
	private final static Logger log = Logger.getLogger(RimPdm2CatalogAdapter.class);
	
//	@In(value="rimEntityManagerPDM2")
	@PersistenceContext(type=PersistenceContextType.EXTENDED, unitName="rimEntityManagerPDM2")
	private EntityManager em;
	
//	@Resource
//	private transient SessionContext sctx;
	
	private boolean flushNeeded = false;

	
	@Create
	public void init() {
//		em.setFlushMode(FlushModeType.COMMIT);
		((Session)em.getDelegate()).setFlushMode(FlushMode.MANUAL);
		String publicKey=System.getProperty("phi.key.public");
		String privateKey=System.getProperty("phi.key.private");

		if(RepositoryManager.instance().isUsing_EncryptPatientData() && publicKey!=null && privateKey!=null){
			em.createNativeQuery("{call set_crypt_context(?, ?)}")
			.setParameter(1, publicKey)
			.setParameter(2, privateKey)
			.executeUpdate();
		}
	}
	
	/**
	 * Check db connection
	 * FIXME: change query for mySql and SqlServer
	 * @return true if connection is ok
	 */
	public boolean checkDb() {
		try {
			//FIXME: extract check-valid-connection-sql from hibernate properties and use correct sql for current db!
//			Properties hibProb = ((SessionFactoryImpl)((Session)em.getDelegate()).getSessionFactory()).getProperties();
			
			BigDecimal val = (BigDecimal)createNativeQuery("select 1 from dual").getSingleResult();
			
			if (val.equals(BigDecimal.ONE)) {
				return true;
			} else {
				return false;
			}
		} catch (PersistenceException e) {
			log.error("Error checking db connection", e);
			return false;
		}
	}
	
	/**
	 *  Query by example
	 */
	public List read(Object example) {
    	// get the native hibernate session
    	Session session = ((Session)em.getDelegate());
    	// create an example, exclude creationDate
    	Example ex = Example.create(example);
    	
    	ex.excludeProperty("creationDate");
    	
    	ex.enableLike();
    	
    	// create criteria based on the customer example
    	Criteria criteria = session.createCriteria(example.getClass()).add(ex);

    	return criteria.list();
	}
	
	public <T extends NestedSetNode<T>> List<NestedSetNodeWrapper<T>> readNestedSet(T nsNode) throws PersistenceException {
		
		try {
		
//			NestedSetQueryBuilder nsQueryBuilder = new NestedSetQueryBuilder(nsNode,false,true);
			NestedSetQueryBuilder nsQueryBuilder = new NestedSetQueryBuilder(nsNode);
			
			//FIXME: using hibernate query instead of jpa query to allow setResultTransformer
			org.hibernate.Query nsQuery = ((Session)em.getDelegate()).createQuery(nsQueryBuilder.getSimpleQuery());
			
			// Bind parameters
			nsQuery.setParameter("nsThread", nsNode.getNodeInfo().getNsThread());
			nsQuery.setParameter("nsLeft", nsNode.getNodeInfo().getNsLeft());
			nsQuery.setParameter("nsRight", nsNode.getNodeInfo().getNsRight());
			
			NestedSetNodeWrapper<T> startNodeWrapper = new NestedSetNodeWrapper<T>(nsNode);
			
			nsQuery.setResultTransformer(new NestedSetResultTransformer<T>(startNodeWrapper, 10));
	
			nsQuery.list();
			
			return startNodeWrapper.getWrappedChildren();
		
		} catch (Exception e) {
			throw new PersistenceException(ErrorConstants.PERSISTENCE_GENERIC_ERR_INTERNAL_MSG, e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
		}
	}
	
	/**
	 *  Create criteria
	 */
	public Criteria createCriteria(Class clazz) {
    	// get the native hibernate session
    	Session session = ((Session)em.getDelegate());
    	//create criteria
    	return session.createCriteria(clazz);
	}
	
	/**
	 *  Create JPA Query
	 */
	public Query createQuery(String hql) {
    	return em.createQuery(hql);
	}
	
	/**
	 *  Create Hibernate Query
	 *  Used this to set a ResultTransformer
	 */
	public org.hibernate.Query createHibernateQuery(String hql) {
    	return ((Session)em.getDelegate()).createQuery(hql);
	}
	
	/**
	 *  Create Envers Query
	 */
	public org.hibernate.envers.query.AuditQuery createAuditQuery(Class clazz) {
		AuditReader auditReader = AuditReaderFactory.get(em);
		AuditQuery query = auditReader.createQuery().forRevisionsOfEntity(clazz, false, true);
		return query;
	}
	
	/**
	 * Save or update an object into db.
	 */
	public Object create(Object entity) throws PersistenceException {
//		long t = System.currentTimeMillis();
		Object returned = null;

		try {
			if( entity != null ) {

				//If entity implements TracedEntity fill tracing fields
				if (entity instanceof BaseEntity && entity instanceof TracedEntity) {
					
					TracedEntity tracedEntity = (TracedEntity)entity;
					
					UserBean ub = UserBean.instance();
					
					if (((BaseEntity)entity).getInternalId() == 0) { //CREATE
						tracedEntity.setCreatedByLocation(ub.getRemoteHost());
					} else { //UPDATE
						tracedEntity.setModifiedBy(ub.getUsername());
						tracedEntity.setModificationDate(new Date());
						tracedEntity.setModifiedByLocation(ub.getRemoteHost());
					}
				}

				em.persist(entity);
				
				// versionNumber++ of root class of the data graph is useful to simplify the history reading. 
				// In this way to obtain a list of revision of a data graph is enough to take revisions of the root entity.
				if (((Session)em.getDelegate()).isDirty()) 
					if (entity instanceof BaseEntity && ((BaseEntity)entity).getVersion() >= 0 )
						((BaseEntity)entity).setVersion(((BaseEntity)entity).getVersion() + 1);

				returned = entity;
				
//				Object id = getIdentifier(entity);
				
//				baseActionlogger.info("[cid="+Conversation.instance().getId()+"|"+ub.getUsername()+"] Created object of Class "+
//						entity.getClass().getName()+" with internal_id: "+((id != null) ? id.toString() : "N.A.")+
//						" in: "+(System.currentTimeMillis() -t)+" ms ");
			} else {
				throw new PersistenceException(ErrorConstants.PERSISTENCE_SAVE_ERR_INTERNAL_MSG, ErrorConstants.PERSISTENCE_SAVE_ERR_CODE);
			}
		} catch (Exception e) {
			throw new PersistenceException(ErrorConstants.PERSISTENCE_SAVE_ERR_INTERNAL_MSG,e, ErrorConstants.PERSISTENCE_SAVE_ERR_CODE);
		}
		flushNeeded = true;
		return returned;
	}
	
	//Draft to check dirty session, and list object in hibernate cache.
	public Object isSessionDirty() throws Exception {
		//return type is object, for future improvement: return list of dirty objects.
		
		Session s = ((Session)em.getDelegate()); 
		//log.info("session isDirty:"+s.isDirty());
		return s.isDirty();
		
		
		/* THIS code try to list object managed by Hibernate, and try to find which one is dirty. Not completed.
		SessionImpl sImpl = (SessionImpl) getField(s, "delegate");
		StatefulPersistenceContext pc = (StatefulPersistenceContext) getField(sImpl, "persistenceContext");
		Map entries = pc.getCollectionEntries();
		
		Set entrySet = entries.entrySet();
		Iterator iter = entrySet.iterator();
		
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			CollectionEntry collectionEntry = (CollectionEntry) me.getValue();
			
			log.info(" collEnt:"+collectionEntry +" snapshot:"+collectionEntry.getSnapshot()  +" ignore:"+collectionEntry.isIgnore() +" processed:"+ collectionEntry.isProcessed());
		}
		
		Iterator contextIterator = new LazyIterator( pc.getEntitiesByKey() );
		while (contextIterator.hasNext()) {
            Object entity = contextIterator.next();
			log.info(entity);
		}
		*/
		
		
	}

	private Object getField(Object instance, String name) throws Exception {
		Field f = instance.getClass().getDeclaredField(name);
		f.setAccessible(true);      
		return f.get(instance);
	}
	
	/**
	 * Simply persist an entity.
	 * Doesen't set flushNeeded to true as create. So at the end of the process entity will be flushed only if also a create is called.
	 * @param entity
	 * @throws PersistenceException
	 */
	public void persist(Object entity) throws PersistenceException {
		try {
				
			em.persist(entity);

		} catch (Exception e) {
			throw new PersistenceException(ErrorConstants.PERSISTENCE_SAVE_ERR_INTERNAL_MSG,e, ErrorConstants.PERSISTENCE_SAVE_ERR_CODE);
		}
	}

	/**
	 * Used to flush the Session in a long running conversation context
	 * @throws PersistenceException
	 */
	public boolean flushSession() throws PersistenceException {
		long t = System.currentTimeMillis();
		try {
			if (flushNeeded) {
				//this.printPersistence();
				em.flush();
				
				/*
				 * To see this message, add a <rich:messages tag in home>errorMenu
				 */
				FunctionsBean fn = FunctionsBean.instance();
				FacesMessage msg = new FacesMessage(fn.getStaticTranslation("saveOK"));
				msg.setSeverity(FacesMessage.SEVERITY_INFO);
				FacesContext fc = FacesContext.getCurrentInstance();
				if (fc != null) {
					fc.addMessage(null, msg);
				}
				log.info("[cid="+Conversation.instance().getId()+"] FLUSHED HIBERNATE SESSION in " + (System.currentTimeMillis() -t) + " ms");
				
				flushNeeded = false;
				return true;
			} else {
				return false;
			} 
		} catch (Exception e ) {
//			sctx.setRollbackOnly();
			throw new PersistenceException(ErrorConstants.PERSISTENCE_FLUSH_ERR_INTERNAL_MSG,e, ErrorConstants.PERSISTENCE_FLUSH_ERR_CODE);
		}
	}

	/**
	 * Load an object from the db
	 */
	public Object get(String entityName, Serializable id) {
		Class<?> clazz;
		Object found = null;
		try {
			clazz = Class.forName(entityName);
			found = get(clazz, id);
		} catch (Exception e) {
			log.error("Error getting " + entityName + " with id "+id, e);
		}
		return found;
	}
	
	public <T> T get(Class<T> clazz, Serializable id) {
		/*
		 * occasionally, em.find=>session.get returns element in session
		 * to ensure loading from datasource, use session.load
		 */
		return (T) ((Session)em.getDelegate()).load(clazz, id);
	}
	
	public Object get(Class clazz, Serializable id, lockType locktype) {
		LockMode lm;
		if (locktype == locktype.UPDATE) {
			lm = LockMode.UPGRADE;
		} else {
			lm = LockMode.NONE;
		}
		return ((Session)em.getDelegate()).get(clazz, id, lm);
	}
	
	/**
	 * Load a proxy from the db
	 * REMBER: entityName is the class name including all the package.
	 * e.g.: com.phi.entities.role.Patient
	 */
	public Object load(String entityName, Serializable id) {
		Class<?> clazz;
		Object proxy = null;
		try {
			clazz = Class.forName(entityName);
			proxy = load(clazz, id);
		} catch (Exception e) {
			log.error("Error getting " + entityName + " with id "+id, e);
		}
		return proxy;
	}
	
	public <T> T load(Class<T> clazz, Serializable id) {
		return em.getReference(clazz, id);
	}
	
	/**
	 * Delete an object
	 */
	public void delete(Object obj) throws PersistenceException {
//		long t = System.currentTimeMillis();
		try {
		
			em.remove(obj);
			flushNeeded = true;
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(), e, ErrorConstants.APPLICATION_DELETE_OBJECT_ERR_CODE);
		}
	}
	
	/**
	 *  Merge an object
	 */
	public Object update(Object obj) throws PersistenceException {
		Object mergedObj;
		try {
			mergedObj = (BaseEntity)em.merge(obj);
			flushNeeded = true;
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(), e, ErrorConstants.APPLICATION_UPDATE_OBJECT_ERR_CODE);
		}
		return mergedObj;
	}

	/**
	 * Refresh an object, if it is loaded in another session, 
	 * cannot be saved directly without refresh
	 */
	public void refresh(Object entity) {
		em.refresh(entity);
//		((Session)em.getDelegate()).refresh(entity);
	}
	
	public boolean contains(Object entity){
		return em.contains(entity);
	}
	
	public void refreshIfNeeded(Object entity) {
		if (em.contains(entity)) 
			em.refresh(entity);
//		if (((Session)em.getDelegate()).contains(entity))
//			((Session)em.getDelegate()).refresh(entity);
	}
	
	public void refreshSessionObjectIfNeeded(Object entity){
		if (((Session)em.getDelegate()).contains(entity))
		((Session)em.getDelegate()).refresh(entity); 
	}
	
	/**
	 * Remove object from first level cache (session)
	 */
	public void evict(Object entity) {
		((Session)em.getDelegate()).evict(entity);
	}
	
	public String dumpHibernateSession() {
	    try { 
	    	Session s = ((Session)em.getDelegate()); 
	        SessionImplementor sessionImpl = (SessionImplementor) getField( s, "delegate");
	        StatefulPersistenceContext pc = (StatefulPersistenceContext) getField(sessionImpl, "persistenceContext");
	        IdentityMap entities = (IdentityMap) getField(pc, "entityEntries");
	        log.warn(" ====   entities dump: ===="+entities);
	        
	        //FIXME: ugly json build
	        String ret = "{\"entities\":[";
	        
	        Set entrySet = entities.entrySet();
			Iterator iter = entrySet.iterator();
			
			while ( iter.hasNext() ) {
				Map.Entry me = (Map.Entry) iter.next();
				Object val = me.getValue();
				if (val instanceof CollectionEntry) {
					CollectionEntry collectionEntry = (CollectionEntry) val;
					String item = " \"collEnt\":\""+collectionEntry +"\", \"snapshot\":\""+collectionEntry.getSnapshot()  +"\", \"ignore\":\""+collectionEntry.isIgnore() +"\", \"processed\":\""+ collectionEntry.isProcessed()+"\"";
					log.debug(item);
					ret+="{"+item+"},";
				}
				else if (val instanceof EntityEntry) {
					EntityEntry entityEntry = (EntityEntry) val;
					String item = "\"class\": \""+entityEntry.getEntityName()+"\",  \"id\": \""+entityEntry.getId()+ "\",  \"existsInDb\": \""+entityEntry.isExistsInDatabase()+ "\",  \"status\": \""+entityEntry.getStatus()+"\"";
					log.debug(item);
					ret+="{"+item+"},";
				}
			}
			
			if (ret.endsWith(",")) {
				ret=ret.substring(0,ret.length()-1);
			}
			return ret+"]}";
			
			
//			Iterator contextIterator = new LazyIterator( pc.getEntitiesByKey() );
//			while (contextIterator.hasNext()) {
//	            Object entity = contextIterator.next();
//				log.info(entity);
//			}
	        	
	    } catch (Exception e) {
	        log.error(e);
	        
	    }
	    return "";
	}
	
//	public void setSessionCacheMode(CacheMode cacheMode) {
//		Session s =((Session)em.getDelegate()); 
//		s.setCacheMode(cacheMode); 
//		System.out.println("done");
//	}
	
	// Envers audit methods
	
	////////Not more needed, use only method based on Class Name for history
//	/**
//	 * Get a list containing all revisions of entity with name entityName and id id.
//	 */
//	public List<BaseEntity> getHistoryof(String entityName, Serializable id) throws PersistenceException {
//		try {
//			String clsName = ((Session)em.getDelegate()).getSessionFactory().getClassMetadata(entityName).getMappedClass(EntityMode.POJO).getName();
//			Class<BaseEntity> entityClass = (Class<BaseEntity>)Class.forName(clsName);
//			
//			return getHistoryof(entityClass, /*entityName,*/ id);
//			
//		} catch (Exception e) {
//			throw new PersistenceException(e.getMessage(), e, ErrorConstants.APPLICATION_READ_OBJECT_ERR_CODE);
//		}
//	}
	
	
	public <T> List<T> getHistoryof(Class<T> entityClass, /*String entityName,*/ Serializable id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getHistoryof(entityClass, /*entityName,*/ id, null);
	}
	
	
	/**
	 * Get a list containing all revisions of class of type entityClass and id id.
	 * entityClass: is the class where the history is retrieved
	 * id: the id of the object for which the history is required
	 * changedAttributeFilter:  the result of history can be filtered for the change of only some attribute.
	 * if null all the history is returned
	 * if not null, only the history entries where the provided attributes are changed is returned. 
	 * 
	 * The returned history does not contains the current object revision, only the history.
	 * The current is added to the history element only if includeCurrent is true.
	 * 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	
	
	public <T> List<T> getHistoryof(Class<T> entityClass, /*String entityName,*/ Serializable id, String[] changedAttributeFilter) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		AuditReader auditReader = AuditReaderFactory.get(em);
		List<T> history = new ArrayList<T>(); 

		List<Number> totRevisions = getRevisionNumbers(entityClass, /*entityName,*/ id, auditReader); 
	
		Collections.sort((List)totRevisions);
		
		HashMap <String,Object> attributePreviousValue = new HashMap<String, Object>();
		
		for (Number rev : totRevisions) {
			T objAtRev = getRevision(entityClass, /*entityName,*/ id, rev, auditReader); 
			PhiRevisionEntity revision = get(PhiRevisionEntity.class, rev);
			if (objAtRev instanceof BaseEntity) {
				((BaseEntity)objAtRev).setRevision(revision);
			
			}else if (objAtRev instanceof CodeValue) {
				((CodeValue)objAtRev).setRevision(revision);
			
			}else{
				//objAtRev can be null occasionally..better avoiding red screens
				continue;
			}
			if (changedAttributeFilter == null || changedAttributeFilter.length == 0 ) {
				history.add(objAtRev);
			}
			else {
				//changedAttributeFilter are not null, so need to cycle on attribute filter list, 
				//and look if the current value and the previous are different, 
				//to decide if add it to returned list of history elements.
				
				boolean alreadyInsertedRevision = false;
				for (String attributeFilter : changedAttributeFilter) {
					Object revAttrValue = null;
					try {
						revAttrValue = PropertyUtils.getProperty(objAtRev, attributeFilter);
					} catch (Exception e) {
						log.warn("Warning: " + objAtRev + " revision: " + revision.getId() + " doesen't contain property " + attributeFilter);
					}
					if (!alreadyInsertedRevision) {
						//alredyInsertedRevision check to above insert same revision multiple time one for each attribute.
						
						if (!attributePreviousValue.containsKey(attributeFilter)) {
							//this is the first instance of the object in the history, added. 
							history.add(objAtRev);
							alreadyInsertedRevision=true;
						}
						else {
							Object previousValue =  attributePreviousValue.get(attributeFilter);
							if  ((previousValue == null && revAttrValue != null) || (previousValue!=null && !previousValue.equals(revAttrValue))) {
								//add to history only if the current revision value, and the previous are different.
								history.add(objAtRev);
								alreadyInsertedRevision=true;
							}
						}
					}
					//update in any case the previus value.
					attributePreviousValue.put(attributeFilter, revAttrValue);
					
				}
			}
			
		}
		
		log.info("[cid="+Conversation.instance().getId()+"] Found " + history.size() + " history of " + entityClass.getName() + " id: " + id);	
		
		return history;
	}

	/*
	 * get the N-th revision. If it is not present, return the last one available (log error).
	 * WARN: the history could be corrupted: 
	 * object in table_xx has for example version 10 (current). In the history should be present 10 rows.
	 * The hisotry is wrong if in z_table_xx are are less revision than 10.   
	 *  
	 */
	public PhiRevisionEntity getNthRevision(BaseEntity rimObject, long version) {
		int nth =  (int) version; 
//		AuditReader auditReader = AuditReaderFactory.get(em);
		Serializable id =  rimObject.getInternalId();
		Class<BaseEntity> entityClass= (Class<BaseEntity>)rimObject.getClass();
		
		if (entityClass != null) {
			
			List<Number> revisions = getRevisionNumbers(entityClass, id);
			if (nth > revisions.size()) {
				log.error("HISTORY CORRUPTED: number of version mismatch (asked "+nth+", available only "+revisions.size()+" on "+entityClass.getName()+" id:"+id);
				nth = revisions.size(); //take the lastone.
			}
			Number rev = revisions.get(nth-1);
			if (rev != null)
				return  get(PhiRevisionEntity.class, rev);
			
//			Object[] revision = (Object[])auditReader.createQuery().forRevisionsOfEntity(entityClass, false, true)
//					.add(AuditEntity.id().eq(id))
//					.setFirstResult(nth-1)
//					.getSingleResult();
//			if (revision != null && revision.length == 3 && revision[1] != null )
//				return (PhiRevisionEntity)revision[1];// get(PhiRevisionEntity.class, revision);
			
		}
		return null;
	}
	
	/** 
	 * get the number of version for an entity, where for the first time the attribute is equal to the specified value
	 */
	public PhiRevisionEntity getFirstRevisionOccurenceWhere (BaseEntity rimObject, String attribute,  Object value) throws PersistenceException {
		
		
		AuditReader auditReader = AuditReaderFactory.get(em);
		Serializable id =  rimObject.getInternalId();
		
//		String entityName = rimObject.getClass().getName(); 
//		String clsName = ((Session)em.getDelegate()).getSessionFactory().getClassMetadata(entityName).getMappedClass(EntityMode.POJO).getName();
//		Class<RimObject> entityClass=null;
//		try {
//			entityClass = (Class<RimObject>)Class.forName(clsName);
//		} catch (ClassNotFoundException e) {
//			throw new PersistenceException(e.getMessage(), e, ErrorConstants.APPLICATION_READ_OBJECT_ERR_CODE);
//		}

		Class<BaseEntity> entityClass= (Class<BaseEntity>)rimObject.getClass();
		
		if (entityClass != null) {
//			return (Number) auditReader.createQuery().forEntitiesAtRevision(entityClass, (Number)id)
//				.set
//				.add(AuditEntity.relatedId(attribute).eq(value))
//				.setProjection(AuditEntity.revisionNumber().min());
			
			Number revision = (Number) auditReader.createQuery().forRevisionsOfEntity(entityClass, false, true)
			.addProjection(AuditEntity.revisionNumber().min())
			.add(AuditEntity.id().eq(id))
			.add(AuditEntity.property(attribute).eq(value))
			.getSingleResult();
			
			if (revision != null)
				return  get(PhiRevisionEntity.class, revision);
		}
		return null;
	}
	
	////////Not more needed, use only method based on Class Name for history
//	/**
//	 * Get revision numbers of entity with name entityName and id id.
//	 */
//	public List<Number> getRevisionNumbers(String entityName, Serializable id) throws PersistenceException {
//		try {
//			String clsName = ((Session)em.getDelegate()).getSessionFactory().getClassMetadata(entityName).getMappedClass(EntityMode.POJO).getName();
//			Class<?> entityClass = Class.forName(clsName);
//			
//			return getRevisionNumbers(entityClass, entityName, id);
//			
//		} catch (Exception e) {
//			throw new PersistenceException(e.getMessage(), e, ErrorConstants.APPLICATION_READ_OBJECT_ERR_CODE);
//		}
//	}
	
	/**
	 * Get revision numbers of class of type entityClass and id id.
	 */
	public List<Number> getRevisionNumbers(Class<?> entityClass, /*String entityName,*/ Serializable id) {
		AuditReader auditReader = AuditReaderFactory.get(em);
		return getRevisionNumbers(entityClass, /*entityName,*/ id, auditReader);
	}
	
	private List<Number> getRevisionNumbers(Class<?> entityClass, /*String entityName,*/ Serializable id, AuditReader auditReader) {
		return auditReader.getRevisions(entityClass, /*entityName,*/ id);
	}
	
	/**
	 * Return the revision number specified, of a given rimobject
	 */
	public Object getRevision (BaseEntity rimObject, Number revN) throws PersistenceException  {
//		String entityName = rimObject.getClass().getName(); 
		Class entityClass = rimObject.getClass(); 
		Serializable id =  rimObject.getInternalId();
		//return getRevision( entityName,  id,  revN);
		return getRevision( entityClass,  id,  revN);
	}
	
	
//	//////Not more needed, use only method based on Class Name for history
//	/**
//	 * Get a single revision of entity with name entityName and id id.
//	 */
//	//public Object getRevision(String entityName, Serializable id, Number revN) throws PersistenceException {
//	public Object getRevision(Class<?> entityClass, Serializable id, Number revN) throws PersistenceException {
//		try {
////			String clsName = ((Session)em.getDelegate()).getSessionFactory().getClassMetadata(entityName).getMappedClass(EntityMode.POJO).getName();
////			Class<?> entityClass = Class.forName(clsName);
//			
//			return getRevision(entityClass, /*entityName,*/ id, revN);
//			
//		} catch (Exception e) {
//			throw new PersistenceException(e.getMessage(), e, ErrorConstants.APPLICATION_READ_OBJECT_ERR_CODE);
//		}
//	}
	
	/**
	 * Get a single revision of class of type entityClass and id id.
	 */
	public <T> T getRevision(Class<T> entityClass, /*String entityName,*/ Serializable id, Number revN) {
		AuditReader auditReader = AuditReaderFactory.get(em);
		return getRevision(entityClass, /*entityName,*/ id, revN, auditReader);
	}
	
	private <T> T getRevision(Class<T> entityClass, /*String entityName,*/ Serializable id, Number revN, AuditReader auditReader) {
		return auditReader.find(entityClass, /*entityName,*/ id, revN);
	}
	
	public Serializable getIdentifier(Object entity) {
		if (em.contains(entity)) {
			Session session = ((Session)em.getDelegate());
			return session.getIdentifier(entity);
		}
		else {
			return 0L;
		}
	}
	
	
	/**
	 * Get revisionType of a given revision of a specific object.
	 */
	public Number getRevisionType (BaseEntity rimObject, Number revN) {
		AuditReader reader = AuditReaderFactory.get(em);
		Serializable id =  rimObject.getInternalId();
		Class<BaseEntity> entityClass= (Class<BaseEntity>)rimObject.getClass();
		AuditQuery q = reader
	                .createQuery()
	                .forEntitiesAtRevision(entityClass, revN)
	                .add(AuditEntity.id().eq(id))
	                //.addProjection(AuditEntity.revisionNumber())
	                .addProjection(AuditEntity.revisionType());
	                
	    List<RevisionType> revisionNumberAndType = q.getResultList();
	    if (revisionNumberAndType != null && revisionNumberAndType.size() == 1){
	    	int ret = revisionNumberAndType.get(0).ordinal(); 
	    	return ret;
	    	
	    }
		return null;
	}

	//Query methods
	
	/**
	 * Execute an hql query
	 */
	public List<?> executeHQL(String query) throws PersistenceException{
		return executePagedHQL(query, null, null, null);
	}
	
	public List<?> executeHQLwithParameters(String query,Map<String, Object> pars) throws PersistenceException{
		return executePagedHQL(query, pars, null, null);
	}

	public List<?> executeLimitedHQL(String query, Map<String, Object> pars, int maxResults) throws PersistenceException{
		return executePagedHQL(query, pars, null, maxResults);
	}

	/**
	 * Execute an hql query, return maxResults n. of results from offset firstResult
	 */
	public List<?> executePagedHQL(String query, Map<String,Object> pars, Integer firstResult, Integer maxResults) throws PersistenceException{
		long t = System.currentTimeMillis();
		List<?> result = null;
		try{
			Query qry = em.createQuery(query);
			
			if(pars!=null){
				Object[] parNames = pars.keySet().toArray();
	
				for(int p=0;p<parNames.length;p++){
					String key = (String)parNames[p];
					qry.setParameter(key, pars.get(key));
				}
			}
			if (firstResult!= null)
				qry.setFirstResult(firstResult);
			
			if (maxResults!= null)
				qry.setMaxResults(maxResults);
			
			
			result = qry.getResultList();
			
			log.info("[cid="+Conversation.instance().getId()+"] "+
					"Found " + result.size() + " in " + (System.currentTimeMillis()-t) + " ms [" + query + "]");	
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage() + " Error executing: " + query,e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
		}

		return result;
	}
	
	/**
	 * Execute update hql
	 */
	public int executeUpdateHql(String query, Map<String, Object> pars) throws PersistenceException {
		int nOfUpdatedEntities = 0;
		try {
			Query qry = em.createQuery(query);
			
			if(pars!=null){
				Object[] parNames = pars.keySet().toArray();
				for(int p=0;p<parNames.length;p++){
					String key = (String)parNames[p];
					qry.setParameter(key, pars.get(key));
				}
			}
			
			nOfUpdatedEntities = qry.executeUpdate();
			
			log.debug("executeUpdateHql: " + query.toString());
			flushNeeded = true;
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(),e, ErrorConstants.PERSISTENCE_SELECT_ITEM_ERR_INTERNAL_MSG);
		}

		return nOfUpdatedEntities;
	}

	/**
	 * Named SQL or HQL queries can be defined in the mapping document
	 * And called by these two methods
	 */
	public List<?> executeNamedQuery(String name) throws PersistenceException {
		return executeNamedQuery(name, null);
	}
	
	public List<?> executeNamedQuery(String name, Map<String,Object> parameters) throws PersistenceException {
		
		long t = System.currentTimeMillis();
		Query qry = null;
		
		try {
			qry = em.createNamedQuery(name);
			
			if (parameters != null){
				for(String key : parameters.keySet())
					qry.setParameter(key, parameters.get(key));
			}

			List<?> result = qry.getResultList();
			
			String qryString = qry.toString();
			if (qry instanceof QueryImpl) {
				qryString = ((QueryImpl)qry).getHibernateQuery().getQueryString();
			}
			
			log.info("[cid="+Conversation.instance().getId()+"] "+
					"Found " + result.size() + " in " + (System.currentTimeMillis()-t) + " ms [" + qryString + "]");
			
			return result;
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage() + " Error executing: " + qry, e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
		}
	}
	
	public int executeUpdateNamedQuery(String name, Map<String,Object> parameters) throws PersistenceException {
		
		long t = System.currentTimeMillis();
		Query qry = null;
		
		try {
			qry = em.createNamedQuery(name);
			
			if (parameters != null){
				for(String key : parameters.keySet())
					qry.setParameter(key, parameters.get(key));
			}

			int result = qry.executeUpdate();
			
			String qryString = qry.toString();
			if (qry instanceof QueryImpl) {
				qryString = ((QueryImpl)qry).getHibernateQuery().getQueryString();
			}
			
			log.info("[cid="+Conversation.instance().getId()+"] "+
					"Updated " + result + " rows in " + (System.currentTimeMillis()-t) + " ms [" + qryString + "]");
			
			
			return result;
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage() + " Error executing: " + qry, e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
		}
	}
	
	public Query createNativeQuery(String sql) throws PersistenceException {
		Query qry = null;
		
		try {
			qry = em.createNativeQuery(sql);
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(),e, ErrorConstants.PERSISTENCE_SQLFIXEDQUERY_ERR_INTERNAL_MSG);
		}
		return qry;
	}
	
	/**
	 *  Create Hibernate Native Query
	 *  Used this to set a ResultTransformer
	 */
	public SQLQuery createHibernateNativeQuery(String sql) {
    	return ((Session)em.getDelegate()).createSQLQuery(sql);
	}
	
	/**
	 * Find previous Rim Object with same Status code and Id4PHI
	 * @param rimObj
	 * @return
	 * @throws PersistenceException
	 */
	public BaseEntity findPreviusObjectWithSameIdAndStatusCode(String rimObjClassName, II Id4Phi, String statusCode) throws PersistenceException {
		try{
			Query query = em.createQuery("SELECT DISTINCT rimObj FROM " + rimObjClassName + " rimObj JOIN rimObj.id id JOIN rimObj.statusCode WHERE id.root = :root AND id.extension = :extension AND rimObj.statusCode.code = :statusCode order by rimObj.creationDate desc" );
			query.setParameter("root", Id4Phi.getRoot());
			query.setParameter("extension", Id4Phi.getExtension());
			query.setParameter("statusCode", statusCode);
			
			query.setMaxResults(1);
			
			return (BaseEntity)query.getSingleResult();
			
		} catch (Exception e ) {
			throw new PersistenceException(ErrorConstants.PERSISTENCE_GENERIC_ERR_INTERNAL_MSG,e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
		}
	}
	
	/**
	 * Find an entity by id4Phi
	 */
	public BaseEntity findById(String rimObjClassName, II Id4Phi) throws PersistenceException {
		try{
			Query query = em.createQuery("SELECT rimObj FROM " + rimObjClassName + " rimObj JOIN rimObj.id id WHERE rimObj.isActive = true AND id.root = :root AND id.extension = :extension" );
			query.setParameter("root", Id4Phi.getRoot());
			query.setParameter("extension", Id4Phi.getExtension());
			
			query.setMaxResults(1);
			
			return (BaseEntity)query.getSingleResult();
			
		} catch (Exception e ) {
			throw new PersistenceException(ErrorConstants.PERSISTENCE_GENERIC_ERR_INTERNAL_MSG,e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
		}
	}
	
//	public Object execService(String serviceName,HashMap<String,Object> inputPars,Object currentObject,Object cO2) {
//		return null;
//	}
	
	@Destroy @Remove 
	public void remove(){
		clearSession();
	}

	@PrePassivate
	public void prePassivate(){
	}

	@PostActivate
	public void postActivate(){
	}

	@Override
	public PhiRevisionEntity getLastRevisionOccurrenceWhere(
			BaseEntity entity, String attribute, Object value)
			throws PersistenceException {
		return getRevisionOccurrenceWhere (entity, attribute,  value,  false);
	}
	
	public PhiRevisionEntity getLastRevision(Class<?> entityClass, Serializable id){
		if(entityClass==null || id==null)
			return null;
		
		AuditReader auditReader = AuditReaderFactory.get(em);
		Number revision = (Number) auditReader.createQuery().forRevisionsOfEntity(entityClass, false, true)
				.addProjection(AuditEntity.revisionNumber().max())
				.add(AuditEntity.id().eq(id))
				.getSingleResult();
		
		if (revision != null)
			return  get(PhiRevisionEntity.class, revision);
		else
			return null;
	}
	
	/**
	 * get the number of version for an entity, where for the first time the attribute is equal to the specified value
	 */
	private PhiRevisionEntity getRevisionOccurrenceWhere (BaseEntity rimObject, String attribute,  Object value, boolean firstOrLast) throws PersistenceException {
		
		
		AuditReader auditReader = AuditReaderFactory.get(em);
		Serializable id =  rimObject.getInternalId();
		
//		String entityName = rimObject.getClass().getName(); 
//		String clsName = ((Session)em.getDelegate()).getSessionFactory().getClassMetadata(entityName).getMappedClass(EntityMode.POJO).getName();
//		Class<RimObject> entityClass=null;
//		try {
//			entityClass = (Class<RimObject>)Class.forName(clsName);
//		} catch (ClassNotFoundException e) {
//			throw new PersistenceException(e.getMessage(), e, ErrorConstants.APPLICATION_READ_OBJECT_ERR_CODE);
//		}

		Class<BaseEntity> entityClass= (Class<BaseEntity>)rimObject.getClass();
		
		if (entityClass != null) {
//			return (Number) auditReader.createQuery().forEntitiesAtRevision(entityClass, (Number)id)
//				.set
//				.add(AuditEntity.relatedId(attribute).eq(value))
//				.setProjection(AuditEntity.revisionNumber().min());
			Number revision;
			if(firstOrLast){
				revision = (Number) auditReader.createQuery().forRevisionsOfEntity(entityClass, false, true)
				.addProjection(AuditEntity.revisionNumber().min())
				.add(AuditEntity.id().eq(id))
				.add(AuditEntity.property(attribute).eq(value))
				.getSingleResult();
			}else{
				revision = (Number) auditReader.createQuery().forRevisionsOfEntity(entityClass, false, true)
				.addProjection(AuditEntity.revisionNumber().max())
				.add(AuditEntity.id().eq(id))
				.add(AuditEntity.property(attribute).eq(value))
				.getSingleResult();
			}
			 
			if (revision != null)
				return  get(PhiRevisionEntity.class, revision);
		}
		return null;
	}

	@Override
	public void setEntityManager(EntityManager em) {
		this.em=em;
	}

	private void clearSession(){
		Session sess = ((Session)em.getDelegate());
		if(sess!=null && sess.isOpen()){
			sess.clear();
		}
	}
	
	public void removeRegionCache(String regionCache) {
		clearRegionCache(regionCache);
	}
	
	private void clearRegionCache(String regionCache){
		Session sess = ((Session)em.getDelegate());
		if(sess!=null && sess.isOpen()){
			sess.getSessionFactory().evictQueries(regionCache);
		}
	}

	public void analyzePersistenceContext(){
		Session ss = (Session)em.getDelegate();
		if(ss instanceof HibernateSessionProxy){
			org.hibernate.engine.PersistenceContext pc = ((HibernateSessionProxy)ss).getPersistenceContext();
			if(pc instanceof StatefulPersistenceContext){
				StatefulPersistenceContext sp = (StatefulPersistenceContext)pc;
				Map entities = sp.getEntityEntries();
				for(Object o : entities.values()){
					String ciao="";
				}
			}
		}
	}
	
	private void printPersistence() {
		Session ss = (Session)em.getDelegate();
		if(ss instanceof HibernateSessionProxy){
			org.hibernate.engine.PersistenceContext pc = ((HibernateSessionProxy)ss).getPersistenceContext();
			if(pc instanceof StatefulPersistenceContext){
				StatefulPersistenceContext sp = (StatefulPersistenceContext)pc;
				log.info(sp.toString());
			}
		}
	}
}
