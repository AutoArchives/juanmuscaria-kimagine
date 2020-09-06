package pw.prok.imagine.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class ImagineReflect implements IScanner {
    public static IScanner create() {
        return new ImagineReflect();
    }

    @Override
    public <S> void scanClass(final Class<S> clazz, final IClassScanCallback<S> callback) {
        if (clazz == null || !filterClass(clazz)) return;
        Class<? super S> c = clazz;
        while (c != Object.class) {
            callback.scanClass(clazz, c);
            c = c.getSuperclass();
        }
    }

    @Override
    public <S> void scanFields(final Class<S> clazz, final IFieldScanCallback<S> callback) {
        scanMembersInternal(clazz, callback, null);
    }

    @Override
    public <S> void scanMethod(final Class<S> clazz, final IMethodScanCallback<S> callback) {
        scanMembersInternal(clazz, null, callback);
    }

    @Override
    public <S> void scanMembers(final Class<S> clazz, final IMemberScanCallback<S> callback) {
        scanMembersInternal(clazz, callback, callback);
    }

    private <S> void scanMembersInternal(final Class<S> clazz, final IFieldScanCallback<S> fieldCallback, final IMethodScanCallback<S> methodCallback) {
        scanClass(clazz, new IClassScanCallback<S>() {
            @Override
            public void scanClass(Class<S> mainClass, Class<? super S> superClass) {
                if (fieldCallback != null) {
                    for (Field field : superClass.getDeclaredFields()) {
                        if (filterField(mainClass, superClass, field)) {
                            fieldCallback.scanField(mainClass, superClass, field);
                        }
                    }
                }
                if (methodCallback != null) {
                    for (Method method : superClass.getDeclaredMethods()) {
                        if (filterMethod(mainClass, superClass, method)) {
                            methodCallback.scanMethod(mainClass, superClass, method);
                        }
                    }
                }
            }
        });
    }

    private List<IFilter> mFilters = new LinkedList<IFilter>();

    @Override
    public IScanner addFilter(IFilter filter) {
        mFilters.add(filter);
        return this;
    }

    @Override
    public IScanner removeFilter(IFilter filter) {
        mFilters.remove(filter);
        return this;
    }

    @Override
    public <A extends Annotation> IScanner addAnnotationFilter(Class<A> annotationClass, AnnotationFilter.Filter<A> filter, boolean filterClass, boolean filterFields, boolean filterMethod) {
        return addFilter(new AnnotationFilter<>(annotationClass, filter,
                (filterClass ? AnnotationFilter.FILTER_CLASS : 0)
                        | (filterFields ? AnnotationFilter.FILTER_FIELDS : 0)
                        | (filterMethod ? AnnotationFilter.FILTER_METHODS : 0)));
    }

    @Override
    public <A extends Annotation> IScanner addAnnotationFilter(Class<A> annotationClass, boolean filterClass, boolean filterFields, boolean filterMethod) {
        return addAnnotationFilter(annotationClass, null, filterClass, filterFields, filterMethod);
    }

    @Override
    public <A extends Annotation> IScanner withClassAnnotation(Class<A> annotationClass, AnnotationFilter.Filter<A> filter) {
        return addFilter(new AnnotationFilter<>(annotationClass, filter, AnnotationFilter.FILTER_CLASS));
    }

    @Override
    public <A extends Annotation> IScanner withClassAnnotation(Class<A> annotationClass) {
        return withClassAnnotation(annotationClass, null);
    }

    @Override
    public <A extends Annotation> IScanner withFieldAnnotation(Class<A> annotationClass, AnnotationFilter.Filter<A> filter) {
        return addFilter(new AnnotationFilter<>(annotationClass, filter, AnnotationFilter.FILTER_FIELDS));
    }

    @Override
    public <A extends Annotation> IScanner withFieldAnnotation(Class<A> annotationClass) {
        return withFieldAnnotation(annotationClass, null);
    }

    @Override
    public <A extends Annotation> IScanner withMethodAnnotation(Class<A> annotationClass, AnnotationFilter.Filter<A> filter) {
        return addFilter(new AnnotationFilter<>(annotationClass, filter, AnnotationFilter.FILTER_METHODS));
    }

    @Override
    public <A extends Annotation> IScanner withMethodAnnotation(Class<A> annotationClass) {
        return withMethodAnnotation(annotationClass, null);
    }

    @Override
    public IScanner addGetSetFilter(boolean get, boolean set) {
        return addFilter(new GetSetMethodFilter(get, set));
    }

    @Override
    public IScanner withGetMethods() {
        return addGetSetFilter(true, false);
    }

    @Override
    public IScanner withSetMethods() {
        return addGetSetFilter(false, true);
    }


    private <S> boolean filterClass(Class<S> mainClass) {
        for (IFilter filter : mFilters) {
            switch (filter.filterClass(this, mainClass)) {
                case Accept:
                    return true;
                case Reject:
                    return false;
            }
        }
        return true;
    }

    private <S> boolean filterField(Class<S> mainClass, Class<? super S> superClass, Field field) {
        for (IFilter filter : mFilters) {
            switch (filter.filterField(this, mainClass, superClass, field)) {
                case Accept:
                    return true;
                case Reject:
                    return false;
            }
        }
        return true;
    }

    private <S> boolean filterMethod(Class<S> mainClass, Class<? super S> superClass, Method method) {
        for (IFilter filter : mFilters) {
            switch (filter.filterMethod(this, mainClass, superClass, method)) {
                case Accept:
                    return true;
                case Reject:
                    return false;
            }
        }
        return true;
    }
}
