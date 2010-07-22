package com.foo.client.event;

import org.gwtmpv.GenEvent;
import org.gwtmpv.Param;

@GenEvent
public class FooChangedEventSpec {
	@Param(1)
	Integer fooId;
}
