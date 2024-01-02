package com.phi.entities.actions;

import java.util.ArrayList;

import com.phi.entities.baseEntity.MisMdl;
import com.phi.entities.baseEntity.PrestMdl;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PrestMdlAction")
@Scope(ScopeType.CONVERSATION)
public class PrestMdlAction extends BaseAction<PrestMdl, Long> {

	private static final long serialVersionUID = 1366967813L;

	public static PrestMdlAction instance() {
		return (PrestMdlAction) Component.getInstance(PrestMdlAction.class, ScopeType.CONVERSATION);
	}
	
	public void initList(){
		MisMdlAction misMdlAction = MisMdlAction.instance();
		misMdlAction.injectList(new ArrayList<MisMdl>());
	}
}