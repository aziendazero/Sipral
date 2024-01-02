package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.act.ObjectiveExam;

@BypassInterceptors
@Name("ObjectiveExamAction")
@Scope(ScopeType.CONVERSATION)
public class ObjectiveExamAction extends BaseAction<ObjectiveExam, Long> {

	private static final long serialVersionUID = 2249133645021729583L;

	public static ObjectiveExamAction instance() {
        return (ObjectiveExamAction) Component.getInstance(ObjectiveExamAction.class, ScopeType.CONVERSATION);
    }

}
