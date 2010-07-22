package com.foo.client.dispatch;

import org.gwtmpv.GenDispatch;
import org.gwtmpv.In;
import org.gwtmpv.Out;

@GenDispatch
public class BarSpec {

	@In(1)
	Integer integer;
	@Out(1)
	String foo;
	@Out(2)
	Integer bar;

	// Shouldn't really put stuff here, but checking compile time order
	public void foo() {
		new BarResult("2", 1);
	}

}
