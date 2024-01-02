package com.phi.entities.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.act.AbstractProcedure;
import com.phi.entities.act.Diagnosis;
import com.phi.entities.act.EncounterProcedure;
import com.phi.entities.act.ProcedureDefinition;

@BypassInterceptors
@Name("EncounterProcedureAction")
@Scope(ScopeType.CONVERSATION)
public class EncounterProcedureAction extends BaseAction<EncounterProcedure, Long> {

	private static final long serialVersionUID = -5092382867539591845L;
	private static final Logger log = Logger.getLogger(EncounterProcedureAction.class);
	
    public static EncounterProcedureAction instance() {
        return (EncounterProcedureAction) Component.getInstance(EncounterProcedureAction.class, ScopeType.CONVERSATION);
    }
    
	/** 
	 * ADDS A PROCEDURE TO THE PERFORMEDPROCEDURE OF THE APPOINTMENT, TRANSFORMING A PROCEDUREDEFINED INTO A PROCEDURE
	 * @param procdef
	 * @throws PhiException 
	 */
	public void  addProcDefToProcList(List<EncounterProcedure> procList,ProcedureDefinition procDef) throws PhiException{
//		Procedure proc= new Procedure();
//		entity=proc;
//		proc.setClassCode(procDef.getClassCode());
//		proc.setText(procDef.getText());
//		proc.setRegionalCodeIcd9(procDef.getRegionalCodeIcd9());
//		proc.setQuantity(1);
//		procList.add(proc);
		throw new IllegalArgumentException("converti a encounterProcedure?!?");
//		if (selectedEntities == null) {
//			selectedEntities = new HashMap<Procedure, Boolean>();
//		}
//		selectedEntities.put(proc, true);
//		eject();
		
	}
	
	public Boolean mainProcedureSelected(List<EncounterProcedure> procedureList) throws Exception {
		
		if (procedureList.isEmpty()) {
			return false;
		}
			   
		for (EncounterProcedure p : procedureList){
			Boolean isMain = p.getMainProcedure();
			if (isMain != null && isMain) {
				return true;
			}
		}
		
		return false;
	}

}