package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.role.Person;

@BypassInterceptors
@Name("DiffAction")
@Scope(ScopeType.CONVERSATION)
public class DiffAction extends BaseAction<Person, Long> {
	
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -1098825690440838400L;

	public static DiffAction instance() {
        return (DiffAction) Component.getInstance(DiffAction.class, ScopeType.CONVERSATION);
    }

	public DiffAction() {
		super();
		conversationName = "Diff";
	}
	

}