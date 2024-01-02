package com.phi.entities.actions;

import com.phi.entities.baseEntity.PersoneProvvedimenti;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PersoneProvvedimentiAction")
@Scope(ScopeType.CONVERSATION)
public class PersoneProvvedimentiAction extends BaseAction<PersoneProvvedimenti, Long> {

	private static final long serialVersionUID = 478471247L;

	public static PersoneProvvedimentiAction instance() {
		return (PersoneProvvedimentiAction) Component.getInstance(PersoneProvvedimentiAction.class, ScopeType.CONVERSATION);
	}


}