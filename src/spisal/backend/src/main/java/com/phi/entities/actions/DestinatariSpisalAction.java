package com.phi.entities.actions;

import com.phi.entities.baseEntity.DestinatariSpisal;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DestinatariSpisalAction")
@Scope(ScopeType.CONVERSATION)
public class DestinatariSpisalAction extends BaseAction<DestinatariSpisal, Long> {

	private static final long serialVersionUID = 627185611L;

	public static DestinatariSpisalAction instance() {
		return (DestinatariSpisalAction) Component.getInstance(DestinatariSpisalAction.class, ScopeType.CONVERSATION);
	}


}