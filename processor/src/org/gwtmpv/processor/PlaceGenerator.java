package org.gwtmpv.processor;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;
import joist.util.Join;

import org.exigencecorp.aptutil.Util;
import org.gwtmpv.GenPlace;

/**
 * Generates Place classes that wrap the boilerplate {@code GWT.runAsync} and call presenter static methods when requested.
 *
 * For example:
 *
 * <code>
 *    public class FooPresenter {
 *      @GenPlace("foo")
 *      public static void handleRequest(AppWideState state) {
 *        // called when FooPlace is fired
 *        state.doStuffToShowMe();
 *      }
 *    }
 * </code>
 *
 * Generates a {@code FooPlace} class that takes an {@code AppWideState} state
 * as its constructor, e.g.:
 * 
 * <code>
 *     PlaceManager m = ...;
 *     m.registerPlace(new FooPlace(appWideState));
 * </code>
 */
public class PlaceGenerator {

	private final ProcessingEnvironment env;
	private final ExecutableElement element;
	private final GenPlace place;
	private final GClass p;

	public PlaceGenerator(ProcessingEnvironment env, ExecutableElement element, GenPlace place) throws InvalidTypeElementException {
		if (!element.getModifiers().contains(Modifier.STATIC)) {
			env.getMessager().printMessage(Kind.ERROR, "GenPlace methods must be static", element);
			throw new InvalidTypeElementException();
		}
		this.env = env;
		this.element = element;
		this.place = place;
		p = new GClass(getPlaceQualifiedClassName()).baseClassName("org.gwtmpv.place.Place");
	}

	public void generate() {
		GMethod cstr = p.getConstructor();
		addCstrSuperCall(cstr);
		addCstrStaticMethodArguments(cstr);
		addCstrFailureCallbackIfNeeded(cstr);
		if (place.async()) {
			addAsyncHandleRequest();
		} else {
			addSyncHandleRequest();
		}
		Util.saveCode(env, p, element);
	}

	private void addCstrSuperCall(GMethod cstr) {
		cstr.body.line("super(\"{}\");", place.value());
	}

	private void addCstrStaticMethodArguments(GMethod cstr) {
		// any of the static method arguments become constructor arguments
		for (VariableElement param : element.getParameters()) {
			String paramName = param.getSimpleName().toString();
			String paramType = param.asType().toString();

			// this isn't a static argument
			if (paramType.equals("org.gwtmpv.place.PlaceRequest")) {
				continue;
			}

			p.getField(paramName).type(paramType).setFinal();
			cstr.argument(paramType, paramName);
			cstr.body.line("this.{} = {};", paramName, paramName);
		}
	}

	private void addCstrFailureCallbackIfNeeded(GMethod cstr) {
		if (place.async()) {
			p.getField("failureCallback").type("org.gwtmpv.util.FailureCallback").setFinal();
			cstr.argument("org.gwtmpv.util.FailureCallback", "failureCallback");
			cstr.body.line("this.{} = {};", "failureCallback", "failureCallback");
		}
	}

	private void addAsyncHandleRequest() {
		GMethod handleRequest = p.getMethod("handleRequest").argument("final org.gwtmpv.place.PlaceRequest", "request");
		handleRequest.body.line("GWT.runAsync(new RunAsyncCallback() {");
		handleRequest.body.line("    public void onSuccess() {");
		handleRequest.body.line("        {}.{}({});", getPresenterClassName(), getMethodName(), Join.commaSpace(getMethodParamNames()));
		handleRequest.body.line("    }");
		handleRequest.body.line("");
		handleRequest.body.line("    public void onFailure(Throwable caught) {");
		handleRequest.body.line("        failureCallback.onFailure(caught);");
		handleRequest.body.line("    }");
		handleRequest.body.line("});");
		p.addImports("com.google.gwt.core.client.GWT", "com.google.gwt.core.client.RunAsyncCallback");
	}

	private void addSyncHandleRequest() {
		GMethod handleRequest = p.getMethod("handleRequest").argument("final org.gwtmpv.place.PlaceRequest", "request");
		handleRequest.body.line("{}.{}({});", getPresenterClassName(), getMethodName(), Join.commaSpace(getMethodParamNames()));
	}

	private List<String> getMethodParamNames() {
		List<String> paramNames = new ArrayList<String>();
		for (VariableElement param : element.getParameters()) {
			paramNames.add(param.getSimpleName().toString());
		}
		return paramNames;
	}

	private String getMethodName() {
		return element.getSimpleName().toString();
	}

	private String getPresenterClassName() {
		return ((TypeElement) element.getEnclosingElement()).getSimpleName().toString();
	}

	private String getPlaceQualifiedClassName() {
		return ((TypeElement) element.getEnclosingElement()).getQualifiedName().toString().replace("Presenter", "Place");
	}

}
