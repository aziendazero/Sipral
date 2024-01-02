package com.phi.entities.actions;

import com.phi.entities.baseEntity.VisitaExt;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("VisitaExtAction")
@Scope(ScopeType.CONVERSATION)
public class VisitaExtAction extends BaseAction<VisitaExt, Long> {

	private static final long serialVersionUID = 427699430L;

	public static VisitaExtAction instance() {
		return (VisitaExtAction) Component.getInstance(VisitaExtAction.class, ScopeType.CONVERSATION);
	}


}