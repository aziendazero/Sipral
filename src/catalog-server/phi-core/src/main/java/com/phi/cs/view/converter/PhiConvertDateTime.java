package com.phi.cs.view.converter;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * 
 * @author Francesco Bruni
 * 
 * Converter for date only calendars used as search filter. If Search is GreaterThan or LessOrEqual
 * converter adds 23:59:59 to Date object in order to perform a right check against used string date
 * 
 * TODO: AFTER UPGRADE GETTING PARAMETERS FROM ATTRIBUTES WE MAY REMOVE ANNOTATIONS AND REGISTER CONVERTER IN FACES-CONFIG
 */

@org.jboss.seam.annotations.faces.Converter
@BypassInterceptors
@Name("phiConvertDateTime")
public class PhiConvertDateTime implements Converter {

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String dateString) {
		String datePattern = null;
		String filterOperator = null;
		//TODO: UPGRADE GETTING PARAMETERS FROM ATTRIBUTES ALSO
		for (UIComponent child : arg1.getChildren()) {
			if (child instanceof UIParameter) {
				UIParameter parameter = (UIParameter)child;
				String name = parameter.getName();
				String value= (String)parameter.getValue();
				if (name.equals("datePattern")) {
					datePattern = value;
				} else if (name.equals("filterOperator")) {
					filterOperator = value;
				}
			}
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		Date newDate = sdf.parse(dateString, new ParsePosition(0));
		if (newDate != null) {
			if ("le".equals(filterOperator) || "gt".equals(filterOperator)) {
				//newDate.setTime(newDate.getTime()+86399000);//aggiungo 23:59:59..NON TIENE CONTO DEI DST!
				Calendar cal = Calendar.getInstance();
				cal.setTime(newDate);
				cal.add(Calendar.DAY_OF_YEAR, 1);//aggiungo 1g
				cal.add(Calendar.MILLISECOND, -1);//tolgo 1ms
				newDate = cal.getTime();
			} else if ("lt".equals(filterOperator)) {
				// NOTHING TO DO
			} else if ("ge".equals(filterOperator)) {
				// NOTHING TO DO
			}
		}
		
		return newDate;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object dateObject) {
		String result = null;
		if (dateObject != null) {
			String datePattern = null;
			//TODO: UPGRADE GETTING PARAMETERS FROM ATTRIBUTES ALSO
			for (UIComponent child : arg1.getChildren()) {
				if (child instanceof UIParameter) {
					UIParameter parameter = (UIParameter)child;
					String name = parameter.getName();
					String value= (String)parameter.getValue();
					if (name.equals("datePattern")) {
						datePattern = value;
					}
				}
			}

			SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
			result = sdf.format((Date)dateObject);
		}
		return result;
	}

}
