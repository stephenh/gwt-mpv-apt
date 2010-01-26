package org.gwtasyncgen.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;

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

}
