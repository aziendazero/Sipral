package com.phi.validators;

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

public class CoordsLatitudeValidator implements Validator {


	// only digits syntax
	private static final String regex = "[+-]?\\d*\\.?\\d*"; 

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {

		String number = value.toString();

		if (!Pattern.matches(regex, number)) {
			// number verify
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_NUMBER_INVALID_NUMBER_FORMAT_ERR_CODE));
		}else{
		
			Double longValue = Double.parseDouble(value.toString());
			
			if(longValue>90 || longValue<-90){
				throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_LATITUDE_OUT_OF_RANGE_ERR_CODE));
			}
			
		}
		
		

	}



}
