package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("DistrettiAction")
@Scope(ScopeType.CONVERSATION)
public class DistrettiAction extends BaseAction<ServiceDeliveryLocation, Long> {
	

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7863519013590428887L;

	public static DistrettiAction instance() {
        return (DistrettiAction) Component.getInstance(DistrettiAction.class, ScopeType.CONVERSATION);
    }

	public DistrettiAction() {
		super();
		conversationName = "Distretti";
	}
	
}