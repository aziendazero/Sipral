package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Sequestri;

@BypassInterceptors
@Name("SequestriAction")
@Scope(ScopeType.CONVERSATION)
public class SequestriAction extends BaseAction<Sequestri, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2423158353623182848L;

	public static SequestriAction instance() {
		return (SequestriAction) Component.getInstance(SequestriAction.class, ScopeType.CONVERSATION);
	}
	
}