package com.phi.cs.validators;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.contexts.Contexts;

import com.phi.entities.actions.CodeSystemAction;
import com.phi.entities.actions.CodeValueAction;
import com.phi.entities.dataTypes.CodeValue;

public class DictionaryConsistencyValidator  implements javax.faces.validator.Validator{
	
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
		CodeValue cv = (CodeValue)Contexts.getConversationContext().get("CodeValue");
		String cvType = cv.getType();
		
		
//		if (uiComponent.getId().contains("Code") && ("T".equals(cvType))) {
//			//validating the field code in the form for a top level. Non needed  
//			return;
//		}
		if (uiComponent.getId().contains("DisplayName") && "C".equals(cvType)) {
			//validating the field display name in the form for a code at level. Non needed  
			return;
		}
		
		
		if ("T".equals(cvType) || ("C".equals(cvType) && cv.getParent()==null)) {
			//top level domain, or code at top level
			CodeSystemAction csa = CodeSystemAction.instance();
			
			try{
				csa.checkConsistency((String)value, cvType);
			}
			catch (Exception e) {
				throw new ValidatorException(new FacesMessage(e.getMessage()));
			}
		}
		else if ("S".equals(cvType) || "C".equals(cvType)) {
			CodeValueAction cva = CodeValueAction.instance();
			try{
				cva.checkConsistency((String)value, cv.getParent(), cvType);
			}
			catch (Exception e) {
				throw new ValidatorException(new FacesMessage(e.getMessage()));
			}
		}
		
	}
	
}
