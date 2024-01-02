package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Tariffario;
import com.phi.entities.role.ServiceDeliveryLocation;

@BypassInterceptors
@Name("TariffarioLastAction")
@Scope(ScopeType.CONVERSATION)
public class TariffarioLastAction extends BaseAction<Tariffario, Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -3544945279884538937L;

	public static TariffarioLastAction instance() {
        return (TariffarioLastAction) Component.getInstance(TariffarioLastAction.class, ScopeType.CONVERSATION);
    }

	public TariffarioLastAction() {
		super();
		conversationName = "TariffarioLast";
	}
	
}