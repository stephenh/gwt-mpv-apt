package org.gwtasyncgen.processor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;
import joist.util.Join;

import org.gwtasyncgen.GenEvent;

public class EventGenerator {

	private final ProcessingEnvironment env;
	private final TypeElement element;
	private final GClass eventClass;
	private final GenEvent eventSpec;
	private final String handlerName;
	private final String genericSuffix;
	private final String genericSuffixBounds;
	private final String genericSuffixStatic;

	public EventGenerator(ProcessingEnvironment env, TypeElement element, GenEvent eventSpec) throws InvalidTypeElementException {
		if (!element.toString().endsWith("EventSpec")) {
			env.getMessager().printMessage(Kind.ERROR, "GenEvent target must end with a suffix EventSpec");
			throw new InvalidTypeElementException();
		}

		// TODO: Move this to processors util
		final List<String> generics = new ArrayList<String>();
		final List<String> genericsBounds = new ArrayList<String>();
		final List<String> genericsStatic = new ArrayList<String>();
		for (TypeParameterElement tpe : element.getTypeParameters()) {
			List<String> bounds = new ArrayList<String>();
			for (TypeMirror tm : tpe.getBounds()) {
				bounds.add("extends " + tm.toString());
			}
			if (bounds.size() > 0) {
				genericsBounds.add(tpe.toString() + " " + Join.commaSpace(bounds));
			} else {
				genericsBounds.add(tpe.toString());
			}
			generics.add(tpe.toString());
			genericsStatic.add("?");
		}
		if (generics.size() > 0) {
			genericSuffix = "<" + Join.commaSpace(generics) + ">";
			genericSuffixBounds = "<" + Join.commaSpace(genericsBounds) + ">";
			genericSuffixStatic = "<" + Join.commaSpace(genericsStatic) + ">";
		} else {
			genericSuffix = "";
			genericSuffixBounds = "";
			genericSuffixStatic = "";
		}

		this.env = env;
		this.element = element;
		this.eventClass = new GClass(element.toString().replaceAll("Spec$", "") + genericSuffixBounds);
		this.eventSpec = eventSpec;
		this.handlerName = element.getSimpleName().toString().replaceAll("EventSpec$", "Handler");
		this.eventClass.baseClassName("com.google.gwt.event.shared.GwtEvent<{}.{}>", eventClass.getSimpleClassNameWithoutGeneric(), handlerName
			+ genericSuffix);

	}

	public void generate() {
		generateInnerInterface();
		generateType();
		generateDispatch();
		generateFields();
		Util.saveCode(env, eventClass);
	}

	private void generateInnerInterface() {
		GClass inner = eventClass.getInnerClass(handlerName + genericSuffixBounds);
		inner.setInterface().baseClassName("com.google.gwt.event.shared.EventHandler");
		inner.getMethod(getMethodName()).argument(eventClass.getFullClassNameWithoutGeneric() + genericSuffix, "event");
	}

	private void generateType() {
		eventClass.getField("TYPE").setStatic().setPublic().setFinal().type("Type<{}>", handlerName + genericSuffixStatic).initialValue(
			"new Type<{}>()",
			handlerName + genericSuffixStatic);
		eventClass.getMethod("getType").setStatic().returnType("Type<{}>", handlerName + genericSuffixStatic).body.append("return TYPE;");

		GMethod associatedType = eventClass.getMethod("getAssociatedType");
		associatedType.returnType("Type<{}>", handlerName + genericSuffix).addAnnotation("@Override");
		if (genericSuffix.length() > 0) {
			associatedType.addAnnotation("@SuppressWarnings(\"unchecked\")");
			associatedType.body.line("return (Type) TYPE;");
		} else {
			associatedType.body.line("return TYPE;");
		}
	}

	private void generateDispatch() {
		eventClass.getMethod("dispatch").setProtected().addAnnotation("@Override").argument(handlerName + genericSuffix, "handler").body.line(
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
