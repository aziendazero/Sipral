package com.phi.cs.validators;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
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
public class DateOnlyValidator implements javax.faces.validator.Validator {

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value)
			throws ValidatorException {

		try {
			List<UIComponent> componentChildren = uiComponent.getChildren();
			String pattern = "dd/MM/yyyy";
			boolean allowPast = true;
			for (Object child : componentChildren) {
				if(child instanceof UIParameter){
					UIParameter fparam = (UIParameter)child;
					if(fparam.getName().equals("pattern")){
						pattern=(String)fparam.getValue();
					}
					if(fparam.getName().equals("allowPast")){
						allowPast=Boolean.parseBoolean((String)fparam.getValue());
					}
				}
			}
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			Date date;
			date = sdf.parse((String)value);
			if (!allowPast){
				// initialise the calendar to midnight to prevent
				// the current day from being rejected
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				if (cal.getTime().after(date))
					throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_DATE_INVALID_FORMAT_ERR_CODE));
			}
			// now test for legal values of parameters
			if (!sdf.format(date).equals((String)value))
				throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_DATE_INVALID_FORMAT_ERR_CODE));
		}catch (Exception e) {
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_DATE_INVALID_FORMAT_ERR_CODE));
		}
	}
	
}
