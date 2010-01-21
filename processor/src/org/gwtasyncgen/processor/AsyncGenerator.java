package org.gwtasyncgen.processor;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.util.Join;

public class AsyncGenerator {

	private final ProcessingEnvironment processingEnv;
	private final TypeElement element;
	private final GClass asyncClass;

	public AsyncGenerator(ProcessingEnvironment processingEnv, TypeElement element) {
		this.processingEnv = processingEnv;
		this.element = element;
		
		this.asyncClass = new GClass(this.element.toString() + "Async");
		
		String date = new SimpleDateFormat("yyyy MMM dd hh:mm").format(new Date());
		this.asyncClass.addImports(Generated.class).addAnnotation("@Generated(value = \"" + Processor.class.getName() + "\", date = \"" + date + "\")");
	}

	public void generate() {
		for (Element enclosed : this.element.getEnclosedElements()) {
			if (this.isInstanceMethod(enclosed)) {
				this.addMethod((ExecutableElement) enclosed);
			}
		}
		this.saveCode();
	}

	private boolean isInstanceMethod(Element enclosed) {
		return enclosed.getKind() == ElementKind.METHOD && !enclosed.getModifiers().contains(Modifier.STATIC);
	}

	private void addMethod(ExecutableElement method) {
		List<String> args = new ArrayList<String>();
		this.addMethodArguments(method, args);
		this.addCallbackArgument(method, args);

		// This is an interface, so just touch the method
		String nameAndArgs = method.getSimpleName() + "(" + Join.commaSpace(args) + ")";
		this.asyncClass.getMethod(nameAndArgs);

		this.asyncClass.addImports("com.google.gwt.user.client.rpc.AsyncCallback");
	}

	private void addMethodArguments(ExecutableElement method, List<String> args) {
		for (VariableElement var : method.getParameters()) {
			args.add(var.asType().toString() + " " + var.getSimpleName().toString());
		}
	}

	private void addCallbackArgument(ExecutableElement method, List<String> args) {
		String returnType = this.mungeReturnTypeToString(method.getReturnType());
		args.add("AsyncCallback<" + returnType + "> callback");
	}

	private String mungeReturnTypeToString(TypeMirror returnType) {
		if (returnType.getKind() == TypeKind.VOID) {
			return "Void";
		}
		if (returnType instanceof PrimitiveType) {
			returnType = this.processingEnv.getTypeUtils().boxedClass((PrimitiveType) returnType).asType();
		}
		return returnType.toString();
	}

	private void saveCode() {
		try {
			JavaFileObject jfo = this.processingEnv.getFiler().createSourceFile(this.asyncClass.getFullClassNameWithoutGeneric(), this.element);
			Writer w = jfo.openWriter();
			w.write(this.asyncClass.toCode());
			w.close();
		} catch (IOException io) {
			this.processingEnv.getMessager().printMessage(Kind.ERROR, io.getMessage());
		}
	}

}
