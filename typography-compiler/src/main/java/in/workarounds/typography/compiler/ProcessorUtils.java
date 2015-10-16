package in.workarounds.typography.compiler;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 * Created by mouli on 10/16/15.
 */
public class ProcessorUtils {

    public static boolean isSameKind(Element e, ElementKind kind) {
        return e.getKind()==kind;
    }

    public static boolean hasSameName(Element e, String name) {
        return e.getSimpleName().toString().equals(name);
    }

    public static boolean hasSameName(Method method, String name) {
        return method.getName().equals(name);
    }

    /**
     * Checks if the given string has the input modifiers
     * @param modifiers String of modifiers from Modifier.toString(int mod). TODO:// have to implement security checks
     * @param input String array of modifiers that should be there in the given modifier list
     * @return true if the input modifiers are present the in the given modifiers list (here a string), false otherwise
     */
    public static boolean hasSameModifiers(String modifiers, String[] input) {
        for(String modifier: input) {
            if(!modifiers.contains(modifier)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSameModifiers(Element e, javax.lang.model.element.Modifier[] modifiers) {
        for(javax.lang.model.element.Modifier modifier: modifiers) {
            if(!e.getModifiers().contains(modifier)) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasSameParams(ExecutableElement executableElement, String[] params) {
        List<? extends  VariableElement> variableElements = executableElement.getParameters();
        int paramCount = executableElement.getParameters().size();
        if(paramCount==params.length) {
            boolean flag = true;
            for(int i=0;i<paramCount;i++) {
                VariableElement variableElement = variableElements.get(i);
                if(variableElement.asType().toString().equals(params[i])) {
                    flag = true;
                } else {
                    flag = false;
                    break;
                }
            }
            if(flag) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSameParams(Method method, String[] params) {
        Class<?>[] paramTypes = method.getParameterTypes();
        boolean flag = true;
        int paramCount = params.length;
        if(method.getParameterCount()==paramCount) {
            for(int i=0;i<paramCount;i++) {
                Class<?> paramType = paramTypes[i];
                if(paramType.getCanonicalName().equals(params[i])) {
                    flag = true;
                } else {
                    flag = false;
                    break;
                }
            }
            if(flag) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSameParams(Constructor constructor, String[] params) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        boolean flag = true;
        int paramCount = params.length;
        if(constructor.getParameterCount()==paramCount) {
            for(int i=0;i<paramCount;i++) {
                Class<?> paramType = paramTypes[i];
                if(paramType.getCanonicalName().equals(params[i])) {
                    flag = true;
                } else {
                    flag = false;
                    break;
                }
            }
            if(flag) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSameReturnType(Types typeUtils, ExecutableElement executableElement, String returnType) {
        TypeMirror typeMirror = executableElement.getReturnType();
        TypeElement typeElement = (TypeElement) typeUtils.asElement(typeMirror);
        if(typeElement==null) {
            throw new IllegalStateException("The return type cannot be converted to TypeElement");
        } else {
            if(typeElement.getQualifiedName().toString().equals(returnType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasSameReturnType(ExecutableElement executableElement, TypeKind returnType) {
        return executableElement.getReturnType().getKind()==returnType;
    }

    public static boolean hasSameReturnType(Method method, String returnType) {
        return method.getReturnType().getCanonicalName().equals(returnType);
    }
}
