package com.phi.entities.actions;

import com.phi.entities.baseEntity.Iter758;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("Iter758Action")
@Scope(ScopeType.CONVERSATION)
public class Iter758Action extends BaseAction<Iter758, Long> {

	private static final long serialVersionUID = 703020359L;

	public static Iter758Action instance() {
		return (Iter758Action) Component.getInstance(Iter758Action.class, ScopeType.CONVERSATION);
	}


}