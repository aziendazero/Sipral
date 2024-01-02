package com.phi.cs.view.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.HibernateProxyHelper;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;
import com.phi.entities.dataTypes.CodeValue;

/**
 * Converts CodeValue to id and vice-versa
 * @author Alex Zupan
 */
public class CodeValueConverter implements Converter {
	
	private static final String CV_PACKAGE = "com.phi.entities.dataTypes.";

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		CodeValue cv = null;
		
		if (value != null && !value.isEmpty() && value.contains("-")) {
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
//			String[] entitNameAndId = value.split("-");
//			String entityName = CV_PACKAGE + entitNameAndId[0];
//			String id = entitNameAndId[1];
			String entityName = CV_PACKAGE + value.substring(0, value.indexOf("-"));
			String id = value.substring(value.indexOf("-")+1);
			
			cv = (CodeValue)ca.load(entityName, id);
		}
		
		return cv;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String result = "";
		if (value instanceof CodeValue || value instanceof HibernateProxy) {
			result = HibernateProxyHelper.getClassWithoutInitializingProxy(value).getSimpleName() + "-" + ((CodeValue)value).getId();
		} else if (value != null) {
			result = value.toString();
		}
		return result;
	}

}