package com.foo.client.stub;

import org.gwtasyncgen.GenStub;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

@GenStub(name = "StubFooViewBlah")
public interface IFooView {
	HasText text();
	
	HasValue<Boolean> setting();
}
