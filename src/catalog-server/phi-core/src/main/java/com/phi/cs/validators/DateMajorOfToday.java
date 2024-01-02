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
 * Verifies that the given date is major of today date
 * 
 */
public class DateMajorOfToday implements javax.faces.validator.Validator {

	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {

		if (arg2 == null) {
			return;
		}

		if (arg2 instanceof Date) {
			Date date = (Date)arg2;

			Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.add(Calendar.DATE, 1);

			if (date.before(cal.getTime())) {
				throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_MAJOR_OF_TODAY));
			}
		}

	}

}
