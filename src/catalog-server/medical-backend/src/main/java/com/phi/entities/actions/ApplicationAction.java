package com.phi.entities.actions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.Application;

@BypassInterceptors
@Name("ApplicationAction")
@Scope(ScopeType.CONVERSATION)
public class ApplicationAction extends BaseAction<Application, Long> {

	private static final long serialVersionUID = 1651977666L;

	public static ApplicationAction instance() {
		return (ApplicationAction) Component.getInstance(ApplicationAction.class, ScopeType.CONVERSATION);
	}

}