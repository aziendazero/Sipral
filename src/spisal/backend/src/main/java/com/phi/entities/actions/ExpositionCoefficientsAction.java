package com.phi.entities.actions;

import com.phi.entities.baseEntity.ExpositionCoefficients;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ExpositionCoefficientsAction")
@Scope(ScopeType.CONVERSATION)
public class ExpositionCoefficientsAction extends BaseAction<ExpositionCoefficients, Long> {

	private static final long serialVersionUID = 771529171L;

	public static ExpositionCoefficientsAction instance() {
		return (ExpositionCoefficientsAction) Component.getInstance(ExpositionCoefficientsAction.class, ScopeType.CONVERSATION);
	}


}