package com.phi.cs.catalog.adapter;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import com.phi.cs.exception.PersistenceException;

@Remote
public interface GenericAdapterRemoteInterface {
	public Object createObject(Object obj);

	public Object updateObject(Object obj);
	
	public List<Object> readObject(String hqlQuery);
	
	//taken from rimPdm2CatalogAdapter
	public int executeUpdateHql(String query, Map<String, Object> pars) throws PersistenceException;
	public List executeHQL(String query, Map<String,Object> pars) throws PersistenceException;
}
