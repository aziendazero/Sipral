package com.phi.entities.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.datamodel.PhiDataModel;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PersistenceException;
import com.phi.cs.exception.PhiException;
import com.phi.entities.act.Procedure;
import com.phi.entities.baseEntity.Appointment;
import com.phi.entities.baseEntity.ProcedureRequest;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("ProcedureRequestAction")
@Scope(ScopeType.CONVERSATION)
public class ProcedureRequestAction extends BaseAction<ProcedureRequest, Long> {

	private static final long serialVersionUID = 2104489706L;

	public static ProcedureRequestAction instance() {
		return (ProcedureRequestAction) Component.getInstance(ProcedureRequestAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * Returns icd9 displayname of procedures attached to app
	 * @param app
	 * @return
	 * @throws PhiException
	 */
	@ShowInDesigner(description="Returns icd9 displayname of procedures attached to app")
	public String getAllProcedure(Appointment app) throws PhiException{

		if(app == null)
			return ""; 

		String str="";
		List<String> procList = null ;

		String hql ="SELECT icd9.displayName" +
				" FROM ProcedureRequest procRec" +
				" LEFT JOIN procRec.procedure proc " +
				" LEFT JOIN procRec.appointment app" +
				" LEFT JOIN proc.codeIcd9 icd9" +
				" LEFT JOIN proc.statusCode statusCode" +
				" WHERE app.internalId =" +app.getInternalId()+
				" AND statusCode.code IN ('new','completed')";
		try {

			procList = ca.executeHQL(hql);

		} catch (PersistenceException e) {
			throw new PhiException("Error finding icd9 displayname of procedures attached to " + app,e, ErrorConstants.PERSISTENCE_QUERY_TREE_CREATION_ERR_CODE);
		}
		if (procList == null || procList.size()==0) {
			return "";
		} else {
			for(String row:procList){
				str = str + "; " + row ;
			}
			return str.replaceFirst("; ","");
		}

	}

	/**
	 * Link the list of Procedure to the ProcedureRequest
	 * @param list
	 * @throws PhiException
	 */
	@ShowInDesigner(description="Link the list of Procedure to the ProcedureRequest")
	public void linkProcedures(List<Procedure> list) throws PhiException {

		if (list!=null){
			for (Procedure proc : list) {
				this.link("procedure", proc);	
			}
		}
	}
	
	/**
	 * Link the list of Procedure to the ProcedureRequest
	 * @param list
	 * @throws PhiException
	 */
	@ShowInDesigner(description="Link the list of Procedure to the ProcedureRequest")
	public void unLinkProcedures(List<Procedure> list) throws PhiException {

		if (list!=null){
			for (Procedure proc : list) {
				this.unLink("procedure", proc);	
			}
		}
	}
	
	/**
	 * Remove the procedure that are not save
	 * @param procedureListNew
	 */
	public void removeProcedureNoSave(List<Procedure> procedureListNew){
		if(procedureListNew!=null){
			Iterator itr= procedureListNew.iterator();
			while(itr.hasNext()){
				Procedure nextProcedureNext= (Procedure)itr.next();
				if(nextProcedureNext.getInternalId()==0){
					itr.remove();
				}
			}
		}
	}
	
	/**
	 * Inject in a single list all the procedures from all the ProcedureRequests in conversation
	 * @param
	 * @return
	 * @throws PhiException
	 */
	@ShowInDesigner(description="Inject in a single list all the procedures from all the ProcedureRequests in conversation")
	public void getAllProcedures() throws PhiException{
		try {
			Context conversationContext = Contexts.getConversationContext(); 
			List<ProcedureRequest> procReqList = ((PhiDataModel<ProcedureRequest>) conversationContext.get("ProcedureRequestList")).getList();

			List<Procedure> procList = getAllProcedures(procReqList);
			
			ProcedureAction pa = new ProcedureAction();
			pa.injectList(procList, "AllProcedureList");

		} catch (Exception e) {
			throw new PhiException("Error injecting in a single list all the procedures from all the ProcedureRequests in conversation " + e, ErrorConstants.PERSISTENCE_QUERY_TREE_CREATION_ERR_CODE);
		}

	}
	
	public List<Procedure> getAllProcedures(List<ProcedureRequest> procReqList) throws PhiException{
		List<Procedure> procList = new ArrayList<Procedure>();
		
		if((procReqList != null)&&(procReqList.size()>0)) {
			Iterator<ProcedureRequest> itr= procReqList.iterator();
			
			while(itr.hasNext()){
				ProcedureRequest procReq = (ProcedureRequest)itr.next();
				if(procReq != null) {
					List<Procedure> procs = procReq.getProcedure();
					if (procs!=null) {
						for (Procedure proc : procs) {
							if (!"nullified".equals(proc.getStatusCode().getCode())) {
								procList.add(proc);
							}
						}
					}
				}
			}
		}
		
		return procList;
	}
	
	public boolean procedureListContainsCode(List<Procedure> procList, String... codes) {
		for (Procedure proc : procList) {
			CodeValue codeIcd9 = proc.getCodeIcd9();
			if (codeIcd9 != null && !"nullified".equals(proc.getStatusCode().getCode())) {
				for (String code : codes) {
					if (code.equals(codeIcd9.getCode())) {
						return true;
					}
				}
			}
		}
		return false;
	}
}