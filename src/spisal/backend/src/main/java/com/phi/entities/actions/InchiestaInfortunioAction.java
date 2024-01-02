package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.InchiestaInfortunio;


@BypassInterceptors
@Name("InchiestaInfortunioAction")
@Scope(ScopeType.CONVERSATION)
public class InchiestaInfortunioAction extends BaseAction<InchiestaInfortunio, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4869072411649129851L;

	public static InchiestaInfortunioAction instance() {
		return (InchiestaInfortunioAction) Component.getInstance(InchiestaInfortunioAction.class, ScopeType.CONVERSATION);
	}

}