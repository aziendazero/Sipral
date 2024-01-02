package com.phi.entities.actions;

import com.phi.entities.baseEntity.TagDitta;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("TagDittaAction")
@Scope(ScopeType.CONVERSATION)
public class TagDittaAction extends BaseAction<TagDitta, Long> {

	private static final long serialVersionUID = 838164196L;

	public static TagDittaAction instance() {
		return (TagDittaAction) Component.getInstance(TagDittaAction.class, ScopeType.CONVERSATION);
	}


}