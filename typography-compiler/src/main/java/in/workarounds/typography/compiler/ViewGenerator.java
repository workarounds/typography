package in.workarounds.typography.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

/**
 * Created by madki on 13/10/15.
 */
public class ViewGenerator {

    private final String mFontName = "mFontName";
    private final String mFontVariant = "mFontVariant";
    private final String mAttrs = "attrs";
    private final String mContext = "context";
    private final String mDefStyleAttrs = "defStyleAttrs";

    private final FieldSpec FONT_NAME;
    private final FieldSpec FONT_VARIANT;

    private final MethodSpec SET_FONT;
    private final MethodSpec GET_ATTRS;

    private final MethodSpec CONST1;
    private final MethodSpec CONST2;
    private final MethodSpec CONST3;

    public ViewGenerator() {

        FONT_NAME = FieldSpec.builder(String.class, mFontName, Modifier.PRIVATE).build();
        FONT_VARIANT = FieldSpec.builder(String.class, mFontVariant, Modifier.PRIVATE).build();

        ClassName CONTEXT = ClassName.get("android.content", "Context");
        ClassName FONT_LOADER = ClassName.get("in.workarounds.typography", "FontLoader");
        ClassName TYPEFACE = ClassName.get("android.graphics", "Typeface");
        ClassName TYPED_ARRAY = ClassName.get("android.content.res", "TypedArray");
        ClassName ATTR_SET = ClassName.get("android.util", "AttributeSet");

        SET_FONT = MethodSpec.methodBuilder("setFont")
                .addModifiers(Modifier.PRIVATE)
                .beginControlFlow("if(isInEditMode())")
                .addStatement("return")
                .endControlFlow()
                .addStatement("$T typeface = $T.getInstance(getContext()).getTypeface($L, $L)",
                        TYPEFACE, FONT_LOADER, mFontName, mFontVariant)
                .addStatement("setTypeface(typeface)")
                .build();

        GET_ATTRS = MethodSpec.methodBuilder("getAttrs")
                .addModifiers(Modifier.PRIVATE)
                .addParameter(ATTR_SET, mAttrs)
                .addStatement("$L a = getContext().obtainStyledAttributes($L, R.styleable.TextView)", TYPED_ARRAY, mAttrs)
                .beginControlFlow("try")
                .addStatement("$L = a.getString(R.styleable.TextView_font_name)", mFontName)
                .addStatement("$L = a.getString(R.styleable.TextView_font_variant)", mFontVariant)
                .endControlFlow()
                .beginControlFlow("finally")
                .addStatement("a.recycle()")
                .endControlFlow()
                .build();

        CONST1 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT, mContext)
                .addStatement("super($L)", mContext)
                .addStatement("$N()", SET_FONT)
                .build();

        CONST2 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT, mContext)
                .addParameter(ATTR_SET, mAttrs)
                .addStatement("super($L, $L)", mContext, mAttrs)
                .addStatement("$N($L)", GET_ATTRS, mAttrs)
                .addStatement("$N()", SET_FONT)
                .build();

        CONST3 = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(CONTEXT, mContext)
                .addParameter(ATTR_SET, mAttrs)
                .addParameter(int.class, mDefStyleAttrs)
                .addStatement("super($L, $L, $L)", mContext, mAttrs, mDefStyleAttrs)
                .addStatement("$N($L)", GET_ATTRS, mAttrs)
                .addStatement("$N()", SET_FONT)
                .build();
    }

    public JavaFile generateView(String outputPackage, String baseClassName, String outputName) {

        TypeSpec outputView = buildClass(outputName, baseClassName);
        return JavaFile.builder(outputPackage, outputView).build();
    }

    public TypeSpec buildClass(String name, String superClassName) {
        return TypeSpec.classBuilder(name)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ClassName.bestGuess(superClassName))
                .addField(FONT_NAME)
                .addField(FONT_VARIANT)
                .addMethod(CONST1)
                .addMethod(CONST2)
                .addMethod(CONST3)
                .addMethod(SET_FONT)
                .addMethod(GET_ATTRS)
                .build();
    }

}
