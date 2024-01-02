package com.phi.entities.actions;

import com.phi.entities.baseEntity.Sospensione;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("SospensioneAction")
@Scope(ScopeType.CONVERSATION)
public class SospensioneAction extends BaseAction<Sospensione, Long> {

	private static final long serialVersionUID = 1340774537L;

	public static SospensioneAction instance() {
		return (SospensioneAction) Component.getInstance(SospensioneAction.class, ScopeType.CONVERSATION);
	}


}