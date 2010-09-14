package org.gwtmpv.processor;

import static javax.lang.model.util.ElementFilter.methodsIn;
import static javax.lang.model.util.ElementFilter.typesIn;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.gwtmpv.GenDispatch;
import org.gwtmpv.GenEvent;
import org.gwtmpv.GenPlace;

@SupportedAnnotationTypes({ "org.gwtmpv.GenDispatch", "org.gwtmpv.GenEvent", "org.gwtmpv.GenPlace" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (TypeElement element : typesIn(roundEnv.getElementsAnnotatedWith(GenDispatch.class))) {
			try {
				new DispatchGenerator(this.processingEnv, element).generate();
			} catch (InvalidTypeElementException itee) {
				// continue
			}
		}

		for (TypeElement element : typesIn(roundEnv.getElementsAnnotatedWith(GenEvent.class))) {
			try {
				new EventGenerator(this.processingEnv, element, element.getAnnotation(GenEvent.class)).generate();
			} catch (InvalidTypeElementException itee) {
				// continue
			}
		}

		for (ExecutableElement element : methodsIn(roundEnv.getElementsAnnotatedWith(GenPlace.class))) {
			try {
				new PlaceGenerator(this.processingEnv, element, element.getAnnotation(GenPlace.class)).generate();
			} catch (InvalidTypeElementException itee) {
				// continue
			}
		}

		return true;
	}

}
