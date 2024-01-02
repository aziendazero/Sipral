package bundle;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class PhiResourceBundle extends ResourceBundle {

	public PhiResourceBundle() {
		setParent(getBundle("bundle.label.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale()));
	}

	@Override
	protected Object handleGetObject(String key) {
		try {
			return parent.getObject(key);

		} catch (MissingResourceException e) {
			// return empty string instead of ???key???
			return "";
		}
	}

	@Override
	public Enumeration<String> getKeys() {
		return parent.getKeys();
	}

}
