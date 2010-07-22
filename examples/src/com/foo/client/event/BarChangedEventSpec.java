package com.foo.client.event;

import org.gwtmpv.GenEvent;
import org.gwtmpv.Param;

@GenEvent(methodName = "onBarDone")
public class BarChangedEventSpec {
	@Param(1)
	Integer fooId;
}
