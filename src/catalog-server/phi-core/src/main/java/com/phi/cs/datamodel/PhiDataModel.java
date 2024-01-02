package com.phi.cs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;

import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.jsf.ListDataModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.resultTransformer.AliasToEntityMapResultTransformer;
import com.phi.cs.exception.PhiException;
import com.phi.cs.paging.LazyList;
import com.phi.entities.actions.ActionInterface;
import com.phi.entities.actions.BaseAction.OrderBy;
import com.phi.ps.ProcessManagerImpl;

public class PhiDataModel<T> implements IdataModel<T>, Serializable {

	private static final long serialVersionUID = -6054712272390590008L;

	//Used by datagrid column with repetable=true to manage variable column number headers
	protected List<String> headers = new ArrayList<String>();

	protected String conversationName;
	protected List<T> model = new ArrayList<T>();
	protected int selectedIndex = -1;

	protected DataModel entities;

	protected boolean isPatient = false;

	// ORDERING OPTIONS
	private String orderedBy;
	private OrderBy orderDirection = OrderBy.ascending;

	// MNEMONIC NAME
	private String mnemonicName;

	public PhiDataModel(List<T> model, String conversationName) {

		this.model = model;
		this.conversationName = conversationName;
		entities = new ListDataModel(model);
	}
	
	public List<String> getHeaders() {
		return headers;
	}

	public void setHeaders(List<String> headers) {
		this.headers = headers;
	}

	public void next() {
		//nothing to do!
	}

	public boolean hasNext() {
		return false;
	}

	public void prev() {
		//nothing to do!
	} 

	public boolean hasPrev() {
		return false;
	}

	public List<Integer> getPageIndexes() {
		return null;
	}

	public int getCurrentPage() {
		return 1;
	}

	@Override
	public Object getFullList() {
		return getList();
	} 

	public List<T> getList() {
		return this.model;
	}	

	/**
	 * Used by datagrids to set the pageSize.
	 * @param pageSize
	 * @return
	 */
	public void setPageSize(int pageSize) {
		if (getList() instanceof LazyList) {
			((LazyList)getList()).setPageSize(pageSize);	
		}
	}


	// List methods

	public T get(int index) {
		return model.get(index);
	}

	public boolean isEmpty() {
		return model.isEmpty();
	}

	//Retrocompatibility:

	public int getSize() {
		return model.size();
	}

	public int size() {
		return model.size();
	}

	public int getRowCount() {
		return model.size();
	}

	//Datamodel
	@JsonIgnore
	public DataModel getEntities() {
		return entities;
	}

	public void setEntities(DataModel entities) {
		this.entities = entities;
	}

	public T injectEject() throws PhiException {
		Context conversation = Contexts.getConversationContext();
		T result = null;

		ActionInterface<T> action = (ActionInterface<T>)Component.getInstance(conversationName+"Action");

		if (selectedIndex != -1 && selectedIndex<model.size()) {
			Object row = model.get(selectedIndex);

			if (action == null) {
				T entity = null;
				if (row instanceof Map) {
					Map rowMap = ((Map)row);
					entity = (T)CatalogPersistenceManagerImpl.instance().get((String)rowMap.get(AliasToEntityMapResultTransformer.ENTITY_CLASS), (Long)rowMap.get("internalId"));
				} else {
					entity = (T)row;
				}
				conversation.set(conversationName, entity);
				result = entity;
			} else {
				T current = action.getEntity();
				if (current == null || !current.equals(row)) {
					action.inject(row);
					result = action.getEntity();
				} else {
					action.eject();
					result = null;
					setSelectedIndex(-1);
				}
			}

		} else {

			if (action == null) {
				conversation.remove(conversationName);
			} else {
				action.eject();
			}
		}

		if ( mnemonicName != null && !mnemonicName.isEmpty() ){
			ProcessManagerImpl.instance().manageTask(mnemonicName);
		}

		return result;
	}

	public String getMnemonicName() {
		return mnemonicName;
	}

	public void setMnemonicName(String mnemonicName) {
		this.mnemonicName = mnemonicName;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex != this.selectedIndex) 
			this.selectedIndex = selectedIndex;
		else
			this.selectedIndex = -1;
	}

	public void orderBy(String expression) throws PhiException {
		//flip orderDirection (datagrid column button)
		if (orderDirection == OrderBy.descending) {
			orderDirection = OrderBy.ascending;
		} else {
			orderDirection = OrderBy.descending;
		}
		
		orderBy(expression, orderDirection.name());
		
	}

	public void orderBy(String expression, String trueOrdering){
		
		if (orderedBy == null || !orderedBy.equals(expression)) {
			orderedBy = expression;
		}
		
		if(trueOrdering==null)
			return;
				
		if(trueOrdering.equals(OrderBy.ascending.name())){
			orderDirection = OrderBy.ascending;
		}else if(trueOrdering.equals(OrderBy.descending.name())){
			orderDirection = OrderBy.descending;
		}
		
		Collections.sort(model, new Comparator<T>() {

			@Override
			public int compare(T o1, T o2) {

				int result = 0;

				Object p1 = null;
				Object p2 = null;

				String[] bindingParts = orderedBy.split("\\.");
				if (o1 == null && o2 == null) {
					return result;
				} else if (o1 == null) {
					result = -1;
				} else if (o2 == null) {
					result = 1;
				} else if (HashMap.class.isInstance(o1) && HashMap.class.isInstance(o2)) {
					HashMap<String, Object> h1 = (HashMap<String, Object>)o1;
					HashMap<String, Object> h2 = (HashMap<String, Object>)o2;

					for (int i=0;i<bindingParts.length-1;i++){
						h1 = (HashMap<String, Object>)h1.get(bindingParts[i]);
						h2 = (HashMap<String, Object>)h2.get(bindingParts[i]);
					}

					p1 = h1.get(bindingParts[bindingParts.length-1]);
					p2 = h2.get(bindingParts[bindingParts.length-1]);

				} else {
					try {
						p1 = PropertyUtils.getProperty(o1, bindingParts[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						p2 = PropertyUtils.getProperty(o2, bindingParts[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}

					for (int i=1;i<bindingParts.length;i++) {
						if (p1 == null || p2 == null) {
							break;
						}
						try{
							if (p1 != null) {
								p1 = PropertyUtils.getProperty(p1, bindingParts[i]);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try{
							if (p2 != null) {
								p2 = PropertyUtils.getProperty(p2, bindingParts[i]);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				if (p1 == null && p2 == null) {
					result = 0;
				}  else if (p1 == null) {
					result = -1;
				} else if (p2 == null) {
					result = 1;
				} else if (p1 instanceof Date && p2 instanceof Date) {
					result = Date.class.cast(p1).compareTo(Date.class.cast(p2));
				} else {
					String p1s = p1.toString();
					String p2s = p2.toString();
					if (p1 instanceof Number && p2 instanceof Number || isNumeric(p1s) && isNumeric(p2s)){
						result = new Double(p1s.isEmpty() ? "0" : p1s).compareTo(new Double(p2s.isEmpty() ? "0" : p2s));
					} else {
						//TODO: IMPROVE WITH NATURAL ORDER SORTING FOR CONTAINING NUMBERS STRINGS
						result = p1s.compareToIgnoreCase(p2s);
					}
				}
				if (orderDirection == OrderBy.descending) {
					result = -result;
				}
				return result;
			}
		});
	}
	
	private boolean isNumeric(String s) {
		return s.trim().replaceAll("[,.']", "").matches("^[-+]?\\d+$"); //if you use * instead of + you could match "." strings
	}

}