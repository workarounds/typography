package in.workarounds.typography.compiler;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import in.workarounds.typography.annotations.TypefaceView;

import static com.google.auto.common.MoreElements.hasModifiers;
import static in.workarounds.typography.compiler.ProcessorUtils.hasSameModifiers;
import static in.workarounds.typography.compiler.ProcessorUtils.hasSameName;
import static in.workarounds.typography.compiler.ProcessorUtils.hasSameParams;
import static in.workarounds.typography.compiler.ProcessorUtils.hasSameReturnType;
import static in.workarounds.typography.compiler.ProcessorUtils.isSameKind;

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
        try {
            Class<?> type = typefaceAnn.fromView();
            if(!isValid(type)) {
                return true;
            }
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            if(!isValid(classTypeElement)) {
                return true;
            }
        }
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

    /**
     * Checks if the given class has three public cons
     * @param view
     * @return
     */
    private boolean isValid(Class<?> view) {
        if(!hasConstructor(view, new String[]{"android.content.Context"}, new String[] {"public"})) {
            error(null, "Given fromView has no constructor with param Context");
            return false;
        } else if(!hasConstructor(view, new String[]{"android.content.Context", "android.util.AttributeSet"},
                new String[] {"public"})) {
            error(null, "Given fromView has no constructor with params Context, AttributeSet");
            return false;
        } else if(!hasConstructor(view, new String[]{"android.content.Context", "android.util.AttributeSet",
                int.class.getCanonicalName()}, new String[] {"public"})) {
            error(null, "Given fromView has no constructor with params Context, AttributeSet, int");
            return false;
        } else if(!hasMethod(view, "setTypeface", new String[]{"android.graphics.Typeface"}, void.class.getCanonicalName(),
                new String[] {"public"})) {
            error(null, "Given fromView has no method 'public void setTypeface(Typeface)'");
            return false;
        } else if(!hasMethod(view , "isInEditMode", new String[]{}, boolean.class.getCanonicalName(),
                new String[]{
                        "public"
                })) {
            error(null, "Given fromView has no method 'public boolean isInEditMode()'");
            return false;
        } else if(!hasMethod(view , "getContext", new String[]{}, "android.content.Context",
                new String[] {
                        "public",
                        "final"
                })) {
            error(null, "Given fromView has no method 'public final Context getContext()'");
            return false;
        }
        return true;
    }

    private boolean isValid(TypeElement typeElement) {
        // TODO check if the typeElement has the 3 constructors, setTypeface method
        if(!hasConstructor(typeElement, new String[]{"android.content.Context"},
                new javax.lang.model.element.Modifier[] {
                        javax.lang.model.element.Modifier.PUBLIC
                })) {
            error(null, "Given fromView has no constructor with param Context");
            return false;
        } else if(!hasConstructor(typeElement, new String[]{"android.content.Context", "android.util.AttributeSet"},
        new javax.lang.model.element.Modifier[] {
                javax.lang.model.element.Modifier.PUBLIC})) {
            error(null, "Given fromView has no constructor with params Context, AttributeSet");
            return false;
        } else if(!hasConstructor(typeElement, new String[]{"android.content.Context", "android.util.AttributeSet",
                        int.class.getCanonicalName()},
                new javax.lang.model.element.Modifier[]{
                        javax.lang.model.element.Modifier.PUBLIC
                })) {
            error(null, "Given fromView has no constructor with params Context, AttributeSet, int");
            return false;
        } else if (!hasMethod(typeUtils, typeElement, "setTypeface", new String[]{"android.graphics.Typeface"}, TypeKind.VOID,
                new javax.lang.model.element.Modifier[] {
                        javax.lang.model.element.Modifier.PUBLIC
                })) {
            error(null, "Given typeElement has no method 'public void setTypeface(Typeface)'");
            return false;
        } else if (!hasMethod(typeUtils, typeElement, "isInEditMode", new String[]{}, TypeKind.BOOLEAN,
                new javax.lang.model.element.Modifier[]{
                        javax.lang.model.element.Modifier.PUBLIC
                })) {
            error(null, "Given typeElement has no method 'public boolean isInEditMode()'");
            return false;
        } else if(!hasMethod(typeUtils, typeElement, "getContext", new String[]{}, "android.content.Context",
                new javax.lang.model.element.Modifier[] {
                        javax.lang.model.element.Modifier.PUBLIC,
                        javax.lang.model.element.Modifier.FINAL
                })) {
            error(null, "Given typeElement has no method 'public final Context getContext()'");
            return false;
        }
        return true;
    }

    private boolean hasMethod(Class<?> clazz, String name, String[] params, String returnType, String[] modifiers) {
        for(Method method: clazz.getMethods()) {
            if(hasSameName(method, name)) {
                if(hasSameReturnType(method, returnType)) {
                    if(hasSameModifiers(Modifier.toString(method.getModifiers()), modifiers)) {
                        if(hasSameParams(method, params)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasMethod(Types typeUtils, TypeElement typeElement, String name, String[] params, String returnType,
                              javax.lang.model.element.Modifier[] modifiers) {
        for(Element e: typeElement.getEnclosedElements()) {
            if(ProcessorUtils.isSameKind(e, ElementKind.METHOD)) {
                if(ProcessorUtils.hasSameName(e, name)) {
                    if(hasSameModifiers(e, modifiers)) {
                        ExecutableElement executableElement = (ExecutableElement) e;
                        try {
                            if(hasSameReturnType(typeUtils, executableElement, returnType)) {
                                if(hasSameParams(executableElement, params)) {
                                    return true;
                                }
                            }
                        } catch (IllegalStateException exception) {
                            error(executableElement, exception.getMessage());
                        }
                    }
                }
            }
        }
        TypeMirror superClass = typeElement.getSuperclass();
        if(superClass.getKind()!=TypeKind.NONE) {
            return hasMethod(typeUtils, (TypeElement) typeUtils.asElement(superClass), name, params, returnType, modifiers);
        }
        return false;
    }

    private boolean hasMethod(Types typeUtils, TypeElement typeElement, String name, String[] params, TypeKind returnType,
                              javax.lang.model.element.Modifier[] modifiers) {
        for(Element e: typeElement.getEnclosedElements()) {
            if(isSameKind(e, ElementKind.METHOD)) {
                if(hasSameName(e, name)) {
                    if(hasSameModifiers(e, modifiers)) {
                        ExecutableElement executableElement = (ExecutableElement) e;
                        if(hasSameReturnType(executableElement, returnType)) {
                            if(hasSameParams(executableElement, params)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        TypeMirror superClass = typeElement.getSuperclass();
        if(superClass.getKind()!=TypeKind.NONE) {
            return hasMethod(typeUtils, (TypeElement) typeUtils.asElement(superClass), name, params, returnType, modifiers);
        }
        return false;
    }

    private boolean hasConstructor(TypeElement typeElement, String[] params, javax.lang.model.element.Modifier[] modifiers) {
        for(Element e: typeElement.getEnclosedElements()) {
            if(isSameKind(e, ElementKind.CONSTRUCTOR)) {
                if(hasSameModifiers(e, modifiers)) {
                    ExecutableElement executableElement = (ExecutableElement) e;
                    if(hasSameParams(executableElement, params)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasConstructor(Class<?> clazz, String[] params, String[] modifiers) {
        for(Constructor constructor: clazz.getConstructors()) {
            if(hasSameModifiers(Modifier.toString(constructor.getModifiers()), modifiers)) {
                if(hasSameParams(constructor, params)) {
                    return true;
                }
            }
        }
        return false;
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
