package com.phi.entities.actions;

import com.phi.entities.baseEntity.DitteProvvedimenti;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DitteProvvedimentiAction")
@Scope(ScopeType.CONVERSATION)
public class DitteProvvedimentiAction extends BaseAction<DitteProvvedimenti, Long> {

	private static final long serialVersionUID = 2145147897L;

	public static DitteProvvedimentiAction instance() {
		return (DitteProvvedimentiAction) Component.getInstance(DitteProvvedimentiAction.class, ScopeType.CONVERSATION);
	}


}