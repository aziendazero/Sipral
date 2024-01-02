package com.phi.entities.actions;

import com.phi.entities.baseEntity.AccertExt;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("AccertExtAction")
@Scope(ScopeType.CONVERSATION)
public class AccertExtAction extends BaseAction<AccertExt, Long> {

	private static final long serialVersionUID = 427724658L;

	public static AccertExtAction instance() {
		return (AccertExtAction) Component.getInstance(AccertExtAction.class, ScopeType.CONVERSATION);
	}


}