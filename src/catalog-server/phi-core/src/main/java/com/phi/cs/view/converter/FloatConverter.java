package com.phi.cs.view.converter;

import java.text.NumberFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.phi.cs.view.util.NumberFormatUtils;

public class FloatConverter extends NumberFormatUtils implements Converter {

	@Override
	public Object getAsObject(FacesContext fc, UIComponent component, String value) {
		Float valueFloat = null;
		
		if (value != null && !value.trim().isEmpty()) {
			try {
				NumberFormat nf = getNumberFormat(component);
				valueFloat = nf.parse(checkNumberSyntax(value)).floatValue();
			} catch (Exception e) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			}
		}
		
		return valueFloat;
	}

	@Override
	public String getAsString(FacesContext fc, UIComponent component, Object value) {
		String valueString = null;
		
		if (value instanceof Float) {
			try {
				Float valueFloat = (Float)value;

				NumberFormat nf = getNumberFormat(component);
				
				// FORMAT
				valueString = removeZeroes(nf.format(valueFloat));				
			} catch (Exception e) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			}
		} else {
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong type: expected Float but got "+(value == null ? "NULL" : value.getClass().getSimpleName()), null));
		}

		return valueString;
	}

}
