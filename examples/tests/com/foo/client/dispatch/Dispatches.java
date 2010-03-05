package com.foo.client.dispatch;


public class Dispatches {

	public static void main(String[] args) {
		new BarAction(1);
		new BarResult("foo", 2);

		// i really need junit
		System.out.println(new BarAction(1).hashCode() == new BarAction(1).hashCode());

		System.out.println(new ZazAction(1, 2, new String[] {"a"}).hashCode() == new ZazAction(1, 2, new String[] { "a" }).hashCode());
		System.out.println(new ZazAction(1, 2, new String[] {"a"}).equals(new ZazAction(1, 2, new String[] { "a" })));

		System.out.println(new GenericAction<String, Number>("a", 1).equals(new GenericAction<String, Integer>("a", 1)));
		// flip the params, ensure no ClassCastException
		System.out.println(new GenericAction<String, Number>("a", 1).equals(new GenericAction<Number, Number>(1, 1)));
		System.out.println(new GenericAction<Number, Number>(1, 1).equals(new GenericAction<String, Number>("a", 1)));
	}

}
