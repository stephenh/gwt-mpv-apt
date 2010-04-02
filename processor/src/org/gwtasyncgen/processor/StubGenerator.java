package org.gwtasyncgen.processor;

import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import joist.sourcegen.GClass;
import joist.sourcegen.GField;
import joist.sourcegen.GMethod;
import joist.util.Join;

import org.gwtasyncgen.GenStub;

public class StubGenerator {

	private final ProcessingEnvironment env;
	private final Map<String, String> stubConfig;
	private final TypeElement element;
	private final GenStub annotation;
	private final GenericSuffix generics;
	private final GClass stubClass;

	public StubGenerator(ProcessingEnvironment env, Map<String, String> stubConfig, TypeElement element, GenStub annotation) {
		this.env = env;
		this.stubConfig = stubConfig;
		this.element = element;
		this.annotation = annotation;
		this.generics = new GenericSuffix(element);

		final String fullName;
		if ("".equals(annotation.name())) {
			fullName = getNameWithStubPrefix(element);
		} else {
			if (annotation.name().indexOf(".") > -1) {
				fullName = annotation.name(); // if they want a different package
			} else {
				fullName = env.getElementUtils().getPackageOf(element).getQualifiedName().toString() + "." + annotation.name();
			}
		}

		stubClass = new GClass(fullName + generics.varsWithBounds);
		stubClass.implementsInterface(element.toString() + generics.vars);
		if (annotation.isAbstract()) {
			stubClass.setAbstract();
		}
	}

	public void generate() {
		for (ExecutableElement method : ElementFilter.methodsIn(element.getEnclosedElements())) {
			generateMethod(method);
		}
		Util.addGenerated(stubClass, StubGenerator.class);
		Util.saveCode(env, stubClass, element);
	}

	private void generateMethod(ExecutableElement method) {
		if (method.getThrownTypes().size() > 0 || method.getParameters().size() > 0) {
			return;
		}

		final String methodName = method.getSimpleName().toString();
		final String returnType;
		final String stubType;
		if (method.getReturnType().getKind() == TypeKind.DECLARED) {
			GenericSuffix returnTypeGenerics = new GenericSuffix(env, (DeclaredType) method.getReturnType());
			returnType = method.getReturnType().toString();
			if (stubConfig.containsKey(returnTypeGenerics.without)) {
				stubType = stubConfig.get(returnTypeGenerics.without) + returnTypeGenerics.varsAsArguments;
			} else {
				stubType = null;
			}
		} else {
			returnType = method.getReturnType().toString();
			stubType = stubConfig.get(returnType);
		}

		if (stubType == null) {
			return;
		}

		GField f = stubClass.getField(methodName).setPublic().setFinal().type(stubType);
		f.initialValue("new {}()", stubType);

		GMethod m = stubClass.getMethod(methodName).returnType(stubType);
		m.body.line("return {};", methodName);
	}

	private String getNameWithStubPrefix(TypeElement type) {
		String[] parts = type.toString().split("\\.");
		parts[parts.length - 1] = "Stub" + stripi(parts[parts.length - 1]);
		return Join.join(parts, ".");
	}

	private String stripi(String part) {
		if (part.matches("^I[A-Z].+")) {
			return part.substring(1);
		} else {
			return part;
		}
	}

}
