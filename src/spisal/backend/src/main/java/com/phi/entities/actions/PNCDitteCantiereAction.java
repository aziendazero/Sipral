package com.phi.entities.actions;

import com.phi.entities.baseEntity.PNCDitteCantiere;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PNCDitteCantiereAction")
@Scope(ScopeType.CONVERSATION)
public class PNCDitteCantiereAction extends BaseAction<PNCDitteCantiere, Long> {

	private static final long serialVersionUID = 816680978L;

	public static PNCDitteCantiereAction instance() {
		return (PNCDitteCantiereAction) Component.getInstance(PNCDitteCantiereAction.class, ScopeType.CONVERSATION);
	}


}