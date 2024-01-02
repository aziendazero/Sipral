package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.LavorazioniCorrelate;

@BypassInterceptors
@Name("LavorazioniCorrelateAction")
@Scope(ScopeType.CONVERSATION)
public class LavorazioniCorrelateAction extends BaseAction<LavorazioniCorrelate, Long> {

	private static final long serialVersionUID = 1383192885L;

	public static LavorazioniCorrelateAction instance() {
		return (LavorazioniCorrelateAction) Component.getInstance(LavorazioniCorrelateAction.class, ScopeType.CONVERSATION);
	}
	
	public LavorazioniCorrelate copy(LavorazioniCorrelate oldLav){
		if(oldLav==null)
			return null;
		
		LavorazioniCorrelate newLav = new LavorazioniCorrelate();
		newLav.setInail(oldLav.getInail());
		newLav.setLavUnica(oldLav.getLavUnica());
		newLav.setSchedaEsposti(oldLav.getSchedaEsposti());
		
		return newLav;
	}
}