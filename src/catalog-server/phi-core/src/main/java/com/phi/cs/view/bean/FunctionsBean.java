package com.phi.cs.view.bean;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.el.ELException;
import javax.faces.application.Application;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlInputTextarea;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.hibernate.proxy.HibernateProxy;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.SeamExpressionEvaluator;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.LocaleSelector;
import org.joda.time.DateTimeComparator;

import com.phi.annotations.ShowInDesigner;
import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.cs.error.ErrorConstants;
import com.phi.cs.error.FacesErrorUtils;
import com.phi.cs.exception.PhiException;
import com.phi.cs.repository.RepositoryManager;
import com.phi.cs.util.CsConstants;
import com.phi.entities.dataTypes.CodeValue;
import com.phi.entities.dataTypes.IVL;

/**
 * This Bean is though to collect implementation of some high level function used in the 
 * PHI FORM
 *
 */
@BypassInterceptors
@Name("function")
@Scope(ScopeType.EVENT)
public class FunctionsBean {
	private final static Logger log = Logger.getLogger(FunctionsBean.class);

	public static String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";
	private static Calendar calendar = new GregorianCalendar();
	private static final String NL = System.getProperty("line.separator");
	private static final String NOT_ALLOWED = "NA";
	
	public FunctionsBean() {

	}

	private static int getYear(Date thisDate) {
		calendar.setTime(thisDate);
		return calendar.get(Calendar.YEAR);
	}

	private static int getDayOfYear(Date thisDate) {
		calendar.setTime(thisDate);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	private static String trimRimBeanTag (String rimBeanTag) {
		String trimmedString = rimBeanTag;
		if (rimBeanTag != null && rimBeanTag.contains("RimBean.getRimValue"))
			trimmedString = rimBeanTag.substring("RimBean.getRimValue('".length()+1, rimBeanTag.length()-2);
		else if (rimBeanTag != null && rimBeanTag.contains("RimBean.getValue"))
			trimmedString = rimBeanTag.substring("RimBean.getValue('".length()+1, rimBeanTag.length()-2);
		else if (rimBeanTag != null && rimBeanTag.contains("RimBean.getRawValue"))
			trimmedString = rimBeanTag.substring("RimBean.getRawValue('".length()+1, rimBeanTag.length()-2);

		return trimmedString;
	}

	/**
	 * Computes system time using input pattern
	 * @param pattern
	 * @return
	 */
	public String time(String pattern) {
		String value = null;
		try {
			if (pattern == null)
				pattern = DEFAULT_DATE_PATTERN;
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			String lang = org.jboss.seam.core.Locale.instance().getLanguage();
			value = sdf.format(Calendar.getInstance(new Locale(lang)).getTime())
			.toString();
		} catch (RuntimeException e) {
			log.error(e);
			return "0";
		}
		return value;
	}

	/**
	 * Return current java Date()
	 */
	public Date currentDateTime() {
		return new Date();
	}
	
	
	public static Date date235959(Date date) {
	
		Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    c.set(Calendar.HOUR_OF_DAY, 23);
	    c.set(Calendar.MINUTE , 59);
	    c.set(Calendar.SECOND, 59);
	    c.set(Calendar.MILLISECOND, 999);
	  

	    return c.getTime();
	}
	
	public static Date date000000(Date date) {

	    Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    c.set(Calendar.HOUR_OF_DAY, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND,0);
	    c.set(Calendar.MILLISECOND, 0);
	  
	    return c.getTime();
	}
	/**
	 * Computes system time using input pattern
	 * @param pattern
	 * @return
	 */
	public Object createHQL(String query) {
		Object value = " ";
		try {
			CatalogAdapter ca = CatalogPersistenceManagerImpl.getAdapter("");
			Object res = (Object)ca.executeHQL(query);
			if (res instanceof ArrayList) {
				value = ((ArrayList)res).get(0);
			} else {
				value = res;
			}
		} catch (Exception e) {
			log.error(e);
			return "0";
		}
		return value;
	}

	/**
	 * Calculates the age starting from a string with pattern dd/MM/yyyy
	 * 
	 * @param birthday - string representing the birthday
	 * @return age
	 */
	public String age(String birthday) {
//		Date today =new Date ();

		String age = CsConstants.NOT_AVAILABLE;
		try {
			if (birthday != null && !birthday.isEmpty() && !birthday.equals(CsConstants.NOT_AVAILABLE) ) {
				DateFormat defaultDateFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
				Date birthDate = defaultDateFormat.parse(birthday);

				age = age(birthDate);
			}
			else 
				return CsConstants.NOT_AVAILABLE;
		}

		catch (ParseException e) {
			log.error(e);
			return CsConstants.NOT_AVAILABLE;
		}
		return age;		
	}
	/**
	 * Calculates the age starting from the birthday Date 
	 * 
	 * @param birthday - Timestamp of the birthday
	 * @return age
	 */
	
	public String age(Date birthday) {
	      return age(birthday, new Date());
	}

	public String age(Date birthday, Date atDate) {
	     //quello che prima c'era in age() a meno di impostar la data 
		if (atDate == null) {
			return age(birthday);
		}

		String lang = org.jboss.seam.core.Locale.instance().getLanguage();
		Locale locale = new Locale(lang);
		Calendar currentDayCal = Calendar.getInstance(locale);
		currentDayCal.setTime(atDate);
		Calendar birthDayCal = Calendar.getInstance(locale);
		birthDayCal.setTime(birthday);	
		int age = -1;

		if (birthday != null) {
		   age = currentDayCal.get(Calendar.YEAR) - birthDayCal.get(Calendar.YEAR);
		   if ( (birthDayCal.get(Calendar.MONTH) > currentDayCal.get(Calendar.MONTH)) || ((birthDayCal.get(Calendar.MONTH) == currentDayCal.get(Calendar.MONTH))&&(birthDayCal.get(Calendar.DATE) > currentDayCal.get(Calendar.DATE))) )
			   age--;
		}
		if (age<0)
			return CsConstants.NOT_AVAILABLE;
		return Integer.toString(age);		
	}
	
	

	/**
	 * 
	 * @param date - the date object
	 * @param pattern
	 * @return date formatted as pattern
	 */
	public String formatDate(Object date, String pattern, Locale locale){
		if (locale == null) {
			String lang = org.jboss.seam.core.Locale.instance().getLanguage();
			locale = new Locale(lang);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern,locale);
		if (date instanceof Date) {
			return sdf.format(date);
		}
		else if (date instanceof Long) {
			return sdf.format(new Date((Long)date));
		}
		else if (date instanceof String) {
			String dateString = (String)date; 
				if (!CsConstants.NOT_AVAILABLE.equals(dateString) && !dateString.isEmpty()) {
					ParsePosition pp = new ParsePosition(0);
					Date dateRes = sdf.parse(dateString,pp);
					if (dateRes != null) {
						String res = sdf.format(dateRes);
						return res;
					}
				} else {
					return CsConstants.NOT_AVAILABLE;
				}
		}
		return "";
	}

	//format date accordly to selected format in CsConstans.java, using language selected at login. 
	//CsConstans format is used by validators and must be set accordly to js.js jquery calendar widget
	public String formatDate(Object date) {
		String result = "";
		if(date instanceof Date) {
			result = formatDate((Date)date);
		} else if (date != null) {
			result = date.toString();
		}
		return result;
	}
	
	//format date accordly to selected format in CsConstans.java, using language selected at login. 
	//CsConstans format is used by validators and must be set accordly to js.js jquery calendar widget
	private String formatDate(Date date) {
		String lang = org.jboss.seam.core.Locale.instance().getLanguage();
		Locale locale = new Locale(lang);
		RepositoryManager repo = RepositoryManager.instance();
		ClassLoader cl = repo.getClassLoader();
		
		ResourceBundle bundle = ResourceBundle.getBundle("bundle.format.messages", locale, cl);

		String datePattern = bundle.getString("dateNormal");
		
		return formatDate(date, datePattern, locale);
	}
	
	//format date and time accordly to selected format in CsConstans.java, using language selected at login. 
	//CsConstans format is used by validators and must be set accordly to js.js jquery calendar widget
	public String formatDateTime(Date date) {
		String lang = org.jboss.seam.core.Locale.instance().getLanguage();
		Locale locale = new Locale(lang);
		RepositoryManager repo = RepositoryManager.instance();
		ClassLoader cl = repo.getClassLoader();
		
		ResourceBundle bundle = ResourceBundle.getBundle("bundle.format.messages", locale, cl);
		
		String dateTimePattern = bundle.getString("dateTimeNormal");
		
		return formatDate(date, dateTimePattern, locale);
	}
	
	//format date and time accordly to selected format in CsConstans.java, using language selected at login. 
	//CsConstans format is used by validators and must be set accordly to js.js jquery calendar widget
	public String formatTime(Date date) {
		String lang = org.jboss.seam.core.Locale.instance().getLanguage();
		Locale locale = new Locale(lang);
		RepositoryManager repo = RepositoryManager.instance();
		ClassLoader cl = repo.getClassLoader();
		
		ResourceBundle bundle = ResourceBundle.getBundle("bundle.format.messages", locale, cl);

		String timePattern = bundle.getString("timeNormal");
		
		return formatDate(date, timePattern, locale);
	}
	
	public String formatDate(Date date, String pattern, String language) {
		Locale locale = new Locale(language);
		return formatDate(date, pattern, locale);
	}
	
	
	/**
	 * 
	 * @param dateString - the date string to format
	 * @param pattern 
	 * @return date formatted as pattern
	 */
//	@Deprecated
//	public String formatDate(String dateString, String pattern){
//		return formatDate(Object.class.cast(dateString), pattern);
//	}

	
	public String formatDate(Object date, String pattern){
		String lang = org.jboss.seam.core.Locale.instance().getLanguage();
		Locale locale = LocaleSelector.instance().getLocale();
		return formatDate(date, pattern, locale);
	}

//	@Deprecated
//	public String formatDate(Object date, String inPattern, String outPattern){
//		String lang = org.jboss.seam.core.Locale.instance().getLanguage();
//		Locale locale = new Locale(lang);
//		return formatDate(date, inPattern, outPattern, locale);
//	}
//	
//	@Deprecated
//	public String formatDate(String dateString, String inPattern, String outPattern){
//		return formatDate(Object.class.cast(dateString), inPattern, outPattern);
//	}

	public String formatDate(Object date, String inPattern, String outPattern, Locale locale){
		if (locale == null) {
			String lang = org.jboss.seam.core.Locale.instance().getLanguage();
			locale = new Locale(lang);
		}
		SimpleDateFormat outSDF = new SimpleDateFormat(outPattern, locale);
		if (date instanceof Date) {
			return outSDF.format(date);
		} else if (date instanceof String) {
			SimpleDateFormat inSDF = new SimpleDateFormat(inPattern, locale);
			String dateString = (String)date;
				if (!CsConstants.NOT_AVAILABLE.equals(dateString) && !"".equals(dateString)) {
					ParsePosition pp = new ParsePosition(0);
					Date dateRes = inSDF.parse(dateString,pp);
					if (dateRes != null) {
						String res = outSDF.format(dateRes);
						return res;
					}
				} else {
					return CsConstants.NOT_AVAILABLE;
				}
		}
		return new String();
	}

	public boolean compareWithServerDate(String operator,String binding, String pattern) {
		//FIXME PHI 2
		throw new IllegalStateException("TO BE IMPLEMENTED WITH PHI 2");
//		RimBean rimBean = ((RimBean)Component.getInstance("RimBean"));
//		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
//		try {
//			String valueToBeCompared = rimBean.getValue(binding);
//			if (valueToBeCompared==null) return false;
//			if (valueToBeCompared!=null && valueToBeCompared.equals("")) return false;
//			Date comparedDate= sdf.parse(valueToBeCompared);
//			Date currentDate = Calendar.getInstance(Locale.getDefault()).getTime();
//			return validateComparisonResult(operator,currentDate.compareTo(comparedDate));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;		
	}

	private boolean validateComparisonResult(String operator, int result) {
		String OPERATOR_EQUALS = "==";
		String OPERATOR_EQUALS_ALT = "=";
		String OPERATOR_NOT_EQUALS = "!=";
		String OPERATOR_GREATER_THAN = ">";
		String OPERATOR_LESS_THAN = "<";
		String OPERATOR_GREATER_THAN_OR_EQUALS = ">=";
		String OPERATOR_LESS_THAN_OR_EQUALS = "<=";


		if (OPERATOR_EQUALS.equals(operator) || OPERATOR_EQUALS_ALT.equals(operator)) {
			return result == 0;
		} else if (OPERATOR_NOT_EQUALS.equals(operator)) {
			return result != 0;
		} else if (OPERATOR_GREATER_THAN.equals(operator)) {
			return result > 0;
		} else if (OPERATOR_LESS_THAN.equals(operator)) {
			return result < 0;
		} else if (OPERATOR_GREATER_THAN_OR_EQUALS.equals(operator)) {
			return result >= 0;
		} else if (OPERATOR_LESS_THAN_OR_EQUALS.equals(operator)) {
			return result <= 0;
		}

		throw new IllegalStateException("Operator '" + operator + "' unknown");
	}




	/**
	 * Sum numbers
	 * 
	 * @param pattern - Java Number Format patter string
	 * @param objects - list of  number
	 * @return sum
	 */
	public String sum(String pattern,Object ...objects) {
		float sum=0;
		String res = "";
		try {
			for(int i=0;i<objects.length;i++){
				sum = sum + Float.parseFloat((String)objects[i]);
			}
			if (pattern!=null && !pattern.equals("")){
				DecimalFormat nf = new DecimalFormat(pattern);
				res = nf.format(sum);
			}else res = Float.toString(sum);
		} catch (NumberFormatException e) {
			log.error(e);
			return "ERR:"+e.getMessage();
		}

		return res;		
	}

	/**
	 * Sum numbers
	 * 
	 * @param pattern - Java Number Format patter string
	 * @param objects - list of  number
	 * @return sum
	 */
	public String sumFourValues(String val1, String val2, String val3, String val4) {
		Integer sum = 0;

		try {
			if (val1!=null && val1 !="")
				sum=sum + Integer.parseInt(val1);

			if (val2!=null && val2 !="")
				sum=sum + Integer.parseInt(val2);

			if (val3!=null && val3 !="")
				sum=sum + Integer.parseInt(val3);

			if (val4!=null && val4 !="")
				sum=sum + Integer.parseInt(val4);

		} catch (Exception e) {
			log.error(e);
			return "0";
		}

		return sum.toString();
	}

	public String toUpper(String s) {
		if (s == null)
			return s;
		else
			return s.toUpperCase();
	}
	
	
	/**
	 * Concat string
	 * 
	 * @param objects - list of string
	 * @return string
	 */
	public  String concat(String ...strings) {
		String res = "";
		try {
			for(int i=0;i<strings.length;i++){
				res = res + strings[i];
			}
		} catch (NumberFormatException e) {
			log.error(e);
			return "ERR:"+e.getMessage();
		}

		return res;		
	}

	/**
	 * Concat string
	 * 
	 * @param objects - list of string
	 * @return string
	 */
	public  boolean isFirstGreaterThanSecond(String val1,String val2) {
		Boolean ret=false;
		Double Val1=0.0;
		Double Val2=0.0;

		try {	if ((val1!="")&&(val2!="")&&(val1!=null)&&(val2!=null))	{	
			Val1 = Double.parseDouble(val1);
			Val2 = Double.parseDouble(val2);
		}
		if (Val1>Val2) return true; else return false;



		} catch (NumberFormatException e) {
			log.error(e);
			return false;
		}

	}

	/**
	 * Return a humane readable string starting from CRON string
	 * 
	 * @param string - CRON string
	 * 
	 * @return string - humane readable string
	 * @throws PhiException 
	 * 
	 */
	public String getFrequencyByCron(String Cron) throws PhiException {
		try {
			//useful to get Langauge code to show the
			String languageCode = org.jboss.seam.core.Locale.instance().getLanguage();

			String ret = "";
			String str = "";
			String tokens[] = new String[6];

			if ((Cron != null) && (!Cron.equals(""))) {
				String cron = Cron;
				String token = "";
				int i = 0;
				int pos = 0;

				do {
					pos = cron.indexOf(" ");
					if (pos > 0) {
						tokens[i] = cron.substring(0, pos);
						cron = cron.substring(pos + 1, cron.length());
					}

					else
						tokens[i] = cron;
					i++;
				} while (pos > 0);

				if (tokens[5].equals("*"))
				{	str = languageCode.equals("en")? "Everyday" : "Ogni giorno";
				ret = ret.concat(str);
				}

				else {
					str = languageCode.equals("en")? "DAYS: " : "GIORNI: ";
					ret = ret.concat(str);

					if (tokens[5].contains("MON") || tokens[5].contains("2"))
					{	str = languageCode.equals("en")? "Monday " : "Lunedì ";
					ret = ret.concat(str);
					}

					if (tokens[5].contains("TUE") || tokens[5].contains("3"))
					{	str = languageCode.equals("en")? "Tuesday " : "Martedì ";
					ret = ret.concat(str);
					}

					if (tokens[5].contains("WED") || tokens[5].contains("4"))
					{	str = languageCode.equals("en")? "Wednesday " : "Mercoledì ";
					ret = ret.concat(str);
					}						

					if (tokens[5].contains("THU") || tokens[5].contains("5"))
					{	str = languageCode.equals("en")? "Thursday " : "Giovedì ";
					ret = ret.concat(str);
					}	

					if (tokens[5].contains("FRI") || tokens[5].contains("6"))
					{	str = languageCode.equals("en")? "Friday " : "Venerdì ";
					ret = ret.concat(str);
					}	

					if (tokens[5].contains("SAT") || tokens[5].contains("7"))
					{	str = languageCode.equals("en")? "Saturday " : "Sabato ";
					ret = ret.concat(str);
					}	

					if (tokens[5].contains("SUN") || tokens[5].contains("1"))
					{	str = languageCode.equals("en")? "Sunday " : "Domenica ";
					ret = ret.concat(str);
					}
				}

				String minutes = "";
				if (tokens[1].length() == 1)
					minutes = "0" + tokens[1];
				else
					minutes = tokens[1];

				String Hours = tokens[2];
				if ((Hours != null) && (!Hours.equals(""))) {
					if (Hours.equals("*")) {
						str = languageCode.equals("en")? "\nEvery hour, minute " : "\nOgni ora e minuti ";

						ret = ret.concat(str + minutes);

						// FOR MORE DETAILED HOURS DESCRIPTION
						// for (Integer From=0;From<24;From++)
						// { if (From.toString().length()==1) ret =
						// ret.concat("0"+From.toString()+":"+minutes+" ");
						// else ret = ret.concat(From.toString()+":"+minutes+"
						// ");
						// }
					}

					else {
						str = languageCode.equals("en")? "\nat: " : "\nORE: ";
						ret = ret.concat(str);
						String hours = Hours;
						String subHours = "";
						pos = 0;

						do {
							pos = hours.indexOf(",");
							if (pos > 0) {
								subHours = hours.substring(0, pos);
							} else
								subHours = hours;

							if (subHours.indexOf("-") < 0) {
								if (subHours.length() == 1)
									ret = ret.concat("0" + subHours + ":"
											+ minutes + " ");
								else
									ret = ret.concat(subHours + ":" + minutes
											+ " ");
							} else {
								Integer from = Integer.parseInt(subHours
										.substring(0, subHours.indexOf("-")));
								Integer to = Integer.parseInt(subHours
										.substring(subHours.indexOf("-") + 1,
												subHours.length()));

								for (Integer From = from; From <= to; From++) {
									if (From.toString().length() == 1)
										ret = ret.concat("0" + From.toString()
												+ ":" + minutes + " ");
									else
										ret = ret.concat(From.toString() + ":"
												+ minutes + " ");
								}
							}
							hours = hours.substring(pos + 1, hours.length());

						} while (pos > 0);
					}
				}
			}
			return ret;

		} catch (Exception e) {
			throw new PhiException(
					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_INTERNAL_MSG, e,
					ErrorConstants.SCHEDULE_ATTRIBUTE_TYPE_ERR_CODE);
		}
	}

	public String printIVLTS(String ivlTs, int lowOrHigh){
		String res = ivlTs;
		try {
			if (ivlTs.indexOf(",")>0){
				String[] ivl = ivlTs.split(",");
				if (lowOrHigh==1){
					res = ivl[1];
				}else{
					res = ivl[0];
				}
			}
		} catch (RuntimeException e) {
			log.error(e);
			return res;
		}

		return res;
	}

	/**
	 * Parses a string that represents a date to obtain a Date object
	 * 
	 * @param dateString - a string that represents a date 
	 * @param pattern - a pattern for the string-date 
	 * @return a Date object
	 */
	public Date stringToDate (String dateString, String pattern) {
		Date dateDate = null;

		DateFormat sdf = new SimpleDateFormat(pattern);
		try {
			dateDate = sdf.parse(dateString);
		} catch (ParseException e) {
			log.error(e);
			Calendar nullDate = new GregorianCalendar(1, Calendar.JANUARY, 1, 0, 0, 1);
			return nullDate.getTime();
		}

		return dateDate;
	}

	public Integer stringToInteger (String num) {
		if (num == null || num.isEmpty()) 
			return 0 ;
		
		else 
			return Integer.parseInt(num);
	}

	public Long stringToLong (String numString) {
		return stringToLong (numString, null);
	}
	
	/**
	 * Parses a string that represents a number to obtain a Long object
	 * 
	 * @param numString - a string that represents a number
	 * @param radix - the radix of the number-string to convert
	 * @return a Long object
	 */
	public Long stringToLong (String numString, String radix) {
		Long numLong = null;

		if (numString == null) {
			numLong = Long.MIN_VALUE;
		} else {
			if ("null".equals(radix) || radix == null || "".equals(radix)) {
				numLong = Long.parseLong(numString);
			} else {
				numLong = Long.parseLong(numString, Integer.parseInt(radix));
			}
		}
		return numLong;
	}


	/**
	 * Parses a string that represents a number to obtain a Double object
	 * 
	 * @param numString - a string that represents a number
	 * @return a Double object
	 */
	public Double stringToDouble (String numString) {
		Double numDouble = null;

		if (numString == null) {
			numDouble = Double.MIN_VALUE;
		} else {
			numDouble = Double.parseDouble(numString);
		}

		return numDouble;
	}

	/**
	 * Read APPLICATION CONTEXT ATTRIBUTE
	 * 
	 * @param label - attribute name
	 * @return a String value
	 */
	public String getContextValue(String label){
		Context context = Contexts.getApplicationContext();
		label = label.replaceAll("\\-", ";");
		String value = (String) context.get(label);
		return value;
	}

	/**
	 * Evalutes  Mathematical expression like x>6 AND x <10 replace X with VALUE
	 * 
	 * @param value - input value
	 * @param expression - math expression
	 * @return a String value
	 */
	public boolean evalExpression(String value, String expression){
		SeamExpressionEvaluator seamEL = new SeamExpressionEvaluator();
		String valorizedExpression = expression.replace("X", value);
		Boolean o2;
		try {
			o2 = (Boolean)seamEL.evaluate("#{"+valorizedExpression+ "}", Boolean.class, null, null);
		} catch (ELException e) {
			return false;
		}
		return o2.booleanValue();
	}
	
	/**
	 * Returns given string or a one character blank string. Used in reports
	 * to avoid bordered texts to shrink
	 * 
	 * @param valueText
	 * @return valueText or a one character blank string
	 */
	public String getReportTextValue(Object valueText) {
		if (valueText == null ||  valueText.toString().trim().isEmpty())
			return " ";
			
		String ret = valueText.toString(); 
		if (ret.startsWith("[") && ret.endsWith("]")){
			if (valueText instanceof List) {
				//toString of one list, returns always [ at the beginning and ] at the end, also if is empty.
				//removing first last char, you have toString of elementList comma separated.
				ret= ret.substring(1,ret.length()-1);
			}
			else if (valueText instanceof HibernateProxy ) {
				Object ob = ((HibernateProxy)valueText).getHibernateLazyInitializer().getImplementation();
				if (ob instanceof List) {
					ret= ret.substring(1,ret.length()-1);
				}
			}
		}
		
		return ret;
	}
	

	public String getIntegerPart(Object valueObj) {
		
		if (valueObj instanceof String) {
			String value = (String)valueObj;
			if (value.isEmpty())
				return "";
			try {
				Double num = Double.valueOf(value);
				int i = num.intValue();
				return ""+i;
			}
			catch (NumberFormatException  e) {
				log.error("Unable to take integer part of "+value);
				return "";
			}
		} else if (valueObj instanceof Number) {
			Number value = (Number)valueObj;
			return ""+value.intValue();
		} else {
			return "";	
		}
//		if (value.matches("[0-9]+[\\.,\\,]{0,1}[0-9]*")) { }
		 
	}

	
	public String getGenericTranslation (String bundleKey, String messageName) {
		if (bundleKey == null || bundleKey.isEmpty())
			return null;
		
		RepositoryManager repo = RepositoryManager.instance();
		ClassLoader cl = repo.getClassLoader();
		
		try {
			ResourceBundle resBndl = ResourceBundle.getBundle("bundle."+messageName+".messages", org.jboss.seam.core.Locale.instance(), cl);
			return resBndl.getString(bundleKey);
		} catch (MissingResourceException mre) {
			log.error("ERROR GETTING "+messageName.toUpperCase()+" BUNDLE");
			return bundleKey;
		}

	}
	
	public String getTranslation (String bundleKey) {
		return getGenericTranslation(bundleKey, "label");
	}
	
	public String getStaticTranslation (String bundleKey) {
		return getGenericTranslation(bundleKey, "static");
	}
	
	public String getFormatTranslation (String bundleKey) {
		return getGenericTranslation(bundleKey, "format");
	}
	
	/**
	 * Creates string langXx, where Xx is current language code
	 * 
	 * @return the string langXx, where Xx current language
	 */
	public String getLang() {
		return getLang("");
	}

	/**
	 * Creates string prefix + langXx, where Xx is current language code
	 * 
	 * @param prefix - a string to put before langXx
	 * 
	 * @return the string langXx, where Xx current language
	 */
	
	public String getLang(String prefix) {
		String result = prefix + "lang";
		String lang = org.jboss.seam.core.Locale.instance().getLanguage();
		result += WordUtils.capitalize(lang);
		return result;
	}

	public static FunctionsBean instance() {
        return (FunctionsBean) Component.getInstance(FunctionsBean.class, ScopeType.EVENT);
    }

	public static void textAreaInject(Object suggestion, String property, String appendType, Object textAreaInjector, Object... textAreas) {
		if (suggestion != null) { 
			//reset to defualt "-" value the injector, only if theinjector passed contains as first element a select item with label "-".
			if (textAreaInjector instanceof HtmlSelectOneMenu ) {
				HtmlSelectOneMenu injector = ((HtmlSelectOneMenu)textAreaInjector); 
				Object sel = injector.getChildren().get(0);
				if (sel instanceof UISelectItem && "-".equals(((UISelectItem)sel).getItemLabel()))
					injector.setValue("-");
			}
			for (Object textAreaObj : textAreas) {
				if (textAreaObj instanceof HtmlInputTextarea) {
					HtmlInputTextarea textArea = (HtmlInputTextarea)textAreaObj;
					if (textArea.isReadonly())
						continue;
					
//					FacesContext context = FacesContext.getCurrentInstance();
//				    ExpressionFactory expressionFactory = context.getApplication().getExpressionFactory();
//				    ELContext elContext = context.getELContext();
//				    ValueExpression vex = expressionFactory.createValueExpression(elContext, "KlinikReport.anamnesi", String.class);
//				    String result = (String) vex.getValue(elContext);
					
					String oldValue = (String)textArea.getValue(); 
					if (oldValue == null)
						oldValue = "";
					String newValue =""; 
					
					if (property== null || property.isEmpty()){
						if (suggestion instanceof CodeValue)
							newValue+=((CodeValue) suggestion).getCurrentTranslation();
						else 
							newValue+=suggestion.toString();
					}
					else {
						try {
							if (property.contains(";")) {
								for (String prop : property.split(";")) {
									newValue += (PropertyUtils.getProperty(suggestion, prop)).toString() + " ";
								}
							}
							else {
								String propValue = PropertyUtils.getProperty(suggestion, property).toString();
								if (propValue != null)
									newValue += propValue;
							}
							
						} catch (Exception e) {
							log.error("error trying to get property "+property+ " from Object "+suggestion);
						}
					}
					 
					if (newValue != null) {
						newValue = newValue.trim();
					}
					
					boolean isWysiwyg=false;
					if(textArea.getStyleClass() != null && textArea.getStyleClass().contains("formattedTextArea")) {
						isWysiwyg=true;
					}
					
					if("append".equals(appendType)) {
						if (isWysiwyg) {
							if (newValue.startsWith("<body>")) {
								newValue = newValue.replace("<body>", "");
								newValue = newValue.replace("</body>", ""); 
								newValue=oldValue+"\n"+newValue;
							}
							else {
								newValue=oldValue+"<p>"+newValue+"</p>";
							}
						}
						else {
							String breaks = oldValue.endsWith("\n") ? "" : "\n"; 
							newValue=oldValue+breaks+newValue;
						}
					}
					else { //prepend
						if (isWysiwyg){
							
							if (newValue.startsWith("<body>")) {
								newValue = newValue.replace("<body>", "");
								newValue = newValue.replace("</body>", "");
								newValue=newValue+"\n"+oldValue;
							}
							else {
								newValue="<p>"+newValue+"</p>"+oldValue;
							}
							
							
						}
						else {
							String breaks = newValue.endsWith("\n") ? "" : "\n"; 
							newValue=newValue+breaks+oldValue;
						}
					}
					textArea.setValue(newValue);

					ValueChangeEvent vce = new ValueChangeEvent(textArea,oldValue,newValue);
					vce.queue();
				}
			}
		}
	}

	/**
	 * Formats a given number into a localized currency string, with locale taken from current seam session locale and symbol at the end
	 * 
	 * @param inputNumber - the number to format. It may be a Number object or a string
	 * @throws Exception
	 */
	public static String formatCurrency(Object inputNumber) throws Exception {
		return formatCurrency(inputNumber, true, null, null);
	}
	
	/**
	 * Formats a given number into a localized currency string, with locale taken from current seam session locale
	 * 
	 * @param inputNumber - the number to format. It may be a Number object or a string
	 * @param symbolOnEnd - tells formatter if you want currency symbol at string end or at its beginning 
	 * @throws Exception
	 */
	public static String formatCurrency(Object inputNumber, boolean symbolOnEnd) throws Exception {
		return formatCurrency(inputNumber, symbolOnEnd, null, null);
	}
	
	/**
	 * Formats a given number into a localized currency string
	 * 
	 * @param inputNumber - the number to format. It may be a Number object or a string
	 * @param symbolOnEnd - tells formatter if you want currency symbol at string end or at its beginning 
	 * @param customLocale - a two letter language identifier
	 * @param customCurrency - an international currency code [EUR for euros, USD for american dollars, ...]
	 * @return a formatted currency string that represents a given number
	 * @throws Exception
	 */
	public static String formatCurrency(Object inputNumber, boolean symbolOnEnd, String customLocale, String customCurrency) throws Exception {
		
		Double currency; 

		Locale locale;
		if (customLocale != null && !customLocale.trim().isEmpty()) {
			locale = new Locale(customLocale);
		} else {
			locale = new Locale(org.jboss.seam.core.Locale.instance().getLanguage());
		}
		
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);

		if (inputNumber instanceof Number || inputNumber instanceof String) {
			try {
				currency = new Double(inputNumber.toString()); 
			} catch (Exception e) {
				throw new Exception(String.format(FacesErrorUtils.getMessage("WRONG_NUMBER_FORMAT"),"1.234"+dfs.getGroupingSeparator()+"567"+dfs.getDecimalSeparator()+"89"), e); 
			}
		} else {
			return (String)inputNumber;
		}
		
		NumberFormat nFormat = new DecimalFormat("#,##0.00;-#,##0.00", dfs);
		
		Currency cur;
		if (customCurrency != null && !customCurrency.trim().isEmpty()) {
			cur = Currency.getInstance(customCurrency);
		} else {
			cur = Currency.getInstance(new Locale("",locale.getLanguage().toUpperCase()));
		}
		
		
		return ((symbolOnEnd ? "" : (cur.getSymbol() + " ")) + nFormat.format(currency) + (symbolOnEnd ? (" " + cur.getSymbol()) : ""));
	}

	//bozza per jsFunction reload widget generico.
	private Object widgetIdToReload="";
	public Object getWidgetIdToReload() {
		return widgetIdToReload;
	}
	public void setWidgetIdToReload(Object widgetIdToReload) {
		this.widgetIdToReload = widgetIdToReload;
	}

	public void reloadWidget() {
		
		if (widgetIdToReload == null)
			return;
		
		if (widgetIdToReload instanceof String) {
			
			String[] widgetList = ((String)widgetIdToReload).split(";");
			if (widgetList == null || widgetList.length < 1) {
				return;
			}
			for (String widgetId : widgetList){
				Object widget = Contexts.getConversationContext().get(widgetId);
				if (widget instanceof HtmlInputTextarea ) {
					HtmlInputTextarea textArea = (HtmlInputTextarea)widget;
					if (textArea.isReadonly() || !textArea.isRendered())
						continue;

					String value =(String)textArea.getValue();
					
					ValueChangeEvent vce = new ValueChangeEvent(textArea,value,value);
					vce.queue();
					
				}
			}
			
		}
		
		
		
	}
	

	public boolean dateContained (Date check, IVL<Date> interval){
		if (interval == null)
			return false;
		return dateContained(check, (Date)interval.getLow(), (Date)interval.getHigh());
	}
	
	public boolean dateContained (Date toCheck, Date low, Date high) {
		
		if (toCheck == null) 
			return false;
		
		if (low != null && high != null && low.after(high)) {
			log.error("Incompatible dates!  LOW: "+low+ "  HIGH: "+high);
		}
		
		if (low != null && toCheck.before(low))
			return false;
		
		if (high != null && toCheck.after(high))
			return false;
		
		return true;
	}

	public boolean hasAtLeastAnExistingObject(Object... objects) {
		return hasAtLeastNExistingObject(1, objects);
	}
	
	public boolean hasAtLeastNExistingObject(int n, Object... objects) {
		int i = 0;
		for (Object object : objects) {
			if (object == null) {
				continue;
			} else {
				i++;
				if (i >= n) {
					return true;
				}
			}
		}
		return false;
	}
	
	public String truncate(String inputString, int truncateAt) {
		String outputString = inputString;
		
		if (outputString != null) {
			outputString = outputString.substring(0, Math.min(outputString.length(), truncateAt));
		}
		
		return outputString;
	}
	
	public String printStringList (List<String> list) {
		return Arrays.toString(list.toArray());
	}
	
	public String printStringArray (String[] s) {
		return Arrays.toString(s);
	}
	
	public List<String> stringAsList(String commaSeparatedString) {
		List<String> ret = new ArrayList<String>();
		if (commaSeparatedString == null || commaSeparatedString.isEmpty())
			return ret;
		
		for (String s : commaSeparatedString.split(",")) {
			ret.add(s);
		}
		return ret;
	}
	
	public List<Long> longAsList (String commaSeparatedLong) {
		List<Long> ret = new ArrayList<Long>();
		if (commaSeparatedLong == null || commaSeparatedLong.isEmpty())
			return ret;
		
		for (String s : commaSeparatedLong.split(",")) {
			Long l = Long.parseLong(s);
			ret.add(l);
		}
		return ret;
	}
	
	public List objectsAsList(Object... objs) {
		List l = new ArrayList<Object>();
		if (objs == null || objs.length < 1) {
			return l;
		}
		
		for (Object o : objs) {
			if (o instanceof String) {
				if (l.size() < 1)
					l=new ArrayList<String>();
				
				l.add((String)o);
			}
			
			else if (o instanceof Long) {
				if (l.size() < 1)
					l=new ArrayList<Long>();
				
				l.add((Long)o);
			}
			
			else
				l.add(o);
		}
		return l;
	}
	
	/**
	 * Return a list of properties, each one evaluated from its respective element in input list entityList
	 * eg outList[0] = entityList[0].property
	 * eg outList[1] = entityList[1].property
	 * @param entityList
	 * @param property
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public List<Object> propertyAsList (List<Object> entityList, String property) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Object> l = new ArrayList<Object>();
		if (entityList == null || entityList.isEmpty() || property == null || property.isEmpty())
			return l;
		
		for (Object o : entityList) {
			if (o == null )
				continue;
			
			Object value = null;
			try {
				if(o instanceof Map){
					value = resolveMapProperty((Map<Object,Object>)o, property);
				}else{
					value = PropertyUtils.getProperty(o,property);
				}
				
			}
			catch (Exception e) {
				if (e instanceof NestedNullException) {  //passed a property, with a relationship, which cannot be navigate (null), so propertyvalue cannot be get.
					
				}
				else {
					log.error("error getting property "+property+ " from entity "+o+" in list.");
				}
				continue;  
			}
			if (value != null)
				l.add(value);
		}
		
		return l;
		
	}
	
	public String getHostName() {
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.error("Check Network configuration and host resolution: hostname can not be found.");
			hostname = "unknowHost";//+(new Random()).nextInt(8000000) + 1000000;
		}
		return hostname;
	}
	
	//ATTENTION: one host can have multiple network interfaces, with different ip addresses.
	public String getHostAddress() {
		String hostaddr = "";
		try {
			hostaddr = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error("Check Network configuration and host resolution: host address can not be found.");
			hostaddr = "unknowAddr";//+(new Random()).nextInt(8000000) + 1000000;
		}
		return hostaddr;
	}
	
	public String getPortBindingSet() {
		java.util.Properties b =System.getProperties();
		if (b==null)
			return "PortsDefaultBindings";
		String portSet = (String)b.get("jboss.service.binding.set");
		if (portSet==null || portSet.isEmpty())
			portSet="PortsDefaultBindings";
		return portSet;
	}
	
	public String getHostNamePortsBind() {
		String hostname = getHostName();
		String portSet = getPortBindingSet();
		
		return hostname+"_"+portSet;
	}
	
	public List mergeList(List a, List b) {
		List ret = new ArrayList();
		if (a == null && b == null) {
			return ret;
		}
		if (a != null && b == null) {
			return a;
		}
		if (a == null && b != null) {
			return b;
		}
		//both != null
		ret.addAll(a);
		ret.addAll(b);
		return ret;
	}

	public String doubleToString(Double num) {
		return doubleToString(num, null);
	}

	public String doubleToString(Double num, Integer minDecimals) {
		return doubleToString(num, minDecimals, null);
	}

	public String doubleToString(Double num, Integer minDecimals, Integer maxDecimals) {
		return doubleToString(num, minDecimals, maxDecimals, null);
	}

	public String doubleToString(Double num, Integer minDecimals, Integer maxDecimals, Locale locale) {
		return doubleToString(num, minDecimals, maxDecimals, 1, null, locale);
	}

	public String doubleToString(Double num, Integer minDecimals, Integer maxDecimals, Integer minIntegers, Integer maxIntegers) {
		return doubleToString(num, minDecimals, maxDecimals, minIntegers, maxIntegers, null);
	}

	public String doubleToString(Double num, Integer minDecimals, Integer maxDecimals, Integer minIntegers, Integer maxIntegers, Locale locale) {
		String result = "";
		DecimalFormatSymbols dfs;
		if (num != null) {
			if (locale == null) {
				locale = new Locale(org.jboss.seam.core.Locale.instance().getLanguage());
			}
			if (minDecimals == null && maxDecimals == null) {
				result = String.format(locale, "%f", num);
			} else {
				NumberFormat nf = DecimalFormat.getInstance(locale);

				if (minIntegers != null) {
					nf.setMinimumIntegerDigits(minIntegers);
				}
				
				if (maxIntegers != null) {
					nf.setMaximumIntegerDigits(maxIntegers);
				}

				if (minDecimals != null) {
					nf.setMinimumFractionDigits(minDecimals);
				}
				
				if (maxDecimals != null) {
					nf.setMaximumFractionDigits(maxDecimals);
				}
				
				result = nf.format(num);
			}
		}
		
		return result;
	}
	
	@ShowInDesigner(description="check if code is in given list")
	public boolean hasCodeIn(String code, String... codeList){

		if(code!=null && codeList!=null && codeList.length>0){
			return Arrays.asList(codeList).contains(code);
		
		}else{
			return false;
		}
	}
	
	public boolean hasCodeInCvs(String code, List<CodeValue> cvs){
		if (code == null || cvs == null || cvs.isEmpty()){
			return false;
		}
		
		for (CodeValue cv : cvs) {
			if (cv!= null && code.equals(cv.getCode())){
				return true;
			}
		}
		
		return false;
		
	}
	
	@ShowInDesigner(description="Compares two dates without the time portion,  Returns: zero if order does not matter, negative value if date1 < date2, positive value otherwise")
	public int dateOnlyComparator (Date date1, Date date2) {
		return DateTimeComparator.getDateOnlyInstance().compare(date1, date2);
	}
	
	public int dateTimeComparator (Date date1, Date date2) {
		return DateTimeComparator.getInstance().compare(date1, date2);
	}
	
	public String getNewLine() {
		return NL;
	}

	public String cvListToTranslatedList(Collection<CodeValue> cvList) {
		return cvListToTranslatedList(cvList, false);
	}
	
	public String cvListToTranslatedList(Collection<CodeValue> cvList, boolean isvertical) {
		String result = "";
		
		for (CodeValue cv : cvList) {
			result += (result.isEmpty() ? "" : (isvertical ? NL : ", ")) + cv.getCurrentTranslation();
		}
		
		return result;
	}
	
	public List<SelectItem> createNumericListForCombos(Integer start, Integer end) {
		List<SelectItem> options = new ArrayList<SelectItem>();
		
		for(int i=start; i<=end; i++){
			SelectItem sel = new SelectItem();
			sel.setLabel(String.valueOf(i));
			sel.setValue(i);
			options.add(sel);
		}
		
		return options;
	}
	
	public List<SelectItem> createYesNoForCombos() {
		List<SelectItem> options = new ArrayList<SelectItem>();
		
		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();

		SelectItem yes = new SelectItem(new Boolean(true), (String)app.evaluateExpressionGet(fc, "${static.Label_referenceYES}", String.class));
		SelectItem no = new SelectItem(new Boolean(false), (String)app.evaluateExpressionGet(fc, "${static.Label_referenceNO}", String.class));
		
		options.add(yes);
		options.add(no);
		
		return options;
	}
	
	public String createYesNoForReports(Boolean yes) {
		String result = "";
		
		FacesContext fc = FacesContext.getCurrentInstance();
		Application app = fc.getApplication();

		result += app.evaluateExpressionGet(fc, "${static.Label_referenceYES}", String.class) + " [";
		if (yes != null && yes) {
			result += "X";
		} else {
			result += "  ";
		}
		result += "] - " + app.evaluateExpressionGet(fc, "${static.Label_referenceNO}", String.class) + " [";
		if (yes != null && !yes) {
			result += "X";
		} else {
			result += "  ";
		}
		result += "]";		
		return result;
	}
	
	public List<Object> concatLists(List<Object>... lists) {
		List<Object> result = new ArrayList<Object>();
		
		for (List<Object> list : lists) {
			result.addAll(list);
		}
		
		return result;
	}

	/**
	 * Gives current {@link Date} with a custom date/time offset
	 * 
	 * @param days - number of days to add
	 * @param months - number of months to add
	 * @param years - number of years to add
	 * @param hours - number of hours to add
	 * @param minutes - number of minutes to add
	 * @return current {@link Date} with given offset 
	 */
	public Date currentDateTimeWithOffset(Integer days, Integer months, Integer years, Integer hours, Integer minutes) {
		return addOffsetToDate(new Date(), days, months, years, hours, minutes);
	}
	
	/**
	 * Gives current {@link Date} with a custom date/time offset
	 * 
	 * @param hours - number of hours to add
	 * @param minutes - number of minutes to add
	 * @return current {@link Date} with given offset 
	 */
	public Date currentDateTimeWithOffset(Integer hours, Integer minutes) {
		return currentDateTimeWithOffset(0, 0, 0, hours, minutes);
	}
	
	/**
	 * Gives current {@link Date} with a custom date/time offset
	 * 
	 * @param days - number of days to add
	 * @param months - number of months to add
	 * @param years - number of years to add
	 * @return current {@link Date} with given offset 
	 */
	public Date currentDateTimeWithOffset(Integer days, Integer months, Integer years) {
		return currentDateTimeWithOffset(days, months, years, 0, 0);
	}
	
	/**
	 * Shifts a given {@link Date} with a custom date/time offset
	 * 
	 * @param date - a {@link Date} to shift
	 * @param days - number of days to add
	 * @param months - number of months to add
	 * @param years - number of years to add
	 * @param hours - number of hours to add
	 * @param minutes - number of minutes to add
	 * @return input {@link Date} with given offset 
	 */
	public Date addOffsetToDate(Date date, Integer days, Integer months, Integer years, Integer hours, Integer minutes) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (days != null) cal.add(Calendar.DAY_OF_MONTH, days);//longToInteger(days));
		if (months != null) cal.add(Calendar.MONTH, months);//longToInteger(months));
		if (years != null) cal.add(Calendar.YEAR, years);//longToInteger(years));
		if (hours != null) cal.add(Calendar.HOUR_OF_DAY, hours);//longToInteger(hours));
		if (minutes != null) cal.add(Calendar.MINUTE, minutes);//longToInteger(minutes));
		return cal.getTime();
	}
	
	/**
	 * Converts a long number into an integer
	 */
	public Integer longToInteger(Long nLong) {
		Integer nInteger = null;
		
		if (nLong != null) {
			if (nLong > Integer.MAX_VALUE) {
				nInteger = Integer.MAX_VALUE;
			} else if (nLong < Integer.MIN_VALUE) {
				nInteger = Integer.MIN_VALUE;
			} else {
				nInteger = nLong.intValue();
			}
		}
		
		return nInteger;		
	}

	public Object resolveMapProperty(Map<Object,Object> map, String binding){
		
		if(map!=null && binding!=null && !binding.isEmpty()){
			String[] tokens = binding.split("\\.");
			if(tokens.length==1)
				return map.get(tokens[0]);
			
			if(tokens.length>1){
				Object obj = map.get(tokens[0]);
				if(obj instanceof Map){
					return resolveMapProperty((Map)obj, binding.substring(binding.indexOf(".")+1));
				}else{
					//should never happen
					return obj;
				}
			}
		}
		
		return null;
	}
	
	public Object setMapProperty(Map<Object,Object> map, String binding, Object value){
		
		if(map!=null && binding!=null && !binding.isEmpty()){
			String[] tokens = binding.split("\\.");
			if(tokens.length==1){
				map.put(tokens[0], value);
				return map.get(tokens[0]);
			}
			if(tokens.length>1){
				Object obj = map.get(tokens[0]);
				if(obj instanceof Map){
					return setMapProperty((Map)obj, binding.substring(binding.indexOf(".")+1), value);
				}else{
					//should never happen
					return obj;
				}
			}
		}
		
		return null;
	}

	public void historyModeOn(){
		Contexts.getConversationContext().set("history",true);
	}
	
	public void historyModeOff(){
		Contexts.getConversationContext().remove("history");
	}

	public List<SelectItem> createOptions(String...opts){
		List<SelectItem> lst = new ArrayList<SelectItem>();
		for(String opt : opts) {
			String[] optParts = opt.split(":");
			if (optParts.length == 1) {
				lst.add(new SelectItem(optParts[0]));
			} else {
				lst.add(new SelectItem(optParts[0], optParts[1]));
			}
		}
		return lst;
	}
	
	// convert a color like #FFC000 to the corrispondent rgba(r,g,b,alfa) css string 
	public String transparentColor (String hex, double alfa) {
		if (hex == null || hex.length()<6 || hex.length() > 7|| alfa > 1 || alfa < 0){
			return "";
		}
		
		if (hex.startsWith("#")){
			hex = hex.substring(1,7);
		}
		
		String r = hex.substring(0,2);
		String g = hex.substring(2,4);
		String b = hex.substring(4,6);
		
		String ret = "rgba("+Integer.parseInt(r, 16)+","+Integer.parseInt(g, 16)+","+Integer.parseInt(b, 16)+","+alfa+")";
		
		return ret;
	}

	public String printDataOrOther(String data) {
		return printDataOrOther(data, "-");
	}

	public String printDataOrOther(String data, String other) {
		if (data == null || data.trim().isEmpty()) {
			return other;
		} else {
			return data;
		}
	}
	
	public String newLineToHtmlConversion(final String original) {
		String converted = original;
		if (converted != null) {
			FunctionsBean fb = FunctionsBean.instance();
			converted = converted.replace(NL, fb.getStaticTranslation("newLineHtml"));
		}
		return converted;
	}

}
