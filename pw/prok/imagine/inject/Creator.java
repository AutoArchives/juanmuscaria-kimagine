package pw.prok.imagine.inject;

import java.lang.reflect.Constructor;
import java.util.Stack;

public class Creator<D> implements IConstructorBuilder<D, Creator.ConstructorBuilder<D>> {
    private static final IFilter INT_FILTER = new DynamicClassFilter(int.class);

    private final Class<D> mClass;

    public static <D> Creator<D> creator(Class<D> clazz) {
        return new Creator<D>(clazz);
    }

    public static <D> Creator<D> creator(String className) {
        return creator(Creator.class.getClassLoader(), className);
    }

    public static <D> Creator<D> creator(ClassLoader classLoader, String className) {
        try {
            Class<D> clazz = (Class<D>) Class.forName(className, true, classLoader);
            return creator(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    public Creator(Class<D> clazz) {
        assert clazz != null;
        mClass = clazz;
    }

    public ConstructorBuilder<D> builder() {
        return new ConstructorBuilder<D>(mClass);
    }

    @Override
    public ConstructorBuilder<D> atLeast(Class<?> clazz) {
        return builder().atLeast(clazz);
    }

    @Override
    public <T, V extends T> ConstructorBuilder<D> arg(Class<T> clazz, V value) {
        return builder().arg(clazz, value);
    }

    @Override
    public <T, V extends T> ConstructorBuilder<D> arg(int pos, Class<T> clazz, V value) {
        return builder().arg(pos, clazz, value);
    }

    @Override
    public <V> ConstructorBuilder<D> arg(V value) {
        return builder().arg(value);
    }

    @Override
    public <V> ConstructorBuilder<D> arg(int pos, V value) {
        return builder().arg(pos, value);
    }

    @Override
    public ConstructorBuilder<D> arg(int value) {
        return builder().arg(value);
    }

    @Override
    public ConstructorBuilder<D> arg(int pos, int value) {
        return builder().arg(pos, value);
    }

    @Override
    public D build() {
        return builder().build();
    }

    @Override
    public <V> ConstructorBuilder<D> args(V... args) {
        return builder().args(args);
    }

    @Override
    public Class<? extends D> clazz() {
        return mClass;
    }

    public static class ConstructorBuilder<D> implements IConstructorBuilder<D, ConstructorBuilder<D>> {
        private final Class<D> mClass;
        private final Stack<IFilter> mFilters;
        private final Stack<Object> mArgStack;

        private Constructor<D> mConstructor;
        private boolean mConstructorFound = false;
        private Object[] mConstructorArgs;
        private Class<?> mAtLeast;

        public ConstructorBuilder(Class<D> clazz) {
            mClass = clazz;
            mFilters = new Stack<IFilter>();
            mArgStack = new Stack<Object>();
        }

        public boolean valid() {
            return constructor() != null;
        }

        public Constructor<D> constructor() {
            if (!mConstructorFound) {
                Class<? extends D> clazz = mClass;
                if (mAtLeast != null && clazz.isAssignableFrom(mAtLeast)) {
                    clazz = (Class<? extends D>) mAtLeast;
                }
                final int argSize = mArgStack.size();
                if (argSize == 0) {
                    try {
                        mConstructor = (Constructor<D>) clazz.getDeclaredConstructor(new Class[0]);
                        mConstructorFound = true;
                        return mConstructor;
                    } catch (Exception e) {
                        throw new RuntimeException("Default constructor not found", e);
                    }
                }
                for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                    final Class<?>[] types = constructor.getParameterTypes();
                    if (types.length != argSize) continue;
                    boolean constructorMatching = true;
                    for (int i = types.length - 1; i >= 0; i--) {
                        IFilter filter = mFilters.get(i);
                        if (!filter.match(types[i])) {
                            constructorMatching = false;
                            break;
                        }
                    }
                    if (!constructorMatching) continue;
                    mConstructor = (Constructor<D>) constructor;
                }
                mConstructorFound = true;
            }
            return mConstructor;
        }

        @Override
        public D build() {
            Constructor<D> constructor = constructor();
            if (constructor == null) return null;
            if (mConstructorArgs == null) mConstructorArgs = mArgStack.toArray();
            try {
                return constructor.newInstance(mConstructorArgs);
            } catch (Exception e) {
                throw new RuntimeException("Failed to instantiate", e);
            }
        }

        @Override
        public <V> ConstructorBuilder<D> args(V... args) {
            if (args == null || args.length == 0) return this;
            for (V arg : args) {
                arg(arg);
            }
            return this;
        }

        @Override
        public Class<? extends D> clazz() {
            return mClass;
        }

        private ConstructorBuilder<D> iArg(int pos, Object value, IFilter filter) {
            mArgStack.setElementAt(value, pos);
            mFilters.setElementAt(filter, pos);
            mConstructor = null;
            mConstructorFound = false;
            mConstructorArgs = null;
            return this;
        }

        private ConstructorBuilder<D> iArg(Object value, IFilter filter) {
            mArgStack.push(value);
            mFilters.push(filter);
            mConstructor = null;
            mConstructorFound = false;
            mConstructorArgs = null;
            return this;
        }

        @Override
        public ConstructorBuilder<D> atLeast(Class<?> clazz) {
            mAtLeast = clazz;
            return this;
        }

        @Override
        public <T, V extends T> ConstructorBuilder<D> arg(Class<T> clazz, V value) {
            return iArg(value, new FixedClassFilter<>(clazz));
        }

        @Override
        public <T, V extends T> ConstructorBuilder<D> arg(int pos, Class<T> clazz, V value) {
            return iArg(pos, value, new FixedClassFilter<>(clazz));
        }

        @Override
        public <V> ConstructorBuilder<D> arg(V value) {
            return iArg(value, new DynamicClassFilter(value.getClass()));
        }

        @Override
        public <V> ConstructorBuilder<D> arg(int pos, V value) {
            return iArg(pos, value, new DynamicClassFilter(value.getClass()));
        }

        @Override
        public ConstructorBuilder<D> arg(int value) {
            return iArg(value, INT_FILTER);
        }

        @Override
        public ConstructorBuilder<D> arg(int pos, int value) {
            return iArg(pos, value, INT_FILTER);
        }
    }

    private interface IFilter {
        boolean match(Class<?> argType);
    }

    private static class FixedClassFilter<T> implements IFilter {
        private final Class<T> mClass;

        public FixedClassFilter(Class<T> clazz) {
            mClass = clazz;
        }

        @Override
        public boolean match(Class<?> argType) {
            return argType == mClass;
        }
    }

    private static class DynamicClassFilter implements IFilter {
        private final Class<?> mClass;

        public DynamicClassFilter(Class<?> clazz) {
            mClass = clazz;
        }

        @Override
        public boolean match(Class<?> argType) {
            return argType.isAssignableFrom(mClass);
        }
    }
}
