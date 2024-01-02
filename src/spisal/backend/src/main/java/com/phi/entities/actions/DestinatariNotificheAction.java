package com.phi.entities.actions;

import java.util.List;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.baseEntity.Comune;
import com.phi.entities.baseEntity.DestinatariNotifiche;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DestinatariNotificheAction")
@Scope(ScopeType.CONVERSATION)
public class DestinatariNotificheAction extends BaseAction<DestinatariNotifiche, Long> {

	private static final long serialVersionUID = 66754463L;

	public static DestinatariNotificheAction instance() {
		return (DestinatariNotificheAction) Component.getInstance(DestinatariNotificheAction.class, ScopeType.CONVERSATION);
	}

}