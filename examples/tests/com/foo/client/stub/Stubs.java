package com.foo.client.stub;

public class Stubs {

	public static void main(String[] args) {
		IBarView bar = new StubBarView();
		IFooView foo = new StubFooViewBlah();

		System.out.println(bar.setting().getValue());
		System.out.println(foo.setting().getValue());

		StubZazView zaz = new StubZazView();
		try {
			zaz.asWidget();
			throw new IllegalStateException("expected");
		} catch (UnsupportedOperationException uoe) {
			System.out.println(uoe.getMessage());
		}

		try {
			zaz.textButWithArgs("a");
			throw new IllegalStateException("expected");
		} catch (UnsupportedOperationException uoe) {
			System.out.println(uoe.getMessage());
		}

		zaz.withArgs("a");
		zaz.withArgs("a");
		System.out.println(zaz.withArgs);
	}

}
