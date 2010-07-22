package com.foo.client.event;

import org.gwtmpv.GenEvent;
import org.gwtmpv.Param;

@GenEvent
public class BoundsChangedEventSpec<T extends Number, U extends Number> {
	@Param(1)
	T t;

	@Param(2)
	U u;
}
