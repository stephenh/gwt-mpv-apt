package com.foo.client.place;

import org.gwtmpv.GenPlace;
import org.gwtmpv.bus.DefaultEventBus;
import org.gwtmpv.bus.EventBus;
import org.gwtmpv.place.PlaceRequest;

public class WozPresenter {

	@GenPlace(value = "woz", async = false)
	public static void onRequest(EventBus bus, PlaceRequest request) {
	}

	public static void test() {
		// this isn't async, so we don't need the FailureCallback
		new WozPlace(new DefaultEventBus());
	}

}
