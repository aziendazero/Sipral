package com.phi.entities.actions;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Query;

import com.phi.entities.baseEntity.CostoIndiretto;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("CostoIndirettoAction")
@Scope(ScopeType.CONVERSATION)
public class CostoIndirettoAction extends BaseAction<CostoIndiretto, Long> {

	private static final long serialVersionUID = 387421522L;
    private static final Logger log = Logger.getLogger(CostoIndirettoAction.class);


	public static CostoIndirettoAction instance() {
		return (CostoIndirettoAction) Component.getInstance(CostoIndirettoAction.class, ScopeType.CONVERSATION);
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

	public String getTipoStr(CostoIndiretto ci) {
		String ret = "";
		try {
			if (ci==null || ci.getLineaDiLavoro()==null)
				return ret;
			
			String ldl = ci.getLineaDiLavoro().getCode();
			if (ldl==null || "".equals(ldl))
				return ret;
			
			ret += ci.getLineaDiLavoro().getCurrentTranslation();
			
			if (("SUPERVISION".equals(ldl) || "WORKMEDICINE".equals(ldl)) && ci.getTipoLineaDiLavoro()!=null)
				ret += " (" + ci.getTipoLineaDiLavoro().getCurrentTranslation() + ")";
			
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public Boolean check(CostoIndiretto ci) {
		try {
			if (ci==null)
				return false;
			
			long internalId = ci.getInternalId();
			
			long ulssId = ci.getServiceDeliveryLocation()!=null?ci.getServiceDeliveryLocation().getInternalId():null;
			
			Integer annoDal = ci.getAnno();
			Integer annoAl = ci.getAnnoAl();
			
			if (annoAl!=null && annoAl!=null && annoDal>annoAl){
				String error = "<p><b>Attenzione!</b> L'attributo 'Anno al' non può essere minore dell'attributo 'Anno dal'";
				this.getTemporary().put("check", error);
				return true;
			}
			
			String qualifica = ci.getQualifica()!=null?ci.getQualifica().getCode():null;
			String tipologia = ci.getTipologia()!=null?ci.getTipologia().getCode():null;
			
			String qryCI = "SELECT ci FROM CostoIndiretto ci " +
			"LEFT JOIN ci.serviceDeliveryLocation ulss " +
			"WHERE ulss.internalId = :ulssId AND ci.qualifica.code = :qualifica AND ci.tipologia.code = :tipologia " +
			"AND ci.lineaDiLavoro.code = :ldl ";

			String ldl = ci.getLineaDiLavoro()!=null?ci.getLineaDiLavoro().getCode():null;
			String tipoLdL = ci.getTipoLineaDiLavoro()!=null?ci.getTipoLineaDiLavoro().getCode():null;
			
			if ("SUPERVISION WORKMEDICINE".contains(ldl))
				qryCI += "AND ci.tipoLineaDiLavoro.code = :tipoLdL ";

			if (annoAl==null)
				qryCI += "AND (ci.annoAl IS NULL OR ci.annoAl>= :annoDal) ";
			else
				qryCI += "AND ( (ci.annoAl IS NULL AND ci.anno <= :annoAl) OR ci.annoAl>= :annoDal ) ";
			
			if (internalId>0)
				qryCI += "AND ci.internalId <> :internalId";
			
			Query qCI = ca.createQuery(qryCI);
			
			if (qryCI.contains(":annoDal"))
				qCI.setParameter("annoDal", annoDal);
			if (qryCI.contains(":annoAl"))
				qCI.setParameter("annoAl", annoAl);
			if (qryCI.contains(":ulssId"))
				qCI.setParameter("ulssId", ulssId);
			if (qryCI.contains(":qualifica"))
				qCI.setParameter("qualifica", qualifica);
			if (qryCI.contains(":tipologia"))
				qCI.setParameter("tipologia", tipologia);
			if (qryCI.contains(":ldl"))
				qCI.setParameter("ldl", ldl);
			if (qryCI.contains(":tipoLdL"))
				qCI.setParameter("tipoLdL", tipoLdL);
			if (qryCI.contains(":internalId"))
				qCI.setParameter("internalId", internalId);
			
			List<CostoIndiretto> ciList = qCI.getResultList();
			
			if (ciList!=null && ciList.size()>0){
				String error = "<p><b>Attenzione! Impossibile procedere con il salvataggio perché in collisione con ";
				if(ciList.size()==1)
					error += "il seguente costo:</b>";
				else
					error += "i seguenti costi:</b>";
					
				
				for (CostoIndiretto c:ciList)
					error += "<br>" + this.getAsString(c);
				
				error += "</p>";
				
				this.getTemporary().put("check", error);
				return true;
			}
			
			this.getTemporary().remove("check");
			
			return false;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
	
	public String getAsString(CostoIndiretto ci) {
		String ret = "";
		try {
			if (ci==null || ci.getTipologia()==null)
				return ret;
			
			if (ci.getServiceDeliveryLocation()!=null && ci.getServiceDeliveryLocation().getName()!=null){
				String name = ci.getServiceDeliveryLocation().getName().getGiv();
				if (name==null || "".equals(name))
					name = "N.D.";
				
				ret += name + " - "; 
			}
						
			Integer annoDal = ci.getAnno();
			Integer annoAl = ci.getAnnoAl();
			
			ret += "Valido dall'anno " + annoDal;
			if (annoAl!=null)
				ret += " all'anno " + annoAl;
			
			ret += " - ";
			
			String qualifica = ci.getQualifica()!=null?ci.getQualifica().getDisplayName():"N.D.";
			ret += "Qualifica: " + qualifica + " - ";
			
			String tipologia = ci.getTipologia()!=null?ci.getTipologia().getDisplayName():"N.D.";
			ret += "Tipologia: " + tipologia + " - " + this.getTipoStr(ci) + " - ";
			
			String ldl = ci.getLineaDiLavoro()!=null?ci.getLineaDiLavoro().getCurrentTranslation():"N.D.";
			String tipoLdL = ci.getTipoLineaDiLavoro()!=null?ci.getTipoLineaDiLavoro().getDisplayName():"N.D.";
			
			ret += "Linea di lavoro: " + ldl;
			 
			if ("SUPERVISION WORKMEDICINE".contains(ldl))
				ret += " (" + tipoLdL +")";
			
			ret += " - ";
	
			ret += "Peso: " + ci.getPeso() + " (id: " + ci.getInternalId() + ")";
						
			return ret;

		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}

}