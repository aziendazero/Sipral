package com.phi.entities.actions;

import com.phi.entities.baseEntity.DitteCantiere;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DitteCantiereAction")
@Scope(ScopeType.CONVERSATION)
public class DitteCantiereAction extends BaseAction<DitteCantiere, Long> {

	private static final long serialVersionUID = 826909088L;
    private static final Logger log = Logger.getLogger(DitteCantiereAction.class);

	public static DitteCantiereAction instance() {
		return (DitteCantiereAction) Component.getInstance(DitteCantiereAction.class, ScopeType.CONVERSATION);
	}

	public DitteCantiere copy(DitteCantiere toCopy){
		try{
			DitteCantiere ditteCantiere = new DitteCantiere();
			
			ditteCantiere.setCantiere(toCopy.getCantiere());
			ditteCantiere.setPersoneGiuridiche(toCopy.getPersoneGiuridiche());
			ditteCantiere.setCreationDate(toCopy.getCreationDate());
			ditteCantiere.setCreatedBy(toCopy.getCreatedBy());
			ditteCantiere.setRuolo(toCopy.getRuolo());
			
			return ditteCantiere;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
}