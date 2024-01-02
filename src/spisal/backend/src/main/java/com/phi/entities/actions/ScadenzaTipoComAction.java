package com.phi.entities.actions;

import com.phi.entities.baseEntity.ScadenzaTipoCom;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ScadenzaTipoComAction")
@Scope(ScopeType.CONVERSATION)
public class ScadenzaTipoComAction extends BaseAction<ScadenzaTipoCom, Long> {

	private static final long serialVersionUID = 918796899L;

	public static ScadenzaTipoComAction instance() {
		return (ScadenzaTipoComAction) Component.getInstance(ScadenzaTipoComAction.class, ScopeType.CONVERSATION);
	}


}