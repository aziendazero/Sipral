package com.phi.entities.actions;

import com.phi.entities.baseEntity.Atto;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("AttoAction")
@Scope(ScopeType.CONVERSATION)
public class AttoAction extends BaseAction<Atto, Long> {

	private static final long serialVersionUID = 1119355014L;

	public static AttoAction instance() {
		return (AttoAction) Component.getInstance(AttoAction.class, ScopeType.CONVERSATION);
	}


}