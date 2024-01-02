package com.phi.cs.view.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;

import com.phi.cs.error.FacesErrorUtils;

public class NumberFormatUtils {

	private NumberFormat numberFormat = null;
	
	private static final int SEPARATOR_DECIMAL = 0;
	private static final int SEPARATOR_GROUPING = 1;
	
	protected NumberFormat getNumberFormat(final UIComponent component) {
		// LOCALE
		String cLocale = (String)component.getAttributes().get("cLocale");
		if (cLocale == null) {
			cLocale = org.jboss.seam.core.Locale.instance().getLanguage();
		}
		Locale loc = new Locale(cLocale);
		// NUMBER FORMAT INIT
		NumberFormat nf = DecimalFormat.getInstance(loc);

		nf.setRoundingMode(RoundingMode.DOWN);
		
		// MINIMUM FRACTION DIGITS
		String cMinDecimals = (String)component.getAttributes().get("cMinimumFractionDigits");
		if (cMinDecimals != null) {
			nf.setMinimumFractionDigits(Integer.parseInt(cMinDecimals));
		}

		// MAXIMUM FRACTION DIGITS
		String cMaxDecimals = (String)component.getAttributes().get("cMaximumFractionDigits");
		if (cMaxDecimals != null) {
			Integer cMaximumFractionDigits = Integer.parseInt(cMaxDecimals);
			Integer cMinimumFractionDigits = nf.getMinimumFractionDigits();
			if (cMaximumFractionDigits < nf.getMinimumFractionDigits()) {
				cMaximumFractionDigits = cMinimumFractionDigits;
			}
			nf.setMaximumFractionDigits(cMaximumFractionDigits);
		}

		// GROUPING
		String cGrouping = (String)component.getAttributes().get("cGroupingUsed");
		nf.setGroupingUsed(cGrouping == null ? false : Boolean.parseBoolean(cGrouping));

		//TODO:
		//MINIMUM INTEGER DIGITS
		//MAXIMUM INTEGER DIGITS
		//INTEGER ONLY
		//CURRENCY
		
		return numberFormat = nf;
	}
	
	protected String checkNumberSyntax(String value) throws Exception {
		String result = value;
		String[] separatorAndGrouping = getSeparatorAndgrouping();
		String decimal = separatorAndGrouping[SEPARATOR_DECIMAL];
		String grouping = separatorAndGrouping[SEPARATOR_GROUPING];
		final String regex = "^[+-]?\\d{1,3}(" + (".".equals(grouping) ? "\\" : "") + grouping + "\\d{3})*(" + (".".equals(decimal) ? "\\" : "") + decimal + "\\d+(E\\d+)?)?$";
		if (!Pattern.matches(regex, result)) {
			throw new Exception(String.format(FacesErrorUtils.getMessage("WRONG_NUMBER_FORMAT"),"1"+grouping+"234"+grouping+"567"+decimal+"89"));
		}
		return result;
	}
	
	protected String removeZeroes(String value) throws Exception {
		String result = value;
		String[] separatorAndGrouping = getSeparatorAndgrouping();
		String decimal = separatorAndGrouping[SEPARATOR_DECIMAL];
		
		result = result.replaceAll("(\\d)"+ (".".equals(decimal) ? "\\" : "") + decimal +"0+(\\D|$)", "$1$2"); // REMOVING ZEROES
		return result;
	}
	
	private String[] getSeparatorAndgrouping() {
		String decimal = "\\.";
		String grouping = ",";
		if (numberFormat instanceof DecimalFormat) {
			DecimalFormatSymbols symbols=DecimalFormat.class.cast(numberFormat).getDecimalFormatSymbols();
			decimal = String.valueOf(symbols.getDecimalSeparator());
			grouping = String.valueOf(symbols.getGroupingSeparator());
		}
		if (!numberFormat.isGroupingUsed()) {
			grouping = "";
		}
		return new String[]{decimal,grouping};
	}
}
