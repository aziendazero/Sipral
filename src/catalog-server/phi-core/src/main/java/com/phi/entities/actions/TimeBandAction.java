package com.phi.entities.actions;

import com.phi.entities.baseEntity.TimeBand;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("TimeBandAction")
@Scope(ScopeType.CONVERSATION)
public class TimeBandAction extends BaseAction<TimeBand, Long> {

	private static final long serialVersionUID = 32654112L;

	public static TimeBandAction instance() {
		return (TimeBandAction) Component.getInstance(TimeBandAction.class, ScopeType.CONVERSATION);
	}


}