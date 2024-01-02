package com.phi.entities.actions;

import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.actions.BaseAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ImpiantoPerSiAction")
@Scope(ScopeType.CONVERSATION)
public class ImpiantoPerSiAction extends BaseAction<Impianto, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052444462495706568L;
	//private static final long serialVersionUID = 510813406L;
    private static final Logger log = Logger.getLogger(ImpiantoPerSiAction.class);

    public ImpiantoPerSiAction() {
        super();
        conversationName = "ImpiantoPerSi";
     } 
    
    
	public static ImpiantoPerSiAction instance() {
		return (ImpiantoPerSiAction) Component.getInstance(ImpiantoPerSiAction.class, ScopeType.CONVERSATION);
	}



}