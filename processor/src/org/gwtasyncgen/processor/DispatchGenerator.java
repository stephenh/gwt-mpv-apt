package org.gwtasyncgen.processor;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;

public class DispatchGenerator {

	private final ProcessingEnvironment processingEnv;
	private final TypeElement element;
	private final GClass actionClass;
	private final GClass resultClass;

	public DispatchGenerator(ProcessingEnvironment processingEnv, TypeElement element) {
		this.processingEnv = processingEnv;
		String base = element.toString().replaceAll("Spec$", "");
		String date = new SimpleDateFormat("yyyy MMM dd hh:mm").format(new Date());

		this.actionClass = new GClass(base + "Action");
		this.actionClass.getField("serialVersionUID").type("long").setStatic().setFinal().initialValue("1L");
		this.actionClass.implementsInterface("net.customware.gwt.dispatch.shared.Action<{}>", base + "Result");
		this.actionClass.addImports(Generated.class).addAnnotation("@Generated(value = \"" + Processor.class.getName() + "\", date = \"" + date + "\")");

		this.resultClass = new GClass(base + "Result");
		this.resultClass.getField("serialVersionUID").type("long").setStatic().setFinal().initialValue("1L");
		this.resultClass.implementsInterface("net.customware.gwt.dispatch.shared.Result");
		this.resultClass.addImports(Generated.class).addAnnotation("@Generated(value = \"" + Processor.class.getName() + "\", date = \"" + date + "\")");

		this.element = element;
	}

	public void generate() {
		GMethod actionConstructor = this.actionClass.getConstructor();
		GMethod resultConstructor = this.resultClass.getConstructor();

		for (VariableElement field : ElementFilter.fieldsIn(this.processingEnv.getElementUtils().getAllMembers(this.element))) {
			String specName = field.getSimpleName().toString();
			String specType = field.asType().toString();

			if (specName.startsWith("in") && specName.length() > 2) {
				String name = lower(specName.substring(2)); // strip in

				actionClass.getField(name).type(specType);
				actionClass.getMethod("get" + upper(name)).returnType(specType).body.append("return this.{};", name);

				actionConstructor.argument(specType, name);
				actionConstructor.body.line("this.{} = {};", name, name);
			}

			if (specName.startsWith("out") && specName.length() > 3) {
				String name = lower(specName.substring(3)); // strip out

				resultClass.getField(name).type(specType);
				resultClass.getMethod("get" + upper(name)).returnType(specType).body.append("return this.{};", name);

				resultConstructor.argument(specType, name);
				resultConstructor.body.line("this.{} = {};", name, name);
			}
		}

		// Now re-add the default constructor
		this.actionClass.getConstructor().setProtected();
		this.resultClass.getConstructor().setProtected();

		this.saveCode(this.actionClass);
		this.saveCode(this.resultClass);
	}

	private String upper(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private String lower(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	private void saveCode(GClass g) {
		try {
			JavaFileObject jfo = this.processingEnv.getFiler().createSourceFile(g.getFullClassNameWithoutGeneric(), this.element);
			Writer w = jfo.openWriter();
			w.write(g.toCode());
			w.close();
		} catch (IOException io) {
			this.processingEnv.getMessager().printMessage(Kind.ERROR, io.getMessage());
		}
	}

}