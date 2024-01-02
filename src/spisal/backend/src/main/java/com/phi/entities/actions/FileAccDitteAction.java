package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.FileAccDitte;

@BypassInterceptors
@Name("FileAccDitteAction")
@Scope(ScopeType.CONVERSATION)
public class FileAccDitteAction extends BaseAction<FileAccDitte, Long> {

	private static final long serialVersionUID = 75629136L;

	public static FileAccDitteAction instance() {
		return (FileAccDitteAction) Component.getInstance(FileAccDitteAction.class, ScopeType.CONVERSATION);
	}


}