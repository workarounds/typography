package in.workarounds.typography.compiler;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import in.workarounds.typography.annotations.TypefaceView;

/**
 * Created by madki on 13/10/15.
 */
@AutoService(Processor.class)
public class TypographyProcessor extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    private ViewGenerator generator;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();

        generator = new ViewGenerator();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(TypefaceView.class)) {
            if (!processElement(element)) {
                return true;
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();

        annotations.add(TypefaceView.class.getCanonicalName());

        return annotations;
    }

    private boolean processElement(Element element) {
        TypefaceView typefaceAnn = element.getAnnotation(TypefaceView.class);
        String outputPackage = typefaceAnn.outputPackage();
        String name = typefaceAnn.generateView();
        if(name == null || name.trim().equals("")) {
            name = getSimpleClassName(typefaceAnn);
        }

        try {
            generator.generateView(outputPackage, getQualifiedClassName(typefaceAnn), name).writeTo(filer);
        } catch (IOException e) {
            error(null, e.getMessage());
            return false;
        }

        return true;
    }

    private String getQualifiedClassName(TypefaceView annotation) {
        try {
            Class<?> type = annotation.fromView();
            return type.getCanonicalName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            return classTypeElement.getQualifiedName().toString();
        }
    }

    private String getSimpleClassName(TypefaceView annotation) {
        try {
            Class<?> type = annotation.fromView();
            return type.getSimpleName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            return classTypeElement.getSimpleName().toString();
        }
    }

    private boolean isValid(Class<?> view) {
        // TODO check if the view has the 3 constructors, setTypeface method
        return true;
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    private void message(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args),
                e);
    }

    private void warn(Element e, String msg, Object... args) {
        messager.printMessage(
                Diagnostic.Kind.WARNING,
                String.format(msg, args),
                e);
    }
}
