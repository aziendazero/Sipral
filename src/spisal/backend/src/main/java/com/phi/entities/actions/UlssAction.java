package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("UlssAction")
@Scope(ScopeType.CONVERSATION)
public class UlssAction extends BaseAction<ServiceDeliveryLocation, Long> {
	

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 7863519013590428887L;

	public static UlssAction instance() {
        return (UlssAction) Component.getInstance(UlssAction.class, ScopeType.CONVERSATION);
    }

	public UlssAction() {
		super();
		conversationName = "Ulss";
	}
	
}