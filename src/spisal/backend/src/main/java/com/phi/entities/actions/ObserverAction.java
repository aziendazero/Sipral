package com.phi.entities.actions;

import com.phi.entities.baseEntity.Observer;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("ObserverAction")
@Scope(ScopeType.CONVERSATION)
public class ObserverAction extends BaseAction<Observer, Long> {

	private static final long serialVersionUID = 197964709L;

	public static ObserverAction instance() {
		return (ObserverAction) Component.getInstance(ObserverAction.class, ScopeType.CONVERSATION);
	}


}