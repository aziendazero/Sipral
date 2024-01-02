package com.phi.cs.lock;

import java.util.List;

import javax.ejb.Local;

import com.phi.entities.UniqueCheck;

@Local
public interface LockManager {

	/**
	 * Write a lock row for the object and associate to current 
	 * @param contextName
	 * @param rimObject
	 * @param key optionally the key to use to store data
	 * @param userName the user who is trying to lock
	 * @param removePrevious if true remove object owned by current user with 
	 * same root but different id
	 * @return the checker object with details about lock owner
	 */
	public UniqueCheck lock(String root, Long internalId, String userName, boolean removePrevious) throws Exception ;
	public UniqueCheck lock(String root, Long internalId) throws Exception ;
	
	public UniqueCheck lock(String root, String extension, String userName, boolean removePrevious) throws Exception ;
	public UniqueCheck lock(String root, String extension) throws Exception ;
	
	//return true or false is lock is success.
	public boolean lockIt(String root, String extension);
	public boolean lockIt(String root, String extension, boolean removePrevious);
	public boolean lockIt(String root, Long internalId);
	
	/**
	 * delete from db the lock referring to object locked by current user
	 * @param contextName
	 * @param rimObject TODO
	 * @param key TODO
	 * @param userName TODO
	 */
	
	public void unlock(String root, String userName);
	
	public void unlock(String root, String extension, String userName);
	public void unlock(String root, Long internalId);
	/**
	 * Unlock all object used by current user
	 */
	public void unlockAll(String userName);
	
	public void unlockAll();
	
	public boolean isLocked(String root, String extension);
	
	public UniqueCheck getLock(String root, String extension);
	
	public String isLockedBy(String root, String extension);
	public String isLockedBy(String root, String extension, String fieldName);
	public Object getLockingEmployee(String root, Long internalId);
	public String getLockingRole(String root, Long internalId);

	public List<UniqueCheck> getLocks();

}
