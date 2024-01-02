package com.phi.validators;

import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

public class CapacityValidator implements Validator {

	//only digits syntax
	//private static final String regex = "\\d{9}.\\d{1}"; 

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {
		String[] parts = null;
		Double n = null;
		String number = "";
		
		if(value instanceof Double){
			n = (Double)value;
			number = String.format(Locale.US, "%.2f", n);
		}else if(value instanceof String){
			number = (String) value;
		}		
		
		if (number.toString().contains("."))
			parts = number.toString().split("\\."); 

		/* SOLO IL PUNTO COME SEPARATORE DEI DECIMALI
		if (number.toString().contains(","))
			parts = number.toString().split("\\,");
		 */
		if (parts!=null){
			if ((parts.length!=2) || !isInteger(parts[0]) || !isInteger(parts[1]) || parts[0].length()<1 || parts[0].length()>9 || parts[1].length()<1 || parts[1].length()>2)
				throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_CAPACITY_FORMAT_ERR_CODE));
		} else if (!isInteger(number) || number.length()>5)
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_CAPACITY_FORMAT_ERR_CODE));
		/*} else if (!isInteger(number) || number.length()>9)
		//	throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_CAPACITY_FORMAT_ERR_CODE));*/
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
