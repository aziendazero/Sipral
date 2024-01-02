package com.phi.cs.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.mapping.Array;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.view.bean.FunctionsBean;

@BypassInterceptors
@Name("HQL")
@Scope(ScopeType.EVENT)
public class HQL implements Serializable {
	
	private String query;
	private String error;
	private String resultNum;
	private int columNum;
	private List<String> columns;


	private List<Object> results;
	
	private final static Logger log = Logger.getLogger(HQL.class);

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	//=== only getters === //
	
	public String getError() {
		return error;
	}
	
	public String getResultNum() {
		return resultNum;
	}

	public int getColumNum() {
		return columNum;
	}

	public List<String> getColumns() {
		return columns;
	}

	public List<Object> getResults() {
		return results;
	}

	
	
	public void executeQuery() {
		error=null;
		resultNum=null;
		results=null;
		
		if (query == null || query.equals("")) {
			error ="empty query.";
			return;
		}
		
		String cleanQ = query.toLowerCase().trim();
		if (cleanQ.startsWith("update") || cleanQ.startsWith("insert") || cleanQ.startsWith("delete")  ) {
			error ="Only select queries are allowed";
			return;
		}
		
		CatalogAdapter catalogAdapter = (CatalogAdapter)Component.getInstance("rimPdm2CA");
		try {
			List<Object> result = (List<Object>)catalogAdapter.executeHQL(query);
			if (result != null && result.size()>0) {
				resultNum="Found "+result.size()+" records.";
				results= result;
				
				
				Object firstRow = result.get(0);
				if (firstRow instanceof Object[])  {
					columNum = ((Object[])firstRow).length;
				}
				else 
					columNum = 1;
				columns = Arrays.asList(getColumnsId(query,columNum));
					
				
			}
			else {
				resultNum="No records found.";
			}
		} catch (PersistenceException e) {
			error="ERROR executing hql: \n"+e.toString();
		}
		
	}

	
	
	private String[] getColumnsId(String query, int columNum) {
		String[] ret = new String[columNum];
		
		if (query == null || query.equals("") || columNum <1) {
			return ret;
		}
		
		String qLow = query.toLowerCase();
		if (!qLow.contains(" from ")) {
			return ret;
		}
		//To be fixed: select of column or alias called from   
		int indexOfFrom = qLow.indexOf(" from ");
		int indexOfSelect = 0;
		if (qLow.contains ("select"))
			indexOfSelect= qLow.indexOf("select");
		
		if (indexOfFrom == -1 && qLow.trim().startsWith("from")) {
			//could be used the object names in from.
			return ret;
		}
		
		String selectQueryPart = query.substring(indexOfSelect+7, indexOfFrom);
		if (selectQueryPart == null || selectQueryPart.equals(""))
			return ret;
		
		String[] selectItems = selectQueryPart.split(",");
		if (selectItems == null  || selectItems.length != columNum)
			return ret;
			
		for (int i=0; i<columNum; i++){
			if (selectItems[i] == null) {
				ret[i]="";
				continue;
			}
			
			String selectItem = selectItems[i].trim();
			
			if (selectItem==null || selectItem.equals("") ) {
				ret[i]="";
				continue;
			}
			
			if (selectItem.contains(" ")) {
				if (selectItem.contains(" as ") ) {
					ret[i]= selectItem.substring(selectItem.indexOf(" as ")+4, selectItem.length());
				}
				else 
					ret[i]= selectItem.substring(0, selectItem.indexOf(" "));
			} else  
				ret[i] = selectItem;
		}
		
		
		return ret;
	}

}
