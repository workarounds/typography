package in.workarounds.typography.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by madki on 13/10/15.
 */
@Retention(CLASS)
public @interface TypefaceView {
    String outputPackage() default "in.workarounds.typography";
    Class<?> fromView();
    String generateView();
}
