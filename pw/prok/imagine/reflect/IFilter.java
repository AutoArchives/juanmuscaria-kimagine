package pw.prok.imagine.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface IFilter {
    public enum FilterResult {
        Default, Accept, Reject;

        public static FilterResult present(boolean b) {
            return b ? Default : Reject;
        }
    }

    <S> FilterResult filterClass(IScanner scanner, Class<S> mainClass);

    <S> FilterResult filterField(IScanner scanner, Class<S> mainClass, Class<? super S> superClass, Field field);

    <S> FilterResult filterMethod(IScanner scanner, Class<S> mainClass, Class<? super S> superClass, Method method);
}
