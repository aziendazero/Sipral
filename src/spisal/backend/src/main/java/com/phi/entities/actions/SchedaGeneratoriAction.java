package com.phi.entities.actions;

import java.util.List;

import com.phi.entities.baseEntity.SchedaGeneratori;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("SchedaGeneratoriAction")
@Scope(ScopeType.CONVERSATION)
public class SchedaGeneratoriAction extends BaseAction<SchedaGeneratori, Long> {

	private static final long serialVersionUID = 1914525858L;
	private static final Logger log = Logger.getLogger(SchedaGeneratoriAction.class);

	public static SchedaGeneratoriAction instance() {
		return (SchedaGeneratoriAction) Component.getInstance(SchedaGeneratoriAction.class, ScopeType.CONVERSATION);
	}

	public void setNumero(List<SchedaGeneratori> schedaGeneratoriList){ 
		try{
			SchedaGeneratori schedaGeneratori = getEntity();
			Integer number = 0;

			if (schedaGeneratoriList!=null && schedaGeneratoriList.size()>0){
				
				for (SchedaGeneratori sg:schedaGeneratoriList){
					Integer numero = sg.getNumero();
					if (numero!=null && numero>number)
						number=numero;
				}
			}
			
			schedaGeneratori.setNumero(number+1);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}