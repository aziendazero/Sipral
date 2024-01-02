package com.phi.entities.actions;

import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.actions.BaseAction;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ImpiantoCheckAction")
@Scope(ScopeType.CONVERSATION)
public class ImpiantoCheckAction extends BaseAction<Impianto, Long> {

	private static final long serialVersionUID = 6325203396564257673L;

    public ImpiantoCheckAction() {
        super();
        conversationName = "ImpiantoCheck";
     } 
    
    
	public static ImpiantoCheckAction instance() {
		return (ImpiantoCheckAction) Component.getInstance(ImpiantoCheckAction.class, ScopeType.CONVERSATION);
	}



}