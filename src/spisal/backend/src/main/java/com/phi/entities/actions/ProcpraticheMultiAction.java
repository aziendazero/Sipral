package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Procpratiche;

@BypassInterceptors
@Name("ProcpraticheMultiAction")
@Scope(ScopeType.CONVERSATION)
public class ProcpraticheMultiAction extends BaseAction<Procpratiche, Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -5875766414515149719L;

	public static ProcpraticheMultiAction instance() {
        return (ProcpraticheMultiAction) Component.getInstance(ProcpraticheMultiAction.class, ScopeType.CONVERSATION);
    }

	public ProcpraticheMultiAction() {
		super();
		conversationName = "ProcpraticheMulti";
	}

}