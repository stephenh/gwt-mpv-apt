package com.foo.client.stub;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class StubHasValue<T> implements HasValue<T> {

	@Override
	public T getValue() {
		return null;
	}

	@Override
	public void setValue(T value) {
	}

	@Override
	public void setValue(T value, boolean fireEvents) {
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return null;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
	}

}
