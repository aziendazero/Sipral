package com.phi.cs.validators;

import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;


public class SearchControlValidator implements javax.faces.validator.Validator{
	
	//private static final String regex = "([%_\\-'` ]*[a-zA-Z][%_\\-'` ]*){2,}";
	private static final String regex = "([%_\\-'` ]*[a-zA-Z0-9\\u00C0-\\u00FF][%_\\-'` ]*){2,}"; // FIXME: da ricontrollare intervallo UNICODE

	@Override
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value)
			throws ValidatorException {
		
		if (facesContext == null) throw new NullPointerException("facesContext");
        if (uiComponent == null) throw new NullPointerException("uiComponent");
        // Don't perform validation
        if (isDisabled(uiComponent)) return;

        String textInput = value.toString();
        
        
        if (!Pattern.matches(regex, textInput)) {
        	// search criterion syntax verify
        	throw new ValidatorException(new FacesMessage (FacesMessage.SEVERITY_ERROR,"","Inserire almeno due lettere"));
        }
		
	}
	
	  private boolean isDisabled(UIComponent uiComponent) {
	        if (uiComponent instanceof HtmlInputText && ((HtmlInputText)uiComponent).isDisabled()) {
	        	return true;
	        }
	        return false;
	    }

}
