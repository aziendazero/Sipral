package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;

import com.phi.events.PhiEvent;
import com.phi.parix.json.detail.Localizzazione;

@BypassInterceptors
@Name("LocalizzazioneAction")
@Scope(ScopeType.CONVERSATION)
public class LocalizzazioneAction extends BaseAction<Localizzazione, Long> {

	private static final long serialVersionUID = 8811538833059299408L;

	public static LocalizzazioneAction instance() {
		return (LocalizzazioneAction) Component.getInstance(LocalizzazioneAction.class, ScopeType.CONVERSATION);
	}
	
	@Override
	public void inject(Object baseEntityOrMap) {
		Context conversation = Contexts.getConversationContext();

		if(baseEntityOrMap instanceof Localizzazione){
			entity = (Localizzazione)baseEntityOrMap;
			conversation.set(conversationName, baseEntityOrMap);

			Events.instance().raiseEvent(PhiEvent.INJECT, entity);
		}
	}
}