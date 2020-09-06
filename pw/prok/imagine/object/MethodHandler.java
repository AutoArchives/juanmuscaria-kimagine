package pw.prok.imagine.object;

import java.lang.reflect.Method;

public class MethodHandler implements IHandler<Method> {
    @Override
    public void putData(Object object, Method method, Object value) throws Exception {
        method.invoke(object, value);
    }

    @Override
    public Object getData(Object object, Method method) throws Exception {
        return method.invoke(object);
    }
}
