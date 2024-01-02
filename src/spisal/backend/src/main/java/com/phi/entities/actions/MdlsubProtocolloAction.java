package com.phi.entities.actions;

import com.phi.entities.baseEntity.MdlsubProtocollo;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("MdlsubProtocolloAction")
@Scope(ScopeType.CONVERSATION)
public class MdlsubProtocolloAction extends BaseAction<MdlsubProtocollo, Long> {

	private static final long serialVersionUID = 548120607L;

	public static MdlsubProtocolloAction instance() {
		return (MdlsubProtocolloAction) Component.getInstance(MdlsubProtocolloAction.class, ScopeType.CONVERSATION);
	}


}