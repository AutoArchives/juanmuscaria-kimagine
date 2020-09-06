package pw.prok.imagine.reflect;

import java.lang.annotation.Annotation;

public interface IScanner {
    IScanner addFilter(IFilter filter);

    IScanner removeFilter(IFilter filter);

    <A extends Annotation> IScanner addAnnotationFilter(Class<A> annotationClass, AnnotationFilter.Filter<A> filter, boolean filterClass, boolean filterFields, boolean filterMethod);

    <A extends Annotation> IScanner addAnnotationFilter(Class<A> annotationClass, boolean filterClass, boolean filterFields, boolean filterMethod);

    <A extends Annotation> IScanner withClassAnnotation(Class<A> annotationClass, AnnotationFilter.Filter<A> filter);

    <A extends Annotation> IScanner withClassAnnotation(Class<A> annotationClass);

    <A extends Annotation> IScanner withFieldAnnotation(Class<A> annotationClass, AnnotationFilter.Filter<A> filter);

    <A extends Annotation> IScanner withFieldAnnotation(Class<A> annotationClass);

    <A extends Annotation> IScanner withMethodAnnotation(Class<A> annotationClass, AnnotationFilter.Filter<A> filter);

    <A extends Annotation> IScanner withMethodAnnotation(Class<A> annotationClass);

    IScanner addGetSetFilter(boolean get, boolean set);

    IScanner withSetMethods();

    IScanner withGetMethods();

    <S> void scanClass(final Class<S> clazz, final IClassScanCallback<S> callback);

    <S> void scanFields(final Class<S> clazz, final IFieldScanCallback<S> callback);

    <S> void scanMethod(final Class<S> clazz, final IMethodScanCallback<S> callback);

    <S> void scanMembers(final Class<S> clazz, final IMemberScanCallback<S> callback);
}
