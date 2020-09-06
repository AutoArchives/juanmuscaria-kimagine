package pw.prok.imagine.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Network {
    /**
     * Channel name, can be empty for automatic selection
     */
    String value() default "";
}
