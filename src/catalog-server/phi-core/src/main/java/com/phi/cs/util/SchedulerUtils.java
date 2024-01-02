package com.phi.cs.util;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Locale;

/**
 * 
 * Utility class mainly for scheduler days localization 
 * 
 * @author Francesco Bruni
 *
 */
@BypassInterceptors
@Name("scheduler")
@Scope(ScopeType.EVENT)
public class SchedulerUtils implements Serializable {
	
	private static final long serialVersionUID = 7150791247587896627L;
	
	public List<SelectItem> getDays() {
		
		java.util.Locale locale = Locale.instance();
		
		List<SelectItem> days = new ArrayList<SelectItem>();  
		
		DateFormatSymbols dfse = new DateFormatSymbols(locale.ENGLISH);
		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] weekdaysLocale = dfs.getWeekdays();
		String[] weekdaysEng = dfse.getWeekdays();

		for (int i = 0 ; i < weekdaysEng.length ; i++) {
			if (weekdaysEng[i] != null && !weekdaysEng[i].equals("")) {
				days.add(new SelectItem(weekdaysEng[i].substring(0,3).toUpperCase(),weekdaysLocale[i]));
			}
		}
		
		
		return days;
	}

	public List<String> getDaysShort() {
		
		java.util.Locale locale = Locale.instance();
		
		List<String> days = new ArrayList<String>();  
		
		DateFormatSymbols dfs = new DateFormatSymbols(locale);
		String[] weekdaysLocale = dfs.getWeekdays();

		for (int i = 0 ; i < weekdaysLocale.length ; i++) {
			if (weekdaysLocale[i] != null && !weekdaysLocale[i].isEmpty()) {
				days.add(weekdaysLocale[i].substring(0,3).toUpperCase());
			}
		}
		
		return days;
	}

	public List<SelectItem> getTime(int maxSections) {
		
		List<SelectItem> sections = new ArrayList<SelectItem>();  
		
		for (int i = 0 ; i < maxSections ; i++) {
			sections.add(new SelectItem(String.valueOf(i),String.valueOf(i)));
		}
		
		return sections;
	}

}
