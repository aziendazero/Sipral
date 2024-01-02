package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Soggetto;


@BypassInterceptors
@Name("SoggettoInSolidoAction")
@Scope(ScopeType.CONVERSATION)
public class SoggettoInSolidoAction extends BaseAction<Soggetto, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5973679572002858384L;
    
    public static SoggettoInSolidoAction instance() {
        return (SoggettoInSolidoAction) Component.getInstance(SoggettoInSolidoAction.class, ScopeType.CONVERSATION);
    }
    
	public SoggettoInSolidoAction() {
		super();
		conversationName = "SoggettoInSolido";
	}

}