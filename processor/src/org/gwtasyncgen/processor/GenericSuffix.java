package org.gwtasyncgen.processor;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.TypeMirror;

import joist.util.Join;

public class GenericSuffix {
	
	public final String vars;
	public final String varsWithBounds;
	public final String varsAsStatic;

	public GenericSuffix(TypeElement element) {
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
			vars = "<" + Join.commaSpace(generics) + ">";
			varsWithBounds = "<" + Join.commaSpace(genericsBounds) + ">";
			varsAsStatic = "<" + Join.commaSpace(genericsStatic) + ">";
		} else {
			vars = "";
			varsWithBounds = "";
			varsAsStatic = "";
		}
	}

}
