package com.phi.cs.validators;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.PhiException;
import com.phi.entities.actions.BaseAction;
import com.phi.entities.baseEntity.BaseEntity;

public class UniqueRecordValidator implements javax.faces.validator.Validator {

	public static final String ID_SUFFIX = "_id";
	public static final String UNIQUENESS_ID = "com.phi.cs.validators.NationalIdentificationNumberValidator.UNIQUENESS";

	public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {
		checkUniqueness(arg1, arg2);
	}

	protected void checkUniqueness(UIComponent component, Object value)
			throws ValidatorException {

		String el = component.getValueExpression("value").getExpressionString();

		el = el.substring(2, el.length() - 1);

		String rootClass = el.substring(0, el.indexOf("."));

		String restriction = el.substring(el.indexOf(".") + 1);

		if (restriction.contains("'") && restriction.contains(".extension")) {
			restriction = restriction.replaceAll("'", "");
			restriction = restriction.replaceAll(".extension", "");
		}
		
		BaseAction<?, ?> action = null;

		try {
			action = (BaseAction<?, ?>) Class.forName("com.phi.entities.actions." + rootClass + "Action").newInstance();
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage("Unable to find "
					+ rootClass + "Action!"));
		}

		action.getEqual().put(restriction, value);
		action.getNotEqual().put("internalId", ((BaseEntity)action.getEntity()).getInternalId());
		
		List<?> results;

		try {
			results = action.list();
		} catch (PhiException e) {
			throw new ValidatorException(new FacesMessage("Error reading " + rootClass + " with restriction: " + restriction));
		}

		if (!results.isEmpty()) {
			throw new ValidatorException(FacesErrorUtils.manageErrorMessage(ErrorConstants.VALIDATION_VALUE_ALREADY_PRESENT_ERR_CODE));
		}
	}
}