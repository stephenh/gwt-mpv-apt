package com.foo.client.dispatch;

import org.gwtmpv.GenDispatch;

@GenDispatch
public class BarSpec {

	Integer inInteger;
	String out1foo;
	Integer out2bar;

	// Shouldn't really put stuff here, but checking compile time order
	public void foo() {
		new BarResult("2", 1);
	}
	
}
