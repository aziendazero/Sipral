package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.PNCCantiere;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PNCCantiereAction")
@Scope(ScopeType.CONVERSATION)
public class PNCCantiereAction extends BaseAction<PNCCantiere, Long> {

	private static final long serialVersionUID = 815676490L;

	public static PNCCantiereAction instance() {
		return (PNCCantiereAction) Component.getInstance(PNCCantiereAction.class, ScopeType.CONVERSATION);
	}

	public Long getIdPncCantiereFromSipral(String idPnc){
		return Long.valueOf(idPnc);
	}

	public Object[] getIdPncCantiereSipralTree(String idPnc) throws PhiException{
		List<Long> idList = new ArrayList<Long>();

		Cantiere cantiereSipral = null;
		CantiereLastAction cantAction = CantiereLastAction.instance();
		cantAction.cleanRestrictions();
		cantAction.getEqual().put("isActive", true);
		cantAction.getEqual().put("idPnc", idPnc);

		List<Cantiere> cantieriList = (List<Cantiere>) cantAction.select();
		
		if(cantieriList!=null && !cantieriList.isEmpty()){
			cantiereSipral = cantieriList.get(0);
		}
		idList.add(Long.valueOf(cantiereSipral.getIdPnc()));
		
		while(cantiereSipral.getOriginal()!=null){
			cantiereSipral = cantiereSipral.getOriginal();
			idList.add(Long.valueOf(cantiereSipral.getIdPnc()));
		}

		return idList.toArray();
	}

}