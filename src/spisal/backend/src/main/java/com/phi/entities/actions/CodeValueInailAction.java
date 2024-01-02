package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.exception.PhiException;
import com.phi.entities.dataTypes.CodeValueInail;

@BypassInterceptors
@Name("CodeValueInailAction")
@Scope(ScopeType.CONVERSATION)
public class CodeValueInailAction extends CodeValueBaseAction<CodeValueInail> {

	private static final long serialVersionUID = 7839239519840044758L;
	private static final Logger log = Logger.getLogger(CodeValueInailAction.class);
	
	public static CodeValueInailAction instance() {
		return (CodeValueInailAction) Component.getInstance(CodeValueInailAction.class, ScopeType.CONVERSATION);
	}
	
	public void injectFromTree(){
		try {
			injectbyId();
			if(entity!=null && !"C".equals(entity.getType()))
				eject();
		} catch (PhiException e) {
			log.error("Failed to inject CodeValueInail from Tree");
		}
	}
}
