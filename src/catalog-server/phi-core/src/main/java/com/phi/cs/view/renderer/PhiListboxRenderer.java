package com.phi.cs.view.renderer;

import java.util.List;

import javax.faces.component.UISelectMany;
import javax.faces.context.FacesContext;

import com.sun.faces.renderkit.html_basic.ListboxRenderer;

/**
 * Custom Renderer: returns same list if selection remains unchanged. Avoid unnecessary db update.
 * Add to faces-config.xml:
 * <render-kit>
 * 		<renderer>
 *			<component-family>javax.faces.SelectMany</component-family>
 *		    <renderer-type>javax.faces.Listbox</renderer-type>
 *		    <renderer-class>com.phi.cs.view.renderer.PhiListboxRenderer</renderer-class>
 *		</renderer>
 * </render-kit>
 * @author alex.zupan
 */

public class PhiListboxRenderer extends ListboxRenderer {

	/**
	 * Return old list if values not changed
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Object convertSelectManyValuesForModel(FacesContext context, UISelectMany uiSelectMany, Class modelType, String[] newValues) {

		Object newValue = super.convertSelectManyValuesForModel(context, uiSelectMany, modelType, newValues);
		
		Object oldValue = uiSelectMany.getValue();
		
		if (oldValue instanceof List && newValue instanceof List) {
			List oldList = (List)oldValue;
			List newList = (List)newValue;
			
			if (oldList.size() == newList.size() && oldList.containsAll(newList)) {
				return oldList;
			} 
		}
		
		return newValue;
	}

}