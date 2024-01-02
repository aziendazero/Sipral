package com.phi.cs.view.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.LocaleSelector;

/**
 * 
 * Register language selection for PDF rendering and creates SelectItems list for selector
 * 
 * @author Francesco Bruni
 *
 */

@BypassInterceptors
@Name("localeChanger")
@Scope(ScopeType.CONVERSATION)
public class LanguageSelectorBean implements Serializable {

	private static final long serialVersionUID = -9001912991736568502L;

	private static final Logger log = Logger.getLogger(LanguageSelectorBean.class);

	private List<SelectItem> locales;

	public String getLocaleName(String locale) {
		Locale currentLocale = new Locale(locale);
		return currentLocale.getDisplayLanguage(LocaleSelector.instance().getLocale());
	}

	/**
	 * Creates session language dependent SelectItems list for locale selector
	 * 
	 * @param locale - comma separated language codes list
	 * @return session language dependent SelectItems list for locale selector
	 */
	public List<SelectItem> getLocales(String... locale) {
		if (locales == null) {
			List<SelectItem> localeItems = new ArrayList<SelectItem>();
			// GET SESSION LOCALE
			Locale sessionLocale = LocaleSelector.instance().getLocale();
			// POPULATE LIST
			for (String localeCode : locale) {
				Locale currentLocale = new Locale(localeCode);
				localeItems.add(new SelectItem(localeCode, currentLocale.getDisplayLanguage(sessionLocale)));
			}
			// SORT BY LABEL
			Collections.sort(localeItems, new Comparator<SelectItem>() {
				@Override
				public int compare(SelectItem arg0, SelectItem arg1) {
					if (arg0 == null && arg1 == null)
						return 0;
					else if (arg0 == null)
						return -1;
					else if (arg1 == null)
						return 1;

					String l0 = arg0.getLabel();
					String l1 = arg1.getLabel();

					return l0.compareToIgnoreCase(l1);
				}
			});
			locales = localeItems;
		}
		return locales;
	}
	
	
	private HashMap<String, String> locale = new HashMap<String, String>(){

		private static final long serialVersionUID = 1815606933754259989L;

		@Override
		public String get(Object key) {
			String languageSelector = super.get(key);
			if (languageSelector == null) {
				super.put((String)key, LocaleSelector.instance().getLanguage());
			}

			return super.get(key);
		}

	};

	public HashMap<String, String> getLocale() {
		return locale;
	}

	public void setLocale(HashMap<String, String> test) {
		this.locale = test;
	}

	public String getCurrentLocale() {
		String result = null;
		try {
			result = HttpServletRequest.class.cast(FacesContext.getCurrentInstance().getExternalContext().getRequest()).getParameter("locale");
		} catch (Exception e) {
			log.error("Error retrieving source widget name: using session locale", e);
		} finally {
			if (result == null) {
				result = LocaleSelector.instance().getLanguage();
			}
		}
		return result;
	}

}
