package pw.prok.imagine.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GetSetMethodFilter implements IFilter {
    private final boolean mGet;
    private final boolean mSet;

    public GetSetMethodFilter(boolean get, boolean set) {
        mGet = get;
        mSet = set;
    }

    @Override
    public <S> FilterResult filterClass(IScanner scanner, Class<S> mainClass) {
        return FilterResult.Default;
    }

    @Override
    public <S> FilterResult filterField(IScanner scanner, Class<S> mainClass, Class<? super S> childClass, Field field) {
        return FilterResult.Default;
    }

    @Override
    public <S> FilterResult filterMethod(IScanner scanner, Class<S> mainClass, Class<? super S> childClass, Method method) {
        final String name = method.getName();
        if (name.length() > 4) {
            if (mGet && name.startsWith("get")) {
                return FilterResult.Default;
            }
            if (mSet && name.startsWith("set")) {
                return FilterResult.Default;
            }
            return FilterResult.Reject;
        }
        return FilterResult.Reject;
    }

    public static boolean isGetMethod(Method method) {
        return method.getName().startsWith("get");
    }

    public static boolean isSetMethod(Method method) {
        return method.getName().startsWith("set");
    }

    public static String getVarName(Method method) {
        char[] chars = method.getName().substring(3).toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);
        return new String(chars);
    }
}
