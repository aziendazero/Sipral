package com.phi.entities.actions;

import com.phi.entities.baseEntity.Ex301Bis;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("Ex301BisAction")
@Scope(ScopeType.CONVERSATION)
public class Ex301BisAction extends BaseAction<Ex301Bis, Long> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 820929905699267730L;

	public static Iter758Action instance() {
		return (Iter758Action) Component.getInstance(Iter758Action.class, ScopeType.CONVERSATION);
	}


}