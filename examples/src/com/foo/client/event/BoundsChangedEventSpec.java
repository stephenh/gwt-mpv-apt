package com.foo.client.event;

import org.gwtmpv.GenEvent;

@GenEvent
public class BoundsChangedEventSpec<T extends Number, U extends Number> {
	T p1t;
	U p2u;
}

