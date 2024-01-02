package com.phi.entities.actions;

import com.phi.entities.baseEntity.VigilanzaRdpType;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("VigilanzaRdpTypeAction")
@Scope(ScopeType.CONVERSATION)
public class VigilanzaRdpTypeAction extends BaseAction<VigilanzaRdpType, Long> {

	private static final long serialVersionUID = 1017588940L;

	public static VigilanzaRdpTypeAction instance() {
		return (VigilanzaRdpTypeAction) Component.getInstance(VigilanzaRdpTypeAction.class, ScopeType.CONVERSATION);
	}


}