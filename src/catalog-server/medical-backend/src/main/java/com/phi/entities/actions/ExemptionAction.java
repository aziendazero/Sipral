package com.phi.entities.actions;

import java.util.List;

import com.phi.cs.datamodel.PhiDataModel;
import com.phi.entities.act.Allergy;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.baseEntity.ProcedureRequest;
import com.phi.entities.entity.Exemption;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;

@BypassInterceptors
@Name("ExemptionAction")
@Scope(ScopeType.CONVERSATION)
public class ExemptionAction extends BaseAction<Exemption, Long> {

	private static final long serialVersionUID = 490836263L;

	public static ExemptionAction instance() {
		return (ExemptionAction) Component.getInstance(ExemptionAction.class, ScopeType.CONVERSATION);
	}
	
	public String formatExemptions(){
		String ret = "";
		List<Exemption> ExemptionList = ((PhiDataModel<Exemption>) Contexts.getConversationContext().get("ExemptionList")).getList();
		if (ExemptionList != null && ExemptionList.size() >0 ){
			for (Exemption e : ExemptionList) {
				if(e!=null) {
					if(e.getIsActive()){
						if (e.getCode() != null && e.getCode().getCode() !=null) {
							ret += e.getCode().getCode() + ",";
						}
					}	
				}	
			}
		}
		if (!ret.isEmpty() && ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}


}