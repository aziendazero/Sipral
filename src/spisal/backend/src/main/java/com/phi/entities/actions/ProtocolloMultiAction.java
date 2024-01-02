package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Protocollo;

@BypassInterceptors
@Name("ProtocolloMultiAction")
@Scope(ScopeType.CONVERSATION)
public class ProtocolloMultiAction extends BaseAction<Protocollo, Long> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7502689121642689605L;

	public static ProtocolloMultiAction instance() {
        return (ProtocolloMultiAction) Component.getInstance(ProtocolloMultiAction.class, ScopeType.CONVERSATION);
    }

	public ProtocolloMultiAction() {
		super();
		conversationName = "ProtocolloMulti";
	}

}