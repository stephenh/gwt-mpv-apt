package com.foo.client.stub;

import org.gwtasyncgen.GenStub;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

@GenStub
public interface IZazView {
	HasText text();

	HasValue<Boolean> setting();

	Widget asWidget();

	void noReturn();

	void withArgs(String a);

	HasText textButWithArgs(String a);
}
