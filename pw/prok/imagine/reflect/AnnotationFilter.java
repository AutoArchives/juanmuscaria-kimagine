package pw.prok.imagine.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

public class AnnotationFilter<A extends Annotation> implements IFilter {
    public static final int FILTER_CLASS = 1;
    public static final int FILTER_FIELDS = 1 << 1;
    public static final int FILTER_METHODS = 1 << 2;

    private final Class<A> mAnnotationClass;
    private final boolean mFilterClass;
    private final boolean mFilterFields;
    private final boolean mFilterMethods;
    private final Filter<A> mAnnotationFilter;

    public AnnotationFilter(Class<A> clazz, Filter<A> filter, int flags) {
        mAnnotationClass = clazz;
        mAnnotationFilter = filter;
        mFilterClass = (flags & FILTER_CLASS) != 0;
        mFilterFields = (flags & FILTER_FIELDS) != 0;
        mFilterMethods = (flags & FILTER_METHODS) != 0;
    }

    @Override
    public <S> FilterResult filterClass(IScanner scanner, Class<S> mainClass) {
        final AtomicBoolean result = new AtomicBoolean(false);
        if (mFilterClass) {
            ImagineReflect.create().scanClass(mainClass, new IClassScanCallback<S>() {
                @Override
                public void scanClass(Class<S> mainClass, Class<? super S> childClass) {
                    A annotation = childClass.getAnnotation(mAnnotationClass);
                    if (annotation != null && (mAnnotationFilter == null || mAnnotationFilter.classCheckAnnotation(childClass, annotation))) {
                        result.set(true);
                    }
                }
            });
            return FilterResult.present(result.get());
        }
        return FilterResult.Default;
    }

    @Override
    public <S> FilterResult filterField(IScanner scanner, Class<S> mainClass, Class<? super S> childClass, Field field) {
        if (mFilterFields) {
            A annotation = field.getAnnotation(mAnnotationClass);
            return FilterResult.present(annotation != null && (mAnnotationFilter == null || mAnnotationFilter.fieldCheckAnnotation(field, annotation)));
        }
        return FilterResult.Default;
    }

    @Override
    public <S> FilterResult filterMethod(IScanner scanner, Class<S> mainClass, Class<? super S> childClass, Method method) {
        if (mFilterMethods) {
            A annotation = method.getAnnotation(mAnnotationClass);
            return FilterResult.present(annotation != null && (mAnnotationFilter == null || mAnnotationFilter.methodCheckAnnotation(method, annotation)));
        }
        return FilterResult.Default;
    }

    public abstract static class Filter<A extends Annotation> {
        public boolean fieldCheckAnnotation(Field field, A annotation) {
            return true;
        }

        public boolean methodCheckAnnotation(Method method, A annotation) {
            return true;
        }

        public boolean classCheckAnnotation(Class<?> clazz, A annotation) {
            return true;
        }
    }
}
