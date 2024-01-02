package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.AccessoAtti;

@BypassInterceptors
@Name("AccessoAttiAction")
@Scope(ScopeType.CONVERSATION)
public class AccessoAttiAction extends BaseAction<AccessoAtti, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7447691812055812360L;
	
	public static AccessoAttiAction instance() {
        return (AccessoAttiAction) Component.getInstance(AccessoAttiAction.class, ScopeType.CONVERSATION);
    }
}