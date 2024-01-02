package com.phi.entities.actions;

import com.phi.entities.act.DischargeData;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DischargeDataAction")
@Scope(ScopeType.CONVERSATION)
public class DischargeDataAction extends BaseAction<DischargeData, Long> {

	private static final long serialVersionUID = 104196173L;

	public static DischargeDataAction instance() {
		return (DischargeDataAction) Component.getInstance(DischargeDataAction.class, ScopeType.CONVERSATION);
	}


}