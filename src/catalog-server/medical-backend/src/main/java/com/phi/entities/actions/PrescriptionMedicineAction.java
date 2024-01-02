package com.phi.entities.actions;

import com.phi.entities.baseEntity.PrescriptionMedicine;
import com.phi.entities.actions.BaseAction;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@BypassInterceptors
@Name("PrescriptionMedicineAction")
@Scope(ScopeType.CONVERSATION)
public class PrescriptionMedicineAction extends BaseAction<PrescriptionMedicine, Long> {

	private static final long serialVersionUID = 693885705L;

	public static PrescriptionMedicineAction instance() {
		return (PrescriptionMedicineAction) Component.getInstance(PrescriptionMedicineAction.class, ScopeType.CONVERSATION);
	}


}