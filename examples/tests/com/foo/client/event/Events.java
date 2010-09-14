package com.foo.client.event;

import org.gwtmpv.bus.DefaultEventBus;
import org.gwtmpv.bus.EventBus;

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

		System.out.println(new BarChangedEvent(1));
		System.out.println(new BarChangedEvent(null));
		System.out.println(new BoundsChangedEvent<Number, Number>(1, 2));

		System.out.println(new BarChangedEvent(1).equals(new BarChangedEvent(1)));
		System.out.println(new BarChangedEvent(1).equals(new BarChangedEvent(2)));

		// HandlerManager bus = new HandlerManager(null);
		EventBus bus = new DefaultEventBus();
		BarChangedEvent.fire(bus, 1);
		BoundsChangedEvent.fire(bus, 1, 2);

		t.fireEvent(new GenericChangedEvent<String>("foo"));
		// t.fireEvent(new GenericChangedEvent<Integer>(1));
	}
}
