package org.gwtasyncgen;

public @interface GenStub {
	public static final String NAME = "org.gwtasyncgen.GenStub";

	String name() default "";

	boolean isAbstract() default false;
}
