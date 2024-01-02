package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Provvedimenti;

@BypassInterceptors
@Name("ProvvedimentiSospAction")
@Scope(ScopeType.CONVERSATION)
public class ProvvedimentiSospAction extends BaseAction<Provvedimenti, Long> {

	private static final long serialVersionUID = -7982872682705064330L;
	private static final Logger log = Logger.getLogger(ProvvedimentiSospAction.class);

	public ProvvedimentiSospAction(){
		super();
        conversationName = "ProvvedimentiSosp";
	}
	
	public static ProvvedimentiSospAction instance() {
		return (ProvvedimentiSospAction) Component.getInstance(ProvvedimentiSospAction.class, ScopeType.CONVERSATION);
	}

}




