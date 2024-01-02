package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.EventoAccidentale;

@BypassInterceptors
@Name("EventoAccidentaleAction")
@Scope(ScopeType.CONVERSATION)
public class EventoAccidentaleAction extends BaseAction<EventoAccidentale, Long> {

	private static final long serialVersionUID = 1493103911L;

	public static EventoAccidentaleAction instance() {
		return (EventoAccidentaleAction) Component.getInstance(EventoAccidentaleAction.class, ScopeType.CONVERSATION);
	}

}