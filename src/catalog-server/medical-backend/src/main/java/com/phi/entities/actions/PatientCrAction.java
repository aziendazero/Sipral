package com.phi.entities.actions;

import com.phi.entities.role.PatientCr;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PatientCrAction")
@Scope(ScopeType.CONVERSATION)
public class PatientCrAction extends BaseAction<PatientCr, Long> {

	private static final long serialVersionUID = 148707976L;

	public static PatientCrAction instance() {
		return (PatientCrAction) Component.getInstance(PatientCrAction.class, ScopeType.CONVERSATION);
	}


}