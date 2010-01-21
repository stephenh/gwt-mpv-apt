package org.gwtasyncgen.processor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.gwtasyncgen.GenDispatch;

@SupportedAnnotationTypes( { Processor.gwtAnnotationClassName, "org.gwtasyncgen.GenDispatch" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {

	public static final String gwtAnnotationClassName = "com.google.gwt.user.client.rpc.RemoteServiceRelativePath";

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		TypeElement remoteService = this.processingEnv.getElementUtils().getTypeElement(gwtAnnotationClassName);
		for (Element element : roundEnv.getElementsAnnotatedWith(remoteService)) {
			if (element.getKind() == ElementKind.INTERFACE) {
				new AsyncGenerator(this.processingEnv, (TypeElement) element).generate();
			}
		}

		for (Element element : roundEnv.getElementsAnnotatedWith(GenDispatch.class)) {
			if (element.getKind() == ElementKind.CLASS) {
				new DispatchGenerator(this.processingEnv, (TypeElement) element).generate();
			}
		}

		return true;
	}

}
