package com.phi.cs.validators;

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

/**
 * 
 * Verifies that user inserted only numbers
 * 
 * @author Francesco Bruni
 *
 */
public class NumbersOnlyValidator implements javax.faces.validator.Validator {

	// only digits syntax
	private static final String regex = "[+-]?\\d*\\.?\\d*"; 

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value)
			throws ValidatorException {

		String number = value.toString();
		
		if (!Pattern.matches(regex, number)) {
			// number verify
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_NUMBER_INVALID_NUMBER_FORMAT_ERR_CODE));
		}

	}

}
