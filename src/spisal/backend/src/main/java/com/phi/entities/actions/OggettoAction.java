package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Oggetto;
import com.phi.entities.dataTypes.CodeValueLaw;
 
@BypassInterceptors
@Name("OggettoAction")
@Scope(ScopeType.CONVERSATION)
public class OggettoAction extends BaseAction<Oggetto, Long> {

	private static final long serialVersionUID = -4742745158717101926L;
    private static final Logger log = Logger.getLogger(OggettoAction.class);

	public static OggettoAction instance() {
		return (OggettoAction) Component.getInstance(OggettoAction.class, ScopeType.CONVERSATION);
	}
	
	public String getCodeLegge81(CodeValueLaw legge81, boolean flag){
		String ret="";
		String displayName = legge81.getDisplayName();
		
		if (legge81==null || displayName==null || displayName=="")
			return ret;
		
		if (displayName.contains("TITOLO"))
			if (flag)
				ret = displayName;
			else
				ret = (displayName.split(" - "))[0];
		
		else if (displayName.contains("CAPO") || displayName.contains("SEZIONE")){
			String parent = getCodeLegge81((CodeValueLaw)legge81.getParent(), false);
			if (flag)
				ret = parent + " -> " +  displayName;
			else 
				ret = parent + " -> " +  (displayName.split(" - "))[0];
		}
		
		return ret;
	}
	
	public Oggetto copy(Oggetto toCopy){
		try{
			Oggetto oggetto = new Oggetto();
			
			oggetto.setNote(toCopy.getNote());
			oggetto.setCodeLegge81(toCopy.getCodeLegge81());
			
			return oggetto;
			
		} catch (Exception ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
	}
}