package com.phi.entities.actions;

import java.util.List;

import com.phi.cs.exception.PhiException;
import com.phi.entities.baseEntity.Impianto;
import com.phi.entities.baseEntity.IndirizzoSped;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.actions.ImpiantoAction;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("IndirizzoSpedPerSAAction")
@Scope(ScopeType.CONVERSATION)
public class IndirizzoSpedPerSAAction extends BaseAction<IndirizzoSped, Long> {

	private static final long serialVersionUID = 1633274168027580969L;
	private static final Logger log = Logger.getLogger(IndirizzoSpedPerSAAction.class);

    public IndirizzoSpedPerSAAction() {
        super();
        conversationName = "IndirizzoSpedPerSA";
     } 
    
	public static IndirizzoSpedPerSAAction instance() {
		return (IndirizzoSpedPerSAAction) Component.getInstance(IndirizzoSpedPerSAAction.class, ScopeType.CONVERSATION);
	}
	
}