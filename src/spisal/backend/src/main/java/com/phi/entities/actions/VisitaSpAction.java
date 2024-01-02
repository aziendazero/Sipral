package com.phi.entities.actions;

import java.math.BigDecimal;

import com.phi.entities.baseEntity.Attivita;
import com.phi.entities.baseEntity.VisitaSp;
import com.phi.entities.role.Employee;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("VisitaSpAction")
@Scope(ScopeType.CONVERSATION)
public class VisitaSpAction extends BaseAction<VisitaSp, Long> {

	private static final long serialVersionUID = 427690047L;

	public static VisitaSpAction instance() {
		return (VisitaSpAction) Component.getInstance(VisitaSpAction.class, ScopeType.CONVERSATION);
	}

	public Double getBMI(){
		entity = getEntity();
		double bmi=0;
		if(entity==null){
			Attivita a = AttivitaAction.instance().getEntity();
			if(a!=null && a.getVisitaMdl()!=null && a.getVisitaMdl().get(0)!=null)
				entity=a.getVisitaMdl().get(0).getVisitaSp();
		}
		
		if (entity != null) {
			String altezza = entity.getAltezza();
			String peso = entity.getPeso();
			
			if (altezza!=null && altezza != "" && peso != null && peso != ""){
				double height = Double.parseDouble(entity.getAltezza())/100;
				double weight = Double.parseDouble(entity.getPeso());
				
				if(height!=0) {
					bmi=weight / (height * height);
					bmi=new BigDecimal(bmi).setScale(2 , BigDecimal.ROUND_UP).doubleValue(); 
				}
			}
		}

		return bmi;
	}
	
	public String getRriferimentoInternoName(){
		String ret = "";
		entity = getEntity();
		
		if (entity != null && entity.getRiferimentoInterno()!=null){
			Employee emp = entity.getRiferimentoInterno();
			if (emp.getName()!=null)
				ret = emp.getName().toString(); 
		}
		
		return ret;
	}

}