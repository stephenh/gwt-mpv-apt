package com.foo.client.dispatch;

import org.gwtasyncgen.GenDispatch;

@GenDispatch
public class FooSpec {
	
	public Integer inInteger;
	public String outBString;
	public Integer outAInteger;

	// Shouldn't really put stuff here, but checking compile time order
	public void foo() {
		new FooResult(1, "2"); // not expected
	}
}
