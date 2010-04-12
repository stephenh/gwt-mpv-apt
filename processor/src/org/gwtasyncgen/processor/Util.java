package org.gwtasyncgen.processor;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;
import joist.util.Join;

public class Util {

	public static List<? extends VariableElement> getFieldsSorted(TypeElement element) {
		// getAllMembers uses hash maps and so order is non-deterministic
		List<Element> copy = new ArrayList<Element>(element.getEnclosedElements());
		Collections.sort(copy, new Comparator<Element>() {
			@Override
			public int compare(Element o1, Element o2) {
				return o1.getSimpleName().toString().compareTo(o2.getSimpleName().toString());
			}
		});
		return ElementFilter.fieldsIn(copy);
	}

	public static void saveCode(ProcessingEnvironment env, GClass g, Element... originals) {
		try {
			JavaFileObject jfo = env.getFiler().createSourceFile(g.getFullClassNameWithoutGeneric(), originals);
			Writer w = jfo.openWriter();
			w.write(g.toCode());
			w.close();
		} catch (IOException io) {
			env.getMessager().printMessage(Kind.ERROR, io.getMessage());
		}
	}

	public static void addGenerated(GClass gclass, Class<?> processorClass) {
		String value = processorClass.getName();
		String date = new SimpleDateFormat("dd MMM yyyy hh:mm").format(new Date());
		gclass.addImports(Generated.class);
		gclass.addAnnotation("@Generated(value = \"" + value + "\", date = \"" + date + "\")");
	}

	public static List<String> getArguments(ExecutableElement method) {
		List<String> args = new ArrayList<String>();
		for (VariableElement parameter : method.getParameters()) {
			args.add(parameter.asType().toString() + " " + parameter.getSimpleName());
		}
		return args;
	}

	public static List<String> getTypeParameters(ExecutableElement method) {
		List<String> params = new ArrayList<String>();
		if (method.getTypeParameters().size() != 0) {
			for (TypeParameterElement p : method.getTypeParameters()) {
				String base = p.toString();
				if (p.getBounds().size() > 0) {
					List<String> bounds = new ArrayList<String>();
					for (TypeMirror tm : p.getBounds()) {
						bounds.add(tm.toString());
					}
					base += " extends " + Join.join(bounds, " & ");
				}
				params.add(base);
			}
		}
		return params;
	}

	public static List<Prop> getProperties(TypeElement element, String prefix) {
		List<Prop> props = new ArrayList<Prop>();
		for (VariableElement field : Util.getFieldsSorted(element)) {
			String fieldName = field.getSimpleName().toString();
			if (fieldName.startsWith(prefix) && fieldName.length() > prefix.length()) {
				props.add(new Prop(lower(stripPrefixAndIndex(fieldName, prefix)), field.asType().toString()));
			}
		}
		return props;
	}

	public static void addHashCode(GClass gclass, List<Prop> properties) {
		GMethod hashCode = gclass.getMethod("hashCode").addAnnotation("@Override").returnType("int");
		hashCode.body.line("int result = 23;");
		hashCode.body.line("result = (result * 37) + getClass().hashCode();");
		for (Prop p : properties) {
			if (Primitives.isPrimitive(p.type)) {
				hashCode.body.line("result = (result * 37) + new {}({}).hashCode();", Primitives.getWrapper(p.type), p.name);
			} else if (p.type.endsWith("[]")) {
				hashCode.body.line("result = (result * 37) + java.util.Arrays.deepHashCode({});", p.name);
			} else {
				hashCode.body.line("result = (result * 37) + ({} == null ? 1 : {}.hashCode());", p.name, p.name);
			}
		}
		hashCode.body.line("return result;");
	}

	public static void addToString(GClass gclass, List<Prop> properties) {
		GMethod tos = gclass.getMethod("toString").addAnnotation("@Override").returnType("String");
		tos.body.line("return \"{}[\"", gclass.getSimpleClassNameWithoutGeneric());
		int i = 0;
		for (Prop p : properties) {
			if (p.type.endsWith("[]")) {
				tos.body.line("    + java.util.Arrays.toString({})", p.name);
			} else {
				tos.body.line("    + {}", p.name);
			}
			if (i++ < properties.size() - 1) {
				tos.body.line("    + \",\"");
			}
		}
		tos.body.line("    + \"]\";");
	}

	public static void addEquals(GClass gclass, GenericSuffix generics, List<Prop> properties) {
		GMethod equals = gclass.getMethod("equals").addAnnotation("@Override").argument("Object", "other").returnType("boolean");
		if (generics.vars.length() > 0) {
			equals.addAnnotation("@SuppressWarnings(\"unchecked\")");
		}
		equals.body.line("if (other != null && other.getClass().equals(this.getClass())) {");
		if (properties.size() == 0) {
			equals.body.line("    return true;");
		} else {
			equals.body.line("    {} o = ({}) other;",//
				gclass.getSimpleClassNameWithoutGeneric() + generics.vars,//
				gclass.getSimpleClassNameWithoutGeneric() + generics.vars);
			equals.body.line("    return true"); // leave open
			for (Prop p : properties) {
				if (Primitives.isPrimitive(p.type)) {
					equals.body.line("        && o.{} == this.{}", p.name, p.name);
				} else if (p.type.endsWith("[]")) {
					equals.body.line("        && java.util.Arrays.deepEquals(o.{}, this.{})", p.name, p.name);
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
	}

	private static String stripPrefixAndIndex(String name, String prefix) {
		String withoutPrefix = name.substring(prefix.length());
		if (withoutPrefix.length() > 1 && withoutPrefix.substring(0, 1).matches("[0-9]")) {
			return withoutPrefix.substring(1);
		}
		return withoutPrefix;
	}

	public static String lower(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	public static String upper(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

}
