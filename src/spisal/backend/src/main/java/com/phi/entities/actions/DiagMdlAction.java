package com.phi.entities.actions;

import com.phi.entities.baseEntity.DiagMdl;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DiagMdlAction")
@Scope(ScopeType.CONVERSATION)
public class DiagMdlAction extends BaseAction<DiagMdl, Long> {

	private static final long serialVersionUID = 1272121669L;

	public static DiagMdlAction instance() {
		return (DiagMdlAction) Component.getInstance(DiagMdlAction.class, ScopeType.CONVERSATION);
	}
	
	public DiagMdl copy(DiagMdl toCopy) {
		if (toCopy==null)
			return null;
		
		DiagMdl diag = new DiagMdl(); 
		
		diag.setIcd9(toCopy.getIcd9());
		diag.setInail(toCopy.getInail());
		return diag;		
	}
	
}
