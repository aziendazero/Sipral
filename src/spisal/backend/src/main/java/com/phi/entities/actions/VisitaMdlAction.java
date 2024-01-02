package com.phi.entities.actions;

import java.util.List;

import com.phi.entities.baseEntity.VisitaMdl;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("VisitaMdlAction")
@Scope(ScopeType.CONVERSATION)
public class VisitaMdlAction extends BaseAction<VisitaMdl, Long> {

	private static final long serialVersionUID = 427617295L;

	public static VisitaMdlAction instance() {
		return (VisitaMdlAction) Component.getInstance(VisitaMdlAction.class, ScopeType.CONVERSATION);
	}
}