package com.phi.entities.actions;

import com.phi.entities.baseEntity.DittePratiche;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DittePraticheAction")
@Scope(ScopeType.CONVERSATION)
public class DittePraticheAction extends BaseAction<DittePratiche, Long> {

	private static final long serialVersionUID = 1469517952L;

	public static DittePraticheAction instance() {
		return (DittePraticheAction) Component.getInstance(DittePraticheAction.class, ScopeType.CONVERSATION);
	}


}