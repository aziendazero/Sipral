package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.PersoneGiuridiche;

@BypassInterceptors
@Name("PersonaGiuridicaArpavAction")
@Scope(ScopeType.CONVERSATION)
public class PersonaGiuridicaArpavAction extends BaseAction<PersoneGiuridiche, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7802250624215875487L;
	private static final Logger log = Logger.getLogger(PersonaGiuridicaArpavAction.class);

	public PersonaGiuridicaArpavAction() {
		super();
		conversationName = "PersonaGiuridicaArpav";
	} 


	public static PersonaGiuridicaArpavAction instance() {
		return (PersonaGiuridicaArpavAction) Component.getInstance(PersonaGiuridicaArpavAction.class, ScopeType.CONVERSATION);
	}


}