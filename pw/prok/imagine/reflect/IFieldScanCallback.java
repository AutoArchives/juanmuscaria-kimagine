package pw.prok.imagine.reflect;

import java.lang.reflect.Field;

public interface IFieldScanCallback<S> {
    void scanField(Class<S> mainClass, Class<? super S> superClass, Field field);
}
