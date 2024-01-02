package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Cantiere;
import com.phi.entities.baseEntity.DettagliBonifiche;
import com.phi.entities.dataTypes.AD;

@BypassInterceptors
@Name("DettagliBonificheAction")
@Scope(ScopeType.CONVERSATION)
public class DettagliBonificheAction extends BaseAction<DettagliBonifiche, Long> {

	private static final long serialVersionUID = 2027849396L;

	public static DettagliBonificheAction instance() {
		return (DettagliBonificheAction) Component.getInstance(DettagliBonificheAction.class, ScopeType.CONVERSATION);
	}

	public void manageCantiere(Cantiere cant, AD addr){
		DettagliBonifiche dett = getEntity();
		if(dett!=null && cant!=null){
			dett.setNaturaOpera(cant.getNaturaOpera());
			
			CantiereAction cAction = CantiereAction.instance();
			/*
			if(cant.getCommittente()!=null && !cant.getCommittente().isEmpty())
				dett.setCommittente(cAction.printCommittenti(cant.getCommittente()));*/
						
			if(cant.getAddr()!=null)
				dett.setAddr(cant.getAddr().cloneAd());
			
		}else if(dett!=null && addr!=null){
			dett.setNaturaOpera(null);
			//dett.setCommittente(null);
			dett.setAddr(addr.cloneAd());
		
		}
	}
	
	public void clearCommittenti(){
		getEntity();
		entity.setCommittenteBonificaDitta(null);
		entity.setCommittenteBonificaSede(null);
		entity.setCommittenteBonificaUtente(null);
	}
}