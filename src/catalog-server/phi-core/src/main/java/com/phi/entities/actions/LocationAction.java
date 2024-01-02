package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("LocationAction")
@Scope(ScopeType.CONVERSATION)
public class LocationAction extends BaseAction<ServiceDeliveryLocation, Long> {

	/**
	 * Used in \MOD_HBS\CORE\PROCESSES\Asl\Manage_All.
	 * Locations are physical structures (SDLoc) linked to WARD and UP through ServiceDeliveryLocation.serviceDeliveryLocation roleLink
	 */
	
	private static final long serialVersionUID = -7640083334347408668L;
	
    public static LocationAction instance() {
        return (LocationAction) Component.getInstance(LocationAction.class, ScopeType.CONVERSATION);
    }
    
	public LocationAction() {
		super("Location");
	}
}
