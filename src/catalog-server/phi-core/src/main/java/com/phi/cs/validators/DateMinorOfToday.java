package com.phi.cs.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

/**
 * 
 * Verifies that the given date is minor of today date
 * 
 */
public class DateMinorOfToday implements javax.faces.validator.Validator {

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {

		if (value == null) {
			return;
		}
		
		if (value instanceof Date) {
			Date date = (Date)value;

			Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.add(Calendar.DATE, -1);

			if (date.after(cal.getTime()) ) {			
				throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_MINOR_OF_TODAY));
			}


		}
		
		
	}
	
}