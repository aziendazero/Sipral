package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.SituazioneProfessionale;

@BypassInterceptors
@Name("SituazioneProfessionaleAction")
@Scope(ScopeType.CONVERSATION)
public class SituazioneProfessionaleAction extends BaseAction<SituazioneProfessionale, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2238387401845750376L;
	
	public static SituazioneProfessionaleAction instance() {
		return (SituazioneProfessionaleAction) Component.getInstance(SituazioneProfessionaleAction.class, ScopeType.CONVERSATION);
	}
}