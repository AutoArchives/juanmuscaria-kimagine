package pw.prok.imagine.pool;

import pw.prok.imagine.inject.Creator;
import pw.prok.imagine.inject.IConstructorBuilder;

public class ReflectionFactory<T> implements PoolFactory<T> {
    private final IConstructorBuilder<T, ?> mCreator;

    public ReflectionFactory(Class<T> clazz, Object... args) {
        mCreator = Creator.creator(clazz).args(args);
    }

    @Override
    public T create() {
        return mCreator.build();
    }
}
