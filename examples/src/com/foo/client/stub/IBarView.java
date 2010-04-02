package com.foo.client.stub;

import org.gwtasyncgen.GenStub;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

@GenStub
public interface IBarView {
	HasText text();
	
	HasValue<Boolean> setting();
}
