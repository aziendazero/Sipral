package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.exception.ApplicationException;
import com.phi.cs.exception.PersistenceException;
import com.phi.entities.dataTypes.CodeValuePhi;

@BypassInterceptors
@Name("CodeValuePhiAction")
@Scope(ScopeType.CONVERSATION)
public class CodeValuePhiAction extends CodeValueBaseAction<CodeValuePhi>{

	private static final long serialVersionUID = -301215865722L;
	private static final Logger log = Logger.getLogger(CodeValuePhiAction.class);

	public static CodeValuePhiAction instance() {
		return (CodeValuePhiAction) Component.getInstance(CodeValuePhiAction.class, ScopeType.CONVERSATION);
	}

}


