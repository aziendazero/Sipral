package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.act.Therapy;

@BypassInterceptors
@Name("TherapyAction")
@Scope(ScopeType.CONVERSATION)
public class TherapyAction extends BaseAction<Therapy, Long> {

private static final long serialVersionUID = 1L;

	public static TherapyAction instance() {
	    return (TherapyAction) Component.getInstance(TherapyAction.class, ScopeType.CONVERSATION);
	}

}