package org.gwtmpv.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;
import joist.util.Copy;
import joist.util.Join;

import org.exigencecorp.aptutil.GenericSuffix;
import org.exigencecorp.aptutil.Prop;
import org.exigencecorp.aptutil.PropUtil;
import org.exigencecorp.aptutil.Util;
import org.gwtmpv.GenEvent;
import org.gwtmpv.Param;

public class EventGenerator {

	private final ProcessingEnvironment env;
	private final TypeElement element;
	private final GClass eventClass;
	private final GenEvent eventSpec;
	private final String handlerName;
	private final GenericSuffix generics;
	private final List<Prop> properties;

	public EventGenerator(ProcessingEnvironment env, TypeElement element, GenEvent eventSpec) throws InvalidTypeElementException {
		if (!element.toString().endsWith("EventSpec")) {
			env.getMessager().printMessage(Kind.ERROR, "GenEvent target must end with a suffix EventSpec", element);
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
		this.properties = MpvUtil.toProperties(findParamsInOrder());
	}

	public void generate() {
		generateInnerInterface();
		generateType();
		generateDispatch();
		generateFields();
		generateFire();
		PropUtil.addEquals(eventClass, generics, properties);
		PropUtil.addHashCode(eventClass, properties);
		PropUtil.addToString(eventClass, properties);
		PropUtil.addGenerated(eventClass, DispatchGenerator.class);
		Util.saveCode(env, eventClass);
	}

	private void generateInnerInterface() {
		GClass inner = eventClass.getInnerClass(handlerName + generics.varsWithBounds);
		inner.setInterface().baseClassName("com.google.gwt.event.shared.EventHandler");
		inner.getMethod(getMethodName()).argument(eventClass.getFullClassNameWithoutGeneric() + generics.vars, "event");
	}

	private void generateType() {
		eventClass
			.getField("TYPE")
			.setStatic()
			.setPublic()
			.setFinal()
			.type("Type<{}>", handlerName + generics.varsAsStatic)
			.initialValue("new Type<{}>()", handlerName + generics.varsAsStatic);
		eventClass.getMethod("getType").setStatic().returnType("Type<{}>", handlerName + generics.varsAsStatic).body.append("return TYPE;");

		GMethod associatedType = eventClass.getMethod("getAssociatedType");
		associatedType.returnType("Type<{}>", handlerName + generics.vars).addAnnotation("@Override");
		if (generics.vars.length() > 0) {
			associatedType.addAnnotation("@SuppressWarnings(\"all\")");
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
		for (Prop p : properties) {
			eventClass.getField(p.name).type(p.type).setFinal();
			eventClass.getMethod("get" + Util.upper(p.name)).returnType(p.type).body.append("return {};", p.name);
			cstr.argument(p.type, p.name);
			cstr.body.line("this.{} = {};", p.name, p.name);
		}
	}

	private void generateFire() {
		GMethod fire = eventClass.getMethod("fire").setStatic();
		if (generics.varsWithBounds.length() > 0) {
			fire.typeParameters(generics.varsWithBounds.substring(1, generics.varsWithBounds.length() - 1)); // ugly
		}
		fire.argument(detectEventBus(env), "eventBus");
		List<String> args = new ArrayList<String>();
		for (Prop p : properties) {
			fire.argument(p.type, p.name);
			args.add(p.name);
		}
		fire.body.line("eventBus.fireEvent(new {}({}));", eventClass.getSimpleClassNameWithoutGeneric() + generics.vars, Join.commaSpace(args));
	}

	private String getMethodName() {
		if (eventSpec.methodName().length() > 0) {
			return eventSpec.methodName();
		} else {
			return "on" + element.getSimpleName().toString().replaceAll("EventSpec$", "");
		}
	}

	private String detectEventBus(ProcessingEnvironment env) {
		String eventBusOption = env.getOptions().get("eventBus");
		if (eventBusOption != null) {
			return eventBusOption;
		}
		for (String option : Copy.list("net.customware.gwt.presenter.client.EventBus", "com.gwtplatform.mvp.client.EventBus", "org.gwtmpv.bus.EventBus")) {
			TypeElement t = env.getElementUtils().getTypeElement(option);
			if (t != null) {
				return t.toString();
			}
		}
		return "com.google.gwt.event.shared.HandlerManager";
	}

	private Collection<VariableElement> findParamsInOrder() {
		Map<Integer, VariableElement> params = new TreeMap<Integer, VariableElement>();
		for (VariableElement field : ElementFilter.fieldsIn(element.getEnclosedElements())) {
			Param param = field.getAnnotation(Param.class);
			if (param != null) {
				if (params.containsKey(param.value())) {
					env.getMessager().printMessage(Kind.ERROR, field.getSimpleName().toString() + " reuses an order value", field);
				} else {
					params.put(param.value(), field);
				}
				continue;
			}
			env.getMessager().printMessage(Kind.ERROR, field.getSimpleName().toString() + " must be annotated with @Param", field);
		}
		return params.values();
	}

}
