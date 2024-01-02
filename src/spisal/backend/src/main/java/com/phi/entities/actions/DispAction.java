package com.phi.entities.actions;

import com.phi.entities.baseEntity.Disp;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DispAction")
@Scope(ScopeType.CONVERSATION)
public class DispAction extends BaseAction<Disp, Long> {

	private static final long serialVersionUID = 141015114L;

	public static DispAction instance() {
		return (DispAction) Component.getInstance(DispAction.class, ScopeType.CONVERSATION);
	}


}