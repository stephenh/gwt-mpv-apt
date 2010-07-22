package com.foo.client.event;

import org.gwtmpv.GenEvent;
import org.gwtmpv.Param;

@GenEvent
public class GenericChangedEventSpec<T> {
	@Param(1)
	T t;
}