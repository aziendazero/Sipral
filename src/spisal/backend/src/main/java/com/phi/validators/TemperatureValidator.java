package com.phi.validators;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

public class TemperatureValidator implements Validator {

	//only digits syntax
	//private static final String regex = "\\d{3}.\\d{2}"; 

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
		String[] parts = null;
		String number = value.toString();
		
		if (number.contains("."))
			parts = number.split("\\.");

		/* SOLO IL PUNTO COME SEPARATORE DEI DECIMALI
		if (number.contains(","))
			parts = number.split("\\,");
		*/
		if (parts!=null){
			if ((parts.length!=2) || !isInteger(parts[0]) || !isInteger(parts[1]) || parts[0].length()<1 || parts[0].length()>3 || parts[1].length()<1 || parts[1].length()>2)
				throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_PRESSURE_FORMAT_ERR_CODE));
		} else if (!isInteger(number) || number.length()>3)
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_PRESSURE_FORMAT_ERR_CODE));
	}
	
	public boolean isInteger(String input){
		try {
			Integer.parseInt(input);
			return true;
	   } catch(Exception e) {
		   return false;
	   }
	}

}
