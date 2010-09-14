package com.foo.client.place;

import org.gwtmpv.GenPlace;
import org.gwtmpv.util.FailureCallback;

public class FooPresenter {

	@GenPlace("foo")
	public static void onRequest() {
	}

	public static void test() {
		FailureCallback failureCallback = null;
		new FooPlace(failureCallback);
	}
	
}
