package com.phi.entities.actions;

import com.phi.entities.baseEntity.DataBaseLog;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("DataBaseLogAction")
@Scope(ScopeType.CONVERSATION)
public class DataBaseLogAction extends BaseAction<DataBaseLog, Long> {

	private static final long serialVersionUID = 1476862279L;

	public static DataBaseLogAction instance() {
		return (DataBaseLogAction) Component.getInstance(DataBaseLogAction.class, ScopeType.CONVERSATION);
	}


}