package com.phi.entities.actions;

import com.phi.entities.baseEntity.Disposizioni;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DisposizioniAction")
@Scope(ScopeType.CONVERSATION)
public class DisposizioniAction extends BaseAction<Disposizioni, Long> {

	private static final long serialVersionUID = 612653842L;

	public static DisposizioniAction instance() {
		return (DisposizioniAction) Component.getInstance(DisposizioniAction.class, ScopeType.CONVERSATION);
	}


}