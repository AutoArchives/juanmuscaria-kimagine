package pw.prok.imagine.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface Member {
    boolean save() default true;

    boolean load() default true;

    boolean object() default true;

    boolean nbt() default true;

    String name() default "";
}
