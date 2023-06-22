package org.mapthree.annotation.processor;

import org.mapthree.annotation.Author;
import org.mapthree.annotation.TaskListInfo;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public class MyProcessor extends AbstractProcessor {

    // A set to store the annotations that this processor can handle
    private final Set<Class<? extends Annotation>> supportedAnnotations = new HashSet<>();
    private final Set<String> generatedClasses = new HashSet<>();

    public MyProcessor() {
        // all annotations which I want to process
        supportedAnnotations.add(Author.class);
        supportedAnnotations.add(TaskListInfo.class);
    }

    // This method is called when the processor is initialized
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    // This is the main processing method. It gets called once for each annotation found in the source files
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Loop over all the annotations that this processor can handle
        for (Class<? extends Annotation> annotation : supportedAnnotations) {
            // For each annotation, find all elements that are annotated with it
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                // Check if the annotated element is a class (TypeElement)
                if (element instanceof TypeElement typeElement) {
                    // Generate a new class for each annotated class
                    generateNewClass(typeElement);
                }
            }
        }
        // Return true to claim that the annotations have been handled by this processor
        return true;
    }

    private void generateNewClass(TypeElement typeElement) {
        // generate name and package for new class
        String className = typeElement.getSimpleName().toString() + "Generated";
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).toString();

        String qualifiedClassName = packageName + "." + className;
        if (generatedClasses.contains(qualifiedClassName)) {
            // Class already generated, skip
            return;
        }

        try {
            // Create a new source file
            JavaFileObject fileObject = processingEnv.getFiler().createSourceFile(packageName + "." + className);
            PrintWriter writer = new PrintWriter(fileObject.openWriter());

            // add code for the new class
            writer.println("package " + packageName + ";");
            writer.println();
            writer.println("public class " + className + " {");
            writer.println();
            writer.println("    public void generatedMethod() {");
            writer.println("        System.out.println(\"Generated method\");");
            writer.println("    }");
            writer.println();
            writer.println("}");
            writer.close();

            generatedClasses.add(qualifiedClassName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Specify the annotations that the processor can handle (from my supportedAnnotations)
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationNames = new HashSet<>();
        for (Class annotation : supportedAnnotations)
            supportedAnnotationNames.add(annotation.getCanonicalName());
        return supportedAnnotationNames;
    }

    // Specify the latest source version that the processor can handle
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
