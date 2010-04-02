package com.foo.client.stub;

public class Stubs {

	public static void main(String[] args) {
		IBarView bar = new StubBarView();
		IFooView foo = new StubFooViewBlah();
		
		System.out.println(bar.setting().getValue());
		System.out.println(foo.setting().getValue());
	}
	
}
