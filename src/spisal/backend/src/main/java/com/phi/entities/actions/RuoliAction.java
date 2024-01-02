package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Ruoli;

@BypassInterceptors
@Name("RuoliAction")
@Scope(ScopeType.CONVERSATION)
public class RuoliAction extends BaseAction<Ruoli, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6272229643017758676L;

	public static RuoliAction instance() {
        return (RuoliAction) Component.getInstance(RuoliAction.class, ScopeType.CONVERSATION);
    }
	
}