package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.entity.Organization;


@BypassInterceptors
@Name("OrganizationAction")
@Scope(ScopeType.CONVERSATION)
public class OrganizationAction extends BaseAction<Organization, Long> {

	private static final long serialVersionUID = -5092382867539591845L;

    public static OrganizationAction instance() {
        return (OrganizationAction) Component.getInstance(OrganizationAction.class, ScopeType.CONVERSATION);
    }
}