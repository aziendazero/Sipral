package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.dataTypes.CodeValueAteco;

@BypassInterceptors
@Name("CodeValueAtecoAction")
@Scope(ScopeType.CONVERSATION)
public class CodeValueAtecoAction extends CodeValueBaseAction<CodeValueAteco> {

	private static final long serialVersionUID = 7839239519840044758L;
	//private static final Logger log = Logger.getLogger(CodeValueAtecoAction.class);
	
	public static CodeValueAtecoAction instance() {
		return (CodeValueAtecoAction) Component.getInstance(CodeValueAtecoAction.class, ScopeType.CONVERSATION);
	}
}
