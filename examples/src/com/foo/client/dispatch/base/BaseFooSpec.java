package com.foo.client.dispatch.base;

import org.gwtmpv.GenDispatch;

@GenDispatch(baseAction = BaseAction.NAME, baseResult = BaseResult.NAME)
public class BaseFooSpec {

	Integer inInteger;
	String out1foo;

	// Shouldn't really put stuff here, but checking compile against base methods
	public void foo() {
		new BaseFooAction(1).inBaseAction();
		new BaseFooResult("1").inBaseResult();
	}

}
