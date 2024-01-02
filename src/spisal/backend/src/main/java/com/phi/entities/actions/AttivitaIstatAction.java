package com.phi.entities.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PersistenceException;
import com.phi.entities.baseEntity.AttivitaIstat;
import com.phi.entities.baseEntity.PersoneGiuridiche;
import com.phi.entities.baseEntity.Sedi;
import com.phi.entities.dataTypes.CodeValueAteco;


@BypassInterceptors
@Name("AttivitaIstatAction")
@Scope(ScopeType.CONVERSATION)
public class AttivitaIstatAction extends BaseAction<AttivitaIstat, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7843158108875817401L;
	private static final Logger log = Logger.getLogger(AttivitaIstatAction.class);
	
	public static AttivitaIstatAction instance() {
        return (AttivitaIstatAction) Component.getInstance(AttivitaIstatAction.class, ScopeType.CONVERSATION);
    }
	
	public CodeValueAteco getImportantAteco(PersoneGiuridiche ditta, Sedi sede){
		Long dittaId = ditta != null ? ditta.getInternalId() : null;
		Long sedeId = sede != null ? sede.getInternalId() : null;
		
		return this.getImportantAteco(dittaId, sedeId);
	}

	@SuppressWarnings("unchecked")
	public CodeValueAteco getImportantAteco(Long dittaId, Long sedeId){
		try{
			List<CodeValueAteco> atecoCodeList = null;
			
			if (sedeId != null) {
				atecoCodeList = (List<CodeValueAteco>) ca.executeNamedQuery("AttivitaIstat.getAtecoSede", Collections.singletonMap("sedeId", (Object) sedeId));
				if (atecoCodeList!=null && atecoCodeList.size()>0){
					return (CodeValueAteco) atecoCodeList.get(0);
				}
			}
			
			if (dittaId != null) {
				atecoCodeList = (List<CodeValueAteco>) ca.executeNamedQuery("AttivitaIstat.getAtecoDitta", Collections.singletonMap("dittaId", (Object) dittaId));
				if (atecoCodeList!=null && atecoCodeList.size()>0){
					return (CodeValueAteco) atecoCodeList.get(0);
				}
			}
		} catch (PersistenceException ex) {
			log.error(ex);
			throw new RuntimeException(ex);
		} 
		
		return null;
	}
}