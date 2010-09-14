package com.foo.client.place;

import org.gwtmpv.GenPlace;
import org.gwtmpv.bus.DefaultEventBus;
import org.gwtmpv.bus.EventBus;
import org.gwtmpv.place.PlaceRequest;
import org.gwtmpv.util.FailureCallback;

public class ZazPresenter {

	@GenPlace("zaz")
	public static void onRequest(EventBus bus, PlaceRequest request) {
	}

	public static void test() {
		FailureCallback failureCallback = null;
		new ZazPlace(new DefaultEventBus(), failureCallback);
	}

}
