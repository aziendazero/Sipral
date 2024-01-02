package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.RilevazioniAmbientali;

@BypassInterceptors
@Name("RilevazioniAmbientaliAction")
@Scope(ScopeType.CONVERSATION)
public class RilevazioniAmbientaliAction extends BaseAction<RilevazioniAmbientali, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3990573767774983529L;
	
	public static RilevazioniAmbientaliAction instance() {
        return (RilevazioniAmbientaliAction) Component.getInstance(RilevazioniAmbientaliAction.class, ScopeType.CONVERSATION);
    }

}