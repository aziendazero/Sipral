package com.phi.cs.util;

import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;

/**
 * This class contains some utilities for UIComponents managing 
 * 
 * @author Francesco Bruni
 *
 */
public class ComponentUtils{

	/**
	 * Recursively looks through the given component tree for a component with a specific ID
	 * 
	 * @param parentComponent - the root of a UIComponent tree
	 * @param componentID - ID of the requested component
	 * @return the UIComponent we're looking for or null if component doesn't exist
	 */
	public static UIComponent getComponentByName(UIComponent parentComponent, String componentID) {
    	UIComponent component = parentComponent.findComponent(componentID);
    	if (component == null) {
    		List<UIComponent> children = parentComponent.getChildren();
    		if (!children.isEmpty()) {
    			Iterator<UIComponent> childrenIterator = children.iterator();
    			while (component == null && childrenIterator.hasNext()) {
    				component = getComponentByName(childrenIterator.next(), componentID);
    			}
    		}
    	}
    	return component;
    }

}
