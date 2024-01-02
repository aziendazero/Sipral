package com.phi.cs.validators;

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

/**
 * 
 * Verifies that only letters used
 * 
 * @author Francesco Bruni
 */
public class LettersOnlyValidator implements javax.faces.validator.Validator {

	// only letters and apostrophe syntax
	private static final String regex = "[a-zA-Z ']*"; 

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {

		String phrase = value.toString();
		
		if (!Pattern.matches(regex, phrase)) {
			// number verify
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_PHRASE_INVALID_PHRASE_FORMAT_ERR_CODE));
		}
		
	}
	
}