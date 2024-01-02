package com.phi.entities.actions;

import com.phi.entities.baseEntity.PNCPersoneSPID;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PNCPersoneSPIDAction")
@Scope(ScopeType.CONVERSATION)
public class PNCPersoneSPIDAction extends BaseAction<PNCPersoneSPID, Long> {

	private static final long serialVersionUID = 277604505L;

	public static PNCPersoneSPIDAction instance() {
		return (PNCPersoneSPIDAction) Component.getInstance(PNCPersoneSPIDAction.class, ScopeType.CONVERSATION);
	}


}