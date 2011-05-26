package com.foo.client.event;

import org.gwtmpv.GenEvent;
import org.gwtmpv.Param;

@GenEvent(gwtEvent = true)
public class OldSchoolEventSpec {
	@Param(1)
	Integer fooId;
}
