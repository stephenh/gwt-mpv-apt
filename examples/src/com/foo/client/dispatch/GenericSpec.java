package com.foo.client.dispatch;

import org.gwtmpv.GenDispatch;

@GenDispatch
public class GenericSpec<T, U extends Number> {

	T in1t;
	U in2u;
	T out1t;
	U out2u;
	
}
