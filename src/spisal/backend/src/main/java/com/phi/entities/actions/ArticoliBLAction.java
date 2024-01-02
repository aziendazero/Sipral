package com.phi.entities.actions;

import com.phi.entities.baseEntity.ArticoliBL;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ArticoliBLAction")
@Scope(ScopeType.CONVERSATION)
public class ArticoliBLAction extends BaseAction<ArticoliBL, Long> {

	private static final long serialVersionUID = 23087017L;

	public static ArticoliBLAction instance() {
		return (ArticoliBLAction) Component.getInstance(ArticoliBLAction.class, ScopeType.CONVERSATION);
	}


}