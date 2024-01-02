package com.phi.entities.actions;

import com.phi.entities.baseEntity.Committente;
import com.phi.entities.actions.BaseAction;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("CommittenteCantiereAction")
@Scope(ScopeType.CONVERSATION)
public class CommittenteCantiereAction extends BaseAction<Committente, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7359164244067496810L;
	
	public static CommittenteCantiereAction instance() {
		return (CommittenteCantiereAction) Component.getInstance(CommittenteCantiereAction.class, ScopeType.CONVERSATION);
	}
	
	public CommittenteCantiereAction() {
		super();
		conversationName = "CommittenteCantiere";
	}

}