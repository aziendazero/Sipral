package com.phi.entities.actions;
import com.phi.entities.baseEntity.Committente;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("CommittenteAction")
@Scope(ScopeType.CONVERSATION)
public class CommittenteAction extends BaseAction<Committente, Long> {

	private static final long serialVersionUID = 828683022L;
    private static final Logger log = Logger.getLogger(CommittenteAction.class);

	public static CommittenteAction instance() {
		return (CommittenteAction) Component.getInstance(CommittenteAction.class, ScopeType.CONVERSATION);
	}

	public Committente copy(Committente toCopy){
		try{
			Committente committente = new Committente();
			
			committente.setCantiere(toCopy.getCantiere());
			committente.setPersoneGiuridiche(toCopy.getPersoneGiuridiche());
			committente.setVigilanza(toCopy.getVigilanza());
			committente.setPerson(toCopy.getPerson());
			committente.setCreationDate(toCopy.getCreationDate());
			committente.setCreatedBy(toCopy.getCreatedBy());
			
			return committente;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}