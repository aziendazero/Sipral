package com.phi.cs.catalog.adapter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.LockMode;

import com.phi.cs.exception.PersistenceException;

@Local
public interface GenericAdapterLocalInterface {

	public Object create(Object obj);
	
	public Object createObject(Object obj);

	public Object updateObject(Object obj);
	
	public List<Object> readObject(String hqlQuery);
	//taken from rimPdm2CatalogAdapter
	public Criteria createCriteria(Class clazz);
	//taken from rimPdm2CatalogAdapter
	public <T> T get(Class<T> clazz, Serializable id);
	public <T> T get(Class<T> clazz, Serializable id, LockMode lm);
	
	//taken from rimPdm2CatalogAdapter
	public void delete(Object obj) throws PersistenceException;
	
	//taken from rimPdm2CatalogAdapter
	public int executeUpdateHql(String query, Map<String, Object> pars) throws PersistenceException;
	
	public int executeUpdateNative(String query, Map<String, Object> pars) throws PersistenceException;

	public List executeHQL(String query, Map<String,Object> pars) throws PersistenceException;
	
	public List<?> executeNative(String query, Map<String,Object> pars) throws PersistenceException;
	
	//taken from rimPdm2CatalogAdapter
	public List executePagedHQL(String query, Map<String,Object> pars, Integer firstResult, Integer maxResults) throws PersistenceException;
	//taken from rimPdm2CatalogAdapter
	public Query createNativeQuery(String sql) throws PersistenceException;

}