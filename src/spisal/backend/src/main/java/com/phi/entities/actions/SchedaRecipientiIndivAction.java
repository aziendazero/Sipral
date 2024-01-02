package com.phi.entities.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.SchedaRecipientiIndiv;

@BypassInterceptors
@Name("SchedaRecipientiIndivAction")
@Scope(ScopeType.CONVERSATION)
public class SchedaRecipientiIndivAction extends BaseAction<SchedaRecipientiIndiv, Long> {

	private static final long serialVersionUID = 908800473L;
	private static final Logger log = Logger.getLogger(SchedaRecipientiIndivAction.class);

	public static SchedaRecipientiIndivAction instance() {
		return (SchedaRecipientiIndivAction) Component.getInstance(SchedaRecipientiIndivAction.class, ScopeType.CONVERSATION);
	}
	
	public void setNumero(List<SchedaRecipientiIndiv> schedaRecipientiList){ 
		try{
			SchedaRecipientiIndiv schedaRec = getEntity();
			Integer number = 0;

			if (schedaRecipientiList!=null && schedaRecipientiList.size()>0){
				
				for (SchedaRecipientiIndiv sr:schedaRecipientiList){
					Integer numero = sr.getNumero();
					if (numero!=null && numero>number)
						number=numero;
				}
			}
			
			schedaRec.setNumero(number+1);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}