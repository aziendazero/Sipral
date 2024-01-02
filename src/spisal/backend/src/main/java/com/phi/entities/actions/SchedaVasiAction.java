package com.phi.entities.actions;

import java.util.List;

import com.phi.entities.baseEntity.SchedaVasi;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("SchedaVasiAction")
@Scope(ScopeType.CONVERSATION)
public class SchedaVasiAction extends BaseAction<SchedaVasi, Long> {

	private static final long serialVersionUID = 1914559279L;
	private static final Logger log = Logger.getLogger(SchedaVasiAction.class);

	public static SchedaVasiAction instance() {
		return (SchedaVasiAction) Component.getInstance(SchedaVasiAction.class, ScopeType.CONVERSATION);
	}
	
	public void setNumero(List<SchedaVasi> schedaVasiList){ 
		try{
			SchedaVasi schedaVasi = getEntity();
			Integer number = 0;

			if (schedaVasiList!=null && schedaVasiList.size()>0){
				
				for (SchedaVasi sv:schedaVasiList){
					Integer numero = sv.getNumero();
					if (numero!=null && numero>number)
						number=numero;
				}
			}
			
			schedaVasi.setNumero(number+1);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}