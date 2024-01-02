package com.phi.entities.actions;

import com.phi.entities.baseEntity.PersoneCantiere;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PersoneCantiereAction")
@Scope(ScopeType.CONVERSATION)
public class PersoneCantiereAction extends BaseAction<PersoneCantiere, Long> {

	private static final long serialVersionUID = 827427421L;
    private static final Logger log = Logger.getLogger(PersoneCantiereAction.class);

	public static PersoneCantiereAction instance() {
		return (PersoneCantiereAction) Component.getInstance(PersoneCantiereAction.class, ScopeType.CONVERSATION);
	}
	
	public PersoneCantiere copy(PersoneCantiere toCopy){
		try{
			PersoneCantiere personeCantiere = new PersoneCantiere();
			
			personeCantiere.setCantiere(toCopy.getCantiere());
			personeCantiere.setPerson(toCopy.getPerson());
			personeCantiere.setCreationDate(toCopy.getCreationDate());
			personeCantiere.setCreatedBy(toCopy.getCreatedBy());
			personeCantiere.setRuolo(toCopy.getRuolo());
			
			return personeCantiere;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}