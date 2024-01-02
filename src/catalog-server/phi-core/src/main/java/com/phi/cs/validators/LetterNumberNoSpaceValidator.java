package com.phi.cs.validators;

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;

public class LetterNumberNoSpaceValidator  implements javax.faces.validator.Validator {


		// only letters and numbers
		private static final String regex = "[a-zA-Z0-9]*"; 

		public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {

			String phrase = value.toString(); 
			
			if (phrase.equals("-") && !((UIInput)uiComponent).isRequired()) {  //Allowed for code of CodeValue 
				return;
			}
			
			if (!Pattern.matches(regex, phrase)) {
				throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_LETTER_NUMBER_ONLY_NOSPACE_ERR_CODE));
			}
			
		}
		

}
