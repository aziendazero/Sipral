package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Sedi;

@BypassInterceptors
@Name("SediToIndSpedAction")
@Scope(ScopeType.CONVERSATION)
public class SediToIndSpedAction extends BaseAction<Sedi, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1084964523545263112L;
	private static final Logger log = Logger.getLogger(SediToIndSpedAction.class);


    public SediToIndSpedAction() {
        super();
        conversationName = "SediToIndSped";
     } 
    
	
	public static SediToIndSpedAction instance() {
		return (SediToIndSpedAction) Component.getInstance(SediToIndSpedAction.class, ScopeType.CONVERSATION);
	}

	public void injectMatchingSede(Sedi se){
		if(se==null)
			return;
		
		this.inject(se);
	}
}