package org.exigencecorp.gwtasyncgen;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;
import javax.tools.Diagnostic.Kind;

import joist.sourcegen.GClass;
import joist.sourcegen.GMethod;
import joist.util.Copy;
import joist.util.Join;

public class Generator {

    private final ProcessingEnvironment processingEnv;
    private final TypeElement element;
    private final GClass asyncClass;

    public Generator(ProcessingEnvironment processingEnv, TypeElement element) {
        this.processingEnv = processingEnv;
        this.element = element;
        this.asyncClass = new GClass(this.element.toString() + "Async");
    }

    public void generate() {
        for (Element enclosed : this.element.getEnclosedElements()) {
            if (enclosed.getModifiers().contains(Modifier.STATIC) || enclosed.getKind() != ElementKind.METHOD) {
                continue;
            }
            this.addMethod((ExecutableElement) enclosed);
        }
        this.saveCode();
    }

    private void addMethod(ExecutableElement method) {
        List<String> args = new ArrayList<String>();
        for (VariableElement var : method.getParameters()) {
            args.add(var.asType().toString() + " " + var.getSimpleName().toString());
        }
        String nameAndArgs = method.getSimpleName().toString() + "(" + Join.commaSpace(args) + ")";

        GMethod asyncMethod = this.asyncClass.getMethod(nameAndArgs);
        asyncMethod.arguments(Copy.array(String.class, args));
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
