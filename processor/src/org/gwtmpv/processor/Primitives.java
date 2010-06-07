package org.gwtmpv.processor;

import java.util.HashMap;
import java.util.Map;

public class Primitives {

	private static Map<String, String> primitives = new HashMap<String, String>();

	static {
		primitives.put("byte", "Byte");
		primitives.put("short", "Short");
		primitives.put("int", "Integer");
		primitives.put("long", "Long");
		primitives.put("double", "Double");
		primitives.put("boolean", "Boolean");
		primitives.put("char", "Char");
	}

	public static boolean isPrimitive(String type) {
		return primitives.containsKey(type);
	}

	public static String getWrapper(String type) {
		return primitives.get(type);
	}

}
