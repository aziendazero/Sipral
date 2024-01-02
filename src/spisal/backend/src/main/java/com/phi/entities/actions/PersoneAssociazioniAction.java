package com.phi.entities.actions;

import com.phi.entities.baseEntity.PersoneAssociazioni;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PersoneAssociazioniAction")
@Scope(ScopeType.CONVERSATION)
public class PersoneAssociazioniAction extends BaseAction<PersoneAssociazioni, Long> {

	private static final long serialVersionUID = 1704859925L;

	public static PersoneAssociazioniAction instance() {
		return (PersoneAssociazioniAction) Component.getInstance(PersoneAssociazioniAction.class, ScopeType.CONVERSATION);
	}


}