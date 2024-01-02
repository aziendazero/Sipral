package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Attivita;

@BypassInterceptors
@Name("AttivitaChildrenAction")
@Scope(ScopeType.CONVERSATION)
public class AttivitaChildrenAction extends BaseAction<Attivita, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2439496747551973521L;

	public static AttivitaChildrenAction instance() {
        return (AttivitaChildrenAction) Component.getInstance(AttivitaChildrenAction.class, ScopeType.CONVERSATION);
    }

	public AttivitaChildrenAction() {
		super();
		conversationName = "AttivitaChildren";
	}
	
}