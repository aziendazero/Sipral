package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Commissioni;

@BypassInterceptors
@Name("CommissioniAction")
@Scope(ScopeType.CONVERSATION)
public class CommissioniAction extends BaseAction<Commissioni, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3039267167362669525L;

	public static CommissioniAction instance() {
		return (CommissioniAction) Component.getInstance(CommissioniAction.class, ScopeType.CONVERSATION);
	}

}