package pw.prok.imagine.reflect;

import java.lang.reflect.Method;

public interface IMethodScanCallback<S> {
    void scanMethod(Class<S> mainClass, Class<? super S> superClass, Method method);
}
