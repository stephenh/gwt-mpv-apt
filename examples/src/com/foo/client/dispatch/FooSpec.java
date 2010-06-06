package com.foo.client.dispatch;

import org.gwtmpv.GenDispatch;

@GenDispatch
public class FooSpec {

	Integer inInteger;
	String outFoo;
	Integer outBar;

	// Shouldn't really put stuff here, but checking compile time order
	public void foo() {
		new FooResult(1, "2"); // alphabetical not expected
	}

}
