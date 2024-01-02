package com.phi.cs.view.converter;

import java.io.Serializable;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.hibernate.proxy.HibernateProxyHelper;

import com.phi.cs.CatalogPersistenceManagerImpl;
import com.phi.cs.catalog.adapter.CatalogAdapter;

/**
 * Converts JPA entity to entityName-id and vice-versa
 * Works for all Jpa entityies with id of type long or String.
 * @author Alex Zupan
 */
public class EntityConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		Object entity = null;
		
		if (value != null && !value.isEmpty() && value.contains("-")) {
			CatalogAdapter ca = CatalogPersistenceManagerImpl.instance();
			String[] entitNameAndId = value.split("-");
			String entityName = entitNameAndId[0];
			Serializable id;
			if (entitNameAndId[1].matches("\\d*")) {
				id = Long.parseLong(entitNameAndId[1]);
			} else {
				id = entitNameAndId[1];
			}
			entity = ca.load(entityName, id);
		}
		
		return entity;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		
		if (value == null) {
			return null;
		}
		
		if (value instanceof String) {
			return value.toString();
		}
		
		Serializable id = CatalogPersistenceManagerImpl.instance().getIdentifier(value);
		String entityName = HibernateProxyHelper.getClassWithoutInitializingProxy(value).getName();

		return entityName + "-" + id;
	}

}