package com.foo.client.event;

import com.foo.client.event.BarChangedEvent.BarChangedHandler;
import com.foo.client.event.FooChangedEvent.FooChangedHandler;
import com.foo.client.event.GenericChangedEvent.GenericChangedHandler;
import com.google.gwt.event.shared.HandlerManager;

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

	public static void main(String[] args) {
		HandlerManager t = new HandlerManager(null);
		t.addHandler(GenericChangedEvent.getType(), new GenericChangedHandler<String>() {
			@Override
			public void onGenericChanged(GenericChangedEvent<String> event) {
				System.out.println(event.getT());
			}
		});

		t.fireEvent(new GenericChangedEvent<String>("foo"));
		// t.fireEvent(new GenericChangedEvent<Integer>(1));
	}
}
