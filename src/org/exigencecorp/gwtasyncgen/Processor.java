package org.exigencecorp.gwtasyncgen;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes( { Processor.gwtAnnotationClassName })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class Processor extends AbstractProcessor {

    public static final String gwtAnnotationClassName = "com.google.gwt.user.client.rpc.RemoteServiceRelativePath";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        TypeElement annotation = this.processingEnv.getElementUtils().getTypeElement(gwtAnnotationClassName);
        for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
            if (element instanceof TypeElement) {
                new Generator(this.processingEnv, (TypeElement) element).generate();
            } else {
                this.processingEnv.getMessager().printMessage(Kind.WARNING, "Unhandled element " + element);
            }
        }
        return true;
    }

}
