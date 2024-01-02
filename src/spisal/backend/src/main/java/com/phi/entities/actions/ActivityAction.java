package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.act.Activity;

@BypassInterceptors
@Name("ActivityAction")
@Scope(ScopeType.CONVERSATION)
public class ActivityAction extends BaseAction<Activity, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4401026309977941187L;

	public static ActivityAction instance() {
        return (ActivityAction) Component.getInstance(ActivityAction.class, ScopeType.CONVERSATION);
    }

}