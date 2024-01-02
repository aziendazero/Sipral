package com.phi.validators;

import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.entities.actions.AlfrescoDocumentAction;
import com.phi.entities.baseEntity.AlfrescoDocument;

public class AlfrescoNameValidator implements Validator {


	// only digits syntax
	//private static final String regex = "[+-]?\\d*\\.?\\d*"; 

	public void validate(FacesContext facesContext, UIComponent uiComponent, Object value) throws ValidatorException {

		String documentName = value.toString();
		AlfrescoDocument ad = AlfrescoDocumentAction.instance().getEntity();
		AlfrescoDocumentAction ada = AlfrescoDocumentAction.instance();
		ad.setName(documentName);
		
		if (!ada.checkValidName(ad)) {
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_ALFRESCO_DUPLICATE_NAME_ERR_CODE));
		}
		
		

	}



}
