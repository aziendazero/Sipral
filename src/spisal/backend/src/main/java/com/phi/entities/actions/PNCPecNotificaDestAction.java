package com.phi.entities.actions;

import com.phi.entities.baseEntity.PNCPecNotificaDest;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PNCPecNotificaDestAction")
@Scope(ScopeType.CONVERSATION)
public class PNCPecNotificaDestAction extends BaseAction<PNCPecNotificaDest, Long> {

	private static final long serialVersionUID = 1446795571L;

	public static PNCPecNotificaDestAction instance() {
		return (PNCPecNotificaDestAction) Component.getInstance(PNCPecNotificaDestAction.class, ScopeType.CONVERSATION);
	}


}