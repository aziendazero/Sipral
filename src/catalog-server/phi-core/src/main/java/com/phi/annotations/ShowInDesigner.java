package com.phi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Methods and fields annotated with this annotation are shown in model explorer actions
 * @author Alex Zupan
 */

@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ShowInDesigner {
	public String description();
}
