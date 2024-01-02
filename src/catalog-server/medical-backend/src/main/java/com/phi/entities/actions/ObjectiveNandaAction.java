package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.act.ObjectiveNanda;

@BypassInterceptors
@Name("ObjectiveNandaAction")
@Scope(ScopeType.CONVERSATION)
public class ObjectiveNandaAction extends BaseAction<ObjectiveNanda, Long> {
	public static ObjectiveNandaAction instance() {
		return (ObjectiveNandaAction) Component.getInstance(ObjectiveNandaAction.class, ScopeType.CONVERSATION);
	}
	private static final long serialVersionUID = 1743759847L;



}