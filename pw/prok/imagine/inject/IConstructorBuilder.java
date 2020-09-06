package pw.prok.imagine.inject;

public interface IConstructorBuilder<Z, I extends IConstructorBuilder<Z, I>> {
    I atLeast(Class<?> clazz);

    <T, V extends T> I arg(Class<T> clazz, V value);

    <T, V extends T> I arg(int pos, Class<T> clazz, V value);

    <V> I arg(V value);

    <V> I arg(int pos, V value);

    I arg(int value);

    I arg(int pos, int value);

    Z build();

    <V> I args(V... args);

    Class<? extends Z> clazz();
}
