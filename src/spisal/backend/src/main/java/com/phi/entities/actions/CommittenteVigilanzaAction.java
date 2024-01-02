package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.CommittenteVigilanza;
import com.phi.entities.baseEntity.DettagliBonifiche;
import com.phi.entities.baseEntity.Procpratiche;
import com.phi.entities.baseEntity.Vigilanza;

@BypassInterceptors
@Name("CommittenteVigilanzaAction")
@Scope(ScopeType.CONVERSATION)
public class CommittenteVigilanzaAction extends BaseAction<CommittenteVigilanza, Long> {

	private static final long serialVersionUID = 1681399233L;

	public static CommittenteVigilanzaAction instance() {
		return (CommittenteVigilanzaAction) Component.getInstance(CommittenteVigilanzaAction.class, ScopeType.CONVERSATION);
	}
	private static final Logger log = Logger.getLogger(MalattiaProfessionaleAction.class);
	
	public void copyFrom(DettagliBonifiche dettagli){
		if (dettagli==null)
			return;
		
		if(getEntity() == null){
			try {
				inject(newEntity());
				
				Vigilanza currentVigilanza = VigilanzaAction.instance().getEntity();
				
				entity.setCommittente(dettagli.getCommittenteBonifica());
				
				if(dettagli.getCommittenteBonificaDitta()!=null){
					entity.setCommittenteDitta(dettagli.getCommittenteBonificaDitta());
					entity.setCommittenteSede(dettagli.getCommittenteBonificaSede());
				}
				
				if(dettagli.getCommittenteBonificaUtente()!=null){
					entity.setCommittenteUtente(dettagli.getCommittenteBonificaUtente());			
				}
				
				entity.setVigilanza(currentVigilanza);
				create();
				
			} catch (Exception e) {
				log.error("Error copying DettagliBonifiche");
			} 
		}
	}

	public void fixCodeValue(){

		Procpratiche procPratiche = (Procpratiche) Component.getInstance("Procpratiche");
		if(procPratiche==null || procPratiche.getVigilanza()==null || procPratiche.getVigilanza().getType()==null)
			return;
		
		String tipoVigilanza = 	procPratiche.getVigilanza().getType().getCode();

		if("Asbestos".equals(tipoVigilanza) && entity!=null){
			try {
				String strCode = entity.getCommittente().getCode();
				
				if(entity.getCommittenteUtente()!=null && !"Utente".equals(strCode)){
					setCodeValue("committente", "PHIDIC", "TargetSource", "Utente");
				}
				
				if(entity.getCommittenteDitta()!=null && !"Ditta".equals(strCode)){
					setCodeValue("committente", "PHIDIC", "TargetSource", "Ditta");
				}			
			} catch (Exception e) {
				log.error("Error setting committentevigilanza code");
			}
		}

	}
	

	public void fakeMethod(){
		return;
	}
}