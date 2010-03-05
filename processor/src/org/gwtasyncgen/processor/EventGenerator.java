package org.gwtasyncgen.processor;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;

import org.gwtasyncgen.GenEvent;

public class EventGenerator {

	private final ProcessingEnvironment env;
	private final TypeElement element;
	private final GClass eventClass;
	private final GenEvent eventSpec;
	private final String handlerName;
	private final GenericSuffix generics;

	public EventGenerator(ProcessingEnvironment env, TypeElement element, GenEvent eventSpec) throws InvalidTypeElementException {
		if (!element.toString().endsWith("EventSpec")) {
			env.getMessager().printMessage(Kind.ERROR, "GenEvent target must end with a suffix EventSpec");
			throw new InvalidTypeElementException();
		}

		this.env = env;
		this.element = element;
		this.generics = new GenericSuffix(element);
		this.eventClass = new GClass(element.toString().replaceAll("Spec$", "") + generics.varsWithBounds);
		this.eventSpec = eventSpec;
		this.handlerName = element.getSimpleName().toString().replaceAll("EventSpec$", "Handler");
		this.eventClass.baseClassName("com.google.gwt.event.shared.GwtEvent<{}.{}>", eventClass.getSimpleClassNameWithoutGeneric(), handlerName
			+ generics.vars);

		Util.addGenerated(this.eventClass, DispatchGenerator.class);
	}

	public void generate() {
		generateInnerInterface();
		generateType();
		generateDispatch();
		generateFields();
		Util.saveCode(env, eventClass);
	}

	private void generateInnerInterface() {
		GClass inner = eventClass.getInnerClass(handlerName + generics.varsWithBounds);
		inner.setInterface().baseClassName("com.google.gwt.event.shared.EventHandler");
		inner.getMethod(getMethodName()).argument(eventClass.getFullClassNameWithoutGeneric() + generics.vars, "event");
	}

	private void generateType() {
		eventClass.getField("TYPE").setStatic().setPublic().setFinal().type("Type<{}>", handlerName + generics.varsAsStatic).initialValue(
			"new Type<{}>()",
			handlerName + generics.varsAsStatic);
		eventClass.getMethod("getType").setStatic().returnType("Type<{}>", handlerName + generics.varsAsStatic).body.append("return TYPE;");

		GMethod associatedType = eventClass.getMethod("getAssociatedType");
		associatedType.returnType("Type<{}>", handlerName + generics.vars).addAnnotation("@Override");
		if (generics.vars.length() > 0) {
			associatedType.addAnnotation("@SuppressWarnings(\"unchecked\")");
			associatedType.body.line("return (Type) TYPE;");
		} else {
			associatedType.body.line("return TYPE;");
		}
	}

	private void generateDispatch() {
		eventClass.getMethod("dispatch").setProtected().addAnnotation("@Override").argument(handlerName + generics.vars, "handler").body.line(
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
