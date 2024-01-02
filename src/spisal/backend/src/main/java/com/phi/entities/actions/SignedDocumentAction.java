package com.phi.entities.actions;

import com.phi.entities.baseEntity.SignedDocument;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("SignedDocumentAction")
@Scope(ScopeType.CONVERSATION)
public class SignedDocumentAction extends BaseAction<SignedDocument, Long> {

	private static final long serialVersionUID = 1516816279L;

	public static SignedDocumentAction instance() {
		return (SignedDocumentAction) Component.getInstance(SignedDocumentAction.class, ScopeType.CONVERSATION);
	}


}