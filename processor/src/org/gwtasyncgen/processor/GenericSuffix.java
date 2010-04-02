package org.gwtasyncgen.processor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import joist.util.Join;

public class GenericSuffix {
	
	public String without;
	public String vars;
	public String varsWithBounds;
	public String varsAsStatic;
	public String varsAsArguments;

	public GenericSuffix(ProcessingEnvironment env, DeclaredType type) {
		setWithout(type.toString());
		probeElement((TypeElement) env.getTypeUtils().asElement(type));
		probeType(type);
	}
	
	public GenericSuffix(TypeElement element) {
		setWithout(element.toString());
		probeElement(element);
		varsAsArguments = "";
	}
	
	private void setWithout(String name) {
		if (name.indexOf("<") > -1) {
			without = name.substring(0, name.indexOf("<"));
		} else {
			without = name;
		}
	}
	
	private void probeType(DeclaredType type) {
		final List<String> typeArguments = new ArrayList<String>();
		for (TypeMirror tm : type.getTypeArguments()) {
			typeArguments.add(tm.toString());
		}
		if (typeArguments.size() > 0) {
  		varsAsArguments = "<" + Join.commaSpace(typeArguments) + ">";
		} else {
			varsAsArguments = "";
		}
	}
	
	private void probeElement(TypeElement element) {
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
