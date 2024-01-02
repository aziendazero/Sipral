package com.phi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Annotation to distinguish timers which have to be managed by TimerManager
 * @author Francesco Bragagna
 */

@Target({ElementType.TYPE})
public @interface PhiTimer {
	public String description();
}
