package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.dataTypes.CodeValueIcd9;

@BypassInterceptors
@Name("CodeValueIcd9Action")
@Scope(ScopeType.CONVERSATION)
public class CodeValueIcd9Action extends CodeValueBaseAction<CodeValueIcd9> {

	private static final long serialVersionUID = 7839239519840044758L;
	//private static final Logger log = Logger.getLogger(CodeValueIcd9Action.class);
	
	public static CodeValueIcd9Action instance() {
		return (CodeValueIcd9Action) Component.getInstance(CodeValueIcd9Action.class, ScopeType.CONVERSATION);
	}
}
