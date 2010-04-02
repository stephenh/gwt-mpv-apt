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

}
