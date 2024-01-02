package com.phi.entities.actions;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.VerificheTecDocument;

@BypassInterceptors
@Name("VerificheTecDocumentAction")
@Scope(ScopeType.CONVERSATION)
public class VerificheTecDocumentAction extends BaseAction<VerificheTecDocument, Long> {

	private static final long serialVersionUID = 287670917L;

	public static VerificheTecDocumentAction instance() {
		return (VerificheTecDocumentAction) Component.getInstance(VerificheTecDocumentAction.class, ScopeType.CONVERSATION);
	}


}