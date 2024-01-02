package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Misure;

@BypassInterceptors
@Name("MisureAction")
@Scope(ScopeType.CONVERSATION)
public class MisureAction extends BaseAction<Misure, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8906207979211264174L;

	public static MisureAction instance() {
		return (MisureAction) Component.getInstance(MisureAction.class, ScopeType.CONVERSATION);
	}
	
	public void clearMisure(Misure m){
		if(m.getType()==null)
			return;
		
		String last = (String) getTemporary().get("lastValue");
		if(last==null || !last.equals(m.getType().getId())){
			getTemporary().put("lastValue", m.getType().getId());
			m.setCampionamentoCv(null);
			m.setMisurazioneCv(null);
			m.setValue(null);
			m.setUm(null);
		}
		
	}

}