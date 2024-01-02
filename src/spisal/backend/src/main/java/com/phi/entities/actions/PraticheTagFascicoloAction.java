package com.phi.entities.actions;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.TagFascicolo;

@BypassInterceptors
@Name("PraticheTagFascicoloAction")
@Scope(ScopeType.CONVERSATION)
public class PraticheTagFascicoloAction extends BaseAction<TagFascicolo, Long> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4608503589080213731L;

	

	public PraticheTagFascicoloAction() {
		super();
		conversationName = "PraticheTagFascicolo";
	}
	


}