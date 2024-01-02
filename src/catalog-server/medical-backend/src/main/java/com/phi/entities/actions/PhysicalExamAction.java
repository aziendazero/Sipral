package com.phi.entities.actions;

import com.phi.entities.baseEntity.PhysicalExam;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PhysicalExamAction")
@Scope(ScopeType.CONVERSATION)
public class PhysicalExamAction extends BaseAction<PhysicalExam, Long> {

	private static final long serialVersionUID = 1473074195L;

	public static PhysicalExamAction instance() {
		return (PhysicalExamAction) Component.getInstance(PhysicalExamAction.class, ScopeType.CONVERSATION);
	}


}