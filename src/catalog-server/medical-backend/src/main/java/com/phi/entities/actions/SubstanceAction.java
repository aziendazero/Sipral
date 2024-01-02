package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Substance;

@BypassInterceptors
@Name("SubstanceAction")
@Scope(ScopeType.CONVERSATION)

public class SubstanceAction extends BaseAction<Substance, Long> {

	private static final long serialVersionUID = -5092382867539591845L;

    public static SubstanceAction instance() {
        return (SubstanceAction) Component.getInstance(SubstanceAction.class, ScopeType.CONVERSATION);
    }

    
}