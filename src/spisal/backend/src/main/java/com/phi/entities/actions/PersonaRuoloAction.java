package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.PersonaRuolo;

@BypassInterceptors
@Name("PersonaRuoloAction")
@Scope(ScopeType.CONVERSATION)
public class PersonaRuoloAction extends BaseAction<PersonaRuolo, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7231165017766578217L;

	public static PersonaRuoloAction instance() {
        return (PersonaRuoloAction) Component.getInstance(PersonaRuoloAction.class, ScopeType.CONVERSATION);
    }
}