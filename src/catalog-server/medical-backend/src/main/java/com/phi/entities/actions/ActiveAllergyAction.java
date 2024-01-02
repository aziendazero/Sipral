package com.phi.entities.actions;

import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.datamodel.PhiDataModel;
import com.phi.entities.act.Allergy;

@BypassInterceptors
@Name("ActiveAllergyAction")
@Scope(ScopeType.CONVERSATION)
public class ActiveAllergyAction extends BaseAction<Allergy, Long> {

	private static final long serialVersionUID = -1340613609399046877L;

	public ActiveAllergyAction() {
		super("ActiveAllergy");
	}
	
	public static ActiveAllergyAction instance() {
		return (ActiveAllergyAction) Component.getInstance(ActiveAllergyAction.class, ScopeType.CONVERSATION);
	}

	/**
	 * check if patient claimed he has no allergy.
	 * @param phiDataAllergy
	 * @return return true if there is at least one active allergy in which the patient claimed that has no allergies,
	 * 			false otherwise
	 */
	public boolean isNoPatientAllergy(PhiDataModel<Allergy> phiDataAllergy) {
		
		if(phiDataAllergy == null || phiDataAllergy.isEmpty()) {
			return false;
		}
		
		boolean noPatientAllergy = false;
		
		List<Allergy> listActiveAllergy = phiDataAllergy.getList();
		if(listActiveAllergy == null || listActiveAllergy.isEmpty()) {
			return false;
		}
		
		for(Allergy allergy : listActiveAllergy) {
			if(allergy.isNoAllergy){
				return noPatientAllergy = true;
			}
		}
		
		return noPatientAllergy;
	} 
	
}