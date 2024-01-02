package com.phi.cs.validators;

import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.phi.entities.actions.CodeValueAction;
import com.phi.entities.dataTypes.CodeValue;

public class CodeValueDateValidator implements Validator {

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ValidatorException {
		
		Date date = null;

		if (arg2 instanceof Date) {
			date = (Date)arg2;

		}else {
			return;
		}
		
		CodeValueAction cvAction = CodeValueAction.instance();
		CodeValue cv = cvAction.getEntity();
		
		if(cv==null)
			return;
		
		Date start = cv.getValidFrom();
		Date stop = cv.getValidTo();
		
		if("startValidity_id".equals(arg1.getId())){
			if(stop!=null && date.after(stop)){
				throw new ValidatorException(new FacesMessage (FacesMessage.SEVERITY_ERROR, "", "La data deve essere successiva a quella di inizio validità"));
			}
		}
		
		if("endValidity_id".equals(arg1.getId())){
			if(start!=null && date.before(start)){
				throw new ValidatorException(new FacesMessage (FacesMessage.SEVERITY_ERROR, "", "La data deve essere precedente a quella di fine validità"));
			}
		}
	}
}
