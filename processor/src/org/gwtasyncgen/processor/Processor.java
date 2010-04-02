package org.gwtasyncgen.processor;

import java.util.Map;
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
import org.gwtasyncgen.GenEvent;
import org.gwtasyncgen.GenStub;

@SupportedAnnotationTypes( { "org.gwtasyncgen.GenDispatch", "org.gwtasyncgen.GenEvent", "org.gwtasyncgen.GenStub" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {

	private Map<String, String> stubConfig;
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (stubConfig == null) {
			stubConfig = ConfUtil.loadProperties(processingEnv, "stubgen.properties");
		}
		
		for (Element element : roundEnv.getElementsAnnotatedWith(GenDispatch.class)) {
			if (element.getKind() == ElementKind.CLASS) {
				try {
					new DispatchGenerator(this.processingEnv, (TypeElement) element).generate();
				} catch (InvalidTypeElementException itee) {
					// continue
				}
			}
		}

		for (Element element : roundEnv.getElementsAnnotatedWith(GenEvent.class)) {
			if (element.getKind() == ElementKind.CLASS) {
				try {
					new EventGenerator(this.processingEnv, (TypeElement) element, element.getAnnotation(GenEvent.class)).generate();
				} catch (InvalidTypeElementException itee) {
					// continue
				}
			}
		}

		for (Element element : roundEnv.getElementsAnnotatedWith(GenStub.class)) {
			if (element.getKind() == ElementKind.INTERFACE) {
				new StubGenerator(this.processingEnv, stubConfig, (TypeElement) element, element.getAnnotation(GenStub.class)).generate();
			}
		}

		return true;
	}

}
