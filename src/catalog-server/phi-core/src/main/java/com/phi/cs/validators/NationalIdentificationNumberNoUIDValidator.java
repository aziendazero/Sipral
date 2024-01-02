package com.phi.cs.validators;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

public class NationalIdentificationNumberNoUIDValidator extends
		NationalIdentificationNumberValidator {
	
	public NationalIdentificationNumberNoUIDValidator() {
		check = false;
	}

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		super.validate(arg0, arg1, arg2);
	}

}
