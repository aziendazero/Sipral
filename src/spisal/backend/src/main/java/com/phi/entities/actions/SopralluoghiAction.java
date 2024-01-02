package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.Sopralluoghi;

@BypassInterceptors
@Name("SopralluoghiAction")
@Scope(ScopeType.CONVERSATION)
public class SopralluoghiAction extends BaseAction<Sopralluoghi, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4532648280576748929L;

    public static SopralluoghiAction instance() {
        return (SopralluoghiAction) Component.getInstance(SopralluoghiAction.class, ScopeType.CONVERSATION);
    }
}