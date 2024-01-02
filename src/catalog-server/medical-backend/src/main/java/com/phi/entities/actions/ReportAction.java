package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Report;


@BypassInterceptors
@Name("ReportAction")
@Scope(ScopeType.CONVERSATION)
public class ReportAction extends BaseAction<Report, Long> {

	private static final long serialVersionUID = 254457541L;
	
	public static ReportAction instance() {
		return (ReportAction) Component.getInstance(ReportAction.class, ScopeType.CONVERSATION);
	}

}