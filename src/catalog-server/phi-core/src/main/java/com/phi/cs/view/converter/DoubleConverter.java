package com.phi.cs.view.converter;

import java.math.RoundingMode;
import java.text.NumberFormat;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import com.phi.cs.view.util.NumberFormatUtils;

public class DoubleConverter extends NumberFormatUtils implements Converter {

	@Override
	public Object getAsObject(FacesContext fc, UIComponent component, String value) {
		Double valueDouble = null;
		
		if (value != null && !value.trim().isEmpty()) {
			try {
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
				//nf.setRoundingMode(RoundingMode.HALF_EVEN);  //FIXME: meglio impostare rounding piuttosto che troncare?
				// FORMAT
				if(component instanceof HtmlInputText && ((HtmlInputText)component).getStyleClass()!=null && ((HtmlInputText)component).getStyleClass().contains("autoNumeric")){
					return nf.format(valueDouble);
				}
				valueString = removeZeroes(nf.format(valueDouble));				
			} catch (Exception e) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			}
		} else {
			throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Wrong type: expected Double but got "+(value == null ? "NULL" : value.getClass().getSimpleName()), null));
		}

		return valueString;
	}

}
