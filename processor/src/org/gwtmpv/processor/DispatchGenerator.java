package org.gwtmpv.processor;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;

import org.exigencecorp.aptutil.GenericSuffix;
import org.exigencecorp.aptutil.Prop;
import org.exigencecorp.aptutil.PropUtil;
import org.exigencecorp.aptutil.Util;
import org.gwtmpv.GenDispatch;
import org.gwtmpv.In;
import org.gwtmpv.Out;

public class DispatchGenerator {

	private final ProcessingEnvironment env;
	private final TypeElement element;
	private final GClass actionClass;
	private final GClass resultClass;
	private final GenericSuffix generics;
	private final Map<Integer, VariableElement> inParams = new TreeMap<Integer, VariableElement>();
	private final Map<Integer, VariableElement> outParams = new TreeMap<Integer, VariableElement>();
	private final String simpleName;
	private final String packageName;

	public DispatchGenerator(ProcessingEnvironment env, TypeElement element) throws InvalidTypeElementException {
		if (!element.toString().endsWith("Spec")) {
			env.getMessager().printMessage(Kind.ERROR, "GenDispatch targets must end with a Spec suffix", element);
			throw new InvalidTypeElementException();
		}

		this.env = env;
		this.element = element;
		this.generics = new GenericSuffix(element);
		simpleName = element.toString().replaceAll("Spec$", "");
		packageName = detectDispatchBasePackage(env);

		this.actionClass = new GClass(simpleName + "Action" + generics.varsWithBounds);
		this.resultClass = new GClass(simpleName + "Result" + generics.varsWithBounds);
	}

	public void generate() {
		setResultBaseClassOrInterface();
		setActionBaseClassOrInterface();
		addSerialVersionUID();
		addAnnotatedInAndOutParams();
		generateDto(actionClass, MpvUtil.toProperties(inParams.values()));
		generateDto(resultClass, MpvUtil.toProperties(outParams.values()));
	}

	private void addSerialVersionUID() {
		this.actionClass.getField("serialVersionUID").type("long").setStatic().setFinal().initialValue("1L");
		this.resultClass.getField("serialVersionUID").type("long").setStatic().setFinal().initialValue("1L");
	}

	private void addAnnotatedInAndOutParams() {
		for (VariableElement field : ElementFilter.fieldsIn(element.getEnclosedElements())) {
			In in = field.getAnnotation(In.class);
			Out out = field.getAnnotation(Out.class);
			if (in != null) {
				addInParam(field, in);
			} else if (out != null) {
				addOutParam(field, out);
			} else {
				env.getMessager().printMessage(Kind.ERROR, field.getSimpleName().toString() + " must be annotated with @In or @Out", field);
			}
		}
	}

	private void addInParam(VariableElement field, In in) {
		if (inParams.containsKey(in.value())) {
			env.getMessager().printMessage(Kind.ERROR, field.getSimpleName().toString() + " reuses an order value", field);
		} else {
			inParams.put(in.value(), field);
		}
	}

	private void addOutParam(VariableElement field, Out out) {
		if (outParams.containsKey(out.value())) {
			env.getMessager().printMessage(Kind.ERROR, field.getSimpleName().toString() + " reuses an order value", field);
		} else {
			outParams.put(out.value(), field);
		}
	}

	private void setActionBaseClassOrInterface() {
		GenDispatch genDispatch = element.getAnnotation(GenDispatch.class);
		if (genDispatch.baseAction() != null && genDispatch.baseAction().length() > 0) {
			this.actionClass.baseClassName("{}<{}>", genDispatch.baseAction(), simpleName + "Result" + generics.vars);
		} else {
			this.actionClass.implementsInterface("{}.Action<{}>", packageName, simpleName + "Result" + generics.vars);
		}
	}

	private void setResultBaseClassOrInterface() {
		GenDispatch genDispatch = element.getAnnotation(GenDispatch.class);
		if (genDispatch.baseResult() != null && genDispatch.baseResult().length() > 0) {
			this.resultClass.baseClassName(genDispatch.baseResult());
		} else {
			this.resultClass.implementsInterface("{}.Result", packageName);
		}
	}

	private void generateDto(GClass gclass, List<Prop> properties) {
		PropUtil.addGenerated(gclass, DispatchGenerator.class);
		// move to GClass as a utility method
		GMethod cstr = gclass.getConstructor();
		for (Prop p : properties) {
			addFieldAndGetterAndConstructorArg(gclass, cstr, p.name, p.type);
		}
		if (properties.size() > 0) {
			// re-add the default constructor for serialization
			gclass.getConstructor().setProtected();
		}
		PropUtil.addHashCode(gclass, properties);
		PropUtil.addEquals(gclass, generics, properties);
		PropUtil.addToString(gclass, properties);
		Util.saveCode(env, gclass);
	}

	private String detectDispatchBasePackage(ProcessingEnvironment env) {
		String dispatchBasePackage = env.getOptions().get("dispatchBasePackage");
		if (dispatchBasePackage != null) {
			return dispatchBasePackage;
		}
		// Auto-detect gwt-dispatch
		TypeElement gwtDispatchAction = env.getElementUtils().getTypeElement("net.customware.gwt.dispatch.shared.Action");
		// Auto-detect gwt-platform
		TypeElement gwtpAction = env.getElementUtils().getTypeElement("com.gwtplatform.dispatch.shared.Action");
		if (gwtDispatchAction != null) {
			return "net.customware.gwt.dispatch.shared";
		} else if (gwtpAction != null) {
			return "com.gwtplatform.dispatch.shared";
		} else {
			return "org.gwtmpv.dispatch.shared";
		}
	}

	private void addFieldAndGetterAndConstructorArg(GClass gclass, GMethod cstr, String name, String type) {
		gclass.getField(name).type(type);
		gclass.getMethod("get" + Util.upper(name)).returnType(type).body.append("return this.{};", name);
		cstr.argument(type, name);
		cstr.body.line("this.{} = {};", name, name);
	}

}
