package pw.prok.imagine.object;

import java.lang.reflect.Field;

public class FieldHandler implements IHandler<Field> {
    @Override
    public void putData(Object object, Field field, Object value) throws Exception {
        field.set(object, value);
    }

    @Override
    public Object getData(Object object, Field field) throws Exception {
        return field.get(object);
    }
}
