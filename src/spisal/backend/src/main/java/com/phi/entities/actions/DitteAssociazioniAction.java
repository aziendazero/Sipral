package com.phi.entities.actions;

import com.phi.entities.baseEntity.DitteAssociazioni;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DitteAssociazioniAction")
@Scope(ScopeType.CONVERSATION)
public class DitteAssociazioniAction extends BaseAction<DitteAssociazioni, Long> {

	private static final long serialVersionUID = 1703486715L;

	public static DitteAssociazioniAction instance() {
		return (DitteAssociazioniAction) Component.getInstance(DitteAssociazioniAction.class, ScopeType.CONVERSATION);
	}


}