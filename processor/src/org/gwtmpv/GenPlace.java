package org.gwtmpv;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/** Marks a method as an entry point for a place. */
@Target({ ElementType.METHOD })
public @interface GenPlace {
	String value();
	boolean async() default true;
}
