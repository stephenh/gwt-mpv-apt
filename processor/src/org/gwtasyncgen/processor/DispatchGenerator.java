package org.gwtasyncgen.processor;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;

public class DispatchGenerator {

	private final ProcessingEnvironment env;
	private final TypeElement element;
	private final GClass actionClass;
	private final GClass resultClass;
	private final GenericSuffix generics;

	public DispatchGenerator(ProcessingEnvironment env, TypeElement element) throws InvalidTypeElementException {
		if (!element.toString().endsWith("Spec")) {
			env.getMessager().printMessage(Kind.ERROR, "GenDispatch targets must end with a Spec suffix");
			throw new InvalidTypeElementException();
		}

		this.env = env;
		this.generics = new GenericSuffix(element);
		String base = element.toString().replaceAll("Spec$", "");

		this.actionClass = new GClass(base + "Action" + generics.varsWithBounds);
		this.actionClass.getField("serialVersionUID").type("long").setStatic().setFinal().initialValue("1L");
		this.actionClass.implementsInterface("net.customware.gwt.dispatch.shared.Action<{}>", base + "Result" + generics.vars);

		this.resultClass = new GClass(base + "Result" + generics.varsWithBounds);
		this.resultClass.getField("serialVersionUID").type("long").setStatic().setFinal().initialValue("1L");
		this.resultClass.implementsInterface("net.customware.gwt.dispatch.shared.Result");

		Util.addGenerated(this.actionClass, DispatchGenerator.class);
		Util.addGenerated(this.resultClass, DispatchGenerator.class);

		this.element = element;
	}

	public void generate() {
		generateDto(actionClass, Util.getProperties(element, "in"));
		generateDto(resultClass, Util.getProperties(element, "out"));
	}

	private void generateDto(GClass gclass, List<Prop> properties) {
		// move to GClass as a utility method
		GMethod cstr = gclass.getConstructor();
		for (Prop p : properties) {
			addFieldAndGetterAndConstructorArg(gclass, cstr, p.name, p.type);
		}
		if (properties.size() > 0) {
			// re-add the default constructor for serialization
			gclass.getConstructor().setProtected();
		}
		Util.addHashCode(gclass, properties);
		Util.addEquals(gclass, generics, properties);
		Util.saveCode(env, gclass);
	}

	private void addFieldAndGetterAndConstructorArg(GClass gclass, GMethod cstr, String name, String type) {
		gclass.getField(name).type(type);
		gclass.getMethod("get" + Util.upper(name)).returnType(type).body.append("return this.{};", name);
		cstr.argument(type, name);
		cstr.body.line("this.{} = {};", name, name);
	}

}
