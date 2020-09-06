package pw.prok.imagine.inject;

import pw.prok.imagine.util.Array;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

public abstract class AnnotationFieldInjector<A extends Annotation, T> extends Injector<AnnotationFieldInjector.AnnotationFieldState<A, T>> {
    private final Class<A> mAnnotationClass;
    private final Class<T> mObjectClass;

    public AnnotationFieldInjector(Class<A> annotationClass, Class<T> objectClass) {
        mAnnotationClass = annotationClass;
        mObjectClass = objectClass;
    }

    @Override
    public AnnotationFieldState<A, T> parseClass(Class<?> clazz) {
        List<A> annotations = new LinkedList<A>();
        List<Field> fields = new LinkedList<Field>();
        List<Class<? extends T>> classes = new LinkedList<Class<? extends T>>();
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            A annotation = field.getAnnotation(mAnnotationClass);
            if (annotation == null) continue;
            final Class<?> type = field.getType();
            if (mObjectClass != null && !mObjectClass.isAssignableFrom(type)) continue;
            field.setAccessible(true);

            annotations.add(annotation);
            fields.add(field);
            classes.add((Class<? extends T>) type);
        }
        if (annotations.size() == 0) return null;
        return new AnnotationFieldState<A, T>(annotations, fields, classes);
    }

    @Override
    public boolean inject(AnnotationFieldState<A, T> state, Object o, Object... args) {
        for (int i = 0; i < state.length; i++) {
            try {
                state.mFields[i].set(o, inject(state.mAnnotations[i], state.mTypes[i], o, args));
            } catch (Exception e) {
                throw new RuntimeException("Failed to inject annotation " + state.mAnnotations[i], e);
            }
        }
        return true;
    }

    @Override
    public <Z> IConstructorBuilder<Z, ?> create(Class<Z> clazz) {
        return super.create(clazz).atLeast(mObjectClass);
    }

    public abstract <V extends T> V inject(A annotation, Class<V> type, Object o, Object... args) throws Exception;

    public static class AnnotationFieldState<A, T> extends Injector.InjectorState {
        final A[] mAnnotations;
        final Class<? extends T>[] mTypes;
        final Field[] mFields;
        final int length;

        public AnnotationFieldState(List<A> annotations, List<Field> fields, List<Class<? extends T>> types) {
            mAnnotations = Array.asArray(annotations);
            mFields = Array.asArray(fields);
            mTypes = Array.asArray(types);
            if (mAnnotations.length != mFields.length || mAnnotations.length != mTypes.length) {
                throw new RuntimeException("Annotation/field/types desync!");
            }
            length = mAnnotations.length;
        }
    }
}
