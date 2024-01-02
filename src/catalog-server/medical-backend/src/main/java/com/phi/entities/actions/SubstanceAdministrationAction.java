package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.act.SubstanceAdministration;

@BypassInterceptors
@Name("SubstanceAdministrationAction")
@Scope(ScopeType.CONVERSATION)
public class SubstanceAdministrationAction extends BaseAction<SubstanceAdministration, Long> {

	private static final long serialVersionUID = -993234286864L;

	public static SubstanceAdministrationAction instance() {
		return (SubstanceAdministrationAction) Component.getInstance(SubstanceAdministrationAction.class, ScopeType.CONVERSATION);
	}
	


}
	
	
	