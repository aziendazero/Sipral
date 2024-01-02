package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Sintomo;

@BypassInterceptors
@Name("SintomoAction")
@Scope(ScopeType.CONVERSATION)
public class SintomoAction extends BaseAction<Sintomo, Long> {

	private static final long serialVersionUID = 815049553L;

	public static SintomoAction instance() {
		return (SintomoAction) Component.getInstance(SintomoAction.class, ScopeType.CONVERSATION);
	}

	public Sintomo copy(Sintomo sOld){
		if(sOld==null)
			return null;
		
		Sintomo sNew = new Sintomo();
		sNew.setCode(sOld.getCode());
		sNew.setData(sOld.getData());
		sNew.setPeggioramento(sOld.getPeggioramento());
		
		return sNew;
	}
}