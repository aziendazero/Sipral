package com.phi.cs.catalog.adapter;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;

import com.phi.cs.catalog.adapter.nestedset.NestedSetNode;
import com.phi.cs.catalog.adapter.nestedset.query.NestedSetNodeWrapper;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.PhiRevisionEntity;
import com.phi.entities.baseEntity.BaseEntity;
import com.phi.entities.dataTypes.II;

 
/** 
 * This class defines the main method that an Adpter must implement.<br/>
 * The Adpater Impl. is the main class that gets the data from different sources (DBMS, WS,..)<br/>
 *  
 */
@Local
public interface CatalogAdapter {
	
	public boolean checkDb();
	public enum lockType {UPDATE};
	
	/** 
	 *  This method is used to insert a rim object in the database. 			</br>
	 *  The rim object must be not null, <i>session.save</i> will be invoked 	</br>
	 *  to the saving.															</p>
	 *  
	 *  @parameter  p_rimObj - a object to insert.
	 *  
	 *  @return  the object if inserted or null if not.
	 */
	public Object create(Object entity) throws PersistenceException;
	
	public void persist(Object entity) throws PersistenceException;

	public List read(Object example);
	
	public <T extends NestedSetNode<T>> List<NestedSetNodeWrapper<T>> readNestedSet(T nsNode) throws PersistenceException;
	
	public boolean contains(Object entity);
	
	public Criteria createCriteria(Class clazz);
	
	public Query createQuery(String hql);
	
	public org.hibernate.Query createHibernateQuery(String hql);
	
	public org.hibernate.envers.query.AuditQuery createAuditQuery(Class clazz);
	
	/** 
	 *  This method deletes a rim object in database. <br>
	 *    
	 *  @param rimObj - the rim object to delete
	 *  
	 *  @throws PersistenceException
	 *  
	 */
	public void delete(Object obj) throws PersistenceException;


	/**
	 *  This method updates a object in database. <br>
     *  
	 *  @param obj - the object to be updated
	 *  
	 *  @return updated object
	 *  
	 *  @throws PersistenceException
	 *  
	 */
	public Object update(Object obj) throws PersistenceException;

	/**
	 * Used to flush the Session in a long running conversation context
	 * @throws PersistenceException
	 */
	public boolean flushSession() throws PersistenceException;

	/**
	 * Used to get data by ID
	 */
	public Object get(String entityName, Serializable id);
	public <T> T get(Class<T> clazz, Serializable id);
	public Object get(Class clazz, Serializable id, lockType locktype);
	
	/**
	 * Used to get proxy by ID
	 */
	public Object load(String entityName, Serializable id);
	public <T> T load(Class<T> clazz, Serializable id);

	/**
	 * Used to get special data from DB via RIM Object and HQL. Used in REPORTING
	 * @throws PersistenceException
	 */
	public List executeHQL(String query) throws PersistenceException;
	
	
	/**
	 * execute a paged query 
	 * @param query
	 * @param pars
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws PersistenceException
	 */
	public List executePagedHQL(String query, Map<String,Object> pars, Integer firstResult, Integer maxResults) throws PersistenceException;
	
	/**
	 * 
	 * @param query
	 * @param maxResults
	 * @return
	 * @throws PersistenceException
	 */
	public List executeLimitedHQL(String query, Map<String, Object> pars, int maxResults) throws PersistenceException;

	/**
	 * Used to get special data from DB via RIM Object and HQL with parameter. Used in REPORTING
	 * @throws PersistenceException
	 */
	public List executeHQLwithParameters(String query,Map<String, Object> pars)throws PersistenceException;
	
	public int executeUpdateHql(String query, Map<String, Object> pars) throws PersistenceException;
	
	public List<?> executeNamedQuery(String name) throws PersistenceException;
	
	public List<?> executeNamedQuery(String name, Map<String,Object> parameters) throws PersistenceException;
	
	public int executeUpdateNamedQuery(String name, Map<String,Object> parameters) throws PersistenceException;
	
	public Query createNativeQuery(String name) throws PersistenceException;
	
	public SQLQuery createHibernateNativeQuery(String sql);
	
	/**
	 * Called when SFSB is removed
	 */
	public void remove();

	/**
	 * Called when SFSB pre passivate
	 */
	public void prePassivate();
	
	/**
	 * Called when SFSB post activate
	 */
	public void postActivate();

	/**
	 * Called when SFSB is created
	 */
	public void init();
	
	
	public BaseEntity findPreviusObjectWithSameIdAndStatusCode(String rimObjClassName, II Id4Phi, String statusCode) throws PersistenceException;

	/**
	 * Given the class of the object to retrieve from db and the id, the method
	 * queries the db and returns the object. Return null if the id is not
	 * unique.
	 * @param rimObjClassName
	 * @param Id4Phi
	 * @return a rimObject
	 * @throws PersistenceException problem with transaction arised
	 */
	public BaseEntity findById(String rimObjClassName, II Id4Phi) throws PersistenceException ;
	
	/**
	 * Synchronizes given entity with database, discarding current object attributes values
	 * @param RimObject - RimObject to synchronize
	 */
	public void refresh(Object entity);
	
	public void refreshIfNeeded(Object entity);
	public void refreshSessionObjectIfNeeded(Object entity);
	
	public void evict(Object entity);
	
	public String dumpHibernateSession();
	
	//public void setSessionCacheMode(CacheMode cacheMode);
	//Audit methods
	
	//public List<BaseEntity> getHistoryof(String entityName, Serializable id) throws PersistenceException;
	public <T> List<T> getHistoryof(Class<T> entityClass, /*String entityName,*/ Serializable id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	public <T> List<T> getHistoryof(Class<T> entityClass, /*String entityName,*/ Serializable id, String[] changedAttributesFilter) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;
	
	//public List<Number> getRevisionNumbers(String entityName, Serializable id) throws PersistenceException;
	public List<Number> getRevisionNumbers(Class<?> entityClass, /*String entityName,*/ Serializable id);
	
	public Object getRevision (BaseEntity rimObject, Number revN) throws PersistenceException;
	//public Object getRevision(String entityName, Serializable id, Number revN) throws PersistenceException;
	public <T> T getRevision(Class<T> entityClass, /*String entityName,*/ Serializable id, Number revN);
	
	public PhiRevisionEntity getFirstRevisionOccurenceWhere (BaseEntity rimObject, String attribute,  Object value) throws PersistenceException ;
	public PhiRevisionEntity getNthRevision(BaseEntity rimObject, long version);
	public PhiRevisionEntity getLastRevision(Class<?> entityClass, Serializable id);
	public PhiRevisionEntity getLastRevisionOccurrenceWhere (BaseEntity entity, String attribute,  Object value) throws PersistenceException ;
	public Number getRevisionType (BaseEntity rimObject, Number revN);
	
	public Serializable getIdentifier(Object entity);
	
	public Object isSessionDirty() throws Exception;
	
	public void setEntityManager(EntityManager em);
	
	public void removeRegionCache(String regionCache);

	public void analyzePersistenceContext();
}
