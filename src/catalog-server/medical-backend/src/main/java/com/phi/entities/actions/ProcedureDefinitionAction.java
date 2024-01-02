package com.phi.entities.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.exception.PhiException;
import com.phi.entities.act.Procedure;
import com.phi.entities.act.ProcedureDefinition;
import com.phi.entities.dataTypes.CodeValue;

@BypassInterceptors
@Name("ProcedureDefinitionAction")
@Scope(ScopeType.CONVERSATION)
public class ProcedureDefinitionAction extends BaseAction<ProcedureDefinition, Long> {

	private static final long serialVersionUID = -370900325358L;

	public static ProcedureDefinitionAction instance() {
		return (ProcedureDefinitionAction) Component.getInstance(ProcedureDefinitionAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * 
	 * @param procList
	 * @return List of CodeValue representing the the description of the procedure
	 * @throws PhiException
	 */
	@ShowInDesigner(description="Return a list of CodeValue representing the the description of the procedure")
	public List<CodeValue> getCodesFromProcedureList (List<ProcedureDefinition> procList) throws PhiException {
		List<CodeValue> ret = new ArrayList<CodeValue>();

		try{
			if (procList != null && procList.size() > 0 ) { 
				for (ProcedureDefinition proc : procList) {
					if (proc.getCode() != null)
						ret.add(proc.getCode());
				}
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			throw new PhiException(ErrorConstants.GENERIC_ERR_INTERNAL_MSG, e, ErrorConstants.GENERIC_ERR_INTERNAL_MSG);

		}
		return ret;
	}
	
	/** 
	 * ADDS A PROCEDURE DEFINITION TO PROCEDURE
	 * @param procdef
	 * @throws PhiException
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@ShowInDesigner(description="ADDS A PROCEDURE DEFINITION TO PROCEDURE")
	public void addProcToProcDefList(List<ProcedureDefinition> procDefList, Procedure proc) throws PhiException{
		if(procDefList!=null && proc!=null){
			for(ProcedureDefinition procDefi:procDefList ){
				if(procDefi.getCodeIcd9().getCode()== proc.getCodeIcd9().getCode()){
					return;
				}
			}
			ProcedureDefinition procDef = new ProcedureDefinition();
			procDef.setRegionalCodeIcd9(proc.getRegionalCodeIcd9());
			procDef.setClassCode(proc.getClassCode());
			procDef.setCodeIcd9(proc.getCodeIcd9());
			procDef.setText(proc.getText());
			procDefList.add(procDef);
		}
	}

		
	public void putCodeDisplayFilter (String code, String displayName) {
		//put multiple or.. 
		if (entityCriteria == null) 
			entityCriteria = ca.createCriteria(entityClass);
		
		if (code != null && !code.isEmpty()) {
			
			removeSubCriteria(entityCriteria, "Code");
			removeSubCriteria(entityCriteria, "CodeIcd9");
			removeSubCriteria(entityCriteria, "RegionalCodeIcd9");
			
			entityCriteria.createAlias("code", "Code",Criteria.LEFT_JOIN);
			entityCriteria.createAlias("codeIcd9", "CodeIcd9",Criteria.LEFT_JOIN); 
			entityCriteria.createAlias("regionalCodeIcd9", "RegionalCodeIcd9",Criteria.LEFT_JOIN);
		
			SimpleExpression c1 = Restrictions.like("Code.code", "%"+code+"%").ignoreCase();
			SimpleExpression c2 = Restrictions.like("CodeIcd9.code", "%"+code+"%").ignoreCase();
			SimpleExpression c3 = Restrictions.like("RegionalCodeIcd9.code", "%"+code+"%").ignoreCase();
			entityCriteria.add(Restrictions.or(Restrictions.or(c1, c2), c3));
			
		}
				
	}
	
	/** 
	 * REMOVE DUPLICATE FROM PROCEDURE DEFINITION
	 * @param procdef
	 * @throws PhiException
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@ShowInDesigner(description = "REMOVE DUPLICATE FROM PROCEDURE DEFINITION")
	public List<ProcedureDefinition> removeDuplicates(List<ProcedureDefinition> procDefList) throws PhiException {
		List<ProcedureDefinition> ret = new ArrayList<ProcedureDefinition>();
		
		try {
			if (procDefList == null || procDefList.size() <= 0)
				return procDefList;

			for (ProcedureDefinition procDefi : procDefList) {
				if (!ret.contains(procDefi)) {
					ret.add(procDefi);
				}
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			throw new PhiException(ErrorConstants.GENERIC_ERR_INTERNAL_MSG, e, ErrorConstants.GENERIC_ERR_INTERNAL_MSG);

		}
		return ret;
	}
	
}