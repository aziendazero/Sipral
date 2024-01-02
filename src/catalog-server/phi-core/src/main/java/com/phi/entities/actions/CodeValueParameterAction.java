package com.phi.entities.actions;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.catalog.adapter.GenericAdapter;
import com.phi.cs.catalog.adapter.GenericAdapterLocalInterface;
import com.phi.entities.dataTypes.CodeValueParameter;
import com.phi.entities.dataTypes.CodeValueRole;
import com.phi.entities.role.Employee;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("CodeValueParameterAction")
@Scope(ScopeType.CONVERSATION)
public class CodeValueParameterAction extends CodeValueBaseAction<CodeValueParameter>{

	private static final long serialVersionUID = -1841224743739985390L;
	private static final Logger log = Logger.getLogger(CodeValueParameterAction.class);
	
	public static CodeValueParameterAction instance() {
		return (CodeValueParameterAction) Component.getInstance(CodeValueParameterAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Evaluates a parameter returning defaultValue 
	 * Value depends on username, host and location
	 * @param parameter
	 * @return
	 */
	public Object evaluate(String parameter) {
		try {
			GenericAdapterLocalInterface ca = GenericAdapter.instance();
			CodeValueParameter p = ca.get(CodeValueParameter.class, parameter);
			
			return evaluate(p);
		} catch (Exception e) {
			log.error("Error evaluating parameter " + parameter, e);
		}
		return null;
	}
	
	/**
	 * Evaluate φ parameter: select child of parameter based on current data in following order:
	 *  1 username
	 *  2 host
	 *  3 role
	 *  4 sdlId
	 *  5 customer
	 * @param p parameter to evaluate
	 * @return evaluated data
	 */
	public HashMap<String, Object> evaluate(CodeValueParameter p) {

		UserBean ub = UserBean.instance();
		
		String username = ub.getUsername();
		
		String role = ub.getRole();
		
		String host = ub.getRemoteHost();
		
		Long sdlId = null;
		
		if (ub.getSdLocs() != null && !ub.getSdLocs().isEmpty()) {
			sdlId = ub.getSdLocs().get(0); //FIXME
		}
		
//		Context app = Contexts.getApplicationContext();	
//		String customer = (String)app.get(CsConstants.CUSTOMER);

		return evaluate(p, username, role, host, sdlId/*, customer*/);
	}
	
	public HashMap<String, Object> evaluate(CodeValueParameter p, Long sdlId) {
		return evaluate(p, null, null, null, sdlId);
	}
		
	protected HashMap<String, Object> evaluate(CodeValueParameter p, String username, String role, String host, Long sdlId/*, String customer*/) {

		
		HashMap<String, Object> evaluatedParameter = new HashMap<String, Object>();
		
		try {
			
			CodeValueParameter activeParameter = findParameterFor(p, username, role, host, sdlId);
			
//			Collection<CodeValueParameter> pOptions = (Collection)p.getChildren();
//			
//			CodeValueParameter activeParameter = p;
//
//			if (!pOptions.isEmpty()) {
//				for (CodeValueParameter opt : pOptions) {
//					if (username.equals(opt.getUsername())) {
//						activeParameter = opt;
//					}
//					if (host.equals(opt.getHost())) {
//						activeParameter = opt;
//					}
//					if (opt.getEnabledRole() != null && role.equals(opt.getEnabledRole().getCode())) {
//						activeParameter = opt;
//					}
//					if (opt.getDisabledRole() != null && !role.equals(opt.getDisabledRole().getCode())) {
//						activeParameter = opt;
//					}
//					if (sdlId!= null && opt.getLocation() != null && !opt.getLocation().isEmpty() && sdlId.equals(new Long(opt.getLocation()))) {
//						activeParameter = opt;
//					}
////					if (customer != null && opt.getCustomer() != null && customer.equalsIgnoreCase(opt.getCustomer())) {
////						activeParameter = opt;
////					}
//				}
//			}

			//convert to type
			Object value = activeParameter.getValue();
			if (activeParameter.getCurrentValue() != null && p.getDataType() != null ) {
				if (p.getDataType().equals("boolean")) {
					value = new Boolean(value.toString()); 
				} else if (p.getDataType().equals("date")) {
					if (Boolean.TRUE.equals(activeParameter.getCurrentValue())) {
						value = new Date(); 
					} else if (value!=null) {
						try {
							value = Date.parse(value.toString());
						} catch (Exception e) {
							log.warn("Parameter " + activeParameter.getOid() + " of type Date is invalid " + value);
						}
					}
				} else if (p.getDataType().equals("employee")) {
					if (Boolean.TRUE.equals(activeParameter.getCurrentValue())) {
						value = UserBean.instance().getCurrentEmployee();
					} else {
						//value = ca.get(Employee.class, new Long(value.toString())); //FIXME change into username
						GenericAdapterLocalInterface ca = GenericAdapter.instance();
						Criteria employeeCrit = ca.createCriteria(Employee.class);
						employeeCrit.add(Restrictions.eq("this.username", value.toString()));
						value = employeeCrit.uniqueResult();
					}
				} else if (p.getDataType().equals("double")) {
					try {
						if (value == null || ((String)value).trim().isEmpty() ) {
							value = 0d;
						} 
						else {
							value = Double.parseDouble(((String)value).trim());
						}
						
					}catch (NumberFormatException e){
						log.error("unable to convert parameter "+value + " to Double. Set to 0.0 double");
						value = 0d;
					}
				}
			}
			
			evaluatedParameter.put("value", value);
			evaluatedParameter.put("visible", activeParameter.getVisible());
			evaluatedParameter.put("readOnly", activeParameter.getReadOnly());
			evaluatedParameter.put("required", activeParameter.getRequired());
			evaluatedParameter.put("validFrom", activeParameter.getValidFrom());
			evaluatedParameter.put("validTo", activeParameter.getValidTo());
			
		} catch (Exception e) {
			log.error("Error evaluating parameter" + p + " username: " + username + " role: " + role + " host: " + host + " sdlId: " + sdlId, e);
		}
		
		return evaluatedParameter;
	}
	
	/**
	 * Return map of parameters by role
	 * Used by role management process to edit parameters connected to a process
	 * @param params list of default parameters
	 * @param role 
	 * @return
	 */
	public Map<String, CodeValueParameter> findParametersFor(List<CodeValueParameter> params, String role) {
		
		UserBean ub = UserBean.instance();
		
		String username = ub.getUsername();
		
		String host = ub.getRemoteHost();
		
		Long sdlId = null;
		
		if (ub.getSdLocs() != null && !ub.getSdLocs().isEmpty()) {
			sdlId = ub.getSdLocs().get(0); //FIXME
		}
		
		Map<String, CodeValueParameter> res = new HashMap<String, CodeValueParameter>();
		
		for (CodeValueParameter p : params) {
			res.put(p.getId(), findParameterFor(p, username, role, host, sdlId));
		}
		
		return res;
	}
	
	protected CodeValueParameter findParameterFor(CodeValueParameter p, String username, String role, String host, Long sdlId) {
		Collection<CodeValueParameter> pOptions = (Collection)p.getChildren();
		
		CodeValueParameter activeParameter = p;

		try {
			if (!pOptions.isEmpty()) {
				GenericAdapterLocalInterface ca = GenericAdapter.instance();
				for (CodeValueParameter opt : pOptions) {
					if (username!= null && username.equals(opt.getUsername())) {
						activeParameter = opt;
					}
					if (host!= null && host.equals(opt.getHost())) {
						activeParameter = opt;
					}
//					CodeValueRole cvEnabledRole = opt.getEnabledRole();
//					if (cvEnabledRole != null) {
//						cvEnabledRole = ca.get(CodeValueRole.class, cvEnabledRole.getId());
//						if(role.equals(cvEnabledRole.getCode())) {
//							activeParameter = opt;
//						}
//					}
						if (role != null) {
							CodeValueRole cvRole = opt.getRole();
							if (cvRole != null) {
								cvRole = ca.get(CodeValueRole.class, cvRole.getId());
								if(role.equals(cvRole.getCode())) {
									activeParameter = opt;
								}
							}
						}
						
						String location = opt.getLocation();
						
						if (sdlId!= null && location != null && location.contains("-") && sdlId.equals(new Long(opt.getLocation().split("-")[1]))){
							Date validFrom = opt.getValidFrom();
							Date validTo = opt.getValidTo();
							Date today = new Date();
							
							if (validFrom!=null){													
								if (today.after(validFrom) && (validTo==null || today.before(validTo)))
									activeParameter = opt;	
							} 
						}
//				if (customer != null && opt.getCustomer() != null && customer.equalsIgnoreCase(opt.getCustomer())) {
//					activeParameter = opt;
//				}
				}
			}
		} catch (Exception e) {
			log.error("Error findParameterFor ", e);
		}
		return activeParameter;
	}
	
}