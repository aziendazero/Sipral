package com.phi.cs.lock;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import org.apache.log4j.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;

import com.phi.entities.UniqueCheck;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.security.UserBean;
/**
 * implementation of {@link LockManager}
 * @author rossi
 *
 */
@Name("Locker")
@Stateless(name="Locker")
@TransactionManagement(TransactionManagementType.BEAN)
public class Locker implements LockManager {
	
	private static final Logger log = Logger.getLogger(Locker.class);

	@Resource
	private EJBContext context;

//	@PersistenceContext(unitName="zzzCATALOG_SERVER.jar#rimEntityManagerPDM2")
	@PersistenceContext(unitName="rimEntityManagerPDM2")
	protected EntityManager em;

	/**
	 *  The lock is implemented with a combination of two unique keys: root and extension.
	 *  In general the two string attributes of lock can be used in this way:
	 *     root:  className
	 *     extension:  internalId
	 *  The two attributes can be anyways used in other custom way. (e.g. insead of internal Id, another unique object id).
	 *  
	 *  removePrevious: if true remove previous Lock of same object class, for the selected username.
	 *  This configuration is to avoid a user lock more than one object of same class. 
	 *  
	 *  *** ATTENTION ********************************************************
	 *    locks methods return an UniqueCheck object: it is the object lock
	 *    but it could be the lock of another user of the same object!!!!! 
	 *  **********************************************************************
	 *  
	 *  USE lockIt to simplify the usage.
	 *  
	 *  === NOTE =========================================================================================================
	 *   this EJB is used at runtime from jbpm process via LockerAnnotated.java class which is exactly annotated "Locker"
	 *  ==================================================================================================================
	 *  
	 *  
	 */
	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public UniqueCheck lock(String root, String extension, String userName, boolean removePrevious) throws Exception {
		String userRole = UserBean.instance().getRoleCodeId();
		Query queryBuilder = em.createQuery("SELECT un FROM UniqueCheck un where root=?1 and extension=?2");
		queryBuilder.setParameter(1, root);
		queryBuilder.setParameter(2, extension);
		List result= queryBuilder.getResultList();
		UniqueCheck uniqueCheck=null;
		if (result.size()>0){
			
			return (UniqueCheck)result.get(0);
			
			//check if user already owns the lock
//			for (Object unique: result) {
//				uniqueCheck=(UniqueCheck)unique;
//				if(uniqueCheck.getUserName().equals(userName)){
//					//update uniqueCheck
//					uniqueCheck.setCreationDate(new Date());
//					uniqueCheck.setRoot(root);
//					uniqueCheck.setExtension(extension);
//					uniqueCheck.setUserName(userName);
//					uniqueCheck.setUserRole(userRole);
//				}
//				else 
//					return uniqueCheck;
//			}
			
		} else {
			uniqueCheck= new UniqueCheck();
			uniqueCheck.setCreationDate(new Date());
			uniqueCheck.setRoot(root);
			uniqueCheck.setExtension(extension);
			uniqueCheck.setUserName(userName);
			uniqueCheck.setUserRole(userRole);
		}

		UserTransaction utx = context.getUserTransaction();
		utx.begin();
		
		em.persist(uniqueCheck);
		em.flush();
		
		if(removePrevious){
			queryBuilder = em.createQuery("DELETE FROM UniqueCheck un where root=?1 and extension<>?2 and userName=?3");
			queryBuilder.setParameter(1, root);
			queryBuilder.setParameter(2, extension);
			queryBuilder.setParameter(3,userName);
			int resultNumber =queryBuilder.executeUpdate();
			if(log.isInfoEnabled())
				log.info("[cid="+Conversation.instance().getId()+"] Released previous object "+root+ " locks. (deleted " + resultNumber + " lock rows owned by " + userName +")" );
		}
		
		try{
			utx.commit();
		} catch (Exception e) {
			try {
				utx.rollback();
			} catch (Exception e1) {
				throw new Exception(e1);
			}
			throw new Exception(e);
		}

		log.info("[cid="+Conversation.instance().getId()+"] Locked object "+root+ ": "+extension + " by current user");
		return uniqueCheck;
	}

	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public UniqueCheck lock(String root, String extension) throws Exception{
		String userName = UserBean.instance().getUsername();
		return lock( root,  extension,  userName, true);
	}
	
	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public UniqueCheck lock(String root, Long internalId, String userName, boolean removePrevious) throws Exception{
		String idInt = Long.toString(internalId);
		return lock( root,  idInt,  userName, removePrevious);
	}
	
	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public UniqueCheck lock(String root, Long internalId) throws Exception{
		String idInt = Long.toString(internalId);
		return lock( root,  idInt );
	}
	
	
	
	
	public boolean lockIt(String root, String extension) {
		return lockIt(root, extension, true);
	}
	
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean lockIt(String root, String extension, boolean removePrevious) {
		UniqueCheck result = null;
		String currentUser = UserBean.instance().getUsername();
		try {
			result = lock(root, extension, currentUser, removePrevious);
		} catch (Exception e) {
			log.error("Problem while locking object of class"+ root + " with id "+extension + ". CAUSE:"+e.getMessage());
			return false;
		}
		if (result==null) {
			return false;
		}
		
		if (!result.getUserName().equals(currentUser)) {
			log.info("user "+currentUser+" cannot lock object of class " +root + " with id " + extension + " beacuse already locked by "+result.getUserName() );
			return false;
		}
		
		return true;

	}
	
	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean lockIt(String root, Long internalId) {
		String idInt = Long.toString(internalId);
		return lockIt(root, idInt);
	}
	
	
	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unlock(String root, String userName) {
		UserTransaction utx = context.getUserTransaction();
		try {
			
			utx.begin();
			
			Query queryBuilder = em.createQuery("DELETE FROM UniqueCheck un where root=?1 and userName = ?2");
			queryBuilder.setParameter(1, root);
			queryBuilder.setParameter(2, userName);
			int result =queryBuilder.executeUpdate();
			if(log.isInfoEnabled())
				log.info("[cid="+Conversation.instance().getId()+"] Released all lock for objects "+root+ ". (deleted " + result + " lock row)" );
			
			utx.commit();
			
		} catch (Exception e) {
			try {
	            utx.rollback();
	        } catch (Exception e1) {
	        	log.error("[cid="+Conversation.instance().getId()+"] Error releasing all lock for objects "+root+ ".", e);
	        }
			log.error("[cid="+Conversation.instance().getId()+"] Error releasing all lock for objects "+root+ ".", e);
		}
	}	
	
	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unlock(String root, String extension, String userName) {
		
		UserTransaction utx = context.getUserTransaction();
		try {

			utx.begin();

			Query queryBuilder = em.createQuery("DELETE FROM UniqueCheck un where root=?1 and extension=?2 and userName = ?3");
			queryBuilder.setParameter(1, root);
			queryBuilder.setParameter(2, extension);
			queryBuilder.setParameter(3, userName);
			int result =queryBuilder.executeUpdate();
			if(log.isDebugEnabled())
				log.debug("[cid="+Conversation.instance().getId()+"] Released lock for object "+root+ ": "+extension + ". (deleted " + result + " lock row owned by " + userName);
			utx.commit();

		} catch (Exception e) {
			try {
	            utx.rollback();
	        } catch (Exception e1) {
	        	log.error("[cid="+Conversation.instance().getId()+"] Error releasing lock for object "+root+ ": "+extension + ".", e);
	        }
			log.error("[cid="+Conversation.instance().getId()+"] Error releasing lock for object "+root+ ": "+extension + ".", e);
		}
	}
	
	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unlock(String root, Long internalId) {
		String idInt = Long.toString(internalId);
		String userName = UserBean.instance().getUsername();
		unlock (root, idInt, userName);
	}

	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unlockAll(String userName) {
		UserTransaction utx = context.getUserTransaction();
		try {

			utx.begin();
			Query queryBuilder = em.createQuery("DELETE FROM UniqueCheck un where userName = ?1");
			queryBuilder.setParameter(1, userName);

			int result =queryBuilder.executeUpdate();

			if(log.isInfoEnabled()) {
				log.debug("[cid="+Conversation.instance().getId()+"] Released all lock for user "+userName+" (Deleted " + result + " locks)");
			}
			utx.commit();

		} catch (Exception e) {
			try {
	            utx.rollback();
	        } catch (Exception e1) {
	        	log.error("[cid="+Conversation.instance().getId()+"] Error releasing all lock for user "+userName+ ".", e);
	        }
			log.error("[cid="+Conversation.instance().getId()+"] Error releasing all lock for user "+userName+ ".", e);
		}
	}
	
	@Override
//	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void unlockAll() {
		UserTransaction utx = context.getUserTransaction();
		try {

			utx.begin();
			Query queryBuilder = em.createQuery("DELETE FROM UniqueCheck");

			int result = queryBuilder.executeUpdate();

			if(log.isInfoEnabled()) {
				log.debug("Deleted " + result + " lock row");
			}
			utx.commit();

		} catch (Exception e) {
			try {
	            utx.rollback();
	        } catch (Exception e1) {
	        	log.error("[cid="+Conversation.instance().getId()+"] Error deleting lock.", e);
	        }
			log.error("[cid="+Conversation.instance().getId()+"] Error deleting lock.", e);
		}
	}
	
	public boolean isLocked(String root, String extension) {
		Query queryBuilder = em.createQuery("SELECT count(*) FROM UniqueCheck un where root=?1 and extension=?2");
		queryBuilder.setParameter(1, root);
		queryBuilder.setParameter(2, extension);
		List result= queryBuilder.getResultList();
		
		if (((Long)result.get(0)) > 0)
			return true;
		return false;
	}
	
	//FIXME: Where's uded?
	public UniqueCheck getLock(String root, String extension) {
		Query queryBuilder = em.createQuery("SELECT un FROM UniqueCheck un where root=?1 and extension=?2");
		queryBuilder.setParameter(1, root);
		queryBuilder.setParameter(2, extension);
		List<UniqueCheck> result = queryBuilder.getResultList();
		
		if (!result.isEmpty())
			return result.get(0);
		
		return null;
	}

	public String isLockedBy(String root, String extension) {
		return isLockedBy(root, extension, "userName");
	}

	public String isLockedBy(String root, String extension, String fieldName) {
		Query queryBuilder = em.createQuery("SELECT "+fieldName+" FROM UniqueCheck un where root=?1 and extension=?2");
		queryBuilder.setParameter(1, root);
		queryBuilder.setParameter(2, extension);
		List result= queryBuilder.getResultList();
		
		if ((result.size() > 0))
			return result.get(0).toString();
		return null;
	}
	
	public Object getLockingEmployee(String root, Long internalId) {
		String userName = isLockedBy(root, String.valueOf(internalId));
		if (userName != null) {
			Query queryBuilder = em.createQuery("SELECT emp FROM Employee emp WHERE emp.username = ? AND emp.isActive = true");
			queryBuilder.setParameter(1, userName);
			List<Object> result= queryBuilder.getResultList();

			if ((result.size() > 0))
				return result.get(0);
		}
		return null;
	}
	
	public String getLockingRole(String root, Long internalId) {
		String userRole = isLockedBy(root, String.valueOf(internalId), "userRole");
		
		if (userRole != null) {
			Query queryBuilder = em.createQuery("SELECT cv FROM CodeValueRole cv WHERE cv.id = ? AND cv.status = 1");
			queryBuilder.setParameter(1, userRole);
			List<CodeValueRole> result= queryBuilder.getResultList();

			if ((result.size() > 0)) {
				userRole = result.get(0).getCurrentTranslation();
			}
		}

		return userRole;
	}
	
	public List<UniqueCheck> getLocks() {
		Query queryBuilder = em.createQuery("FROM UniqueCheck");

		return queryBuilder.getResultList();
	}
	
	public static LockManager instance() throws NamingException {
		InitialContext ic = new InitialContext();
		LockManager delegate;
        try {
            delegate = (LockManager) ic.lookup("CATALOG_SERVER/Locker/local");
        } catch (NamingException e) {
            log.warn("could not do lookup Locker/local");
            throw e;
        } finally {
            ic.close();
        }
		return delegate;
	}


}
