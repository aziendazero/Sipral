package com.phi.parameters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.entities.actions.CodeValueParameterAction;
import com.phi.entities.dataTypes.CodeValueParameter;

/**
 * Manages reading and evaluating of φ parameters stored in CodeValueParameter entity.
 * 
 * @author Alex
 */

@BypassInterceptors
@Name("ParameterManager")
@Scope(ScopeType.SESSION)
@AutoCreate
public class ParameterManager implements Serializable {
	
	private static final Logger log = Logger.getLogger(ParameterManager.class);

	private static final long serialVersionUID = 4366487502418504438L;
	
	private HashMap<String,HashMap<String, Object>> param = new HashMap<String,HashMap<String, Object>>();
	
	private List<SelectItem> usernamesSeletItems = new ArrayList<SelectItem>();
	
	private static final String parameterHql = "SELECT p FROM CodeValueParameter p LEFT JOIN FETCH p.children WHERE p.type = 'A'";
	
	private static final String employeeHql = "SELECT e.username FROM Employee e WHERE e.username is not null";
	
    /**
     * Evaluate φ parameters for current user, current sdl and current host
     * @throws PhiException
     */
    @Create
    public void init() throws PhiException {
    	try {
	//    	CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
	    	
	//    	Criteria parameterCriteria = ca.createCriteria(CodeValueParameter.class);
	//		parameterCriteria.add( Restrictions.eq("type", "A" ) );
			
	//		List<CodeValueParameter> paramLst = parameterCriteria.list();
	    	
			GenericAdapterLocalInterface ca = GenericAdapter.instance();
	
			List<CodeValueParameter> paramLst = (List)ca.readObject(parameterHql);
			
			CodeValueParameterAction cvpa = CodeValueParameterAction.instance();
			
			for (CodeValueParameter cvp : paramLst) {
				HashMap<String, Object> evaluatedParameter = cvpa.evaluate(cvp);
				param.put(cvp.getOid(), evaluatedParameter);
			}
			
			Contexts.getSessionContext().set("Param" , param);
			
			
	//		Criteria usernames = ca.createCriteria(Employee.class);
	//		
	//		List<String> lst = usernames    
	//	        .setProjection(Projections.projectionList()
	//	        	.add(Projections.property("username").as("username"))
	////	        	.add(Projections.property("name.fam").as("fam"))
	////	        	.add(Projections.property("name.giv").as("giv"))      
	//	        )
	////	        .addOrder(Order.asc("name.fam"))
	//	        .add(Restrictions.isNotNull("this.username"))
	//	        .list();
			
		} catch (Exception e) {
			log.error("Error initializing ParameterManager", e);
		}
    }
	
    /**
     * @return evaluated φ parameters
     */
//    @Factory
    public HashMap<String,HashMap<String, Object>> getParam() {
    	return param;
    }
    
    public static ParameterManager instance() {
        return (ParameterManager) Component.getInstance(ParameterManager.class, ScopeType.SESSION);
    }
    
	//Select items for parametrization
	
	public List<SelectItem>  getDataTypes() {

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		
		selectItems.add(new SelectItem("boolean", "Boolean"));
		selectItems.add(new SelectItem("date", "Date"));
		selectItems.add(new SelectItem("employee", "Employee"));
	
		return selectItems;
	}
	
	public List<SelectItem>  getBoolean() {

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		
		selectItems.add(new SelectItem(true, "True"));
		selectItems.add(new SelectItem(false, "False"));
	
		return selectItems;
	}
	
	public List<SelectItem>  getDate() {

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		
		selectItems.add(new SelectItem(true, "Current"));
		selectItems.add(new SelectItem(false, "Value"));
	
		return selectItems;
	}
	
	public List<SelectItem>  getEmployee() {

		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
		
		selectItems.add(new SelectItem(true, "Logged user"));
		selectItems.add(new SelectItem(false, "Value"));
	
		return selectItems;
	}
	
	public List<SelectItem>  getUsernames() {
		if (usernamesSeletItems == null || usernamesSeletItems.isEmpty()) {
			try {
				GenericAdapterLocalInterface ca = GenericAdapter.instance();
				List<String> lst = (List)ca.readObject(employeeHql);

				//usernamesSeletItems.clear();

				for (String row : lst) {
					if (row != null) {
						usernamesSeletItems.add(new SelectItem(row, row/*row[1] + " " + row[2]*/));
					}
				}
			} catch (Exception e) {
				log.error("Error getting Usernames", e);
			}
		}
		return usernamesSeletItems;
	}
	
	public Object getParameter(String paramId, String paramKey) throws PhiException {
		Object result = null;
		Object param = getParam().get(paramId);
		if(param != null){
			try {
				result = HashMap.class.cast(param).get(paramKey);
			} catch (Exception e) {
				throw new PhiException(e.getMessage(), e.getCause(), ErrorConstants.PERSISTENCE_GENERIC_ERR_CODE);
			}
		}
		return result;
	}

//	
//	//@Factory
//	public List<SelectItem>  getSdlocs() {
//
//		Criteria sdlocs = ca.createCriteria(ServiceDeliveryLocation.class);
//		
//		List<Object[]> lst = sdlocs    
//	        .setProjection(Projections.projectionList()
//	        	.add(Projections.property("internalId").as("internalId"))
//	        	.add(Projections.property("name.giv").as("giv"))      
//	        )
//	        .addOrder(Order.asc("name.giv"))
//	        .list();
//		
//		
//		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
//		for (Object[] row : lst) {
//			selectItems.add(new SelectItem(row[0], row[1].toString()));
//		}
//
//		return selectItems;
//	}
	
//	public List<SelectItem> getCustomers() {
//
//		List<SelectItem> selectItems =  new ArrayList<SelectItem>();
//		
//		//FIXME: read from seam.properties
//		selectItems.add(new SelectItem("core", "CORE"));
//		selectItems.add(new SelectItem("modena", "MODENA"));
//		selectItems.add(new SelectItem("treviso", "TREVISO"));
//		selectItems.add(new SelectItem("vco", "VCO"));
//	
//		return selectItems;
//	}

}