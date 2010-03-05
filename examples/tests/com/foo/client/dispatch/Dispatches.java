package com.foo.client.dispatch;

import com.foo.client.event.GenericChangedEvent;
import com.foo.client.event.GenericChangedEvent.GenericChangedHandler;
import com.google.gwt.event.shared.HandlerManager;

public class Dispatches {

	public static void main(String[] args) {
		new BarAction(1);
		new BarResult("foo", 2);

		// i really need junit
		System.out.println(new BarAction(1).hashCode() == new BarAction(1).hashCode());

		System.out.println(new ZazAction(1, 2, new String[] {"a"}).hashCode() == new ZazAction(1, 2, new String[] { "a" }).hashCode());
		System.out.println(new ZazAction(1, 2, new String[] {"a"}).equals(new ZazAction(1, 2, new String[] { "a" })));

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
