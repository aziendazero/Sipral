package com.phi.entities.actions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.phi.entities.baseEntity.Progetto;
import com.phi.entities.dataTypes.CodeValuePhi;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ProgettoAction")
@Scope(ScopeType.CONVERSATION)
public class ProgettoAction extends BaseAction<Progetto, Long> {

	private static final long serialVersionUID = 569967077L;
    private static final Logger log = Logger.getLogger(ProgettoAction.class);


	public static ProgettoAction instance() {
		return (ProgettoAction) Component.getInstance(ProgettoAction.class, ScopeType.CONVERSATION);
	}
	
	public void setUlss() {
		try {
			getEqual().remove("serviceDeliveryLocation.internalId");
			HashMap<String, Object> temp = getTemporary();
			
			if (!temp.isEmpty()){
				Object ulss_id = temp.get("selectedULSS");
				
				if (ulss_id != null) {
					Long id = Long.parseLong(ulss_id.toString());
					((FilterMap)getEqual()).put("serviceDeliveryLocation.internalId", id);
				}
			}
			

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
/*	public String getLineeDiLavoroStr(Progetto p) {
		String ret = "";
		try {
			if (p==null || p.getLineeDiLavoro()==null || p.getLineeDiLavoro().size()<1)
				return ret;
			
			List<CodeValuePhi> linee = p.getLineeDiLavoro();
			for (CodeValuePhi linea:linee){
				ret += "<b>" + linea.getCurrentTranslation() + "</b>";
				if ("SUPERVISION".equals(linea.getCode()) && p.getSubVigilanza()!=null && p.getSubVigilanza().size()>0){
					ret += " (";
					for (CodeValuePhi subVig:p.getSubVigilanza())
						ret += subVig.getCurrentTranslation() + " - ";
					
					ret = ret.substring(0, ret.length()-3) + ")";
					 
				} else if ("WORKMEDICINE".equals(linea.getCode()) && p.getSubMdl()!=null && p.getSubMdl().size()>0){
					ret += " (";
					for (CodeValuePhi subMdl:p.getSubMdl())
						ret += subMdl.getCurrentTranslation() + " - ";
					
					ret = ret.substring(0, ret.length()-3) + ")";
				}
				
				 ret += "<br>";		
			}
			
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}*/
	
	public boolean isSelectable(Progetto p, Integer annoPianificazione) {
		try {
			
			if (p==null)
				return false;
			
			//Selezionabile se non ha data di fine validità
			if (p.getDataAl()==null)
				return true;
			
			else {
				//Altrimenti bisogna controllare che l'anno della pianificazione sia quello attuale
				//e che la data fine validità del progetto non sia superata
				
				//Non selezionabile se l'anno della pianificazione non è l'anno attuale
				if (Calendar.getInstance().get(Calendar.YEAR)!=annoPianificazione)
					return false;
				
				//Non selezionabile se la data fine validità del progetto è superata
				if(p.getDataAl().before(new Date()))
					return false;
			}

			return true;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}