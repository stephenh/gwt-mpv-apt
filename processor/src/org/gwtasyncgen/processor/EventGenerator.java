package org.gwtasyncgen.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;

import org.gwtasyncgen.GenEvent;

public class EventGenerator {

	private final ProcessingEnvironment env;
	private final TypeElement element;
	private final GClass eventClass;
	private final GenEvent eventSpec;
	private final String handlerName;

	public EventGenerator(ProcessingEnvironment env, TypeElement element, GenEvent eventSpec) {
		this.env = env;
		this.element = element;
		this.eventClass = new GClass(element.toString().replaceAll("Spec$", ""));
		this.eventSpec = eventSpec;
		this.handlerName = element.getSimpleName().toString().replaceAll("EventSpec$", "Handler");
		this.eventClass.baseClassName("com.google.gwt.event.shared.GwtEvent<{}.{}>", eventClass.getSimpleClassNameWithoutGeneric(), handlerName);
	}

	public void generate() {
		generateInnerInterface();
		generateType();
		generateDispatch();
		generateFields();
		Util.saveCode(env, eventClass);
	}

	private void generateInnerInterface() {
		GClass inner = eventClass.getInnerClass(handlerName);
		inner.setInterface().baseClassName("com.google.gwt.event.shared.EventHandler");
		inner.getMethod(getMethodName()).argument(eventClass.getFullClassName(), "event");
	}

	private void generateType() {
		eventClass.getField("TYPE").setStatic().setPublic().setFinal().type("Type<{}>", handlerName).initialValue("new Type<{}>()", handlerName);
		eventClass.getMethod("getType").setStatic().returnType("Type<{}>", handlerName).body.append("return TYPE;");
		eventClass.getMethod("getAssociatedType").returnType("Type<{}>", handlerName).addAnnotation("@Override").body.line("return TYPE;");
	}

	private void generateDispatch() {
		eventClass.getMethod("dispatch").setProtected().addAnnotation("@Override").argument(handlerName, "handler").body.line(
			"handler.{}(this);",
			getMethodName());
	}

	private void generateFields() {
		GMethod cstr = eventClass.getConstructor();

		for (VariableElement field : Util.getFieldsSorted(element)) {
			String specName = field.getSimpleName().toString();
			String specType = field.asType().toString();
			String name = lower(stripPrefix(specName));

			eventClass.getField(name).type(specType).setFinal();
			eventClass.getMethod("get" + upper(name)).returnType(specType).body.append("return {};", name);

			cstr.argument(specType, name);
			cstr.body.line("this.{} = {};", name, name);
		}
	}

	private String stripPrefix(String name) {
		if (name.length() > 2 && name.substring(0, 2).matches("p[0-9]")) {
			return name.substring(2);
		}
		return name;
	}

	private String upper(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private String lower(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	private String getMethodName() {
		if (eventSpec.methodName().length() > 0) {
			return eventSpec.methodName();
		} else {
			return "on" + element.getSimpleName().toString().replaceAll("EventSpec$", "");
		}
	}

}
