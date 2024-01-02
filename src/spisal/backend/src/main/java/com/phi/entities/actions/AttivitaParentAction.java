package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Attivita;

@BypassInterceptors
@Name("AttivitaParentAction")
@Scope(ScopeType.CONVERSATION)
public class AttivitaParentAction extends BaseAction<Attivita, Long> {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -8634612756623753665L;

	public static AttivitaParentAction instance() {
        return (AttivitaParentAction) Component.getInstance(AttivitaParentAction.class, ScopeType.CONVERSATION);
    }

	public AttivitaParentAction() {
		super();
		conversationName = "AttivitaParent";
	}
	
}