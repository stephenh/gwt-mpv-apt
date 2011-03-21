package com.foo.client.place;

import org.gwtmpv.GenPlace;
import org.gwtmpv.place.PlaceRequest;
import org.gwtmpv.util.FailureCallback;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;

public class ZazPresenter {

	@GenPlace(name = "zaz")
	public static void onRequest(EventBus bus, PlaceRequest request) {
	}

	public static void test() {
		FailureCallback failureCallback = null;
		new ZazPlace(new SimpleEventBus(), failureCallback);
	}

}
