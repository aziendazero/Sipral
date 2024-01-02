package com.phi.entities.actions;

import com.phi.entities.baseEntity.PNCPersoneCantiere;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PNCPersoneCantiereAction")
@Scope(ScopeType.CONVERSATION)
public class PNCPersoneCantiereAction extends BaseAction<PNCPersoneCantiere, Long> {

	private static final long serialVersionUID = 818331446L;

	public static PNCPersoneCantiereAction instance() {
		return (PNCPersoneCantiereAction) Component.getInstance(PNCPersoneCantiereAction.class, ScopeType.CONVERSATION);
	}


}