package pw.prok.imagine.config;

import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

public class Config {
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Property {
        boolean load() default true;

        boolean save() default true;

        String desc() default "";
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Name {
        String value();

        boolean autoload() default true;
    }

    public static Map<String, String> load(InputStream is) {
return null;
    }
}
