package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("LineeAction")
@Scope(ScopeType.CONVERSATION)
public class LineeAction extends BaseAction<ServiceDeliveryLocation, Long> {
	


	/**
	 * 
	 */
	private static final long serialVersionUID = -8069335531506884072L;

	public static LineeAction instance() {
        return (LineeAction) Component.getInstance(LineeAction.class, ScopeType.CONVERSATION);
    }

	public LineeAction() {
		super();
		conversationName = "Linee";
	}
	
}