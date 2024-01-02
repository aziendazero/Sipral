package com.phi.entities.actions;

import com.phi.entities.baseEntity.MalattiaAgente;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("MalattiaAgenteAction")
@Scope(ScopeType.CONVERSATION)
public class MalattiaAgenteAction extends BaseAction<MalattiaAgente, Long> {

	private static final long serialVersionUID = 794636762L;

	public static MalattiaAgenteAction instance() {
		return (MalattiaAgenteAction) Component.getInstance(MalattiaAgenteAction.class, ScopeType.CONVERSATION);
	}


}