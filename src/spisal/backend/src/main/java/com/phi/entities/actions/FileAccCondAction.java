package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.FileAccCond;

@BypassInterceptors
@Name("FileAccCondAction")
@Scope(ScopeType.CONVERSATION)
public class FileAccCondAction extends BaseAction<FileAccCond, Long> {

	private static final long serialVersionUID = 68450416L;

	public static FileAccCondAction instance() {
		return (FileAccCondAction) Component.getInstance(FileAccCondAction.class, ScopeType.CONVERSATION);
	}


}