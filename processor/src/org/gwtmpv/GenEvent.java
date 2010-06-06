package org.gwtmpv;

/** Marks a class as a specification for GWT event classes. */
public @interface GenEvent {
	String methodName() default "";
}
