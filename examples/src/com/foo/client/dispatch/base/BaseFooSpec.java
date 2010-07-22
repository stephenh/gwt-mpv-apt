package com.foo.client.dispatch.base;

import org.gwtmpv.GenDispatch;
import org.gwtmpv.In;
import org.gwtmpv.Out;

@GenDispatch(baseAction = BaseAction.NAME, baseResult = BaseResult.NAME)
public class BaseFooSpec {

	@In(1)
	Integer integer;
	@Out(1)
	String foo;

	// Shouldn't really put stuff here, but checking compile against base methods
	public void foo() {
		new BaseFooAction(1).inBaseAction();
		new BaseFooResult("1").inBaseResult();
	}

}
