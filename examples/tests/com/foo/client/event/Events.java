package com.foo.client.event;

import com.foo.client.event.BarChangedEvent.BarChangedHandler;
import com.foo.client.event.FooChangedEvent.FooChangedHandler;

public class Events {

	public static class Foo implements FooChangedHandler {
		@Override
		public void onFooChanged(FooChangedEvent event) {
		}
	}
	
	public static class Bar implements BarChangedHandler {
		@Override
		public void onBarDone(BarChangedEvent event) {
		}
	}
}
