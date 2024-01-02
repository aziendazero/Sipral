package com.phi.entities.actions;

import com.phi.entities.baseEntity.AccertSp;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("AccertSpAction")
@Scope(ScopeType.CONVERSATION)
public class AccertSpAction extends BaseAction<AccertSp, Long> {

	private static final long serialVersionUID = 427712788L;

	public static AccertSpAction instance() {
		return (AccertSpAction) Component.getInstance(AccertSpAction.class, ScopeType.CONVERSATION);
	}


}