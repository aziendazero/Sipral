package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.dataTypes.CodeValueLaw;

@BypassInterceptors
@Name("CodeValueLawAction")
@Scope(ScopeType.CONVERSATION)
public class CodeValueLawAction extends CodeValueBaseAction<CodeValueLaw> {

	private static final long serialVersionUID = 7398329547116351676L;
	private static final Logger log = Logger.getLogger(CodeValueLawAction.class);
	
	public static CodeValueLawAction instance() {
		return (CodeValueLawAction) Component.getInstance(CodeValueLawAction.class, ScopeType.CONVERSATION);
	}

	// permette di selezionare solo le foglie
	public void injectFromTree(){
		try {
			injectbyId();
			if(entity!=null && !"C".equals(entity.getType()))
				eject();
		} catch (PhiException e) {
			log.error("Failed to inject CodeValueLaw from Tree");
		}
	}
	
	// permette di selezionare anche i rami
	public void injectFromTreeWithBranches(){
		try {
			injectbyId();
			if(entity==null) eject();
		} catch (PhiException e) {
			log.error("Failed to inject CodeValueLaw from Tree");
		}
	}
	
	public void injectbyId() throws PhiException {
		try {
			
			super.injectbyId();
			String code = getEntity().getCode();
			
			if (code==null)
				super.eject();
						
		} catch (Exception e) {
			
			super.eject();
			
		}
	}
	
}
