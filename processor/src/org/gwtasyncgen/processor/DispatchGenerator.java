package org.gwtasyncgen.processor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;

public class DispatchGenerator {

	private final ProcessingEnvironment env;
	private final TypeElement element;
	private final GClass actionClass;
	private final GClass resultClass;

	public DispatchGenerator(ProcessingEnvironment env, TypeElement element) throws InvalidTypeElementException {
		if (!element.toString().endsWith("Spec")) {
			env.getMessager().printMessage(Kind.ERROR, "GenDispatch targets must end with a Spec suffix");
			throw new InvalidTypeElementException();
		}

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
		generateDto(actionClass, getProperties("in"));
		generateDto(resultClass, getProperties("out"));
	}

	private void generateDto(GClass gclass, List<Prop> properties) {
		// move to GClass as a utility method
		GMethod cstr = gclass.getConstructor();
		for (Prop p : properties) {
			addFieldAndGetterAndConstructorArg(gclass, cstr, p.name, p.type);
		}

		GMethod hashCode = gclass.getMethod("hashCode").addAnnotation("@Override").returnType("int");
		hashCode.body.line("int result = 23;");
		hashCode.body.line("result = (result * 37) + getClass().hashCode();");
		for (Prop p : properties) {
			if (Primitives.isPrimitive(p.type)) {
				hashCode.body.line("result = (result * 37) + new {}({}).hashCode();", Primitives.getWrapper(p.type), p.name);
			} else if (p.type.endsWith("[]")) {
				hashCode.body.line("result = (result * 37) + java.util.Arrays.deepHashCode({});", p.name);
			} else {
				hashCode.body.line("result = (result * 37) + {}.hashCode();", p.name);
			}
		}
		hashCode.body.line("return result;");

		GMethod equals = gclass.getMethod("equals").addAnnotation("@Override").argument("Object", "other").returnType("boolean");
		equals.body.line("if (other != null && other.getClass().equals(this.getClass())) {");
		if (properties.size() == 0) {
			equals.body.line("    return true;");
		} else {
			equals.body.line("    {} o = ({}) other;", gclass.getSimpleClassName(), gclass.getSimpleClassName());
			equals.body.line("    return true"); // leave open
			for (Prop p : properties) {
				if (Primitives.isPrimitive(p.type)) {
					equals.body.line("        && (o.{} == this.{})", p.name, p.name);
				} else if (p.type.endsWith("[]")) {
					equals.body.line("        && (java.util.Arrays.deepEquals(o.{}, this.{}))", p.name, p.name);
				} else {
					equals.body.line(
						"        && ((o.{} == null && this.{} == null) || (o.{} != null && o.{}.equals(this.{})))",
						p.name,
						p.name,
						p.name,
						p.name,
						p.name);
				}
			}
			equals.body.line("       ;");
		}
		equals.body.line("}");
		equals.body.line("return false;");

		if (properties.size() > 0) {
			// re-add the default constructor for serialization
			gclass.getConstructor().setProtected();
		}

		Util.saveCode(env, gclass);
	}

	private void addFieldAndGetterAndConstructorArg(GClass gclass, GMethod cstr, String name, String type) {
		gclass.getField(name).type(type);
		gclass.getMethod("get" + upper(name)).returnType(type).body.append("return this.{};", name);
		cstr.argument(type, name);
		cstr.body.line("this.{} = {};", name, name);
	}

	private List<Prop> getProperties(String prefix) {
		List<Prop> props = new ArrayList<Prop>();
		for (VariableElement field : Util.getFieldsSorted(element)) {
			String fieldName = field.getSimpleName().toString();
			if (fieldName.startsWith(prefix) && fieldName.length() > prefix.length()) {
				props.add(new Prop(lower(stripPrefixAndIndex(fieldName, prefix)), field.asType().toString()));
			}
		}
		return props;
	}

	private String stripPrefixAndIndex(String name, String prefix) {
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

	private static class Prop {
		private final String name;
		private final String type;

		public Prop(String name, String type) {
			this.name = name;
			this.type = type;
		}
	}

}
