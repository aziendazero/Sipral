package com.phi.entities.actions;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

import com.phi.cs.datamodel.IdataModel;
import com.phi.cs.exception.PhiException;



/**
 * Interface implemented by BaseAction and CodeValueBaseAction
 * @author 510087
 *
 */

public interface ActionInterface<T> extends Serializable {

	public void create() throws PhiException;

	public void create(T entity) throws PhiException;

	public void delete() throws PhiException;

	public void eject();

	public void ejectList();

	public void ejectList(String name);
	
	public List injectList (List list);
	public List injectList (List list, String name);
	public void injectEmptyList(String name);
	

	public T getEntity();

	public boolean getHistoryOrderDescending();

	public HashMap<String, Object> getTemporary();

	public IdataModel<T> history() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

	public IdataModel<T> historyFiltered (String...attributesChanged) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

	public IdataModel<T> historyFilteredIncludeCurrent (String...attributesChanged) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

	public IdataModel<T> historyIncludeCurrent() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException;

	public void inject(Object baseEntityOrMap);

	public void inject(T rimObj, String conversationName);

	public void injectAndProceed(Object baseEntityOrMap, String mnemonicName) throws PhiException;

	public void injectbyId() throws PhiException;

	public T newEntity() throws InstantiationException, IllegalAccessException;

	public void refresh() throws PhiException;

	public void setHistoryOrderDescending(boolean historyOrderDescending);

	public void setId(String id);

	public void setInjectIntoName(String injectIntoName);

	public void setTemporary(HashMap<String, Object> temporary);
}
