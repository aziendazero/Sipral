package com.phi.entities.actions;

import org.apache.log4j.Logger;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.entities.baseEntity.MedicalHistory;

@BypassInterceptors
@Name("MedicalHistoryAction")
@Scope(ScopeType.CONVERSATION)
public class MedicalHistoryAction extends BaseAction<MedicalHistory, Long> {

	private static final long serialVersionUID = 1471796511L;
	private static final Logger log = Logger.getLogger(MedicalHistoryAction.class);

	public static MedicalHistoryAction instance() {
		return (MedicalHistoryAction) Component.getInstance(MedicalHistoryAction.class, ScopeType.CONVERSATION);
	}

}