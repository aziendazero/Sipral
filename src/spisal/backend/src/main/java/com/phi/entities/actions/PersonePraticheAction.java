package com.phi.entities.actions;

import com.phi.entities.baseEntity.PersonePratiche;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PersonePraticheAction")
@Scope(ScopeType.CONVERSATION)
public class PersonePraticheAction extends BaseAction<PersonePratiche, Long> {

	private static final long serialVersionUID = 478480251L;

	public static PersonePraticheAction instance() {
		return (PersonePraticheAction) Component.getInstance(PersonePraticheAction.class, ScopeType.CONVERSATION);
	}


}