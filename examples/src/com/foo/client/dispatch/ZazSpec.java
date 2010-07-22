package com.foo.client.dispatch;

import org.gwtmpv.GenDispatch;
import org.gwtmpv.In;
import org.gwtmpv.Out;

@GenDispatch
public class ZazSpec {

	@In(1)
	Integer integer;
	@In(2)
	int pint;
	@In(3)
	String[] strings;
	@Out(1)
	Integer bar;

}
