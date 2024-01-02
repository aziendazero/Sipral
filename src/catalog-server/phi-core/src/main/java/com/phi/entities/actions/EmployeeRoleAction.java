package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PersistenceException;
import com.phi.entities.baseEntity.EmployeeRole;
import com.phi.entities.role.ServiceDeliveryLocation;
import com.phi.security.UserBean;

@BypassInterceptors
@Name("EmployeeRoleAction")
@Scope(ScopeType.CONVERSATION)
public class EmployeeRoleAction extends BaseAction<EmployeeRole, Long> {

	private static final long serialVersionUID = -5092382867539591845L;

    public static EmployeeRoleAction instance() {
        return (EmployeeRoleAction) Component.getInstance(EmployeeRoleAction.class, ScopeType.CONVERSATION);
    }
    
    
 

	public void copyRoleEmployee(EmployeeRole empRole,EmployeeRole empSearchRole){
		if(empSearchRole!=null ){
			if(	empSearchRole.getEnabledServiceDeliveryLocations()!=null && 	empSearchRole.getEnabledServiceDeliveryLocations().size()>0){
				empRole.setEnabledServiceDeliveryLocations(null);
				for(ServiceDeliveryLocation enabledServiceDeliveryLocation:empSearchRole.getEnabledServiceDeliveryLocations()){
					empRole.addEnabledServiceDeliveryLocations(enabledServiceDeliveryLocation);
				}
			}
			

				empRole.setCreatedBy(UserBean.instance().getCurrentEmployee().getUsername());
	
	
		}
	}
	
	//** try to remove an employee Role, if it's used exception is raised.
	// avoid to show error page at final user
	public boolean tryToRemove() {
		
		try {
			//unlink:
			entity.getEmployee().removeEmployeeRole(entity);
			
			//FIXME: usare generic adapter e fare merge
			ca.delete(entity);
			ca.flushSession();
		} catch (PersistenceException e) {
			return false;
		}
		
		return true;
		
		
	}
}