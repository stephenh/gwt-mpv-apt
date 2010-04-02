package org.gwtasyncgen;

public @interface GenStub {
	String name() default "";
	boolean isAbstract() default false;
}
