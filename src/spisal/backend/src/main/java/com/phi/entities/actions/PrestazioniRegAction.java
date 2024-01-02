package com.phi.entities.actions;

import com.phi.entities.baseEntity.PrestazioniReg;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PrestazioniRegAction")
@Scope(ScopeType.CONVERSATION)
public class PrestazioniRegAction extends BaseAction<PrestazioniReg, Long> {

	private static final long serialVersionUID = 1793548507L;

	public static PrestazioniRegAction instance() {
		return (PrestazioniRegAction) Component.getInstance(PrestazioniRegAction.class, ScopeType.CONVERSATION);
	}


}