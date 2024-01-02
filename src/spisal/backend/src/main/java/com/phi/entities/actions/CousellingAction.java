package com.phi.entities.actions;

import com.phi.entities.baseEntity.Couselling;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("CousellingAction")
@Scope(ScopeType.CONVERSATION)
public class CousellingAction extends BaseAction<Couselling, Long> {

	private static final long serialVersionUID = 427675313L;

	public static CousellingAction instance() {
		return (CousellingAction) Component.getInstance(CousellingAction.class, ScopeType.CONVERSATION);
	}


}