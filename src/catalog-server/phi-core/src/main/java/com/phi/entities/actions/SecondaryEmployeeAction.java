package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.role.Employee;

@BypassInterceptors
@Name("SecondaryEmployeeAction")
@Scope(ScopeType.CONVERSATION)
public class SecondaryEmployeeAction extends BaseAction<Employee, Long> {

	private static final long serialVersionUID = -5092382867539591845L;

    public static SecondaryEmployeeAction instance() {
        return (SecondaryEmployeeAction) Component.getInstance(SecondaryEmployeeAction.class, ScopeType.CONVERSATION);
    }
	
//    @Override
//    protected void init() {
//    	//Initialize valid date to creation date
////    	put("effectiveTime/low", new Date());
//    }

}