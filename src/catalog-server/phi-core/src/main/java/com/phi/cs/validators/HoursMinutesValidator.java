package com.phi.cs.validators;

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Validator;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

/**
 * this class validates a hh:mm syntax.
 * 
 * 
 * @author Fabio Esposito
 *
 */
public class HoursMinutesValidator implements javax.faces.validator.Validator {


	private static final String regex = "(([0-1][0-9]|[2][0-3])\\:([0-5][0-9]))|([2][4]\\:[0][0])";


	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value)
			throws ValidatorException {


		if (facesContext == null) throw new NullPointerException("facesContext");
        if (uiComponent == null) throw new NullPointerException("uiComponent");
        // Don't perform validation
        if (isDisabled(uiComponent)) return;

        String hhmm = value.toString();
        if (!Pattern.matches(regex, hhmm)) {
			// hh:mm syntax verify
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_TIME_INVALID_HHMM_FORMAT_ERR_CODE));
		}
       
	}

    private boolean isDisabled(UIComponent uiComponent) {
        if (uiComponent instanceof HtmlInputText && ((HtmlInputText)uiComponent).isDisabled()) {
        	return true;
        }
        return false;
    }

}
