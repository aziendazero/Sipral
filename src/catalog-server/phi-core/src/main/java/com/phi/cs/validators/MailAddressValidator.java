package com.phi.cs.validators;

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

/**
 * this class validates an e-mail address syntax.
 * It can accept also multiple mails separeted by semicolon.
 * 
 * @author Francesco Bruni
 *
 */
public class MailAddressValidator implements javax.faces.validator.Validator {

	// E-Mail syntactic regular expression 
	private static final String regex = "[\\w\\-\\.]+\\@[\\w\\-\\.]+\\.[a-zA-Z]{2,6}";
	private static final String SEMICOLON_CHAR =";";

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value)
			throws ValidatorException {


		if (facesContext == null) throw new NullPointerException("facesContext");
        if (uiComponent == null) throw new NullPointerException("uiComponent");
        // Don't perform validation
        if (isDisabled(uiComponent)) return;

        String mailInput = value.toString();
        
        if (mailInput.contains(SEMICOLON_CHAR)) {
        	// validate multiple mail addresses
        	String[] splittedMail = mailInput.split(SEMICOLON_CHAR);
        	for (int i = 0; i < splittedMail.length; i++) {
        		
        		//allow to have spaces between address, before/after semicolumn
        		if (!Pattern.matches("\\s*"+regex+"\\s*", splittedMail[i])) {
         			// e-mail syntax verify
         			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_MAIL_INVALID_MAIL_FORMAT_ERR_CODE));
         		}
 			}
        	
        }
        else {
        	//validate single mail address
        	if (!Pattern.matches(regex, mailInput)) {
     			// e-mail syntax verify
     			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_MAIL_INVALID_MAIL_FORMAT_ERR_CODE));
     		}
        }
        
       
       
	}

    private boolean isDisabled(UIComponent uiComponent) {
        if (uiComponent instanceof HtmlInputText && ((HtmlInputText)uiComponent).isDisabled()) {
        	return true;
        }
        return false;
    }

}
