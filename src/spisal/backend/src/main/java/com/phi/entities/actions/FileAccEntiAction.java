package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.FileAccEnti;

@BypassInterceptors
@Name("FileAccEntiAction")
@Scope(ScopeType.CONVERSATION)
public class FileAccEntiAction extends BaseAction<FileAccEnti, Long> {

	private static final long serialVersionUID = 68424728L;

	public static FileAccEntiAction instance() {
		return (FileAccEntiAction) Component.getInstance(FileAccEntiAction.class, ScopeType.CONVERSATION);
	}


}