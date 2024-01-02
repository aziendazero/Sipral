package com.phi.entities.actions;

import com.phi.entities.baseEntity.MisMdl;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("MisMdlAction")
@Scope(ScopeType.CONVERSATION)
public class MisMdlAction extends BaseAction<MisMdl, Long> {

	private static final long serialVersionUID = 1374489481L;

	public static MisMdlAction instance() {
		return (MisMdlAction) Component.getInstance(MisMdlAction.class, ScopeType.CONVERSATION);
	}


}