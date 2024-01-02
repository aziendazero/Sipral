package com.phi.cs.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;

import org.jboss.seam.Component;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.jsf.ListDataModel;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.resultTransformer.AliasToEntityMapResultTransformer;
import com.phi.cs.exception.PhiException;
import com.phi.cs.paging.LazyList;
import com.phi.entities.actions.ActionInterface;
import com.phi.entities.actions.BaseAction.OrderBy;
import com.phi.ps.ProcessManagerImpl;

/**
 * Data model that manages pagination for tables without richfaces
 * @author Alex Zupan
 *
 * @param <T>
 */
public class PagedDataModel<T> implements IdataModel<T>, Serializable {

	private static final long serialVersionUID = 5622849880836725523L;

	private LazyList<T> wrappedList;
	//	private Class<T> entityClass;
	private String conversationName;
	private List<T> model = new ArrayList<T>();
	private int selectedIndex = -1;
	private int pageIndex = 0;

	// ORDERING OPTIONS
	private String orderedBy;
	private OrderBy orderDirection = OrderBy.ascending;

	// MNEMONIC NAME
	private String mnemonicName;

	private DataModel entities;

	private List<Integer> pageIndexes = new ArrayList<Integer>();

	protected boolean isPatient = false;

	public PagedDataModel(LazyList<T> wrappedList, String conversationName/*, Class entityClass*/) {

		//        if (entityClass.equals(Patient.class)) {
		//        	isPatient = true;
		//        }

		this.wrappedList = wrappedList;
		this.conversationName = conversationName;
		//		this.entityClass = entityClass;
		updateModel();
		entities = new ListDataModel(model);
		if (wrappedList.numResultsUnknown())
			pageIndexes.add(1);
		else {
			for (int z=0;z*wrappedList.getPageSize()<wrappedList.size();z++)
				pageIndexes.add(z+1);
		}

	}

	public void next() {
		if((pageIndex + 1) * wrappedList.getPageSize() < wrappedList.size()) {
			pageIndex++;
			boolean modelUpdated = updateModel();

			if (modelUpdated) {
				this.selectedIndex = -1; 

				if (!pageIndexes.contains(pageIndex+1))
					pageIndexes.add(pageIndex+1);
			} else
				pageIndex--;
		}
	}

	public boolean hasNext() {
		if (wrappedList.hasMoreResults() || pageIndexes.contains(pageIndex+2))
			return true;
		return false;
	}

	public void prev() {
		if(pageIndex > 0) {
			pageIndex--;
			updateModel();
			this.selectedIndex = -1; 
		}
	} 

	public boolean hasPrev() {
		if (pageIndex > 0)
			return true;
		return false;
	}

	public void first() {
		if(pageIndex > 0) {
			pageIndex = 0;
			updateModel();
			this.selectedIndex = -1; 
		}
	} 

	public List<Integer> getPageIndexes() {
		return pageIndexes;
	}

	public void goToPage(int pageIndex) {
		pageIndex--;

		if(pageIndex * wrappedList.getPageSize() < wrappedList.size() && (pageIndex >= 0)) {
			this.pageIndex = pageIndex;
			updateModel();
			this.selectedIndex = -1; 
		}
	}

	private boolean updateModel() {
		int fromIndex = pageIndex * wrappedList.getPageSize();
		int toIndex = fromIndex + wrappedList.getPageSize();

		if (wrappedList.get(fromIndex) == null)
			return false;

		model.clear();

		for (int z=fromIndex; z<toIndex && z<wrappedList.size(); z++) {
			T element = wrappedList.get(z);
			if (element!=null)
				model.add(element);
		}
		return true;
	}

	public int getCurrentPage() {
		return pageIndex + 1;
	} 

	// Idatamodel metods

	@Override
	public Object getFullList() {
		return this.wrappedList.asNormalList();
	}

	public List<T> getList() {
		return this.wrappedList;
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
		
		if (selectedIndex != -1l && selectedIndex<model.size()) {
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

	/**
	 * re-orders list by given expression
	 * 
	 * @param expression - ordering expression
	 * @throws PhiException 
	 */
	public void orderBy(String expression) throws PhiException {
		//flip orderDirection (datagrid column button)
		if (orderDirection == OrderBy.descending) {
			orderDirection = OrderBy.ascending;
		} else {
			orderDirection = OrderBy.descending;
		}
		orderBy(expression, orderDirection.name());
	}
	
	public void orderBy(String expression, String trueOrdering) throws PhiException {
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
		
		wrappedList.orderBy(orderedBy, orderDirection);
		goToPage(1);
		updateModel();
	}
}