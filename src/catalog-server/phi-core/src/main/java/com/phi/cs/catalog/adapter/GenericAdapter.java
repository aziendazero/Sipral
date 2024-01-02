package com.phi.cs.catalog.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.engine.StatefulPersistenceContext;
import org.hibernate.impl.SessionImpl;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.persistence.HibernateSessionProxy;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.repository.RepositoryManager;

/**
 * Class for CRUD operation on non RIM object.
 * It is implemented using CMT transaction.
 * @author rossi
 *
 */
@Stateless
//@RemoteBinding(jndiBinding="GenericAdapter")
public class GenericAdapter implements GenericAdapterLocalInterface,GenericAdapterRemoteInterface {
	private static final Logger log = Logger.getLogger(GenericAdapter.class);
	
	@PersistenceContext(unitName="rimEntityManagerPDM2")
	private EntityManager rimEntityManagerPDM2;
	
	@Create
	public void init() {
		String publicKey=System.getProperty("phi.key.public");
		String privateKey=System.getProperty("phi.key.private");

		if(RepositoryManager.instance().isUsing_EncryptPatientData() && publicKey!=null && privateKey!=null){
			rimEntityManagerPDM2.createNativeQuery("{call set_crypt_context(?, ?)}")
			.setParameter(1, publicKey)
			.setParameter(2, privateKey)
			.executeUpdate();
		}
	}
	
	/**
	 * Create object, merge or persist
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Object create(Object obj){
		//this.printPersistence();
		if (!rimEntityManagerPDM2.contains(obj)) {
			obj = rimEntityManagerPDM2.merge(obj);
		} else {
			rimEntityManagerPDM2.persist(obj);
		}
		rimEntityManagerPDM2.flush();
		log.info("create - Flushed session");
		return obj;
	}
	
	/**
	 * Create attached object
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Object createObject(Object obj){
		//this.printPersistence();
		rimEntityManagerPDM2.persist(obj);
		rimEntityManagerPDM2.flush();
		log.info("createObject - Flushed session");
		return obj;
	}
	
	/**
	 * Create detached object
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Object updateObject(Object obj){
		
		Object returnedObject = rimEntityManagerPDM2.merge(obj);
		rimEntityManagerPDM2.flush();
		log.info(" - Flushed session");
		return returnedObject;
	}
	
	/**
	 *  Create criteria
	 */
	@SuppressWarnings("rawtypes")
	public Criteria createCriteria(Class clazz) {
    	// get the native hibernate session
    	Session session = ((Session)rimEntityManagerPDM2.getDelegate());
    	//create criteria
    	return session.createCriteria(clazz);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public <T> T get(Class<T> clazz, Serializable id) {
		return rimEntityManagerPDM2.find(clazz, id);
	}
	
	/**
	 * LockMode.UPGRADE or LockMode.NONE
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Object get(Class clazz, Serializable id, LockMode lm) {
		
		return ((Session)rimEntityManagerPDM2.getDelegate()).get(clazz, id, lm);
	}
	
	/**
	 * Delete an object
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void delete(Object obj) throws PersistenceException {
		try {
			if (!rimEntityManagerPDM2.contains(obj)) {
				obj = rimEntityManagerPDM2.merge(obj);
			}
			rimEntityManagerPDM2.remove(obj);
			rimEntityManagerPDM2.flush();
			log.info("delete - Flushed session");
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(), e, ErrorConstants.APPLICATION_DELETE_OBJECT_ERR_CODE);
		}
	}
	
	/**
	 * Execute hql query
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<Object> readObject(String hqlQuery){
		List<Object> resultList = (List<Object>)rimEntityManagerPDM2.createQuery(hqlQuery).getResultList();
		log.debug("Query returned " + resultList.size() +" objects"); 
		if (resultList.isEmpty())
			return resultList;
		Session hibernateSession = (Session) rimEntityManagerPDM2.getDelegate();
		for (Object object : resultList)
			hibernateSession.evict(object);
		return resultList;
	}
	
	/**
	 * Execute hql update query
	 */
	public int executeUpdateHql(String query, Map<String, Object> pars) throws PersistenceException {
		int nOfUpdatedEntities = 0;
		try {
			Query qry = rimEntityManagerPDM2.createQuery(query);
			
			if(pars!=null){
				Object[] parNames = pars.keySet().toArray();
				for(int p=0;p<parNames.length;p++){
					String key = (String)parNames[p];
					qry.setParameter(key, pars.get(key));
				}
			}
			
			nOfUpdatedEntities = qry.executeUpdate();
			log.debug("executeUpdateHql: " + query.toString());
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(),e, ErrorConstants.PERSISTENCE_SELECT_ITEM_ERR_INTERNAL_MSG);
		}

		return nOfUpdatedEntities;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int executeUpdateNative(String query, Map<String, Object> pars) throws PersistenceException {
		int nOfUpdatedEntities = 0;
		try {
			Query qry = createNativeQuery(query);
			
			if(pars!=null){
				Object[] parNames = pars.keySet().toArray();
				for(int p=0;p<parNames.length;p++){
					String key = (String)parNames[p];
					qry.setParameter(key, pars.get(key));
				}
			}
			nOfUpdatedEntities = qry.executeUpdate();
			log.debug("executeUpdateHql: " + query.toString());
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(),e, ErrorConstants.PERSISTENCE_SELECT_ITEM_ERR_INTERNAL_MSG);
		}

		return nOfUpdatedEntities;
	}
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<?> executeHQL(String query, Map<String,Object> pars) throws PersistenceException{

		List<Object> result = null;
		try{
			Query qry = rimEntityManagerPDM2.createQuery(query);
			
			if(pars!=null){
				Object[] parNames = pars.keySet().toArray();
	
				for(int p=0;p<parNames.length;p++){
					String key = (String)parNames[p];
					qry.setParameter(key, pars.get(key));
				}
			}
			
			result = qry.getResultList();
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(),e, ErrorConstants.PERSISTENCE_SELECT_ITEM_ERR_INTERNAL_MSG);
		}

		return result;
	}
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<?> executeNative(String query, Map<String,Object> pars) throws PersistenceException{

		List<Object> result = null;
		try{
			Query qry = createNativeQuery(query);
			
			if(pars!=null){
				Object[] parNames = pars.keySet().toArray();
	
				for(int p=0;p<parNames.length;p++){
					String key = (String)parNames[p];
					qry.setParameter(key, pars.get(key));
				}
			}
			
			result = qry.getResultList();
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(),e, ErrorConstants.PERSISTENCE_SELECT_ITEM_ERR_INTERNAL_MSG);
		}

		return result;
	}
	
	/**
	 * Execute an hql query, return maxResults n. of results from offset firstResult
	 */
	public List<?> executePagedHQL(String query, Map<String,Object> pars, Integer firstResult, Integer maxResults) throws PersistenceException{
		long t = System.currentTimeMillis();
		List<?> result = null;
		try{
			Query qry = rimEntityManagerPDM2.createQuery(query);
			
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
			
			log.info("Found " + result.size() + " in " + (System.currentTimeMillis()-t) + " ms [" + query + "]");	
			
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage() + " Error executing: " + query,e, ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
		}

		return result;
	}
	
	public Query createNativeQuery(String sql) throws PersistenceException {
		Query qry = null;
		
		try {
			qry = rimEntityManagerPDM2.createNativeQuery(sql);
		} catch (Exception e) {
			throw new PersistenceException(e.getMessage(),e, ErrorConstants.PERSISTENCE_SQLFIXEDQUERY_ERR_INTERNAL_MSG);
		}
		return qry;
	}
	
	/**
	 * Used to flush the Session
	 * Rimosso dalle interfacce @Local e @Remote perchè se invocato da un Seam Bean eredita il Persitence Context della CONVERSATION
	 * e quindi di fatto equivale a rimpdm2CatalogAdapter.flushSession. I flush sono già stati aggiunti nei metodi create / update...
	 * di GenericAdapter che sono gestiti con TransactionAttributeType.REQUIRES_NEW
	 * @throws PersistenceException
	 */
	@Deprecated
	public void flushSession() throws PersistenceException {
		try {
			rimEntityManagerPDM2.flush();
			log.info("Flushed session");
		} catch (Exception e) {
			throw new PersistenceException(ErrorConstants.PERSISTENCE_FLUSH_ERR_INTERNAL_MSG,e, ErrorConstants.PERSISTENCE_FLUSH_ERR_CODE);
		}
	}
	
	
	public static GenericAdapterLocalInterface instance() throws NamingException {
		InitialContext ic = new InitialContext();
		GenericAdapterLocalInterface delegate;
        try {
            delegate = (GenericAdapterLocalInterface)ic.lookup("CATALOG_SERVER/GenericAdapter/local");
        } catch (NamingException e) {
            log.warn("could not do lookup GenericAdapter");
            throw e;
        } finally {
            ic.close();
        }
		return delegate;
	}
	
	private void printPersistence() {
		Session ss = (Session)rimEntityManagerPDM2.getDelegate();
		org.hibernate.engine.PersistenceContext pc = null;
		if(ss instanceof HibernateSessionProxy){
			pc = ((HibernateSessionProxy)ss).getPersistenceContext();
		} else if(ss instanceof SessionImpl) {
			pc = ((SessionImpl)ss).getPersistenceContext();
		}
		
		if(pc instanceof StatefulPersistenceContext){
			StatefulPersistenceContext sp = (StatefulPersistenceContext)pc;
			log.info(sp.toString());
		}
	}
}
