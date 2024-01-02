package com.phi.entities.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.SchedaTubazioniIndiv;

@BypassInterceptors
@Name("SchedaTubazioniIndivAction")
@Scope(ScopeType.CONVERSATION)
public class SchedaTubazioniIndivAction extends BaseAction<SchedaTubazioniIndiv, Long> {

	private static final long serialVersionUID = 908906397L;
	private static final Logger log = Logger.getLogger(SchedaTubazioniIndivAction.class);

	public static SchedaTubazioniIndivAction instance() {
		return (SchedaTubazioniIndivAction) Component.getInstance(SchedaTubazioniIndivAction.class, ScopeType.CONVERSATION);
	}
	
	public void setNumero(List<SchedaTubazioniIndiv> schedaTubiList){ 
		try{
			SchedaTubazioniIndiv schedaTubi = getEntity();
			Integer number = 0;

			if (schedaTubiList!=null && schedaTubiList.size()>0){
				
				for (SchedaTubazioniIndiv st:schedaTubiList){
					Integer numero = st.getNumero();
					if (numero!=null && numero>number)
						number=numero;
				}
			}
			
			schedaTubi.setNumero(number+1);
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}