package com.phi.entities.actions;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.faces.model.SelectItem;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PersistenceException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.util.RandomPasswordGenerator;
import com.phi.cs.view.bean.FunctionsBean;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.role.Employee;
import com.phi.parameters.ParameterManager;
import com.phi.security.Security;

@BypassInterceptors
@Name("EmployeeAction")
@Scope(ScopeType.CONVERSATION)
public class EmployeeAction extends BaseAction<Employee, Long> {

	private static final long serialVersionUID = -5092382867539591845L;
	private static final Logger log = Logger.getLogger(EmployeeAction.class);

    public static EmployeeAction instance() {
        return (EmployeeAction) Component.getInstance(EmployeeAction.class, ScopeType.CONVERSATION);
    }
    
	/**
* From PHI CI -> FIXMEEEEEEEEEEEEEEE
*/
    @Deprecated
    public void savePassword(String actionString){
        RepositoryManager rm = RepositoryManager.instance();
        boolean useMD5password = rm.isUsing_MD5_passwords();
        Employee employee = EmployeeAction.instance().getEntity();
        
        if("reset".equals(actionString)){
        
        	if(useMD5password)
        		employee.setPassword(Security.crypt("12345678"));
        	else employee.setPassword("12345678");
    
        }
        
    }
	
    //set password passed in clear text, as current Employee password
    //the password is automatically encrypted if the solution use MD5 pass configuration. 
    public void setPassword(String clearTextPassword){
    	
    	boolean useMD5password = RepositoryManager.instance().isUsing_MD5_passwords();
        Employee employee = getEntity();
        
        if(useMD5password)
    		employee.setPassword(Security.crypt(clearTextPassword));
    	else 
    		employee.setPassword(clearTextPassword);
    }
    
    public String setRandomPassword(){
    	
    	boolean useMD5password = RepositoryManager.instance().isUsing_MD5_passwords();
        Employee employee = getEntity();
        String password = RandomPasswordGenerator.generateRandomPassword(8);
        
        if(useMD5password)
    		employee.setPassword(Security.crypt(password));
    	else 
    		employee.setPassword(password);
        return password;
    }
    
    /**
	 * 	Useful: use it to set distinct filter in reads and most important to remove
	 * 	entityCriteria.setResultTransformer(new AliasToEntityMapResultTransformer(entityClass))
	 * 	(which is useless with distinct btw). 
	 *  Example of application: in SSA Employee Management process: if you set a filter on Employee.employeeRole.code
	 *  and an employee has 3 roles, the read will return 3 results...so you need to set distinct in this way.
	 * 
	 */
	public void setDistinctBean(){
		entityCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
	}

    //looking to current Employee, check if one of the role of the employee is coded with given string
	public boolean haveRoleCode(String roleCode) {
		
		if (roleCode== null || roleCode.isEmpty())
			return false;
		
		getEntity();
		if (entity == null) 
			return false;
	
		return haveRoleCode(entity, roleCode);
	}
	
	public boolean haveRoleCode(Employee emp, String roleCode) {
	 if (emp == null || emp.getEmployeeRole() == null || emp.getEmployeeRole().isEmpty())
		 return false;
	 
	for (EmployeeRole r : emp.getEmployeeRole()) {
		if (roleCode.equals(r.getCode().getCode())) {
			return true;
		}
	}
	
	return false;
	}

	public EmployeeRole currentRoleCode(String roleCode) {

		if (roleCode== null || roleCode.isEmpty())
			return null;

		getEntity();
		if (entity == null) 
			return null;

		return currentRoleCode(entity, roleCode);
	}

	public EmployeeRole currentRoleCode(Employee emp, String roleCode) {
		if (emp == null || emp.getEmployeeRole() == null || emp.getEmployeeRole().isEmpty())
			return null;

		for (EmployeeRole r : emp.getEmployeeRole()) {
			if (roleCode.equalsIgnoreCase(r.getCode().toString()) ){
				return r;
			}
		}

		return null;
	}
	
	/*
	 * Method to check if an employee has a valid role, of specific code. It can check or not different attributes, according to booleans passed. 
	 */
	public boolean hasValidRole (Employee emp, String specificRoleCode, Date pastDate, boolean checkEmpActive, boolean checkEmpDate, boolean checkRoleActive, boolean checkRoleDate) {
		
		if (checkEmpActive && !emp.getIsActive()) {
			return false;
		}
		
		Date dateCheck = new Date();
		if (pastDate != null)
			dateCheck = pastDate;
		
		FunctionsBean f = FunctionsBean.instance();
		if (checkEmpDate && !f.dateContained(dateCheck, emp.getEffectiveTime())) {
			return false;
		}
		
		if (specificRoleCode == null && !checkRoleActive && !checkRoleDate) {
			//requested only check on employee validity. Return true!
			return true;
		}
		
		List<EmployeeRole> roles = emp.getEmployeeRole();
		
		if (roles == null || roles.isEmpty()) {
			return false;
		}
		
		
		for (EmployeeRole rol : roles ) {
			if (specificRoleCode != null && !rol.getCode().getCode().equals(specificRoleCode))
				continue;

			//role has specificRoleCode, or all role are checked.
			if (!isValidRole(rol, dateCheck, checkRoleActive, checkRoleDate)){
				continue;
			}
			
			//role code is ok (or any role is ok), validity is ok (or not to be checked), date are valid (or not to be checked)
			return true;
		}
		
		
		return false;
		
	}
	
	/*
	 * Method to check if a role is valid in a specifc date. It can check or not different attributes, according to booleans passed. 
	 */
	public boolean isValidRole (EmployeeRole rol, Date pastDate, boolean checkRoleActive, boolean checkRoleDate) {
		
		Date dateCheck = new Date();
		if (pastDate != null)
			dateCheck = pastDate;
		
		if (checkRoleActive && !rol.getIsActive()) {
			return false;
		}
		
		FunctionsBean f = FunctionsBean.instance();
		if (checkRoleDate && !f.dateContained(dateCheck, rol.getEffectiveTime())) {
			return false;
		}
		
		return true; 
	}
	
	


	/*
	 * input List of HashMap is a list of EmployeeRoles, selecting (filter on code is not needed):
	 * 
	 * 		<action name="1" expression="#{EmployeeRoleAction.equal.put('code.displayName','MEDICO')}"></action>
	 * 		<action name="2" expression="#{EmployeeRoleAction.orderBy.put('employee.name.fam','ascending')}"></action>
	 *		<action name="3" expression="#{EmployeeRoleAction.setDistinct(true)}"></action>
	 *		<action name="4" expression="#{EmployeeRoleAction.select.add('isActive')}"></action>
	 *		<action name="5" expression="#{EmployeeRoleAction.select.add('effectiveTime.low')}"></action>
	 *		<action name="6" expression="#{EmployeeRoleAction.select.add('effectiveTime.high')}"></action>
	 *		<action name="7" expression="#{EmployeeRoleAction.select.add('employee.internalId')}"></action>
	 *		<action name="8" expression="#{EmployeeRoleAction.select.add('employee.isActive')}"></action>
	 *		<action name="9" expression="#{EmployeeRoleAction.select.add('employee.effectiveTime.low')}"></action>
	 *		<action name="10" expression="#{EmployeeRoleAction.select.add('employee.effectiveTime.high')}"></action>
	 *		<action name="11" expression="#{EmployeeRoleAction.select.add('employee.name.fam')}"></action>
	 *		<action name="12" expression="#{EmployeeRoleAction.select.add('employee.name.giv')}"></action>
	 *		<action name="13" expression="#{EmployeeRoleAction.select.add('employee.username')}"></action>
	 *		<action name="14" expression="#{EmployeeRoleAction.read()}"></action>
	 * 
	 *  The method returns a list of selectitems with disabled selectItem, where role is not morevalid, or employee validity is not more valid.
	 */
	
	public List<SelectItem> employeeList (List<HashMap<String,Object>>  roles) {
		
		List<SelectItem> employeeCombo = new ArrayList<SelectItem>();
		FunctionsBean f = FunctionsBean.instance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		
		for (HashMap role : roles) {
			String label = ""; 
			HashMap <String, Object> emp = (HashMap <String, Object>)role.get("employee");
			
			if (emp.get("name") != null && emp.get("username") != null) { 
				label = ((HashMap)emp.get("name")).get("fam") + " " + ((HashMap)emp.get("name")).get("giv") +  " ("+emp.get("username")+")";
			}
				
			SelectItem sel = new SelectItem("com.phi.entities.role.Employee-"+emp.get("internalId"), label );
			
			Date empLow = (Date)((HashMap)emp.get("effectiveTime")).get("low");
			Date empHigh = (Date)((HashMap)emp.get("effectiveTime")).get("high");
			Boolean empIsActive = (Boolean)emp.get("isActive"); 
			
			if (!empIsActive || !f.dateContained(new Date(), empLow, empHigh)) {
				sel.setLabel(sel.getLabel()+ " [ "+ (!empIsActive ? "not active" :  sdf.format(empLow) + " - " + (empHigh==null?"":sdf.format(empHigh)))+ " ]");
				sel.setDisabled(true);
			}
			
			Date roleLow = (Date)((HashMap)role.get("effectiveTime")).get("low");
			Date roleHigh = (Date)((HashMap)role.get("effectiveTime")).get("high");
			Boolean roleIsActive = (Boolean)role.get("isActive"); 
			
			if (!roleIsActive || !f.dateContained(new Date(), roleLow, roleHigh)) {
				//change label of disabled item, to let user understand cause of disabling
				sel.setLabel(sel.getLabel()+ " [ "+ (!roleIsActive ? "not active" :  sdf.format(roleLow) + " - " + (roleHigh==null?"":sdf.format(roleHigh)))+ " ]");
				sel.setDisabled(true);
			}
			
			employeeCombo.add(sel);
		}
		
		return employeeCombo;
	}
	
	public List<SelectItem> getEmployees () throws PersistenceException {
		String hql = "SELECT e.internalId, e.name.fam, e.name.giv FROM Employee e";
		List<Object[]> res = ca.executeHQL(hql);
		List<SelectItem> employeeSelItem = new ArrayList<SelectItem>();
		for (Object[] emp : res) {
			employeeSelItem.add(new SelectItem("com.phi.entities.role.Employee-"+emp[0], emp[1] + " " + emp[2]));
		}
		return employeeSelItem;
	}
	
	//not workign as annotation, needed to be call from process.
	public void onPrePersist() {
		
		String signClearPassword = getEntity().getSignClearPassword();
		
		if (signClearPassword == null || signClearPassword.isEmpty()){
			entity.setSignPassword(signClearPassword);
			return;
		}
		
		try {
			String key = (String)ParameterManager.instance().getParameter("p.employees.globalsignsecret", "value");
			if (key == null || key.isEmpty()) {
				key = "Sign4tur3secretZ"; //"Bar12345Bar12345";
			}
			 

			//Create key and cipher
			Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance("AES");
			
			// encrypt the text
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			
			// encoded byte array, multiple of 16 byte!!
			byte[] encrypted = cipher.doFinal(signClearPassword.getBytes());
			
			//transform in Base64 string.
			Base64 codec = new Base64();
	        byte[] encryptedB64 = codec.encode(encrypted);
	        			
			entity.setSignPassword(new String(encryptedB64));
			
		}
		catch(Exception e) {
			log.error("erro encrypting sign password for user "+entity.getUsername());
			e.printStackTrace();
		}
		
	}
	
	
}