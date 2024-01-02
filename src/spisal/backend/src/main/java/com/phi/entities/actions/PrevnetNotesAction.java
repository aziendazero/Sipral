package com.phi.entities.actions;

import com.phi.entities.baseEntity.PrevnetNotes;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PrevnetNotesAction")
@Scope(ScopeType.CONVERSATION)
public class PrevnetNotesAction extends BaseAction<PrevnetNotes, Long> {

	private static final long serialVersionUID = 284585827L;

	public static PrevnetNotesAction instance() {
		return (PrevnetNotesAction) Component.getInstance(PrevnetNotesAction.class, ScopeType.CONVERSATION);
	}


}