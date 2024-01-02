package com.phi.entities.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.SchedaGeneratoriIndiv;

@BypassInterceptors
@Name("SchedaGeneratoriIndivAction")
@Scope(ScopeType.CONVERSATION)
public class SchedaGeneratoriIndivAction extends BaseAction<SchedaGeneratoriIndiv, Long> {

	private static final long serialVersionUID = 908570687L;
	private static final Logger log = Logger.getLogger(SchedaGeneratoriIndivAction.class);

	public static SchedaGeneratoriIndivAction instance() {
		return (SchedaGeneratoriIndivAction) Component.getInstance(SchedaGeneratoriIndivAction.class, ScopeType.CONVERSATION);
	}
	
	public void setNumero(List<SchedaGeneratoriIndiv> schedaGenList){ 
		try{
			SchedaGeneratoriIndiv schedaGen = getEntity();
			Integer number = 0;

			if (schedaGenList!=null && schedaGenList.size()>0){
				
				for (SchedaGeneratoriIndiv sg:schedaGenList){
					Integer numero = sg.getNumero();
					if (numero!=null && numero>number)
						number=numero;
				}
			}
			
			schedaGen.setNumero(number+1);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}