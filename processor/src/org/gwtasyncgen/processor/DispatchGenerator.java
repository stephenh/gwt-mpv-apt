package org.gwtasyncgen.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;

public class DispatchGenerator {

	private final ProcessingEnvironment env;
	private final TypeElement element;
	private final GClass actionClass;
	private final GClass resultClass;

	public DispatchGenerator(ProcessingEnvironment env, TypeElement element) {
		this.env = env;
		String base = element.toString().replaceAll("Spec$", "");

		this.actionClass = new GClass(base + "Action");
		this.actionClass.getField("serialVersionUID").type("long").setStatic().setFinal().initialValue("1L");
		this.actionClass.implementsInterface("net.customware.gwt.dispatch.shared.Action<{}>", base + "Result");

		this.resultClass = new GClass(base + "Result");
		this.resultClass.getField("serialVersionUID").type("long").setStatic().setFinal().initialValue("1L");
		this.resultClass.implementsInterface("net.customware.gwt.dispatch.shared.Result");

		this.element = element;
	}

	public void generate() {
		GMethod actionConstructor = this.actionClass.getConstructor();
		GMethod resultConstructor = this.resultClass.getConstructor();

		// getAllMembers uses hash maps and so order is non-deterministic
		for (VariableElement field : Util.getFieldsSorted(element)) {
			String specName = field.getSimpleName().toString();
			String specType = field.asType().toString();

			if (specName.startsWith("in") && specName.length() > 2) {
				String name = lower(stripPrefix(specName, "in"));

				actionClass.getField(name).type(specType);
				actionClass.getMethod("get" + upper(name)).returnType(specType).body.append("return this.{};", name);

				actionConstructor.argument(specType, name);
				actionConstructor.body.line("this.{} = {};", name, name);
			}

			if (specName.startsWith("out") && specName.length() > 3) {
				String name = lower(stripPrefix(specName, "out"));

				resultClass.getField(name).type(specType);
				resultClass.getMethod("get" + upper(name)).returnType(specType).body.append("return this.{};", name);

				resultConstructor.argument(specType, name);
				resultConstructor.body.line("this.{} = {};", name, name);
			}
		}

		// Now re-add the default constructor
		this.actionClass.getConstructor().setProtected();
		this.resultClass.getConstructor().setProtected();

		Util.saveCode(env, this.actionClass);
		Util.saveCode(env, this.resultClass);
	}

	private String stripPrefix(String name, String prefix) {
		String withoutPrefix = name.substring(prefix.length());
		if (withoutPrefix.length() > 1 && withoutPrefix.substring(0, 1).matches("[0-9]")) {
			return withoutPrefix.substring(1);
		}
		return withoutPrefix;
	}

	private String upper(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private String lower(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

}
