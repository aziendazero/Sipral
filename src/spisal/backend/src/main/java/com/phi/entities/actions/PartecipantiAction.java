package com.phi.entities.actions;

import java.util.Date;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import com.phi.entities.baseEntity.Partecipanti;

@BypassInterceptors
@Name("PartecipantiAction")
@Scope(ScopeType.CONVERSATION)
public class PartecipantiAction extends BaseAction<Partecipanti, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1624929418199796897L;
	
	public static PartecipantiAction instance() {
		return (PartecipantiAction) Component.getInstance(PartecipantiAction.class, ScopeType.CONVERSATION);
	}
	
    private static final Logger log = Logger.getLogger(PartecipantiAction.class);

	
	public String durata(Partecipanti partecipante){
		try{
			String ret = "";
			if (partecipante!=null) {

				Date dataInizio = partecipante.getDataInizio();
				Date dataFine = partecipante.getDataFine();
				if (dataInizio != null && dataFine != null){
					long diff = dataFine.getTime() - dataInizio.getTime();
				
					long diffInDays = (long) (diff / (1000 * 60 * 60 * 24));
					
					diff = diff - (diffInDays * 1000 * 60 * 60 * 24);
					long diffInHours = diff / (1000 * 60 * 60);
					
					diff = diff - (diffInHours * 1000 * 60 * 60);
					long diffInMinutes = diff / (1000 * 60);
					
					if (diffInDays > 0)
						ret = diffInDays + " giorni";
					
					if (diffInHours > 0) {
						if (ret != "")
							ret += " e ";
						if (diffInHours == 1)
							ret += "1 ora";
						else 
							ret += diffInHours + " ore";
					}
					
					if (diffInMinutes > 0) {
						if (ret != "")
							ret += " e ";
						if (diffInMinutes == 1)
							ret += "1 minuto";
						else 
							ret += diffInMinutes + " minuti";
					}
				}
			}
		        
			return ret;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}


}