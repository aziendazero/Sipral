package com.phi.entities.actions;

import com.phi.entities.baseEntity.TipoProdFinito;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("TipoProdFinitoAction")
@Scope(ScopeType.CONVERSATION)
public class TipoProdFinitoAction extends BaseAction<TipoProdFinito, Long> {

	private static final long serialVersionUID = 834113820L;

	public static TipoProdFinitoAction instance() {
		return (TipoProdFinitoAction) Component.getInstance(TipoProdFinitoAction.class, ScopeType.CONVERSATION);
	}


}