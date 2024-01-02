package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.cs.vocabulary.VocabulariesImpl;
import com.phi.entities.act.Procedure;
import com.phi.entities.act.ProcedureDefinition;
import com.phi.entities.baseEntity.Appointment;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("ProcedureAction")
@Scope(ScopeType.CONVERSATION)
public class ProcedureAction extends BaseAction<Procedure, Long> {

	private static final long serialVersionUID = -5092382867539591845L;
	private static final Logger log = Logger.getLogger(ProcedureAction.class);
	
    public static ProcedureAction instance() {
        return (ProcedureAction) Component.getInstance(ProcedureAction.class, ScopeType.CONVERSATION);
    }
    
	/** 
	 * ADDS A PROCEDURE TO THE PERFORMEDPROCEDURE OF THE APPOINTMENT, TRANSFORMING A PROCEDUREDEFINED INTO A PROCEDURE
	 * @param procdef
	 * @throws PhiException 
	 */
	public void  addProcDefToProcList(List<Procedure> procList,ProcedureDefinition procDef, Appointment app) throws PhiException{
		Procedure proc= new Procedure();
		entity=proc;
		proc.setClassCode(procDef.getClassCode());
		proc.setText(procDef.getText());
		proc.setCodeIcd9(procDef.getCodeIcd9());
		proc.setRegionalCodeIcd9(procDef.getRegionalCodeIcd9());
		proc.setQuantity(1);
		if(procDef.getFullPrice()!=null){
			proc.setFullPrice(procDef.getFullPrice().clonePq());
		}
		
		proc.setLevelCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "Length", "30min"));
		
//		if(!"CUP".equals(app.getExternalIdRoot()) && app.getStatusCode()!=null && ("completed".equals(app.getStatusCode().getCode())|| "reported".equals(app.getStatusCode().getCode()))){
//			proc.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "completed"));
//		}
//		else{
//			proc.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "new"));
//		}
		proc.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "new"));
		
		if(procList==null){
			procList = new ArrayList<Procedure>();
			procList.add(proc);
		} else {
			procList.add(proc);
		}
		if (selectedEntities == null) {
			selectedEntities = new HashMap<Procedure, Boolean>();
		}
		selectedEntities.put(proc, true);
		eject();
		
	}
	
	public void setProcsStatusCode(List<Procedure> procList, Appointment app) throws PhiException {
		CodeValue appStatusCode = app.getStatusCode();
		if (procList!=null) {
			for (Procedure proc : procList) {
				if (!"nullified".equals(proc.getStatusCode().getCode())) {
					if(appStatusCode!=null && ("completed".equals(appStatusCode.getCode()) || "reported".equals(appStatusCode.getCode()))){
						proc.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "completed"));
					} else {
						proc.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "new"));
					}
				}
			}
		}
	}
	
	/** 
	 * ADDS A PROCEDURE TO THE APPOINTMENT
	 * @param procdef
	 * @throws PhiException 
	 */
	public void  addProcToAppointment(Procedure proc, Appointment app) throws PhiException{
		Procedure procToAdd= new Procedure();
		
		procToAdd.setClassCode(proc.getClassCode());
		procToAdd.setText(proc.getText());
		procToAdd.setCodeIcd9(proc.getCodeIcd9());
		procToAdd.setRegionalCodeIcd9(proc.getRegionalCodeIcd9());
		procToAdd.setCharge(proc.getCharge());
		procToAdd.setQuantity(1);
		if(procToAdd.getFullPrice()!=null){
			procToAdd.setFullPrice(proc.getFullPrice().clonePq());
		}
		
		procToAdd.setStatusCode(proc.getStatusCode());
		procToAdd.setDescription(proc.getDescription());
		app.addPerformedProcedure(procToAdd);	
	}
	
	/** 
	 * ADDS A PROCEDURE TO THE PERFORMEDPROCEDURE, TRANSFORMING A PROCEDUREDEFINED INTO A PROCEDURE
	 * @param procdef
	 * @throws PhiException 
	 */
	public void  addProcDefToProcList(List<Procedure> procList,ProcedureDefinition procDef) throws PhiException{
		Procedure proc= new Procedure();
		entity=proc;
		proc.setClassCode(procDef.getClassCode());
		proc.setText(procDef.getText());
		proc.setCodeIcd9(procDef.getCodeIcd9());
		proc.setRegionalCodeIcd9(procDef.getRegionalCodeIcd9());
		proc.setQuantity(1);
		if(procDef.getFullPrice()!=null){
			proc.setFullPrice(procDef.getFullPrice().clonePq());
		}
		proc.setStatusCode(VocabulariesImpl.instance().getCodeValueCsDomainCode("PHIDIC", "StatusCodes", "new"));
		
		if(procList==null){
			procList = new ArrayList<Procedure>();
			procList.add(proc);
		}
		else{
			procList.add(proc);
		}
		
		/*if (selectedEntities == null) {
			selectedEntities = new HashMap<Procedure, Boolean>();
		}
		
		selectedEntities.put(proc, true);
		eject();*/
		this.injectList(procList, "ProcedureList");
		
	}
	
//	/** 
//	 * REMOVE THE PROCEDURE FROM THE LIST
//	 * @param proc
//	 * @throws PhiException
//	 */
//	public void removeProcFromProcedureList(Procedure proc) throws PhiException {
//		Context conversationContext = Contexts.getConversationContext(); 
//		List<Procedure> procList = ((PhiDataModel<Procedure>) conversationContext.get("ProcedureList")).getList();
//		if(procList.size()>0 && procList !=null){
//			Iterator<Procedure> itr= procList.iterator();
//			while(itr.hasNext()){
//				Procedure procedure = itr.next();
//				if(proc.getRegionalCodeIcd9()!=null && proc.getRegionalCodeIcd9().equals(procedure.getRegionalCodeIcd9())){
//					itr.remove();
//					selectedEntities.remove(itr);
//				}
//				
//			}
//
//			
//		}
//	}
	
	public Boolean mainProcedureSelected(List<Procedure> procedureList) throws Exception {
		
		if (procedureList.isEmpty()) {
			return false;
		}
			   
		for (Procedure p : procedureList){
			Boolean isMain = p.getMainProcedure();
			if (isMain != null && isMain) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes from a Procedure list the procedures which have a given icd9_code.
	 * @param procList -> the procedure List
	 * @param proc -> Procedure to remove
	 */
	public void removeProcedureByCodeIcd9(List<Procedure> procList, Procedure proc){
		if(procList.isEmpty() || proc== null){
			return;
		}
		CodeValue tmpCv = proc.getCodeIcd9();
		String codeProc = tmpCv != null ? tmpCv.getCode() : null;
		if(codeProc!=null){
			Iterator itr= procList.iterator();
			Procedure nextProc= null;
			CodeValue nextProcCv = null;
			String nextProcCodeIcd9 = null;
			while(itr.hasNext()){
				nextProc= (Procedure)itr.next();
				nextProcCv = nextProc.getCodeIcd9();
				nextProcCodeIcd9 = nextProcCv != null ? nextProcCv.getCode() : null;
				if(nextProcCodeIcd9 != null && codeProc.equals(nextProcCodeIcd9)){
					itr.remove();
					}
				}
			}
		}

	/**
	 * Copy the updated price from procedure definition to procedure.
	 * @param procedure -> the procedure
	 * @param sdlId -> up id
	 * @throws PhiException 
	 */
	public void copyPriceFromProcDef(Procedure procedure, long sdlId) throws PhiException {
		ProcedureDefinitionAction pda = new ProcedureDefinitionAction();
		pda.getEqual().put("isActive", true);
		pda.getEqual().put("codeIcd9.code", procedure.getCodeIcd9().getCode());
//		pda.getEqual().put("serviceDeliveryLocation.internalId", sdlId);
		pda.read();
		List<ProcedureDefinition> pdlist = pda.list();
		if (pdlist != null && pdlist.size() > 0) {
			if (pdlist.get(0) != null) {
				procedure.setFullPrice(pdlist.get(0).getFullPrice());	
			}				
		}
	}
}