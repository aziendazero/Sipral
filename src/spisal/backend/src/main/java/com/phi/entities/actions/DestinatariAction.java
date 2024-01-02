package com.phi.entities.actions;

import com.phi.entities.baseEntity.Destinatari;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DestinatariAction")
@Scope(ScopeType.CONVERSATION)
public class DestinatariAction extends BaseAction<Destinatari, Long> {

	private static final long serialVersionUID = 227054859L;

	public static DestinatariAction instance() {
		return (DestinatariAction) Component.getInstance(DestinatariAction.class, ScopeType.CONVERSATION);
	}


}