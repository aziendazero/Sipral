package com.phi.entities.actions;

import com.phi.entities.baseEntity.Miglioramenti;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("MiglioramentiAction")
@Scope(ScopeType.CONVERSATION)
public class MiglioramentiAction extends BaseAction<Miglioramenti, Long> {

	private static final long serialVersionUID = 1047577285L;

	public static MiglioramentiAction instance() {
		return (MiglioramentiAction) Component.getInstance(MiglioramentiAction.class, ScopeType.CONVERSATION);
	}


}