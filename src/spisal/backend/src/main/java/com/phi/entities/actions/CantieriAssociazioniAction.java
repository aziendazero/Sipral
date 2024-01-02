package com.phi.entities.actions;

import com.phi.entities.baseEntity.CantieriAssociazioni;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("CantieriAssociazioniAction")
@Scope(ScopeType.CONVERSATION)
public class CantieriAssociazioniAction extends BaseAction<CantieriAssociazioni, Long> {

	private static final long serialVersionUID = 1703295709L;

	public static CantieriAssociazioniAction instance() {
		return (CantieriAssociazioniAction) Component.getInstance(CantieriAssociazioniAction.class, ScopeType.CONVERSATION);
	}


}