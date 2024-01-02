package com.phi.cs.validators;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

/**
 * validate a CheckBox Value. Throws an exception if CheckBox value is different from the expected one
 * 
 * @author Francesco Bruni
 *
 */
public class CheckedValidator implements javax.faces.validator.Validator {

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {

		if (facesContext == null) throw new NullPointerException("facesContext");
        if (uiComponent == null) throw new NullPointerException("uiComponent");

        if (!(uiComponent instanceof HtmlSelectBooleanCheckbox))
            // Don't perform validation
        	return;
        if (isDisabled((HtmlSelectBooleanCheckbox)uiComponent)) {
            // Don't perform validation
        	return;
        }
        
		List<UIComponent> componentChildren = uiComponent.getChildren();
		Boolean validValue = null;
		Boolean checkValue = (Boolean)value;
		
		//scan widget looking for f:params and other values
		for (Object child : componentChildren) {
			if(child instanceof UIParameter){
				UIParameter fparam = (UIParameter)child;
				String name = fparam.getName();
				if(name.equalsIgnoreCase("validValue")){
					// get checkbox value to validate
					validValue = Boolean.parseBoolean((String)fparam.getValue());
				}
			}
		}
		
				
		if (checkValue.compareTo(validValue) > 0) {
			// checkbox value must be FALSE
        	throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_CHECK_FALSE_ERR_CODE));
		} else if (checkValue.compareTo(validValue) < 0) {
			// checkbox value must be TRUE
        	throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_CHECK_TRUE_ERR_CODE));
		}

	}

	
    private boolean isDisabled(HtmlSelectBooleanCheckbox uiComponent) {
        if (uiComponent.isDisabled()) {
        	return true;
        }
        return false;

    }

}


