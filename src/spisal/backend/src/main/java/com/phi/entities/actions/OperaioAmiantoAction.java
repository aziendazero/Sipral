package com.phi.entities.actions;

import com.phi.entities.baseEntity.OperaioAmianto;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("OperaioAmiantoAction")
@Scope(ScopeType.CONVERSATION)
public class OperaioAmiantoAction extends BaseAction<OperaioAmianto, Long> {

	private static final long serialVersionUID = 387354511L;

	public static OperaioAmiantoAction instance() {
		return (OperaioAmiantoAction) Component.getInstance(OperaioAmiantoAction.class, ScopeType.CONVERSATION);
	}


}