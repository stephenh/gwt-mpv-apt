package com.foo.client.dispatch;

import org.gwtmpv.GenDispatch;
import org.gwtmpv.In;
import org.gwtmpv.Out;

@GenDispatch
public class GenericSpec<T, U extends Number> {

	@In(1)
	T t;
	@In(2)
	U u;

	@Out(1)
	T t2;
	@Out(2)
	U u2;

}
