package com.foo.client.place;

import org.gwtmpv.GenPlace;
import org.gwtmpv.place.PlaceRequest;
import org.gwtmpv.util.FailureCallback;

public class BarPresenter {

	@GenPlace("bar")
	public static void onRequest(PlaceRequest request) {
	}
	
	public static void test() {
		FailureCallback failureCallback = null;
		new BarPlace(failureCallback); // request isn't put into the cstr
	}

}
