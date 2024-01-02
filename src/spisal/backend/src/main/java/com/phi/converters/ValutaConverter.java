package com.phi.converters;

import java.text.NumberFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.phi.cs.view.util.NumberFormatUtils;

public class ValutaConverter extends NumberFormatUtils implements Converter {
	
	@Override
	public Object getAsObject(FacesContext fc, UIComponent component, String value) {
		Double valueDouble = null;
		
		if (value != null && !value.trim().isEmpty()) {
			try {
				value = value.replace("\u20AC", "");
				NumberFormat nf = getNumberFormat(component);
				valueDouble = nf.parse(checkNumberSyntax(value)).doubleValue();
			} catch (Exception e) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			}
		}
		
		return valueDouble;
	}
	
	@Override
	public String getAsString(FacesContext fc, UIComponent component, Object value) {
		String valueString = null;
		
		if (value instanceof Double) {
			try {
				Double valueDouble = (Double)value;
				NumberFormat nf = getNumberFormat(component);
				valueString = nf.format(valueDouble);
				valueString = "\u20AC"+valueString;

			} catch (Exception e) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			}
		} else {
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong type: expected Double but got "+(value == null ? "NULL" : value.getClass().getSimpleName()), null));
		}

		return valueString;
	}
}
