package com.phi.entities.actions;

import com.phi.entities.act.NotePostOperativeCard;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("NotePostOperativeCardAction")
@Scope(ScopeType.CONVERSATION)
public class NotePostOperativeCardAction extends BaseAction<NotePostOperativeCard, Long> {

	private static final long serialVersionUID = 609283979L;

	public static NotePostOperativeCardAction instance() {
		return (NotePostOperativeCardAction) Component.getInstance(NotePostOperativeCardAction.class, ScopeType.CONVERSATION);
	}


}