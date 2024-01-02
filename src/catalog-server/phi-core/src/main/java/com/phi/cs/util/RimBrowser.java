package com.phi.cs.util;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.baseEntity.BaseEntity;



@BypassInterceptors
@Name("RimBrowser")
@Scope(ScopeType.EVENT)
public class RimBrowser {
	
	private String[] skipProperty = {"transientValue","transientValue2","class"};
	private String[] firstColumn = {"internalId", "creationDate"};
	private List<String> skipProperties = Arrays.asList(skipProperty);
	private List<String> firstColumns = Arrays.asList(firstColumn);
	
	
	private String className="";
	private String internalIdString="";
	private long internalId = 0;
	
	
	//whereClauses
	private Map<Integer,String> whereParameter=new HashMap<Integer,String>();
	private Map<Integer,String> whereRestriction=new HashMap<Integer,String>();
	private Map<Integer,String> whereValue=new HashMap<Integer,String>();
	
	
	private String error;
	private String resultNum;
	private int columNum;
	
	private List<Object> results;

	private List<String> columns = new ArrayList<String>();
	//keys for this map are the column name / property name.
	private Map<String,Class> propertyClass = new HashMap<String, Class>();
	private Map<String,Boolean> propertyIsRim = new HashMap<String, Boolean>();
	private Map<String,Boolean> propertyIsListRim = new HashMap<String, Boolean>();
	
	private Map<String,List<String>> columnsCache = new HashMap<String, List<String>>();
	private Map<String, Map<String,Class>> propertyClassCache = new HashMap<String, Map <String, Class>>();
	private Map<String, Map<String,Boolean>> propertyIsRimCache = new HashMap<String, Map <String, Boolean>>();
	private Map<String, Map<String,Boolean>> propertyIsListRimCache = new HashMap<String, Map <String, Boolean>>();
	
	private static List<SelectItem> listItemClass = new ArrayList<SelectItem>();
	
	
	
	public String getInternalIdString() {
		return internalIdString;
	}
	public void setInternalIdString(String internalIdString) {
		this.internalIdString = internalIdString;
		if (internalIdString!= null && !internalIdString.isEmpty())
			internalId=Long.parseLong(internalIdString);
	}
	public long getInternalId() {
		return internalId;
	}
	public void setInternalId(long internalId) {
		this.internalId = internalId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getResultNum() {
		return resultNum;
	}
	public void setResultNum(String resultNum) {
		this.resultNum = resultNum;
	}
	public int getColumNum() {
		return columNum;
	}
	public void setColumNum(int columNum) {
		this.columNum = columNum;
	}
	public List<String> getColumns() {
		return columns;
	}
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	public List<Object> getResults() {
		return results;
	}
	public void setResults(List<Object> results) {
		this.results = results;
	}
	
	
	
	public void read () throws PersistenceException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException, NoSuchFieldException {
		CatalogAdapter catalogAdapter = (CatalogAdapter)Component.getInstance("rimPdm2CA"); 
		if (className.isEmpty())
			className ="com.phi.entities.act.PatientEncounter";
			//return;
		
		String query = "select obj from "+className+" obj";
		
		if (internalId != 0)
			query+=" where internalId ="+internalId;
		
		//dynamic where restrictions, based on whereParameter. 
		//TODO: create on a form a dynamic resctrioction list. 
		if (whereParameter.size() > 0) {
			if (internalId == 0) 
				query += " where ";
			else 
				query += " and ";
			for (Integer clauseId : whereParameter.keySet()) {
				query+= " "+whereParameter.get(clauseId)+ " "+whereRestriction.get(clauseId) +" '"+ whereValue.get(clauseId) + "' and ";
			}
			query=query.substring(0,query.length()-4);
		}
		
		
		
		try {
			List<Object> result = (List<Object>)catalogAdapter.executeHQL(query);
			if (result != null && result.size()>0) {
				resultNum="Found "+result.size()+" records.";
				results= result;
				
				if (columnsCache.containsKey(className)) {
					
					//property are already calculated, get from cache.
					columns=columnsCache.get(className);
					columNum=columns.size();
					propertyClass=propertyClassCache.get(className);
					propertyIsListRim=propertyIsListRimCache.get(className);
					propertyIsRim=propertyIsRimCache.get(className);
					
				}
				else {
					//get a result sample, to draw list of columns, and obtain a dynamic table based on selected result.
					Object sample = result.get(0);
					
					PropertyDescriptor [] pds = PropertyUtils.getPropertyDescriptors(sample.getClass());
					columNum = pds.length-skipProperty.length;
					
					for (String column : firstColumns) {
						columns.add(column);
					}
					for (PropertyDescriptor pd : pds) {
						String propertyName= pd.getName();
						if (!skipProperties.contains(propertyName) && !firstColumns.contains(propertyName))
							columns.add(propertyName);
						
						Class columnClass =pd.getPropertyType();
						
						//propertyClass, propertyIsListRim and propertyIsRim are used during datagrid drawing
						//depending on type of variable (a Class, a RimObject, a RimObjectList) 
						//the render on the cell is different (e.g. for RimObject is displayed the internalId)
						propertyClass.put(propertyName, columnClass);
						propertyIsListRim.put(propertyName, isListRim(propertyName,columnClass,sample.getClass()));
						propertyIsRim.put (propertyName, isRim(columnClass));
						
						//cache calculated value
						columnsCache.put(className,columns);
						propertyClassCache.put(className, propertyClass);
						propertyIsListRimCache.put(className, propertyIsListRim);
						propertyIsRimCache.put(className,propertyIsRim);
						
					}
				}
			}
			else {
				resultNum="No records found.";
			}
		} catch (PersistenceException e) {
			error="ERROR executing hql: \n"+e.toString()+"\n"+e.getMessage();
		}
		
	}
	
	
	//get the value, of a specific property, of one object. 
	//if the property is a relationship with another rim object (or list<RimObjct>) 
	//the internalId (or comma separated list) is provided.
	public String get(Object obj, String property) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Object val = PropertyUtils.getProperty(obj, property);
		if (val == null)
			return "";
		
		if (propertyIsListRim.get(property)) {
			Collection coll = (Collection)val;
			Iterator it = coll.iterator();
			String ret="";
			while (it.hasNext()) {
				Object element = it.next();
				ret+=((BaseEntity)element).getInternalId()+",";
			}
			if (ret.length() >0)
				ret=ret.substring(0, ret.length()-1);
			return ret;
			
		}
		
		if (propertyIsRim.get(property)) {
			return ((BaseEntity)val).getInternalId()+"";
		}
		
		return val.toString();
	}
	
	
	//Return true if the class provided is a class extending a RimObjct or it's a list of class extending rimObject.
	public boolean isRim(Class clazz) {
		
		if (clazz.isAssignableFrom(Collection.class)  || clazz.isAssignableFrom(List.class) ) {
			return false;
		}
		else {
			return BaseEntity.class.isAssignableFrom(clazz);
		}
	}
	
	public boolean isListRim(String property, Class clazz, Class fatherClass) throws SecurityException, NoSuchFieldException {
		
		if (clazz.isAssignableFrom(Collection.class)  || clazz.isAssignableFrom(List.class) ) {
		
			Field field;
			try {
				field = fatherClass.getDeclaredField(property);
			} catch (NoSuchFieldException e) {
				if (fatherClass.getName().equals("RimObject") || fatherClass.getName().equals("RimObject") )
					throw new NoSuchFieldException() ;
				return isListRim(property, List.class, fatherClass.getSuperclass());
			}
			Type genericFieldType = field.getGenericType();
			    
			if(genericFieldType instanceof ParameterizedType){
			    ParameterizedType aType = (ParameterizedType) genericFieldType;
			    Type[] fieldArgTypes = aType.getActualTypeArguments();
			    for(Type fieldArgType : fieldArgTypes){
			        Class fieldArgClass = (Class) fieldArgType;
			        return isRim(fieldArgClass);
			    }
			}
		}

		return false;
	}
	
	public static List<SelectItem> getListItemClass() throws IOException {
		List<SelectItem> ret = new ArrayList<SelectItem>();
		listItemClass.clear(); 
		if (listItemClass != null && !listItemClass.isEmpty()) { 
			return listItemClass;
		}
		else {
			HashMap<String, Class<BaseEntity>> rims = BaseEntity.getDerivedClasses();
			
			
			for (String key : rims.keySet()) {
				if (!key.contains("$")) {
					SelectItem sel = new SelectItem(key, (rims.get(key)).getCanonicalName());
					listItemClass.add(sel);
				}
			}
			
			Collections.sort(listItemClass, new SelectItemComparator());
		}
		return listItemClass;
	}
	
//	public List<SelectItem> getClassListItems() throws IOException {
//		List<String> list = getClassList();
//		List<SelectItem> ret = new ArrayList<SelectItem>();
//		for (String cl : list) {
//			SelectItem sel = new SelectItem(cl,cl);
//			ret.add(sel);
//		}
//		
//		return ret;
//	}
	
	
	
}

class SelectItemComparator implements Comparator<SelectItem> {

	@Override
	public int compare(SelectItem si1, SelectItem si2) {
		return si1.getLabel().compareToIgnoreCase(si2.getLabel());
	}
	
}
