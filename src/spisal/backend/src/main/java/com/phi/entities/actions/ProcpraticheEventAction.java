package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.ProcpraticheEvent;

@BypassInterceptors
@Name("ProcpraticheEventAction")
@Scope(ScopeType.CONVERSATION)
public class ProcpraticheEventAction extends BaseAction<ProcpraticheEvent, Long> {

	private static final long serialVersionUID = 669326385L;
	
	public static ProcpraticheEventAction instance() {
		return (ProcpraticheEventAction) Component.getInstance(ProcpraticheEventAction.class, ScopeType.CONVERSATION);
	}


}